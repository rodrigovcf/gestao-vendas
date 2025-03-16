package com.rodrigo.gestaovendas.domain.repositories;

import java.util.List;

import com.rodrigo.gestaovendas.domain.models.Produto;

public interface ProdutoRepository {
    
	Produto incluir(Produto produto);
    Produto consultar(int codigo);
    List<Produto> listarTodos();
    boolean excluir(int codigo);
    Produto alterar(Produto produto);
}