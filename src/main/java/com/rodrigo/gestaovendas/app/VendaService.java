package com.rodrigo.gestaovendas.app;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.rodrigo.gestaovendas.domain.models.Cliente;
import com.rodrigo.gestaovendas.domain.models.ItemVenda;
import com.rodrigo.gestaovendas.domain.models.Produto;
import com.rodrigo.gestaovendas.domain.models.Venda;
import com.rodrigo.gestaovendas.domain.repositories.ClienteRepository;
import com.rodrigo.gestaovendas.domain.repositories.ProdutoRepository;
import com.rodrigo.gestaovendas.domain.repositories.VendaRepository;

public class VendaService {
    private final VendaRepository vendaRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;

    public VendaService(VendaRepository vendaRepository, ClienteRepository clienteRepository, ProdutoRepository produtoRepository) {
        this.vendaRepository = vendaRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
    }

    public void registrarVenda(int clienteId, Map<Integer, Integer> produtosQuantidade) {
        Cliente cliente = clienteRepository.consultar(clienteId);
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não encontrado.");
        }

        double totalVenda = 0;
        List<ItemVenda> itensVenda = new ArrayList<>();

        for (Map.Entry<Integer, Integer> entry : produtosQuantidade.entrySet()) {
            Produto produto = produtoRepository.consultar(entry.getKey());
            if (produto == null) {
                throw new IllegalArgumentException("Produto não encontrado.");
            }

            ItemVenda item = ItemVenda.builder()
                    .produto(produto)
                    .quantidade(entry.getValue())
                    .build();

            itensVenda.add(item);
            totalVenda += produto.getPreco() * entry.getValue();
        }

        if (totalVenda > cliente.getLimiteCompra()) {
            throw new IllegalStateException("Limite de crédito excedido. Disponível: " + cliente.getLimiteCompra());
        }

        Venda venda = Venda.builder()
                .cliente(cliente)
                .itens(itensVenda)
                .valorTotal(totalVenda)
                .data(LocalDate.now())
                .build();

        vendaRepository.incluir(venda);
    }

    public List<Venda> buscarVendasPorCliente(int clienteId) {
        return vendaRepository.buscarPorCliente(clienteId);
    }

    public List<Venda> buscarVendasPorPeriodo(Date inicio, Date fim) {
        return vendaRepository.buscarPorPeriodo(inicio, fim);
    }
}

