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

// Data de hoje no formato aaaa-mm-dd, no fuso local
// (toISOString() usaria UTC e, à noite em Manaus, já devolveria o dia seguinte)
export const dataDeHoje = () => {
  const agora = new Date();
  const doisDigitos = (numero) => String(numero).padStart(2, '0');
  return `${agora.getFullYear()}-${doisDigitos(agora.getMonth() + 1)}-${doisDigitos(agora.getDate())}`;
};

// Converte texto digitado ("12,90", "12.90") em número; vazio ou inválido vira NaN
export const textoParaNumero = (texto) => (String(texto).trim() === '' ? NaN : Number(String(texto).replace(',', '.')));

export const formatarDataHora = (dataHora) => {
  if (!dataHora) return '—';
  return new Date(dataHora).toLocaleString('pt-BR');
};

// Texto em minúsculas e sem acentos, para buscas ("Genérico" e "generico" ficam iguais)
export const normalizarTexto = (texto) =>
  (texto || '').normalize('NFD').replace(/[̀-ͯ]/g, '').toLowerCase();

// Mensagem enviada pelo backend (campo "message") ou um texto padrão
export const mensagemDeErro = (error, textoPadrao) => error.response?.data?.message || textoPadrao;

export const TIPOS_MOVIMENTACAO = {
  ENTRADA: 'Entrada',
  SAIDA_VENDA: 'Saída (venda)',
  AJUSTE_INVENTARIO: 'Ajuste de inventário',
  PERDA: 'Perda',
  DEVOLUCAO: 'Devolução',
  CANCELAMENTO_VENDA: 'Cancelamento de venda'
};

export const FORMAS_PAGAMENTO = {
  DINHEIRO: 'Dinheiro',
  CARTAO_DEBITO: 'Cartão de débito',
  CARTAO_CREDITO: 'Cartão de crédito',
  PIX: 'PIX'
};

export const CONTROLES_RECEITA = {
  sob_prescricao: 'Venda sob prescrição',
  especial: 'Controle especial'
};

export const SITUACOES_LOTE = {
  VENCIDO: { texto: 'Vencido', cor: '#dc3545', fundo: '#f8d7da' },
  A_VENCER: { texto: 'A vencer', cor: '#856404', fundo: '#fff3cd' },
  OK: { texto: 'Dentro da validade', cor: '#155724', fundo: '#d4edda' },
  SEM_VALIDADE: { texto: 'Sem validade (lote único)', cor: '#555', fundo: '#e9ecef' }
};
