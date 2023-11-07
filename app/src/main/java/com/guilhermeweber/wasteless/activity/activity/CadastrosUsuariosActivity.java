package com.guilhermeweber.wasteless.activity.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.RadioButton;

import com.guilhermeweber.wasteless.R;

public class CadastrosUsuariosActivity extends AppCompatActivity {

    //RadioButton para o tipo de cadastro que está sendo realizado
    private RadioButton cliente, empresa;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastros_usuarios);

        //RadioButton para o tipo de cadastro que está sendo realizado

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
