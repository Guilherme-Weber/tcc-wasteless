package com.guilhermeweber.wasteless.activity.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.guilhermeweber.wasteless.R;
import com.guilhermeweber.wasteless.activity.helper.ConfigFirebase;
import com.guilhermeweber.wasteless.activity.helper.Permissoes;
import com.guilhermeweber.wasteless.activity.helper.RESTService;
import com.guilhermeweber.wasteless.activity.model.CEP;
import com.guilhermeweber.wasteless.activity.model.Empresa;
import com.guilhermeweber.wasteless.activity.model.Usuario;
import com.santalu.maskedittext.MaskEditText;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class ConfigEmpresaActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int SELECAO_GALERIA = 200;
    Empresa empresa = new Empresa();
    Usuario usuario = new Usuario();
    private Retrofit retrofitCEP;
    private EditText editTextEmailEmpConfig, editTextNomeEmpresa, editTextEmpresaCEP, editTextEmpresaTempo, editTextLogradouroConfig, editTextComplementoConfig, editTextBairroConfig, editTextUFConfig, editTextCidadeConfig;
    private Spinner spinnerEmpresaCategoria;
    private MaskEditText editTextNumeroTelefone;
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

        //Validar Permissões
//        Permissoes.validarPermissoes(permissoes, this, 1);

        iniciarComponentes();

        carregarDadosSpinner();

        storageReference = ConfigFirebase.getRefStorage();
        firebaseRef = ConfigFirebase.getFirebase();
        idLogUsuario = Usuario.getIdUsuario();

        //config toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações da Empresa");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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
                    editTextEmpresaCEP.setText(usuario.getcEP());
                    editTextLogradouroConfig.setText(usuario.getLogradouro());
                    editTextComplementoConfig.setText(usuario.getComplemento());
                    editTextBairroConfig.setText(usuario.getBairro());
                    editTextUFConfig.setText(usuario.getUF());
                    editTextCidadeConfig.setText(usuario.getLocalidade());
                    editTextEmailEmpConfig.setText(usuario.getEmail());

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
        } else if (v.getId() == android.R.id.home) {
            Intent intent = new Intent(ConfigEmpresaActivity.this, HomeActivity.class);
            startActivity(intent);
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
            editTextEmpresaCEP.setError("Digite um CEP válido.");
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

//      if (listaFotosRec.size() != 0) {
//      if (!nome.isEmpty()) {
//      if (categoria != null) {
//      if (!taxa.isEmpty() && !taxa.equals("0")) {
//      if (!tempo.isEmpty()) {
//      if (!telefone.isEmpty() && fone.length() >= 10) {

        usuario.setId(idLogUsuario);
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setcEP(cEP);
        usuario.setLogradouro(logradouro);
        usuario.setComplemento(complemento);
        usuario.setBairro(bairro);
        usuario.setUF(uF);
        usuario.setLocalidade(cidade);

        usuario.setTipo("E");

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

        for (int i = 0; i < listaFotosRec.size(); ++i) {
            String urlImagem = listaFotosRec.get(i);
            int tamanhoLista = listaFotosRec.size();
            salvarFotoStorage(urlImagem, tamanhoLista, i);
        }


        usuario.salvar();
        empresa.salvar();

//      } else {
//          mensagemToast("Preencha o campo telefone");
//      }
//      } else {
//          mensagemToast("Digite o tempo de entrega");
//      }
//      } else {
//          mensagemToast("Digite a taxa de entrega da empresa");
//      }
//      } else {
//          mensagemToast("Selecione a categoria em que a empresa opera");
//      }
//      } else {
//          mensagemToast("Preencha o nome para a empresa");
//      }
//      } else {
//          mensagemToast("Selecione uma imagem para a empresa");
//      }
    }

    private void mensagemToast(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
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

    private void carregarDadosSpinner() {

        String[] categorias = new String[]{"Padaria", "Comercio em Geral", "Autônomo", "Hotelaria"};
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

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        for (int permissaoResultado : grantResults) {
//            if (permissaoResultado == PackageManager.PERMISSION_DENIED) {
////                alertPermissao();
//            }
//        }
//    }

//    private void alertPermissao() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Permissões Negadas");
//        builder.setMessage("Para utilizar o app é necessário aceitar as permissões");
//        builder.setCancelable(false);
//        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Permissoes.validarPermissoes(permissoes, configEmpresaActivity, 1);
//                finish();
//            }
//        });
//
//        AlertDialog dialog = builder.create();
//        dialog.show();
//    }

    //metodo antigo mais complexo que ja envia logo que o usuario escolhe a foto (n funciona corretamento com a ultimas adições)

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (resultCode == RESULT_FIRST_USER) {
//            Bitmap imagem = null;
//            try {
//
//                switch (requestCode) {
//                    case SELECAO_GALERIA:
//                        Uri localImagem = data.getData();
//                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagem);
//                        break;
//                }
//
//                if (imagem != null) {
//                    imagePerfilEmpresa.setImageBitmap(imagem);
//
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
//                    byte[] dadosImagem = baos.toByteArray();
//
//                    final StorageReference imagemRef = storageReference.child("imagens").child("empresas").child(idLogUsuario + ".jpeg");
//
//                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
//                    uploadTask.addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(ConfigEmpresaActivity.this, "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
//                        }
//                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
////                            Task<Uri> url = imagemRef.getDownloadUrl();
////                            urlImagemSelecionada = taskSnapshot.getStorage().getDownloadUrl().toString();
//
//                            urlImagemSelecionada = imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Uri> task) {
//                                    Uri url = task.getResult();
//                                }
//                            }).toString();
//                            Toast.makeText(ConfigEmpresaActivity.this, "Sucesso ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

}