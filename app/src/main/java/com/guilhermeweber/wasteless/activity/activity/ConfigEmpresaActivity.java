package com.guilhermeweber.wasteless.activity.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.google.firebase.storage.StorageReference;
import com.guilhermeweber.wasteless.R;
import com.guilhermeweber.wasteless.activity.helper.ConfigFirebase;
import com.guilhermeweber.wasteless.activity.helper.Permissoes;
import com.guilhermeweber.wasteless.activity.model.Empresa;
import com.guilhermeweber.wasteless.activity.model.Usuario;
import com.santalu.maskedittext.MaskEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class ConfigEmpresaActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int SELECAO_GALERIA = 200;
    private EditText editTextEmpresaNome, editTextEmpresaCategoria, editTextEmpresaTempo;
    private MaskEditText editTextNumeroTelefone;
    private CurrencyEditText editTextEmpresaTaxa;
    private CircleImageView imagePerfilEmpresa;
    private StorageReference storageReference;
    private String idLogUsuario;
    private String urlImagemSelecionada = "";

    private List<String> listaFotosRec = new ArrayList<>();

    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_empresa);


        //Validar Permissões
        Permissoes.validarPermissoes(permissoes, this, 1);

        iniciarComponentes();

        storageReference = ConfigFirebase.getRefStorage();
        idLogUsuario = Usuario.getIdUsuario();

        //config toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imagePerfilEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

        int needif = v.getId();
        if (needif == R.id.imagePerfilEmpresa) {
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

        String nome = editTextEmpresaNome.getText().toString();
        String categoria = editTextEmpresaCategoria.getText().toString();
        String taxa = editTextEmpresaTaxa.getText().toString();
        String tempo = editTextEmpresaTempo.getText().toString();
        String telefone = editTextNumeroTelefone.getText().toString();

        if (!nome.isEmpty()) {
            if (!categoria.isEmpty()) {
                if (!taxa.isEmpty()) {
                    if (!tempo.isEmpty()) {
                        if (!telefone.isEmpty()) {
                            Empresa empresa = new Empresa();
                            empresa.setIdUsuario(idLogUsuario);
                            empresa.setNome(nome);
                            empresa.setPrecoEntrega(Double.parseDouble(taxa));
                            empresa.setCategoria(categoria);
                            empresa.setTempo(tempo);
                            empresa.setTelefone(telefone);
                            empresa.setUrlImagem(urlImagemSelecionada);
                            empresa.salvar();
                        } else {
                            mensagemToast("Digite o numero de telefone");
                        }
                    } else {
                        mensagemToast("Digite o tempo de entrega");
                    }

                } else {
                    mensagemToast("Digite a taxa de entrega da empresa");
                }
            } else {
                mensagemToast("Digite uma categoria");
            }
        } else {
            mensagemToast("Digite o nome para a empresa");
        }

    }

    private void mensagemToast(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }


    //metodo antigo mais complexo que ja envia logo que o usuario escolhe a foto

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

    private void iniciarComponentes() {
        editTextEmpresaNome = findViewById(R.id.editTextNomeEmpresa);
        editTextEmpresaCategoria = findViewById(R.id.editTextEmpresaCategoria);
        editTextEmpresaTempo = findViewById(R.id.editTextEmpresaTempo);
        editTextNumeroTelefone = findViewById(R.id.editTextNumeroTelefone);

        imagePerfilEmpresa = findViewById(R.id.imagePerfilEmpresa);
        imagePerfilEmpresa.setOnClickListener(this);

        editTextEmpresaTaxa = findViewById(R.id.editTextEmpresaTaxa);
        Locale locale = new Locale("pt", "BR");
        editTextEmpresaTaxa.setLocale(locale);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissaoResultado : grantResults) {
            if (permissaoResultado == PackageManager.PERMISSION_DENIED) {
                alertPermissao();
            }
        }
    }

    private void alertPermissao() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o appp é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}