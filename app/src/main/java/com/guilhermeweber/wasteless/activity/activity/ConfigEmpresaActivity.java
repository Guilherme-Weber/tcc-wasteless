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
import com.guilhermeweber.wasteless.activity.model.Empresa;
import com.guilhermeweber.wasteless.activity.model.Usuario;
import com.santalu.maskedittext.MaskEditText;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class ConfigEmpresaActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int SELECAO_GALERIA = 200;
    private EditText editTextEmpresaNome, editTextEmpresaTempo;
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
        Permissoes.validarPermissoes(permissoes, this, 1);

        iniciarComponentes();

        carregarDadosSpinner();

        storageReference = ConfigFirebase.getRefStorage();
        firebaseRef = ConfigFirebase.getFirebase();
        idLogUsuario = Usuario.getIdUsuario();

        //config toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
                    editTextEmpresaNome.setText(empresa.getNome());
                    spinnerEmpresaCategoria.setSelection(empresa.getIdCategoria());
                    editTextEmpresaTaxa.setValue(empresa.getPrecoEntrega());
                    editTextNumeroTelefone.setText(empresa.getTelefone());
                    editTextEmpresaTempo.setText(empresa.getTempo());

                    urlImagemSelecionada = empresa.getUrlImagem();

                    if (urlImagemSelecionada != "") {
                        Picasso.get().load(urlImagemSelecionada).into(imagePerfilEmpresa);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View v) {

        int needif = v.getId();
        if (needif == R.id.imageEmpresa1) {
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

    public void validarDadosEmpresa(View view) {

        String fone = "";
        String nome = editTextEmpresaNome.getText().toString();
        String categoriaText = spinnerEmpresaCategoria.getSelectedItem().toString();
        Integer categoria = spinnerEmpresaCategoria.getSelectedItemPosition();
        String taxa = String.valueOf(editTextEmpresaTaxa.getRawValue());
        String tempo = editTextEmpresaTempo.getText().toString();

        String telefone = editTextNumeroTelefone.getText().toString();
        if (editTextNumeroTelefone.getRawText() != null) {
            fone = editTextNumeroTelefone.getRawText().toString();
        }

        if (listaFotosRec.size() != 0) {
            if (!nome.isEmpty()) {
                if (categoria != null) {
                    if (!taxa.isEmpty() && !taxa.equals("0")) {
                        if (!tempo.isEmpty()) {
                            if (!telefone.isEmpty() && fone.length() >= 10) {
                                Empresa empresa = new Empresa();

                                empresa.setIdEmpresa(idLogUsuario);
                                empresa.setNome(nome);
                                empresa.setPrecoEntrega(Long.parseLong(taxa));
                                empresa.setCategoria(categoriaText);
                                empresa.setIdCategoria(categoria);
                                empresa.setTempo(tempo);
                                empresa.setTelefone(telefone);

                                for (int i = 0; i < listaFotosRec.size(); ++i) {
                                    String urlImagem = listaFotosRec.get(i);
                                    int tamanhoLista = listaFotosRec.size();
                                    salvarFotoStorage(urlImagem, tamanhoLista, i);
                                }
                                empresa.salvar();

                            } else {
                                mensagemToast("Preencha o campo telefone");
                            }
                        } else {
                            mensagemToast("Digite o tempo de entrega");
                        }
                    } else {
                        mensagemToast("Digite a taxa de entrega da empresa");
                    }
                } else {
                    mensagemToast("Selecione a categoria em que a empresa opera");
                }
            } else {
                mensagemToast("Preencha o nome para a empresa");
            }
        } else {
            mensagemToast("Selecione uma imagem para a empresa");
        }
    }

    private void mensagemToast(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

    private void iniciarComponentes() {
        editTextEmpresaNome = findViewById(R.id.TextNomeCadastro);
        editTextEmpresaTempo = findViewById(R.id.editTextEmpresaTempo);
        editTextNumeroTelefone = findViewById(R.id.editTextNumeroTelefone);
        spinnerEmpresaCategoria = findViewById(R.id.spinnerProdutoCategoria);

        imagePerfilEmpresa = findViewById(R.id.imageEmpresa1);
        imagePerfilEmpresa.setOnClickListener(this);

        editTextEmpresaTaxa = findViewById(R.id.editTextPrecoProduto);
        Locale locale = new Locale("pt", "BR");
        editTextEmpresaTaxa.setLocale(locale);
    }

    private void carregarDadosSpinner() {

        String[] categorias = new String[]{"Padaria", "Comercio em Geral", "Opção 3", "Opção 4", "Opção 5"};
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissaoResultado : grantResults) {
            if (permissaoResultado == PackageManager.PERMISSION_DENIED) {
//                alertPermissao();
            }
        }
    }

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