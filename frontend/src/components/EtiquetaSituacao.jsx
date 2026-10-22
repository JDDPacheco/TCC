import { SITUACOES_LOTE } from '../utils/formatacao';

// Etiqueta colorida com a situação de validade de um lote (vencido, a vencer, ok, sem validade)
export default function EtiquetaSituacao({ situacao }) {
  const estilo = SITUACOES_LOTE[situacao] || SITUACOES_LOTE.OK;
  return (
    <span
      style={{
        display: 'inline-block',
        padding: '2px 8px',
        borderRadius: '10px',
        fontSize: '0.85em',
        fontWeight: 'bold',
        color: estilo.cor,
        backgroundColor: estilo.fundo,
        whiteSpace: 'nowrap'
      }}
    >
      {estilo.texto}
    </span>
  );
}
