package com.guilhermeweber.wasteless.activity.activity;

import android.Manifest;
import android.content.DialogInterface;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.guilhermeweber.wasteless.R;
import com.guilhermeweber.wasteless.activity.helper.ConfigFirebase;
import com.guilhermeweber.wasteless.activity.helper.Mascara;
import com.guilhermeweber.wasteless.activity.helper.Permissoes;
import com.guilhermeweber.wasteless.activity.model.Produto;
import com.guilhermeweber.wasteless.activity.model.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NovoProdutoEmpresaActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int SELECAO_GALERIA = 200;
    String contact = "+55 41 99844-2385";
    private FirebaseAuth auth;
    private StorageReference storageReference;
    private DatabaseReference firebaseRef;
    private String idLogUsuario, ramdom;
    private EditText editTextNomeProduto, editTexTextDescricao, editTextHoraRetDe, editTextHoraRetAte;
    private ImageView ImageViewImageProduto;
    private CurrencyEditText editTextPrecoProduto;
    private List<String> listaFotosRec = new ArrayList<>();
    private String[] permissoes = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
    private RadioButton salgado, doce, mista, pequena, media, grande;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_produto_empresa);

        //Validar Permissões
        Permissoes.validarPermissoes(permissoes, this, 1);

        iniciarComponentes();

        ramdom = String.valueOf(System.currentTimeMillis());
        auth = ConfigFirebase.getFireAuth();
        storageReference = ConfigFirebase.getRefStorage();
        firebaseRef = ConfigFirebase.getFirebase();
        idLogUsuario = Usuario.getIdUsuario();

        //config toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Cadastro de Produto");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        editTextHoraRetDe.addTextChangedListener(Mascara.insert(Mascara.MASCARA_TIME, editTextHoraRetDe));
        editTextHoraRetAte.addTextChangedListener(Mascara.insert(Mascara.MASCARA_TIME, editTextHoraRetAte));

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
        if (v.getId() == R.id.imageNovoProduto) {
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

        String nome = editTextNomeProduto.getText().toString();
        String descricao = editTexTextDescricao.getText().toString();
        Long preco = editTextPrecoProduto.getRawValue();
        String horarioDe = editTextHoraRetDe.getText().toString();
        String horarioAte = editTextHoraRetAte.getText().toString();

        if (preco == null) {
            preco = Long.valueOf("0");
        }

        if (!nome.isEmpty()) {
            if (!descricao.isEmpty()) {
                if (salgado.isChecked() || doce.isChecked() || mista.isChecked()) {
                    if (pequena.isChecked() || media.isChecked() || grande.isChecked()) {
                        if (!(preco == 0)) {

                            Produto produto = new Produto();

                            produto.setIdEmpresa(idLogUsuario);
                            produto.setNomeProduto(nome);
                            produto.setDescricao(descricao);
                            produto.setTempoDe(horarioDe);
                            produto.setTempoAte(horarioAte);

                            if (salgado.isChecked()) {
                                produto.setTipoPacote(salgado.getText().toString());
                                produto.setIdTipoPacote("1");
                            } else if (doce.isChecked()) {
                                produto.setTipoPacote(doce.getText().toString());
                                produto.setIdTipoPacote("2");
                            } else if (mista.isChecked()) {
                                produto.setTipoPacote(mista.getText().toString());
                                produto.setIdTipoPacote("3");
                            }

                            if (pequena.isChecked()) {
                                produto.setTamanhoPacote(pequena.getText().toString());
                                produto.setIdTamanhoPacote("1");
                            } else if (media.isChecked()) {
                                produto.setTamanhoPacote(media.getText().toString());
                                produto.setIdTamanhoPacote("2");
                            } else if (grande.isChecked()) {
                                produto.setTamanhoPacote(grande.getText().toString());
                                produto.setIdTamanhoPacote("3");
                            }

                            produto.setPreco(preco);

                            for (int i = 0; i < listaFotosRec.size(); ++i) {
                                String urlImagem = listaFotosRec.get(i);
                                int tamanhoLista = listaFotosRec.size();
                                salvarFotoStorage(urlImagem, tamanhoLista, i);
                            }

                            produto.setIdProduto(ramdom);

                            produto.salvar(ramdom);
                            finish();
                            mensagemToast("Produto cadastrado");
                        } else {
                            String pc = "Informe o preço do produto";
                            mensagemToast(pc);
                            editTextPrecoProduto.setError(pc);
                        }
                    } else {
                        String tm = "Informe o tamanho do pacote";
                        mensagemToast(tm);
                        pequena.setError(tm);
                        media.setError(tm);
                        grande.setError(tm);
                    }
                } else {
                    String tp = "Informe o tipo de pacote";
                    mensagemToast(tp);
                    salgado.setError(tp);
                    doce.setError(tp);
                    mista.setError(tp);
                }
            } else {
                String des = "Informe a descrição do produto";
                mensagemToast(des);
                editTexTextDescricao.setError(des);
            }
        } else {
            mensagemToast("Informe o nome do produto");
            editTextNomeProduto.setError("Informe o nome do produto");
        }
    }

    private void mensagemToast(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

    private void salvarFotoStorage(String urlString, int totalFotos, int contador) {
        final StorageReference imagemProduto = storageReference.child("imagens").child("produto").child(ramdom).child(idLogUsuario).child("image" + contador + ".jpg");

        UploadTask uploadTask = imagemProduto.putFile(Uri.parse(urlString));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imagemProduto.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Uri uri = task.getResult();
                        firebaseRef.child("produto").child("tudo").child(ramdom).child("urlImagem").setValue(uri.toString());
                        firebaseRef.child("produto").child(idLogUsuario).child(ramdom).child("urlImagem").setValue(uri.toString());
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_empresa_novo, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.menuSair) {
            deslogarUsuario();
        } else if (item.getItemId() == R.id.menuConfig) {
            abrirConfig();
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

    private void abrirConfig() {
        startActivity(new Intent(NovoProdutoEmpresaActivity.this, ConfigEmpresaActivity.class));
    }

    private void iniciarComponentes() {

        ImageViewImageProduto = findViewById(R.id.imageNovoProduto);
        ImageViewImageProduto.setOnClickListener(this);
        editTextNomeProduto = findViewById(R.id.editTextNomeProduto);
        editTexTextDescricao = findViewById(R.id.editTextDescrição);
        editTextHoraRetDe = findViewById(R.id.editTextHoraRetDe);
        editTextHoraRetAte = findViewById(R.id.editTextHoraRetAte);
        editTextPrecoProduto = findViewById(R.id.editTextPrecoProduto);

        Locale locale = new Locale("pt", "BR");
        editTextPrecoProduto.setLocale(locale);

        salgado = findViewById(R.id.radioButtonSalgado);
        doce = findViewById(R.id.radioButtonDoce);
        mista = findViewById(R.id.radioButtonMista);
        pequena = findViewById(R.id.radioButtonPequena);
        media = findViewById(R.id.radioButtonMedia);
        grande = findViewById(R.id.radioButtonGrande);
    }
}