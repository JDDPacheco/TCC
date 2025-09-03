package com.josepacheco.tcc.model.remedio;

import java.util.ArrayList;
import java.util.List;

public class Laboratorio {
    private Long id;
    private String marca; // nome usado comercialmente
    private String nomeFantasia; // nome completo da empresa
    private List<Remedio> remedios = new ArrayList<>(); //OnetoMany
}
