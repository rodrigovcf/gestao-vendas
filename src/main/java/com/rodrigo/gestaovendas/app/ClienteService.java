package com.rodrigo.gestaovendas.app;

import java.time.LocalDate;
import java.util.List;

import com.rodrigo.gestaovendas.domain.models.Cliente;
import com.rodrigo.gestaovendas.domain.repositories.ClienteRepository;

public class ClienteService {
    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public void cadastrarCliente(String nome, double limiteCompra, LocalDate diaFechamentoFatura) {
        if (limiteCompra < 0) {
            throw new IllegalArgumentException("O limite de crédito deve ser positivo.");
        }

        Cliente cliente = Cliente.builder()
                .nome(nome)
                .limiteCompra(limiteCompra)
                .diaFechamentoFatura(diaFechamentoFatura)
                .build();

        clienteRepository.incluir(cliente);
    }

    public Cliente buscarClientePorId(int id) {
        return clienteRepository.consultar(id);
    }

    public void excluirCliente(int id) {
//        if (clienteRepository.itemVendasAssociadas(id)) {
//            throw new IllegalStateException("Cliente possui vendas associadas e não pode ser excluído.");
//        }
        clienteRepository.excluir(id);
    }

	public List<Cliente> buscarTodosClientes() {
		return clienteRepository.listarTodos();
	}

	public List<Cliente> buscarPorNome(String termo) {
		return clienteRepository.buscarNome(termo);
	}
	
	public Cliente buscarUmPorNome(String termo) {
		return clienteRepository.buscarUmPorNome(termo);
	}

	public void atualizarCliente(Cliente clienteEdicao) {
	    if (clienteEdicao == null) {
	        throw new IllegalArgumentException("O cliente a ser atualizado não pode ser nulo.");
	    }

	    if (clienteEdicao.getCodigo() == 0) {
	        throw new IllegalArgumentException("O cliente deve ter um código válido para ser atualizado.");
	    }

	    if (clienteEdicao.getLimiteCompra() < 0) {
	        throw new IllegalArgumentException("O limite de crédito deve ser positivo.");
	    }

	    try {
	        // Delegar a atualização para o repositório
	        clienteRepository.alterar(clienteEdicao);
	    } catch (Exception e) {
	        throw new RuntimeException("Erro ao atualizar o cliente: " + e.getMessage(), e);
	    }
	}

}


