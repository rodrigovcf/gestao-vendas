package com.rodrigo.gestaovendas.infra;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.rodrigo.gestaovendas.domain.models.Cliente;
import com.rodrigo.gestaovendas.domain.models.ItemVenda;
import com.rodrigo.gestaovendas.domain.models.Produto;
import com.rodrigo.gestaovendas.domain.models.Venda;
import com.rodrigo.gestaovendas.domain.repositories.VendaDTO;
import com.rodrigo.gestaovendas.domain.repositories.VendaRepository;
import com.rodrigo.gestaovendas.exceptions.DAOException;

public class VendaDAO implements VendaRepository {

	@Override
    public int incluir(Venda venda) {
        String sql = "INSERT INTO venda (codigo_cliente, data_venda, valor_total) VALUES (?, ?, ?)";
        try (Connection conexao = ConexaoBD.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, venda.getCliente().getCodigo()); 
            stmt.setDate(2, Date.valueOf(venda.getData()));
            stmt.setDouble(3, venda.getValorTotal());
            stmt.executeUpdate();

            // Recupera o código gerado
            try (var rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar venda: " + e.getMessage(), e);
        }
        return 0;
    }
	
	@Override
	public Venda consultar(int idVenda) {
	    String sqlVenda = "SELECT v.codigo AS id_venda, v.codigo_cliente, v.data_venda, v.valor_total " +
	                      "FROM venda v " +
	                      "WHERE v.codigo = ?";
	    String sqlCliente = "SELECT codigo, nome, limite_compra, dia_fechamento_fatura " +
	                        "FROM clientes " +
	                        "WHERE codigo = ?";
	    String sqlProdutos = "SELECT vp.codigo_produto, vp.quantidade, p.descricao, p.preco " +
	                         "FROM venda_produto vp " +
	                         "JOIN produto p ON vp.codigo_produto = p.codigo " +
	                         "WHERE vp.codigo_venda = ?";

	    try (Connection conexao = ConexaoBD.conectar();
	         PreparedStatement stmtVenda = conexao.prepareStatement(sqlVenda);
	         PreparedStatement stmtCliente = conexao.prepareStatement(sqlCliente);
	         PreparedStatement stmtProdutos = conexao.prepareStatement(sqlProdutos)) {

	        // Consultar informações da venda
	        stmtVenda.setInt(1, idVenda);
	        ResultSet rsVenda = stmtVenda.executeQuery();

	        if (!rsVenda.next()) {
	            return null; // Venda não encontrada
	        }

	        Venda venda = new Venda();
	        venda.setCodigo(rsVenda.getInt("id_venda"));
	        venda.setCodigoCliente(rsVenda.getInt("codigo_cliente"));
	        venda.setData(rsVenda.getDate("data_venda").toLocalDate());
	        venda.setValorTotal(rsVenda.getDouble("valor_total"));

	        // Consultar informações do cliente
	        stmtCliente.setInt(1, venda.getCodigoCliente());
	        ResultSet rsCliente = stmtCliente.executeQuery();

	        if (rsCliente.next()) {
	            Cliente cliente = new Cliente();
	            cliente.setCodigo(rsCliente.getInt("codigo"));
	            cliente.setNome(rsCliente.getString("nome"));
	            cliente.setLimiteCompra(rsCliente.getDouble("limite_compra"));
	            cliente.setDiaFechamentoFatura(rsCliente.getDate("dia_fechamento_fatura").toLocalDate());

	            venda.setCliente(cliente); // Associar o cliente à venda
	        } else {
	            throw new RuntimeException("Cliente associado à venda não encontrado.");
	        }

	        // Consultar os produtos associados à venda
	        stmtProdutos.setInt(1, idVenda);
	        ResultSet rsProdutos = stmtProdutos.executeQuery();

	        List<ItemVenda> itensVenda = new ArrayList<>();
	        while (rsProdutos.next()) {
	            ItemVenda item = new ItemVenda();

	            Produto produto = new Produto();
	            produto.setCodigo(rsProdutos.getInt("codigo_produto"));
	            produto.setDescricao(rsProdutos.getString("descricao"));
	            produto.setPreco(rsProdutos.getDouble("preco"));

	            item.setProduto(produto);
	            item.setCodigoProduto(produto.getCodigo());
	            item.setQuantidade(rsProdutos.getInt("quantidade"));
	            item.setPrecoUnitario(produto.getPreco());

	            itensVenda.add(item);
	        }
	        venda.setItens(itensVenda);

	        return venda;

	    } catch (SQLException e) {
	        throw new RuntimeException("Erro ao consultar venda por ID: " + e.getMessage(), e);
	    }
	}


