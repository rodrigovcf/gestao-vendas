package com.rodrigo.gestaovendas.domain.repositories;

import java.util.List;

import com.rodrigo.gestaovendas.domain.models.Produto;

interface ProdutoRepository {
    
	void salvar(Produto produto);
    Produto buscarPorCodigo(int codigo);
    List<Produto> listarTodos();
    void excluir(int codigo);
}