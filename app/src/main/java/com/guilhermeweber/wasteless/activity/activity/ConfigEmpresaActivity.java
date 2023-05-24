package com.guilhermeweber.wasteless.activity.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.guilhermeweber.wasteless.R;
import com.guilhermeweber.wasteless.activity.helper.ConfigFirebase;
import com.guilhermeweber.wasteless.activity.model.Empresa;
import com.guilhermeweber.wasteless.activity.model.Usuario;

import java.io.ByteArrayOutputStream;

public class ConfigEmpresaActivity extends AppCompatActivity {
    private static final int SELECAO_GALERIA = 200;
    private EditText editTextEmpresaNome, editTextEmpresaCategoria, editTextEmpresaTempo, editTextEmpresaTaxa;
    private ImageView imagePerfilEmpresa;
    private StorageReference storageReference;
    private String idLogUsuario;
    private String urlImagemSelecionada = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_empresa);

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

    public void validarDadosEmpresa(View view) {

        String nome = editTextEmpresaNome.getText().toString();
        String categoria = editTextEmpresaCategoria.getText().toString();
        String taxa = editTextEmpresaTaxa.getText().toString();
        String tempo = editTextEmpresaTempo.getText().toString();

        if (!nome.isEmpty()) {
            if (!categoria.isEmpty()) {
                if (!taxa.isEmpty()) {
                    if (!tempo.isEmpty()) {

                        Empresa empresa = new Empresa();
                        empresa.setIdUsuario(idLogUsuario);
                        empresa.setNome(nome);
                        empresa.setPrecoEntrega(Double.parseDouble(taxa));
                        empresa.setCategoria(categoria);
                        empresa.setTempo(tempo);
                        empresa.setUrlImagem(urlImagemSelecionada);
                        empresa.salvar();

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_FIRST_USER) {
            Bitmap imagem = null;
            try {

                switch (requestCode) {
                    case SELECAO_GALERIA:
                        Uri localImagem = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagem);
                        break;
                }

                if (imagem != null) {
                    imagePerfilEmpresa.setImageBitmap(imagem);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    final StorageReference imagemRef = storageReference.child("imagens").child("empresas").child(idLogUsuario + ".jpeg");

                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ConfigEmpresaActivity.this, "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

//                            Task<Uri> url = imagemRef.getDownloadUrl();
//                            urlImagemSelecionada = taskSnapshot.getStorage().getDownloadUrl().toString();

                            urlImagemSelecionada = imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Uri url = task.getResult();
                                }
                            }).toString();
                            Toast.makeText(ConfigEmpresaActivity.this, "Sucesso ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void iniciarComponentes() {
        editTextEmpresaNome = findViewById(R.id.editTextNomeEmpresa);
        editTextEmpresaCategoria = findViewById(R.id.editTextEmpresaCategoria);
        editTextEmpresaTaxa = findViewById(R.id.editTextEmpresaTaxa);
        editTextEmpresaTempo = findViewById(R.id.editTextEmpresaTempo);
        imagePerfilEmpresa = findViewById(R.id.imagePerfilEmpresa);
    }
}