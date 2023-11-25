package com.guilhermeweber.wasteless.activity.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
    private RecyclerView recyclerProdutosCardapio, recyclerProdutosCardapioTeste;
    private Button buttonMaisInfo;
    private ImageButton buttonCarrinho;
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

    }

    private void abrirCarrinho() {
//        startActivity(new Intent(this, CarrinhoActivity.class));

//        Intent i = new Intent(this, CarrinhoActivity.class);
//        i.putExtra("empresaSelecionada", empresaSelecionada);
//        i.putExtra("itensCarrinho", (Serializable) itensCarrinho);
//        startActivity(i);
//        CardapioActivity.this.startActivity(new Intent(CardapioActivity.this, CardapioActivity.class).putExtra("idEmpresa", idEmpresa));
//        CardapioActivity.this.startActivity(new Intent(CardapioActivity.this, CarrinhoActivity.class));


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Carrinho ");

        // Layout para armazenar os TextViews
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout linearLayoutP = new LinearLayout(this);
        linearLayoutP.setOrientation(LinearLayout.VERTICAL);

        ScrollView scrollView = new ScrollView(this);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

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

            itensCarrinho.get(x).getNomeProduto();

            linearLayoutD.addView(alertText("Nome: ", itensCarrinho.get(x).getNomeProduto()), layoutParams);

            TextView textView = new TextView(this);
            Double preco = itensCarrinho.get(x).getPreco();
            preco = preco / 100;
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


//        int tamanhoCarrinho = itensCarrinho.get(x).getQuantidade();
//        itensCarrinho.get(x).setQuantidade(tamanhoCarrinho + Integer.parseInt(quantidade));

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
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout linearLayoutP = new LinearLayout(this);
        linearLayoutP.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

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

        // jeito feio de antes, aqui apenas pra exemplo
//        TextView textViewEmail = new TextView(this);
//        textViewEmail.setText("Email: " + empresaSelecionada.getEmail());
//        linearLayout.addView(textViewEmail);
//
//        TextView textViewEndereco = new TextView(this);
//        textViewEndereco.setText("Endereço: ");
//        linearLayout.addView(textViewEndereco);
//
//        TextView textViewCEP = new TextView(this);
//        textViewEndereco.setText("CEP: " + empresaSelecionada.getcEP());
//        linearLayout.addView(textViewCEP);

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
        builder.setMessage("Digite a Quantidade");

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

                    //jeito simples - adiciona sem ver se ja tem o item no carrinho
//                    itemPedido.setQuantidade(Integer.parseInt(quantidade));
//                    itensCarrinho.add(itemPedido);

                    if (pedidoRecuperado == null) {
                        pedidoRecuperado = new Pedido(idUsuarioLogado, idEmpresa);
                    }

                    pedidoRecuperado.setNome(usuario.getNome());
                    pedidoRecuperado.setEndereco(usuario.getEndereco());
                    pedidoRecuperado.setcEP(usuario.getcEP());
                    pedidoRecuperado.setTelefone(usuario.getTelefone());
                    pedidoRecuperado.setItens(itensCarrinho);

                    pedidoRecuperado.salvar();

                } else {
                    mensagemToast("Quantidade Invalidada de Itens!");
                }
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
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

                textCarrinhoQtd.setText("Itens no Carrinho: " + String.valueOf(qtdItensCarrinho));
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

        int needif = item.getItemId();
        if (needif == R.id.menuPedido) {
            confirmarPedido();
        } else if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void inicializarComponentes() {

        recyclerProdutosCardapio = findViewById(R.id.recyclerProdutosCardapio);
        imageEmpresaCardapio = findViewById(R.id.imageEmpresaCardapio);
        textNomeEmpresaCardapio = findViewById(R.id.textNomeEmpresaCardapio);

        buttonMaisInfo = findViewById(R.id.buttonMaisInfo);
        buttonCarrinho = findViewById(R.id.buttonCarrinho);

        textCarrinhoQtd = findViewById(R.id.textCarrinhoQtd);
        textCarrinhoTotal = findViewById(R.id.textCarrinhoTotal);
        Locale locale = new Locale("pt", "BR");
        textCarrinhoTotal.setLocale(locale);

    }

    private void confirmarPedidoNovo(){
        Intent intent = new Intent(CardapioActivity.this, PagamentoActivity.class);
        intent.putExtra("pedido", pedidoRecuperado);
        startActivity(intent);
    }

    private void confirmarPedido() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecione um método de pagamento");

         CharSequence[] itens = new CharSequence[]{"Dinheiro", "PIX", "Máquina de Cartão"};
         builder.setSingleChoiceItems(itens, 0, new DialogInterface.OnClickListener() {
        @Override
        public void onClick (DialogInterface dialog,int which){
            metodoPagamento = which;
        }
    });

    EditText editObservacao = new EditText(this);
    editObservacao.setHint("Digite uma observação (opcional)");
    builder.setView(editObservacao);

    builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialog, int which) {

        String observacao = editObservacao.getText().toString();
        pedidoRecuperado.setMetodoPagamento(metodoPagamento);
        pedidoRecuperado.setObservacao(observacao);
        pedidoRecuperado.setStatus("confirmado");
        pedidoRecuperado.confirmar();
        pedidoRecuperado.remover();
        pedidoRecuperado = null;

    }
});
        builder.setNegativeButton("Cancelar",new DialogInterface.OnClickListener(){
        @Override
        public void onClick(DialogInterface dialog,int which){

        }
        });

        AlertDialog dialog=builder.create();
        dialog.show();

        }

        private void mensagemToast(String texto){
            Toast.makeText(this,texto,Toast.LENGTH_SHORT).show();
    }
}