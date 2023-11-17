package com.guilhermeweber.wasteless.activity.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.guilhermeweber.wasteless.R;

public class PagamentoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagamento);

        // Botão para pagar com cartão de crédito
        Button buttonCredito = findViewById(R.id.buttonCredito);
        buttonCredito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lógica para pagamento com cartão de crédito
                realizarPagamento("Cartão de Crédito");
            }
        });

        // Botão para pagar com Débito
        Button buttonDebito = findViewById(R.id.buttonDebito);
        buttonDebito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lógica para pagamento com débito
                realizarPagamento("Débito");
            }
        });

        // Botão para pagar com Pix
        Button buttonPix = findViewById(R.id.buttonPix);
        buttonPix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lógica de pagamento com Pix aqui
                realizarPagamento("Pix");
            }
        });
    }

    // Método simulando a lógica de pagamento
    private void realizarPagamento(String metodoPagamento) {

        // Mensagem somente indicando o método de pagamento escolhido
        String mensagem = "Pagamento realizado com sucesso usando " + metodoPagamento;
        Intent intent = new Intent(PagamentoActivity.this, PedidoConfirmadoActivity.class);
        intent.putExtra("mensagem", mensagem);
        startActivity(intent);
    }
}