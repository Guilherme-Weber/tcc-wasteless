package com.guilhermeweber.wasteless.activity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.guilhermeweber.wasteless.R;
import com.guilhermeweber.wasteless.activity.model.Usuario;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                abrirAutenticacao();
            }
        }, 3000);
    }

    private void abrirAutenticacao() {

        Usuario usuario = new Usuario();

        if (usuario.getUsuarioAtual() != null) {
            //dialog.show();

            //adicionar tela de carregando aqui no futuro
            Usuario.redirectUser(SplashActivity.this);
        } else {
            Intent i = new Intent(SplashActivity.this, AutentificacaoActivity.class);
            startActivity(i);
            finish();
        }

    }
}