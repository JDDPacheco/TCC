import { useState, useEffect } from 'react';
import api from '../services/api';
import { toast } from 'react-toastify';

export default function PrincipiosAtivos() {
  const [principios, setPrincipios] = useState([]);
  const [novoPrincipio, setNovoPrincipio] = useState({ nome: '' });
  const [idEditando, setIdEditando] = useState(null);

  // 1. NOVO ESTADO: Guarda o texto que o usuário digita na barra de busca
  const [filtroPesquisa, setFiltroPesquisa] = useState('');

  const carregarPrincipios = () => {
    api.get('/api/produto/remedio/formulacao/principio_ativo')
      .then((response) => setPrincipios(response.data))
      .catch((error) => console.error("Erro ao buscar princípios ativos:", error));
  };

  useEffect(() => {
    carregarPrincipios();
  }, []);

  const handleInputChange = (event) => {
    const { name, value } = event.target;
    setNovoPrincipio({ ...novoPrincipio, [name]: value });
  };

  const prepararEdicao = (principio) => {
    const confirmacao = window.confirm(
      "⚠️ ATENÇÃO! AVISO DE SEGURANÇA ⚠️\n\n" +
      "Esta opção deve ser usada EXCLUSIVAMENTE para corrigir pequenos erros de digitação (ex: letras trocadas, acentos).\n\n" +
      "NUNCA mude completamente o nome para outro Princípio Ativo, pois isso adulterará a fórmula de TODOS os medicamentos atrelados a ele no banco de dados.\n\n" +
      "Você tem certeza que deseja apenas corrigir a ortografia deste registro?"
    );

    if (confirmacao) {
      setNovoPrincipio({ nome: principio.nome }); 
      setIdEditando(principio.id);
    }
  };

  const cancelarEdicao = () => {
    setNovoPrincipio({ nome: '' });
    setIdEditando(null);
  };

  const salvarPrincipio = (event) => {
    event.preventDefault();

    if (idEditando) {
      api.put(`/api/produto/remedio/formulacao/principio_ativo/${idEditando}`, novoPrincipio)
        .then(() => {
          toast.success("Ortografia corrigida com sucesso!");
          cancelarEdicao();
          carregarPrincipios();
        })
        .catch((error) => {
          console.error("Erro ao atualizar:", error);
          toast.error("Erro ao atualizar o princípio ativo.");
        });
    } else {
      api.post('/api/produto/remedio/formulacao/principio_ativo', novoPrincipio)
        .then(() => {
          toast.success("Princípio ativo cadastrado com sucesso!");
          setNovoPrincipio({ nome: '' });
          carregarPrincipios();
        })
        .catch((error) => {
          console.error("Erro ao cadastrar:", error);
          toast.error("Erro ao cadastrar o princípio ativo.");
        });
    }
  };

  // 2. MÁGICA DO FILTRO: Cria uma sublista filtrada em tempo real em memória
  const principiosFiltrados = principios.filter((principio) =>
    principio.nome.toLowerCase().includes(filtroPesquisa.toLowerCase())
  );

  return (
    <div>
      <h2>Gestão de Princípios Ativos</h2>
      <p>Gerencie as substâncias base das composições.</p>

      {/* Formulário */}
      <div style={{ border: idEditando ? '2px solid #ffc107' : '1px solid #ccc', padding: '20px', marginTop: '20px', borderRadius: '5px', maxWidth: '500px', backgroundColor: '#fff' }}>
        <h4 style={{ margin: '0 0 15px 0', color: idEditando ? '#d39e00' : '#333' }}>
          {idEditando ? `Corrigindo Ortografia` : 'Cadastrar Novo Princípio Ativo'}
        </h4>

        <form onSubmit={salvarPrincipio} style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
          <input 
            type="text" 
            name="nome" 
            placeholder="Nome (ex: Paracetamol)" 
            value={novoPrincipio.nome} 
            onChange={handleInputChange} 
            required 
            style={{ padding: '8px' }}
          />
          
          <div style={{ display: 'flex', gap: '10px', marginTop: '10px' }}>
            <button type="submit" style={{ flex: 1, padding: '10px', cursor: 'pointer', backgroundColor: idEditando ? '#ffc107' : '#28a745', color: idEditando ? '#000' : 'white', border: 'none', borderRadius: '4px', fontWeight: 'bold' }}>
              {idEditando ? 'Salvar Correção' : 'Salvar Princípio Ativo'}
            </button>

            {idEditando && (
              <button type="button" onClick={cancelarEdicao} style={{ padding: '10px 15px', cursor: 'pointer', backgroundColor: '#dc3545', color: 'white', border: 'none', borderRadius: '4px', fontWeight: 'bold' }}>
                Cancelar
              </button>
            )}
          </div>
        </form>
      </div>

      {/* SEÇÃO DA LISTAGEM COM PESQUISA */}
      <h3 style={{ marginTop: '30px' }}>Substâncias Registradas</h3>
      
      {/* 3. BARRA DE PESQUISA DINÂMICA */}
      <div style={{ marginBottom: '15px', maxWidth: '500px' }}>
        <input 
          type="text"
          placeholder="🔍 Digite para filtrar a lista abaixo..."
          value={filtroPesquisa}
          onChange={(e) => setFiltroPesquisa(e.target.value)}
          style={{ width: '100%', padding: '10px', borderRadius: '4px', border: '1px solid #ccc', boxSizing: 'border-box', fontSize: '0.95em' }}
        />
      </div>

      {/* RENDERIZAÇÃO DA LISTA FILTRADA */}
      {principiosFiltrados.length === 0 ? (
        <p style={{ color: '#777', fontStyle: 'italic' }}>Nenhum princípio ativo correspondente...</p>
      ) : (
        <ul style={{ marginTop: '10px', listStyleType: 'none', padding: 0, maxWidth: '500px' }}>
          {principiosFiltrados.map((principio) => (
            <li key={principio.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '12px', borderBottom: '1px solid #eee', backgroundColor: '#fff', marginBottom: '5px', borderRadius: '4px', boxShadow: '0 1px 3px rgba(0,0,0,0.05)' }}>
              
              {/* 4. SEGREDO REVELADO: O ID sumiu da tela! Fica guardado apenas na propriedade 'key' do React */}
              <span style={{ fontWeight: '500', color: '#333' }}>
                {principio.nome}
              </span>
              
              <button 
                type="button"
                onClick={() => prepararEdicao(principio)} 
                style={{ padding: '5px 10px', cursor: 'pointer', backgroundColor: '#ffc107', color: '#000', border: 'none', borderRadius: '4px', fontSize: '0.85em', fontWeight: 'bold' }}
              >
                ✏️ Corrigir
              </button>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}