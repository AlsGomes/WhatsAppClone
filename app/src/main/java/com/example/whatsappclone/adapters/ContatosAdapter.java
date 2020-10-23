package com.example.whatsappclone.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.R;
import com.example.whatsappclone.model.Usuario;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContatosAdapter extends RecyclerView.Adapter<ContatosAdapter.ContatosViewHolder> {

    private List<Usuario> contatos = new ArrayList<>();
    private Fragment fragment;
    private Activity activity;

    public ContatosAdapter(Fragment fragment) {
        this.fragment = fragment;
    }

    public ContatosAdapter(Activity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public ContatosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_contatos, parent, false);
        return new ContatosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContatosViewHolder holder, int position) {
        holder.txtNome.setText(getContatos().get(position).getNome());
        holder.txtEmail.setText(getContatos().get(position).getEmail());
        if (getContatos().get(position).getFoto() != null) {
            if (fragment != null) {
                Glide.with(fragment.getActivity()).load(getContatos().get(position).getFoto()).into(holder.imagePerfil);
            } else {
                Glide.with(activity).load(getContatos().get(position).getFoto()).into(holder.imagePerfil);
            }
        } else {
            holder.imagePerfil.setImageResource(R.drawable.padrao);
        }
    }

    @Override
    public int getItemCount() {
        return getContatos().size();
    }

    public class ContatosViewHolder extends RecyclerView.ViewHolder {

        private TextView txtNome;
        private TextView txtEmail;
        private CircleImageView imagePerfil;

        public ContatosViewHolder(@NonNull View itemView) {
            super(itemView);

            txtNome = itemView.findViewById(R.id.txtNomeMembro);
            txtEmail = itemView.findViewById(R.id.txtUltimaMensagem);
            imagePerfil = itemView.findViewById(R.id.imagePerfil);
        }
    }

    public List<Usuario> getContatos() {
        return contatos;
    }
}
