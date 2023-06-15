package com.guilhermeweber.wasteless.activity.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.guilhermeweber.wasteless.R;
import com.guilhermeweber.wasteless.activity.helper.ConfigFirebase;
import com.guilhermeweber.wasteless.activity.model.Usuario;

import dmax.dialog.SpotsDialog;

public class AutentificacaoActivity extends AppCompatActivity {

    private Button botaoAcessar;
    private EditText campoEmail, campoSenha, campoNome;
    private Switch tipoAcesso, tipoUsuario;
    private LinearLayout linearTipoUsuario;
    private FirebaseAuth auth;
    private AlertDialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autentificacao);

        Usuario usuario = new Usuario();

        //ideia pra a o carregamento mais suave da tela de login
//        dialog = new SpotsDialog.Builder().setContext(this).setMessage("Carregando Dados").setCancelable(false).build();


        inicializarComponentes();
        auth = ConfigFirebase.getFireAuth();

        //forsa o usuario ja logado a deslogar
        //auth.signOut();
        //força user null pra casos mais extremos
        //usuario = null;

        //verifica o usuario logado
        if (usuario.getUsuarioAtual() != null) {
            //dialog.show();

            //adicionar tela de carregando aqui no futuro
            Usuario.redirectUser(AutentificacaoActivity.this);
        }

        tipoAcesso.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) { // se estiver selecionado é uma empresa
                    botaoAcessar.setText("Cadastrar");
                    linearTipoUsuario.setVisibility(View.VISIBLE);
                    campoNome.setVisibility(View.VISIBLE);
                } else { // se n estiver selecionado é um usuario
                    botaoAcessar.setText("Acessar");
                    linearTipoUsuario.setVisibility(View.GONE);
                    campoNome.setVisibility(View.GONE);
                }
            }
        });

        botaoAcessar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = campoEmail.getText().toString();
                String senha = campoSenha.getText().toString();
                String nome = campoNome.getText().toString();

                usuario.setNome(nome);
                usuario.setEmail(email);
                usuario.setSenha(senha);
                usuario.setTipo(verificaUsuario());

                //verifica se os campos estão vazios
                if (!email.isEmpty()) {
                    if (!senha.isEmpty()) {

                        //Verifica se o switch esta em login ou cadastro
                        if (tipoAcesso.isChecked()) { //cadastro

                            auth.createUserWithEmailAndPassword( // envia pro servidor o email e senha pra cadastro
                                    usuario.getEmail(), usuario.getSenha()
                            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) { //Verifica se o processo de cadastro do usuario deu certo

                                        String idUsuario = task.getResult().getUser().getUid();
                                        usuario.setId(idUsuario);
                                        usuario.salvar();

                                        //usuario.updateUserName(usuario.getNome());

                                        if (usuario.getTipo() == "E") {//Empresa
                                            startActivity(new Intent(getApplicationContext(), EmpresaActivity.class));
                                            finish();
                                            Toast.makeText(AutentificacaoActivity.this, "Empresa Cadastrada com Sucesso! ", Toast.LENGTH_LONG).show();
                                        } else {//usuario
                                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                            finish();
                                            Toast.makeText(AutentificacaoActivity.this, "Usuario Cadastrado com Sucesso! ", Toast.LENGTH_LONG).show();
                                        }
                                    } else {

                                        //caso cadastro n seja realizado com sucesso é mostrado uma mensagem de erro
                                        String erroExcecao = "";
                                        try {
                                            throw task.getException();
                                        } catch (FirebaseAuthWeakPasswordException e) {
                                            erroExcecao = "Digite uma senha mais forte!";
                                        } catch (FirebaseAuthInvalidCredentialsException e) {
                                            erroExcecao = "Por Favor, digite um e-mail válido";
                                        } catch (FirebaseAuthUserCollisionException e) {
                                            erroExcecao = "Esta conta já foi cadastrada";
                                        } catch (Exception e) {
                                            erroExcecao = "Ao Cadastrar usuário: " + e.getMessage();
                                            e.printStackTrace();
                                        }
                                        Toast.makeText(AutentificacaoActivity.this, "Erro:" + erroExcecao, Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                        } else { //login

                            auth.signInWithEmailAndPassword( // envia pro servidor email e senha pra logar
                                    email, senha
                            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) { // verifica se o retorno foi um sucesso

                                        usuario.redirectUser(AutentificacaoActivity.this);
                                        Toast.makeText(AutentificacaoActivity.this, "Logado com Sucesso", Toast.LENGTH_SHORT).show();

                                    } else {// caso login tenha dado falha ele informa ao usuario o erro

                                        String excecao = "";
                                        try {
                                            throw task.getException();
                                        } catch (FirebaseAuthInvalidUserException e) {
                                            excecao = "Usuario não está cadastrado";
                                        } catch (FirebaseAuthInvalidCredentialsException e) {
                                            excecao = "E-mail e senha não são validos";
                                        } catch (Exception e) {
                                            excecao = "erro ao cadastrar usuário" + e.getMessage();
                                            e.printStackTrace();
                                        }
                                        Toast.makeText(AutentificacaoActivity.this, excecao, Toast.LENGTH_LONG).show();

                                    }
                                }
                            });
                        }
                    } else {
                        Toast.makeText(AutentificacaoActivity.this, "Preencha o Senha!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AutentificacaoActivity.this, "Preencha o E-mail!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void inicializarComponentes() {
        campoNome = findViewById(R.id.editTextNome);
        campoEmail = findViewById(R.id.editTextNomeEmpresa);
        campoSenha = findViewById(R.id.editTextEmpresaCategoria);
        tipoAcesso = findViewById(R.id.switchTipoAcesso);
        tipoUsuario = findViewById(R.id.switchTipoUsuario);
        botaoAcessar = findViewById(R.id.buttonAcesso);
        linearTipoUsuario = findViewById(R.id.linearTipoUsuario);
    }

    public String verificaUsuario() {
        return tipoUsuario.isChecked() ? "E" : "U";
    }
}