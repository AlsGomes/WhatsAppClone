package com.example.whatsappclone.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Grupo implements Serializable {

    private String id;
    private String nome;
    private String foto;
    private List<Usuario> membros = new ArrayList<>();

    public Grupo() {
    }

    public Grupo(String id, String nome, String foto) {
        this.id = id;
        this.nome = nome;
        this.foto = foto;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public List<Usuario> getMembros() {
        return membros;
    }
}
