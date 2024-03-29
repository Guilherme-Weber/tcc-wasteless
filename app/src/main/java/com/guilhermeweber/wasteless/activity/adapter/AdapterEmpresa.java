package com.guilhermeweber.wasteless.activity.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.guilhermeweber.wasteless.R;
import com.guilhermeweber.wasteless.activity.model.Empresa;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterEmpresa extends RecyclerView.Adapter<AdapterEmpresa.MyViewHolder> {

    private List<Empresa> empresas;
    private Context context;

    public AdapterEmpresa(List<Empresa> empresas, Context context) {
        this.empresas = empresas;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_empresa, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Empresa empresa = empresas.get(position);

        holder.nomeEmpresa.setText(empresa.getNome());

        if (!(empresa.getCategoria() == null)) {
            holder.categoria.setText(empresa.getCategoria());
        } else {
            holder.categoria.setText("Empresa");
        }

        //calculo de media
        double totalNum = empresa.getTotalAnalise();
        double totalAva = empresa.getTotalStrela();
        double average = 0;

        if (totalNum == 0) {
            if (totalAva == 0) {
            } else {
                average = totalNum / totalAva;
            }
        } else {
            average = totalNum / totalAva;
        }

        holder.textAnalize.setText(String.format("%.1f", average));

        average = average * 10;
        int averageInt = (int) average;
        holder.progressBar.setProgress(averageInt);

        int color_int = ContextCompat.getColor(context, R.color.secundaria);
        holder.progressBar.getProgressDrawable().setColorFilter(color_int, android.graphics.PorterDuff.Mode.SRC_IN);

        holder.tempo.setText(empresa.getBairro());

        //recuperar imagem
        String urlFotos = empresa.getUrlImagem();
        Picasso.get().load(urlFotos).into(holder.imagemEmpresa);
    }

    @Override
    public int getItemCount() {
        return empresas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imagemEmpresa;
        TextView nomeEmpresa;
        TextView categoria;
        TextView textTotalAnalise;
        TextView textAnalize;
        TextView tempo;
        ProgressBar progressBar;

        public MyViewHolder(View itemView) {
            super(itemView);

            textAnalize = itemView.findViewById(R.id.textAnalize);
            nomeEmpresa = itemView.findViewById(R.id.textNomeEmpresa);
            categoria = itemView.findViewById(R.id.textCategoriaEmpresa);
            tempo = itemView.findViewById(R.id.textTempoEmpresa);
            imagemEmpresa = itemView.findViewById(R.id.imageEmpresa);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}

