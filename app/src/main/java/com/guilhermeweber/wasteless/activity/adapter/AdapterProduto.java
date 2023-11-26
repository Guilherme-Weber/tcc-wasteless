package com.guilhermeweber.wasteless.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.guilhermeweber.wasteless.R;
import com.guilhermeweber.wasteless.activity.model.Produto;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class AdapterProduto extends RecyclerView.Adapter<AdapterProduto.MyViewHolder> {
    private List<Produto> produtos;
    private Context context;

    public AdapterProduto(List<Produto> produtos, Context context) {
        this.produtos = produtos;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_produto, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Produto produto = produtos.get(position);

        String tempo = "Por Unidade";

        holder.nome.setText(produto.getNomeProduto());
        holder.descricao.setText(produto.getDescricao());
        holder.valor.setValue(produto.getPreco());
        holder.tipoValor.setText(produto.getTamanhoPacote());
        holder.tipoPeso.setText(produto.getTipoPacote());

        //recuperar imagem
        String urlFotos = produto.getUrlImagem();
        Picasso.get().load(urlFotos).into(holder.produtoImg);
    }

    @Override
    public int getItemCount() {
        return produtos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nome;
        TextView descricao;
        TextView tipoValor;
        TextView tipoPeso;
        CurrencyEditText valor;
        ImageView produtoImg;

        public MyViewHolder(View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.textNomeRefeicao);
            descricao = itemView.findViewById(R.id.textDescricaoRefeicao);
            tipoValor = itemView.findViewById(R.id.textViewTipoValor);
            valor = itemView.findViewById(R.id.textPrecoRefeicao);
            Locale locale = new Locale("pt", "BR");
            valor.setLocale(locale);
            produtoImg = itemView.findViewById(R.id.imageProdutoLista);

        }
    }
}
