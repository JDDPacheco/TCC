import { useState } from 'react';
import { toast } from 'react-toastify';
import api from '../services/api';
import SeletorProduto from '../components/SeletorProduto';
import { CONTROLES_RECEITA, FORMAS_PAGAMENTO, formatarMoeda, mensagemDeErro, textoParaNumero } from '../utils/formatacao';
import { botaoPrimario, botaoSecundario, campo, cartao, rotulo, tabela, td, textoAjuda, th } from '../utils/estilos';

const receitaVazia = { nomePaciente: '', nomePrescritor: '', registroPrescritor: '', numeroReceita: '' };

// Valores em centavos (inteiros) evitam erros de arredondamento do JavaScript (0.1 + 0.2 !== 0.3)
const emCentavos = (valor) => Math.round(Number(valor) * 100);

export default function CaixaVenda() {
  const [carrinho, setCarrinho] = useState([]); // [{ produto, quantidade, receitaApresentada, receita }]
  const [desconto, setDesconto] = useState('');
  const [formaPagamento, setFormaPagamento] = useState('DINHEIRO');
  const [valorRecebido, setValorRecebido] = useState('');
  const [finalizando, setFinalizando] = useState(false);
  const [vendaConcluida, setVendaConcluida] = useState(null);

  // ---------------------------------------------------------- carrinho

  const adicionarProduto = async (produtoSelecionado) => {
    if (!produtoSelecionado) return;
    setVendaConcluida(null);
    try {
      const { data: produto } = await api.get(`/api/venda/produto/${produtoSelecionado.ean}`);
      if (produto.precoAtual === null) return toast.error(`${produto.nomeComercial} não tem preço cadastrado.`);

      const existente = carrinho.find((item) => item.produto.ean === produto.ean);
      const quantidadeDesejada = (existente?.quantidade || 0) + 1;
      if (quantidadeDesejada > produto.saldoDisponivel)
        return toast.error(`Estoque insuficiente para ${produto.nomeComercial}: disponível ${produto.saldoDisponivel}.`);

      if (existente) {
        setCarrinho(carrinho.map((item) => (item.produto.ean === produto.ean ? { ...item, produto, quantidade: quantidadeDesejada } : item)));
      } else {
        setCarrinho([...carrinho, { produto, quantidade: 1, receitaApresentada: false, receita: receitaVazia }]);
      }
    } catch (error) {
      console.error('Erro ao consultar produto:', error);
      toast.error(mensagemDeErro(error, 'Não foi possível consultar o produto.'));
    }
  };

  const alterarItem = (ean, alteracoes) => {
    setCarrinho(carrinho.map((item) => (item.produto.ean === ean ? { ...item, ...alteracoes } : item)));
  };

  const alterarQuantidade = (item, texto) => {
    const quantidade = Number(texto);
    if (texto !== '' && (!Number.isInteger(quantidade) || quantidade < 1)) return;
    if (quantidade > item.produto.saldoDisponivel) {
      toast.warning(`Disponível: ${item.produto.saldoDisponivel} ${item.produto.unidadeDeMedida}.`);
      return alterarItem(item.produto.ean, { quantidade: item.produto.saldoDisponivel });
    }
    alterarItem(item.produto.ean, { quantidade: texto === '' ? '' : quantidade });
  };

  const alterarReceita = (item, campoReceita, valor) => {
    alterarItem(item.produto.ean, { receita: { ...item.receita, [campoReceita]: valor } });
  };

  const removerItem = (ean) => setCarrinho(carrinho.filter((item) => item.produto.ean !== ean));

  const limparVenda = () => {
    setCarrinho([]);
    setDesconto('');
    setValorRecebido('');
    setFormaPagamento('DINHEIRO');
  };

  // ---------------------------------------------------------- totais (em centavos)

  const subtotalCentavos = carrinho.reduce((soma, item) => soma + emCentavos(item.produto.precoAtual) * (Number(item.quantidade) || 0), 0);
  const descontoNumero = textoParaNumero(desconto);
  const descontoCentavos = Number.isNaN(descontoNumero) ? 0 : emCentavos(descontoNumero);
  const totalCentavos = subtotalCentavos - descontoCentavos;
  const recebidoNumero = textoParaNumero(valorRecebido);
  const trocoCentavos = Number.isNaN(recebidoNumero) ? null : emCentavos(recebidoNumero) - totalCentavos;

  // ---------------------------------------------------------- finalização

  const validarAntesDeEnviar = () => {
    if (carrinho.length === 0) return 'Adicione pelo menos um produto.';
    for (const item of carrinho) {
      const nome = item.produto.nomeComercial;
      if (!item.quantidade) return `Informe a quantidade de ${nome}.`;
      if (item.produto.exigeReceita && !item.receitaApresentada) return `${nome} exige receita: confirme que ela foi apresentada.`;
      if (item.produto.exigeDadosReceita && Object.values(item.receita).some((valor) => !valor.trim()))
        return `${nome} é de controle especial: preencha todos os dados da receita.`;
    }
    if (desconto !== '' && (Number.isNaN(descontoNumero) || descontoNumero < 0)) return 'Desconto inválido.';
    if (descontoCentavos > subtotalCentavos) return 'O desconto não pode ser maior que o valor da venda.';
    if (formaPagamento === 'DINHEIRO') {
      if (Number.isNaN(recebidoNumero)) return 'Informe o valor recebido em dinheiro.';
      if (trocoCentavos < 0) return 'O valor recebido é menor que o total da venda.';
    }
    return null;
  };

  const finalizarVenda = async () => {
    const problema = validarAntesDeEnviar();
    if (problema) return toast.warning(problema);

    setFinalizando(true);
    try {
      const { data: venda } = await api.post('/api/venda', {
        itens: carrinho.map((item) => ({
          ean: item.produto.ean,
          quantidade: Number(item.quantidade),
          receitaApresentada: item.produto.exigeReceita ? item.receitaApresentada : null,
          receita: item.produto.exigeDadosReceita ? item.receita : null
        })),
        formaPagamento,
        desconto: descontoCentavos > 0 ? descontoCentavos / 100 : null,
        valorRecebido: formaPagamento === 'DINHEIRO' ? recebidoNumero : null
      });
      setVendaConcluida(venda);
      toast.success(`Venda nº ${venda.id} concluída!`);
      limparVenda();
      window.scrollTo({ top: 0, behavior: 'smooth' }); // mostra o quadro da venda concluída (com o troco)
    } catch (error) {
      console.error('Erro ao finalizar venda:', error);
      toast.error(mensagemDeErro(error, 'Erro ao finalizar a venda.'));
    } finally {
      setFinalizando(false);
    }
  };

  // ---------------------------------------------------------- tela

  return (
    <div>
      <h2>Caixa (PDV)</h2>
      <p>Busque o produto pelo nome, digite o EAN ou use o leitor de código de barras. Ler o mesmo produto de novo aumenta a quantidade.</p>

      {vendaConcluida && (
        <div style={{ ...cartao, border: '2px solid #28a745', backgroundColor: '#f3fbf5' }}>
          <h3 style={{ marginTop: 0, color: '#155724' }}>✔ Venda nº {vendaConcluida.id} concluída</h3>
          <div>Total: <strong>{formatarMoeda(vendaConcluida.valorTotal)}</strong> · {FORMAS_PAGAMENTO[vendaConcluida.formaPagamento]}</div>
          {vendaConcluida.troco !== null && (
            <div style={{ fontSize: '1.4em', marginTop: '8px' }}>Troco: <strong>{formatarMoeda(vendaConcluida.troco)}</strong></div>
          )}
          <div style={{ ...textoAjuda, marginTop: '8px' }}>
            {vendaConcluida.itens.map((item) => `${item.quantidade}× ${item.nomeProduto} (lote ${item.codigoLote})`).join(' · ')}
          </div>
        </div>
      )}

      <div style={cartao}>
        <label style={rotulo}>Adicionar produto</label>
        <SeletorProduto ean={null} onChange={adicionarProduto} isClearable={false} placeholder="Nome, EAN ou leitor de código de barras..." />
      </div>

      <div style={cartao}>
        <h3 style={{ marginTop: 0 }}>Itens da venda</h3>
        {carrinho.length === 0 ? (
          <p style={{ color: '#777', fontStyle: 'italic', margin: 0 }}>Nenhum produto adicionado.</p>
        ) : (
          <table style={tabela}>
            <thead>
              <tr>
                <th style={th}>Produto</th>
                <th style={{ ...th, textAlign: 'right' }}>Preço</th>
                <th style={{ ...th, textAlign: 'center' }}>Quantidade</th>
                <th style={{ ...th, textAlign: 'right' }}>Subtotal</th>
                <th style={th}></th>
              </tr>
            </thead>
            <tbody>
              {carrinho.map((item) => (
                <tr key={item.produto.ean}>
                  <td style={td}>
                    <strong>{item.produto.nomeComercial}</strong>
                    <div style={textoAjuda}>EAN {item.produto.ean} · disponível {item.produto.saldoDisponivel} {item.produto.unidadeDeMedida}</div>

                    {item.produto.exigeReceita && (
                      <div style={{ marginTop: '8px', padding: '8px', backgroundColor: '#fff3cd', borderRadius: '4px' }}>
                        <strong style={{ color: '#856404' }}>⚠ {CONTROLES_RECEITA[item.produto.tipoControle]}</strong>
                        <label style={{ display: 'block', marginTop: '5px', cursor: 'pointer' }}>
                          <input type="checkbox" checked={item.receitaApresentada}
                            onChange={(event) => alterarItem(item.produto.ean, { receitaApresentada: event.target.checked })} />{' '}
                          Receita apresentada
                        </label>
                        {item.produto.exigeDadosReceita && (
                          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '6px', marginTop: '6px' }}>
                            <input placeholder="Nome do paciente" value={item.receita.nomePaciente}
                              onChange={(event) => alterarReceita(item, 'nomePaciente', event.target.value)} style={campo} />
                            <input placeholder="Nome do prescritor" value={item.receita.nomePrescritor}
                              onChange={(event) => alterarReceita(item, 'nomePrescritor', event.target.value)} style={campo} />
                            <input placeholder="Registro (ex: CRM-AM 12345)" value={item.receita.registroPrescritor}
                              onChange={(event) => alterarReceita(item, 'registroPrescritor', event.target.value)} style={campo} />
                            <input placeholder="Número da receita" value={item.receita.numeroReceita}
                              onChange={(event) => alterarReceita(item, 'numeroReceita', event.target.value)} style={campo} />
                          </div>
                        )}
                      </div>
                    )}
                  </td>
                  <td style={{ ...td, textAlign: 'right' }}>{formatarMoeda(item.produto.precoAtual)}</td>
                  <td style={{ ...td, textAlign: 'center' }}>
                    <input type="number" min="1" max={item.produto.saldoDisponivel} step="1" value={item.quantidade}
                      onChange={(event) => alterarQuantidade(item, event.target.value)} style={{ ...campo, width: '80px', textAlign: 'center' }} />
                  </td>
                  <td style={{ ...td, textAlign: 'right', fontWeight: 'bold' }}>
                    {formatarMoeda((emCentavos(item.produto.precoAtual) * (Number(item.quantidade) || 0)) / 100)}
                  </td>
                  <td style={td}>
                    <button type="button" onClick={() => removerItem(item.produto.ean)} title="Remover item"
                      style={{ ...botaoSecundario, color: '#dc3545' }}>✕</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      <div style={cartao}>
        <h3 style={{ marginTop: 0 }}>Pagamento</h3>

        <div style={{ display: 'flex', gap: '20px', flexWrap: 'wrap', marginBottom: '15px' }}>
          {Object.entries(FORMAS_PAGAMENTO).map(([chave, texto]) => (
            <label key={chave} style={{ cursor: 'pointer' }}>
              <input type="radio" name="formaPagamento" checked={formaPagamento === chave} onChange={() => setFormaPagamento(chave)} /> {texto}
            </label>
          ))}
        </div>

        <div style={{ display: 'flex', gap: '30px', flexWrap: 'wrap', alignItems: 'flex-end' }}>
          <div>
            <label style={rotulo}>Desconto (R$)</label>
            <input inputMode="decimal" value={desconto} onChange={(event) => setDesconto(event.target.value)} placeholder="0,00"
              style={{ ...campo, width: '120px' }} />
          </div>
          {formaPagamento === 'DINHEIRO' && (
            <div>
              <label style={rotulo}>Valor recebido (R$)</label>
              <input inputMode="decimal" value={valorRecebido} onChange={(event) => setValorRecebido(event.target.value)} placeholder="Ex: 50,00"
                style={{ ...campo, width: '140px' }} />
            </div>
          )}
          <div style={{ marginLeft: 'auto', textAlign: 'right' }}>
            <div style={textoAjuda}>Subtotal: {formatarMoeda(subtotalCentavos / 100)}</div>
            {descontoCentavos > 0 && <div style={textoAjuda}>Desconto: − {formatarMoeda(descontoCentavos / 100)}</div>}
            <div style={{ fontSize: '1.6em', fontWeight: 'bold' }}>Total: {formatarMoeda(totalCentavos / 100)}</div>
            {formaPagamento === 'DINHEIRO' && trocoCentavos !== null && (
              <div style={{ fontSize: '1.1em', color: trocoCentavos < 0 ? '#dc3545' : '#155724' }}>
                {trocoCentavos < 0 ? `Faltam ${formatarMoeda(-trocoCentavos / 100)}` : `Troco: ${formatarMoeda(trocoCentavos / 100)}`}
              </div>
            )}
          </div>
        </div>

        <div style={{ display: 'flex', gap: '10px', marginTop: '20px' }}>
          <button type="button" onClick={finalizarVenda} disabled={finalizando || carrinho.length === 0}
            style={{ ...botaoPrimario, flex: 1, padding: '14px', fontSize: '1.1em', opacity: carrinho.length === 0 ? 0.6 : 1 }}>
            {finalizando ? 'Finalizando...' : 'Finalizar venda'}
          </button>
          <button type="button" onClick={() => { if (carrinho.length === 0 || window.confirm('Descartar os itens desta venda?')) limparVenda(); }}
            style={botaoSecundario}>
            Limpar
          </button>
        </div>
      </div>
    </div>
  );
}
