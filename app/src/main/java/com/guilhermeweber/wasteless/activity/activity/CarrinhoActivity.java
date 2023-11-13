package com.guilhermeweber.wasteless.activity.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.guilhermeweber.wasteless.R;

public class CarrinhoActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCarrinho;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrinho);

        recyclerViewCarrinho = findViewById(R.id.recyclerCarrinho);

        //Adapter


        //Configuração RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewCarrinho.setLayoutManager(layoutManager);
        recyclerViewCarrinho.setHasFixedSize(true);
        //recyclerViewCarrinho.setAdapter();
    }
}