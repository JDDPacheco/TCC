import { useState, useEffect } from 'react';
import api from '../services/api';
import { toast } from 'react-toastify';

export default function Laboratorios() {

  //** Definição dos estados da Tela. */ 
  const [laboratorios, setLaboratorios] = useState([]); // inicia Laboratórios como uma lista vazia
  const [novoLab, setNovoLab] = useState({ marca: '', nomeFantasia: '' }); // inicia o JSON do novo laboratório com campos inicialmente vazios
  const [idEditando, setIdEditando] = useState(null); // inicia o estado de edição vazio, ou seja, inicialmente não está ocorrendo edição

  //** Definição das funções da Tela */

  // Função para preencher a lista de Laboratórios
  // Faz a solicitação GET para a API e recebe a resposta
  const carregarLaboratorios = () => {
    api.get('/api/produto/remedio/laboratorio')
      .then((response) => setLaboratorios(response.data))
      .catch((error) => console.error("Erro ao buscar laboratórios:", error));
  };

  useEffect(() => { // usamos o useEffect para definir como usar as funções, a função carregarLaboratorios vai ser feita apenas uma vez quando a tela iniciar devido ao[]
    carregarLaboratorios();
  }, []);

  // Função para preenchimento do formulário
  // Lida com os campos do formulário
  const handleInputChange = (event) => {
    const { name, value } = event.target;
    setNovoLab({ ...novoLab, [name]: value });
  };

  // Funções para edição de registros
  // Prepara o formulário para edição
  const prepararEdicao = (lab) => {
    setNovoLab({ marca: lab.marca, nomeFantasia: lab.nomeFantasia }); // prepara o JSON com os dados do laboratório selecionado para edição
    setIdEditando(lab.id); // coloca o valor do ID do Laboratorio no estado idEditando, travando o estado em edição
  };

  // Cancela a edição e limpa tudo
  const cancelarEdicao = () => {
    setNovoLab({ marca: '', nomeFantasia: '' }); // deixa vazios os valores do JSON do novo laboratório
    setIdEditando(null); // torna nulo, desttiva, o estado de edição
  };

  // Salva o novo laboratório (POST) ou edita um já existente quando em edição (PUT)
  const salvarLaboratorio = (event) => {
    event.preventDefault(); // evita que o navegador recarregue a página ao clicar no submit do HTML

    if (idEditando) {
      // MODO EDIÇÃO: Faz o PUT (passando o ID na URL)
      api.put(`/api/produto/remedio/laboratorio/${idEditando}`, novoLab)
        .then(() => {
          toast.success("Laboratório atualizado com sucesso!");
          cancelarEdicao(); // Limpa e sai do modo edição
          carregarLaboratorios(); // Recarrega a lista com os dados novos
        })
        .catch((error) => {
          console.error("Erro ao atualizar:", error);
          toast.error("Erro ao atualizar o laboratório.");
        });
    } else {
      // MODO CRIAÇÃO: Faz o POST
      api.post('/api/produto/remedio/laboratorio', novoLab)
        .then(() => {
          toast.success("Laboratório cadastrado com sucesso!");
          cancelarEdicao(); // Usamos aqui só para limpar os campos
          carregarLaboratorios();
        })
        .catch((error) => {
          console.error("Erro ao cadastrar:", error);
          toast.error("Erro ao cadastrar o laboratório.");
        });
    }
  };

  return (
    <div>
      <h2>Gestão de Laboratórios</h2>
      
      {/* Formulário Dinâmico (Muda a cor da borda se estiver editando) */}
      <div style={{ border: idEditando ? '2px solid #ffc107' : '1px solid #ccc', padding: '15px', marginTop: '20px', borderRadius: '5px', maxWidth: '500px', backgroundColor: '#fff' }}>
        
        <h3 style={{ marginTop: 0, color: idEditando ? '#d39e00' : '#333' }}>
          {idEditando ? `Editando Laboratório (ID: ${idEditando})` : 'Cadastrar Novo Laboratório'}
        </h3>

        <form onSubmit={salvarLaboratorio} style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
          <input 
            type="text" name="marca" placeholder="Marca (ex: Geolab)" 
            value={novoLab.marca} onChange={handleInputChange} required style={{ padding: '8px' }}
          />
          <input 
            type="text" name="nomeFantasia" placeholder="Nome Fantasia" 
            value={novoLab.nomeFantasia} onChange={handleInputChange} required style={{ padding: '8px' }}
          />
          
          <div style={{ display: 'flex', gap: '10px', marginTop: '10px' }}>
            <button type="submit" style={{ flex: 1, padding: '8px', cursor: 'pointer', backgroundColor: idEditando ? '#ffc107' : '#28a745', color: idEditando ? '#000' : 'white', border: 'none', borderRadius: '4px', fontWeight: 'bold' }}>
              {idEditando ? 'Atualizar Laboratório' : 'Salvar Laboratório'}
            </button>

            {/* O botão cancelar só aparece no modo de edição */}
            {idEditando && (
              <button type="button" onClick={cancelarEdicao} style={{ padding: '8px 15px', cursor: 'pointer', backgroundColor: '#dc3545', color: 'white', border: 'none', borderRadius: '4px' }}>
                Cancelar
              </button>
            )}
          </div>
        </form>
      </div>

      <h3 style={{ marginTop: '30px' }}>Laboratórios Cadastrados</h3>
      {laboratorios.length === 0 ? (
        <p>Nenhum laboratório cadastrado...</p>
      ) : (
        <ul style={{ marginTop: '10px', listStyleType: 'none', padding: 0 }}>
          {laboratorios.map((lab) => (
            <li key={lab.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '10px', borderBottom: '1px solid #eee', backgroundColor: '#fff', marginBottom: '5px', borderRadius: '4px' }}>
              <span>
                <strong>ID:</strong> {lab.id} | <strong>Marca:</strong> {lab.marca} | <strong>Nome Fantasia:</strong> {lab.nomeFantasia}
              </span>
              
              {/* BOTÃO EDITAR NA LISTA */}
              <button onClick={() => prepararEdicao(lab)} style={{ padding: '5px 10px', cursor: 'pointer', backgroundColor: '#007bff', color: 'white', border: 'none', borderRadius: '4px', fontSize: '0.85em' }}>
                ✏️ Editar
              </button>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}