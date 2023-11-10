package com.guilhermeweber.wasteless.activity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.guilhermeweber.wasteless.R;
import com.guilhermeweber.wasteless.activity.model.Usuario;

import android.Manifest;

public class SplashActivity extends AppCompatActivity {

    private ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            // Permission is granted. Continue the action or workflow in your
            // app.
        } else {
            // Explain to the user that the feature is unavailable because the
            // feature requires a permission that the user has denied. At the
            // same time, respect the user's decision. Don't link to system
            // settings in an effort to convince the user to change their
            // decision.
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
        requestPermissionLauncher.launch(Manifest.permission.INTERNET);
        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION);
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);


        //https://www.youtube.com/watch?v=M7z2MFoI6MQ

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    System.out.println("Fetching FCM registration token failed");
                    return;
                }

                // Get new FCM registration token
                String token = task.getResult();

                // Log and toast

                System.out.println(token);
//                Toast.makeText(SplashActivity.this, " Token de registro: " + token, Toast.LENGTH_SHORT).show();

            }
        });

        FirebaseMessaging.getInstance().subscribeToTopic("Main")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed";
                        if (!task.isSuccessful()) {
                            msg = "Subscribe failed";
                        }
                        System.out.println(msg);
//                        Toast.makeText(SplashActivity.this, msg, Toast.LENGTH_SHORT).show();
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
            //dialog.show();

//            usuario = null;

            //adicionar tela de carregando aqui no futuro
            Usuario.redirectUser(SplashActivity.this);
        } else {
            Intent i = new Intent(SplashActivity.this, AutentificacaoActivity.class);
            startActivity(i);
            finish();
        }

    }
}