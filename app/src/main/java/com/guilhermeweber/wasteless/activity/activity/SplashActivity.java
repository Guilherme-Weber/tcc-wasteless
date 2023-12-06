package com.guilhermeweber.wasteless.activity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.guilhermeweber.wasteless.R;
import com.guilhermeweber.wasteless.activity.model.Usuario;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    System.out.println("Fetching FCM registration token failed");
                    return;
                }

                // Get new FCM registration token
                String token = task.getResult();

                System.out.println(token);
            }
        });

        FirebaseMessaging.getInstance().subscribeToTopic("Main").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                String msg = "Subscribed";
                if (!task.isSuccessful()) {
                    msg = "Subscribe failed";
                }
                System.out.println(msg);
            }
        });

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
            Usuario.redirectUser(SplashActivity.this);
        } else {
            Intent i = new Intent(SplashActivity.this, AutentificacaoActivity.class);
            startActivity(i);
            finish();
        }
    }
}