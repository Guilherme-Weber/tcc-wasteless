package com.guilhermeweber.wasteless.activity.model;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.guilhermeweber.wasteless.activity.helper.ConfigFirebase;

import java.io.Serializable;

public class Empresa implements Serializable {

    private String idEmpresaUsuario;
    private String categoria;
    private Integer idCategoria;
    private String urlImagem;
    private String nome;
    private String email;
    private String tipo;
    private String Telefone;
    private String cEP;
    private String endereco;
    private String logradouro;
    private String complemento;
    private String bairro;
    private String UF;
    private String localidade;

    public Empresa() {
    }

    public void salvar() {
        DatabaseReference firebaseRef = ConfigFirebase.getFirebase();

        DatabaseReference empresaRef = firebaseRef.child("empresa").child(getIdEmpresaUsuario());
        empresaRef.setValue(this);
    }

    @Override
    public boolean equals(Object empresa) {
        if (empresa instanceof Empresa) {
            return this.getIdEmpresaUsuario().equals(((Empresa)empresa).getIdEmpresaUsuario());
        }

        return false;
    }

    public String getIdEmpresaUsuario() {
        return idEmpresaUsuario;
    }

    public void setIdEmpresaUsuario(String idEmpresaUsuario) {
        this.idEmpresaUsuario = idEmpresaUsuario;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTelefone() {
        return Telefone;
    }

    public void setTelefone(String telefone) {
        Telefone = telefone;
    }

    public String getcEP() {
        return cEP;
    }

    public void setcEP(String cEP) {
        this.cEP = cEP;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getUF() {
        return UF;
    }

    public void setUF(String UF) {
        this.UF = UF;
    }

    public String getLocalidade() {
        return localidade;
    }

    public void setLocalidade(String localidade) {
        this.localidade = localidade;
    }
}
