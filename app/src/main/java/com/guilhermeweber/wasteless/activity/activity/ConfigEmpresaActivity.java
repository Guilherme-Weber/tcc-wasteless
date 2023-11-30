package com.guilhermeweber.wasteless.activity.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.guilhermeweber.wasteless.R;
import com.guilhermeweber.wasteless.activity.helper.ConfigFirebase;
import com.guilhermeweber.wasteless.activity.helper.Mascara;
import com.guilhermeweber.wasteless.activity.helper.RESTService;
import com.guilhermeweber.wasteless.activity.model.CEP;
import com.guilhermeweber.wasteless.activity.model.Empresa;
import com.guilhermeweber.wasteless.activity.model.Usuario;
import com.santalu.maskedittext.MaskEditText;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class ConfigEmpresaActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int SELECAO_GALERIA = 200;
    String contact = "+55 41 99844-2385";
    Empresa empresa = new Empresa();
    Usuario usuario = new Usuario();
    private Retrofit retrofitCEP;
    private EditText editTextEmailEmpConfig, editTextNomeEmpresa, editTextEmpresaCEP, editTextEmpresaTempo, editTextLogradouroConfig, editTextComplementoConfig, editTextBairroConfig, editTextUFConfig, editTextCidadeConfig;
    private Spinner spinnerEmpresaCategoria;
    private MaskEditText editTextNumeroTelefone;
    private FirebaseAuth auth;
    private CurrencyEditText editTextEmpresaTaxa;
    private Activity configEmpresaActivity = this;
    private CircleImageView imagePerfilEmpresa;
    private StorageReference storageReference;
    private DatabaseReference firebaseRef;
    private String idLogUsuario, urlImagemSelecionada;
    private List<String> listaFotosRec = new ArrayList<>();
    private String[] permissoes = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_empresa);

        iniciarComponentes();
        carregarDadosSpinner();

        auth = ConfigFirebase.getFireAuth();
        storageReference = ConfigFirebase.getRefStorage();
        firebaseRef = ConfigFirebase.getFirebase();
        idLogUsuario = Usuario.getIdUsuario();

        //config toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações da Empresa");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        editTextEmpresaCEP.addTextChangedListener(Mascara.insert(Mascara.MASCARA_CEP, editTextEmpresaCEP));

        imagePerfilEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });

        //recupera os dados do empresa logada
        recuperarDados();
    }

    private void recuperarDados() {

        DatabaseReference empresaRef = firebaseRef.child("empresa").child(idLogUsuario);
        empresaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    Empresa empresa = snapshot.getValue(Empresa.class);

                    if (!(empresa.getCategoria() == null)) {
                        spinnerEmpresaCategoria.setSelection(empresa.getIdCategoria());
                    }

                    urlImagemSelecionada = empresa.getUrlImagem();
                    editTextNumeroTelefone.setText(empresa.getTelefone());

                    if (urlImagemSelecionada != "") {
                        Picasso.get().load(urlImagemSelecionada).into(imagePerfilEmpresa);
                    }

                    editTextEmpresaCEP.setText(empresa.getcEP());
                    editTextLogradouroConfig.setText(empresa.getLogradouro());
                    editTextComplementoConfig.setText(empresa.getComplemento());
                    editTextBairroConfig.setText(empresa.getBairro());
                    editTextUFConfig.setText(empresa.getUF());
                    editTextCidadeConfig.setText(empresa.getLocalidade());
                    editTextEmailEmpConfig.setText(empresa.getEmail());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        DatabaseReference empresaRef2 = firebaseRef.child("usuarios").child(idLogUsuario);

        empresaRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    usuario = snapshot.getValue(Usuario.class);
                    editTextNomeEmpresa.setText(usuario.getNome());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.imageEmpresa2) {
            escolherImagem(1);
        }
    }

    public void escolherImagem(int requestCore) {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, requestCore);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //recuperar imagem
        Uri imagemSelecionada = data.getData();
        String caminhoImagem = imagemSelecionada.toString();

        //config umagem no ImageView
        imagePerfilEmpresa.setImageURI(imagemSelecionada);
        listaFotosRec.add(caminhoImagem);
    }

    public void CEP(View view) {
        if (validarCampos()) {
            String cep = editTextEmpresaCEP.getText().toString().trim();
            empresa.setcEP(cep);

            consultarCEP();
        }
    }

    private void consultarCEP() {

        String sCep = editTextEmpresaCEP.getText().toString().trim();

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
                    editTextLogradouroConfig.setText(cep.getLogradouro());
                    editTextComplementoConfig.setText(cep.getComplemento());
                    editTextBairroConfig.setText(cep.getBairro());
                    editTextUFConfig.setText(cep.getUf());
                    editTextCidadeConfig.setText(cep.getLocalidade());
                    Toast.makeText(getApplicationContext(), "CEP consultado com sucesso", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<CEP> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Ocorreu um erro ao tentar consultar o CEP. Erro: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private Boolean validarCampos() {

        Boolean status = true;
        String cep = editTextEmpresaCEP.getText().toString().trim();

        if (cep.isEmpty()) {
            editTextEmpresaCEP.setError("Informe um CEP válido.");
            status = false;
        }

        if ((cep.length() > 1) && (cep.length() < 10)) {
            editTextEmpresaCEP.setError("O CEP deve possuir 8 dígitos");
            status = false;
        }
        return status;
    }

    public void validarDadosEmpresa(View view) {

        String fone = "";
        String nome = editTextNomeEmpresa.getText().toString();
        String cEP = editTextEmpresaCEP.getText().toString();
        String logradouro = editTextLogradouroConfig.getText().toString();
        String complemento = editTextComplementoConfig.getText().toString();
        String bairro = editTextBairroConfig.getText().toString();
        String uF = editTextUFConfig.getText().toString();
        String cidade = editTextCidadeConfig.getText().toString();
        String email = editTextEmailEmpConfig.getText().toString();

        String categoriaText = spinnerEmpresaCategoria.getSelectedItem().toString();
        Integer categoria = spinnerEmpresaCategoria.getSelectedItemPosition();

        String telefone = editTextNumeroTelefone.getText().toString();
        if (editTextNumeroTelefone.getRawText() != null) {
            fone = editTextNumeroTelefone.getRawText().toString();
        }

        usuario.setId(idLogUsuario);
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setTipo("E");
        usuario.setTelefone(telefone);

        empresa.setCategoria(categoriaText);
        empresa.setIdCategoria(categoria);
        empresa.setIdEmpresaUsuario(idLogUsuario);
        empresa.setNome(nome);
        empresa.setEmail(email);
        empresa.setcEP(cEP);
        empresa.setLogradouro(logradouro);
        empresa.setComplemento(complemento);
        empresa.setBairro(bairro);
        empresa.setUF(uF);
        empresa.setLocalidade(cidade);
        empresa.setTelefone(telefone);

        for (int i = 0; i < listaFotosRec.size(); ++i) {
            String urlImagem = listaFotosRec.get(i);
            int tamanhoLista = listaFotosRec.size();
            salvarFotoStorage(urlImagem, tamanhoLista, i);
        }

        usuario.salvar();
        empresa.salvar();
    }

    private void mensagemToast(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

    private void carregarDadosSpinner() {

        String[] categorias = new String[]{"Padaria", "Confeitaria", "Doceria", "Hotelaria"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categorias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEmpresaCategoria.setAdapter(adapter);

    }

    private void salvarFotoStorage(String urlString, int totalFotos, int contador) {
        final StorageReference imagemEmpresa = storageReference.child("imagens").child("empresa").child(idLogUsuario).child("image" + contador + ".jpg");

        UploadTask uploadTask = imagemEmpresa.putFile(Uri.parse(urlString));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imagemEmpresa.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Uri uri = task.getResult();
                        firebaseRef.child("empresa").child(idLogUsuario).child("urlImagem").setValue(uri.toString());
                        firebaseRef.child("usuarios").child(idLogUsuario).child("urlImagem").setValue(uri.toString());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mensagemToast("Falha ao fazer upload");
                Log.i("INFO", "Falha ao fazer upload: " + e.getMessage());
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_empresa_config, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.menuSair) {
            deslogarUsuario();
        } else if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(this, EmpresaActivity.class));
        } else if (item.getItemId() == R.id.zapzap) {
            abrirZapZap();
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

    private void iniciarComponentes() {

        editTextNomeEmpresa = findViewById(R.id.editTextNomeEmpresa);
        editTextEmpresaCEP = findViewById(R.id.editTextEmpresaCEP);
        editTextLogradouroConfig = findViewById(R.id.editTextLogradouroConfig);
        editTextComplementoConfig = findViewById(R.id.editTextComplementoConfig);
        editTextBairroConfig = findViewById(R.id.editTextBairroConfig);
        editTextUFConfig = findViewById(R.id.editTextUFConfig);
        editTextCidadeConfig = findViewById(R.id.editTextCidadeConfig);
        editTextNumeroTelefone = findViewById(R.id.editTextTelefone);
        editTextEmailEmpConfig = findViewById(R.id.editTextEmailEmpConfig);
        imagePerfilEmpresa = findViewById(R.id.imageEmpresa2);
        imagePerfilEmpresa.setOnClickListener(this);
        spinnerEmpresaCategoria = findViewById(R.id.spinnerEmpresaCategoria);
    }
}