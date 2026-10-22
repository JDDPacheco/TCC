import { useEffect, useState } from 'react';
import { toast } from 'react-toastify';
import api from '../services/api';
import { CONTROLES_RECEITA, dataDeHoje, FORMAS_PAGAMENTO, formatarData, formatarDataHora, formatarMoeda, mensagemDeErro } from '../utils/formatacao';
import { botaoSecundario, campo, cartao, rotulo, tabela, td, textoAjuda, th } from '../utils/estilos';

// Vendas do período (lista vazia em caso de 204 ou erro)
const buscarVendas = (inicio, fim) =>
  api.get('/api/venda', { params: { inicio, fim } })
    .then((response) => response.data || [])
    .catch((error) => {
      console.error('Erro ao buscar vendas:', error);
      toast.error(mensagemDeErro(error, 'Não foi possível carregar as vendas.'));
      return [];
    });

const EtiquetaStatus = ({ status }) => (
  <span style={{
    padding: '2px 8px', borderRadius: '10px', fontSize: '0.85em', fontWeight: 'bold', whiteSpace: 'nowrap',
    color: status === 'CANCELADA' ? '#721c24' : '#155724', backgroundColor: status === 'CANCELADA' ? '#f8d7da' : '#d4edda'
  }}>
    {status === 'CANCELADA' ? 'Cancelada' : 'Concluída'}
  </span>
);

