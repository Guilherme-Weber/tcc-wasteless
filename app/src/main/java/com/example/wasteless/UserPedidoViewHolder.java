package com.example.wasteless;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserPedidoViewHolder extends RecyclerView.ViewHolder {

    TextView titulo, coordenador, mensagem;

    public UserPedidoViewHolder(@NonNull View itemView) {
        super(itemView);
        titulo = itemView.findViewById(R.id.titulo_text);
        coordenador = itemView.findViewById(R.id.coordenador_text);
        mensagem = itemView.findViewById(R.id.mensagem_text);
    }
}
