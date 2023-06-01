package com.guilhermeweber.wasteless.activity.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.guilhermeweber.wasteless.R;
import com.guilhermeweber.wasteless.activity.helper.ConfigFirebase;
import com.guilhermeweber.wasteless.activity.helper.Permissoes;
import com.guilhermeweber.wasteless.activity.model.Produto;
import com.guilhermeweber.wasteless.activity.model.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NovoProdutoEmpresaActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int SELECAO_GALERIA = 200;
    private StorageReference storageReference;
    private DatabaseReference firebaseRef;
    private String idLogUsuario;
    private EditText editTextNomeProduto;
    private EditText editTexTextDescricao;
    private Spinner spinnerProdutoCategoria;
    private ImageView ImageViewImageProduto;
    private CurrencyEditText editTextPrecoProduto;
    private List<String> listaFotosRec = new ArrayList<>();
    private String[] permissoes = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_produto_empresa);

        //Validar Permissões
        Permissoes.validarPermissoes(permissoes, this, 1);


        storageReference = ConfigFirebase.getRefStorage();
        firebaseRef = ConfigFirebase.getFirebase();
        idLogUsuario = Usuario.getIdUsuario();

        //config toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo Produto");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageViewImageProduto.setOnClickListener(new View.OnClickListener() {
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
        if (needif == R.id.imageProduto) {
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

        ImageViewImageProduto.setImageURI(imagemSelecionada);
        listaFotosRec.add(caminhoImagem);
    }

    public void validarDadosProduto(View view) {

        String fone = "";
        String nome = editTextNomeProduto.getText().toString();
        Integer categoria = spinnerProdutoCategoria.getSelectedItemPosition();
        String descricao = editTexTextDescricao.getText().toString();

        if (listaFotosRec.size() != 0) {
            if (!nome.isEmpty()) {
                if (categoria != null) {
                    Produto produto = new Produto();

//                    empresa.setIdProduto(idLogUsuario);
//                    empresa.setNome(nome);
//                    empresa.setCategoria(categoria);
//
//                    for (int i = 0; i < listaFotosRec.size(); ++i) {
//                        String urlImagem = listaFotosRec.get(i);
//                        int tamanhoLista = listaFotosRec.size();
//                        salvarFotoStorage(urlImagem, tamanhoLista, i);
//                    }
//                    empresa.salvar();

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
        editTextNomeProduto = findViewById(R.id.editTextNomeProduto);
        editTexTextDescricao = findViewById(R.id.editTextDescricao);
        spinnerProdutoCategoria = findViewById(R.id.spinnerProdutoCategoria);

        ImageViewImageProduto = findViewById(R.id.imageProduto);
        ImageViewImageProduto.setOnClickListener(this);

        editTextPrecoProduto = findViewById(R.id.editTextPrecoProduto);
        Locale locale = new Locale("pt", "BR");
        editTextPrecoProduto.setLocale(locale);
    }
}