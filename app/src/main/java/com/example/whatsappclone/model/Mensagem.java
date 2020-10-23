package com.example.whatsappclone.model;

import java.io.Serializable;

public class Mensagem implements Serializable {

    private String id;
    private String mensagem;
    private String imagem;

    public Mensagem() {
    }

    public Mensagem(String id, String mensagem, String imagem) {
        this.id = id;
        this.mensagem = mensagem;
        this.imagem = imagem;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }
}
