import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { toast } from 'react-toastify';
import api from '../services/api';
import EtiquetaSituacao from '../components/EtiquetaSituacao';
import { formatarData } from '../utils/formatacao';
import { botaoSecundario, campo, cartao, tabela, td, textoAjuda, th } from '../utils/estilos';

// Lotes com saldo que vencem nos próximos "dias" dias (lista vazia em caso de 204 ou erro)
const buscarLotesAVencer = (dias) =>
  api.get('/api/estoque/alertas/vencimento', { params: { dias } })
    .then((response) => response.data || [])
    .catch((error) => {
      console.error('Erro ao carregar lotes a vencer:', error);
      toast.error('Não foi possível carregar os lotes a vencer.');
      return [];
    });

export default function AlertasEstoque() {
  const [vencidos, setVencidos] = useState([]);
  const [aVencer, setAVencer] = useState([]);
  const [estoqueBaixo, setEstoqueBaixo] = useState([]);
  const [dias, setDias] = useState(30);

  const carregarAVencer = () => {
    buscarLotesAVencer(dias).then(setAVencer);
  };

  useEffect(() => {
    api.get('/api/estoque/alertas/vencidos')
      .then((response) => setVencidos(response.data || [])) // 204 (nenhum alerta) vem sem corpo
      .catch((error) => console.error('Erro ao carregar lotes vencidos:', error));
    api.get('/api/estoque/alertas/estoque_baixo')
      .then((response) => setEstoqueBaixo(response.data || []))
      .catch((error) => console.error('Erro ao carregar estoque baixo:', error));
    buscarLotesAVencer(30).then(setAVencer);
  }, []);

  const tabelaDeLotes = (lotes) => (
    <table style={tabela}>
      <thead>
        <tr>
          <th style={th}>Produto</th>
          <th style={th}>Lote</th>
          <th style={th}>Validade</th>
          <th style={th}>Situação</th>
          <th style={{ ...th, textAlign: 'right' }}>Saldo</th>
        </tr>
      </thead>
      <tbody>
        {lotes.map((lote) => (
          <tr key={lote.id}>
            <td style={td}>{lote.nomeProduto}<div style={textoAjuda}>EAN {lote.ean}</div></td>
            <td style={td}>{lote.codigoLote}</td>
            <td style={td}>
              {formatarData(lote.dataValidade)}
              <div style={textoAjuda}>
                {lote.diasParaVencer < 0 ? `venceu há ${-lote.diasParaVencer} dia(s)`
                  : lote.diasParaVencer === 0 ? 'vence hoje' : `em ${lote.diasParaVencer} dia(s)`}
              </div>
            </td>
            <td style={td}><EtiquetaSituacao situacao={lote.situacao} /></td>
            <td style={{ ...td, textAlign: 'right', fontWeight: 'bold' }}>{lote.quantidadeAtual}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );

  const nenhum = (texto) => <p style={{ color: '#28a745', margin: 0 }}>✔ {texto}</p>;

  return (
    <div>
      <h2>Alertas de Estoque</h2>
      <p>Lotes vencidos ou perto do vencimento e produtos abaixo do estoque mínimo.</p>

      <div style={{ ...cartao, borderLeft: '5px solid #dc3545' }}>
        <h3 style={{ marginTop: 0 }}>Lotes vencidos com saldo ({vencidos.length})</h3>
        <p style={textoAjuda}>
          Não podem ser vendidos. Retire-os do estoque registrando uma perda em <Link to="/estoque/ajustes">Ajustes e Perdas</Link>.
        </p>
        {vencidos.length === 0 ? nenhum('Nenhum lote vencido em estoque.') : tabelaDeLotes(vencidos)}
      </div>

      <div style={{ ...cartao, borderLeft: '5px solid #ffc107' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '10px' }}>
          <h3 style={{ margin: 0 }}>Lotes a vencer ({aVencer.length})</h3>
          <form onSubmit={(event) => { event.preventDefault(); carregarAVencer(); }} style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
            <span>nos próximos</span>
            <input type="number" min="0" step="1" value={dias} onChange={(event) => setDias(event.target.value)} style={{ ...campo, width: '80px' }} />
            <span>dias</span>
            <button type="submit" style={botaoSecundario}>Atualizar</button>
          </form>
        </div>
        <div style={{ marginTop: '15px' }}>
          {aVencer.length === 0 ? nenhum('Nenhum lote vence no período.') : tabelaDeLotes(aVencer)}
        </div>
      </div>

      <div style={{ ...cartao, borderLeft: '5px solid #17a2b8' }}>
        <h3 style={{ marginTop: 0 }}>Estoque baixo ({estoqueBaixo.length})</h3>
        <p style={textoAjuda}>
          Produtos com saldo disponível igual ou abaixo do mínimo. O mínimo é configurado em <Link to="/estoque/consulta">Consulta de Estoque</Link>.
        </p>
        {estoqueBaixo.length === 0 ? nenhum('Nenhum produto abaixo do mínimo.') : (
          <table style={tabela}>
            <thead>
              <tr>
                <th style={th}>Produto</th>
                <th style={th}>EAN</th>
                <th style={{ ...th, textAlign: 'right' }}>Disponível</th>
                <th style={{ ...th, textAlign: 'right' }}>Mínimo</th>
              </tr>
            </thead>
            <tbody>
              {estoqueBaixo.map((saldo) => (
                <tr key={saldo.ean}>
                  <td style={td}>{saldo.nomeComercial}</td>
                  <td style={td}>{saldo.ean}</td>
                  <td style={{ ...td, textAlign: 'right', fontWeight: 'bold', color: '#dc3545' }}>{saldo.saldoDisponivel} {saldo.unidadeDeMedida}</td>
                  <td style={{ ...td, textAlign: 'right' }}>{saldo.estoqueMinimo}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}
