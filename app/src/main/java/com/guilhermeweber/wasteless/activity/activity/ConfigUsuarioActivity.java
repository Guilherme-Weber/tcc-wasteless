package com.guilhermeweber.wasteless.activity.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import com.guilhermeweber.wasteless.activity.model.Usuario;
import com.santalu.maskedittext.MaskEditText;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConfigUsuarioActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int SELECAO_GALERIA = 200;
    private CircleImageView imageUsuario;
    private EditText editTextNomeUsuario, editTextUsuarioCEP, editTextUsuarioEndereco;
    private MaskEditText editTextTelefone;
    private StorageReference storageReference;
    private DatabaseReference firebaseRef;
    private String idLogUsuario, urlImagemSelecionada;
    private List<String> listaFotosRec = new ArrayList<>();
    private String[] permissoes = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_usuario);

        //Validar Permissões
        Permissoes.validarPermissoes(permissoes, this, 1);

        iniciarComponentes();

        storageReference = ConfigFirebase.getRefStorage();
        firebaseRef = ConfigFirebase.getFirebase();
        idLogUsuario = Usuario.getIdUsuario();

        //config toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações do Usuário");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });

        //recupera os dados do usuario logada
        recuperarDados();
    }

    private void recuperarDados() {
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idLogUsuario);
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {

                    Usuario usuario = snapshot.getValue(Usuario.class);
                    editTextNomeUsuario.setText(usuario.getNome());
                    editTextTelefone.setText(usuario.getTelefone());
                    editTextUsuarioCEP.setText(usuario.getcEP());
                    editTextUsuarioEndereco.setText(usuario.getEndereco());

                    urlImagemSelecionada = usuario.getUrlImagem();

                    if (urlImagemSelecionada != "") {
                        Picasso.get().load(urlImagemSelecionada).into(imageUsuario);
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
        if (needif == R.id.imageUsuario) {
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

        imageUsuario.setImageURI(imagemSelecionada);
        listaFotosRec.add(caminhoImagem);
    }

    public void validarDadosUsuario(View view) {

        String fone = "";
        String nome = editTextNomeUsuario.getText().toString();
        String endereco = editTextUsuarioEndereco.getText().toString();
        String cEP = editTextUsuarioCEP.getText().toString();

        String telefone = editTextTelefone.getText().toString();
        if (editTextTelefone.getRawText() != null) {
            fone = editTextTelefone.getRawText().toString();
        }

        if (listaFotosRec.size() != 0) {
            if (!nome.isEmpty()) {
                if (!endereco.isEmpty()) {
                    if (!cEP.isEmpty()) {
                        if (!telefone.isEmpty() && fone.length() >= 10) {

                            Usuario usuario = new Usuario();

                            usuario.setNome(nome);
                            usuario.setEndereco(endereco);
                            usuario.setcEP(cEP);
                            usuario.setTelefone(telefone);

                            for (int i = 0; i < listaFotosRec.size(); ++i) {
                                String urlImagem = listaFotosRec.get(i);
                                int tamanhoLista = listaFotosRec.size();
                                salvarFotoStorage(urlImagem, tamanhoLista, i);
                            }

                            usuario.salvar();

                        } else {
                            mensagemToast("Preencha o campo Telefone");
                        }
                    } else {
                        mensagemToast("Preencha o campo CEP");
                    }
                } else {
                    mensagemToast("Preencha o campo Endereço Completo");
                }
            } else {
                mensagemToast("Preencha o campo Nome");
            }
        } else {
            mensagemToast("Selecione uma imagem de perfil");
        }

    }

    private void mensagemToast(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

    private void iniciarComponentes() {
        editTextNomeUsuario = findViewById(R.id.editTextNomeUsuario);
        editTextUsuarioEndereco = findViewById(R.id.editTextUsuarioEndereco);
        editTextUsuarioCEP = findViewById(R.id.editTextUsuarioCEP);
        editTextTelefone = findViewById(R.id.editTextTelefone);

        imageUsuario = findViewById(R.id.imageUsuario);
        imageUsuario.setOnClickListener(this);
    }


    private void salvarFotoStorage(String urlString, int totalFotos, int contador) {
        final StorageReference imagemUsuario = storageReference.child("imagens").child("usuarios").child(idLogUsuario).child("image" + contador + ".jpg");

        UploadTask uploadTask = imagemUsuario.putFile(Uri.parse(urlString));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imagemUsuario.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Uri uri = task.getResult();
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
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões");
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