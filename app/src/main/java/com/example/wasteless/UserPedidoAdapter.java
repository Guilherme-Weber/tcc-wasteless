package com.example.wasteless;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wasteless.roomdatabase.PedidoEntity;
import com.example.wasteless.tela.TelaUserAnalisa;

import java.util.List;

public class UserPedidoAdapter extends RecyclerView.Adapter<UserPedidoViewHolder> {

    private String nome, email, curso; // Visualizar na tela
    private int horas;
    int pos;

    private Context context;
    private List<PedidoEntity> pedido;

    public UserPedidoAdapter(String nome, String email, String curso, int horas) {
        this.nome = nome;
        this.email = email;
        this.curso = curso;
        this.horas = horas;
    }

    public UserPedidoAdapter(List<PedidoEntity> pedido, Context context, String nome, String email, String curso, int horas) {
        this.pedido = pedido;
        this.context = context;
        this.nome = nome;
        this.email = email;
        this.curso = curso;
        this.horas = horas;
    }

    @NonNull
    @Override
    public UserPedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pedido_linha, parent, false);
        UserPedidoViewHolder viewHolder = new UserPedidoViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserPedidoViewHolder userPedidoViewHolder, int position) {
        //Contrutor que fez a boa!
        UserPedidoAdapter aluno = new UserPedidoAdapter(nome, email, curso, horas);
        nome = aluno.getNome();
        email = aluno.getEmail();
        curso = aluno.getCurso();
        horas = aluno.getHoras();

        PedidoEntity pedidoEntity = pedido.get(position);
        userPedidoViewHolder.titulo.setText(pedido.get(position).getTipo());
        userPedidoViewHolder.coordenador.setText(pedido.get(position).getCoordenaId());
        userPedidoViewHolder.mensagem.setText(pedido.get(position).getTexto());

        //FUNÇÕES DE CLICK
        userPedidoViewHolder.titulo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pos = userPedidoViewHolder.getAdapterPosition();//CONTA A POSITION

                //adicionar um check pra achar a posição do botão
                Intent intent = new Intent(userPedidoViewHolder.itemView.getContext(), TelaUserAnalisa.class)
                        .putExtra("nome", nome)
                        .putExtra("email", email)
                        .putExtra("curso", curso)
                        .putExtra("titulo", pedidoEntity.getTipo())
                        .putExtra("mensagem", pedidoEntity.getTexto())
                        .putExtra("emailc", pedidoEntity.getCoordenaId())
                        .putExtra("horas", horas);
                //COLOCAR PARAMETROS PARA PASAR DA TELA AQUI TIPO .putExtra
                userPedidoViewHolder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pedido.size();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public int getHoras() {
        return horas;
    }

    public void setHoras(int horas) {
        this.horas = horas;
    }
}

