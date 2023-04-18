package com.example.wasteless;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wasteless.roomdatabase.StoreDao;
import com.example.wasteless.roomdatabase.StoreDatabase;
import com.example.wasteless.roomdatabase.StoreEntity;
import com.example.wasteless.roomdatabase.UserDao;
import com.example.wasteless.roomdatabase.UserDatabase;
import com.example.wasteless.roomdatabase.UserEntity;
import com.example.wasteless.tela.TelaAdm;
import com.example.wasteless.tela.TelaCadastroUser;
import com.example.wasteless.tela.TelaHomeScreen;
import com.example.wasteless.tela.TelaHomeStore;

import io.github.muddz.styleabletoast.StyleableToast;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    EditText emailtext, senhatext;
    Button btn_acessar, btn_cadastro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(R.style.Theme_WasteLess);//SPASH SCREEN
        setContentView(R.layout.activity_main);//ATIVIDADE
        getSupportActionBar().hide();

        emailtext = findViewById(R.id.emailText);
        senhatext = findViewById(R.id.senhaText);

        btn_acessar = findViewById(R.id.btn_acesso);
        btn_cadastro = findViewById(R.id.btn_acessar);

        btn_acessar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Função de Login
                //Validação de Input
                final String email = emailtext.getText().toString();
                final String senha = senhatext.getText().toString();

                if (email.isEmpty() || senha.isEmpty()) {
                    toastErradoCampos();// TOAST DE VERIFICAÇÃO DE CAMPOS ---------
                } else {

                    //Login de administrador
                    if (email.equals("admin") && (senha.equals("1234"))) {

                        toastCorretoAdm();// TOAST DE VERIFICAÇÃO ADMINISTRADOR -------------
                        telaAdm();
                    } else {

                        //Realizar o query
                        UserDatabase userDatabase = UserDatabase.getUserDatabase(getApplicationContext());
                        StoreDatabase storeDatabase = StoreDatabase.getStoreDatabase(getApplicationContext());
                        final UserDao userDao = userDatabase.userDao();
                        final StoreDao storeDao = storeDatabase.storeDao();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                UserEntity userEntity = userDao.login(email, senha);
                                StoreEntity storeEntity = storeDao.login(email, senha);

                                // adicionar uma logica a onde ser for userEntity == null ele entrana na tela de coordenador e ser for coordenaEntity == null ele
                                // entra na tela de usuario caso ambos sejão null ele dira o erro "Login ou Senha Incorretos!"
                                if (userEntity == null && storeEntity == null) {

                                    //LÓGICA DE ERRO DE PROCURA DE CADASTRO
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            toastErradoLogin(); //TOAST DE VERIFICAÇÃO DE LOGIN
                                        }
                                    });
                                } else if (userEntity == null) {

                                    //LÓGICA PARA ENTRAR NA DE COORDENADOR
                                    String nome = storeEntity.getNome();
                                    String email = storeEntity.getStoreId();
                                    String bio = storeEntity.getBio();
                                    startActivity(new Intent(MainActivity.this, TelaHomeStore.class)
                                            .putExtra("nome", nome)
                                            .putExtra("email", email)
                                            .putExtra("Bio", bio));

                                } else if (storeEntity == null) {

                                    //LÓGICA PARA ENTRAR NA DE ALUNO(USER)
                                    String nome = userEntity.getNome();
                                    String email = userEntity.getUserId();
                                    String fav1 = userEntity.getFav1();
                                    int horas = userEntity.getHoras();
                                    startActivity(new Intent(MainActivity.this, TelaHomeScreen.class)
                                            .putExtra("nome", nome)
                                            .putExtra("email", email)
                                            .putExtra("fav1", fav1)
                                            .putExtra("horas", horas));

                                } else {

                                    //LÓGICA DE ERRO ASTRONOMICO
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "RATINHO JUNIOR PARABENS VC N DEVIA ESTAR LENDO ISSO", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }).start();
                    }
                }
            }
        });

        btn_cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                telaCadastro();//TELA TIPO CADASTRO
            }
        });
    }

    public void telaCadastro() {
        Intent tela = new Intent(MainActivity.this, TelaCadastroUser.class);
        startActivity(tela);
        finish();
    }

    public void telaAdm() {
        Intent tela = new Intent(MainActivity.this, TelaAdm.class);
        startActivity(tela);
        finish();
    }

    public void toastCorretoAdm() {
        StyleableToast.makeText(this, "Bem vindo Administrador", R.style.toast_verificado).show();
    }

    public void toastErradoCampos() {
        StyleableToast.makeText(this, "Preencha Todos os Campos!", R.style.toast_negado).show();
    }

    public void toastErradoLogin() {
        StyleableToast.makeText(this, "Login ou Senha Incorretos!", R.style.toast_negado).show();
    }
}