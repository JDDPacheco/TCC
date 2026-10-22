import { useEffect, useState } from 'react';
import Select from 'react-select';
import api from '../services/api';

/**
 * Campo de busca de produto por nome ou EAN (também funciona com leitor de código de barras:
 * o leitor "digita" o EAN e o Enter seleciona o produto encontrado).
 * Devolve o produto completo ({ ean, nomeComercial, tipoProduto, unidadeDeMedida }) ou null.
 */
export default function SeletorProduto({ ean, onChange, placeholder = 'Buscar produto por nome ou EAN...', isClearable = true }) {
  const [produtos, setProdutos] = useState([]);

  useEffect(() => {
    api.get('/api/produto')
      .then((response) => setProdutos(response.data || [])) // 204 (sem produtos) vem sem corpo
      .catch((error) => console.error('Erro ao buscar produtos:', error));
  }, []);

  const opcoes = produtos.map((produto) => ({
    value: produto.ean,
    label: `${produto.nomeComercial} — EAN ${produto.ean}`,
    produto
  }));

  return (
    <Select
      options={opcoes}
      value={opcoes.find((opcao) => opcao.value === ean) || null}
      onChange={(opcao) => onChange(opcao ? opcao.produto : null)}
      placeholder={placeholder}
      noOptionsMessage={() => 'Nenhum produto encontrado'}
      isClearable={isClearable}
    />
  );
}
