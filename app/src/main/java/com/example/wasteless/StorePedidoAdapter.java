package com.example.wasteless;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wasteless.roomdatabase.PedidoEntity;
import com.example.wasteless.tela.TelaStoreAnalisa;

import java.util.List;

public class StorePedidoAdapter extends RecyclerView.Adapter<StorePedidoViewHolder> {

    String email;

    private Context context;
    private List<PedidoEntity> pedido;

    public StorePedidoAdapter(String email) {
        this.email = email;
    }

    public StorePedidoAdapter(Context context, List<PedidoEntity> pedido, String email) {
        this.context = context;
        this.pedido = pedido;
        this.email = email;
    }

    @NonNull
    @Override
    public StorePedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pedido_store_linha, parent, false);
        StorePedidoViewHolder viewHolder = new StorePedidoViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StorePedidoViewHolder storePedidoViewHolder, int position) {
        StorePedidoAdapter coordenador = new StorePedidoAdapter(email);
        email = coordenador.getEmail();

        PedidoEntity pedidoEntity = pedido.get(position);
        storePedidoViewHolder.titulo.setText(pedido.get(position).getTipo());
        storePedidoViewHolder.aluno.setText(pedido.get(position).getAlunoId());
        storePedidoViewHolder.mensagem.setText(pedido.get(position).getTexto());
        //FUNÇÕES DE CLICK
        storePedidoViewHolder.titulo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(storePedidoViewHolder.itemView.getContext(), TelaStoreAnalisa.class)
                        .putExtra("email", email)
                        .putExtra("email_aluno", pedidoEntity.getAlunoId())
                        .putExtra("titulo", pedidoEntity.getTipo())
                        .putExtra("curso", pedidoEntity.getCurso())
                        .putExtra("nome", pedidoEntity.getAlunoNome())
                        .putExtra("mensagem", pedidoEntity.getTexto());
                //COLOCAR PARAMETROS PARA PASAR DA TELA AQUI TIPO .putExtra
                storePedidoViewHolder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pedido.size();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

