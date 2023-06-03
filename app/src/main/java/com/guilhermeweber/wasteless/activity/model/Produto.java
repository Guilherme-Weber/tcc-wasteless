package com.guilhermeweber.wasteless.activity.model;

import com.google.firebase.database.DatabaseReference;
import com.guilhermeweber.wasteless.activity.helper.ConfigFirebase;

public class Produto {

    private String idProduto;
    private String nomeProduto;
    private String urlImagem;
    private String descricao;
    private Integer categoria;
    private Long preco;
    private String TipoValor;
    private String TipoPeso;

    public Produto() {
    }

    public void salvar() {
        DatabaseReference firebaseRef = ConfigFirebase.getFirebase();
        DatabaseReference empresaRef = firebaseRef.child("produto").child(getIdProduto()).push();
        empresaRef.setValue(this);
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

    public Integer getCategoria() {
        return categoria;
    }

    public void setCategoria(Integer categoria) {
        this.categoria = categoria;
    }

    public Long getPreco() {
        return preco;
    }

    public void setPreco(Long preco) {
        this.preco = preco;
    }

    public String getTipoValor() {
        return TipoValor;
    }

    public void setTipoValor(String tipoValor) {
        TipoValor = tipoValor;
    }

    public String getTipoPeso() {
        return TipoPeso;
    }

    public void setTipoPeso(String tipoPeso) {
        TipoPeso = tipoPeso;
    }
}
