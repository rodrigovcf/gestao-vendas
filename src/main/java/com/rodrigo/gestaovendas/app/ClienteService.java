package com.rodrigo.gestaovendas.app;

import com.rodrigo.gestaovendas.domain.models.Cliente;
import com.rodrigo.gestaovendas.domain.repositories.ClienteRepository;

public class ClienteService {
    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public void cadastrarCliente(String nome, double limiteCompra, int diaFechamentoFatura) {
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
}


