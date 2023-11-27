package com.guilhermeweber.wasteless.activity.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.guilhermeweber.wasteless.R;
import com.guilhermeweber.wasteless.activity.adapter.AdapterProduto;
import com.guilhermeweber.wasteless.activity.helper.ConfigFirebase;
import com.guilhermeweber.wasteless.activity.listener.RecyclerItemClickListener;
import com.guilhermeweber.wasteless.activity.model.Empresa;
import com.guilhermeweber.wasteless.activity.model.ItemPedido;
import com.guilhermeweber.wasteless.activity.model.Pedido;
import com.guilhermeweber.wasteless.activity.model.Produto;
import com.guilhermeweber.wasteless.activity.model.Usuario;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;

public class CardapioActivity extends AppCompatActivity {
    String contact = "+55 41 99844-2385";
    private RecyclerView recyclerProdutosCardapio, recyclerProdutosCardapioTeste;
    private Button buttonMaisInfo;
    private ImageButton buttonCarrinho, imageButtonFavoritar;
    private FirebaseAuth auth;
    private DatabaseReference firebaseRef;
    private FirebaseDatabase firebaseDatabase;
    private ImageView imageEmpresaCardapio;
    private TextView textNomeEmpresaCardapio, textCarrinhoQtd;
    private CurrencyEditText textCarrinhoTotal;
    private Empresa empresaSelecionada;
    private Empresa empresa;
    private Usuario usuario;
    private Pedido pedidoRecuperado;
    private AdapterProduto adapterProduto;
    private List<Produto> produtos = new ArrayList<>();
    private List<ItemPedido> itensCarrinho = new ArrayList<>();
    private String idEmpresa, idUsuarioLogado;
    private AlertDialog dialog;
    private int qtdItensCarrinho, metodoPagamento;
    private Double totalCarrinho;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardapio);

        inicializarComponentes();
        auth = ConfigFirebase.getFireAuth();
        firebaseRef = ConfigFirebase.getFirebase();
        idUsuarioLogado = Usuario.getIdUsuario();

        //recuperar a empresa selecionada
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            empresaSelecionada = (Empresa) bundle.getSerializable("empresa");

            textNomeEmpresaCardapio.setText(empresaSelecionada.getNome());
            idEmpresa = empresaSelecionada.getIdEmpresaUsuario();
            String url = empresaSelecionada.getUrlImagem();
            Picasso.get().load(url).into(imageEmpresaCardapio);
        }

        //config toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Cardápio");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //recyclerview
        RecyclerView.LayoutManager recyclerViewProdutos = new LinearLayoutManager(getApplicationContext());
        recyclerProdutosCardapio.setLayoutManager(recyclerViewProdutos);
        recyclerProdutosCardapio.setHasFixedSize(true);
        adapterProduto = new AdapterProduto(produtos, this);
        recyclerProdutosCardapio.setAdapter(adapterProduto);

        //recuperar produtos
        recuperarProdutos();
        recuperarDadosUsuario();

        //config evento click
        recyclerProdutosCardapio.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerProdutosCardapio, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                confirmarQuantidade(position);
            }

            @Override
            public void onLongItemClick(View view, int position) {
            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        }));

        buttonMaisInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maisInfo();
            }
        });

        buttonCarrinho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirCarrinho();
            }
        });

        imageButtonFavoritar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = usuario.manageEmpresaFavorita(empresaSelecionada, CardapioActivity.this);

                if (result.equals("1"))
                    imageButtonFavoritar.setImageResource(R.drawable.ic_favorito);
                else {
                    imageButtonFavoritar.setImageResource(R.drawable.ic_favorito_red);
                }
            }
        });
    }

    private void abrirCarrinho() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Carrinho ");

        // Layout para armazenar os TextViews
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout linearLayoutP = new LinearLayout(this);
        linearLayoutP.setOrientation(LinearLayout.VERTICAL);
        linearLayoutP.setPadding(20, 20, 20, 20);
        linearLayoutP.addView(linearLayout, layoutParams);
        TextView textViewVazio = new TextView(this);
        linearLayout.setBackgroundResource(R.drawable.bg_edit_text);
        linearLayout.setPadding(20, 20, 20, 20);

        for (int x = 0; x < itensCarrinho.size(); x++) {

            LinearLayout linearLayoutD = new LinearLayout(this);
            linearLayoutD.setOrientation(LinearLayout.VERTICAL);
            linearLayoutD.setBackgroundResource(R.drawable.bg_edit_text);
            linearLayoutD.setPadding(20, 20, 20, 20);

            TextView textView = new TextView(this);
            Double preco = itensCarrinho.get(x).getPreco();
            preco = preco / 100;

            linearLayoutD.addView(alertText("Nome: ", itensCarrinho.get(x).getNomeProduto()), layoutParams);
            linearLayoutD.addView(alertText("Preço: R$", String.valueOf(preco)));
            linearLayoutD.addView(alertText("Quantidade: ", String.valueOf(itensCarrinho.get(x).getQuantidade())), layoutParams);
            linearLayout.addView(linearLayoutD);
        }

        LinearLayout linearLayoutD = new LinearLayout(this);
        linearLayoutD.setOrientation(LinearLayout.VERTICAL);
        linearLayoutD.setBackgroundResource(R.drawable.bg_edit_text);
        linearLayoutD.setPadding(20, 20, 20, 20);

        String total;

        total = String.valueOf(textCarrinhoTotal.getText());

        linearLayoutD.addView(alertText("Total: ", total));

        linearLayout.addView(linearLayoutD);

        builder.setPositiveButton("Finalizar Pedido", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //confirmarPedido();
                confirmarPedidoNovo();
            }
        }).setNeutralButton("Voltar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setNegativeButton("Limpar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pedidoRecuperado.remover();
            }
        });

        builder.setView(linearLayoutP);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void maisInfo() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Informações Sobre " + empresaSelecionada.getNome());

        // Layout para armazenar os TextViews
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout linearLayoutP = new LinearLayout(this);
        linearLayoutP.setOrientation(LinearLayout.VERTICAL);
        linearLayoutP.setPadding(20, 20, 20, 20);
        linearLayoutP.addView(linearLayout, layoutParams);

        TextView textViewVazio = new TextView(this);

        linearLayout.setBackgroundResource(R.drawable.bg_edit_text);
        linearLayout.setPadding(20, 20, 20, 20);

        linearLayout.addView(alertText("Email: ", empresaSelecionada.getEmail()), layoutParams);
        linearLayout.addView(alertText("Endereço: ", ""), layoutParams);
        linearLayout.addView(alertText("CEP: ", empresaSelecionada.getcEP()), layoutParams);
        linearLayout.addView(alertText("Cidade: ", empresaSelecionada.getLocalidade()), layoutParams);
        linearLayout.addView(alertText("Estado: ", empresaSelecionada.getUF()), layoutParams);
        linearLayout.addView(alertText("Bairro: ", empresaSelecionada.getBairro()), layoutParams);
        linearLayout.addView(alertText("Logradouro: ", empresaSelecionada.getLogradouro()), layoutParams);
        linearLayout.addView(alertText("Complemento(opcional): ", empresaSelecionada.getComplemento()), layoutParams);

        builder.setView(linearLayoutP);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private TextView alertText(String texto, String info) {

        TextView textView = new TextView(this);
        textView.setText(texto + info);

        textView.setPadding(20, 20, 20, 20);
        return textView;
    }

    private void confirmarQuantidade(int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quantidade");
        builder.setMessage("Informe a Quantidade");

        EditText editQuantidade = new EditText(this);
        editQuantidade.setText("1");

        builder.setView(editQuantidade);

        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String quantidade = editQuantidade.getText().toString();

                Produto produtoSelecionado = produtos.get(position);
                ItemPedido itemPedido = new ItemPedido();
                itemPedido.setIdProduto(produtoSelecionado.getIdProduto());
                itemPedido.setNomeProduto(produtoSelecionado.getNomeProduto());
                itemPedido.setPreco(Double.parseDouble(String.valueOf(produtoSelecionado.getPreco())));

                int teste = 0;

                if (!quantidade.isEmpty() && !quantidade.equals("0")) {

                    if (itensCarrinho.size() != 0) {
                        for (int x = 0; x < itensCarrinho.size(); x++) {
                            if (itensCarrinho.get(x).getIdProduto().equals(itemPedido.getIdProduto())) {
                                int tamanhoCarrinho = itensCarrinho.get(x).getQuantidade();
                                itensCarrinho.get(x).setQuantidade(tamanhoCarrinho + Integer.parseInt(quantidade));
                                teste = 1;
                            }
                        }
                        if (teste != 1) {
                            itemPedido.setQuantidade(Integer.parseInt(quantidade));
                            itensCarrinho.add(itemPedido);
                        }
                    } else {
                        itemPedido.setQuantidade(Integer.parseInt(quantidade));
                        itensCarrinho.add(itemPedido);
                    }

                    if (pedidoRecuperado == null) {
                        pedidoRecuperado = new Pedido(idUsuarioLogado, idEmpresa);
                    }

                    pedidoRecuperado.setNome(usuario.getNome());
                    pedidoRecuperado.setEmail(usuario.getEmail());
                    pedidoRecuperado.setTelefone(usuario.getTelefone());
                    pedidoRecuperado.setItens(itensCarrinho);

                    pedidoRecuperado.salvar();

                } else {
                    mensagemToast("Quantidade inválida de itens!");
                }
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void recuperarDadosUsuario() {

        dialog = new SpotsDialog.Builder().setContext(this).setMessage("Carregando Dados").setCancelable(false).build();
        dialog.show();

        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idUsuarioLogado);

        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    usuario = snapshot.getValue(Usuario.class);
                }
                recuperarPedido();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void recuperarPedido() {

        DatabaseReference pedidoRef = firebaseRef.child("pedido_usuario").child(idEmpresa).child(idUsuarioLogado);
        pedidoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                qtdItensCarrinho = 0;
                totalCarrinho = 0.0;
                itensCarrinho = new ArrayList<>();

                if (snapshot.getValue() != null) {

                    pedidoRecuperado = snapshot.getValue(Pedido.class);
                    itensCarrinho = pedidoRecuperado.getItens();
                    for (ItemPedido itemPedido : itensCarrinho) {
                        int qtde = itemPedido.getQuantidade();
                        Double preco = itemPedido.getPreco();

                        totalCarrinho += (qtde * preco);
                        qtdItensCarrinho += qtde;
                    }
                }

                DecimalFormat df = new DecimalFormat("0.00");

                textCarrinhoQtd.setText("Quantidade: " + String.valueOf(qtdItensCarrinho));
                textCarrinhoTotal.setValue(Double.valueOf(totalCarrinho).longValue());

                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void recuperarProdutos() {

        firebaseRef = ConfigFirebase.getFirebase();
        DatabaseReference produtosRef = firebaseRef.child("produto").child(idEmpresa);
        produtosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                produtos.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    produtos.add(ds.getValue(Produto.class));
                }
                adapterProduto.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_cardapio, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.zapzap) {
            abrirZapZap();
        }

        return super.onOptionsItemSelected(item);
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

    private void confirmarPedidoNovo() {
        Intent intent = new Intent(CardapioActivity.this, PagamentoActivity.class);
        intent.putExtra("pedido", pedidoRecuperado);
        startActivity(intent);
    }

    private void mensagemToast(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

    private void inicializarComponentes() {

        recyclerProdutosCardapio = findViewById(R.id.recyclerProdutosCardapio);
        imageEmpresaCardapio = findViewById(R.id.imageEmpresaCardapio);
        textNomeEmpresaCardapio = findViewById(R.id.textNomeEmpresaCardapio);

        buttonMaisInfo = findViewById(R.id.buttonMaisInfo);
        buttonCarrinho = findViewById(R.id.buttonCarrinho);
        imageButtonFavoritar = findViewById(R.id.imageButtonFavoritar);

        textCarrinhoQtd = findViewById(R.id.textCarrinhoQtd);
        textCarrinhoTotal = findViewById(R.id.textCarrinhoTotal);
        Locale locale = new Locale("pt", "BR");
        textCarrinhoTotal.setLocale(locale);

    }

//    private void confirmarPedido() {
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Selecione um método de pagamento");
//
//        CharSequence[] itens = new CharSequence[]{"Dinheiro", "PIX", "Máquina de Cartão"};
//        builder.setSingleChoiceItems(itens, 0, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                metodoPagamento = which;
//            }
//        });
//
//        EditText editObservacao = new EditText(this);
//        editObservacao.setHint("Digite uma observação (opcional)");
//        builder.setView(editObservacao);
//
//        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                String observacao = editObservacao.getText().toString();
//                pedidoRecuperado.setMetodoPagamento(metodoPagamento);
//                pedidoRecuperado.setObservacao(observacao);
//                pedidoRecuperado.setStatus("confirmado");
//                pedidoRecuperado.confirmar();
//                pedidoRecuperado.remover();
//                pedidoRecuperado = null;
//
//            }
//        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });
//
//        AlertDialog dialog = builder.create();
//        dialog.show();
//
//    }

}