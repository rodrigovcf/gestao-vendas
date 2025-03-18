package com.rodrigo.gestaovendas.app;

import java.util.List;

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

    public List<Produto> buscarPorDescricao(String termo) {
        return produtoRepository.buscarDesc(termo);
    }
    
    public Produto buscarProdutoPorId(int id) {
        return produtoRepository.consultar(id);
    }

    public void excluirProduto(int id) {
        if (produtoRepository.estaVinculadoAVenda(id)) {
            throw new IllegalStateException("Produto está vinculado a uma venda e não pode ser excluído.");
        }
        produtoRepository.excluir(id);
    }

	public List<Produto> buscarTodosProdutos() {
		return produtoRepository.listarTodos();
	}

	public boolean estaVinculadoAVenda(Integer idProduto) {
		// TODO Auto-generated method stub
		return false;
	}

	public void atualizarProduto(Produto produtoEdicao) {
		 if (produtoEdicao == null) {
		        throw new IllegalArgumentException("O produto a ser atualizado não pode ser nulo.");
		    }

		    if (produtoEdicao.getCodigo() == 0) {
		        throw new IllegalArgumentException("O produto deve ter um código válido para ser atualizado.");
		    }

		    if (produtoEdicao.getPreco() < 0) {
		        throw new IllegalArgumentException("O preço deve ser positivo.");
		    }

		    try {
		        // Delegar a atualização para o repositório
		        produtoRepository.alterar(produtoEdicao);
		    } catch (Exception e) {
		        throw new RuntimeException("Erro ao atualizar o produto: " + e.getMessage(), e);
		
		    }
	}
}
