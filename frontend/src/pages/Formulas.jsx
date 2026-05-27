import { useState, useEffect } from 'react';
import api from '../services/api';
import { toast } from 'react-toastify';
import Select from 'react-select';

// 1. IMPORTAMOS A TELA DE COMPOSIÇÕES
import Composicoes from './Composicoes'; 

export default function Formulas() {
  const [formulas, setFormulas] = useState([]);
  const [composicoesDisponiveis, setComposicoesDisponiveis] = useState([]);

  // 2. ESTADO DO MODAL DE COMPOSIÇÕES
  const [modalComposicaoAberto, setModalComposicaoAberto] = useState(false);

  // Estados para montar o formulário
  // Agora o react-select guarda o objeto da opção selecionada { value, label }
  const [composicaoSelecionada, setComposicaoSelecionada] = useState(null);
  const [idsComposicoesEscolhidas, setIdsComposicoesEscolhidas] = useState([]);

  const carregarFormulas = () => {
    api.get('/api/produto/remedio/formulacao/formula')
      .then((res) => setFormulas(res.data))
      .catch((err) => console.error("Erro ao buscar fórmulas:", err));
  };

  // 3. ISOLAMOS A BUSCA DE COMPOSIÇÕES para atualizar após o fecho do modal
  const carregarComposicoes = () => {
    api.get('/api/produto/remedio/formulacao/composicao')
      .then((res) => setComposicoesDisponiveis(res.data))
      .catch((err) => console.error("Erro ao buscar composições:", err));
  };

  useEffect(() => {
    carregarFormulas();
    carregarComposicoes();
  }, []);

  // Função auxiliar para formatar o nome legível da composição
  const formatarNomeComposicao = (comp) => {
    let nome = `${comp.principioAtivo} ${comp.quantiaPrincipio}${comp.unidadeMedidaPrincipio}`;
    if (comp.quantiaExcipiente > 0 && comp.unidadeMedidaExcipiente) {
      nome += ` / ${comp.quantiaExcipiente}${comp.unidadeMedidaExcipiente}`;
    }
    return nome;
  };

  // 4. FORMATANDO AS COMPOSIÇÕES PARA O REACT-SELECT
  const optionsComposicao = composicoesDisponiveis.map(comp => ({
    value: comp.id,
    label: formatarNomeComposicao(comp)
  }));

  // --- LÓGICA DO CONSTRUTOR DE LISTA ---
  const adicionarComposicaoALista = () => {
    if (!composicaoSelecionada) return;
    
    const id = Number(composicaoSelecionada.value);
    
    if (!idsComposicoesEscolhidas.includes(id)) {
      setIdsComposicoesEscolhidas([...idsComposicoesEscolhidas, id]);
    }
    
    // Limpa o campo de pesquisa após adicionar para facilitar a próxima busca
    setComposicaoSelecionada(null); 
  };

  const removerComposicaoDaLista = (idParaRemover) => {
    setIdsComposicoesEscolhidas(idsComposicoesEscolhidas.filter(id => id !== idParaRemover));
  };

  // --- SUBMISSÃO ---
  const registarFormula = (event) => {
    event.preventDefault();

    if (idsComposicoesEscolhidas.length === 0) {
      toast.warning("Por favor, adicione pelo menos uma composição à fórmula!");
      return;
    }

    const payload = {
      idComposicoes: idsComposicoesEscolhidas
    };

    api.post('/api/produto/remedio/formulacao/formula', payload)
      .then(() => {
        toast.success("Fórmula registada com sucesso!");
        setIdsComposicoesEscolhidas([]); 
        carregarFormulas(); 
      })
      .catch((error) => {
        console.error("Erro ao registar fórmula:", error);
        toast.error("Erro ao registar a fórmula.");
      });
  };

  return (
    <div>
      <h2>Gestão de Fórmulas</h2>
      <p>Crie uma fórmula combinando uma ou mais composições.</p>

      {/* Formulário */}
      <div style={{ border: '1px solid #ccc', padding: '20px', marginTop: '20px', borderRadius: '5px', maxWidth: '600px', backgroundColor: '#fff' }}>
        <h4 style={{ margin: '0 0 15px 0', color: '#333' }}>Montar Nova Fórmula</h4>
        
        {/* SELEÇÃO COM PESQUISA DINÂMICA E BOTÃO DE ATALHO */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '5px', marginBottom: '20px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <label style={{ fontWeight: 'bold' }}>Selecionar Composição:</label>
            <button 
              type="button" 
              onClick={() => setModalComposicaoAberto(true)} 
              style={{ padding: '2px 8px', fontSize: '0.85em', backgroundColor: '#17a2b8', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}
            >
              + Nova Composição
            </button>
          </div>
          
          <div style={{ display: 'flex', gap: '10px', marginTop: '5px' }}>
            <div style={{ flex: 1 }}>
              <Select 
                options={optionsComposicao}
                value={composicaoSelecionada}
                onChange={(option) => setComposicaoSelecionada(option)}
                placeholder="Busque por princípio ativo ou concentração..."
                isClearable
              />
            </div>
            <button 
              type="button" 
              onClick={adicionarComposicaoALista} 
              style={{ padding: '0 15px', backgroundColor: '#007bff', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold' }}
            >
              + Incluir
            </button>
          </div>
        </div>

        {/* Carrinho de Composições Escolhidas */}
        <div style={{ backgroundColor: '#f8f9fa', padding: '15px', borderRadius: '4px', minHeight: '80px', border: '1px dashed #ccc', marginBottom: '15px' }}>
          <h5 style={{ margin: '0 0 10px 0', color: '#555' }}>Composições incluídas nesta Fórmula:</h5>
          
          {idsComposicoesEscolhidas.length === 0 ? (
            <span style={{ fontSize: '0.9em', color: '#777' }}>Ainda não adicionou nenhuma composição.</span>
          ) : (
            <ul style={{ paddingLeft: '20px', margin: 0 }}>
              {idsComposicoesEscolhidas.map(idEsc => {
                const compObj = composicoesDisponiveis.find(c => c.id === idEsc);
                return (
                  <li key={idEsc} style={{ marginBottom: '8px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', backgroundColor: '#fff', padding: '5px 10px', borderRadius: '4px', border: '1px solid #eee' }}>
                    <span>{compObj ? formatarNomeComposicao(compObj) : `ID: ${idEsc}`}</span>
                    <button 
                      type="button" 
                      onClick={() => removerComposicaoDaLista(idEsc)} 
                      style={{ color: '#dc3545', background: 'none', border: 'none', cursor: 'pointer', fontWeight: 'bold' }}
                    >
                      [Remover]
                    </button>
                  </li>
                );
              })}
            </ul>
          )}
        </div>

        <button type="button" onClick={registarFormula} style={{ padding: '10px 20px', backgroundColor: '#28a745', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold', width: '100%' }}>
          Salvar Fórmula
        </button>
      </div>

      

      {/* ============================================================== */}
      {/* MODAL DE COMPOSIÇÕES (z-index: 1010 para empilhamento correto)  */}
      {/* ============================================================== */}
      {modalComposicaoAberto && (
        <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, backgroundColor: 'rgba(0,0,0,0.7)', display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 1010 }}>
          
          <div style={{ backgroundColor: '#fff', padding: '30px', borderRadius: '8px', maxWidth: '750px', width: '100%', maxHeight: '90vh', overflowY: 'auto', position: 'relative', boxShadow: '0 5px 25px rgba(0,0,0,0.3)' }}>
            
            <button 
              type="button"
              onClick={() => {
                setModalComposicaoAberto(false);
                carregarComposicoes(); // Atualiza a lista secreta do Select quando este fechar
              }} 
              style={{ position: 'absolute', top: '15px', right: '15px', padding: '5px 10px', backgroundColor: '#dc3545', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold' }}
            >
              X Fechar
            </button>

            <div style={{ marginTop: '10px' }}>
              <Composicoes /> 
            </div>

          </div>
        </div>
      )}
    </div>
  );
}