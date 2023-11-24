package com.guilhermeweber.wasteless.activity.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.guilhermeweber.wasteless.R;
import com.guilhermeweber.wasteless.activity.adapter.AdapterCarrinho;
import com.guilhermeweber.wasteless.activity.adapter.AdapterProduto;
import com.guilhermeweber.wasteless.activity.helper.ConfigFirebase;
import com.guilhermeweber.wasteless.activity.model.Empresa;
import com.guilhermeweber.wasteless.activity.model.ItemPedido;
import com.guilhermeweber.wasteless.activity.model.Pedido;
import com.guilhermeweber.wasteless.activity.model.Produto;
import com.guilhermeweber.wasteless.activity.model.Usuario;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CarrinhoActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private DatabaseReference firebaseRef;
    private FirebaseDatabase firebaseDatabase;
    private AdapterCarrinho adapterCarrinho;
    private Empresa empresaSelecionada;
    private String idEmpresa, idUsuarioLogado;
    private RecyclerView recyclerProdutosCarrinho;
    private List<ItemPedido> itemPedidos = new ArrayList<>();
    private List<Pedido> pedidos = new ArrayList<>();
    private RecyclerView recyclerProdutosCardapio;
    private Button buttonMaisInfo, buttonCarrinho;
    private ImageView imageEmpresaCardapio;
    private TextView textNomeEmpresaCardapio, textCarrinhoQtd;
    private CurrencyEditText textCarrinhoTotal;
    private Empresa empresa;
    private Usuario usuario;
    private Pedido pedidoRecuperado;
    private AdapterProduto adapterProduto;
    private List<Produto> produtos = new ArrayList<>();
    private List<ItemPedido> itensCarrinho = new ArrayList<>();
    private AlertDialog dialog;
    private int qtdItensCarrinho, metodoPagamento;
    private Double totalCarrinho;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrinho);

        auth = ConfigFirebase.getFireAuth();
        firebaseRef = ConfigFirebase.getFirebase();
        idUsuarioLogado = Usuario.getIdUsuario();

        inicializarComponentes();

        //config toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Carrinho");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Configuração RecyclerView
        RecyclerView.LayoutManager recyclerViewCarrinho = new LinearLayoutManager(getApplicationContext());
        recyclerProdutosCarrinho.setLayoutManager(recyclerViewCarrinho);
        recyclerProdutosCarrinho.setHasFixedSize(true);
        adapterCarrinho = new AdapterCarrinho(pedidos);
        recyclerProdutosCarrinho.setAdapter(adapterCarrinho);

//      recuperar a empresa selecionada
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            empresaSelecionada = (Empresa) bundle.getSerializable("empresaSelecionada");
            itensCarrinho = (List<ItemPedido>) bundle.get("itensCarrinho");

            idEmpresa = empresaSelecionada.getIdEmpresaUsuario();
        }

//        recuperarCarrinho();
    }

    private void mensagemToast(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

    private void inicializarComponentes() {

        recyclerProdutosCarrinho = findViewById(R.id.recyclerProdutosCarrinho);

    }

    private void recuperarCarrinho() {


    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int needif = item.getItemId();
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}