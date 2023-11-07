package com.guilhermeweber.wasteless.activity.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.RadioButton;

import com.guilhermeweber.wasteless.R;

import org.w3c.dom.Text;

public class CadastrosUsuariosActivity extends AppCompatActivity {

    //Instanciando RadioButton e textos para o tipo de cadastro que está sendo realizado
    private Text nome, email, senha;
    private RadioButton cliente, empresa;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastros_usuarios);

        nome = findViewById(R.id.TextNomeCadastro);
        email = findViewById(R.id.TextEmailAddress);
        senha = findViewById(R.id.TextSenhaCadastro);
        cliente = findViewById(R.id.radioButtonClienteCadastro);
        empresa = findViewById(R.id.radioButtonEmpresaCadastro);

    }

    //Metódo que chama o radiobutton do tipo de cadastro
    public void radiobutton(){
        if(cliente.isChecked()) {
        }else if(cliente.isChecked()) {
        }
        }
    }
