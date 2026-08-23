import { useState, useEffect } from 'react';
import api from '../services/api';
import { toast } from 'react-toastify';
import Select from 'react-select';

export default function ConsultaProdutos() {
  const [produtos, setProdutos] = useState([]);
  const [filtroPesquisa, setFiltroPesquisa] = useState('');
  
  // ==========================================
  // ESTADOS PARA OS MODAIS E DADOS
  // ==========================================
  const [produtoSelecionado, setProdutoSelecionado] = useState(null); // Para Visualização (Read)
  const [modalVisualizarAberto, setModalVisualizarAberto] = useState(false);

  const [produtoEditando, setProdutoEditando] = useState(null);       // Para Edição (Update)
  const [modalEdicaoAberto, setModalEdicaoAberto] = useState(false);

  // Estados Auxiliares para preencher os Selects da Edição
  const [laboratorios, setLaboratorios] = useState([]);
  const [formulas, setFormulas] = useState([]);
  const [apresentacoes, setApresentacoes] = useState([]);
  const [medidasFarmaceuticas, setMedidasFarmaceuticas] = useState([]); 
  const [medidasBasicas, setMedidasBasicas] = useState([]);             
  const [medidasPadrao, setMedidasPadrao] = useState([]);

  // ==========================================
  // FUNÇÕES DE CARREGAMENTO
  // ==========================================
  const carregarProdutos = () => {
    api.get('/api/produto')
      .then((response) => setProdutos(response.data))
      .catch((error) => console.error("Erro ao buscar produtos:", error));
  };

  useEffect(() => {
    carregarProdutos();
    // Carregamento passivo das opções de edição
    api.get('/api/produto/remedio/laboratorio').then(res => setLaboratorios(res.data)).catch(console.error);
    api.get('/api/produto/remedio/formulacao/formula').then(res => setFormulas(res.data)).catch(console.error);
    api.get('/api/produto/remedio/apresentacao').then(res => setApresentacoes(res.data)).catch(console.error);
    api.get('/api/produto/remedio/medida_farmaceutica').then(res => setMedidasFarmaceuticas(res.data)).catch(console.error);
    api.get('/api/produto/remedio/formulacao/medida_basica').then(res => setMedidasBasicas(res.data)).catch(console.error);
    api.get('/api/produto/medida_padrao').then(res => setMedidasPadrao(res.data)).catch(console.error);
  }, []);

  // Formatações do React-Select (Idênticas ao Cadastro)
  const optionsLaboratorio = laboratorios.map(lab => ({ value: lab.id, label: lab.marca }));
  const optionsFormula = formulas.map(form => ({ value: form.id, label: form.composicoes ? form.composicoes.join(" + ") : `Fórmula ID ${form.id}` }));
  const optionsApresentacao = apresentacoes.map(apr => ({ value: apr.sigla, label: `${apr.sigla} - ${apr.nome || apr.apresentacao}` }));
  const optionsMedidaFarmaceutica = medidasFarmaceuticas.map(med => ({ value: med.sigla, label: `${med.sigla} - ${med.descricao || med.nome}` }));
  const optionsMedidaBasica = medidasBasicas.map(med => ({ value: med.sigla, label: `${med.sigla} - ${med.nome}` }));
  
  const optionsMedidaPadrao = medidasPadrao.map(med => {
    if (typeof med === 'object' && med !== null) {
      const sigla = med.sigla || '';
      const descricao = med.descricao || '';
      return { value: sigla, label: descricao ? `${sigla} - ${descricao}` : sigla };
    }
    return { value: med, label: med };
  });

  // ==========================================
  // LÓGICA DO FILTRO DE PESQUISA
  // ==========================================
  const produtosFiltrados = produtos.filter((prod) => {
    const termo = filtroPesquisa.toLowerCase();
    const nomeMatch = prod.nomeComercial?.toLowerCase().includes(termo);
    const eanMatch = prod.ean?.includes(termo);
    return nomeMatch || eanMatch;
  });

  // ==========================================
  // LÓGICA DE VISUALIZAÇÃO (READ)
  // ==========================================
  const abrirDetalhes = (produto) => {
    setProdutoSelecionado(produto);
    setModalVisualizarAberto(true);
  };

  // ==========================================
  // LÓGICA DE EDIÇÃO (UPDATE) - BARREIRA
  // ==========================================
  const prepararEdicao = (produtoParaEditar) => {
    const confirmacao = window.confirm(
      "⚠️ ATENÇÃO! AVISO DE SEGURANÇA ⚠️\n\n" +
      "Esta opção deve ser usada EXCLUSIVAMENTE para corrigir pequenos erros de digitação (ex: letras trocadas no nome, ajuste na unidade de medida).\n\n" +
      "NUNCA mude completamente os dados para outro Produto (ex: alterar de Dipirona para Paracetamol, ou modificar o código de barras do produto), pois isso corromperá o histórico de vendas de toda a drogaria.\n\n" +
      "Tem a certeza de que deseja apenas corrigir o registo de " + produtoParaEditar.nomeComercial + "?"
    );

    if (confirmacao) {
      // 1. ENGENHARIA REVERSA: Procura os IDs e Siglas baseados no texto que veio do backend
      
      const labEncontrado = laboratorios.find(l => l.marca === produtoParaEditar.nomeLaboratorio);
      
      const formulaEncontrada = formulas.find(f => {
        const stringComposicao = f.composicoes ? f.composicoes.join(" + ") : `Fórmula ID ${f.id}`;
        return stringComposicao === produtoParaEditar.formula;
      });

      const apresEncontrada = apresentacoes.find(a => 
        a.apresentacao === produtoParaEditar.apresentacao || a.nome === produtoParaEditar.apresentacao
      );

      const medDosesEncontrada = medidasFarmaceuticas.find(m => 
        m.descricao === produtoParaEditar.descricaoMedidaDoses || m.nome === produtoParaEditar.descricaoMedidaDoses
      );

      const medConteudoEncontrada = medidasBasicas.find(m => 
        m.nome === produtoParaEditar.descricaoMedidaConteudo || m.descricao === produtoParaEditar.descricaoMedidaConteudo
      );

      // 2. Injeta os dados convertidos no estado do formulário de edição
      setProdutoEditando({
        ...produtoParaEditar,
        ean: produtoParaEditar.ean,
        
        // Se encontrou na lista, usa o ID/Sigla. Se não, deixa em branco ('')
        idLaboratorio: labEncontrado ? labEncontrado.id : '',
        idFormula: formulaEncontrada ? formulaEncontrada.id : '',
        siglaApresentacao: apresEncontrada ? apresEncontrada.sigla : '',
        siglaMedidaDoses: medDosesEncontrada ? medDosesEncontrada.sigla : '',
        siglaMedidaConteudo: medConteudoEncontrada ? medConteudoEncontrada.sigla : '',
        
        // A unidade padrão já vem correta (ex: "CX"), por isso usamos diretamente
        unidadeDeMedida: produtoParaEditar.unidadeDeMedida || ''
      });
      
      setModalEdicaoAberto(true);
    }
  };

  const handleEditChange = (event) => {
    const { name, value } = event.target;
    setProdutoEditando({ ...produtoEditando, [name]: value });
  };

  const handleEditSelectChange = (selectedOption, actionMeta) => {
    const fieldName = actionMeta.name;
    const value = selectedOption ? selectedOption.value : '';
    setProdutoEditando({ ...produtoEditando, [fieldName]: value });
  };

  const salvarEdicao = (event) => {
    event.preventDefault();

    const isRemedio = produtoEditando.tipoProduto === 'remedio' || produtoEditando.tipoProduto === 'generico';
    let payload;
    const url = `/api/produto/${produtoEditando.ean}`;

    // Constrói o pacote de dados exato para a API, mantendo a regra do seu DTO
    if (isRemedio) {
      payload = {
        nomeComercial: produtoEditando.nomeComercial,
        //ean: produtoEditando.ean,
        unidadeDeMedida: produtoEditando.unidadeDeMedida,
        tipoProduto: produtoEditando.tipoProduto, // Mantém o original
        idLaboratorio: Number(produtoEditando.idLaboratorio),
        idFormula: Number(produtoEditando.idFormula),
        tipoControle: produtoEditando.tipoControle,
        siglaApresentacao: produtoEditando.siglaApresentacao,
        quantidadeDoses: produtoEditando.quantidadeDoses ? Number(produtoEditando.quantidadeDoses) : 0,
        siglaMedidaDoses: produtoEditando.siglaMedidaDoses || null,
        conteudo: produtoEditando.conteudo ? Number(produtoEditando.conteudo) : 0,
        siglaMedidaConteudo: produtoEditando.siglaMedidaConteudo || null,
        pesoLiquido: produtoEditando.pesoLiquido ? Number(produtoEditando.pesoLiquido) : 0
      };
      
    } else {
      payload = {
        nomeComercial: produtoEditando.nomeComercial,
        ean: produtoEditando.ean,
        unidadeDeMedida: produtoEditando.unidadeDeMedida,
        tipoProduto: produtoEditando.tipoProduto
      };
    }

    api.put(url, payload)
      .then(() => {
        toast.success("Registo corrigido com sucesso!");
        setModalEdicaoAberto(false);
        carregarProdutos(); // Atualiza a lista por trás
      })
      .catch((error) => {
        const msg = error.response?.data?.message || error.response?.data || "Erro desconhecido";
        console.error("Erro ao editar:", error);
        toast.error("Erro ao corrigir: " + JSON.stringify(msg));
      });
  };

  return (
    <div>
      <h2>Consulta de Produtos</h2>
      <p>Pesquise, visualize os detalhes ou corrija informações do catálogo.</p>

      {/* BARRA DE PESQUISA */}
      <div style={{ marginBottom: '20px', maxWidth: '600px' }}>
        <input 
          type="text"
          placeholder="🔍 Digite o Nome Comercial ou EAN (Código de Barras)..."
          value={filtroPesquisa}
          onChange={(e) => setFiltroPesquisa(e.target.value)}
          style={{ width: '100%', padding: '12px', borderRadius: '5px', border: '1px solid #ccc', boxSizing: 'border-box', fontSize: '1em' }}
        />
      </div>

      {/* LISTAGEM DE RESULTADOS */}
      <div style={{ maxWidth: '800px' }}>
        {produtosFiltrados.length === 0 ? (
          <p style={{ color: '#777', fontStyle: 'italic' }}>Nenhum produto encontrado...</p>
        ) : (
          <ul style={{ listStyleType: 'none', padding: 0 }}>
            {produtosFiltrados.map((prod) => (
              <li 
                key={prod.id || prod.ean} 
                style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '15px', borderBottom: '1px solid #eee', backgroundColor: '#fff', marginBottom: '8px', borderRadius: '5px', boxShadow: '0 2px 4px rgba(0,0,0,0.05)' }}
              >
                <div>
                  <div style={{ fontWeight: 'bold', fontSize: '1.1em', color: '#333' }}>
                    {prod.nomeComercial}
                  </div>
                  <div style={{ fontSize: '0.85em', color: '#666', marginTop: '4px' }}>
                    <strong>EAN:</strong> {prod.ean} | <strong>Tipo:</strong> {prod.tipoProduto?.toUpperCase()}
                  </div>
                </div>
                
                <div style={{ display: 'flex', gap: '10px' }}>
                  <button 
                    type="button"
                    onClick={() => abrirDetalhes(prod)}
                    style={{ padding: '8px 15px', cursor: 'pointer', backgroundColor: '#007bff', color: 'white', border: 'none', borderRadius: '4px', fontWeight: 'bold' }}
                  >
                    👁️ Ver
                  </button>
                  <button 
                    type="button"
                    onClick={() => prepararEdicao(prod)}
                    style={{ padding: '8px 15px', cursor: 'pointer', backgroundColor: '#ffc107', color: '#000', border: 'none', borderRadius: '4px', fontWeight: 'bold' }}
                  >
                    ✏️ Corrigir
                  </button>
                </div>
              </li>
            ))}
          </ul>
        )}
      </div>

      {/* ========================================================= */}
      {/* 1. MODAL DE VISUALIZAÇÃO DE DETALHES (READ)               */}
      {/* ========================================================= */}
      {modalVisualizarAberto && produtoSelecionado && (
        <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, backgroundColor: 'rgba(0,0,0,0.6)', display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 1000 }}>
          <div style={{ backgroundColor: '#fff', padding: '30px', borderRadius: '8px', maxWidth: '700px', width: '100%', maxHeight: '90vh', overflowY: 'auto', position: 'relative' }}>
            <button onClick={() => setModalVisualizarAberto(false)} style={{ position: 'absolute', top: '15px', right: '15px', padding: '5px 10px', backgroundColor: '#dc3545', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold' }}>X Fechar</button>
            <h3 style={{ marginTop: 0, color: '#004085', borderBottom: '2px solid #e6f2ff', paddingBottom: '10px' }}>Ficha do Produto</h3>
            <div style={{ marginBottom: '20px', display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px' }}>
              <div><strong>Nome:</strong> {produtoSelecionado.nomeComercial}</div>
              <div><strong>EAN:</strong> {produtoSelecionado.ean}</div>
              <div><strong>Tipo:</strong> {produtoSelecionado.tipoProduto?.toUpperCase()}</div>
              <div><strong>Unidade Padrão:</strong> {produtoSelecionado.unidadeDeMedida}</div>
            </div>
            {(produtoSelecionado.tipoProduto === 'remedio' || produtoSelecionado.tipoProduto === 'generico') && (
              <div style={{ padding: '15px', backgroundColor: '#f8f9fa', borderRadius: '5px', border: '1px solid #dee2e6' }}>
                <h4 style={{ margin: '0 0 15px 0', color: '#495057' }}>Dados Farmacêuticos</h4>
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px', fontSize: '0.95em' }}>
                  <div><strong>Laboratório ID:</strong> {produtoSelecionado.idLaboratorio}</div>
                  <div><strong>Fórmula ID:</strong> {produtoSelecionado.idFormula}</div>
                  <div><strong>Controle:</strong> {produtoSelecionado.tipoControle?.replace('_', ' ').toUpperCase()}</div>
                  <div><strong>Apresentação:</strong> {produtoSelecionado.siglaApresentacao}</div>
                </div>
                <h5 style={{ marginTop: '15px', marginBottom: '10px', color: '#6c757d' }}>Dosagem e Embalagem</h5>
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '10px', fontSize: '0.9em', backgroundColor: '#fff', padding: '10px', borderRadius: '4px', border: '1px solid #e9ecef' }}>
                  <div><strong>Doses:</strong><br/>{produtoSelecionado.quantidadeDoses} {produtoSelecionado.siglaMedidaDoses || ''}</div>
                  <div><strong>Conteúdo:</strong><br/>{produtoSelecionado.conteudo} {produtoSelecionado.siglaMedidaConteudo || ''}</div>
                  <div><strong>Peso Líq:</strong><br/>{produtoSelecionado.pesoLiquido ? `${produtoSelecionado.pesoLiquido} g/mg` : 'N/A'}</div>
                </div>
              </div>
            )}
          </div>
        </div>
      )}

      {/* ========================================================= */}
      {/* 2. MODAL DE EDIÇÃO (UPDATE)                               */}
      {/* ========================================================= */}
      {modalEdicaoAberto && produtoEditando && (
        <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, backgroundColor: 'rgba(0,0,0,0.8)', display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 1050 }}>
          <div style={{ backgroundColor: '#fff', padding: '30px', borderRadius: '8px', maxWidth: '850px', width: '100%', maxHeight: '95vh', overflowY: 'auto', position: 'relative', border: '3px solid #ffc107' }}>
            
            <button onClick={() => setModalEdicaoAberto(false)} style={{ position: 'absolute', top: '15px', right: '15px', padding: '5px 10px', backgroundColor: '#6c757d', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold' }}>X Cancelar</button>
            <h3 style={{ marginTop: 0, color: '#d39e00', borderBottom: '2px solid #fff3cd', paddingBottom: '10px' }}>✏️ Modo de Correção de Dados</h3>
            
            <form onSubmit={salvarEdicao} style={{ display: 'flex', flexDirection: 'column', gap: '15px', marginTop: '15px' }}>
              
              {/* DADOS GLOBAIS */}
              <div style={{ display: 'flex', gap: '10px' }}>
                <div style={{ flex: 2 }}>
  <label>EAN:</label>
  <input type="text" name="ean" value={produtoEditando.ean || ''} readOnly title="O EAN é definido pelo código de barras e não pode ser alterado."
      style={{
        width: '100%',
        padding: '8px',
        marginTop: '5px',
        boxSizing: 'border-box',
        backgroundColor: '#e9ecef',
        color: '#6c757d',
        cursor: 'not-allowed'
      }}
  />
</div>
                <div style={{ flex: 2 }}>
                  <label>Unidade Padrão:</label>
                  <div style={{ marginTop: '5px' }}>
                    <Select 
                      name="unidadeDeMedida" 
                      options={optionsMedidaPadrao} 
                      value={produtoEditando.unidadeDeMedida ? optionsMedidaPadrao.find(opt => String(opt.value) === String(produtoEditando.unidadeDeMedida)) : null} 
                      onChange={handleEditSelectChange} 
                      isClearable required 
                    />
                  </div>
                </div>
                <div style={{ flex: 3 }}>
                  <label>Nome do Produto:</label>
                  <input type="text" name="nomeComercial" value={produtoEditando.nomeComercial || ''} onChange={handleEditChange} required disabled={produtoEditando.tipoProduto === 'generico'} style={{ width: '100%', padding: '8px', marginTop: '5px', boxSizing: 'border-box', backgroundColor: produtoEditando.tipoProduto === 'generico' ? '#e9ecef' : '#fff' }} />
                </div>
              </div>

              {/* DADOS FARMACÊUTICOS (Só aparecem se for remédio) */}
              {(produtoEditando.tipoProduto === 'remedio' || produtoEditando.tipoProduto === 'generico') && (
                <div style={{ padding: '15px', border: '1px solid #ffeeba', backgroundColor: '#fff8b3', borderRadius: '4px', display: 'flex', flexDirection: 'column', gap: '15px' }}>
                  
                  <div style={{ display: 'flex', gap: '15px' }}>
                    <div style={{ flex: 1 }}>
                      <label>Laboratório:</label>
                      <Select 
                        name="idLaboratorio" 
                        options={optionsLaboratorio} 
                        value={produtoEditando.idLaboratorio ? optionsLaboratorio.find(opt => Number(opt.value) === Number(produtoEditando.idLaboratorio)) : null} 
                        onChange={handleEditSelectChange} 
                        isClearable required 
                      />
                    </div>
                    <div style={{ flex: 2 }}>
                      <label>Fórmula Base:</label>
                      <Select 
                        name="idFormula" 
                        options={optionsFormula} 
                        value={produtoEditando.idFormula ? optionsFormula.find(opt => Number(opt.value) === Number(produtoEditando.idFormula)) : null} 
                        onChange={handleEditSelectChange} 
                        isClearable required 
                      />
                    </div>
                  </div>

                  <div style={{ display: 'flex', gap: '15px' }}>
                    <div style={{ flex: 1 }}>
                      <label>Tipo de Controle:</label>
                      <select name="tipoControle" value={produtoEditando.tipoControle || 'isento'} onChange={handleEditChange} required style={{ width: '100%', padding: '8px', marginTop: '5px', borderRadius: '4px', border: '1px solid #ccc' }}>
                        <option value="isento">Isento de Receita</option>
                        <option value="sob_prescricao">Venda Sob Prescrição Médica</option>
                        <option value="especial">Controle Especial</option>
                      </select>
                    </div>
                    <div style={{ flex: 1 }}>
                      <label>Apresentação:</label>
                      <div style={{ marginTop: '5px' }}>
                        <Select 
                          name="siglaApresentacao" 
                          options={optionsApresentacao} 
                          value={produtoEditando.siglaApresentacao ? optionsApresentacao.find(opt => String(opt.value) === String(produtoEditando.siglaApresentacao)) : null} 
                          onChange={handleEditSelectChange} 
                          isClearable required 
                        />
                      </div>
                    </div>
                  </div>

                  <div style={{ display: 'flex', gap: '10px', flexWrap: 'wrap' }}>
                    <div style={{ flex: '1 1 150px' }}>
                      <label>Qtd. Doses:</label>
                      <input type="number" step="1" name="quantidadeDoses" value={produtoEditando.quantidadeDoses || ''} onChange={handleEditChange} style={{ width: '100%', padding: '6px', marginTop: '5px', boxSizing: 'border-box' }} />
                    </div>
                    <div style={{ flex: '1 1 150px' }}>
                      <label>Unid. Dose:</label>
                      <div style={{ marginTop: '5px' }}>
                        <Select 
                          name="siglaMedidaDoses" 
                          options={optionsMedidaFarmaceutica} 
                          value={produtoEditando.siglaMedidaDoses ? optionsMedidaFarmaceutica.find(opt => String(opt.value) === String(produtoEditando.siglaMedidaDoses)) : null} 
                          onChange={handleEditSelectChange} 
                          isClearable 
                        />
                      </div>
                    </div>
                    <div style={{ flex: '1 1 150px' }}>
                      <label>Conteúdo:</label>
                      <input type="number" step="0.01" name="conteudo" value={produtoEditando.conteudo || ''} onChange={handleEditChange} style={{ width: '100%', padding: '6px', marginTop: '5px', boxSizing: 'border-box' }} />
                    </div>
                    <div style={{ flex: '1 1 150px' }}>
                      <label>Unid. Conteúdo:</label>
                      <div style={{ marginTop: '5px' }}>
                        <Select 
                          name="siglaMedidaConteudo" 
                          options={optionsMedidaBasica} 
                          value={produtoEditando.siglaMedidaConteudo ? optionsMedidaBasica.find(opt => String(opt.value) === String(produtoEditando.siglaMedidaConteudo)) : null} 
                          onChange={handleEditSelectChange} 
                          isClearable 
                        />
                      </div>
                    </div>
                  </div>
                </div>
              )}

              <button type="submit" style={{ padding: '12px', marginTop: '10px', backgroundColor: '#ffc107', color: '#000', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold', fontSize: '1.1em' }}>
                💾 Guardar Correção
              </button>
            </form>

          </div>
        </div>
      )}

    </div>
  );
}