    @Override
    public List<Venda> listarTodos() {
        String sql = "SELECT v.codigo, v.codigo_cliente, v.data_venda, v.valor_total, c.nome FROM venda v " +
                     "JOIN clientes c ON v.codigo_cliente= c.codigo";

        List<Venda> vendas = new ArrayList<>();

        try (Connection conexao = ConexaoBD.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Cliente cliente = Cliente.builder()
                        .codigo(rs.getInt("codigo_cliente"))
                        .nome(rs.getString("nome"))
                        .build();

                Venda venda = Venda.builder()
                        .codigo(rs.getInt("codigo"))
                        .cliente(cliente)
                        .data(rs.getDate("data").toLocalDate())
                        .valorTotal(rs.getDouble("valor_total"))
                        .build();
                
                List<ItemVenda> itens = carregarItensVenda(venda.getCodigo());
                venda.setItens(itens);

                vendas.add(venda);
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao listar todas as vendas", e);
        }

        return vendas;
    }

    @Override
    public boolean excluir(int codigo) {
        String sqlExcluirVendaProduto = "DELETE FROM venda_produto WHERE codigo_venda = ?";
        String sqlExcluirVenda = "DELETE FROM venda WHERE codigo = ?";

        try (Connection conexao = ConexaoBD.conectar();
             PreparedStatement stmtVendaProduto = conexao.prepareStatement(sqlExcluirVendaProduto);
             PreparedStatement stmtVenda = conexao.prepareStatement(sqlExcluirVenda)) {

            // Excluir registros relacionados na tabela 'venda_produto'
            stmtVendaProduto.setInt(1, codigo);
            stmtVendaProduto.executeUpdate();

            // Excluir registro principal na tabela 'venda'
            stmtVenda.setInt(1, codigo);
            int linhasAfetadas = stmtVenda.executeUpdate();

            // Retorna true se a exclusão na tabela 'venda' afetar pelo menos uma linha
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao excluir venda: " + e.getMessage());
            return false;
        }
    }


    @Override
    public Venda alterar(Venda venda) {
        String sqlAtualizarVenda = "UPDATE venda SET valor_total = ?, codigo_cliente = ?, data_venda = ? WHERE codigo = ?";
        String sqlExcluirProdutos = "DELETE FROM venda_produto WHERE codigo_venda = ?";
        String sqlInserirProdutos = "INSERT INTO venda_produto (codigo_venda, codigo_produto, quantidade) VALUES (?, ?, ?)";

        try (Connection conexao = ConexaoBD.conectar();
             PreparedStatement stmtVenda = conexao.prepareStatement(sqlAtualizarVenda);
             PreparedStatement stmtExcluirProdutos = conexao.prepareStatement(sqlExcluirProdutos);
             PreparedStatement stmtInserirProdutos = conexao.prepareStatement(sqlInserirProdutos)) {

            // Atualizar os dados da tabela 'venda'
        	stmtVenda.setDouble(1, venda.getValorTotal()); 
        	stmtVenda.setInt(2, venda.getCodigoCliente());
            stmtVenda.setDate(3, java.sql.Date.valueOf(venda.getData()));
            stmtVenda.setInt(4, venda.getCodigo());
            stmtVenda.executeUpdate();

            // Excluir os produtos antigos relacionados à venda
            stmtExcluirProdutos.setInt(1, venda.getCodigo());
            stmtExcluirProdutos.executeUpdate();

            // Inserir os novos produtos relacionados à venda
            for (ItemVenda item : venda.getItens()) {
                stmtInserirProdutos.setInt(1, venda.getCodigo());
                stmtInserirProdutos.setInt(2, item.getCodigoProduto());
                stmtInserirProdutos.setInt(3, item.getQuantidade());
                stmtInserirProdutos.addBatch();
            }
            stmtInserirProdutos.executeBatch();

            // Retornar o objeto Venda atualizado
            return venda;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao alterar venda: " + e.getMessage(), e);
        }
    }


    private List<ItemVenda> carregarItensVenda(int vendaCodigo) {
        String sql = "SELECT iv.produto_codigo, iv.quantidade, iv.valor_total, p.descricao FROM item_venda iv " +
                     "JOIN produto p ON iv.produto_codigo = p.codigo WHERE iv.venda_codigo = ?";

        List<ItemVenda> itens = new ArrayList<>();

        try (Connection conexao = ConexaoBD.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, vendaCodigo);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Produto produto = Produto.builder()
                        .codigo(rs.getInt("produto_codigo"))
                        .descricao(rs.getString("descricao"))
                        .build();

                ItemVenda item = ItemVenda.builder()
                        .produto(produto)
                        .quantidade(rs.getInt("quantidade"))
                        .build();

                itens.add(item);
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao carregar itens da venda com código " + vendaCodigo, e);
        }

