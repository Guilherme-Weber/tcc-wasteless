package com.guilhermeweber.wasteless.activity.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.guilhermeweber.wasteless.R;
import com.guilhermeweber.wasteless.activity.adapter.AdapterPedido;
import com.guilhermeweber.wasteless.activity.helper.ConfigFirebase;
import com.guilhermeweber.wasteless.activity.listener.RecyclerItemClickListener;
import com.guilhermeweber.wasteless.activity.model.Pedido;
import com.guilhermeweber.wasteless.activity.model.Usuario;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class PedidoActivity extends AppCompatActivity {
    String contact = "+55 41 99844-2385";
    private FirebaseAuth auth;
    private RecyclerView recyclerPedidos;
    private AdapterPedido adapterPedido;
    private List<Pedido> pedidos = new ArrayList<>();
    private AlertDialog dialog;
    private DatabaseReference firebaseRef;
    private String idEmpresa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido);

        //iniciar componentes
        inicializarComponentes();

        auth = ConfigFirebase.getFireAuth();
        firebaseRef = ConfigFirebase.getFirebase();
        idEmpresa = Usuario.getIdUsuario();

        //config toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Pedidos");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //recyclerview
        RecyclerView.LayoutManager recyclerViewProdutos = new LinearLayoutManager(getApplicationContext());
        recyclerPedidos.setLayoutManager(recyclerViewProdutos);
        recyclerPedidos.setHasFixedSize(true);
        adapterPedido = new AdapterPedido(pedidos);
        recyclerPedidos.setAdapter(adapterPedido);

        recuperarPedidos();

        //click no recyclerview
        recyclerPedidos.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerPedidos, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }

            @Override
            public void onLongItemClick(View view, int position) {
                finalizar(position);
            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        }));
    }

    private void finalizar(int position) {

        int corSecundaria = ContextCompat.getColor(this, R.color.secundaria);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Deseja finalizar esse pedido?");
        builder.setPositiveButton(Html.fromHtml("<font color='" + corSecundaria + "'>Finalizar Pedido</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Pedido pedido = pedidos.get(position);
                pedido.setStatus("finalizado");
                pedido.atualizarStatus();
            }
        }).setNeutralButton(Html.fromHtml("<font color='" + corSecundaria + "'>Voltar</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();

        Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button neutralButton = dialog.getButton(DialogInterface.BUTTON_NEUTRAL);

        if (positiveButton != null) {
            positiveButton.setTextColor(corSecundaria);
        }

        if (neutralButton != null) {
            neutralButton.setTextColor(corSecundaria);
        }

        dialog.show();
    }

    private void recuperarPedidos() {

        dialog = new SpotsDialog.Builder().setContext(this).setMessage("Carregando Dados").setCancelable(false).build();
        dialog.show();

        firebaseRef = ConfigFirebase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef.child("pedido").child(idEmpresa);

        Query pedidoPesquisa = pedidoRef.orderByChild("status").equalTo("confirmado");

        pedidoPesquisa.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pedidos.clear();
                if (snapshot != null) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Pedido pedido = ds.getValue(Pedido.class);
                        pedidos.add(pedido);
                    }
                    adapterPedido.notifyDataSetChanged();
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_empresa_pedi, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.menuSair) {
            deslogarUsuario();
        } else if (item.getItemId() == R.id.menuConfig) {
            abrirConfig();
        } else if (item.getItemId() == R.id.zapzap) {
            abrirZapZap();
        } else if (item.getItemId() == android.R.id.home) {
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
        startActivity(new Intent(PedidoActivity.this, ConfigEmpresaActivity.class));
    }

    private void abrirNovoProduto() {
        startActivity(new Intent(PedidoActivity.this, NovoProdutoEmpresaActivity.class));
    }

    private void inicializarComponentes() {
        recyclerPedidos = findViewById(R.id.recyclerPedidos);
    }
}