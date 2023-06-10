package com.guilhermeweber.wasteless.activity.model;

import com.google.firebase.database.DatabaseReference;
import com.guilhermeweber.wasteless.activity.helper.ConfigFirebase;

public class Produto {

    private String idEmpresa;
    private String idProduto;
    private String nomeProduto;
    private String urlImagem;
    private String descricao;

    private String categoria;
    private Integer idCategoria;
    private Long preco;
    private String TipoValor;
    private String TipoPeso;

    public Produto() {

    }

    public void salvar(String seed) {
        DatabaseReference firebaseRef = ConfigFirebase.getFirebase();
        DatabaseReference produtoRef = firebaseRef.child("produto").child(getIdEmpresa()).child(seed);
        produtoRef.setValue(this);
    }

    public void remover(String seed) {
        DatabaseReference firebaseRef = ConfigFirebase.getFirebase();
        DatabaseReference produtoRef = firebaseRef.child("produto").child(getIdEmpresa()).child(seed);
        produtoRef.removeValue();

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