        return itens;
    }
    
    @Override
    public Venda buscarVendaPorId(int idVenda) {
        String sql = "SELECT v.codigo, v.valor_total, v.data_venda, c.codigo AS codigo_cliente, c.nome AS nome_cliente " +
                     "FROM venda v " +
                     "JOIN clientes c ON v.codigo_cliente = c.codigo " +
                     "WHERE v.codigo = ?";
        try (Connection conexao = ConexaoBD.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idVenda);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Venda venda = new Venda();
                    venda.setCodigo(rs.getInt("codigo"));
                    venda.setValorTotal(rs.getDouble("valor_total"));
                    venda.setData(rs.getDate("data_venda").toLocalDate());

                    Cliente cliente = new Cliente();
                    cliente.setCodigo(rs.getInt("codigo_cliente"));
                    cliente.setNome(rs.getString("nome_cliente"));
                    venda.setCliente(cliente);

                    return venda;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar venda por ID: " + e.getMessage(), e);
        }
        return null; // Retorna nulo caso a venda não seja encontrada
    }


	@Override
	public List<Venda> buscarPorCliente(int clienteId) {
		// Melhorias
		return null;
	}

	

	@Override
    public void salvarItensVenda(List<ItemVenda> itens) {
        String sql = "INSERT INTO venda_produto (codigo_venda, codigo_produto, quantidade) VALUES (?, ?, ?)";
        try (Connection conexao = ConexaoBD.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            for (ItemVenda item : itens) {
                stmt.setInt(1, item.getCodigoVenda());
                stmt.setInt(2, item.getCodigoProduto());
                stmt.setInt(3, item.getQuantidade());
                stmt.addBatch();
            }

            stmt.executeBatch();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar itens da venda: " + e.getMessage(), e);
        }
    }

	@Override
	public List<VendaDTO> carregarDadosVendas() {
	    List<VendaDTO> vendas = new ArrayList<>();
	    String sql = "SELECT v.codigo AS id_venda, c.nome AS nome_cliente, p.descricao AS descricao_produto, vp.quantidade, v.data_venda, " +
	                 "       (vp.quantidade * p.preco) AS total " +
	                 "FROM venda v " +
	                 "JOIN clientes c ON v.codigo_cliente = c.codigo " +
	                 "JOIN venda_produto vp ON v.codigo = vp.codigo_venda " +
	                 "JOIN produto p ON vp.codigo_produto = p.codigo";

	    try (Connection conexao = ConexaoBD.conectar();
	         PreparedStatement stmt = conexao.prepareStatement(sql);
	         ResultSet rs = stmt.executeQuery()) {

	        while (rs.next()) {
	            VendaDTO venda = new VendaDTO();
	            venda.setId(rs.getInt("id_venda"));
	            venda.setCliente(rs.getString("nome_cliente"));
	            venda.setProduto(rs.getString("descricao_produto"));
	            venda.setQuantidade(rs.getInt("quantidade"));
	            venda.setData(rs.getDate("data_venda").toLocalDate());
	            venda.setTotal(rs.getDouble("total"));

	            vendas.add(venda);
	        }
	    } catch (SQLException e) {
	        throw new RuntimeException("Erro ao carregar dados de vendas: " + e.getMessage(), e);
	    }
	    return vendas;
	}

	@Override
	public List<VendaDTO> filtrarDados(String cliente, String produto, String dataInicio, String dataFim) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Venda> buscarPorPeriodo(Date inicio, Date fim, int clienteId) {
	    String sql = "SELECT v.codigo, v.valor_total, v.data_venda, " +
	                 "c.codigo AS codigo_cliente, c.nome AS nome_cliente, " +
	                 "c.limite_compra AS limite_cliente, c.dia_fechamento_fatura AS fechamento_cliente " +
	                 "FROM venda v " +
	                 "JOIN clientes c ON v.codigo_cliente = c.codigo " +
	                 "WHERE v.data_venda BETWEEN ? AND ? AND v.codigo_cliente = ?";

	    List<Venda> vendas = new ArrayList<>();
	    try (Connection conexao = ConexaoBD.conectar();
	         PreparedStatement stmt = conexao.prepareStatement(sql)) {

	        stmt.setDate(1, new java.sql.Date(inicio.getTime()));
	        stmt.setDate(2, new java.sql.Date(fim.getTime()));
	        stmt.setInt(3, clienteId);

	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                // Mapeia os dados da venda
	                Venda venda = new Venda();
	                venda.setCodigo(rs.getInt("codigo"));
	                venda.setValorTotal(rs.getDouble("valor_total"));
	                venda.setData(rs.getDate("data_venda").toLocalDate());

	                // Mapeia os dados do cliente
	                Cliente cliente = new Cliente();
	                cliente.setCodigo(rs.getInt("codigo_cliente"));
	                cliente.setNome(rs.getString("nome_cliente"));
	                cliente.setLimiteCompra(rs.getDouble("limite_cliente"));
	                cliente.setDiaFechamentoFatura(rs.getDate("fechamento_cliente") != null 
	                    ? rs.getDate("fechamento_cliente").toLocalDate()
	                    : null);
	                
	                // Associa o cliente à venda
	                venda.setCliente(cliente);

	                vendas.add(venda);
	            }
	        }
	    } catch (SQLException e) {
	        throw new RuntimeException("Erro ao buscar vendas por período: " + e.getMessage(), e);
	    }
	    return vendas;
	}



}


