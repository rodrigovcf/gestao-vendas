package com.rodrigo.gestaovendas.infra;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.rodrigo.gestaovendas.domain.models.Cliente;
import com.rodrigo.gestaovendas.domain.repositories.ClienteRepository;
import com.rodrigo.gestaovendas.exceptions.DAOException;

public class ClienteDAO implements ClienteRepository {

    // Método para incluir cliente no banco de dados
    @Override
    public Cliente incluir(Cliente cliente) {
        String sql = "INSERT INTO clientes (nome, limite_compra, dia_fechamento_fatura) VALUES (?, ?, ?)";
        try (Connection conexao = ConexaoBD.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, cliente.getNome());
            stmt.setDouble(2, cliente.getLimiteCompra());
            stmt.setDate(3, Util.toSqlDate(cliente.getDiaFechamentoFatura())); // Utilitário para converter LocalDate
            stmt.executeUpdate();

            // Recuperar o valor gerado para 'codigo' pelo banco
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    cliente.setCodigo(rs.getInt(1));
                }
            }
            return cliente;
        } catch (SQLException e) {
            throw new DAOException("Erro ao inserir cliente: " + e.getMessage(), e);
        }
    }

    // Método para consultar cliente pelo código
    @Override
    public Cliente consultar(int codigo) {
        String sql = "SELECT * FROM clientes WHERE codigo = ?";
        try (Connection conexao = ConexaoBD.conectar(); PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, codigo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Cliente.builder()
                            .codigo(rs.getInt("codigo"))
                            .nome(rs.getString("nome"))
                            .limiteCompra(rs.getDouble("limite_compra"))
                            .diaFechamentoFatura(Util.toLocalDate(rs.getDate("dia_fechamento_fatura"))) // Utilitário
                            .build();
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao buscar cliente", e);
        }
        return null; // Cliente não encontrado
    }

    // Método para listar todos os clientes
    @Override
    public List<Cliente> listarTodos() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes ORDER BY codigo";
        try (Connection conexao = ConexaoBD.conectar();
             Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                clientes.add(Cliente.builder()
                        .codigo(rs.getInt("codigo"))
                        .nome(rs.getString("nome"))
                        .limiteCompra(rs.getDouble("limite_compra"))
                        .diaFechamentoFatura(Util.toLocalDate(rs.getDate("dia_fechamento_fatura"))) // Utilitário
                        .build());
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao listar clientes", e);
        }
        return clientes;
    }

    // Método para alterar os dados de um cliente
    @Override
    public Cliente alterar(Cliente cliente) {
        String sql = "UPDATE clientes SET nome = ?, limite_compra = ?, dia_fechamento_fatura = ? WHERE codigo = ?";

        try (Connection conexao = ConexaoBD.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, cliente.getNome());
            stmt.setDouble(2, cliente.getLimiteCompra());
            stmt.setDate(3, Util.toSqlDate(cliente.getDiaFechamentoFatura())); // Utilitário
            stmt.setInt(4, cliente.getCodigo());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                return cliente; // Retorna o cliente atualizado
            } else {
                throw new DAOException("Cliente não encontrado ou não alterado.");
            }

        } catch (SQLException e) {
            throw new DAOException("Erro ao alterar o cliente", e);
        }
    }

    // Método para excluir cliente pelo código
    @Override
    public boolean excluir(int codigo) {
        String sql = "DELETE FROM clientes WHERE codigo = ?";

        try (Connection conexao = ConexaoBD.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, codigo);
            int affectedRows = stmt.executeUpdate();

            return affectedRows > 0; // Retorna true se o cliente foi excluído
        } catch (SQLException e) {
            throw new DAOException("Erro ao excluir o cliente com código " + codigo, e);
        }
    }

    // Método para buscar clientes pelo nome
    @Override
    public List<Cliente> buscarNome(String termo) {
        String sql = "SELECT * FROM clientes WHERE nome ILIKE ?";
        try (Connection conexao = ConexaoBD.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, "%" + termo + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                List<Cliente> clientes = new ArrayList<>();
                while (rs.next()) {
                    clientes.add(Cliente.builder()
                            .codigo(rs.getInt("codigo"))
                            .nome(rs.getString("nome"))
                            .limiteCompra(rs.getDouble("limite_compra"))
                            .diaFechamentoFatura(Util.toLocalDate(rs.getDate("dia_fechamento_fatura")))
                            .build());
                }
                return clientes;
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao buscar clientes por nome", e);
        }
    }

	@Override
	public Cliente buscarUmPorNome(String termo) {
		String sql = "SELECT * FROM clientes WHERE nome = ?";
        try (Connection conexao = ConexaoBD.conectar(); PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, termo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Cliente.builder()
                            .codigo(rs.getInt("codigo"))
                            .nome(rs.getString("nome"))
                            .limiteCompra(rs.getDouble("limite_compra"))
                            .diaFechamentoFatura(Util.toLocalDate(rs.getDate("dia_fechamento_fatura"))) // Utilitário
                            .build();
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao buscar cliente", e);
        }
        return null; // Cliente não encontrado
	}
}

// Classe Utilitária para conversão de datas
class Util {
    public static LocalDate toLocalDate(Date date) {
        return date != null ? date.toLocalDate() : null;
    }

    public static Date toSqlDate(LocalDate localDate) {
        return localDate != null ? Date.valueOf(localDate) : null;
    }
}
