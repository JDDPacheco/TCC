import { useState } from 'react';
import { toast } from 'react-toastify';
import api from '../services/api';

export default function Precos() {
  const [ean, setEan] = useState('');
  const [valorVenda, setValorVenda] = useState('');

  const [precoAtual, setPrecoAtual] = useState(null);
  const [historico, setHistorico] = useState([]);

  const [consultando, setConsultando] = useState(false);
  const [salvando, setSalvando] = useState(false);

  const limparResultado = () => {
    setPrecoAtual(null);
    setHistorico([]);
  };

  const carregarDadosPreco = async (eanProduto = ean) => {
    const codigo = eanProduto.trim();

    if (!codigo) {
      toast.warning('Informe o EAN do produto.');
      return;
    }

    setConsultando(true);
    limparResultado();

    try {
      // Consulta o histórico primeiro.
      // O endpoint também valida se o produto existe.
      const respostaHistorico = await api.get(
        `/api/preco/${codigo}/historico`
      );

      setHistorico(respostaHistorico.data);

      if (respostaHistorico.data.length === 0) {
        toast.info('Produto encontrado, mas ainda não possui preço cadastrado.');
        return;
      }

      const respostaAtual = await api.get(
        `/api/preco/${codigo}/atual`
      );

      setPrecoAtual(respostaAtual.data);
    } catch (error) {
      console.error('Erro ao consultar preço:', error);

      if (error.response?.status === 404) {
        toast.error('Produto não encontrado.');
      } else {
        toast.error('Não foi possível consultar o preço.');
      }
    } finally {
      setConsultando(false);
    }
  };

  const salvarNovoPreco = async (event) => {
    event.preventDefault();

    const codigo = ean.trim();
    const valorConvertido = Number(
      String(valorVenda).replace(',', '.')
    );

    if (!codigo) {
      toast.warning('Informe o EAN do produto.');
      return;
    }

    if (!valorConvertido || valorConvertido <= 0) {
      toast.warning('Informe um preço maior que zero.');
      return;
    }

    setSalvando(true);

    try {
      const resposta = await api.post('/api/preco', {
        ean: codigo,
        valorVenda: valorConvertido
      });

      toast.success('Preço cadastrado com sucesso!');

      setPrecoAtual(resposta.data);
      setValorVenda('');

      // Recarrega o histórico, incluindo o preço recém-criado.
      const respostaHistorico = await api.get(
        `/api/preco/${codigo}/historico`
      );

      setHistorico(respostaHistorico.data);
    } catch (error) {
      console.error('Erro ao cadastrar preço:', error);

      if (error.response?.status === 404) {
        toast.error('Não existe produto cadastrado com este EAN.');
      } else if (error.response?.status === 400) {
        toast.error('Preço inválido.');
      } else {
        toast.error('Erro ao cadastrar preço.');
      }
    } finally {
      setSalvando(false);
    }
  };

  const formatarMoeda = (valor) => {
    return Number(valor).toLocaleString('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    });
  };

  const formatarData = (data) => {
    return new Date(data).toLocaleString('pt-BR');
  };

  return (
    <div className="content-area">
      <h2>Gestão de Preços</h2>

      <p>
        Consulte o preço atual de um produto e registre novas alterações
        de preço.
      </p>

      {/* CONSULTA DO PRODUTO */}
      <div
        style={{
          maxWidth: '700px',
          backgroundColor: '#fff',
          padding: '20px',
          borderRadius: '6px',
          border: '1px solid #ddd'
        }}
      >
        <h3>Produto</h3>

        <div style={{ display: 'flex', gap: '10px' }}>
          <input
            type="text"
            placeholder="EAN / Código de barras"
            value={ean}
            onChange={(event) => {
              setEan(event.target.value);
              limparResultado();
            }}
            style={{
              flex: 1,
              padding: '10px'
            }}
          />

          <button
            type="button"
            onClick={() => carregarDadosPreco()}
            disabled={consultando}
            style={{
              padding: '10px 20px',
              cursor: 'pointer'
            }}
          >
            {consultando ? 'Consultando...' : 'Consultar'}
          </button>
        </div>

        {/* PREÇO ATUAL */}
        {precoAtual && (
          <div
            style={{
              marginTop: '20px',
              padding: '15px',
              backgroundColor: '#f8f9fa',
              borderRadius: '5px'
            }}
          >
            <strong>{precoAtual.nomeProduto}</strong>

            <div style={{ marginTop: '8px' }}>
              EAN: {precoAtual.ean}
            </div>

            <div
              style={{
                marginTop: '10px',
                fontSize: '1.5em',
                fontWeight: 'bold'
              }}
            >
              {formatarMoeda(precoAtual.valorVenda)}
            </div>

            <small>
              Vigente desde:{' '}
              {formatarData(precoAtual.dataInicioVigencia)}
            </small>
          </div>
        )}
      </div>

      {/* NOVO PREÇO */}
      <form
        onSubmit={salvarNovoPreco}
        style={{
          maxWidth: '700px',
          marginTop: '20px',
          backgroundColor: '#fff',
          padding: '20px',
          borderRadius: '6px',
          border: '1px solid #ddd'
        }}
      >
        <h3>Registrar Novo Preço</h3>

        <div style={{ display: 'flex', gap: '10px' }}>
          <input
            type="text"
            inputMode="decimal"
            placeholder="Novo preço. Ex: 15,90"
            value={valorVenda}
            onChange={(event) => setValorVenda(event.target.value)}
            style={{
              flex: 1,
              padding: '10px'
            }}
          />

          <button
            type="submit"
            disabled={salvando}
            style={{
              padding: '10px 20px',
              cursor: 'pointer'
            }}
          >
            {salvando ? 'Salvando...' : 'Salvar Preço'}
          </button>
        </div>

        <small style={{ color: '#666' }}>
          Cada alteração gera um novo registro e preserva o histórico
          anterior.
        </small>
      </form>

      {/* HISTÓRICO */}
      {historico.length > 0 && (
        <div
          style={{
            maxWidth: '700px',
            marginTop: '20px'
          }}
        >
          <h3>Histórico de Preços</h3>

          <table
            style={{
              width: '100%',
              borderCollapse: 'collapse',
              backgroundColor: '#fff'
            }}
          >
            <thead>
              <tr>
                <th
                  style={{
                    textAlign: 'left',
                    padding: '10px',
                    borderBottom: '2px solid #ddd'
                  }}
                >
                  Vigência
                </th>

                <th
                  style={{
                    textAlign: 'right',
                    padding: '10px',
                    borderBottom: '2px solid #ddd'
                  }}
                >
                  Preço
                </th>
              </tr>
            </thead>

            <tbody>
              {historico.map((preco) => (
                <tr key={preco.id}>
                  <td
                    style={{
                      padding: '10px',
                      borderBottom: '1px solid #eee'
                    }}
                  >
                    {formatarData(preco.dataInicioVigencia)}
                  </td>

                  <td
                    style={{
                      padding: '10px',
                      textAlign: 'right',
                      borderBottom: '1px solid #eee',
                      fontWeight: 'bold'
                    }}
                  >
                    {formatarMoeda(preco.valorVenda)}
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