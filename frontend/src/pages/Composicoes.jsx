import { useState, useEffect } from 'react';
import api from '../services/api';
import { toast } from 'react-toastify';
import Select from 'react-select';
import PrincipiosAtivos from './PrincipiosAtivos'; 

export default function Composicoes() {
  const [composicoes, setComposicoes] = useState([]);
  const [principios, setPrincipios] = useState([]);
  
  // NOVO: Estado para guardar as unidades de medida vindas do backend
  const [medidas, setMedidas] = useState([]);
  
  const [modalPrincipioAberto, setModalPrincipioAberto] = useState(false);

  const [novaComposicao, setNovaComposicao] = useState({
    idPrincipioAtivo: '',
    quantiaPrincipio: '',
    unidadeMedidaPrincipio: '',
    quantiaExcipiente: '',
    unidadeMedidaExcipiente: ''
  });

  const carregarPrincipios = () => {
    api.get('/api/produto/remedio/formulacao/principio_ativo')
      .then((res) => setPrincipios(res.data))
      .catch((err) => console.error("Erro ao buscar princípios ativos:", err));
  };

  const carregarComposicoes = () => {
    api.get('/api/produto/remedio/formulacao/composicao')
      .then((res) => setComposicoes(res.data))
      .catch((err) => console.error("Erro ao buscar composições:", err));
  };

  // NOVO: Função para buscar as medidas
  const carregarMedidas = () => {
    // Confirme se o caminho da URL é este mesmo
    api.get('/api/produto/remedio/formulacao/medida_basica')
      .then((res) => setMedidas(res.data))
      .catch((err) => console.error("Erro ao buscar medidas:", err));
  };

  useEffect(() => {
    carregarPrincipios();
    carregarComposicoes();
    carregarMedidas(); // Chama a função ao abrir a tela
  }, []);

  // Formatação para o react-select
  const optionsPrincipio = principios.map(prin => ({ 
    value: prin.id, 
    label: prin.nome 
  }));

  // NOVO: Formatação das medidas para o Select
  const optionsMedida = medidas.map(med => ({ 
    value: med.sigla, // Usamos a sigla como valor para salvar no banco
    label: `${med.sigla} - ${med.nome}` 
  }));

  const handleInputChange = (event) => {
    const { name, value } = event.target;
    setNovaComposicao({ ...novaComposicao, [name]: value });
  };

  const handleSelectChange = (selectedOption, actionMeta) => {
    const fieldName = actionMeta.name;
    const value = selectedOption ? selectedOption.value : '';
    setNovaComposicao({ ...novaComposicao, [fieldName]: value });
  };

  const salvarComposicao = (event) => {
    event.preventDefault();

    // 1. Buscamos o objeto completo do princípio ativo na nossa lista 'principios' usando o ID selecionado
    const principioObj = principios.find(p => p.id === Number(novaComposicao.idPrincipioAtivo));
    
    // 2. Extraímos o nome (ajuste para 'descricao' ou o campo correspondente se necessário)
    const nomePrincipioAtivo = principioObj ? principioObj.nome : '';

    // 3. Montamos o payload EXATAMENTE no formato que o Swagger especificou
    const payload = {
      principioAtivo: nomePrincipioAtivo, // <-- Injetando a String com o nome em vez do ID!
      quantiaPrincipio: Number(novaComposicao.quantiaPrincipio),
      unidadeMedidaPrincipio: novaComposicao.unidadeMedidaPrincipio,
      
      // Tratamento dos campos opcionais
      quantiaExcipiente: novaComposicao.quantiaExcipiente ? Number(novaComposicao.quantiaExcipiente) : null,
      unidadeMedidaExcipiente: novaComposicao.unidadeMedidaExcipiente || null
    };

    // Envia o pacote corrigido para o servidor
    api.post('/api/produto/remedio/formulacao/composicao', payload)
      .then(() => {
        toast.success("Composição registrada com sucesso!");
        // Limpa o formulário após o sucesso
        setNovaComposicao({
          idPrincipioAtivo: '', quantiaPrincipio: '', unidadeMedidaPrincipio: '',
          quantiaExcipiente: '', unidadeMedidaExcipiente: ''
        });
        carregarComposicoes();
      })
      .catch((error) => {
        console.error("Erro detalhado do backend:", error.response?.data);
        toast.error("Erro ao registrar a composição.");
      });
  };

  return (
    <div>
      <h2>Gestão de Composições</h2>
      
      <div style={{ border: '1px solid #ccc', padding: '20px', marginTop: '20px', borderRadius: '5px', maxWidth: '600px', backgroundColor: '#fff' }}>
        <form onSubmit={salvarComposicao} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
          
          <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '5px' }}>
              <label style={{ fontWeight: 'bold' }}>Princípio Ativo Base:</label>
              <button 
                type="button" 
                onClick={() => setModalPrincipioAberto(true)} 
                style={{ padding: '2px 8px', fontSize: '0.85em', backgroundColor: '#17a2b8', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}
              >
                + Novo Princípio
              </button>
            </div>
            
            <Select 
              name="idPrincipioAtivo"
              options={optionsPrincipio}
              value={optionsPrincipio.find(opt => opt.value === novaComposicao.idPrincipioAtivo) || null}
              onChange={handleSelectChange}
              placeholder="Digite para buscar o Princípio Ativo..."
              isClearable
              required
            />
          </div>

          <div style={{ display: 'flex', gap: '10px' }}>
            <div style={{ flex: 1 }}>
              <label>Quantia do Princípio:</label>
              <input type="number" step="0.01" name="quantiaPrincipio" value={novaComposicao.quantiaPrincipio} onChange={handleInputChange} required style={{ width: '100%', padding: '8px', marginTop: '5px' }} />
            </div>
            
            {/* NOVO SELECT: Unidade de Medida do Princípio */}
            <div style={{ flex: 1 }}>
              <label>Unidade de Medida:</label>
              <div style={{ marginTop: '5px' }}>
                <Select 
                  name="unidadeMedidaPrincipio"
                  options={optionsMedida}
                  value={optionsMedida.find(opt => opt.value === novaComposicao.unidadeMedidaPrincipio) || null}
                  onChange={handleSelectChange}
                  placeholder="Ex: mg, ml..."
                  isClearable
                  required
                />
              </div>
            </div>
          </div>

          <div style={{ display: 'flex', gap: '10px' }}>
            <div style={{ flex: 1 }}>
              <label>Quantia Excipiente (Opcional):</label>
              <input type="number" step="0.01" name="quantiaExcipiente" value={novaComposicao.quantiaExcipiente} onChange={handleInputChange} style={{ width: '100%', padding: '8px', marginTop: '5px' }} />
            </div>
            
            {/* NOVO SELECT: Unidade de Medida do Excipiente */}
            <div style={{ flex: 1 }}>
              <label>Unid. Excipiente (Opcional):</label>
              <div style={{ marginTop: '5px' }}>
                <Select 
                  name="unidadeMedidaExcipiente"
                  options={optionsMedida}
                  value={optionsMedida.find(opt => opt.value === novaComposicao.unidadeMedidaExcipiente) || null}
                  onChange={handleSelectChange}
                  placeholder="Ex: g, gotas..."
                  isClearable
                  // Note que este Select NÃO TEM o "required", pois é opcional
                />
              </div>
            </div>
          </div>

          <button type="submit" style={{ padding: '10px', backgroundColor: '#28a745', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold' }}>
            Salvar Composição
          </button>
        </form>
      </div>

      {modalPrincipioAberto && (
        <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, backgroundColor: 'rgba(0,0,0,0.7)', display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 1020 }}>
          
          <div style={{ backgroundColor: '#fff', padding: '30px', borderRadius: '8px', maxWidth: '600px', width: '100%', maxHeight: '90vh', overflowY: 'auto', position: 'relative', boxShadow: '0 5px 25px rgba(0,0,0,0.3)' }}>
            
            <button 
              type="button"
              onClick={() => {
                setModalPrincipioAberto(false);
                carregarPrincipios(); 
              }} 
              style={{ position: 'absolute', top: '15px', right: '15px', padding: '5px 10px', backgroundColor: '#dc3545', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold' }}
            >
              X Fechar
            </button>

            <div style={{ marginTop: '10px' }}>
              <PrincipiosAtivos /> 
            </div>

          </div>
        </div>
      )}
    </div>
  );
}