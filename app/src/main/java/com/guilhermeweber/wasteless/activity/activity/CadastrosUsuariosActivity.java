package com.guilhermeweber.wasteless.activity.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
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
import com.santalu.maskedittext.MaskEditText;


public class CadastrosUsuariosActivity extends AppCompatActivity {

    private EditText campoNome, campoEmail, campoSenha, campoSenhaDenovo;
    private MaskEditText editTextTelefone;
    private String nome, email, senha, senhadenovo, telefone;
    private RadioButton cliente, empresa;
    private Button buttonCadastro;
    private FirebaseAuth auth;
    private CheckBox checkBoxTermos;

    public static boolean stringCompare(String str1, String str2) {

        int l1 = str1.length();
        int l2 = str2.length();
        int lmin = Math.min(l1, l2);

        for (int i = 0; i < lmin; i++) {
            int str1_ch = (int) str1.charAt(i);
            int str2_ch = (int) str2.charAt(i);

            if (str1_ch != str2_ch) {
                return false;
            }
        }

        if (l1 != l2) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastros_usuarios);

        Usuario usuario = new Usuario();

        TextView textTermos = findViewById(R.id.textTermos);
        checkBoxTermos = findViewById(R.id.checkBoxTermos);

        textTermos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Site com os "termos"
                String url = "https://docs.google.com/document/d/1o0RWL4XDadayu7z3_n6zjX6glhPmCtM3SfiEiQXm5dQ/edit";

                //Criando a Intent que irá abrir a URL no navegador
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

                //Iniciando a Intent
                startActivity(intent);
            }
        });

        inicializarComponentes();
        auth = ConfigFirebase.getFireAuth();

        buttonCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = campoEmail.getText().toString();
                senha = campoSenha.getText().toString();
                senhadenovo = campoSenhaDenovo.getText().toString();
                nome = campoNome.getText().toString();
                telefone = editTextTelefone.getText().toString();

                String fone = "";
                if (editTextTelefone.getRawText() != null) {
                    fone = editTextTelefone.getRawText().toString();
                }

                usuario.setNome(nome);
                usuario.setEmail(email);
                usuario.setSenha(senha);
                usuario.setTelefone(telefone);
                usuario.setTipo(verificaUsuario());

                if (nome.isEmpty()) {
                    campoNome.setError("Informe seu nome");
                    Toast.makeText(CadastrosUsuariosActivity.this, "Informe seu nome", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (email.isEmpty()) {
                    campoEmail.setError("Informe seu e-mail");
                    Toast.makeText(CadastrosUsuariosActivity.this, "Informe seu e-mail", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (senha.isEmpty()) {
                    campoSenha.setError("Informe sua senha");
                    Toast.makeText(CadastrosUsuariosActivity.this, "Informe sua senha", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (cliente.isChecked() || empresa.isChecked()) {
                    if (!telefone.isEmpty() && fone.length() >= 10) {
                        if (stringCompare(senha, senhadenovo)) {
                            if (checkBoxTermos.isChecked()) {

                                auth.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        if (task.isSuccessful()) { //Verifica se o processo de cadastro do usuario deu certo

                                            String idUsuario = task.getResult().getUser().getUid();

                                            usuario.setId(idUsuario);

                                            Intent i = new Intent(CadastrosUsuariosActivity.this, CadastroEnderecoActivity.class);
                                            i.putExtra("usuario", usuario);
                                            startActivity(i);

                                        } else {

                                            //caso cadastro n seja realizado com sucesso é mostrado uma mensagem de erro
                                            String erroExcecao = "";
                                            try {
                                                throw task.getException();
                                            } catch (FirebaseAuthWeakPasswordException e) {
                                                erroExcecao = "Informe uma senha mais forte!";
                                                campoSenha.setError("Informe uma senha mais forte!");
                                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                                erroExcecao = "Por Favor, informe um e-mail válido";
                                                campoEmail.setError("Por Favor, informe um e-mail válido");
                                            } catch (FirebaseAuthUserCollisionException e) {
                                                erroExcecao = "E-mail já cadastrado";
                                                campoEmail.setError("E-mail já cadastrado");
                                            } catch (Exception e) {
                                                erroExcecao = "Ao Cadastrar usuário: " + e.getMessage();
                                                e.printStackTrace();
                                            }
                                            Toast.makeText(CadastrosUsuariosActivity.this, "Erro: " + erroExcecao, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(CadastrosUsuariosActivity.this, "Você deve conconcordar com os termos e condições de uso", Toast.LENGTH_SHORT).show();
                                checkBoxTermos.setError("Concorde com os termos e condições de uso");
                            }
                        } else {
                            String pw = "Ambas as senhas devem ser idênticas";
                            Toast.makeText(CadastrosUsuariosActivity.this, pw, Toast.LENGTH_SHORT).show();
                            campoSenha.setError(pw);
                            campoSenhaDenovo.setError(pw);
                        }
                    } else {
                        String tf = "Preencha o campo telefone";
                        Toast.makeText(CadastrosUsuariosActivity.this, tf, Toast.LENGTH_SHORT).show();
                        editTextTelefone.setError(tf);
                    }
                } else {
                    String t = "Escolha a opção de tipo cadastro";
                    Toast.makeText(CadastrosUsuariosActivity.this, t, Toast.LENGTH_SHORT).show();
                    cliente.setError(t);
                    empresa.setError(t);
                }
            }
        });
    }

    private void inicializarComponentes() {

        campoNome = findViewById(R.id.editTextNome);
        campoEmail = findViewById(R.id.editTextEmail);
        campoSenha = findViewById(R.id.editTextSenha);
        campoSenhaDenovo = findViewById(R.id.editTextSenhaDenovo);
        editTextTelefone = findViewById(R.id.editTextTelefoneAuth);
        cliente = findViewById(R.id.radioButtonClienteCadastro);
        empresa = findViewById(R.id.radioButtonEmpresaCadastro);
        buttonCadastro = findViewById(R.id.buttonCadastroProximo);
        checkBoxTermos = findViewById(R.id.checkBoxTermos);

    }

    public String verificaUsuario() {
        return cliente.isChecked() ? "U" : "E";
    }
}
