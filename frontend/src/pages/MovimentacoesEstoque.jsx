import { useEffect, useState } from 'react';
import { toast } from 'react-toastify';
import api from '../services/api';
import SeletorProduto from '../components/SeletorProduto';
import { formatarData, formatarDataHora, formatarMoeda, mensagemDeErro, TIPOS_MOVIMENTACAO } from '../utils/formatacao';
import { cartao, rotulo, tabela, td, textoAjuda, th } from '../utils/estilos';

export default function MovimentacoesEstoque() {
  const [produto, setProduto] = useState(null);
  const [movimentacoes, setMovimentacoes] = useState([]);
  const [tipo, setTipo] = useState('');

  // Sem produto selecionado, traz o histórico completo
  useEffect(() => {
    api.get('/api/estoque/movimentacoes', { params: produto ? { ean: produto.ean } : {} })
      .then((response) => setMovimentacoes(response.data || [])) // 204 (sem movimentações) vem sem corpo
      .catch((error) => {
        console.error('Erro ao carregar movimentações:', error);
        toast.error(mensagemDeErro(error, 'Não foi possível carregar as movimentações.'));
      });
  }, [produto]);

  const movimentacoesFiltradas = tipo ? movimentacoes.filter((mov) => mov.tipo === tipo) : movimentacoes;

  return (
    <div>
      <h2>Movimentações de Estoque</h2>
      <p>Histórico de todas as alterações de saldo, da mais recente para a mais antiga. Nenhum registro é apagado ou editado.</p>

      <div style={{ ...cartao, display: 'flex', gap: '15px', flexWrap: 'wrap' }}>
        <div style={{ flex: 3, minWidth: '260px' }}>
          <label style={rotulo}>Produto (vazio = todos)</label>
          <SeletorProduto ean={produto?.ean} onChange={setProduto} />
        </div>
        <div style={{ flex: 1, minWidth: '180px' }}>
          <label style={rotulo}>Tipo</label>
          <select value={tipo} onChange={(event) => setTipo(event.target.value)} style={{ width: '100%', padding: '8px' }}>
            <option value="">Todos</option>
            {Object.entries(TIPOS_MOVIMENTACAO).map(([chave, texto]) => <option key={chave} value={chave}>{texto}</option>)}
          </select>
        </div>
      </div>

      {movimentacoesFiltradas.length === 0 ? (
        <p style={{ color: '#777', fontStyle: 'italic' }}>Nenhuma movimentação encontrada.</p>
      ) : (
        <div style={{ overflowX: 'auto' }}>
          <table style={tabela}>
            <thead>
              <tr>
                <th style={th}>Data/hora</th>
                <th style={th}>Produto</th>
                <th style={th}>Lote</th>
                <th style={th}>Tipo</th>
                <th style={{ ...th, textAlign: 'right' }}>Quantidade</th>
                <th style={{ ...th, textAlign: 'right' }}>Saldo após</th>
                <th style={th}>Fornecedor / custo</th>
                <th style={th}>Observação</th>
              </tr>
            </thead>
            <tbody>
              {movimentacoesFiltradas.map((mov) => (
                <tr key={mov.id}>
                  <td style={{ ...td, whiteSpace: 'nowrap' }}>{formatarDataHora(mov.dataHora)}</td>
                  <td style={td}>{mov.nomeProduto}<div style={textoAjuda}>EAN {mov.ean}</div></td>
                  <td style={td}>{mov.codigoLote}{mov.dataValidade && <div style={textoAjuda}>val. {formatarData(mov.dataValidade)}</div>}</td>
                  <td style={td}>{TIPOS_MOVIMENTACAO[mov.tipo]}</td>
                  <td style={{ ...td, textAlign: 'right', fontWeight: 'bold', color: mov.quantidade > 0 ? '#28a745' : '#dc3545' }}>
                    {mov.quantidade > 0 ? '+' : ''}{mov.quantidade}
                  </td>
                  <td style={{ ...td, textAlign: 'right' }}>{mov.saldoApos}</td>
                  <td style={td}>
                    {mov.fornecedor ? <>{mov.fornecedor}<div style={textoAjuda}>{formatarMoeda(mov.custoUnitario)} / un.</div></> : '—'}
                  </td>
                  <td style={td}>{mov.observacao || '—'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
