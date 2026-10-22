import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { toast } from 'react-toastify';
import api from '../services/api';
import { CONTROLES_RECEITA, formatarMoeda, normalizarTexto } from '../utils/formatacao';
import { campo, tabela, td, textoAjuda, th } from '../utils/estilos';

// Texto em que a busca procura: nome, marca, fórmula (princípios ativos) e EAN, já normalizado
const textoPesquisavel = (produto) =>
  normalizarTexto([produto.nomeComercial, produto.marca, produto.formula, produto.ean].filter(Boolean).join(' '));

export default function Inicio() {
  const [produtos, setProdutos] = useState([]);
  const [busca, setBusca] = useState('');
  const [carregando, setCarregando] = useState(true);

  useEffect(() => {
    api.get('/api/catalogo')
      .then((response) => setProdutos((response.data || []).map((produto) => ({ ...produto, pesquisavel: textoPesquisavel(produto) }))))
      .catch((error) => {
        console.error('Erro ao carregar o catálogo:', error);
        toast.error('Não foi possível carregar os produtos.');
      })
      .finally(() => setCarregando(false));
  }, []);

  // Cada palavra digitada precisa aparecer em algum campo: "dipirona 500" encontra "Dipirona ... 500 mg"
  const palavras = normalizarTexto(busca).split(/\s+/).filter(Boolean);
  const produtosFiltrados = produtos.filter((produto) => palavras.every((palavra) => produto.pesquisavel.includes(palavra)));

  return (
    <div className="content-area">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: '20px', flexWrap: 'wrap', marginBottom: '20px' }}>
        <div>
          <h2 style={{ margin: 0 }}>Produtos</h2>
          <p style={{ margin: '5px 0 0' }}>Consulte estoque e preço de qualquer produto pelo nome, marca, princípio ativo ou EAN.</p>
        </div>
        <Link to="/vendas/caixa"
          style={{ padding: '14px 28px', backgroundColor: '#28a745', color: '#fff', borderRadius: '6px', textDecoration: 'none', fontWeight: 'bold', fontSize: '1.1em', boxShadow: '0 2px 4px rgba(0,0,0,0.15)' }}>
          🛒 Abrir Caixa (PDV)
        </Link>
      </div>

      <div style={{ marginBottom: '10px' }}>
        <input autoFocus value={busca} onChange={(event) => setBusca(event.target.value)}
          placeholder="🔍 Buscar por nome, marca, princípio ativo ou EAN (ex: dipirona)"
          style={{ ...campo, padding: '12px', fontSize: '1.05em' }} />
        <div style={{ ...textoAjuda, marginTop: '6px' }}>
          {carregando ? 'Carregando produtos...'
            : busca ? `${produtosFiltrados.length} de ${produtos.length} produto(s) encontrado(s)` : `${produtos.length} produto(s) cadastrado(s)`}
        </div>
      </div>

      {!carregando && produtosFiltrados.length === 0 ? (
        <p style={{ color: '#777', fontStyle: 'italic' }}>
          {produtos.length === 0 ? 'Nenhum produto cadastrado.' : 'Nenhum produto corresponde à busca.'}
        </p>
      ) : (
        <div style={{ overflowX: 'auto' }}>
          <table style={tabela}>
            <thead>
              <tr>
                <th style={th}>Nome comercial</th>
                <th style={th}>Marca</th>
                <th style={th}>Fórmula</th>
                <th style={{ ...th, textAlign: 'right' }}>Estoque</th>
                <th style={{ ...th, textAlign: 'right' }}>Preço atual</th>
              </tr>
            </thead>
            <tbody>
              {produtosFiltrados.map((produto) => (
                <tr key={produto.ean}>
                  <td style={td}>
                    <strong>{produto.nomeComercial}</strong>
                    <div style={textoAjuda}>
                      EAN {produto.ean}
                      {CONTROLES_RECEITA[produto.tipoControle] && (
                        <span style={{ marginLeft: '8px', padding: '1px 6px', borderRadius: '8px', backgroundColor: '#fff3cd', color: '#856404', fontSize: '0.9em' }}>
                          {CONTROLES_RECEITA[produto.tipoControle]}
                        </span>
                      )}
                    </div>
                  </td>
                  <td style={td}>{produto.marca || <span style={{ color: '#999' }}>—</span>}</td>
                  <td style={td}>{produto.formula || <span style={{ color: '#999' }}>—</span>}</td>
                  <td style={{ ...td, textAlign: 'right', whiteSpace: 'nowrap', fontWeight: 'bold', color: produto.saldoDisponivel > 0 ? 'inherit' : '#dc3545' }}>
                    {produto.saldoDisponivel > 0 ? `${produto.saldoDisponivel} ${produto.unidadeDeMedida}` : 'Sem estoque'}
                  </td>
                  <td style={{ ...td, textAlign: 'right', whiteSpace: 'nowrap', fontWeight: 'bold' }}>
                    {produto.precoAtual !== null ? formatarMoeda(produto.precoAtual) : <span style={{ color: '#999', fontWeight: 'normal' }}>sem preço</span>}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
