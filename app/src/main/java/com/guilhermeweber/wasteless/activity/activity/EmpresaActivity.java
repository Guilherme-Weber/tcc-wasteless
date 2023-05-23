package com.guilhermeweber.wasteless.activity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.guilhermeweber.wasteless.R;
import com.guilhermeweber.wasteless.activity.helper.ConfigFirebase;

public class EmpresaActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa);

        auth = ConfigFirebase.getFireAuth();

        //config toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Wasteless - Empresa");
        setSupportActionBar(toolbar);

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

}