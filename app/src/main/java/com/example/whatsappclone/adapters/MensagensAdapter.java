package com.example.whatsappclone.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.R;
import com.example.whatsappclone.database.FirebaseManager;
import com.example.whatsappclone.model.Mensagem;
import com.example.whatsappclone.util.Base64Custom;

import java.util.ArrayList;
import java.util.List;

public class MensagensAdapter extends RecyclerView.Adapter<MensagensAdapter.MensagensViewHolder> {

    private List<Mensagem> mensagens = new ArrayList<>();
    private Activity activity;

    private static final int REMETENTE = 0;
    private static final int DESTINATARIO = 1;

    public MensagensAdapter(Activity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public MensagensViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;

        switch (viewType) {
            case REMETENTE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_mensagens_remetente, parent, false);
                break;
            case DESTINATARIO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_mensagens_destinatario, parent, false);
                break;
            default:
                throw new IllegalArgumentException("viewType não corresponde a nenhuma das constantes");
        }

        return new MensagensViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MensagensViewHolder holder, int position) {
        if (getMensagens().get(position).getImagem() != null) {
            holder.txtMensagem.setVisibility(View.GONE);
            Glide.with(activity).load(getMensagens().get(position).getImagem()).into(holder.imagem);
        } else {
            holder.imagem.setVisibility(View.GONE);
            holder.txtMensagem.setText(getMensagens().get(position).getMensagem());
        }
    }

    @Override
    public int getItemCount() {
        return getMensagens().size();
    }

    @Override
    public int getItemViewType(int position) {
        if (getMensagens().get(position).getId().equals(Base64Custom.encode(FirebaseManager.getFirebaseAuth().getCurrentUser().getEmail()))) {
            return REMETENTE;
        } else {
            return DESTINATARIO;
        }
    }

    public class MensagensViewHolder extends RecyclerView.ViewHolder {

        private ImageView imagem;
        private TextView txtMensagem;

        public MensagensViewHolder(@NonNull View itemView) {
            super(itemView);

            imagem = itemView.findViewById(R.id.imagem);
            txtMensagem = itemView.findViewById(R.id.txtMensagem);
        }
    }

    public List<Mensagem> getMensagens() {
        return mensagens;
    }
}
