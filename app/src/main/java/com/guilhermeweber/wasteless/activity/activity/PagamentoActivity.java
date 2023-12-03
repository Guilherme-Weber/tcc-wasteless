package com.guilhermeweber.wasteless.activity.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.guilhermeweber.wasteless.R;
import com.guilhermeweber.wasteless.activity.helper.ConfigFirebase;
import com.guilhermeweber.wasteless.activity.model.Empresa;
import com.guilhermeweber.wasteless.activity.model.Pedido;

import java.io.InputStream;
import java.util.Locale;
import java.util.Random;

import dmax.dialog.SpotsDialog;

public class PagamentoActivity extends AppCompatActivity {
    String contact = "+55 41 99844-2385";
    private RadioGroup radioGroupMetodoPagamento;
    private RadioButton radioButtonCredito, radioButtonDebito, radioButtonPix;
    private LinearLayout layoutCartaoCredito;
    private EditText textNumeroCartao, textDataExpiracao, textCVV;
    private Button buttonPagar;
    private Pedido pedido;
    private Empresa empresa;
    private Double totalCarrinho;
    private FirebaseAuth auth;
    private DatabaseReference firebaseRef;
    private CurrencyEditText editTextCascalho;
    private String formaPagamento; // 1 = crédito 2 = débito 3 = pix
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagamento);

        inicializarComponentes();
        firebaseRef = ConfigFirebase.getFirebase();
        auth = ConfigFirebase.getFireAuth();

        //config toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Pagamento");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            pedido = (Pedido) bundle.getSerializable("pedido");
            empresa = (Empresa) bundle.getSerializable("empresa");
            totalCarrinho = bundle.getDouble("totalCarrinho");
            editTextCascalho.setValue(Double.valueOf(totalCarrinho).longValue());
        }

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

                if (checkedId == R.id.radioButtonCredito) {
                    formaPagamento = "1";
                } else if (checkedId == R.id.radioButtonDebito) {
                    formaPagamento = "2";
                } else if (checkedId == R.id.radioButtonPix) {
                    formaPagamento = "3";
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
                String cleanText1 = s.toString().replace("/", "");
                if (cleanText1.length() > 2) {
                    String formattedText = cleanText1.substring(0, 2) + "/" + cleanText1.substring(2);
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

                    pagamentoLogica();

//                    mostrarAlertaPedidoConfirmado();
                }
            }
        });
    }

    private void pagamentoLogica() {
        int corSecundaria = ContextCompat.getColor(this, R.color.secundaria);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Layout para armazenar os TextViews
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setBackgroundResource(R.drawable.bg_edit_text);
        linearLayout.setPadding(20, 20, 20, 20);

        LinearLayout linearLayoutP = new LinearLayout(this);
        linearLayoutP.setOrientation(LinearLayout.VERTICAL);
        linearLayoutP.setPadding(20, 20, 20, 20);
        linearLayoutP.addView(linearLayout, layoutParams);

        TextView textViewVazio = new TextView(this);
        TextView textViewVazi2 = new TextView(this);

        TextView massageQuantidadeTitle = new TextView(this);
        massageQuantidadeTitle.setTextSize(20);
        massageQuantidadeTitle.setPadding(20, 20, 20, 20);
        massageQuantidadeTitle.setBackgroundResource(R.drawable.bg_edit_text);
        massageQuantidadeTitle.setTextColor(Color.BLACK);
        massageQuantidadeTitle.setText("Pagamento");

        linearLayout.addView(massageQuantidadeTitle);

        linearLayout.addView(textViewVazio);

        if (radioButtonPix.isChecked()) {
            TextView textViewPix = new TextView(this);

            if (TextUtils.isEmpty(empresa.getPix())) {
                textViewPix.setText("Pix para pagamento manual: 998472547");
            } else {
                textViewPix.setText("Pix para pagamento manual: " + empresa.getPix());
            }

            textViewPix.setBackgroundColor(Color.WHITE);
            linearLayout.addView(textViewPix);
        }

        linearLayout.addView(textViewVazi2);

        ImageView imageView = new ImageView(this);

        new DownloadImageTask((ImageView) imageView).execute("https://i.pinimg.com/originals/60/c1/4a/60c14a43fb4745795b3b358868517e79.png");

        imageView.setBackgroundResource(R.drawable.bg_edit_text);
        imageView.setPadding(20, 20, 20, 20);

        linearLayout.addView(imageView);

        builder.setPositiveButton(Html.fromHtml("<font color='" + corSecundaria + "'>OK</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fakeLoading();
            }
        });

        builder.setView(linearLayoutP);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void fakeLoading() {
        dialog = new SpotsDialog.Builder().setContext(this).setMessage("Carregando Dados").setCancelable(false).build();
        dialog.show();

        final int min = 2000;
        final int max = 5000;
        final int randoom = new Random().nextInt((max - min) + 1) + min;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                mostrarAlertaPedidoConfirmado();
            }
        }, randoom);
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
        int corSecundaria = ContextCompat.getColor(this, R.color.secundaria);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Erro ao efetuar pagamento");
        builder.setMessage(mensagem);
        builder.setPositiveButton(Html.fromHtml("<font color='" + corSecundaria + "'>OK</font>"), null);
        AlertDialog alertDialog = builder.create();

        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);

        if (positiveButton != null) {
            positiveButton.setTextColor(corSecundaria);
        }

        alertDialog.show();
    }

    private void mostrarAlertaPedidoConfirmado() {
        int corSecundaria = ContextCompat.getColor(this, R.color.secundaria);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pedido Confirmado");
        builder.setMessage("Seu pedido foi confirmado com sucesso!");

        builder.setPositiveButton(Html.fromHtml("<font color='" + corSecundaria + "'>OK</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                pedido.setMetodoPagamento(0);
                pedido.setStatus("confirmado");
                pedido.confirmar();
                pedido.remover();
                pedido = null;

                // Fecha o alerta e retorna para a tela de histórico de pedidos
                Intent intent = new Intent(PagamentoActivity.this, PedidoUsuarioActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        // Cria e exibe o AlertDialog
        AlertDialog alertDialog = builder.create();

        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);

        if (positiveButton != null) {
            positiveButton.setTextColor(corSecundaria);
        }

        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_usuario_carr_pag, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(this, HomeActivity.class));
        } else if (item.getItemId() == R.id.zapzap) {
            abrirZapZap();
        } else if (item.getItemId() == R.id.menuConfig) {
            abrirConfig();
        } else if (item.getItemId() == R.id.menuSair) {
            deslogarUsuario();
        }

        return super.onOptionsItemSelected(item);
    }

    private void abrirConfig() {
        startActivity(new Intent(this, ConfigUsuarioActivity.class));
    }

    private void deslogarUsuario() {
        try {
            //desloga o usuario atual
            auth.signOut();
            startActivity(new Intent(this, AutentificacaoActivity.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void abrirZapZap() {
        String url = "https://api.whatsapp.com/send?phone=" + contact;
        try {
            PackageManager pm = this.getPackageManager();
            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(this, "Parece que você não tem o WhatsApp instalado...", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void mensagemToast(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

    private void inicializarComponentes() {

        radioGroupMetodoPagamento = findViewById(R.id.radioGroupMetodoPagamento);
        radioButtonCredito = findViewById(R.id.radioButtonCredito);
        radioButtonDebito = findViewById(R.id.radioButtonDebito);
        radioButtonPix = findViewById(R.id.radioButtonPix);
        layoutCartaoCredito = findViewById(R.id.layoutCartaoCredito);
        textNumeroCartao = findViewById(R.id.textNumeroCartao);
        textDataExpiracao = findViewById(R.id.textDataExpiracao);
        textCVV = findViewById(R.id.textCVV);
        buttonPagar = findViewById(R.id.buttonPagar);
        editTextCascalho = findViewById(R.id.editTextCascalho);
        Locale locale = new Locale("pt", "BR");
        editTextCascalho.setLocale(locale);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
