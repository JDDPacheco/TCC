package com.josepacheco.tcc.config;

import com.josepacheco.tcc.model.produto.MedidaPadrao;
import com.josepacheco.tcc.model.produto.remedio.atributos.Apresentacao;
import com.josepacheco.tcc.model.produto.remedio.atributos.ControleReceita;
import com.josepacheco.tcc.model.produto.remedio.atributos.MedidaFarmaceutica;
import com.josepacheco.tcc.model.produto.remedio.formulacao.MedidaBasica;
import com.josepacheco.tcc.repository.produto.MedidaPadraoRepository;
import com.josepacheco.tcc.repository.produto.remedio.atributos.ApresentacaoRepository;
import com.josepacheco.tcc.repository.produto.remedio.atributos.ControleReceitaRepository;
import com.josepacheco.tcc.repository.produto.remedio.atributos.MedidaFarmaceuticaRepository;
import com.josepacheco.tcc.repository.produto.remedio.formulacao.MedidaBasicaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private ControleReceitaRepository controleReceitaRepo;

    @Autowired
    private MedidaBasicaRepository medidaBasicaRepo;

    @Autowired
    private MedidaFarmaceuticaRepository medidaFarmaceuticaRepo;

    @Autowired
    private ApresentacaoRepository apresentacaoRepo;

    @Autowired
    private MedidaPadraoRepository medidaPadraoRepo;

    @Override
    public void run(String... args) throws Exception {

        // --- Controle de Receita ---
        if (controleReceitaRepo.count() == 0) {
            controleReceitaRepo.save(new ControleReceita("isento"));
            controleReceitaRepo.save(new ControleReceita("sob_prescricao"));
            controleReceitaRepo.save(new ControleReceita("especial"));
        }

        // --- Medidas Básicas ---
        if (medidaBasicaRepo.count() == 0) {
            medidaBasicaRepo.save(new MedidaBasica("g", "grama"));
            medidaBasicaRepo.save(new MedidaBasica("mg", "miligrama"));
            medidaBasicaRepo.save(new MedidaBasica("mcg", "micrograma"));
            medidaBasicaRepo.save(new MedidaBasica("ng", "nanograma"));
            medidaBasicaRepo.save(new MedidaBasica("l", "litro"));
            medidaBasicaRepo.save(new MedidaBasica("ml", "mililitro"));
            medidaBasicaRepo.save(new MedidaBasica("UI", "Unidade Internacional"));
            medidaBasicaRepo.save(new MedidaBasica("%", "percentual"));
        }

        // --- Medidas Farmacêuticas ---
        if (medidaFarmaceuticaRepo.count() == 0) {
            medidaFarmaceuticaRepo.save(new MedidaFarmaceutica("CP", "Comprimido"));
            medidaFarmaceuticaRepo.save(new MedidaFarmaceutica("CR", "Comprimido Revestido"));
            medidaFarmaceuticaRepo.save(new MedidaFarmaceutica("CM", "Cápsula Mole"));
            medidaFarmaceuticaRepo.save(new MedidaFarmaceutica("CD", "Cápsula Dura"));
            medidaFarmaceuticaRepo.save(new MedidaFarmaceutica("DR", "Drágea"));
            medidaFarmaceuticaRepo.save(new MedidaFarmaceutica("DS", "Dose"));
            medidaFarmaceuticaRepo.save(new MedidaFarmaceutica("FR", "Frasco"));
            medidaFarmaceuticaRepo.save(new MedidaFarmaceutica("SC", "Sachê"));
            medidaFarmaceuticaRepo.save(new MedidaFarmaceutica("AM", "Ampola"));
            medidaFarmaceuticaRepo.save(new MedidaFarmaceutica("UN", "Unidade"));
            medidaFarmaceuticaRepo.save(new MedidaFarmaceutica("ML", "Mililitro"));
            medidaFarmaceuticaRepo.save(new MedidaFarmaceutica("L", "Litro"));
            medidaFarmaceuticaRepo.save(new MedidaFarmaceutica("GR", "Grama"));
            medidaFarmaceuticaRepo.save(new MedidaFarmaceutica("PC", "Pacote"));
        }

        // --- Apresentações ---
        if (apresentacaoRepo.count() == 0) {
            apresentacaoRepo.save(new Apresentacao("CP", "Comprimido"));
            apresentacaoRepo.save(new Apresentacao("CR", "Comprimido Revestido"));
            apresentacaoRepo.save(new Apresentacao("LP", "Comprimido Revestido de Liberação Prolongada"));
            apresentacaoRepo.save(new Apresentacao("CL", "Cápsula Mole"));
            apresentacaoRepo.save(new Apresentacao("CD", "Cápsula Dura"));
            apresentacaoRepo.save(new Apresentacao("DR", "Drágea"));
            apresentacaoRepo.save(new Apresentacao("GE", "Gel"));
            apresentacaoRepo.save(new Apresentacao("SP", "Spray"));
            apresentacaoRepo.save(new Apresentacao("SN", "Spray nasal"));
            apresentacaoRepo.save(new Apresentacao("PO", "Pomada"));
            apresentacaoRepo.save(new Apresentacao("CM", "Creme"));
            apresentacaoRepo.save(new Apresentacao("SO", "Solução"));
            apresentacaoRepo.save(new Apresentacao("XA", "Xarope"));
            apresentacaoRepo.save(new Apresentacao("AM", "Ampola"));
            apresentacaoRepo.save(new Apresentacao("SU", "Supositório"));
            apresentacaoRepo.save(new Apresentacao("IN", "Injetável"));
            apresentacaoRepo.save(new Apresentacao("FR", "Frasco"));
            apresentacaoRepo.save(new Apresentacao("SC", "Sachê"));
            apresentacaoRepo.save(new Apresentacao("PC", "Pacote"));
        }

        // --- Medidas Padrão ---
        if (medidaPadraoRepo.count() == 0) {
            medidaPadraoRepo.save(new MedidaPadrao("CX", "Caixa"));
            medidaPadraoRepo.save(new MedidaPadrao("VD", "Vidro"));
            medidaPadraoRepo.save(new MedidaPadrao("EV", "Envelope"));
            medidaPadraoRepo.save(new MedidaPadrao("BG", "Bisnaga"));
            medidaPadraoRepo.save(new MedidaPadrao("GF", "Garrafa"));
            medidaPadraoRepo.save(new MedidaPadrao("UN", "Unidade"));
            medidaPadraoRepo.save(new MedidaPadrao("M", "Metro"));
            medidaPadraoRepo.save(new MedidaPadrao("KG", "Kilograma"));
            medidaPadraoRepo.save(new MedidaPadrao("L", "Litro"));
            medidaPadraoRepo.save(new MedidaPadrao("DZ", "Dúzia"));
            medidaPadraoRepo.save(new MedidaPadrao("PT", "Pacote"));
        }

    }
}
