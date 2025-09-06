package com.josepacheco.tcc.model.remedio;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Laboratorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String marca; // nome usado comercialmente

    @Column(nullable = false, unique = true)
    private String nomeFantasia; // nome completo da empresa

    @OneToMany(mappedBy = "laboratorio", fetch = FetchType.LAZY)
    private List<Remedio> remedios = new ArrayList<>(); //OnetoMany

    public Laboratorio() {
    }

    public Laboratorio(String marca, String nomeFantasia) {
        this.marca = marca;
        this.nomeFantasia = nomeFantasia;
    }

    public Laboratorio(String marca, String nomeFantasia, List<Remedio> remedios) {
        this.marca = marca;
        this.nomeFantasia = nomeFantasia;
        this.remedios = remedios;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public List<Remedio> getRemedios() {
        return remedios;
    }

    public void setRemedios(List<Remedio> remedios) {
        this.remedios = remedios;
    }
}
