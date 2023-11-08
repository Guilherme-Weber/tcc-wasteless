package com.guilhermeweber.wasteless.activity.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.guilhermeweber.wasteless.R;
import com.guilhermeweber.wasteless.activity.helper.ConfigFirebase;
import com.guilhermeweber.wasteless.activity.model.Usuario;


public class CadastrosUsuariosActivity extends AppCompatActivity {

    //Instanciando RadioButton e textos para o tipo de cadastro que está sendo realizado
    private EditText campoNome, campoEmail, campoSenha;
    private String nome, email, senha;
    private RadioButton cliente, empresa;
    private Button buttonCadastro;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastros_usuarios);

        Usuario usuario = new Usuario();

        inicializarComponentes();
        auth = ConfigFirebase.getFireAuth();

        buttonCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = campoEmail.getText().toString();
                String senha = campoSenha.getText().toString();
                String nome = campoNome.getText().toString();

                usuario.setNome(nome);
                usuario.setEmail(email);
                usuario.setSenha(senha);


                if (!nome.isEmpty()) {
                    if (!email.isEmpty()) {
                        if (!senha.isEmpty()) {
                            if (cliente.isChecked() || empresa.isChecked()) {

                                usuario.setTipo(verificaUsuario());
//                                Toast.makeText(CadastrosUsuariosActivity.this, "Cliente: " + usuario.getTipo(), Toast.LENGTH_SHORT).show();

//                                Toast.makeText(CadastrosUsuariosActivity.this, "Senha: " + usuario.getSenha(), Toast.LENGTH_SHORT).show();

                                // envia pro servidor o email e senha pra cadastro
                                auth.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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
                                                Toast.makeText(CadastrosUsuariosActivity.this, "Cadastrado realizado com sucesso! ", Toast.LENGTH_LONG).show();
                                            } else {//usuario
                                                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                                finish();
                                                Toast.makeText(CadastrosUsuariosActivity.this, "Cadastrado realizado com sucesso! ", Toast.LENGTH_LONG).show();
                                            }
                                        } else {

                                            //caso cadastro n seja realizado com sucesso é mostrado uma mensagem de erro
                                            String erroExcecao = "";
                                            try {
                                                throw task.getException();
                                            } catch (FirebaseAuthWeakPasswordException e) {
                                                erroExcecao = "Informe uma senha mais forte!";
                                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                                erroExcecao = "Por Favor, informe um e-mail válido";
                                            } catch (FirebaseAuthUserCollisionException e) {
                                                erroExcecao = "E-mail já cadastrado";
                                            } catch (Exception e) {
                                                erroExcecao = "Ao Cadastrar usuário: " + e.getMessage();
                                                e.printStackTrace();
                                            }
                                            Toast.makeText(CadastrosUsuariosActivity.this, "Erro: " + erroExcecao, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(CadastrosUsuariosActivity.this, "Escolha a opção de tipo cadastro", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(CadastrosUsuariosActivity.this, "Informe sua senha", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(CadastrosUsuariosActivity.this, "Informe seu e-mail", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CadastrosUsuariosActivity.this, "Informe seu nome", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void inicializarComponentes() {
        campoNome = findViewById(R.id.txtinpedtCEP);
        campoEmail = findViewById(R.id.txtinpedtLogradouro);
        campoSenha = findViewById(R.id.txtinpedtComplemento);
        cliente = findViewById(R.id.radioButtonClienteCadastro);
        empresa = findViewById(R.id.radioButtonEmpresaCadastro);
        buttonCadastro = findViewById(R.id.buttonCadastro);
    }

    public String verificaUsuario() {
        return cliente.isChecked() ? "U" : "E";
    }

}
