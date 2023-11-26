package com.guilhermeweber.wasteless.activity.model;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ValueEventListener;
import com.guilhermeweber.wasteless.activity.activity.AutentificacaoActivity;
import com.guilhermeweber.wasteless.activity.activity.CadastroEnderecoActivity;
import com.guilhermeweber.wasteless.activity.activity.CadastrosUsuariosActivity;
import com.guilhermeweber.wasteless.activity.activity.CardapioActivity;
import com.guilhermeweber.wasteless.activity.activity.EmpresaActivity;
import com.guilhermeweber.wasteless.activity.activity.HomeActivity;
import com.guilhermeweber.wasteless.activity.helper.ConfigFirebase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Usuario implements Serializable {
    private String id;
    private String urlImagem;
    private String nome;
    private String email;
    private String senha;
    private String tipo;
    private String Telefone;
    private List<Empresa> EmpresasFavoritas;

    public Usuario() {
        EmpresasFavoritas = new ArrayList<Empresa>();
    }

    public static FirebaseUser getUsuarioAtual() {
        FirebaseAuth usuario = ConfigFirebase.getFireAuth();
        return usuario.getCurrentUser();
    }

    public static String getIdUsuario() {
        FirebaseAuth auth = ConfigFirebase.getFireAuth();
        return auth.getCurrentUser().getUid();
    }

    public static boolean updateUserName(String nome) {
        try {
            FirebaseUser user = getUsuarioAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().setDisplayName(nome).build();
            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()) {
                        Log.d("Perfil", "Erro ao atualizar Nome de perfil.");
                    }
                }
            });
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void redirectUser(Activity activity) {

        DatabaseReference userRef = ConfigFirebase.getFirebase().child("usuarios").child(getUsuarioAtual().getUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Usuario usuario = snapshot.getValue(Usuario.class);

                try {

                    String tipoUser = usuario.getTipo();

                    if (tipoUser.equals("E")) {
                        activity.startActivity(new Intent(activity, EmpresaActivity.class));
                    } else {
                        activity.startActivity(new Intent(activity, HomeActivity.class));
                    }

                } catch (Exception e) {
                    FirebaseAuth auth;
                    auth = ConfigFirebase.getFireAuth();
                    auth.signOut();

                    System.out.println(e);

                    System.out.println("Erro no tipo de Usuario deslogando e voltando ao login");

                    activity.startActivity(new Intent(activity, AutentificacaoActivity.class));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void salvar() {
        DatabaseReference fireRef = ConfigFirebase.getFirebase();
        DatabaseReference usuarios = fireRef.child("usuarios").child(getId());
        usuarios.setValue(this);
    }

    public void manageEmpresaFavorita(Empresa empresa) {
        if (EmpresasFavoritas.contains(empresa)) {
            EmpresasFavoritas.remove(empresa);
            System.out.println("Empresa " + empresa.getNome() + " removida dos favoritos!");
        } else {
            EmpresasFavoritas.add(empresa);
            System.out.println("Empresa " + empresa.getNome() + " adicionada dos favoritos!");
        }

        salvar();
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }

    public String getTelefone() {
        return Telefone;
    }

    public void setTelefone(String telefone) {
        Telefone = telefone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude //não salva a senha no bando de dados por segurança
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public List<Empresa> getEmpresasFavoritas() {
        return EmpresasFavoritas;
    }

    public void setEmpresasFavoritas(List<Empresa> empresasFavoritas) {
        EmpresasFavoritas = empresasFavoritas;
    }
}
