package com.guilhermeweber.wasteless.activity.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.guilhermeweber.wasteless.R;
import com.guilhermeweber.wasteless.activity.adapter.AdapterProduto;
import com.guilhermeweber.wasteless.activity.helper.ConfigFirebase;
import com.guilhermeweber.wasteless.activity.listener.RecyclerItemClickListener;
import com.guilhermeweber.wasteless.activity.model.Produto;
import com.guilhermeweber.wasteless.activity.model.Usuario;

import java.util.ArrayList;
import java.util.List;

public class EmpresaActivity extends AppCompatActivity {
    String contact = "+55 41 99844-2385";
    private FirebaseAuth auth;
    private DatabaseReference firebaseRef;
    private String idUsuarioLogado;
    private RecyclerView recyclerProdutos;
    private AdapterProduto adapterProduto;
    private List<Produto> produtos = new ArrayList<>();
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa);

        inicializarComponentes();

        auth = ConfigFirebase.getFireAuth();
        firebaseRef = ConfigFirebase.getFirebase();
        idUsuarioLogado = Usuario.getIdUsuario();

        //config toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Wasteless - Empresa");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //configurando o RecycerView
        RecyclerView.LayoutManager recyclerViewProdutos = new LinearLayoutManager(getApplicationContext());
        recyclerProdutos.setLayoutManager(recyclerViewProdutos);
        recyclerProdutos.setHasFixedSize(true);
        adapterProduto = new AdapterProduto(produtos, this);
        recyclerProdutos.setAdapter(adapterProduto);

        //recuperar produtos
        recuperarProdutos();

        //adiciona evento de click ai recyclerview
        recyclerProdutos.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerProdutos, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }

            @Override
            public void onLongItemClick(View view, int position) {
                removerPedido(position);
            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        }));
    }

    private void removerPedido(int position) {

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

        TextView massageQuantidadeTitle = new TextView(this);
        massageQuantidadeTitle.setTextSize(20);
        massageQuantidadeTitle.setTextColor(Color.BLACK);
        massageQuantidadeTitle.setText("Tem certeza que deseja remover esse produto?");

        linearLayout.addView(massageQuantidadeTitle);

        int corSecundaria = ContextCompat.getColor(this, R.color.secundaria);
        builder.setPositiveButton(Html.fromHtml("<font color='" + corSecundaria + "'>Remover Produto</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Produto produtoSelecionado = produtos.get(position);
                produtoSelecionado.remover(produtoSelecionado.getIdProduto());
                mensagemToast("Produto removido");

            }
        }).setNeutralButton(Html.fromHtml("<font color='" + corSecundaria + "'>Voltar</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.setView(linearLayoutP);

        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }

    private void mensagemToast(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

    private void recuperarProdutos() {

        firebaseRef = ConfigFirebase.getFirebase();
        DatabaseReference produtosRef = firebaseRef.child("produto").child(idUsuarioLogado);
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
        inflater.inflate(R.menu.menu_empresa, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            deslogarUsuario();
        } else if (item.getItemId() == R.id.menuNovoProduto) {
            abrirNovoProduto();
        } else if (item.getItemId() == R.id.menuPedidos) {
            abrirPedidos();
        } else if (item.getItemId() == R.id.zapzap) {
            abrirZapZap();
        } else if (item.getItemId() == R.id.menuConfig) {
            abrirConfig();
        } else if (item.getItemId() == R.id.menuSair) {
            deslogarUsuario();
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

    private void deslogarUsuario() {
        try {
            //desloga o usuario atual
            auth.signOut();
            startActivity(new Intent(this, AutentificacaoActivity.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void abrirConfig() {
        startActivity(new Intent(EmpresaActivity.this, ConfigEmpresaActivity.class));
    }

    private void abrirNovoProduto() {
        startActivity(new Intent(EmpresaActivity.this, NovoProdutoEmpresaActivity.class));
    }

    private void abrirPedidos() {
        startActivity(new Intent(EmpresaActivity.this, PedidoActivity.class));
    }

    private void inicializarComponentes() {
        recyclerProdutos = findViewById(R.id.recyclerEmpresas);
    }
}