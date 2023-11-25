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
import com.guilhermeweber.wasteless.activity.model.ItemPedido;
import com.guilhermeweber.wasteless.activity.model.Produto;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterCarrinho extends RecyclerView.Adapter<AdapterCarrinho.MyViewHolder> {
    private List<ItemPedido> produtos;
    private Context context;

    public AdapterCarrinho(List<ItemPedido> produtos, Context context) {
        this.produtos = produtos;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_carrinho, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ItemPedido produto = produtos.get(position);

        String tempo = "Por Unidade";

        holder.nomeProduto.setText(produto.getNomeProduto());
        holder.quantidade.setText(produto.getQuantidade());

        Long l = new Long(String.valueOf(produto.getPreco()));

        holder.preco.setValue(l);

        //recuperar imagem
        String urlFotos = produto.getUrlImagem();
//        Picasso.get().load(urlFotos).into(holder.produtoImg);
    }

    @Override
    public int getItemCount() {
        return produtos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nomeProduto;
        TextView quantidade;
        CurrencyEditText preco;

        public MyViewHolder(View itemView) {
            super(itemView);

            nomeProduto = itemView.findViewById(R.id.textNomeItem);
            quantidade = itemView.findViewById(R.id.textQuantidade);
            preco = itemView.findViewById(R.id.textPrecoCarrinho);

        }
    }
}
