import { useState, useEffect } from 'react';
import api from '../services/api';
import { toast } from 'react-toastify';

export default function Composicoes() {
  const [composicoes, setComposicoes] = useState([]);
  const [principiosAtivos, setPrincipiosAtivos] = useState([]);
  const [medidasBasicas, setMedidasBasicas] = useState([]);

  // Estado inicial do formulário
  const [novaComposicao, setNovaComposicao] = useState({
    principioAtivo: '',
    quantiaPrincipio: '',
    unidadeMedidaPrincipio: '',
    quantiaExcipiente: '',
    unidadeMedidaExcipiente: '' // Vazio significa null no envio
  });

  const carregarDados = () => {
    // 1. Carrega as composições já registadas
    api.get('/api/produto/remedio/formulacao/composicao')
      .then((res) => setComposicoes(res.data))
      .catch((err) => console.error("Erro ao buscar composições:", err));

    // 2. Carrega os Princípios Ativos para o Select
    api.get('/api/produto/remedio/formulacao/principio_ativo')
      .then((res) => {
        setPrincipiosAtivos(res.data);
        if (res.data.length > 0) {
          // Define o primeiro da lista como selecionado por defeito
          setNovaComposicao(prev => ({ ...prev, principioAtivo: res.data[0].nome }));
        }
      })
      .catch((err) => console.error("Erro ao buscar princípios ativos:", err));

    // 3. Carrega as Medidas Básicas para os Selects
    api.get('/api/produto/remedio/formulacao/medida_basica')
      .then((res) => {
        setMedidasBasicas(res.data);
        if (res.data.length > 0) {
          // Define a primeira medida como selecionada por defeito para o princípio
          setNovaComposicao(prev => ({ ...prev, unidadeMedidaPrincipio: res.data[0].sigla }));
        }
      })
      .catch((err) => console.error("Erro ao buscar medidas:", err));
  };

  // Executa assim que o ecrã é aberto
  useEffect(() => {
    carregarDados();
  }, []);

  const handleInputChange = (event) => {
    const { name, value } = event.target;
    setNovaComposicao({ ...novaComposicao, [name]: value });
  };

  const registarComposicao = (event) => {
    event.preventDefault();

    // Formata os dados exatamente como o seu backend espera
    const objetoComposicao = {
      principioAtivo: novaComposicao.principioAtivo,
      quantiaPrincipio: Number(novaComposicao.quantiaPrincipio),
      unidadeMedidaPrincipio: novaComposicao.unidadeMedidaPrincipio,
      // Se estiver vazio, envia 0
      quantiaExcipiente: novaComposicao.quantiaExcipiente ? Number(novaComposicao.quantiaExcipiente) : 0,
      // Se estiver vazio, envia null
      unidadeMedidaExcipiente: novaComposicao.unidadeMedidaExcipiente === '' ? null : novaComposicao.unidadeMedidaExcipiente
    };

    // NOTA: Baseado no seu exemplo, o backend espera um Array [ { ... } ].
    // Por isso, embrulhamos o objeto em parênteses retos [ ]. 
    // Se der erro 400, tente enviar apenas `objetoComposicao` (sem os parênteses retos).
    const payload = objetoComposicao;

    api.post('/api/produto/remedio/formulacao/composicao', payload)
      .then(() => {
        toast.success("Composição registada com sucesso!");
        carregarDados(); // Atualiza a lista
        // Limpa os campos de texto numéricos, mas mantém os selects nas opções iniciais
        setNovaComposicao(prev => ({
          ...prev,
          quantiaPrincipio: '',
          quantiaExcipiente: '',
          unidadeMedidaExcipiente: '' // Volta a ficar sem seleção (null)
        }));
      })
      .catch((error) => {
        const msg = error.response?.data?.message || error.response?.data || "Erro desconhecido";
        console.error("Erro ao registar:", error);
        toast.error("Erro ao registar a composição: " + JSON.stringify(msg));
      });
  };

  return (
    <div>
      <h2>Gestão de Composições</h2>
      <p>Associe Princípios Ativos às suas respetivas concentrações (ex: Dipirona 500mg).</p>

      {/* Formulário */}
      <div style={{ border: '1px solid #ccc', padding: '20px', marginTop: '20px', borderRadius: '5px', maxWidth: '600px', backgroundColor: '#fff' }}>
        <form onSubmit={registarComposicao} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
          
          <div style={{ padding: '10px', backgroundColor: '#f0f8ff', borderRadius: '5px', border: '1px solid #cce5ff' }}>
            <h4 style={{ margin: '0 0 10px 0', color: '#004085' }}>1. Princípio Ativo (Obrigatório)</h4>
            <div style={{ display: 'flex', gap: '10px', alignItems: 'center' }}>
              <select name="principioAtivo" value={novaComposicao.principioAtivo} onChange={handleInputChange} required style={{ flex: 2, padding: '8px' }}>
                {principiosAtivos.map(pa => (
                  <option key={pa.id} value={pa.nome}>{pa.nome}</option>
                ))}
              </select>
              
              <input type="number" step="0.01" name="quantiaPrincipio" placeholder="Quantia (ex: 500)" value={novaComposicao.quantiaPrincipio} onChange={handleInputChange} required style={{ flex: 1, padding: '8px' }} />
              
              <select name="unidadeMedidaPrincipio" value={novaComposicao.unidadeMedidaPrincipio} onChange={handleInputChange} required style={{ flex: 1, padding: '8px' }}>
                {medidasBasicas.map(mb => (
                  <option key={mb.sigla} value={mb.sigla}>{mb.sigla}</option>
                ))}
              </select>
            </div>
          </div>

          <div style={{ padding: '10px', backgroundColor: '#fdfdfe', borderRadius: '5px', border: '1px solid #e2e3e5' }}>
            <h4 style={{ margin: '0 0 10px 0', color: '#383d41' }}>2. Excipiente / Veículo (Opcional - p/ Líquidos)</h4>
            <div style={{ display: 'flex', gap: '10px', alignItems: 'center' }}>
              <input type="number" step="0.01" name="quantiaExcipiente" placeholder="Volume (ex: 5)" value={novaComposicao.quantiaExcipiente} onChange={handleInputChange} style={{ flex: 1, padding: '8px' }} />
              
              <select name="unidadeMedidaExcipiente" value={novaComposicao.unidadeMedidaExcipiente} onChange={handleInputChange} style={{ flex: 1, padding: '8px' }}>
                <option value="">Nenhum</option> {/* Opção vazia que enviará null */}
                {medidasBasicas.map(mb => (
                  <option key={mb.sigla} value={mb.sigla}>{mb.sigla}</option>
                ))}
              </select>
            </div>
          </div>

          <button type="submit" style={{ padding: '10px', cursor: 'pointer', backgroundColor: '#007BFF', color: 'white', border: 'none', borderRadius: '4px', fontWeight: 'bold' }}>
            Registar Composição
          </button>
        </form>
      </div>

      {/* Lista */}
      <h3 style={{ marginTop: '30px' }}>Composições Registadas</h3>
      {composicoes.length === 0 ? (
        <p>Nenhuma composição registada ainda...</p>
      ) : (
        <ul style={{ marginTop: '10px', listStyleType: 'none', padding: 0 }}>
          {composicoes.map((comp, index) => (
            <li key={index} style={{ padding: '12px', borderBottom: '1px solid #eee', backgroundColor: '#fff', marginBottom: '5px', borderRadius: '4px' }}>
              <strong>● {comp.principioAtivo} {comp.quantiaPrincipio}{comp.unidadeMedidaPrincipio} 
              {comp.quantiaExcipiente > 0 && ` / ${comp.quantiaExcipiente}${comp.unidadeMedidaExcipiente}`}</strong>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}