import { useState } from 'react';
import { toast } from 'react-toastify';
import api from '../services/api';
import SeletorProduto from '../components/SeletorProduto';
import EtiquetaSituacao from '../components/EtiquetaSituacao';
import { formatarData, mensagemDeErro, TIPOS_MOVIMENTACAO } from '../utils/formatacao';
import { botaoPrimario, campo, cartao, rotulo, tabela, td, textoAjuda, th } from '../utils/estilos';

// Cada operação: endpoint, campo de quantidade enviado e textos da tela
const OPERACOES = {
  ajuste: {
    titulo: 'Ajuste de inventário',
    mensagemSucesso: 'Ajuste registrado',
    descricao: 'Informe a quantidade contada fisicamente; o sistema registra a diferença em relação ao saldo.',
    endpoint: '/api/estoque/ajuste',
    campoQuantidade: 'quantidadeContada',
    rotuloQuantidade: 'Quantidade contada',
    exemploMotivo: 'Ex: Inventário mensal'
  },
  perda: {
    titulo: 'Perda',
    mensagemSucesso: 'Perda registrada',
    descricao: 'Retira do lote a quantidade avariada, vencida ou extraviada.',
    endpoint: '/api/estoque/perda',
    campoQuantidade: 'quantidade',
    rotuloQuantidade: 'Quantidade perdida',
    exemploMotivo: 'Ex: Caixas amassadas / lote vencido'
  },
  devolucao: {
    titulo: 'Devolução de cliente',
    mensagemSucesso: 'Devolução registrada',
    descricao: 'Devolve ao lote a quantidade trazida de volta pelo cliente.',
    endpoint: '/api/estoque/devolucao',
    campoQuantidade: 'quantidade',
    rotuloQuantidade: 'Quantidade devolvida',
    exemploMotivo: 'Ex: Cliente desistiu da compra'
  }
};

