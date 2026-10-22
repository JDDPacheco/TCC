import { useEffect, useState } from 'react';
import { toast } from 'react-toastify';
import api from '../services/api';
import { dataDeHoje, FORMAS_PAGAMENTO, formatarData, formatarMoeda, mensagemDeErro } from '../utils/formatacao';
import { campo, cartao, rotulo, tabela, td, textoAjuda, th } from '../utils/estilos';

const buscarResumo = (data) =>
  api.get('/api/venda/resumo', { params: { data } })
    .then((response) => response.data)
    .catch((error) => {
      console.error('Erro ao carregar resumo:', error);
      toast.error(mensagemDeErro(error, 'Não foi possível carregar o resumo.'));
      return null;
    });

const Indicador = ({ titulo, valor, destaque }) => (
  <div style={{ flex: 1, minWidth: '150px', padding: '15px', borderRadius: '6px', backgroundColor: destaque ? '#e8f6f8' : '#f8f9fa' }}>
    <div style={textoAjuda}>{titulo}</div>
    <div style={{ fontSize: '1.5em', fontWeight: 'bold', marginTop: '5px' }}>{valor}</div>
  </div>
);

export default function ResumoVendas() {
  const [data, setData] = useState(dataDeHoje());
  const [resumo, setResumo] = useState(null);

  useEffect(() => {
    buscarResumo(data).then(setResumo);
  }, [data]);

  return (
    <div>
      <h2>Resumo do Dia</h2>
      <p>Fechamento de caixa: valores das vendas concluídas no dia (as canceladas não entram nos totais).</p>

      <div style={cartao}>
        <label style={rotulo}>Dia</label>
        <input type="date" value={data} onChange={(event) => event.target.value && setData(event.target.value)} style={{ ...campo, width: '180px' }} />
      </div>

      {resumo && (
        <>
          <div style={{ ...cartao, display: 'flex', gap: '15px', flexWrap: 'wrap' }}>
            <Indicador titulo={`Total recebido em ${formatarData(resumo.data)}`} valor={formatarMoeda(resumo.valorTotal)} destaque />
            <Indicador titulo="Vendas concluídas" valor={resumo.quantidadeVendas} />
            <Indicador titulo="Ticket médio" valor={formatarMoeda(resumo.ticketMedio)} />
            <Indicador titulo="Descontos concedidos" valor={formatarMoeda(resumo.descontos)} />
            <Indicador titulo="Vendas canceladas" valor={resumo.quantidadeCanceladas} />
          </div>

          <div style={cartao}>
            <h3 style={{ marginTop: 0 }}>Por forma de pagamento</h3>
            <table style={tabela}>
              <thead>
                <tr>
                  <th style={th}>Forma de pagamento</th>
                  <th style={{ ...th, textAlign: 'right' }}>Vendas</th>
                  <th style={{ ...th, textAlign: 'right' }}>Total</th>
                </tr>
              </thead>
              <tbody>
                {resumo.porFormaPagamento.map((linha) => (
                  <tr key={linha.formaPagamento} style={{ color: linha.quantidadeVendas === 0 ? '#999' : 'inherit' }}>
                    <td style={td}>{FORMAS_PAGAMENTO[linha.formaPagamento]}</td>
                    <td style={{ ...td, textAlign: 'right' }}>{linha.quantidadeVendas}</td>
                    <td style={{ ...td, textAlign: 'right', fontWeight: 'bold' }}>{formatarMoeda(linha.valorTotal)}</td>
                  </tr>
                ))}
                <tr>
                  <td style={{ ...td, fontWeight: 'bold' }}>Total</td>
                  <td style={{ ...td, textAlign: 'right', fontWeight: 'bold' }}>{resumo.quantidadeVendas}</td>
                  <td style={{ ...td, textAlign: 'right', fontWeight: 'bold' }}>{formatarMoeda(resumo.valorTotal)}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </>
      )}
    </div>
  );
}
