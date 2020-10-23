package com.example.whatsappclone.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Conversa implements Serializable {

    private String idRemetente;
    private String idDestinatario;
    private String ultimaMensagem;
    private Usuario usuarioExibicao;
    private Boolean isGrupo;
    private Grupo grupo;

    public Conversa() {
    }

    public Conversa(String idRemetente, String idDestinatario, String ultimaMensagem, Usuario usuarioExibicao, Boolean isGrupo, Grupo grupo) {
        this.idRemetente = idRemetente;
        this.idDestinatario = idDestinatario;
        this.ultimaMensagem = ultimaMensagem;
        this.usuarioExibicao = usuarioExibicao;
        this.isGrupo = isGrupo;
        this.grupo = grupo;
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

    public Boolean getGrupo() {
        return isGrupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public void setGrupo(Boolean grupo) {
        isGrupo = grupo;
    }

    @Override
    public String toString() {
        return "Conversa{" +
                "idRemetente='" + idRemetente + '\'' +
                ", idDestinatario='" + idDestinatario + '\'' +
                ", ultimaMensagem='" + ultimaMensagem + '\'' +
                ", usuarioExibicao=" + usuarioExibicao +
                ", isGrupo=" + isGrupo +
                ", grupo=" + grupo +
                '}';
    }
}
