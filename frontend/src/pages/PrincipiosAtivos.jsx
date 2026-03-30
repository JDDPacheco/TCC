import { useState, useEffect } from 'react';
import api from '../services/api';
import { toast } from 'react-toastify';

export default function PrincipiosAtivos() {
  const [principios, setPrincipios] = useState([]);
  const [nome, setNome] = useState('');

  const carregarPrincipios = () => {
    // Ajuste o endpoint se necessário
    api.get('/api/produto/remedio/formulacao/principio_ativo')
      .then((response) => setPrincipios(response.data))
      .catch((error) => console.error("Erro ao buscar princípios ativos:", error));
  };

  useEffect(() => {
    carregarPrincipios();
  }, []);

  const cadastrarPrincipio = (event) => {
    event.preventDefault();

    // Como o DTO só exige o campo 'nome', enviamos apenas ele
    api.post('/api/produto/remedio/formulacao/principio_ativo', { nome })
      .then(() => {
        toast.success("Princípio Ativo cadastrado com sucesso!");
        setNome(''); // Limpa o formulário
        carregarPrincipios(); // Recarrega a lista
      })
      .catch((error) => {
        console.error("Erro ao cadastrar:", error);
        toast.error("Erro ao cadastrar o princípio ativo.");
      });
  };

  return (
    <div>
      <h2>Gestão de Princípios Ativos</h2>
      <p>Cadastre e visualize os princípios ativos base para as fórmulas (ex: Paracetamol, Dipirona).</p>

      {/* Formulário */}
      <div style={{ border: '1px solid #ccc', padding: '15px', marginTop: '20px', borderRadius: '5px', maxWidth: '400px', backgroundColor: '#fff' }}>
        <form onSubmit={cadastrarPrincipio} style={{ display: 'flex', gap: '10px' }}>
          <input 
            type="text" 
            placeholder="Nome do Princípio Ativo" 
            value={nome} 
            onChange={(e) => setNome(e.target.value)} 
            required 
            style={{ flex: 1, padding: '8px' }}
          />
          <button type="submit" style={{ padding: '8px 15px', cursor: 'pointer', backgroundColor: '#28a745', color: 'white', border: 'none', borderRadius: '4px' }}>
            Salvar
          </button>
        </form>
      </div>

      {/* Lista */}
      <h3 style={{ marginTop: '30px' }}>Princípios Ativos Cadastrados</h3>
      {principios.length === 0 ? (
        <p>Nenhum princípio ativo cadastrado ainda...</p>
      ) : (
        <ul style={{ marginTop: '10px', listStyleType: 'none', padding: 0 }}>
          {principios.map((pa) => (
            <li key={pa.id} style={{ padding: '10px', borderBottom: '1px solid #eee', backgroundColor: '#fff', marginBottom: '5px', borderRadius: '4px' }}>
              <strong>● {pa.nome}</strong>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}