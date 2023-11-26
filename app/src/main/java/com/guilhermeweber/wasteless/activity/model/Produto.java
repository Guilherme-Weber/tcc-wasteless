package com.guilhermeweber.wasteless.activity.model;

import com.google.firebase.database.DatabaseReference;
import com.guilhermeweber.wasteless.activity.helper.ConfigFirebase;

public class Produto {

    private String idEmpresa;
    private String idProduto;
    private String nomeProduto;
    private String urlImagem;
    private String descricao;
    private Long preco;
    private String tipoPacote;
    private String idTipoPacote;
    private String tamanhoPacote;
    private String idTamanhoPacote;
    private String tempoDe;
    private String tempoAte;

    public Produto() {

    }

    public void salvar(String seed) {
        DatabaseReference firebaseRef = ConfigFirebase.getFirebase();
        DatabaseReference produtoRef = firebaseRef.child("produto").child("tudo").child(seed);
        DatabaseReference produtoRef2 = firebaseRef.child("produto").child(getIdEmpresa()).child(seed);
        produtoRef.setValue(this);
        produtoRef2.setValue(this);
    }

    public void remover(String seed) {
        DatabaseReference firebaseRef = ConfigFirebase.getFirebase();
        DatabaseReference produtoRef = firebaseRef.child("produto").child("tudo").child(seed);
        DatabaseReference produtoRef2 = firebaseRef.child("produto").child(getIdEmpresa()).child(seed);
        produtoRef.removeValue();
        produtoRef2.removeValue();

    }

    public String getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(String idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(String idProduto) {
        this.idProduto = idProduto;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getPreco() {
        return preco;
    }

    public void setPreco(Long preco) {
        this.preco = preco;
    }

    public String getTipoPacote() {
        return tipoPacote;
    }

    public void setTipoPacote(String tipoPacote) {
        this.tipoPacote = tipoPacote;
    }

    public String getIdTipoPacote() {
        return idTipoPacote;
    }

    public void setIdTipoPacote(String idTipoPacote) {
        this.idTipoPacote = idTipoPacote;
    }

    public String getTamanhoPacote() {
        return tamanhoPacote;
    }

    public void setTamanhoPacote(String tamanhoPacote) {
        this.tamanhoPacote = tamanhoPacote;
    }

    public String getIdTamanhoPacote() {
        return idTamanhoPacote;
    }

    public void setIdTamanhoPacote(String idTamanhoPacote) {
        this.idTamanhoPacote = idTamanhoPacote;
    }

    public String getTempoDe() {
        return tempoDe;
    }

    public void setTempoDe(String tempoDe) {
        this.tempoDe = tempoDe;
    }

    public String getTempoAte() {
        return tempoAte;
    }

    public void setTempoAte(String tempoAte) {
        this.tempoAte = tempoAte;
    }
}
