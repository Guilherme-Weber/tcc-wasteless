package com.example.wasteless.tela;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wasteless.MainActivity;
import com.example.wasteless.R;
import com.example.wasteless.StorePedidoAdapter;
import com.example.wasteless.roomdatabase.PedidoDao;
import com.example.wasteless.roomdatabase.PedidoDatabase;
import com.example.wasteless.roomdatabase.PedidoEntity;

import java.util.List;

public class TelaHomeStore extends AppCompatActivity {

    String email;

    private RecyclerView recycler;
    private StorePedidoAdapter adapter;
    TextView tnome;
    Button btn_voltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_WasteLess);//SPASH SCREEN
        setContentView(R.layout.activity_tela_home_store);
        getSupportActionBar().hide();

        tnome = findViewById(R.id.text_coordenador);
        String usuario = getIntent().getStringExtra("nome");
        String email = getIntent().getStringExtra("email");
        String curso = getIntent().getStringExtra("curso");

        recycler = findViewById(R.id.recycler_coordenador);

        PedidoEntity pedidoEntity = new PedidoEntity();
        PedidoDatabase pedidoDatabase = PedidoDatabase.getPedidoDatabase(getApplicationContext());
        PedidoDao pedidoDao = pedidoDatabase.pedidoDao();

        List<PedidoEntity> pedido = pedidoDao.getAllPedidosCoordena(email);//Coordenador

        adapter = new StorePedidoAdapter(TelaHomeStore.this, pedido, email);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TelaHomeStore.this,
                LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(adapter);

        btn_voltar = findViewById(R.id.btn_voltar6);

        btn_voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TelaMain();
            }
        });
    }

    public void TelaMain() {
        Intent tela = new Intent(TelaHomeStore.this, MainActivity.class)
                .putExtra("email", email);
        startActivity(tela);
        finish();
    }
}