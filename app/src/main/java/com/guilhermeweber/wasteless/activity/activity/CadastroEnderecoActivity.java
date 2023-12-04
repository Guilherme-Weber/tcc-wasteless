package com.guilhermeweber.wasteless.activity.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.guilhermeweber.wasteless.R;
import com.guilhermeweber.wasteless.activity.helper.Mascara;
import com.guilhermeweber.wasteless.activity.helper.Permissoes;
import com.guilhermeweber.wasteless.activity.helper.RESTService;
import com.guilhermeweber.wasteless.activity.model.CEP;
import com.guilhermeweber.wasteless.activity.model.Empresa;
import com.guilhermeweber.wasteless.activity.model.Usuario;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CadastroEnderecoActivity extends AppCompatActivity {
    private final String URL = "https://viacep.com.br/ws/";
    Usuario usuario = new Usuario();
    Empresa empresa = new Empresa();
    private Retrofit retrofitCEP;
    private Button btnConsultarCEP, buttonCadastroEndereco;
    private EditText txtCEP, txtLogradouro, txtComplemento, txtBairro, txtUF, txtLocalidade, textCadastroChavePix;
    private TextInputLayout layCEP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_endereco);

        inicializarComponentes();

        //recuperar a usuario
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            usuario = (Usuario) bundle.getSerializable("usuario");
        }

        String tipoUser = usuario.getTipo();

        if (tipoUser.equals("U")) {
            cadastroUser();
        }

        //Aplicando a máscara para CEP
        txtCEP.addTextChangedListener(Mascara.insert(Mascara.MASCARA_CEP, txtCEP));

        //configura os recursos do retrofit
        retrofitCEP = new Retrofit.Builder().baseUrl(URL)                                       //endereço do webservice
                .addConverterFactory(GsonConverterFactory.create()) //conversor
                .build();

        btnConsultarCEP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validarCampos()) {
                    String cep = txtCEP.getText().toString().trim();
                    empresa.setcEP(cep);

                    consultarCEP();
                }
            }
        });

        buttonCadastroEndereco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String cepUf = txtUF.getText().toString().trim();

                if (!cepUf.isEmpty()) {
                    cadastroUser();
                } else {
                    txtCEP.setError("Verifique o CEP primeiro!");
                }
            }
        });
    }

    private Boolean validarCampos() {

        Boolean status = true;
        String cep = txtCEP.getText().toString().trim();

        if (cep.isEmpty()) {
            txtCEP.setError("Informe um CEP válido.");
            status = false;
        }

        if ((cep.length() > 1) && (cep.length() < 10)) {
            txtCEP.setError("O CEP deve possuir 8 dígitos");
            status = false;
        }
        return status;
    }

    private void cadastroUser() {

        String Logradouro = txtLogradouro.getText().toString();
        String Complemento = txtComplemento.getText().toString();
        String Bairro = txtBairro.getText().toString();
        String UF = txtUF.getText().toString();
        String Localidade = txtLocalidade.getText().toString();
        String pix = textCadastroChavePix.getText().toString();

        empresa.setTelefone(usuario.getTelefone());
        empresa.setNome(usuario.getNome());
        empresa.setLogradouro(Logradouro);
        empresa.setEmail(usuario.getEmail());
        empresa.setComplemento(Complemento);
        empresa.setBairro(Bairro);
        empresa.setUF(UF);
        empresa.setLocalidade(Localidade);
        empresa.setPix(pix);

        usuario.salvar();

        String tipoUser = usuario.getTipo();

        if (tipoUser.equals("E")) {//Empresa

            empresa.setIdEmpresaUsuario(usuario.getId());
            empresa.salvar();

            Toast.makeText(CadastroEnderecoActivity.this, "Empresa cadastrada com sucesso! ", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(), EmpresaActivity.class));
            finish();

        } else {//usuario

            Toast.makeText(CadastroEnderecoActivity.this, "Cadastrado realizado com sucesso! " + usuario.getTipo(), Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            Toast.makeText(getApplicationContext(), "Por favor finalize o seu cadastro!", Toast.LENGTH_LONG).show();

        return false;
    }

    private void consultarCEP() {
        String sCep = txtCEP.getText().toString().trim();

        //removendo o ponto e o traço do padrão CEP
        sCep = sCep.replaceAll("[.-]+", "");

        //instanciando a interface
        RESTService restService = retrofitCEP.create(RESTService.class);

        //passando os dados para consulta
        Call<CEP> call = restService.consultarCEP(sCep);

        //colocando a requisição na fila para execução
        call.enqueue(new Callback<CEP>() {
            @Override
            public void onResponse(Call<CEP> call, Response<CEP> response) {
                if (response.isSuccessful()) {
                    CEP cep = response.body();
                    txtLogradouro.setText(cep.getLogradouro());
                    txtComplemento.setText(cep.getComplemento());
                    txtBairro.setText(cep.getBairro());
                    txtUF.setText(cep.getUf());
                    txtLocalidade.setText(cep.getLocalidade());
                    Toast.makeText(getApplicationContext(), "CEP consultado com sucesso", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(Call<CEP> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Ocorreu um erro ao tentar consultar o CEP. Erro: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void inicializarComponentes() {

        textCadastroChavePix = findViewById(R.id.editTextCadastroChavePix);
        txtCEP = findViewById(R.id.txtinpedtCEP);
        txtLogradouro = findViewById(R.id.txtinpedtLogradouro);
        txtComplemento = findViewById(R.id.txtinpedtComplemento);
        txtBairro = findViewById(R.id.txtinpedtBairro);
        txtUF = findViewById(R.id.txtinpedtUF);
        txtLocalidade = findViewById(R.id.txtinpedtLocalidade);
        btnConsultarCEP = findViewById(R.id.btnConsultarCEP);
        buttonCadastroEndereco = findViewById(R.id.buttonCadastroEndereco);

    }
}