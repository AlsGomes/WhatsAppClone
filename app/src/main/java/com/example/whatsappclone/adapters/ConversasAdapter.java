package com.example.whatsappclone.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.R;
import com.example.whatsappclone.model.Conversa;
import com.example.whatsappclone.model.Usuario;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversasAdapter extends RecyclerView.Adapter<ConversasAdapter.ConversasViewHolder> {

    private List<Conversa> conversas = new ArrayList<>();
    private Fragment fragment;

    public ConversasAdapter(Fragment fragment) {
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ConversasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_conversas, parent, false);
        return new ConversasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversasViewHolder holder, int position) {
        Usuario usuarioExibicao = getConversas().get(position).getUsuarioExibicao();
        holder.txtNome.setText(usuarioExibicao.getNomeExibicao());
        holder.txtUltimaMensagem.setText(getConversas().get(position).getUltimaMensagem());
        if (usuarioExibicao.getFoto() != null) {
            Glide.with(fragment.getActivity()).load(usuarioExibicao.getFoto()).into(holder.imagePerfil);
        } else {
            holder.imagePerfil.setImageResource(R.drawable.padrao);
        }
    }

    @Override
    public int getItemCount() {
        return getConversas().size();
    }

    public class ConversasViewHolder extends RecyclerView.ViewHolder {

        private TextView txtNome;
        private TextView txtUltimaMensagem;
        private CircleImageView imagePerfil;

        public ConversasViewHolder(@NonNull View itemView) {
            super(itemView);

            txtNome = itemView.findViewById(R.id.txtNomeMembro);
            txtUltimaMensagem = itemView.findViewById(R.id.txtUltimaMensagem);
            imagePerfil = itemView.findViewById(R.id.imagePerfil);
        }
    }

    public List<Conversa> getConversas() {
        return conversas;
    }
}
