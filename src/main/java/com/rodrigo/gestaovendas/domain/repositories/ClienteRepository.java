package com.rodrigo.gestaovendas.domain.repositories;

import java.util.List;

import com.rodrigo.gestaovendas.domain.models.Cliente;

public interface ClienteRepository {
    
	Cliente incluir(Cliente cliente);
    Cliente consultar(int codigo);
    List<Cliente> listarTodos();
    boolean excluir(int codigo);
    Cliente alterar(Cliente cliente);
    List<Cliente> buscarNome(String termo);
    Cliente buscarUmPorNome(String termo);
    void atualizarLimiteCompra(int clienteId, double novoLimite);
}
