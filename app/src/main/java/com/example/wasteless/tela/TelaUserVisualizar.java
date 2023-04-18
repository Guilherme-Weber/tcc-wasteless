package com.example.wasteless.tela;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wasteless.R;
import com.example.wasteless.UserPedidoAdapter;
import com.example.wasteless.roomdatabase.PedidoDao;
import com.example.wasteless.roomdatabase.PedidoDatabase;
import com.example.wasteless.roomdatabase.PedidoEntity;

import java.util.List;

public class TelaUserVisualizar extends AppCompatActivity {

    private String nome, email, curso; // Visualizar na tela
    private int horas;

    private RecyclerView recycler;
    private UserPedidoAdapter adapter;
    private List<PedidoEntity> itens;
    Button btn_voltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_WasteLess);
        setContentView(R.layout.activity_tela_user_visualizar);
        getSupportActionBar().hide();

        nome = getIntent().getStringExtra("nome");
        email = getIntent().getStringExtra("email");
        curso = getIntent().getStringExtra("curso");
        horas = getIntent().getIntExtra("horas", horas);

        //LISTAR TODOS OS PEDIDOS QUE O USUÁRIO TEM NA CONTA
        recycler = findViewById(R.id.recycler_pedidos);

        PedidoEntity pedidoEntity = new PedidoEntity();
        PedidoDatabase pedidoDatabase = PedidoDatabase.getPedidoDatabase(getApplicationContext());
        PedidoDao pedidoDao = pedidoDatabase.pedidoDao();

        List<PedidoEntity> pedido = pedidoDao.getAllPedidosAluno(email);

        adapter = new UserPedidoAdapter(pedido, TelaUserVisualizar.this, nome, email, curso, horas);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TelaUserVisualizar.this,
                LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(adapter);

        adapter.setNome(getIntent().getStringExtra("nome"));//PASSA VALOR PRA ADAPTER
        adapter.setEmail(getIntent().getStringExtra("email"));
        adapter.setCurso(getIntent().getStringExtra("curso"));
        adapter.setHoras(getIntent().getIntExtra("horas", horas));

        btn_voltar = findViewById(R.id.btn_voltar5);

        btn_voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                telaHome();
            }
        });
    }

    public void telaHome() {
        Intent tela = new Intent(TelaUserVisualizar.this, TelaHomeScreen.class)
                .putExtra("nome", nome)
                .putExtra("email", email)
                .putExtra("curso", curso)
                .putExtra("horas", horas);
        startActivity(tela);
        finish();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public int getHoras() {
        return horas;
    }

    public void setHoras(int horas) {
        this.horas = horas;
    }
}
