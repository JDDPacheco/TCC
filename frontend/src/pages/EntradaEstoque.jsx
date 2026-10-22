import { useState } from 'react';
import { toast } from 'react-toastify';
import api from '../services/api';
import SeletorProduto from '../components/SeletorProduto';
import EtiquetaSituacao from '../components/EtiquetaSituacao';
import { formatarData, formatarDataHora, formatarMoeda, mensagemDeErro } from '../utils/formatacao';
import { botaoPrimario, campo, cartao, linhaDeCampos, rotulo, tabela, td, textoAjuda, th } from '../utils/estilos';

const formularioVazio = { codigoLote: '', dataValidade: '', quantidade: '', custoUnitario: '', observacao: '' };

export default function EntradaEstoque() {
  const [produto, setProduto] = useState(null);
  const [saldo, setSaldo] = useState(null); // posição atual do produto selecionado (lotes existentes)
  const [formulario, setFormulario] = useState(formularioVazio);
  const [fornecedor, setFornecedor] = useState(''); // mantido entre entradas: normalmente vários itens chegam do mesmo fornecedor
  const [salvando, setSalvando] = useState(false);
  const [ultimaEntrada, setUltimaEntrada] = useState(null);

  const ehRemedio = produto && produto.tipoProduto !== 'geral';

  const carregarSaldo = (ean) => {
    api.get(`/api/estoque/${ean}`)
      .then((response) => setSaldo(response.data))
      .catch((error) => console.error('Erro ao consultar saldo:', error));
  };

  const selecionarProduto = (produtoSelecionado) => {
    setProduto(produtoSelecionado);
    setFormulario(formularioVazio);
    setSaldo(null);
    if (produtoSelecionado) carregarSaldo(produtoSelecionado.ean);
  };

  const alterarCampo = (event) => {
    const { name, value } = event.target;
    setFormulario({ ...formulario, [name]: value });
  };

  // Preenche o formulário com um lote já cadastrado (nova entrada no mesmo lote)
  const usarLoteExistente = (lote) => {
    setFormulario({ ...formulario, codigoLote: lote.codigoLote, dataValidade: lote.dataValidade });
  };

  const registrarEntrada = async (event) => {
    event.preventDefault();

    const quantidade = Number(formulario.quantidade);
    const custoUnitario = Number(String(formulario.custoUnitario).replace(',', '.'));

    if (!produto) return toast.warning('Selecione o produto.');
    if (ehRemedio && (!formulario.codigoLote.trim() || !formulario.dataValidade))
      return toast.warning('Para remédios, informe o código do lote e a validade.');
    if (!Number.isInteger(quantidade) || quantidade <= 0) return toast.warning('Informe uma quantidade inteira maior que zero.');
    if (formulario.custoUnitario === '' || Number.isNaN(custoUnitario) || custoUnitario < 0)
      return toast.warning('Informe o custo unitário (zero para bonificação).');
    if (!fornecedor.trim()) return toast.warning('Informe o fornecedor.');

    setSalvando(true);
    try {
      const resposta = await api.post('/api/estoque/entrada', {
        ean: produto.ean,
        codigoLote: ehRemedio ? formulario.codigoLote.trim() : null,
        dataValidade: ehRemedio ? formulario.dataValidade : null,
        quantidade,
        custoUnitario,
        fornecedor: fornecedor.trim(),
        observacao: formulario.observacao.trim() || null
      });

      setUltimaEntrada(resposta.data);
      toast.success(`Entrada registrada! Lote ${resposta.data.codigoLote} agora tem ${resposta.data.saldoApos} ${produto.unidadeDeMedida}.`);
      setFormulario(formularioVazio);
      carregarSaldo(produto.ean);
    } catch (error) {
      console.error('Erro ao registrar entrada:', error);
      toast.error(mensagemDeErro(error, 'Erro ao registrar a entrada.'));
    } finally {
      setSalvando(false);
    }
  };

  return (
    <div>
      <h2>Entrada de Mercadoria</h2>
      <p>Registre os produtos recebidos do fornecedor. Cada entrada soma ao saldo do lote e fica no histórico de movimentações.</p>

      <form onSubmit={registrarEntrada} style={cartao}>
        <div style={{ marginBottom: '15px' }}>
          <label style={rotulo}>Produto</label>
          <SeletorProduto ean={produto?.ean} onChange={selecionarProduto} />
          {saldo && (
            <div style={{ ...textoAjuda, marginTop: '6px' }}>
              Saldo disponível: <strong>{saldo.saldoDisponivel} {saldo.unidadeDeMedida}</strong>
              {saldo.saldoVencido > 0 && <> · vencido: <strong style={{ color: '#dc3545' }}>{saldo.saldoVencido}</strong></>}
            </div>
          )}
        </div>

        {produto && !ehRemedio && (
          <p style={{ ...textoAjuda, backgroundColor: '#e9ecef', padding: '10px', borderRadius: '4px' }}>
            Produto geral: o estoque é controlado em um <strong>lote único</strong>, sem código de lote nem validade.
          </p>
        )}

        {ehRemedio && (
          <>
            <div style={linhaDeCampos}>
              <div style={{ flex: 1, minWidth: '180px' }}>
                <label style={rotulo}>Código do lote</label>
                <input name="codigoLote" value={formulario.codigoLote} onChange={alterarCampo} placeholder="Ex: L2309A" style={campo} />
              </div>
              <div style={{ flex: 1, minWidth: '180px' }}>
                <label style={rotulo}>Validade</label>
                <input type="date" name="dataValidade" value={formulario.dataValidade} onChange={alterarCampo} style={campo} />
              </div>
            </div>

            {saldo?.lotes.length > 0 && (
              <div style={{ marginBottom: '15px' }}>
                <span style={textoAjuda}>Lotes já cadastrados (clique para dar entrada no mesmo lote):</span>
                <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap', marginTop: '6px' }}>
                  {saldo.lotes.filter((lote) => lote.situacao !== 'VENCIDO').map((lote) => (
                    <button key={lote.id} type="button" onClick={() => usarLoteExistente(lote)}
                      style={{ padding: '4px 10px', cursor: 'pointer', border: '1px solid #ccc', borderRadius: '4px', backgroundColor: '#f8f9fa' }}>
                      {lote.codigoLote} · val. {formatarData(lote.dataValidade)} · saldo {lote.quantidadeAtual}
                    </button>
                  ))}
                </div>
              </div>
            )}
          </>
        )}

        <div style={linhaDeCampos}>
          <div style={{ flex: 1, minWidth: '140px' }}>
            <label style={rotulo}>Quantidade {produto && `(${produto.unidadeDeMedida})`}</label>
            <input type="number" min="1" step="1" name="quantidade" value={formulario.quantidade} onChange={alterarCampo} placeholder="Ex: 20" style={campo} />
          </div>
          <div style={{ flex: 1, minWidth: '140px' }}>
            <label style={rotulo}>Custo unitário (R$)</label>
            <input inputMode="decimal" name="custoUnitario" value={formulario.custoUnitario} onChange={alterarCampo} placeholder="Ex: 7,35" style={campo} />
          </div>
          <div style={{ flex: 2, minWidth: '220px' }}>
            <label style={rotulo}>Fornecedor</label>
            <input value={fornecedor} onChange={(event) => setFornecedor(event.target.value)} placeholder="Ex: Distribuidora Santa Cruz" style={campo} />
          </div>
        </div>

        <div style={{ marginBottom: '15px' }}>
          <label style={rotulo}>Observação (opcional)</label>
          <input name="observacao" value={formulario.observacao} onChange={alterarCampo} placeholder="Ex: Pedido 1234" style={campo} />
        </div>

        <button type="submit" disabled={salvando} style={botaoPrimario}>
          {salvando ? 'Registrando...' : 'Registrar Entrada'}
        </button>
      </form>

      {ultimaEntrada && (
        <div style={cartao}>
          <h3 style={{ marginTop: 0 }}>Última entrada registrada</h3>
          <table style={tabela}>
            <thead>
              <tr>
                <th style={th}>Produto</th>
                <th style={th}>Lote</th>
                <th style={th}>Validade</th>
                <th style={th}>Quantidade</th>
                <th style={th}>Saldo do lote</th>
                <th style={th}>Custo unit.</th>
                <th style={th}>Fornecedor</th>
                <th style={th}>Data/hora</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td style={td}>{ultimaEntrada.nomeProduto}</td>
                <td style={td}>{ultimaEntrada.codigoLote}</td>
                <td style={td}>
                  {ultimaEntrada.dataValidade ? formatarData(ultimaEntrada.dataValidade) : <EtiquetaSituacao situacao="SEM_VALIDADE" />}
                </td>
                <td style={td}>+{ultimaEntrada.quantidade}</td>
                <td style={td}><strong>{ultimaEntrada.saldoApos}</strong></td>
                <td style={td}>{formatarMoeda(ultimaEntrada.custoUnitario)}</td>
                <td style={td}>{ultimaEntrada.fornecedor}</td>
                <td style={td}>{formatarDataHora(ultimaEntrada.dataHora)}</td>
              </tr>
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
