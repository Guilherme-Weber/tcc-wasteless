package com.guilhermeweber.wasteless.activity.adapter;

import android.content.ClipData;
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
import com.guilhermeweber.wasteless.activity.activity.CarrinhoActivity;
import com.guilhermeweber.wasteless.activity.model.ItemPedido;
import com.guilhermeweber.wasteless.activity.model.Pedido;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class AdapterCarrinho extends RecyclerView.Adapter<AdapterCarrinho.MyViewHolder> {
    private List<Pedido> pedidos;

    public AdapterCarrinho(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    @NonNull
    @Override
    public AdapterCarrinho.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_pedidos, parent, false);
        return new AdapterCarrinho.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterCarrinho.MyViewHolder holder, int i) {

        Pedido pedido = pedidos.get(i);

        holder.nome.setText(pedido.getNome());
        holder.endereco.setText("Endereço: " + pedido.getEndereco());
        holder.observacao.setText("Obs: " + pedido.getObservacao());

        List<ItemPedido> itens = new ArrayList<>();
        itens = pedido.getItens();
        String descricaoItens = "";

        int numeroItem = 1;
        Double total = 0.0;
        for (ItemPedido itemPedido : itens) {

            int qtde = itemPedido.getQuantidade();
            Double preco = itemPedido.getPreco();
            preco = preco / 100;
            total += (qtde * preco);

            String nome = itemPedido.getNomeProduto();
            descricaoItens += numeroItem + ") " + nome + " / (" + qtde + " x R$ " + preco + ") \n";
            numeroItem++;
        }

        descricaoItens += "Total: R$ " + total;
        holder.itens.setText(descricaoItens);

        int metodoPagamento = pedido.getMetodoPagamento();
        String pagamento = null;

        if (metodoPagamento == 0) {
            pagamento = "Dinheiro";
        } else if (metodoPagamento == 1) {
            pagamento = "PIX";
        } else if (metodoPagamento == 2) {
            pagamento = "Máquina de Cartão";
        } else {
            pagamento = "Erro";
        }

        holder.pgto.setText("pgto: " + pagamento);
    }

    @Override
    public int getItemCount() {
        return pedidos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nome;
        TextView endereco;
        TextView pgto;
        TextView observacao;
        TextView itens;

        public MyViewHolder(View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.textPedidoNome);
            endereco = itemView.findViewById(R.id.textPedidoEndereco);
            pgto = itemView.findViewById(R.id.textPedidoPgto);
            observacao = itemView.findViewById(R.id.textPedidoObs);
            itens = itemView.findViewById(R.id.textPedidoItens);
        }
    }
}
