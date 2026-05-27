import { useState, useEffect } from 'react';
import api from '../services/api';
import { toast } from 'react-toastify';
import Select from 'react-select';

import Laboratorios from './Laboratorios'; 
import Formulas from './Formulas';

export default function CadastroProduto() {
  const [categoriaTela, setCategoriaTela] = useState('remedio');
  
  // ESTADOS DOS DADOS DA API
  const [laboratorios, setLaboratorios] = useState([]);
  const [formulas, setFormulas] = useState([]);
  const [apresentacoes, setApresentacoes] = useState([]);
  const [medidasFarmaceuticas, setMedidasFarmaceuticas] = useState([]); 
  const [medidasBasicas, setMedidasBasicas] = useState([]);             
  const [medidasPadrao, setMedidasPadrao] = useState([]); // NOVO: Para Caixa, Unidade, etc.

  // ESTADOS DOS MODAIS
  const [modalLabAberto, setModalLabAberto] = useState(false);
  const [modalFormulaAberto, setModalFormulaAberto] = useState(false);

  // ESTADO DO FORMULÁRIO COMPATÍVEL COM O DTO DA SUPERCLASSE
  const [produto, setProduto] = useState({
    nome: '', 
    ean: '', 
    unidadeDeMedida: '',      // NOVO: Exigido pela superclasse
    idLaboratorio: '', 
    idFormula: '',
    tipoControle: 'isento', 
    siglaApresentacao: '', 
    quantidadeDoses: '',      
    siglaMedidaDoses: '',     
    conteudo: '',             
    siglaMedidaConteudo: '',  
    pesoLiquido: '',          
    generico: false 
  });

  const carregarLaboratorios = () => {
    api.get('/api/produto/remedio/laboratorio').then(res => setLaboratorios(res.data)).catch(console.error);
  };

  const carregarFormulas = () => {
    api.get('/api/produto/remedio/formulacao/formula').then(res => setFormulas(res.data)).catch(console.error);
  };

  useEffect(() => {
    carregarLaboratorios();
    carregarFormulas();
    api.get('/api/produto/remedio/apresentacao').then(res => setApresentacoes(res.data)).catch(console.error);
    api.get('/api/produto/remedio/medida_farmaceutica').then(res => setMedidasFarmaceuticas(res.data)).catch(console.error);
    api.get('/api/produto/remedio/formulacao/medida_basica').then(res => setMedidasBasicas(res.data)).catch(console.error);
    
    // NOVO: Buscando a unidade de medida padrão da superclasse
    api.get('/api/produto/medida_padrao').then(res => setMedidasPadrao(res.data)).catch(console.error);
  }, []);

  const optionsLaboratorio = laboratorios.map(lab => ({ value: lab.id, label: lab.marca }));
  const optionsFormula = formulas.map(form => ({ value: form.id, label: form.composicoes ? form.composicoes.join(" + ") : `Fórmula ID ${form.id}` }));
  const optionsApresentacao = apresentacoes.map(apr => ({ value: apr.sigla, label: `${apr.sigla} - ${apr.apresentacao}` }));
  const optionsMedidaFarmaceutica = medidasFarmaceuticas.map(med => ({ value: med.sigla, label: `${med.sigla} - ${med.descricao}` }));
  const optionsMedidaBasica = medidasBasicas.map(med => ({ value: med.sigla, label: `${med.sigla} - ${med.nome}` }));
  
  // Assumindo que a medida padrão também retorna sigla e nome. Se retornar apenas string, ajuste para value: med, label: med
  const optionsMedidaPadrao = medidasPadrao.map(med => ({ value: med.sigla, label: `${med.sigla} - ${med.descricao}`}));

  const formulaSelecionadaObj = formulas.find(f => f.id === Number(produto.idFormula));
  const textoFormulaSelecionada = formulaSelecionadaObj?.composicoes ? formulaSelecionadaObj.composicoes.join(" + ") : "";

  const handleInputChange = (event) => {
    const { name, value } = event.target;
    setProduto({ ...produto, [name]: value });
  };

  const handleSelectChange = (selectedOption, actionMeta) => {
    const fieldName = actionMeta.name;
    const value = selectedOption ? selectedOption.value : '';
    setProduto({ ...produto, [fieldName]: value });
  };

  const cadastrarProduto = (event) => {
    event.preventDefault();

    if (categoriaTela === 'remedio') {
      const nomeFinal = produto.generico ? textoFormulaSelecionada : produto.nome;
      
      const payloadRemedio = {
        // SUPERCLASSE
        nomeComercial: nomeFinal, // CORRIGIDO: nome -> nomeComercial
        ean: produto.ean,
        unidadeDeMedida: produto.unidadeDeMedida, // NOVO
        tipoProduto: produto.generico ? "generico" : "remedio", // NOVO: Regra de negócio aplicada

        // SUBCLASSE (RemedioInputDTO)
        idLaboratorio: Number(produto.idLaboratorio),
        idFormula: Number(produto.idFormula),
        tipoControle: produto.tipoControle,
        siglaApresentacao: produto.siglaApresentacao,
        quantidadeDoses: produto.quantidadeDoses ? Number(produto.quantidadeDoses) : 0,
        siglaMedidaDoses: produto.siglaMedidaDoses || null,
        conteudo: produto.conteudo ? Number(produto.conteudo) : 0,
        siglaMedidaConteudo: produto.siglaMedidaConteudo || null,
        pesoLiquido: produto.pesoLiquido ? Number(produto.pesoLiquido) : 0
      };

      api.post('/api/produto', payloadRemedio)
        .then(() => limparFormularioComSucesso("Medicamento"))
        .catch(lidarComErroBackend);
    } else {
      const payloadGeral = { 
        nomeComercial: produto.nome, 
        ean: produto.ean,
        unidadeDeMedida: produto.unidadeDeMedida,
        tipoProduto: "geral"
      };
      api.post('/api/produto/geral', payloadGeral)
        .then(() => limparFormularioComSucesso("Produto Geral"))
        .catch(lidarComErroBackend);
    }
  };

  const limparFormularioComSucesso = (tipo) => {
    toast.success(`${tipo} cadastrado com sucesso!`);
    setProduto({
      nome: '', ean: '', unidadeDeMedida: '', idLaboratorio: '', idFormula: '', 
      tipoControle: 'isento', siglaApresentacao: '', quantidadeDoses: '',
      siglaMedidaDoses: '', conteudo: '', siglaMedidaConteudo: '',
      pesoLiquido: '', generico: false
    });
    setCategoriaTela('remedio'); 
  };

  const lidarComErroBackend = (error) => {
    const msg = error.response?.data?.message || error.response?.data || "Erro desconhecido";
    toast.error("Erro ao cadastrar. Motivo: " + JSON.stringify(msg));
  };

  return (
    <div>
      <h2>Cadastro de Produto</h2>

      <div style={{ border: '1px solid #ccc', padding: '20px', marginTop: '20px', borderRadius: '5px', maxWidth: '850px', backgroundColor: '#fff' }}>
        
        <div style={{ display: 'flex', gap: '20px', marginBottom: '20px', padding: '10px', backgroundColor: '#f4f4f4', borderRadius: '4px' }}>
          <label style={{ fontWeight: 'bold', cursor: 'pointer' }}>
            <input type="radio" value="remedio" checked={categoriaTela === 'remedio'} onChange={(e) => setCategoriaTela(e.target.value)} style={{ marginRight: '5px' }} />
            💊 Medicamento
          </label>
          <label style={{ fontWeight: 'bold', cursor: 'pointer' }}>
            <input type="radio" value="geral" checked={categoriaTela === 'geral'} onChange={(e) => setCategoriaTela(e.target.value)} style={{ marginRight: '5px' }} />
            📦 Produto Geral
          </label>
        </div>

        <form onSubmit={cadastrarProduto} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
          
          {/* IDENTIFICAÇÃO GLOBAL (SUPERCLASSE) */}
          <div style={{ display: 'flex', gap: '10px' }}>
            <div style={{ flex: 2 }}>
              <label>EAN (Código de Barras):</label>
              <input type="text" name="ean" value={produto.ean} onChange={handleInputChange} required style={{ width: '100%', padding: '8px', marginTop: '5px', boxSizing: 'border-box' }} />
            </div>
            
            {/* NOVO: Unidade de Medida Padrão da Superclasse */}
            <div style={{ flex: 2 }}>
              <label>Unidade Padrão:</label>
              <div style={{ marginTop: '5px' }}>
                <Select name="unidadeDeMedida" options={optionsMedidaPadrao} value={optionsMedidaPadrao.find(opt => opt.value === produto.unidadeDeMedida) || null} onChange={handleSelectChange} placeholder="Ex: CX, UN..." isClearable required />
              </div>
            </div>

            <div style={{ flex: 3 }}>
              <label>Nome do Produto:</label>
              <input 
                type="text" 
                name="nome" 
                value={categoriaTela === 'remedio' && produto.generico ? textoFormulaSelecionada : produto.nome} 
                onChange={handleInputChange} 
                required={!(categoriaTela === 'remedio' && produto.generico)} 
                disabled={categoriaTela === 'remedio' && produto.generico}
                placeholder={categoriaTela === 'remedio' && produto.generico ? "O nome será gerado pela fórmula" : "Ex: Amoxil"}
                style={{ width: '100%', padding: '8px', marginTop: '5px', backgroundColor: (categoriaTela === 'remedio' && produto.generico) ? '#e9ecef' : '#fff', fontStyle: (categoriaTela === 'remedio' && produto.generico) ? 'italic' : 'normal', boxSizing: 'border-box' }} 
              />
            </div>
          </div>

          {categoriaTela === 'remedio' && (
            <div style={{ padding: '15px', border: '1px solid #cce5ff', backgroundColor: '#e6f2ff', borderRadius: '4px', display: 'flex', flexDirection: 'column', gap: '15px' }}>
              
              <div>
                <label style={{ fontWeight: 'bold', cursor: 'pointer', color: '#004085' }}>
                  <input type="checkbox" name="generico" checked={produto.generico} onChange={(e) => setProduto({ ...produto, generico: e.target.checked })} />
                  {' '} Este medicamento é Genérico
                </label>
              </div>

              <div style={{ display: 'flex', gap: '15px' }}>
                <div style={{ flex: 1 }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '5px' }}>
                    <label>Laboratório:</label>
                    <button type="button" onClick={() => setModalLabAberto(true)} style={{ padding: '2px 8px', fontSize: '0.8em', backgroundColor: '#17a2b8', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>+ Novo</button>
                  </div>
                  <Select name="idLaboratorio" options={optionsLaboratorio} value={optionsLaboratorio.find(opt => opt.value === produto.idLaboratorio) || null} onChange={handleSelectChange} placeholder="Buscar fabricante..." isClearable required />
                </div>
                
                <div style={{ flex: 2 }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '5px' }}>
                    <label>Fórmula Base:</label>
                    <button type="button" onClick={() => setModalFormulaAberto(true)} style={{ padding: '2px 8px', fontSize: '0.8em', backgroundColor: '#17a2b8', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>+ Nova Fórmula</button>
                  </div>
                  <Select name="idFormula" options={optionsFormula} value={optionsFormula.find(opt => opt.value === produto.idFormula) || null} onChange={handleSelectChange} placeholder="Buscar fórmula..." isClearable required />
                </div>
              </div>

              <div style={{ display: 'flex', gap: '15px' }}>
                <div style={{ flex: 1 }}>
                  <label>Tipo de Controle:</label>
                  <select name="tipoControle" value={produto.tipoControle} onChange={handleInputChange} required style={{ width: '100%', padding: '8px', marginTop: '5px', borderRadius: '4px', border: '1px solid #ccc', boxSizing: 'border-box' }}>
                    <option value="isento">Isento de Receita</option>
                    <option value="sob_prescricao">Venda Sob Prescrição Médica</option>
                    <option value="especial">Controle Especial</option>
                  </select>
                </div>

                <div style={{ flex: 1 }}>
                  <label>Apresentação:</label>
                  <div style={{ marginTop: '5px' }}>
                    <Select name="siglaApresentacao" options={optionsApresentacao} value={optionsApresentacao.find(opt => opt.value === produto.siglaApresentacao) || null} onChange={handleSelectChange} placeholder="Ex: CR" isClearable required />
                  </div>
                </div>
              </div>

              <div style={{ padding: '10px', backgroundColor: '#f0f7ff', borderRadius: '4px', border: '1px dashed #b8daff' }}>
                <h5 style={{ margin: '0 0 10px 0', color: '#004085' }}>Dosagem e Especificações da Embalagem</h5>
                
                <div style={{ display: 'flex', gap: '15px', flexWrap: 'wrap' }}>
                  
                  <div style={{ flex: '1 1 200px' }}>
                    <label>Qtd. Doses (Ex: número cápsulas):</label>
                    <input type="number" step="1" name="quantidadeDoses" value={produto.quantidadeDoses} onChange={handleInputChange} style={{ width: '100%', padding: '6px', marginTop: '5px', boxSizing: 'border-box' }} />
                  </div>
                  <div style={{ flex: '1 1 200px' }}>
                    <label>Unidade Farmacêutica da Dose:</label>
                    <div style={{ marginTop: '5px' }}>
                      <Select name="siglaMedidaDoses" options={optionsMedidaFarmaceutica} value={optionsMedidaFarmaceutica.find(opt => opt.value === produto.siglaMedidaDoses) || null} onChange={handleSelectChange} placeholder="Ex: CP, CAPS..." isClearable />
                    </div>
                  </div>

                  <div style={{ flex: '1 1 200px' }}>
                    <label>Conteúdo (Líquidos/Pastosos):</label>
                    <input type="number" step="0.01" name="conteudo" value={produto.conteudo} onChange={handleInputChange} style={{ width: '100%', padding: '6px', marginTop: '5px', boxSizing: 'border-box' }} />
                  </div>
                  <div style={{ flex: '1 1 200px' }}>
                    <label>Unidade de Medida do Conteúdo:</label>
                    <div style={{ marginTop: '5px' }}>
                      <Select name="siglaMedidaConteudo" options={optionsMedidaBasica} value={optionsMedidaBasica.find(opt => opt.value === produto.siglaMedidaConteudo) || null} onChange={handleSelectChange} placeholder="Ex: ML, G..." isClearable />
                    </div>
                  </div>

                  <div style={{ flex: '1 1 200px' }}>
                    <label>Peso Líquido (Pó/Soluções):</label>
                    <input type="number" step="0.01" name="pesoLiquido" value={produto.pesoLiquido} onChange={handleInputChange} style={{ width: '100%', padding: '6px', marginTop: '5px', boxSizing: 'border-box' }} />
                  </div>

                </div>
              </div>

            </div>
          )}

          <button type="submit" style={{ padding: '12px', marginTop: '10px', backgroundColor: categoriaTela === 'remedio' ? '#007bff' : '#28a745', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold', fontSize: '1.1em' }}>
            {categoriaTela === 'remedio' ? 'Registrar Medicamento' : 'Registrar Produto Geral'}
          </button>
        </form>
      </div>

      {modalLabAberto && (
        <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, backgroundColor: 'rgba(0,0,0,0.6)', display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 1000 }}>
          <div style={{ backgroundColor: '#f9f9f9', padding: '30px', borderRadius: '8px', maxWidth: '600px', width: '100%', maxHeight: '90vh', overflowY: 'auto', position: 'relative', boxShadow: '0 4px 20px rgba(0,0,0,0.2)' }}>
            <button type="button" onClick={() => { setModalLabAberto(false); carregarLaboratorios(); }} style={{ position: 'absolute', top: '15px', right: '15px', padding: '5px 10px', backgroundColor: '#dc3545', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold' }}>X Fechar</button>
            <div style={{ marginTop: '20px' }}><Laboratorios /></div>
          </div>
        </div>
      )}

      {modalFormulaAberto && (
        <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, backgroundColor: 'rgba(0,0,0,0.6)', display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 1000 }}>
          <div style={{ backgroundColor: '#f9f9f9', padding: '30px', borderRadius: '8px', maxWidth: '800px', width: '100%', maxHeight: '90vh', overflowY: 'auto', position: 'relative', boxShadow: '0 4px 20px rgba(0,0,0,0.2)' }}>
            <button type="button" onClick={() => { setModalFormulaAberto(false); carregarFormulas(); }} style={{ position: 'absolute', top: '15px', right: '15px', padding: '5px 10px', backgroundColor: '#dc3545', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold', zIndex: 10 }}>X Fechar</button>
            <div style={{ marginTop: '20px' }}><Formulas /></div>
          </div>
        </div>
      )}

    </div>
  );
}