export default function HistoricoVendas() {
  const [inicio, setInicio] = useState(dataDeHoje());
  const [fim, setFim] = useState(dataDeHoje());
  const [vendas, setVendas] = useState([]);
  const [idSelecionada, setIdSelecionada] = useState(null);
  const [motivo, setMotivo] = useState('');
  const [cancelando, setCancelando] = useState(false);

  useEffect(() => {
    const hoje = dataDeHoje();
    buscarVendas(hoje, hoje).then(setVendas);
  }, []);

  const pesquisar = (event) => {
    event.preventDefault();
    if (fim < inicio) return toast.warning('A data final não pode ser anterior à data inicial.');
    setIdSelecionada(null);
    buscarVendas(inicio, fim).then(setVendas);
  };

  const selecionada = vendas.find((venda) => venda.id === idSelecionada);

  const cancelarVenda = async () => {
    if (!motivo.trim()) return toast.warning('Informe o motivo do cancelamento.');
    if (!window.confirm(`Cancelar a venda nº ${selecionada.id}? Os itens voltarão ao estoque.`)) return;

    setCancelando(true);
    try {
      const { data: cancelada } = await api.post(`/api/venda/${selecionada.id}/cancelamento`, { motivo: motivo.trim() });
      setVendas(vendas.map((venda) => (venda.id === cancelada.id ? cancelada : venda)));
      setMotivo('');
      toast.success(`Venda nº ${cancelada.id} cancelada. Os itens voltaram ao estoque.`);
    } catch (error) {
      console.error('Erro ao cancelar venda:', error);
      toast.error(mensagemDeErro(error, 'Erro ao cancelar a venda.'));
    } finally {
      setCancelando(false);
    }
  };

  const unidadesVendidas = (venda) => venda.itens.reduce((soma, item) => soma + item.quantidade, 0);

  return (
    <div>
      <h2>Histórico de Vendas</h2>
      <p>Consulte as vendas de um período. Clique em uma venda para ver os itens ou cancelá-la.</p>

      <form onSubmit={pesquisar} style={{ ...cartao, display: 'flex', gap: '15px', alignItems: 'flex-end', flexWrap: 'wrap' }}>
        <div>
          <label style={rotulo}>De</label>
          <input type="date" value={inicio} onChange={(event) => setInicio(event.target.value)} style={campo} />
        </div>
        <div>
          <label style={rotulo}>Até</label>
          <input type="date" value={fim} onChange={(event) => setFim(event.target.value)} style={campo} />
        </div>
        <button type="submit" style={botaoSecundario}>Pesquisar</button>
      </form>

      {selecionada && (
        <div style={{ ...cartao, border: '2px solid #17a2b8' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
            <div>
              <h3 style={{ margin: 0 }}>Venda nº {selecionada.id} <EtiquetaStatus status={selecionada.status} /></h3>
              <span style={textoAjuda}>{formatarDataHora(selecionada.dataVenda)} · {FORMAS_PAGAMENTO[selecionada.formaPagamento]}</span>
            </div>
            <button type="button" onClick={() => setIdSelecionada(null)} style={botaoSecundario}>Fechar</button>
          </div>

          <table style={{ ...tabela, marginTop: '15px' }}>
            <thead>
              <tr>
                <th style={th}>Produto</th>
                <th style={th}>Lote</th>
                <th style={{ ...th, textAlign: 'right' }}>Qtd.</th>
                <th style={{ ...th, textAlign: 'right' }}>Preço</th>
                <th style={{ ...th, textAlign: 'right' }}>Subtotal</th>
              </tr>
            </thead>
            <tbody>
              {selecionada.itens.map((item) => (
                <tr key={item.id}>
                  <td style={td}>
                    {item.nomeProduto}
                    <div style={textoAjuda}>EAN {item.ean}</div>
                    {item.tipoControle && (
                      <div style={{ ...textoAjuda, color: '#856404' }}>
                        {CONTROLES_RECEITA[item.tipoControle]} · receita apresentada
                        {item.receita && ` · paciente ${item.receita.nomePaciente} · ${item.receita.nomePrescritor} (${item.receita.registroPrescritor}) · receita nº ${item.receita.numeroReceita}`}
                      </div>
                    )}
                  </td>
                  <td style={td}>{item.codigoLote}{item.dataValidade && <div style={textoAjuda}>val. {formatarData(item.dataValidade)}</div>}</td>
                  <td style={{ ...td, textAlign: 'right' }}>{item.quantidade}</td>
                  <td style={{ ...td, textAlign: 'right' }}>{formatarMoeda(item.precoUnitario)}</td>
                  <td style={{ ...td, textAlign: 'right' }}>{formatarMoeda(item.subtotal)}</td>
                </tr>
              ))}
            </tbody>
          </table>

          <div style={{ textAlign: 'right', marginTop: '10px' }}>
            <div style={textoAjuda}>Subtotal: {formatarMoeda(selecionada.valorBruto)}</div>
            {Number(selecionada.desconto) > 0 && <div style={textoAjuda}>Desconto: − {formatarMoeda(selecionada.desconto)}</div>}
            <div style={{ fontSize: '1.3em', fontWeight: 'bold' }}>Total: {formatarMoeda(selecionada.valorTotal)}</div>
            {selecionada.valorRecebido !== null && (
              <div style={textoAjuda}>Recebido: {formatarMoeda(selecionada.valorRecebido)} · Troco: {formatarMoeda(selecionada.troco)}</div>
            )}
          </div>

          {selecionada.status === 'CANCELADA' ? (
            <p style={{ backgroundColor: '#f8d7da', padding: '10px', borderRadius: '4px', marginBottom: 0 }}>
              Cancelada em {formatarDataHora(selecionada.dataCancelamento)}. Motivo: {selecionada.motivoCancelamento}
            </p>
          ) : (
            <div style={{ display: 'flex', gap: '10px', marginTop: '15px', alignItems: 'flex-end' }}>
              <div style={{ flex: 1 }}>
                <label style={rotulo}>Motivo do cancelamento</label>
                <input value={motivo} onChange={(event) => setMotivo(event.target.value)} placeholder="Ex: Cliente desistiu da compra" style={campo} />
              </div>
              <button type="button" onClick={cancelarVenda} disabled={cancelando}
                style={{ ...botaoSecundario, backgroundColor: '#dc3545', color: '#fff', border: 'none', borderRadius: '4px', fontWeight: 'bold' }}>
                {cancelando ? 'Cancelando...' : 'Cancelar venda'}
              </button>
            </div>
          )}
        </div>
      )}

      {vendas.length === 0 ? (
        <p style={{ color: '#777', fontStyle: 'italic' }}>Nenhuma venda no período.</p>
      ) : (
        <table style={{ ...tabela, maxWidth: '900px' }}>
          <thead>
            <tr>
              <th style={th}>Nº</th>
              <th style={th}>Data/hora</th>
              <th style={{ ...th, textAlign: 'right' }}>Unidades</th>
              <th style={th}>Pagamento</th>
              <th style={{ ...th, textAlign: 'right' }}>Total</th>
              <th style={th}>Situação</th>
            </tr>
          </thead>
          <tbody>
            {vendas.map((venda) => (
              <tr key={venda.id} onClick={() => { setIdSelecionada(venda.id); setMotivo(''); }}
                style={{ cursor: 'pointer', backgroundColor: venda.id === idSelecionada ? '#e8f6f8' : undefined }}>
                <td style={td}>{venda.id}</td>
                <td style={td}>{formatarDataHora(venda.dataVenda)}</td>
                <td style={{ ...td, textAlign: 'right' }}>{unidadesVendidas(venda)}</td>
                <td style={td}>{FORMAS_PAGAMENTO[venda.formaPagamento]}</td>
                <td style={{ ...td, textAlign: 'right', fontWeight: 'bold', textDecoration: venda.status === 'CANCELADA' ? 'line-through' : 'none' }}>
                  {formatarMoeda(venda.valorTotal)}
                </td>
                <td style={td}><EtiquetaStatus status={venda.status} /></td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
