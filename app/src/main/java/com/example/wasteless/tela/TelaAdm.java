package com.example.wasteless.tela;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wasteless.MainActivity;
import com.example.wasteless.R;
import com.example.wasteless.roomdatabase.StoreDao;
import com.example.wasteless.roomdatabase.StoreDatabase;
import com.example.wasteless.roomdatabase.StoreEntity;
import com.example.wasteless.roomdatabase.UserDao;
import com.example.wasteless.roomdatabase.UserDatabase;
import com.example.wasteless.roomdatabase.UserEntity;

import io.github.muddz.styleabletoast.StyleableToast;

public class TelaAdm extends AppCompatActivity {

    Button btn_voltar, btn_cadastrar;
    EditText nometext, emailtext, senhatext, confsenhatext, cursotext;
    int val = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(R.style.Theme_WasteLess);
        setContentView(R.layout.activity_tela_adm);
        getSupportActionBar().hide();

        nometext = findViewById(R.id.nomeText);
        emailtext = findViewById(R.id.emailText);
        senhatext = findViewById(R.id.senhaText);
        confsenhatext = findViewById(R.id.confsenhaText);
        cursotext = findViewById(R.id.fav1Text);

        btn_voltar = findViewById(R.id.btn_voltar);
        btn_cadastrar = findViewById(R.id.btn_cadastrar_coordenador);

        UserDatabase userDatabase = UserDatabase.getUserDatabase(getApplicationContext());
        UserDao userDao = userDatabase.userDao();

        StoreDatabase storeDatabase = StoreDatabase.getStoreDatabase(getApplicationContext());
        StoreDao storeDao = storeDatabase.storeDao();

        StoreEntity storeEntity = new StoreEntity();
        UserEntity userEntity = new UserEntity();

        storeEntity = null;
        userEntity = null;

        btn_voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TelaMain();
            }
        });

        //FUNÇÕES DE CADASTRAR
        btn_cadastrar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                val = 0;

                final String nome = nometext.getText().toString();
                final String email = emailtext.getText().toString();
                final String senha = senhatext.getText().toString();
                final String curso = cursotext.getText().toString();

                //checa pra ver se todos os campos estão preenchidos
                if (nome.isEmpty() ||
                        email.isEmpty() ||
                        senha.isEmpty() ||
                        curso.isEmpty()) {
                    toastErradoCampos(); //TOAST DE CAMPOS INCOMPLETOS
                } else {

                    //checa pra ver se as senhas são iguais
                    if (new String(senhatext.getText().toString()).equals(new String(confsenhatext.getText().toString()))) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                UserEntity userEntity = userDao.loginEmail(emailtext.getText().toString());
                                StoreEntity storeEntity = storeDao.loginEmail(emailtext.getText().toString());

                                //checa pra ver se ambas as Entitys estão vasias, com ambas vazias o email não esta cadastro no sistema
                                if (userEntity == null && storeEntity == null) {

                                    //coloca as informações digitadas na UserEntity
                                    //e cadastra o coordenador
                                    putOnCoordena();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            toastCorretoCadastro();// TOAST DE CADASTRAR COORDENADOR
                                        }
                                    });
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            toastErradoEmail();// TOAST EMAIL JÁ EXISTE
                                            finish();
                                            startActivity(getIntent());
                                        }
                                    });
                                }
                            }
                        }).start();
                    } else {
                        toastErradoSenha();// TOAST DE SENHA ERRADA
                    }
                }
            }
        });
    }

    public void putOnCoordena() {
        StoreEntity storeEntity = new StoreEntity();

        //coloca as informações dentro da Entity
        storeEntity.setNome(nometext.getText().toString());
        storeEntity.setStoreId(emailtext.getText().toString());
        storeEntity.setSenha(senhatext.getText().toString());
        storeEntity.setBio(cursotext.getText().toString());

        StoreDatabase storeDatabase = StoreDatabase.getStoreDatabase(getApplicationContext());
        StoreDao storeDao = storeDatabase.storeDao();

        //Fazer o Insert
        //Registra Usuario
        storeDao.registerStore(storeEntity);
    }


    public void TelaMain() {
        Intent tela = new Intent(TelaAdm.this, MainActivity.class);
        startActivity(tela);
        finish();
    }

    public void toastCorretoCadastro() {
        StyleableToast.makeText(this, "Coordenador Cadastrado!", R.style.toast_verificado).show();
    }

    public void toastErradoCampos() {
        StyleableToast.makeText(this, "Preencha Todos os Campos!", R.style.toast_negado).show();
    }

    public void toastErradoEmail() {
        StyleableToast.makeText(this, "Email já Cadastrado!", R.style.toast_negado).show();
    }

    public void toastErradoSenha() {
        StyleableToast.makeText(this, "As Senhas Não São Iguais", R.style.toast_negado).show();
    }
}