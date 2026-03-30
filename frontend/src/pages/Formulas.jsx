import { useState, useEffect } from 'react';
import api from '../services/api';
import { toast } from 'react-toastify';

export default function Formulas() {
  const [formulas, setFormulas] = useState([]);
  const [composicoesDisponiveis, setComposicoesDisponiveis] = useState([]);

  // Estados para montar o formulário
  const [composicaoSelecionadaNoSelect, setComposicaoSelecionadaNoSelect] = useState('');
  const [idsComposicoesEscolhidas, setIdsComposicoesEscolhidas] = useState([]);

  const carregarDados = () => {
    // 1. Busca as Fórmulas já criadas
    api.get('/api/produto/remedio/formulacao/formula')
      .then((res) => setFormulas(res.data))
      .catch((err) => console.error("Erro ao buscar fórmulas:", err));

    // 2. Busca as Composições para preencher o Select
    api.get('/api/produto/remedio/formulacao/composicao')
      .then((res) => {
        setComposicoesDisponiveis(res.data);
        if (res.data.length > 0) {
          setComposicaoSelecionadaNoSelect(res.data[0].id); // Seleciona o primeiro ID por defeito
        }
      })
      .catch((err) => console.error("Erro ao buscar composições:", err));
  };

  useEffect(() => {
    carregarDados();
  }, []);

  // --- LÓGICA DO CONSTRUTOR DE LISTA ---

  const adicionarComposicaoALista = () => {
    if (!composicaoSelecionadaNoSelect) return;
    
    const id = Number(composicaoSelecionadaNoSelect);
    
    // Só adiciona se o ID ainda não estiver na lista (evita duplicados)
    if (!idsComposicoesEscolhidas.includes(id)) {
      setIdsComposicoesEscolhidas([...idsComposicoesEscolhidas, id]);
    }
  };

  const removerComposicaoDaLista = (idParaRemover) => {
    // Filtra a lista mantendo apenas os IDs diferentes do que queremos remover
    setIdsComposicoesEscolhidas(idsComposicoesEscolhidas.filter(id => id !== idParaRemover));
  };

  // --- SUBMISSÃO ---

  const registarFormula = (event) => {
    event.preventDefault();

    if (idsComposicoesEscolhidas.length === 0) {
      toast.error("Por favor, adicione pelo menos uma composição à fórmula!");
      return;
    }

    // O formato EXATO que o seu backend pediu
    const payload = {
      idComposicoes: idsComposicoesEscolhidas
    };

    api.post('/api/produto/remedio/formulacao/formula', payload)
      .then(() => {
        toast.success("Fórmula registada com sucesso!");
        setIdsComposicoesEscolhidas([]); // Esvazia o carrinho
        carregarDados(); // Atualiza a tabela
      })
      .catch((error) => {
        console.error("Erro ao registar fórmula:", error);
        toast.error("Erro ao registar a fórmula.");
      });
  };

  // Função auxiliar para mostrar o nome da composição de forma bonita no frontend
  const formatarNomeComposicao = (comp) => {
    let nome = `${comp.principioAtivo} ${comp.quantiaPrincipio}${comp.unidadeMedidaPrincipio}`;
    if (comp.quantiaExcipiente > 0 && comp.unidadeMedidaExcipiente) {
      nome += ` / ${comp.quantiaExcipiente}${comp.unidadeMedidaExcipiente}`;
    }
    return nome;
  };

  return (
    <div>
      <h2>Gestão de Fórmulas</h2>
      <p>Crie uma fórmula combinando uma ou mais composições.</p>

      {/* Formulário */}
      <div style={{ border: '1px solid #ccc', padding: '20px', marginTop: '20px', borderRadius: '5px', maxWidth: '600px', backgroundColor: '#fff' }}>
        <h4 style={{ margin: '0 0 15px 0', color: '#333' }}>Montar Nova Fórmula</h4>
        
        <div style={{ display: 'flex', gap: '10px', marginBottom: '20px' }}>
          <select 
            value={composicaoSelecionadaNoSelect} 
            onChange={(e) => setComposicaoSelecionadaNoSelect(e.target.value)} 
            style={{ flex: 1, padding: '8px' }}
          >
            {composicoesDisponiveis.map(comp => (
               // Aqui usamos o ID da composição como value
              <option key={comp.id} value={comp.id}>
                {formatarNomeComposicao(comp)}
              </option>
            ))}
          </select>
          
          <button type="button" onClick={adicionarComposicaoALista} style={{ padding: '8px 15px', backgroundColor: '#17a2b8', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold' }}>
            + Adicionar à Fórmula
          </button>
        </div>

        {/* Carrinho de Composições Escolhidas */}
        <div style={{ backgroundColor: '#f8f9fa', padding: '10px', borderRadius: '4px', minHeight: '80px', border: '1px dashed #ccc', marginBottom: '15px' }}>
          <h5 style={{ margin: '0 0 10px 0' }}>Composições incluídas nesta Fórmula:</h5>
          
          {idsComposicoesEscolhidas.length === 0 ? (
            <span style={{ fontSize: '0.9em', color: '#777' }}>Ainda não adicionou nenhuma composição.</span>
          ) : (
            <ul style={{ paddingLeft: '20px', margin: 0 }}>
              {idsComposicoesEscolhidas.map(idEsc => {
                // Encontra o objeto completo da composição para podermos mostrar o nome ao utilizador em vez de apenas um número
                const compObj = composicoesDisponiveis.find(c => c.id === idEsc);
                return (
                  <li key={idEsc} style={{ marginBottom: '5px' }}>
                    {compObj ? formatarNomeComposicao(compObj) : `ID: ${idEsc}`}
                    <button type="button" onClick={() => removerComposicaoDaLista(idEsc)} style={{ marginLeft: '10px', color: 'red', background: 'none', border: 'none', cursor: 'pointer', fontWeight: 'bold' }}>
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

      {/* Lista de Fórmulas */}
      <h3 style={{ marginTop: '30px' }}>Fórmulas Registadas</h3>
      {formulas.length === 0 ? (
        <p>Nenhuma fórmula registada ainda...</p>
      ) : (
        <ul style={{ marginTop: '10px', listStyleType: 'none', padding: 0 }}>
          {formulas.map((form) => (
            <li key={form.id} style={{ padding: '15px', borderBottom: '1px solid #eee', backgroundColor: '#fff', marginBottom: '10px', borderRadius: '4px', boxShadow: '0 1px 3px rgba(0,0,0,0.1)' }}>
              <div style={{ fontWeight: 'bold', color: '#0056b3', marginBottom: '8px' }}>
                Fórmula ID: {form.id}
              </div>
              <ul style={{ paddingLeft: '20px', color: '#555' }}>
                {/* Aqui renderizamos as strings já compiladas que o seu backend inteligentemente enviou */}
                {form.composicoes.map((stringCompilada, index) => (
                  <li key={index}>{stringCompilada}</li>
                ))}
              </ul>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}