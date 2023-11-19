package com.guilhermeweber.wasteless.activity.activity;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.POST_NOTIFICATIONS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_IMAGES;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
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
    private String[] permissoes6 = new String[]{ACCESS_COARSE_LOCATION};
    private String[] permissoes7 = new String[]{ACCESS_FINE_LOCATION};
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
            //dialog.show();


            //adicionar tela de carregando aqui no futuro
//            Usuario.redirectUser(AutentificacaoActivity.this);
        }

        requestPermissionLauncher.launch(POST_NOTIFICATIONS);
        requestPermissionLauncher.launch(READ_MEDIA_IMAGES);
        requestPermissionLauncher.launch(INTERNET);
        requestPermissionLauncher.launch(READ_EXTERNAL_STORAGE);
        requestPermissionLauncher.launch(ACCESS_COARSE_LOCATION);
        requestPermissionLauncher.launch(ACCESS_FINE_LOCATION);

        Permissoes.validarPermissoes(permissoes, this, 1);
        Permissoes.validarPermissoes(permissoes2, this, 1);
        Permissoes.validarPermissoes(permissoes3, this, 1);
        Permissoes.validarPermissoes(permissoes4, this, 1);
        Permissoes.validarPermissoes(permissoes5, this, 1);
        Permissoes.validarPermissoes(permissoes6, this, 1);
        Permissoes.validarPermissoes(permissoes7, this, 1);

        setContentView(R.layout.activity_autentificacao);

        //ideia pra a o carregamento mais suave da tela de login
//        dialog = new SpotsDialog.Builder().setContext(this).setMessage("Carregando Dados").setCancelable(false).build();

        inicializarComponentes();
        auth = ConfigFirebase.getFireAuth();

        ActivityCompat.requestPermissions(this, new String[]{POST_NOTIFICATIONS, READ_MEDIA_IMAGES, INTERNET, READ_EXTERNAL_STORAGE, ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);

        //forsa o usuario ja logado a deslogar
        //auth.signOut();
        //força user null pra casos mais extremos
//        usuario = null;

        //verifica o usuario logado


//        tipoAcesso.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) { // se estiver selecionado é uma empresa
//                    botaoAcessar.setText("Cadastrar");
//                    linearTipoUsuario.setVisibility(View.VISIBLE);
//                    campoNome.setVisibility(View.VISIBLE);
//                } else { // se n estiver selecionado é um usuario
//                    botaoAcessar.setText("Acessar");
//                    linearTipoUsuario.setVisibility(View.GONE);
//                    campoNome.setVisibility(View.GONE);
//                }
//            }
//        });


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
//                String nome = campoNome.getText().toString();

//                usuario.setNome(nome);
                usuario.setEmail(email);
                usuario.setSenha(senha);
                usuario.setTipo(verificaUsuario());

                //verifica se os campos estão vazios
                if (!email.isEmpty()) {
                    if (!senha.isEmpty()) {

                        //TELA DEDICADA DE CADASTRO CRIADA CODIGO ABAIXO APENAS PARA CONSULTA

                        //Verifica se o switch esta em login ou cadastro
//                        if (tipoAcesso.isChecked()) { //cadastro
//
//                            auth.createUserWithEmailAndPassword( // envia pro servidor o email e senha pra cadastro
//                                    usuario.getEmail(), usuario.getSenha()
//                            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                                @Override
//                                public void onComplete(@NonNull Task<AuthResult> task) {
//
//                                    if (task.isSuccessful()) { //Verifica se o processo de cadastro do usuario deu certo
//
//                                        String idUsuario = task.getResult().getUser().getUid();
//                                        usuario.setId(idUsuario);
//                                        usuario.salvar();
//
//                                        //usuario.updateUserName(usuario.getNome());
//
//                                        if (usuario.getTipo() == "E") {//Empresa
//                                            startActivity(new Intent(getApplicationContext(), EmpresaActivity.class));
//                                            finish();
//                                            Toast.makeText(AutentificacaoActivity.this, "Empresa Cadastrada com Sucesso! ", Toast.LENGTH_LONG).show();
//                                        } else {//usuario
//                                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
//                                            finish();
//                                            Toast.makeText(AutentificacaoActivity.this, "Usuario Cadastrado com Sucesso! ", Toast.LENGTH_LONG).show();
//                                        }
//                                    } else {
//
//                                        //caso cadastro n seja realizado com sucesso é mostrado uma mensagem de erro
//                                        String erroExcecao = "";
//                                        try {
//                                            throw task.getException();
//                                        } catch (FirebaseAuthWeakPasswordException e) {
//                                            erroExcecao = "Digite uma senha mais forte!";
//                                        } catch (FirebaseAuthInvalidCredentialsException e) {
//                                            erroExcecao = "Por Favor, digite um e-mail válido";
//                                        } catch (FirebaseAuthUserCollisionException e) {
//                                            erroExcecao = "Esta conta já foi cadastrada";
//                                        } catch (Exception e) {
//                                            erroExcecao = "Ao Cadastrar usuário: " + e.getMessage();
//                                            e.printStackTrace();
//                                        }
//                                        Toast.makeText(AutentificacaoActivity.this, "Erro:" + erroExcecao, Toast.LENGTH_LONG).show();
//                                    }
//                                }
//                            });
//
//                        } else { //login

                        auth.signInWithEmailAndPassword( // envia pro servidor email e senha pra logar
                                email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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
                                        campoEmail.setError("Usuario não está cadastrado");
                                        campoSenha.setError("Usuario não está cadastrado");
                                    } catch (FirebaseAuthInvalidCredentialsException e) {
                                        excecao = "E-mail e senha não são validos";
                                        campoEmail.setError("E-mail e senha não são validos");
                                        campoSenha.setError("E-mail e senha não são validos");
                                    } catch (Exception e) {
                                        excecao = "erro ao cadastrar usuário" + e.getMessage();
                                        e.printStackTrace();
                                    }
                                    Toast.makeText(AutentificacaoActivity.this, excecao, Toast.LENGTH_LONG).show();

                                }
                            }
                        });
//                        }
                    } else {
//                        Toast.makeText(AutentificacaoActivity.this, "Preencha o Senha!", Toast.LENGTH_SHORT).show();
                        campoSenha.setError("Informe sua senha");

                    }
                } else {
//                    Toast.makeText(AutentificacaoActivity.this, "Preencha o E-mail!", Toast.LENGTH_SHORT).show();
                    campoEmail.setError("Informe seu e-mail");
                }
            }
        });
    }

    private void inicializarComponentes() {

//        campoNome = findViewById(R.id.editTextNome);
        campoEmail = findViewById(R.id.editTextEmailLogin);
        campoSenha = findViewById(R.id.editTextSenhaLogin);
//        tipoAcesso = findViewById(R.id.switchTipoAcesso);
//        tipoUsuario = findViewById(R.id.switchTipoUsuario);
        botaoAcessar = findViewById(R.id.buttonAcesso);
        buttonCadastrar = findViewById(R.id.buttonCadastrar);
//        linearTipoUsuario = findViewById(R.id.linearTipoUsuario);
    }

    public String verificaUsuario() {
        String result = "result";


        return result;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissaoResultado : grantResults) {
            if (permissaoResultado == PackageManager.PERMISSION_DENIED) {
//                alertPermissao();
            }
        }
    }

    private void alertPermissao() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }
}