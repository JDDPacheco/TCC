import { useState, useEffect } from 'react';
import api from '../services/api';
import { toast } from 'react-toastify';

export default function Laboratorios() {
  const [laboratorios, setLaboratorios] = useState([]);
  
  // Confirme se o nome do campo é 'marca' ou 'nome' no seu backend
  const [novoLaboratorio, setNovoLaboratorio] = useState({
    marca: '',
    nomeFantasia: ''
  });
  const [idEditando, setIdEditando] = useState(null);

  // ESTADO DO FILTRO
  const [filtroPesquisa, setFiltroPesquisa] = useState('');

  const carregarLaboratorios = () => {
    api.get('/api/produto/remedio/laboratorio')
      .then((response) => setLaboratorios(response.data))
      .catch((error) => console.error("Erro ao buscar laboratórios:", error));
  };

  useEffect(() => {
    carregarLaboratorios();
  }, []);

  const handleInputChange = (event) => {
    const { name, value } = event.target;
    setNovoLaboratorio({ ...novoLaboratorio, [name]: value });
  };

  // BARREIRA DE SEGURANÇA
  const prepararEdicao = (laboratorio) => {
    const confirmacao = window.confirm(
      "⚠️ ATENÇÃO! AVISO DE SEGURANÇA ⚠️\n\n" +
      "Esta opção deve ser usada EXCLUSIVAMENTE para corrigir pequenos erros de digitação (ex: letras trocadas, acentos).\n\n" +
      "NUNCA mude completamente a marca para outro Laboratório, pois isso afetará o registro de TODOS os medicamentos vinculados a ele no banco de dados.\n\n" +
      "Você tem certeza que deseja apenas corrigir a ortografia deste registro?"
    );

    if (confirmacao) {
      setNovoLaboratorio({
        marca: laboratorio.marca,
        nomeFantasia: laboratorio.nomeFantasia
      });
      setIdEditando(laboratorio.id);
    }
  };

  const cancelarEdicao = () => {
    setNovoLaboratorio({ marca: '' });
    setIdEditando(null);
  };

  const salvarLaboratorio = (event) => {
    event.preventDefault();

    if (idEditando) {
      api.put(`/api/produto/remedio/laboratorio/${idEditando}`, novoLaboratorio)
        .then(() => {
          toast.success("Ortografia corrigida com sucesso!");
          cancelarEdicao();
          carregarLaboratorios();
        })
        .catch((error) => {
          console.error("Erro ao atualizar:", error);
          toast.error("Erro ao atualizar o laboratório.");
        });
    } else {
      api.post('/api/produto/remedio/laboratorio', novoLaboratorio)
        .then(() => {
          toast.success("Laboratório cadastrado com sucesso!");
          setNovoLaboratorio({ marca: '' });
          carregarLaboratorios();
        })
        .catch((error) => {
          console.error("Erro ao cadastrar:", error);
          toast.error("Erro ao cadastrar o laboratório.");
        });
    }
  };

  // LÓGICA DO FILTRO EM TEMPO REAL
  const laboratoriosFiltrados = laboratorios.filter((lab) =>
    lab.marca.toLowerCase().includes(filtroPesquisa.toLowerCase())
  );

  return (
    <div>
      <h2>Gestão de Laboratórios</h2>
      <p>Gerencie as marcas e fabricantes dos medicamentos.</p>

      {/* Formulário */}
      <div style={{ border: idEditando ? '2px solid #ffc107' : '1px solid #ccc', padding: '20px', marginTop: '20px', borderRadius: '5px', maxWidth: '500px', backgroundColor: '#fff' }}>
        <h4 style={{ margin: '0 0 15px 0', color: idEditando ? '#d39e00' : '#333' }}>
          {idEditando ? `Corrigindo Ortografia` : 'Cadastrar Novo Laboratório'}
        </h4>

        <form onSubmit={salvarLaboratorio} style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
          <label>
  Nome oficial do laboratório
          </label>

          <input
            type="text"
            name="nomeFantasia"
            placeholder="Ex: EUROFARMA LABORATÓRIOS S.A."
            value={novoLaboratorio.nomeFantasia}
            onChange={handleInputChange}
            required
            style={{ padding: '8px' }}
          />

          <label>
            Marca
          </label>

          <input
            type="text"
            name="marca"
            placeholder="Ex: Eurofarma"
            value={novoLaboratorio.marca}
            onChange={handleInputChange}
            required
            style={{ padding: '8px' }}
          />
          
          <div style={{ display: 'flex', gap: '10px', marginTop: '10px' }}>
            <button type="submit" style={{ flex: 1, padding: '10px', cursor: 'pointer', backgroundColor: idEditando ? '#ffc107' : '#28a745', color: idEditando ? '#000' : 'white', border: 'none', borderRadius: '4px', fontWeight: 'bold' }}>
              {idEditando ? 'Salvar Correção' : 'Salvar Laboratório'}
            </button>

            {idEditando && (
              <button type="button" onClick={cancelarEdicao} style={{ padding: '10px 15px', cursor: 'pointer', backgroundColor: '#dc3545', color: 'white', border: 'none', borderRadius: '4px', fontWeight: 'bold' }}>
                Cancelar
              </button>
            )}
          </div>
        </form>
      </div>

      <h3 style={{ marginTop: '30px' }}>Laboratórios Registrados</h3>
      
      {/* BARRA DE PESQUISA DINÂMICA */}
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
      {laboratoriosFiltrados.length === 0 ? (
        <p style={{ color: '#777', fontStyle: 'italic' }}>Nenhum laboratório correspondente...</p>
      ) : (
        <ul style={{ marginTop: '10px', listStyleType: 'none', padding: 0, maxWidth: '500px' }}>
          {laboratoriosFiltrados.map((lab) => (
            <li key={lab.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '12px', borderBottom: '1px solid #eee', backgroundColor: '#fff', marginBottom: '5px', borderRadius: '4px', boxShadow: '0 1px 3px rgba(0,0,0,0.05)' }}>
              
              {/* O ID FOI OCULTADO DA INTERFACE */}
              <div>
                <div style={{ fontWeight: '600', color: '#333' }}>
                  {lab.marca}
                </div>

                <div style={{ fontSize: '0.85em', color: '#777', marginTop: '3px' }}>
                  {lab.nomeFantasia}
                </div>
              </div>
              
              <button 
                type="button"
                onClick={() => prepararEdicao(lab)} 
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