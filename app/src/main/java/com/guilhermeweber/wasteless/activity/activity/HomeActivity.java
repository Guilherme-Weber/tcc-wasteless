package com.guilhermeweber.wasteless.activity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.guilhermeweber.wasteless.R;
import com.guilhermeweber.wasteless.activity.adapter.AdapterEmpresa;
import com.guilhermeweber.wasteless.activity.helper.ConfigFirebase;
import com.guilhermeweber.wasteless.activity.listener.RecyclerItemClickListener;
import com.guilhermeweber.wasteless.activity.model.Empresa;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    ImageButton btnHome, btnPedido;
    private FirebaseAuth auth;
    private MaterialSearchView searchView, searchViewTudo;
    private RecyclerView recyclerEmpresa;
    private List<Empresa> empresas = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private AdapterEmpresa adapterEmpresa;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //config toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Wasteless - Home");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        inicializarComponentes();
        firebaseRef = ConfigFirebase.getFirebase();
        auth = ConfigFirebase.getFireAuth();

        /*
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
        btnPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, HistoricoPedidosActivity.class);
                startActivity(intent);
            }
        });

         */

        //configurando o RecycerView
        RecyclerView.LayoutManager recyclerViewEmpresa = new LinearLayoutManager(getApplicationContext());
        recyclerEmpresa.setLayoutManager(recyclerViewEmpresa);
        recyclerEmpresa.setHasFixedSize(true);
        adapterEmpresa = new AdapterEmpresa(empresas, this);
        recyclerEmpresa.setAdapter(adapterEmpresa);

        //recuperar produtos
        recuperarProdutos();

        //config search view
        searchView.setHint("Pesquisar Estabelecimentos");
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(@NonNull String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(@NonNull String newText) {
                pesquisarEmpresas(newText);
                return true;
            }
        });

        searchViewTudo.setHint("Pesquisar Estabelecimentos Pelo Bairro");
        searchViewTudo.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(@NonNull String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(@NonNull String newText) {
                pesquisarTudo(newText);
                return true;
            }
        });


        //evento de click
        recyclerEmpresa.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerEmpresa, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Empresa empresaSelecionada = empresas.get(position);
                Intent i = new Intent(HomeActivity.this, CardapioActivity.class);
                i.putExtra("empresa", empresaSelecionada);
                startActivity(i);

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));
    }

    public void onBackPressed() {

        super.onBackPressed();
        return;
    }


    private void mensagemToast(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

    private void pesquisarTudo(String pesquisa) {
        firebaseRef = ConfigFirebase.getFirebase();
        DatabaseReference empresaRef = firebaseRef.child("empresa");
        Query query = empresaRef.orderByChild("bairro").startAt(pesquisa).endAt(pesquisa + "\uf8ff");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                empresas.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    empresas.add(ds.getValue(Empresa.class));
                }
                adapterEmpresa.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void pesquisarEmpresas(String pesquisa) {
        firebaseRef = ConfigFirebase.getFirebase();
        DatabaseReference empresaRef = firebaseRef.child("empresa");
        Query query = empresaRef.orderByChild("nome").startAt(pesquisa).endAt(pesquisa + "\uf8ff");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                empresas.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    empresas.add(ds.getValue(Empresa.class));
                }
                adapterEmpresa.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void recuperarProdutos() {
        firebaseRef = ConfigFirebase.getFirebase();
        DatabaseReference empresaRef = firebaseRef.child("empresa");
        empresaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                empresas.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    empresas.add(ds.getValue(Empresa.class));
                }
                adapterEmpresa.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_usuario, menu);

        MenuItem item = menu.findItem(R.id.menuPesquisa);
        searchView.setMenuItem(item);

        MenuItem item2 = menu.findItem(R.id.menuPesquisaTudo);
        searchViewTudo.setMenuItem(item2);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        Toolbar toolbar = findViewById(R.id.toolbar);

        if (item.getItemId() == R.id.menuSair) {
            deslogarUsuario();
        } else if (item.getItemId() == R.id.menuConfig) {
            abrirConfig();
        } else if (item.getItemId() == R.id.pedidos) {
            abrirPedidos();
        } else if (item.getItemId() == android.R.id.home) {
            deslogarUsuario();
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
        }
        */
        return super.onOptionsItemSelected(item);
    }

    private void abrirPedidos() {
        startActivity(new Intent(HomeActivity.this, PedidoUsuarioActivity.class));
    }

    public void inicializarComponentes() {
        searchView = findViewById(R.id.materialSearchViewIdTudo);
        searchViewTudo = findViewById(R.id.materialSearchViewIdTudo);
        recyclerEmpresa = findViewById(R.id.recyclerEmpresa);

    }

    private void abrirConfig() {
        startActivity(new Intent(HomeActivity.this, ConfigUsuarioActivity.class));
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
}