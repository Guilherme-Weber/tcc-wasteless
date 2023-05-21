package com.guilhermeweber.wasteless.activity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.guilhermeweber.wasteless.R;
import com.guilhermeweber.wasteless.activity.helper.ConfigFirebase;

public class AutentificacaoActivity extends AppCompatActivity {

    private Button botaoAcessar;
    private EditText campoEmail, campoSenha;
    private Switch tipoAcesso;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autentificacao);

        inicializarComponentes();
        auth = ConfigFirebase.getFireAuth();

        //forsa o usuario ja logado a deslogar
        //auth.signOut();

        //verifica o usuario logado
        usuarioLogado();

        botaoAcessar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = campoEmail.getText().toString();
                String senha = campoSenha.getText().toString();

                //verifica se os campos estão vazios
                if (!email.isEmpty()) {
                    if (!senha.isEmpty()) {

                        //Verifica se o switch esta em login ou cadastro
                        if (tipoAcesso.isChecked()) { //cadastro

                            auth.createUserWithEmailAndPassword( // envia pro servidor o email e senha pra cadastro
                                    email, senha
                            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) { //Verifica se o processo de cadastro do usuario deu certo

                                        Toast.makeText(AutentificacaoActivity.this, "Cadastro realizado com Sucesso!", Toast.LENGTH_LONG).show();
                                        telaHome();

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

                                        Toast.makeText(AutentificacaoActivity.this, "Logado com Sucesso", Toast.LENGTH_SHORT).show();
                                        telaHome();

                                    } else {// caso login tenha dado falha ele informa ao usuario o erro
                                        Toast.makeText(AutentificacaoActivity.this, "Erro ao fazer login: " + task.getException(), Toast.LENGTH_LONG).show();
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

    private void usuarioLogado() {
        FirebaseUser usuario = auth.getCurrentUser();
        if (usuario != null) {
            telaHome();
        }
    }

    private void inicializarComponentes() {
        campoEmail = findViewById(R.id.editTextCadastroEmail);
        campoSenha = findViewById(R.id.editTextCadastroSenha);
        tipoAcesso = findViewById(R.id.switchAcesso);
        botaoAcessar = findViewById(R.id.buttonAcesso);
    }

    private void telaHome() {
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }
}