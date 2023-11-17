package com.guilhermeweber.wasteless.activity.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.guilhermeweber.wasteless.R;

public class CarrinhoActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCarrinho;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrinho);

        Button continuarPedidoButton = findViewById(R.id.continuarPedidoButton);
        recyclerViewCarrinho = findViewById(R.id.recyclerCarrinho);

        continuarPedidoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Iniciar a tela de forma de pagamento
                Intent intent = new Intent(CarrinhoActivity.this, PagamentoActivity.class);
                startActivity(intent);
            }
        });

        //Adapter


        //Configuração RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewCarrinho.setLayoutManager(layoutManager);
        recyclerViewCarrinho.setHasFixedSize(true);
        //recyclerViewCarrinho.setAdapter();
    }
}