export default function AjustesEstoque() {
  const [produto, setProduto] = useState(null);
  const [lotes, setLotes] = useState([]);
  const [idLote, setIdLote] = useState(null);
  const [operacao, setOperacao] = useState('ajuste');
  const [quantidade, setQuantidade] = useState('');
  const [observacao, setObservacao] = useState('');
  const [salvando, setSalvando] = useState(false);
  const [ultimaMovimentacao, setUltimaMovimentacao] = useState(null);

  const config = OPERACOES[operacao];
  const loteSelecionado = lotes.find((lote) => lote.id === idLote);
  const diferencaAjuste = loteSelecionado && quantidade !== '' ? Number(quantidade) - loteSelecionado.quantidadeAtual : null;

  const carregarLotes = (ean) => {
    api.get(`/api/estoque/${ean}`)
      .then((response) => setLotes(response.data.lotes))
      .catch((error) => {
        console.error('Erro ao carregar lotes:', error);
        toast.error(mensagemDeErro(error, 'Não foi possível carregar os lotes.'));
      });
  };

  const selecionarProduto = (produtoSelecionado) => {
    setProduto(produtoSelecionado);
    setLotes([]);
    setIdLote(null);
    setUltimaMovimentacao(null);
    if (produtoSelecionado) carregarLotes(produtoSelecionado.ean);
  };

  const registrar = async (event) => {
    event.preventDefault();
    const valor = Number(quantidade);

    if (!loteSelecionado) return toast.warning('Selecione o lote.');
    if (quantidade === '' || !Number.isInteger(valor) || valor < 0 || (operacao !== 'ajuste' && valor === 0))
      return toast.warning(operacao === 'ajuste' ? 'Informe a quantidade contada (inteiro, zero ou mais).' : 'Informe uma quantidade inteira maior que zero.');
    if (!observacao.trim()) return toast.warning('Informe o motivo.');

    setSalvando(true);
    try {
      const resposta = await api.post(config.endpoint, {
        idLote: loteSelecionado.id,
        [config.campoQuantidade]: valor,
        observacao: observacao.trim()
      });
      setUltimaMovimentacao(resposta.data);
      toast.success(`${config.mensagemSucesso}! Saldo do lote ${resposta.data.codigoLote}: ${resposta.data.saldoApos}.`);
      setQuantidade('');
      setObservacao('');
      carregarLotes(produto.ean);
    } catch (error) {
      console.error('Erro ao registrar movimentação:', error);
      toast.error(mensagemDeErro(error, 'Erro ao registrar a movimentação.'));
    } finally {
      setSalvando(false);
    }
  };

  return (
    <div>
      <h2>Ajustes e Perdas</h2>
      <p>Corrija o saldo após uma contagem, registre perdas ou devoluções. Toda alteração exige um motivo e fica no histórico.</p>

      <form onSubmit={registrar} style={cartao}>
        <div style={{ marginBottom: '15px' }}>
          <label style={rotulo}>Produto</label>
          <SeletorProduto ean={produto?.ean} onChange={selecionarProduto} />
        </div>

        {produto && lotes.length === 0 && (
          <p style={textoAjuda}>Este produto ainda não tem lotes. Registre uma entrada primeiro.</p>
        )}

        {lotes.length > 0 && (
          <div style={{ marginBottom: '15px' }}>
            <label style={rotulo}>Lote</label>
            <table style={tabela}>
              <thead>
                <tr>
                  <th style={th}></th>
                  <th style={th}>Lote</th>
                  <th style={th}>Validade</th>
                  <th style={th}>Situação</th>
                  <th style={{ ...th, textAlign: 'right' }}>Saldo</th>
                </tr>
              </thead>
              <tbody>
                {lotes.map((lote) => (
                  <tr key={lote.id} onClick={() => setIdLote(lote.id)}
                    style={{ cursor: 'pointer', backgroundColor: lote.id === idLote ? '#e8f6f8' : undefined }}>
                    <td style={td}><input type="radio" checked={lote.id === idLote} onChange={() => setIdLote(lote.id)} /></td>
                    <td style={td}>{lote.codigoLote}</td>
                    <td style={td}>{formatarData(lote.dataValidade)}</td>
                    <td style={td}><EtiquetaSituacao situacao={lote.situacao} /></td>
                    <td style={{ ...td, textAlign: 'right', fontWeight: 'bold' }}>{lote.quantidadeAtual}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        <div style={{ marginBottom: '15px' }}>
          <label style={rotulo}>Operação</label>
          <div style={{ display: 'flex', gap: '20px', flexWrap: 'wrap' }}>
            {Object.entries(OPERACOES).map(([chave, op]) => (
              <label key={chave} style={{ cursor: 'pointer' }}>
                <input type="radio" name="operacao" checked={operacao === chave} onChange={() => setOperacao(chave)} /> {op.titulo}
              </label>
            ))}
          </div>
          <div style={{ ...textoAjuda, marginTop: '5px' }}>{config.descricao}</div>
        </div>

        <div style={{ display: 'flex', gap: '15px', marginBottom: '15px', flexWrap: 'wrap' }}>
          <div style={{ flex: 1, minWidth: '160px' }}>
            <label style={rotulo}>{config.rotuloQuantidade}</label>
            <input type="number" min="0" step="1" value={quantidade} onChange={(event) => setQuantidade(event.target.value)} style={campo} />
            {operacao === 'ajuste' && diferencaAjuste !== null && !Number.isNaN(diferencaAjuste) && (
              <div style={{ ...textoAjuda, marginTop: '5px' }}>
                {diferencaAjuste === 0
                  ? 'Igual ao saldo atual: nada a ajustar.'
                  : `Diferença a registrar: ${diferencaAjuste > 0 ? '+' : ''}${diferencaAjuste}`}
              </div>
            )}
          </div>
          <div style={{ flex: 3, minWidth: '260px' }}>
            <label style={rotulo}>Motivo</label>
            <input value={observacao} onChange={(event) => setObservacao(event.target.value)} placeholder={config.exemploMotivo} style={campo} />
          </div>
        </div>

        <button type="submit" disabled={salvando} style={{ ...botaoPrimario, backgroundColor: operacao === 'perda' ? '#dc3545' : botaoPrimario.backgroundColor }}>
          {salvando ? 'Registrando...' : `Registrar ${config.titulo.toLowerCase()}`}
        </button>
      </form>

      {ultimaMovimentacao && (
        <div style={cartao}>
          <strong>Registrado:</strong> {TIPOS_MOVIMENTACAO[ultimaMovimentacao.tipo]} de{' '}
          <strong>{ultimaMovimentacao.quantidade > 0 ? '+' : ''}{ultimaMovimentacao.quantidade}</strong> no lote{' '}
          {ultimaMovimentacao.codigoLote} — saldo atual: <strong>{ultimaMovimentacao.saldoApos}</strong>
          <div style={textoAjuda}>Motivo: {ultimaMovimentacao.observacao}</div>
        </div>
      )}
    </div>
  );
}
