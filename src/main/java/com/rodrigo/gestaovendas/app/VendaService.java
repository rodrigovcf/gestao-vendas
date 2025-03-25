package com.rodrigo.gestaovendas.app;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.rodrigo.gestaovendas.domain.models.Cliente;
import com.rodrigo.gestaovendas.domain.models.ItemVenda;
import com.rodrigo.gestaovendas.domain.models.Produto;
import com.rodrigo.gestaovendas.domain.models.Venda;
import com.rodrigo.gestaovendas.domain.repositories.ClienteRepository;
import com.rodrigo.gestaovendas.domain.repositories.ProdutoRepository;
import com.rodrigo.gestaovendas.domain.repositories.VendaDTO;
import com.rodrigo.gestaovendas.domain.repositories.VendaRepository;
import com.rodrigo.gestaovendas.utils.ValidacaoUtil;

public class VendaService {
    private final VendaRepository vendaRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;

    public VendaService(VendaRepository vendaRepository, ClienteRepository clienteRepository, ProdutoRepository produtoRepository) {
        this.vendaRepository = vendaRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
    }

    public Venda registrarVenda(int clienteId, Map<Integer, Integer> produtosQuantidade) {
        Cliente cliente = clienteRepository.consultar(clienteId);
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não encontrado.");
        }

        // Lógica de registro, cálculo de itens e valor total (conforme já implementado)
        List<ItemVenda> itensVenda = new ArrayList<>();
        double valorTotal = 0.0;

        for (Map.Entry<Integer, Integer> entry : produtosQuantidade.entrySet()) {
            Produto produto = produtoRepository.consultar(entry.getKey());
            if (produto == null) {
                throw new IllegalArgumentException("Produto não encontrado.");
            }

            int quantidade = entry.getValue();
            double precoUnitario = produto.getPreco();
            valorTotal += quantidade * precoUnitario;

            itensVenda.add(new ItemVenda(0, 0, produto, quantidade, precoUnitario));
        }

        Venda novaVenda = new Venda(0, clienteId, cliente, itensVenda, LocalDate.now(), valorTotal);
        int codigoVenda = vendaRepository.incluir(novaVenda); // Registra a venda no banco
        novaVenda.setCodigo(codigoVenda); // Atualiza o ID da venda

        return novaVenda; // Retorna a venda criada
    }

    
    public void verificarLimiteCredito(int clienteId, double valorCompraAtual) {
        Cliente cliente = clienteRepository.consultar(clienteId);
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não encontrado.");
        }

        LocalDate diaFechamentoFatura = cliente.getDiaFechamentoFatura();
        if (diaFechamentoFatura == null) {
            throw new IllegalStateException("Dia de fechamento de fatura não configurado para o cliente.");
        }

        LocalDate dataAtual = LocalDate.now();
        LocalDate dataUltimoFechamento = diaFechamentoFatura.withYear(dataAtual.getYear()).withMonth(dataAtual.getMonthValue());
        if (dataUltimoFechamento.isAfter(dataAtual)) {
            dataUltimoFechamento = dataUltimoFechamento.minusMonths(1);
        }

        System.out.println("Data atual: " + dataAtual);
        System.out.println("Data do último fechamento: " + dataUltimoFechamento);

        // Obtém as vendas realizadas após o fechamento
        List<Venda> vendasAposFechamento = vendaRepository.buscarPorPeriodo(
                java.sql.Date.valueOf(dataUltimoFechamento),
                java.sql.Date.valueOf(dataAtual),
                clienteId);

        if (vendasAposFechamento == null) {
            vendasAposFechamento = new ArrayList<>();
        }

        System.out.println("Vendas após fechamento: " + vendasAposFechamento);

        // Calcula o total das compras realizadas após o fechamento
        double totalVendas = ValidacaoUtil.calcularComprasAposFechamento(vendasAposFechamento);

        System.out.println("Total de vendas após fechamento: " + totalVendas);
        System.out.println("Valor da compra atual: " + valorCompraAtual);
        System.out.println("Limite do cliente: " + cliente.getLimiteCompra());

        double limiteDisponivel = cliente.getLimiteCompra() - totalVendas;

        System.out.printf("Limite disponível: %.2f, Total vendas: %.2f, Compra atual: %.2f%n",
                limiteDisponivel, totalVendas, valorCompraAtual);

        // Valida se o limite será excedido
        if (limiteDisponivel < valorCompraAtual) {
            LocalDate proximoFechamento = ValidacaoUtil.calcularProximoFechamento(diaFechamentoFatura);

            throw new IllegalStateException(
                    String.format("Limite excedido! Valor disponível: %.2f\nPróximo fechamento: %s",
                            limiteDisponivel,
                            proximoFechamento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        }

        System.out.println("Compra aprovada! Limite restante: R$ " + (limiteDisponivel - valorCompraAtual));

    }


    public List<Venda> buscarVendasPorCliente(int clienteId) {
        return vendaRepository.buscarPorCliente(clienteId);
    }

//    public List<Venda> buscarPorPeriodo(Date inicio, Date fim) {
//        return vendaRepository.buscarPorPeriodo(inicio, fim); // Chamada ao repositório
//    }

    public void alterar(Venda venda) {
        if (venda == null || venda.getCodigo() <= 0) {
            throw new IllegalArgumentException("Venda inválida para atualização.");
        }

        vendaRepository.alterar(venda); 
    }
    
    public List<Venda> buscarTodasVendas() {
        // Consulta todas as vendas no repositório
        return vendaRepository.listarTodos();
    }


    public Venda buscarVendaPorId(int idVenda) {
        Venda venda = vendaRepository.buscarVendaPorId(idVenda);
        if (venda == null) {
            throw new IllegalArgumentException("Venda não encontrada para o ID: " + idVenda);
        }
        return venda;
    }


}

