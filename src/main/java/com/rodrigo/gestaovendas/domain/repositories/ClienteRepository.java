package com.rodrigo.gestaovendas.domain.repositories;

import java.util.List;

import com.rodrigo.gestaovendas.domain.models.Cliente;

interface ClienteRepository {
    
	void salvar(Cliente cliente);
    Cliente buscarPorCodigo(int codigo);
    List<Cliente> listarTodos();
    void excluir(int codigo);
}
