package com.rodrigo.gestaovendas.app;

import com.rodrigo.gestaovendas.domain.models.Produto;
import com.rodrigo.gestaovendas.domain.repositories.ProdutoRepository;

public class ProdutoService {
    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public void cadastrarProduto(String descricao, double preco) {
        if (preco <= 0) {
            throw new IllegalArgumentException("O preço do produto deve ser maior que zero.");
        }

        Produto produto = Produto.builder()
                .descricao(descricao)
                .preco(preco)
                .build();

        produtoRepository.incluir(produto);
    }

    public Produto buscarProdutoPorId(int id) {
        return produtoRepository.consultar(id);
    }

    public void excluirProduto(int id) {
//        if (produtoRepository.estaVinculadoAVenda(id)) {
//            throw new IllegalStateException("Produto está vinculado a uma venda e não pode ser excluído.");
//        }
        produtoRepository.excluir(id);
    }
}
