package com.rodrigo.gestaovendas.infra;

import com.rodrigo.gestaovendas.domain.models.Cliente;
import com.rodrigo.gestaovendas.domain.models.ItemVenda;
import com.rodrigo.gestaovendas.domain.models.Produto;
import com.rodrigo.gestaovendas.domain.models.Venda;
import com.rodrigo.gestaovendas.domain.repositories.VendaRepository;
import com.rodrigo.gestaovendas.exceptions.DAOException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VendaDAO implements VendaRepository {

    @Override
    public void incluir(Venda venda) {
        String sql = "INSERT INTO venda (codigo, cliente_id, data, valor_total) VALUES (?, ?, ?, ?)";

        try (Connection conexao = ConexaoBD.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, venda.getCodigo());
            stmt.setInt(2, venda.getCliente().getCodigo());  
            stmt.setDate(3, Date.valueOf(venda.getData()));
            stmt.setDouble(4, venda.getValorTotal());

            stmt.executeUpdate();
            
            inserirItensVenda(venda);

        } catch (SQLException e) {
            throw new DAOException("Erro ao incluir venda com código " + venda.getCodigo(), e);
        }
    }

    @Override
    public Venda consultar(int codigo) {
        String sql = "SELECT v.codigo, v.cliente_id, v.data, v.valor_total, c.nome FROM venda v " +
                     "JOIN cliente c ON v.cliente_id = c.codigo WHERE v.codigo = ?";

        try (Connection conexao = ConexaoBD.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, codigo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Cliente cliente = Cliente.builder() 
                        .codigo(rs.getInt("cliente_id"))
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

                return venda;
            }
            return null;

        } catch (SQLException e) {
            throw new DAOException("Erro ao consultar venda com código " + codigo, e);
        }
    }

    @Override
    public List<Venda> listarTodos() {
        String sql = "SELECT v.codigo, v.cliente_id, v.data, v.valor_total, c.nome FROM venda v " +
                     "JOIN cliente c ON v.cliente_id = c.codigo";

        List<Venda> vendas = new ArrayList<>();

        try (Connection conexao = ConexaoBD.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Cliente cliente = Cliente.builder()
                        .codigo(rs.getInt("cliente_id"))
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
        String sql = "DELETE FROM venda WHERE codigo = ?";

        try (Connection conexao = ConexaoBD.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, codigo);
            int affectedRows = stmt.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException e) {
            throw new DAOException("Erro ao excluir venda com código " + codigo, e);
        }
    }

    @Override
    public Venda alterar(Venda venda) {
        String sql = "UPDATE venda SET cliente_id = ?, data = ?, valor_total = ? WHERE codigo = ?";

        try (Connection conexao = ConexaoBD.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, venda.getCliente().getCodigo());
            stmt.setDate(2, Date.valueOf(venda.getData()));
            stmt.setDouble(3, venda.getValorTotal());
            stmt.setInt(4, venda.getCodigo());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                // Atualizar os itens da venda
                atualizarItensVenda(venda);
                return consultar(venda.getCodigo()); 
            } else {
                throw new DAOException("Nenhuma venda encontrada com código " + venda.getCodigo());
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao alterar venda com código " + venda.getCodigo(), e);
        }
    }

    // Método auxiliar para atualizar os itens da venda
    private void atualizarItensVenda(Venda venda) {
        // Primeiro, remove os itens antigos da venda
        String sqlDelete = "DELETE FROM item_venda WHERE venda_codigo = ?";

        try (Connection conexao = ConexaoBD.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sqlDelete)) {

            stmt.setInt(1, venda.getCodigo());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("Erro ao remover itens antigos da venda com código " + venda.getCodigo(), e);
        }

        inserirItensVenda(venda);
    }


    // Método auxiliar para inserir os itens da venda
    private void inserirItensVenda(Venda venda) {
        String sql = "INSERT INTO item_venda (venda_codigo, produto_codigo, quantidade, valor_total) VALUES (?, ?, ?, ?)";

        try (Connection conexao = ConexaoBD.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            for (ItemVenda item : venda.getItens()) {
                stmt.setInt(1, venda.getCodigo());
                stmt.setInt(2, item.getProduto().getCodigo());
                stmt.setInt(3, item.getQuantidade());
                stmt.setDouble(4, item.getValorTotal());
                stmt.addBatch();
            }

            stmt.executeBatch();

        } catch (SQLException e) {
            throw new DAOException("Erro ao inserir itens da venda com código " + venda.getCodigo(), e);
        }
    }

    // Método auxiliar para carregar os itens de uma venda
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
}


