package com.guilhermeweber.wasteless.activity.model;

import com.google.firebase.database.DatabaseReference;
import com.guilhermeweber.wasteless.activity.helper.ConfigFirebase;

public class Empresa {

    private String idEmpresa;
    private String urlImagem;
    private String nome;
    private String tempo;
    private Integer categoria;
    private String Telefone;
    private Long precoEntrega;

    public Empresa() {
    }

    public void salvar() {
        DatabaseReference firebaseRef = ConfigFirebase.getFirebase();
        DatabaseReference empresaRef = firebaseRef.child("empresa").child(getIdEmpresa());
        empresaRef.setValue(this);
    }


    public String getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(String idEmpresa) {
        this.idEmpresa = idEmpresa;
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

    public String getTempo() {
        return tempo;
    }

    public void setTempo(String tempo) {
        this.tempo = tempo;
    }


    public String getTelefone() {
        return Telefone;
    }

    public void setTelefone(String telefone) {
        Telefone = telefone;
    }

    public Long getPrecoEntrega() {
        return precoEntrega;
    }

    public void setPrecoEntrega(Long precoEntrega) {
        this.precoEntrega = precoEntrega;
    }

    public Integer getCategoria() {
        return categoria;
    }

    public void setCategoria(Integer categoria) {
        this.categoria = categoria;
    }
}
