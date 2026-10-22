import { useEffect, useState } from 'react';
import { toast } from 'react-toastify';
import api from '../services/api';
import EtiquetaSituacao from '../components/EtiquetaSituacao';
import { formatarData, mensagemDeErro } from '../utils/formatacao';
import { botaoSecundario, campo, cartao, tabela, td, textoAjuda, th } from '../utils/estilos';

const TIPOS_PRODUTO = { geral: 'Geral', remedio: 'Remédio', generico: 'Genérico' };

export default function ConsultaEstoque() {
  const [saldos, setSaldos] = useState([]);
  const [filtro, setFiltro] = useState('');
  const [eanSelecionado, setEanSelecionado] = useState(null);
  const [estoqueMinimo, setEstoqueMinimo] = useState('');
  const [salvandoMinimo, setSalvandoMinimo] = useState(false);

  const carregarSaldos = () => {
    api.get('/api/estoque')
      .then((response) => setSaldos(response.data || [])) // 204 (sem produtos) vem sem corpo
      .catch((error) => {
        console.error('Erro ao consultar estoque:', error);
        toast.error('Não foi possível carregar o estoque.');
      });
  };

  useEffect(() => {
    carregarSaldos();
  }, []);

  const selecionado = saldos.find((saldo) => saldo.ean === eanSelecionado);

  const selecionarProduto = (saldo) => {
    setEanSelecionado(saldo.ean);
    setEstoqueMinimo(saldo.estoqueMinimo ?? '');
  };

  const salvarEstoqueMinimo = async (event) => {
    event.preventDefault();
    const valor = estoqueMinimo === '' ? null : Number(estoqueMinimo);
    if (valor !== null && (!Number.isInteger(valor) || valor < 0))
      return toast.warning('Informe um número inteiro maior ou igual a zero, ou deixe em branco para remover.');

    setSalvandoMinimo(true);
    try {
      await api.put(`/api/estoque/${eanSelecionado}/estoque_minimo`, { estoqueMinimo: valor });
      toast.success(valor === null ? 'Estoque mínimo removido.' : 'Estoque mínimo atualizado!');
      carregarSaldos();
    } catch (error) {
      console.error('Erro ao salvar estoque mínimo:', error);
      toast.error(mensagemDeErro(error, 'Erro ao salvar o estoque mínimo.'));
    } finally {
      setSalvandoMinimo(false);
    }
  };

  const termo = filtro.trim().toLowerCase();
  const saldosFiltrados = saldos.filter((saldo) =>
    saldo.nomeComercial.toLowerCase().includes(termo) || saldo.ean.includes(termo)
  );

  return (
    <div>
      <h2>Consulta de Estoque</h2>
      <p>Saldo de cada produto. Clique em um produto para ver os lotes e configurar o estoque mínimo.</p>

      {selecionado && (
        <div style={{ ...cartao, border: '2px solid #17a2b8' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', gap: '10px' }}>
            <div>
              <h3 style={{ margin: 0 }}>{selecionado.nomeComercial}</h3>
              <span style={textoAjuda}>EAN {selecionado.ean} · {TIPOS_PRODUTO[selecionado.tipoProduto]} · contado em {selecionado.unidadeDeMedida}</span>
            </div>
            <button type="button" onClick={() => setEanSelecionado(null)} style={botaoSecundario}>Fechar</button>
          </div>

          <div style={{ display: 'flex', gap: '30px', margin: '15px 0' }}>
            <div>Disponível: <strong style={{ fontSize: '1.3em' }}>{selecionado.saldoDisponivel}</strong></div>
            <div>Vencido: <strong style={{ fontSize: '1.3em', color: selecionado.saldoVencido > 0 ? '#dc3545' : 'inherit' }}>{selecionado.saldoVencido}</strong></div>
          </div>

          {selecionado.lotes.length === 0 ? (
            <p style={textoAjuda}>Este produto ainda não teve entradas no estoque.</p>
          ) : (
            <table style={tabela}>
              <thead>
                <tr>
                  <th style={th}>Lote</th>
                  <th style={th}>Validade</th>
                  <th style={th}>Situação</th>
                  <th style={{ ...th, textAlign: 'right' }}>Saldo</th>
                </tr>
              </thead>
              <tbody>
                {selecionado.lotes.map((lote) => (
                  <tr key={lote.id}>
                    <td style={td}>{lote.codigoLote}</td>
                    <td style={td}>
                      {formatarData(lote.dataValidade)}
                      {lote.diasParaVencer !== null && lote.diasParaVencer >= 0 && (
                        <div style={textoAjuda}>{lote.diasParaVencer === 0 ? 'vence hoje' : `em ${lote.diasParaVencer} dia(s)`}</div>
                      )}
                    </td>
                    <td style={td}><EtiquetaSituacao situacao={lote.situacao} /></td>
                    <td style={{ ...td, textAlign: 'right', fontWeight: 'bold' }}>{lote.quantidadeAtual}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}

          <form onSubmit={salvarEstoqueMinimo} style={{ display: 'flex', gap: '10px', alignItems: 'flex-end', marginTop: '20px' }}>
            <div>
              <label style={{ fontWeight: 'bold', display: 'block', marginBottom: '5px' }}>Estoque mínimo ({selecionado.unidadeDeMedida})</label>
              <input type="number" min="0" step="1" value={estoqueMinimo} onChange={(event) => setEstoqueMinimo(event.target.value)}
                placeholder="Sem mínimo" style={{ ...campo, width: '160px' }} />
            </div>
            <button type="submit" disabled={salvandoMinimo} style={botaoSecundario}>
              {salvandoMinimo ? 'Salvando...' : 'Salvar mínimo'}
            </button>
            <span style={textoAjuda}>Com saldo igual ou abaixo do mínimo, o produto aparece nos alertas. Deixe em branco para remover.</span>
          </form>
        </div>
      )}

      <div style={{ maxWidth: '900px', marginBottom: '15px' }}>
        <input value={filtro} onChange={(event) => setFiltro(event.target.value)} placeholder="🔍 Filtrar por nome ou EAN..."
          style={{ ...campo, padding: '10px' }} />
      </div>

      {saldosFiltrados.length === 0 ? (
        <p style={{ color: '#777', fontStyle: 'italic' }}>Nenhum produto encontrado.</p>
      ) : (
        <table style={{ ...tabela, maxWidth: '900px' }}>
          <thead>
            <tr>
              <th style={th}>Produto</th>
              <th style={th}>EAN</th>
              <th style={th}>Tipo</th>
              <th style={{ ...th, textAlign: 'right' }}>Disponível</th>
              <th style={{ ...th, textAlign: 'right' }}>Vencido</th>
              <th style={{ ...th, textAlign: 'right' }}>Mínimo</th>
            </tr>
          </thead>
          <tbody>
            {saldosFiltrados.map((saldo) => (
              <tr key={saldo.ean} onClick={() => selecionarProduto(saldo)}
                style={{ cursor: 'pointer', backgroundColor: saldo.ean === eanSelecionado ? '#e8f6f8' : undefined }}>
                <td style={td}>{saldo.nomeComercial}</td>
                <td style={td}>{saldo.ean}</td>
                <td style={td}>{TIPOS_PRODUTO[saldo.tipoProduto]}</td>
                <td style={{ ...td, textAlign: 'right', fontWeight: 'bold', color: saldo.abaixoDoMinimo ? '#dc3545' : 'inherit' }}>
                  {saldo.saldoDisponivel} {saldo.unidadeDeMedida}
                  {saldo.abaixoDoMinimo && <div style={{ fontSize: '0.8em' }}>abaixo do mínimo</div>}
                </td>
                <td style={{ ...td, textAlign: 'right', color: saldo.saldoVencido > 0 ? '#dc3545' : '#999' }}>{saldo.saldoVencido}</td>
                <td style={{ ...td, textAlign: 'right', color: '#666' }}>{saldo.estoqueMinimo ?? '—'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
