package com.guilhermeweber.wasteless.activity.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.guilhermeweber.wasteless.R;
import com.guilhermeweber.wasteless.activity.helper.ConfigFirebase;
import com.guilhermeweber.wasteless.activity.model.Empresa;
import com.guilhermeweber.wasteless.activity.model.Pedido;

public class PagamentoActivity extends AppCompatActivity {
    private RadioGroup radioGroupMetodoPagamento;
    private LinearLayout layoutCartaoCredito;
    private EditText textNumeroCartao;
    private EditText textDataExpiracao;
    private EditText textCVV;
    private Button buttonPagar;
    private Pedido pedido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagamento);

        //config toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Wasteless - Home");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        inicializarComponentes();


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) pedido = (Pedido) bundle.getSerializable("pedido");

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

        // Adiciona espaçamento a cada 4 dígitos no número do cartão
        textNumeroCartao.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String cleanText = s.toString().replace(" ", "");

                StringBuilder formattedText = new StringBuilder();
                for (int i = 0; i < cleanText.length(); i++) {
                    if (i > 0 && i % 4 == 0) {
                        formattedText.append(" ");
                    }
                    formattedText.append(cleanText.charAt(i));
                }

                textNumeroCartao.removeTextChangedListener(this);
                textNumeroCartao.setText(formattedText.toString());
                textNumeroCartao.setSelection(formattedText.length());
                textNumeroCartao.addTextChangedListener(this);
            }
        });

        // Formata a data como MM/YY
        textDataExpiracao.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String cleanText = s.toString().replace("/", "");
                if (cleanText.length() > 2) {
                    String formattedText = cleanText.substring(0, 2) + "/" + cleanText.substring(2);
                    textDataExpiracao.removeTextChangedListener(this);
                    textDataExpiracao.setText(formattedText);
                    textDataExpiracao.setSelection(formattedText.length());
                    textDataExpiracao.addTextChangedListener(this);
                }
            }
        });

        // Lógica para lidar com o pagamento quando o botão for clicado
        buttonPagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Lógica de pagamento aqui
                if (realizarPagamento()) {
                    mostrarAlertaPedidoConfirmado();
                }
            }
        });
    }

    private void inicializarComponentes() {

        radioGroupMetodoPagamento = findViewById(R.id.radioGroupMetodoPagamento);
        layoutCartaoCredito = findViewById(R.id.layoutCartaoCredito);
        textNumeroCartao = findViewById(R.id.textNumeroCartao);
        textDataExpiracao = findViewById(R.id.textDataExpiracao);
        textCVV = findViewById(R.id.textCVV);
        buttonPagar = findViewById(R.id.buttonPagar);

    }

    private boolean realizarPagamento() {
        if (radioGroupMetodoPagamento.getCheckedRadioButtonId() == -1) {
            exibirMensagemErro("Selecione um método de pagamento");
            return false;
        }

        if (radioGroupMetodoPagamento.getCheckedRadioButtonId() == R.id.radioButtonCredito || radioGroupMetodoPagamento.getCheckedRadioButtonId() == R.id.radioButtonDebito) {
            // Se o pagamento for cartão de crédito ou débito, validar os campos do cartão
            String numeroCartao = textNumeroCartao.getText().toString().trim();
            String dataExpiracao = textDataExpiracao.getText().toString().trim();
            String cvv = textCVV.getText().toString().trim();

            if (TextUtils.isEmpty(numeroCartao) || TextUtils.isEmpty(dataExpiracao) || TextUtils.isEmpty(cvv)) {
                exibirMensagemErro("Preencha todos os campos do cartão");
                return false;
            }
        }
        return true;
    }

    private void exibirMensagemErro(String mensagem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Erro no Pagamento");
        builder.setMessage(mensagem);
        builder.setPositiveButton("OK", null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void mostrarAlertaPedidoConfirmado() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pedido Confirmado");
        builder.setMessage("Seu pedido foi confirmado com sucesso!");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                pedido.setMetodoPagamento(0);
                pedido.setStatus("confirmado");
                pedido.confirmar();
                pedido.remover();
                pedido = null;

                // Fecha o alerta e retorna para a tela de histórico de pedidos
                Intent intent = new Intent(PagamentoActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        // Cria e exibe o AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
