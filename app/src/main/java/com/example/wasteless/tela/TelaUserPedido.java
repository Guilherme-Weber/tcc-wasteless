package com.example.wasteless.tela;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wasteless.R;
import com.example.wasteless.roomdatabase.PedidoDao;
import com.example.wasteless.roomdatabase.PedidoDatabase;
import com.example.wasteless.roomdatabase.PedidoEntity;
import com.example.wasteless.roomdatabase.StoreDao;
import com.example.wasteless.roomdatabase.StoreDatabase;
import com.example.wasteless.roomdatabase.StoreEntity;

import io.github.muddz.styleabletoast.StyleableToast;

public class TelaUserPedido extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String nome, email, curso;
    int horas;// Visualizar na tela
    String tipo; // Spinner

    Button btn_voltar, btn_solicitar;
    EditText storetext, mensagemtext;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_WasteLess);
        setContentView(R.layout.activity_tela_user_pedido);
        getSupportActionBar().hide();

        nome = getIntent().getStringExtra("nome");
        email = getIntent().getStringExtra("email");
        curso = getIntent().getStringExtra("curso");
        horas = getIntent().getIntExtra("horas", horas);

        btn_voltar = findViewById(R.id.btn_voltar4);
        btn_solicitar = findViewById(R.id.btn_solicitar);

        PedidoDatabase pedidoDatabase = PedidoDatabase.getPedidoDatabase(getApplicationContext());
        PedidoDao pedidoDao = pedidoDatabase.pedidoDao();
        PedidoEntity pedidoEntity = new PedidoEntity();

        //GUARDAR VALORES DE UM PEDIDO AQUI
        storetext = findViewById(R.id.coordenadortext);
        spinner = findViewById(R.id.spinner);
        mensagemtext = findViewById(R.id.mensagemtext);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.tipo, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        StoreDatabase storeDatabase = StoreDatabase.getStoreDatabase(getApplicationContext());
        StoreDao storeDao = storeDatabase.storeDao();
        StoreEntity storeEntity = new StoreEntity();
        storeEntity = null;

        btn_solicitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String storeid = storetext.getText().toString();
                final String mensagem = mensagemtext.getText().toString();

                if (storeid.isEmpty() || mensagem.isEmpty()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toastErradoCampos(); // TOASTER DE PREENCHER TODOS OS CAMPOS
                        }
                    });
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            StoreEntity storeEntity = storeDao.loginEmail(storetext.getText().toString());

                            if (storeEntity == null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        toastErradoEmail(); // TOAST DE VERIFICAR EMAIL
                                    }
                                });
                            } else {
                                pedidoEntity.setStatus(new String("Enviado"));
                                pedidoEntity.setAlunoNome(nome);
                                pedidoEntity.setAlunoId(email);
                                pedidoEntity.setCurso(curso);
                                pedidoEntity.setCoordenaId(storetext.getText().toString());
                                pedidoEntity.setTexto(mensagemtext.getText().toString());
                                pedidoEntity.setTipo(tipo);

                                putOnPedido();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        toastCorretoCadastro(); //PEDIDO CADASTRADO TOASTER
                                        telaHome();
                                    }
                                });
                            }
                        }
                    }).start();
                }
            }
        });

        btn_voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                telaHome();
                //mensagemtext.setText(texto);
            }
        });
    }

    public void putOnPedido() {
        PedidoDatabase pedidoDatabase = PedidoDatabase.getPedidoDatabase(getApplicationContext());
        PedidoDao pedidoDao = pedidoDatabase.pedidoDao();
        PedidoEntity pedidoEntity = new PedidoEntity();

        pedidoEntity.setStatus(new String("Enviado"));
        pedidoEntity.setAlunoNome(nome);
        pedidoEntity.setAlunoId(email);
        pedidoEntity.setCurso(curso);
        pedidoEntity.setCoordenaId(storetext.getText().toString());
        pedidoEntity.setTexto(mensagemtext.getText().toString());
        pedidoEntity.setTipo(tipo);
        try {
            pedidoDao.registerPedido(pedidoEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void telaHome() {
        Intent tela = new Intent(TelaUserPedido.this, TelaHomeScreen.class)
                .putExtra("nome", nome)
                .putExtra("email", email)
                .putExtra("curso", curso)
                .putExtra("horas", horas);
        startActivity(tela);
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        tipo = adapterView.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public void toastCorretoCadastro() {
        StyleableToast.makeText(this, "Pedido Enviado!", R.style.toast_verificado).show();
    }

    public void toastErradoCampos() {
        StyleableToast.makeText(this, "Preencha Todos os Campos!", R.style.toast_negado).show();
    }

    public void toastErradoEmail() {
        StyleableToast.makeText(this, "E-mail Não Condiz Com Nenhum Coordenador", R.style.toast_negado).show();
    }
}