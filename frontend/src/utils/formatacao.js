// Funções de formatação e textos compartilhados pelas telas de estoque.

export const formatarMoeda = (valor) => {
  if (valor === null || valor === undefined) return '—';
  return Number(valor).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
};

// Datas sem horário (LocalDate, "aaaa-mm-dd") são formatadas pelo texto:
// new Date("2027-03-31") seria interpretado como UTC e, no fuso de Manaus, mostraria o dia anterior.
export const formatarData = (data) => {
  if (!data) return '—';
  const [ano, mes, dia] = data.split('-');
  return `${dia}/${mes}/${ano}`;
};

export const formatarDataHora = (dataHora) => {
  if (!dataHora) return '—';
  return new Date(dataHora).toLocaleString('pt-BR');
};

// Mensagem enviada pelo backend (campo "message") ou um texto padrão
export const mensagemDeErro = (error, textoPadrao) => error.response?.data?.message || textoPadrao;

export const TIPOS_MOVIMENTACAO = {
  ENTRADA: 'Entrada',
  SAIDA_VENDA: 'Saída (venda)',
  AJUSTE_INVENTARIO: 'Ajuste de inventário',
  PERDA: 'Perda',
  DEVOLUCAO: 'Devolução'
};

export const SITUACOES_LOTE = {
  VENCIDO: { texto: 'Vencido', cor: '#dc3545', fundo: '#f8d7da' },
  A_VENCER: { texto: 'A vencer', cor: '#856404', fundo: '#fff3cd' },
  OK: { texto: 'Dentro da validade', cor: '#155724', fundo: '#d4edda' },
  SEM_VALIDADE: { texto: 'Sem validade (lote único)', cor: '#555', fundo: '#e9ecef' }
};
