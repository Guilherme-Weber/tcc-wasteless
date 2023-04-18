package com.example.wasteless;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StorePedidoViewHolder extends RecyclerView.ViewHolder {

    TextView titulo, aluno, mensagem;

    public StorePedidoViewHolder(@NonNull View itemView) {
        super(itemView);
        titulo = itemView.findViewById(R.id.tituloc_text);
        aluno = itemView.findViewById(R.id.aluno_text);
        mensagem = itemView.findViewById(R.id.mensagemc_text);
    }
}
