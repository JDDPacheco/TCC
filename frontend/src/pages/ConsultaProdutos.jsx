import { useState, useEffect } from 'react';
import api from '../services/api';
import { toast } from 'react-toastify';

export default function ConsultaProdutos() {
  const [produtos, setProdutos] = useState([]);
  const [filtroPesquisa, setFiltroPesquisa] = useState('');
  
  // Estados para o Modal de Detalhes
  const [produtoSelecionado, setProdutoSelecionado] = useState(null);
  const [modalAberto, setModalAberto] = useState(false);

  const carregarProdutos = () => {
    // Ajuste esta rota caso o seu Spring Boot use um endpoint diferente para listar todos
    api.get('/api/produto')
      .then((response) => setProdutos(response.data))
      .catch((error) => {
        console.error("Erro ao buscar produtos:", error);
        toast.error("Não foi possível carregar a lista de produtos.");
      });
  };

  useEffect(() => {
    carregarProdutos();
  }, []);

  // Lógica do Filtro: Pesquisa por Nome Comercial ou EAN
  const produtosFiltrados = produtos.filter((prod) => {
    const termo = filtroPesquisa.toLowerCase();
    const nomeMatch = prod.nomeComercial?.toLowerCase().includes(termo);
    const eanMatch = prod.ean?.includes(termo);
    return nomeMatch || eanMatch;
  });

  const abrirDetalhes = (produto) => {
    setProdutoSelecionado(produto);
    setModalAberto(true);
  };

  const fecharDetalhes = () => {
    setProdutoSelecionado(null);
    setModalAberto(false);
  };

  return (
    <div>
      <h2>Consulta de Produtos</h2>
      <p>Pesquise e visualize os detalhes do catálogo.</p>

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
                
                <button 
                  type="button"
                  onClick={() => abrirDetalhes(prod)}
                  style={{ padding: '8px 15px', cursor: 'pointer', backgroundColor: '#007bff', color: 'white', border: 'none', borderRadius: '4px', fontWeight: 'bold' }}
                >
                  👁️ Ver Detalhes
                </button>
              </li>
            ))}
          </ul>
        )}
      </div>

      {/* ========================================================= */}
      {/* MODAL DE DETALHES DO PRODUTO                              */}
      {/* ========================================================= */}
      {modalAberto && produtoSelecionado && (
        <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, backgroundColor: 'rgba(0,0,0,0.6)', display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 1000 }}>
          
          <div style={{ backgroundColor: '#fff', padding: '30px', borderRadius: '8px', maxWidth: '700px', width: '100%', maxHeight: '90vh', overflowY: 'auto', position: 'relative', boxShadow: '0 5px 25px rgba(0,0,0,0.3)' }}>
            
            <button 
              type="button"
              onClick={fecharDetalhes} 
              style={{ position: 'absolute', top: '15px', right: '15px', padding: '5px 10px', backgroundColor: '#dc3545', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold' }}
            >
              X Fechar
            </button>

            <h3 style={{ marginTop: 0, color: '#004085', borderBottom: '2px solid #e6f2ff', paddingBottom: '10px' }}>
              Ficha do Produto
            </h3>

            {/* DADOS GLOBAIS (COMUNS A TODOS) */}
            <div style={{ marginBottom: '20px', display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px' }}>
              <div><strong>Nome Comercial:</strong> {produtoSelecionado.nomeComercial}</div>
              <div><strong>Código EAN:</strong> {produtoSelecionado.ean}</div>
              <div><strong>Tipo do Produto:</strong> {produtoSelecionado.tipoProduto?.toUpperCase()}</div>
              <div><strong>Unidade Padrão:</strong> {produtoSelecionado.unidadeDeMedida}</div>
            </div>

            {/* DADOS ESPECÍFICOS DE MEDICAMENTO / GENÉRICO */}
            {(produtoSelecionado.tipoProduto === 'remedio' || produtoSelecionado.tipoProduto === 'generico') && (
              <div style={{ padding: '15px', backgroundColor: '#f8f9fa', borderRadius: '5px', border: '1px solid #dee2e6' }}>
                <h4 style={{ margin: '0 0 15px 0', color: '#495057' }}>Dados Farmacêuticos</h4>
                
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px', fontSize: '0.95em' }}>
                  {/* Nota: Ajuste os nomes das variáveis abaixo conforme o que o seu GET /api/produto devolve (ex: se devolve o objeto laboratório ou só o ID) */}
                  <div><strong>Laboratório:</strong> {produtoSelecionado.nomeLaboratorio}</div>
                  <div><strong>Fórmula:</strong> {produtoSelecionado.formula}</div>
                  <div><strong>Controle:</strong> {produtoSelecionado.tipoControle?.replace('_', ' ').toUpperCase()}</div>
                  <div><strong>Apresentação:</strong> {produtoSelecionado.apresentacao}</div>
                </div>

                <h5 style={{ marginTop: '15px', marginBottom: '10px', color: '#6c757d' }}>Dosagem e Embalagem</h5>
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '10px', fontSize: '0.9em', backgroundColor: '#fff', padding: '10px', borderRadius: '4px', border: '1px solid #e9ecef' }}>
                  <div>
                    <strong>Doses:</strong><br/> 
                    {produtoSelecionado.quantidadeDoses} {produtoSelecionado.siglaMedidaDoses || ''}
                  </div>
                  <div>
                    <strong>Conteúdo:</strong><br/> 
                    {produtoSelecionado.conteudo} {produtoSelecionado.siglaMedidaConteudo || ''}
                  </div>
                  <div>
                    <strong>Peso Líq:</strong><br/> 
                    {produtoSelecionado.pesoLiquido ? `${produtoSelecionado.pesoLiquido} g/mg` : 'N/A'}
                  </div>
                </div>
              </div>
            )}

          </div>
        </div>
      )}

    </div>
  );
}