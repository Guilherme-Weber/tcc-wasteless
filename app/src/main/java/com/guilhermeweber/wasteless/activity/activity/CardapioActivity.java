package com.guilhermeweber.wasteless.activity.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;

public class CardapioActivity extends AppCompatActivity {

    private RecyclerView recyclerProdutosCardapio;
    private FirebaseAuth auth;
    private DatabaseReference firebaseRef;
    private FirebaseDatabase firebaseDatabase;
    private ImageView imageEmpresaCardapio;
    private TextView textNomeEmpresaCardapio, textCarrinhoQtd;
    private CurrencyEditText textCarrinhoTotal;
    private Empresa empresaSelecionada;
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

            textNomeEmpresaCardapio.setText(usuario.getNome());

            idEmpresa = empresaSelecionada.getIdEmpresaUsuario();

            String url = usuario.getUrlImagem();
            Picasso.get().load(url).into(imageEmpresaCardapio);
        }

        //config toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Cardápio");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

//        ImageView carrinho = findViewById(R.id.carrinho);
//        carrinho.setVisibility(View.VISIBLE);

        //recyclerview
        RecyclerView.LayoutManager recyclerViewProdutos = new LinearLayoutManager(getApplicationContext());
        recyclerProdutosCardapio.setLayoutManager(recyclerViewProdutos);
        recyclerProdutosCardapio.setHasFixedSize(true);
        adapterProduto = new AdapterProduto(produtos, this);
        recyclerProdutosCardapio.setAdapter(adapterProduto);

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

        //recuperar produtos
        recuperarProdutos();

        recuperarDadosUsuario();

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

                if (!quantidade.isEmpty() && !quantidade.equals("0")) {

                    //futuramente seria legal adicionar uma logica onde ele percorre os itens carrinho e verifica se ja n tem o mesmo, caso tenha almentando a quantidade em vez de duplicando
//                    if (itensCarrinho.size() != 0) {
//                        for (int x = 0; x < itensCarrinho.size(); x++) {
//                            if (itensCarrinho.get(x).getIdProduto().equals(itemPedido.getIdProduto())) {
//                                itemPedido.setQuantidade(Integer.parseInt(quantidade));
//                            }
//                        }
//                    } else {
//                        itemPedido.setQuantidade(Integer.parseInt(quantidade));
//                        itensCarrinho.add(itemPedido);
//                    }

                    //jeito simples
                    itemPedido.setQuantidade(Integer.parseInt(quantidade));
                    itensCarrinho.add(itemPedido);

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

    private void mensagemToast(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
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

                textCarrinhoQtd.setText("qtd: " + String.valueOf(qtdItensCarrinho));
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
        DatabaseReference produtosRef = firebaseRef.child("produto");
        produtosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                produtos.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.getValue(Produto.class).getIdEmpresa() == idEmpresa) {
                        produtos.add(ds.getValue(Produto.class));
                    }
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

    private void confirmarPedido() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecione um método de pagamento");

        CharSequence[] itens = new CharSequence[]{"Dinheiro", "PIX", "Máquina de Cartão"};
        builder.setSingleChoiceItems(itens, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void inicializarComponentes() {
        recyclerProdutosCardapio = findViewById(R.id.recyclerProdutosCardapio);
        imageEmpresaCardapio = findViewById(R.id.imageEmpresaCardapio);
        textNomeEmpresaCardapio = findViewById(R.id.textNomeEmpresaCardapio);

        textCarrinhoQtd = findViewById(R.id.textCarrinhoQtd);
        textCarrinhoTotal = findViewById(R.id.textCarrinhoTotal);
        Locale locale = new Locale("pt", "BR");
        textCarrinhoTotal.setLocale(locale);
    }
}