package com.example.whatsappclone.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Conversa implements Serializable {

    private String idRemetente;
    private String idDestinatario;
    private String ultimaMensagem;
    private Usuario usuarioExibicao;

    public Conversa() {
    }

    public Conversa(String idRemetente, String idDestinatario, String ultimaMensagem, Usuario usuarioExibicao) {
        this.idRemetente = idRemetente;
        this.idDestinatario = idDestinatario;
        this.ultimaMensagem = ultimaMensagem;
        this.usuarioExibicao = usuarioExibicao;
    }

    @Exclude
    public String getIdRemetente() {
        return idRemetente;
    }

    public void setIdRemetente(String idRemetente) {
        this.idRemetente = idRemetente;
    }

    @Exclude
    public String getIdDestinatario() {
        return idDestinatario;
    }

    public void setIdDestinatario(String idDestinatario) {
        this.idDestinatario = idDestinatario;
    }

    public String getUltimaMensagem() {
        return ultimaMensagem;
    }

    public void setUltimaMensagem(String ultimaMensagem) {
        this.ultimaMensagem = ultimaMensagem;
    }

    public Usuario getUsuarioExibicao() {
        return usuarioExibicao;
    }

    public void setUsuarioExibicao(Usuario usuarioExibicao) {
        this.usuarioExibicao = usuarioExibicao;
    }

    @Override
    public String toString() {
        return "Conversa{" +
                "idRemetente='" + idRemetente + '\'' +
                ", idDestinatario='" + idDestinatario + '\'' +
                ", ultimaMensagem='" + ultimaMensagem + '\'' +
                ", usuarioExibicao=" + usuarioExibicao +
                '}';
    }
}
