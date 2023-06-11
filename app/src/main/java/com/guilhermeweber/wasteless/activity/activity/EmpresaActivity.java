package com.guilhermeweber.wasteless.activity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.guilhermeweber.wasteless.activity.model.Produto;
import com.guilhermeweber.wasteless.activity.model.Usuario;

import java.util.ArrayList;
import java.util.List;

public class EmpresaActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference firebaseRef;
    private FirebaseDatabase firebaseDatabase;
    private String idUsuarioLogado;
    private RecyclerView recyclerProdutos;
    private AdapterProduto adapterProduto;
    private List<Produto> produtos = new ArrayList<>();

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
                Produto produtoSelecionado = produtos.get(position);
                produtoSelecionado.remover(produtoSelecionado.getIdProduto());
                mensagemToast("Produto Excluido com Sucesso!");
            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));

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


//        Produto produtoObj = new Produto();
//
//        firebaseDatabase = FirebaseDatabase.getInstance();
////        DatabaseReference produtosRef = firebaseRef.getDatabase().getReference().child("produto").child(idUsuarioLogado);
//        firebaseDatabase.getReference("produto").child(idUsuarioLogado).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                produtos.clear();
//                for (DataSnapshot ds : snapshot.getChildren()) {
//                    produtos.add(ds.getValue(Produto.class));
//
//                }
//                adapterProduto.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_empresa, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int needif = item.getItemId();

        if (needif == R.id.menuSair) {
            deslogarUsuario();
        } else if (needif == R.id.menuConfig) {
            abrirConfig();
        } else if (needif == R.id.menuNovoProduto) {
            abrirNovoProduto();
        }

        //o jeito certo era fazer com um switch case, porem nas verções mais nova do Android Gradle Plugin (acima de 7.4.2) ele da um erro que só consegui resolver com if else
        //em vez de rodar numa verção mais antiga manterei esse workaround
        /*
        switch (item.getItemId()) {
            case R.id.menuSair:
                deslogarUsuario();
                break;

            case R.id.menuConfig:
                abrirConfig();
                break;

            case R.id.menuNovoProduto:
                abrirNovoProduto();
                break;
        }
        */

        return super.onOptionsItemSelected(item);
    }

    private void deslogarUsuario() {
        try {
            //desloga o usuario atual
            auth.signOut();
            finish();
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

    private void inicializarComponentes() {
        recyclerProdutos = findViewById(R.id.recyclerEmpresas);
    }

}