package com.guilhermeweber.wasteless.activity.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.guilhermeweber.wasteless.R;

public class HistoricoPedidosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico_pedidos);

        /*
        //configurando os botões do menu inferior
        ImageButton btnHome = findViewById(R.id.btnHome);
        ImageButton btnPedido = findViewById(R.id.btnPedido);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HistoricoPedidosActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        btnPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HistoricoPedidosActivity.this, HistoricoPedidosActivity.class);
                startActivity(intent);
            }
        });
         */
    }
}