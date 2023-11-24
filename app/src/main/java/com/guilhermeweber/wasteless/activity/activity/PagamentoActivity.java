package com.guilhermeweber.wasteless.activity.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.guilhermeweber.wasteless.R;

public class PagamentoActivity extends AppCompatActivity {

    private RadioGroup radioGroupMetodoPagamento;
    private LinearLayout layoutCartaoCredito;
    private EditText textNumeroCartao;
    private EditText textDataExpiracao;
    private EditText textCVV;
    private Button buttonPagar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagamento);

        radioGroupMetodoPagamento = findViewById(R.id.radioGroupMetodoPagamento);
        layoutCartaoCredito = findViewById(R.id.layoutCartaoCredito);
        textNumeroCartao = findViewById(R.id.textNumeroCartao);
        textDataExpiracao = findViewById(R.id.textDataExpiracao);
        textCVV = findViewById(R.id.textCVV);
        buttonPagar = findViewById(R.id.buttonPagar);

        radioGroupMetodoPagamento.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Verifica qual opção de pagamento foi selecionada
                if (checkedId == R.id.radioButtonCredito || checkedId == R.id.radioButtonDebito) {
                    // Mostra os campos do cartão de crédito se selecionar crédito ou débito
                    layoutCartaoCredito.setVisibility(View.VISIBLE);
                } else {
                    // Esconde os campos do cartão de crédito se selecionar Pix
                    layoutCartaoCredito.setVisibility(View.GONE);
                }
            }
        });

        // Lógica para lidar com o pagamento quando o botão for clicado
        buttonPagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica de pagamento aqui

                Intent intent = new Intent(PagamentoActivity.this, PedidoConfirmadoActivity.class);
                startActivity(intent);
            }
        });
/*
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

 */
/*
    // Método simulando a lógica de pagamento
    private void realizarPagamento(String metodoPagamento) {

        // Mensagem somente indicando o método de pagamento escolhido
        String mensagem = "Pagamento realizado com sucesso usando " + metodoPagamento;
        Intent intent = new Intent(PagamentoActivity.this, PedidoConfirmadoActivity.class);
        intent.putExtra("mensagem", mensagem);
        startActivity(intent);
    }

 */
    }
}