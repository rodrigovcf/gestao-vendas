package com.rodrigo.gestaovendas.app;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import com.rodrigo.gestaovendas.infra.ConexaoBD;
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
            		.codigoProduto(produto.getCodigo())
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

        // Chamada para salvar a venda
        int codigoVenda = vendaRepository.incluir(venda); // Salva a venda e obtém o código gerado

        // Atualizar os itens com o código da venda
        for (ItemVenda item : itensVenda) {
            item.setCodigoVenda(codigoVenda); // Associa o código da venda ao item
        }

        // Chamada para salvar os itens da venda
        vendaRepository.salvarItensVenda(itensVenda); // Deve salvar os itens na tabela `venda_produto`
        
     // Atualizar o limite de compra do cliente
        double novoLimite = cliente.getLimiteCompra() - totalVenda;
        clienteRepository.atualizarLimiteCompra(clienteId, novoLimite);

    }
    
    public void verificarLimiteCredito(int clienteId, double valorCompraAtual) {
        Cliente cliente = clienteRepository.consultar(clienteId);
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não encontrado.");
        }

        LocalDate dataAtual = LocalDate.now();
        LocalDate diaFechamentoFatura = cliente.getDiaFechamentoFatura();

        // Determina a data do último fechamento
        LocalDate dataUltimoFechamento = diaFechamentoFatura.withYear(dataAtual.getYear()).withMonth(dataAtual.getMonthValue());
        if (dataUltimoFechamento.isAfter(dataAtual)) {
            dataUltimoFechamento = dataUltimoFechamento.minusMonths(1);
        }

        // Obtém as vendas realizadas após o fechamento
        List<Venda> vendasAposFechamento = vendaRepository.buscarPorPeriodo(
                java.sql.Date.valueOf(dataUltimoFechamento),
                java.sql.Date.valueOf(dataAtual));

        if (vendasAposFechamento == null) {
            vendasAposFechamento = new ArrayList<>();
        }

        // Calcula o total das compras realizadas após o fechamento
        double totalVendas = ValidacaoUtil.calcularComprasAposFechamento(vendasAposFechamento);

        // Verifica se o limite será excedido
        if (ValidacaoUtil.verificarSeLimiteExcedido(cliente.getLimiteCompra(), totalVendas, valorCompraAtual)) {
            // Calcula a data do próximo fechamento
            LocalDate proximoFechamento = ValidacaoUtil.calcularProximoFechamento(diaFechamentoFatura);

            // Lança exceção com a mensagem apropriada
            throw new IllegalStateException(
                    String.format("Limite excedido! Valor disponível: %.2f\nPróximo fechamento: %s",
                            ValidacaoUtil.calcularValorDisponivel(cliente.getLimiteCompra(), totalVendas),
                            proximoFechamento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        }

        // Caso o limite não seja excedido, exibe mensagem no console (opcional)
        System.out.println("Compra aprovada! Limite restante: R$ " +
                ValidacaoUtil.calcularValorDisponivel(cliente.getLimiteCompra(), totalVendas + valorCompraAtual));
    }





    public List<Venda> buscarVendasPorCliente(int clienteId) {
        return vendaRepository.buscarPorCliente(clienteId);
    }

    public List<Venda> buscarPorPeriodo(Date inicio, Date fim) {
        String sql = "SELECT * FROM venda WHERE data_venda BETWEEN ? AND ?";
        List<Venda> vendas = new ArrayList<>(); // Inicializa a lista vazia para evitar null
        try (Connection conexao = ConexaoBD.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setDate(1, new java.sql.Date(inicio.getTime()));
            stmt.setDate(2, new java.sql.Date(fim.getTime()));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Venda venda = new Venda();
                    venda.setCodigo(rs.getInt("codigo"));
                    venda.setValorTotal(rs.getDouble("valor_total"));
                    venda.setData(rs.getDate("data_venda").toLocalDate()); // Converte para LocalDate
                    vendas.add(venda);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar vendas por período: " + e.getMessage(), e);
        }
        return vendas; // Garante que retorna uma lista vazia, mesmo que nenhuma venda seja encontrada
    }


    public void alterar(Venda venda) {
        if (venda == null || venda.getCodigo() <= 0) {
            throw new IllegalArgumentException("Venda inválida para atualização.");
        }

        vendaRepository.alterar(venda); 
    }



	public List<Venda> listarTodos() {
		// TODO Auto-generated method stub
		return null;
	}

	public Venda consultar(int codigoVenda) {
		// TODO Auto-generated method stub
		return null;
	}

	public void excluir(int codigoVenda) {
		// TODO Auto-generated method stub
		
	}

	public List<Venda> buscarPorFiltros(String cliente, String produto, LocalDate inicio, LocalDate fim) {
		// TODO Auto-generated method stub
		return null;
	}

	public void incluir(Venda venda) {
		// TODO Auto-generated method stub
		
	}

	

}

