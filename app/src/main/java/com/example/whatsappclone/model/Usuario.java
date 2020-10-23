package com.example.whatsappclone.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Usuario implements Serializable {

    private String id;
    private String nome;
    private String email;
    private String senha;
    private String nomeExibicao;
    private String foto;

    public Usuario() {
    }

    public Usuario(String id, String nome, String email, String senha, String nomeExibicao, String foto) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.nomeExibicao = nomeExibicao;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNomeExibicao() {
        return nomeExibicao;
    }

    public void setNomeExibicao(String nomeExibicao) {
        this.nomeExibicao = nomeExibicao;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id='" + id + '\'' +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", senha='" + senha + '\'' +
                ", nomeExibicao='" + nomeExibicao + '\'' +
                ", foto='" + foto + '\'' +
                '}';
    }
}
