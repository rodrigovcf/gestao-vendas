package com.rodrigo.gestaovendas.infra;

import com.rodrigo.gestaovendas.domain.models.Cliente;
import com.rodrigo.gestaovendas.domain.repositories.ClienteRepository;
import com.rodrigo.gestaovendas.exceptions.DAOException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO implements ClienteRepository{

	@Override
	public Cliente incluir(Cliente cliente) {
		String sql = "INSERT INTO cliente (codigo, nome, limite_compra, dia_fechamento) VALUES (?, ?, ?, ?)";
		try (Connection conexao = ConexaoBD.conectar();
				PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			stmt.setInt(1, cliente.getCodigo());
			stmt.setString(2, cliente.getNome());
			stmt.setDouble(3, cliente.getLimiteCompra());
			stmt.setInt(4, cliente.getDiaFechamentoFatura());

			stmt.executeUpdate();
			
			try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					return cliente.toBuilder()
							.codigo(generatedKeys.getInt(1))
							.build();					
				}
			}
			return cliente;

		} catch (SQLException e) {
			throw new DAOException("Erro ao inserir cliente", e);
		}
	}

	@Override
	public Cliente consultar(int codigo) {
		String sql = "SELECT * FROM cliente WHERE codigo = ?";
		try (Connection conexao = ConexaoBD.conectar(); PreparedStatement stmt = conexao.prepareStatement(sql)) {

			stmt.setInt(1, codigo);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {					
					return Cliente.builder()
							.codigo(rs.getInt("codigo"))
							.nome(rs.getString("nome"))
							.limiteCompra(rs.getDouble("limite_compra"))
							.diaFechamentoFatura(rs.getInt("dia_fechamento"))
							.build();
				}
			}
		} catch (SQLException e) {
			throw new DAOException("Erro ao buscar cliente", e);
		}
		return null;
	}

	@Override
	public List<Cliente> listarTodos() {
		List<Cliente> clientes = new ArrayList<>();
		String sql = "SELECT * FROM cliente";
		try (Connection conexao = ConexaoBD.conectar();
				Statement stmt = conexao.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				clientes.add(Cliente.builder()
						.codigo(rs.getInt("codigo"))
						.nome(rs.getString("nome"))
						.limiteCompra(rs.getDouble("limite_compra"))
						.diaFechamentoFatura(rs.getInt("dia_fechamento"))
						.build());
			}
		} catch (SQLException e) {
			throw new DAOException("Erro ao listar clientes", e);
		}
		return clientes;
	}
	
	@Override
	public Cliente alterar(Cliente cliente) {
	    String sql = "UPDATE produto SET nome = ?, limite_compra = ?, dia_fechamento_fatura = ? WHERE codigo = ?";

	    try (Connection conexao = ConexaoBD.conectar();
	         PreparedStatement stmt = conexao.prepareStatement(sql)) {
	        	        
	        stmt.setInt(1, cliente.getCodigo());
	        stmt.setString(2, cliente.getNome());
	        stmt.setDouble(3, cliente.getLimiteCompra());
	        stmt.setInt(4, cliente.getDiaFechamentoFatura());

	        int affectedRows = stmt.executeUpdate();
 
	        if (affectedRows > 0) {
	            return cliente;  
	        } else {
	            throw new DAOException("Cliente não encontrado ou não alterado.");
	        }

	    } catch (SQLException e) {	       
	        throw new DAOException("Erro ao alterar o cliente", e);
	    }
	}

	@Override
	public boolean excluir(int codigo) {
	    String sql = "DELETE FROM cliente WHERE codigo = ?";

	    try (Connection conexao = ConexaoBD.conectar();
	         PreparedStatement stmt = conexao.prepareStatement(sql)) {

	        stmt.setInt(1, codigo);

	        int affectedRows = stmt.executeUpdate();

	        return affectedRows > 0;

	    } catch (SQLException e) {
	        throw new DAOException("Erro ao excluir o cliente com código " + codigo, e);
	    }
	}

}
