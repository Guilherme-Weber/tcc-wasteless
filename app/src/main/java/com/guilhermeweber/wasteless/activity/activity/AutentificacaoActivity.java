package com.guilhermeweber.wasteless.activity.activity;

import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.POST_NOTIFICATIONS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_IMAGES;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.guilhermeweber.wasteless.R;
import com.guilhermeweber.wasteless.activity.helper.ConfigFirebase;
import com.guilhermeweber.wasteless.activity.helper.Permissoes;
import com.guilhermeweber.wasteless.activity.model.Usuario;

public class AutentificacaoActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 200;
    private String[] permissoes = new String[]{Manifest.permission.INTERNET};
    private String[] permissoes2 = new String[]{Manifest.permission.ACCESS_NETWORK_STATE};
    private String[] permissoes3 = new String[]{READ_EXTERNAL_STORAGE};
    private String[] permissoes4 = new String[]{POST_NOTIFICATIONS};
    private String[] permissoes5 = new String[]{READ_MEDIA_IMAGES};
    private Button botaoAcessar, buttonCadastrar;
    private EditText campoEmail, campoSenha, campoNome;
    private Switch tipoAcesso, tipoUsuario;
    private LinearLayout linearTipoUsuario;
    private FirebaseAuth auth;
    private AlertDialog dialog = null;
    private ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            // Permission is granted. Continue the action or workflow in your
            // app.
        } else {
            // Explain to the user that the feature is unavailable because the
            // feature requires a permission that the user has denied. At the
            // same time, respect the user's decision. Don't link to system
            // settings in an effort to convince the user to change their
            // decision.
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Usuario usuario = new Usuario();

        if (usuario.getUsuarioAtual() != null) {
            Usuario.redirectUser(AutentificacaoActivity.this);
        }

        requestPermissionLauncher.launch(POST_NOTIFICATIONS);
        requestPermissionLauncher.launch(READ_MEDIA_IMAGES);
        requestPermissionLauncher.launch(INTERNET);
        requestPermissionLauncher.launch(READ_EXTERNAL_STORAGE);
        Permissoes.validarPermissoes(permissoes, this, 1);
        Permissoes.validarPermissoes(permissoes2, this, 1);
        Permissoes.validarPermissoes(permissoes3, this, 1);
        Permissoes.validarPermissoes(permissoes4, this, 1);
        Permissoes.validarPermissoes(permissoes5, this, 1);

        setContentView(R.layout.activity_autentificacao);

        inicializarComponentes();
        auth = ConfigFirebase.getFireAuth();

        ActivityCompat.requestPermissions(this, new String[]{POST_NOTIFICATIONS, READ_MEDIA_IMAGES, INTERNET, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

        //forsa o usuario ja logado a deslogar
        //auth.signOut();
        //força user null pra casos mais extremos
        // usuario = null;

        buttonCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AutentificacaoActivity.this, CadastrosUsuariosActivity.class));
            }
        });

        botaoAcessar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = campoEmail.getText().toString();
                String senha = campoSenha.getText().toString();

                usuario.setEmail(email);
                usuario.setSenha(senha);

                //verifica se os campos estão vazios
                if (!email.isEmpty()) {
                    if (!senha.isEmpty()) {

                        auth.signInWithEmailAndPassword( // envia pro servidor email e senha pra logar
                                email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) { // verifica se o retorno foi um sucesso

                                    usuario.redirectUser(AutentificacaoActivity.this);
                                    Toast.makeText(AutentificacaoActivity.this, "Login Efetuado", Toast.LENGTH_SHORT).show();

                                } else {// caso login tenha dado falha ele informa ao usuario o erro

                                    String excecao = "";
                                    try {
                                        throw task.getException();
                                    } catch (FirebaseAuthInvalidUserException e) {
                                        excecao = "Usuario não cadastrado";
                                        campoEmail.setError("Usuario não cadastrado");
                                        campoSenha.setError("Usuario não cadastrado");
                                    } catch (FirebaseAuthInvalidCredentialsException e) {
                                        excecao = "E-mail e senha inválidos";
                                        campoEmail.setError("E-mail inválida");
                                        campoSenha.setError("Senha inválida");
                                    } catch (Exception e) {
                                        excecao = "Erro ao cadastrar usuário" + e.getMessage();
                                        e.printStackTrace();
                                    }
                                    Toast.makeText(AutentificacaoActivity.this, excecao, Toast.LENGTH_LONG).show();

                                }
                            }
                        });
                    } else {
                        Toast.makeText(AutentificacaoActivity.this, "Informe sua senha", Toast.LENGTH_SHORT).show();
                        campoSenha.setError("Informe sua senha");

                    }
                } else {
                    Toast.makeText(AutentificacaoActivity.this, "Informe seu e-mail", Toast.LENGTH_SHORT).show();
                    campoEmail.setError("Informe seu e-mail");
                }
            }
        });
    }

    private void inicializarComponentes() {

        campoEmail = findViewById(R.id.editTextEmailLogin);
        campoSenha = findViewById(R.id.editTextSenhaLogin);
        botaoAcessar = findViewById(R.id.buttonAcesso);
        buttonCadastrar = findViewById(R.id.buttonCadastrar);

    }
}