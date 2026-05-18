package com.example.dam_ac2;

public class Livro {
    private String id;
    private String titulo;
    private String autor;
    private String genero;
    private int publicacao;
    private String status_leitura;
    private Boolean favorito;

    public Livro() {}

    public Livro(String id, String titulo, String autor, String genero, int publicacao, String status_leitura, Boolean favorito) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.genero = genero;
        this.publicacao = publicacao;
        this.status_leitura = status_leitura;
        this.favorito = favorito;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public int getPublicacao() {
        return publicacao;
    }

    public void setPublicacao(int publicacao) {
        this.publicacao = publicacao;
    }

    public String getStatus_leitura() {
        return status_leitura;
    }

    public void setStatus_leitura(String status_leitura) {
        this.status_leitura = status_leitura;
    }

    public Boolean getFavorito() {
        return favorito;
    }

    public void setFavorito(Boolean favorito) {
        this.favorito = favorito;
    }
}
