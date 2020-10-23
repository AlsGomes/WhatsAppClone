package com.example.whatsappclone.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.R;
import com.example.whatsappclone.model.Usuario;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MembroGrupoAdapter extends RecyclerView.Adapter<MembroGrupoAdapter.MembroGrupoViewHolder> {

    List<Usuario> membros = new ArrayList<>();

    @NonNull
    @Override
    public MembroGrupoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_membros_grupo, parent, false);

        return new MembroGrupoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MembroGrupoViewHolder holder, int position) {
        if (membros.get(position).getFoto() != null) {
            Glide.with(holder.itemView.getContext()).load(membros.get(position).getFoto()).into(holder.imageMembro);
        }

        String[] nomes = membros.get(position).getNome().split(" ");
        holder.txtNomeMembro.setText(nomes[0]);
    }

    @Override
    public int getItemCount() {
        return membros.size();
    }

    public class MembroGrupoViewHolder extends RecyclerView.ViewHolder {

        private TextView txtNomeMembro;
        private CircleImageView imageMembro;
        private View itemView;

        public MembroGrupoViewHolder(@NonNull View itemView) {
            super(itemView);

            this.itemView = itemView;
            this.imageMembro = itemView.findViewById(R.id.imageMembro);
            this.txtNomeMembro = itemView.findViewById(R.id.txtNomeMembro);
        }
    }

    public List<Usuario> getMembros() {
        return this.membros;
    }

}
