package com.rodrigo.gestaovendas.infra;

import com.rodrigo.gestaovendas.domain.models.Produto;
import com.rodrigo.gestaovendas.domain.repositories.ProdutoRepository;
import com.rodrigo.gestaovendas.exceptions.DAOException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO implements ProdutoRepository{

	@Override
	public Produto incluir(Produto produto) {
	    String sql = "INSERT INTO produto (descricao, preco) VALUES (?, ?)";
	    try (Connection conexao = ConexaoBD.conectar();
	         PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

	        stmt.setString(1, produto.getDescricao());
	        stmt.setDouble(2, produto.getPreco());

	        stmt.executeUpdate();

	        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
	            if (generatedKeys.next()) {
	                return produto.toBuilder()
	                        .codigo(generatedKeys.getInt(1))
	                        .build();
	            }
	        }
	        return produto; 

	    } catch (SQLException e) {
	        throw new DAOException("Erro ao inserir produto", e);
	    }
	}
	
	@Override
	public Produto alterar(Produto produto) {
	    String sql = "UPDATE produto SET descricao = ?, preco = ? WHERE codigo = ?";

	    try (Connection conexao = ConexaoBD.conectar();
	         PreparedStatement stmt = conexao.prepareStatement(sql)) {
	        	        
	        stmt.setString(1, produto.getDescricao());
	        stmt.setDouble(2, produto.getPreco());
	        stmt.setInt(3, produto.getCodigo());

	        int affectedRows = stmt.executeUpdate();
 
	        if (affectedRows > 0) {
	            return produto;  
	        } else {
	            throw new DAOException("Produto não encontrado ou não alterado.");
	        }

	    } catch (SQLException e) {	       
	        throw new DAOException("Erro ao alterar o produto", e);
	    }
	}

	@Override
	public Produto consultar(int codigo) {
		String sql = "SELECT * FROM produto WHERE codigo = ?";
		try (Connection conexao = ConexaoBD.conectar(); PreparedStatement stmt = conexao.prepareStatement(sql)) {

			stmt.setInt(1, codigo);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return Produto.builder()
							.codigo(rs.getInt("codigo"))
	                        .descricao(rs.getString("descricao"))
	                        .preco(rs.getDouble("preco"))
	                        .build();
				}
			}
		} catch (SQLException e) {
			throw new DAOException("Erro ao buscar produto", e);
		}
		return null;
	}

	@Override
	public List<Produto> listarTodos() {
		List<Produto> produtos = new ArrayList<>();
		String sql = "SELECT * FROM produto";
		try (Connection conexao = ConexaoBD.conectar();
				Statement stmt = conexao.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				produtos.add(Produto.builder()
						.codigo(rs.getInt("codigo"))
						.descricao(rs.getString("descricao"))
						.preco(rs.getDouble("preco"))
						.build());
			}
		} catch (SQLException e) {
			throw new DAOException("Erro ao listar produtos", e);
		}
		return produtos;
	}

	@Override
	public boolean excluir(int codigo) {
	    String sql = "DELETE FROM produto WHERE codigo = ?";

	    try (Connection conexao = ConexaoBD.conectar();
	         PreparedStatement stmt = conexao.prepareStatement(sql)) {

	        stmt.setInt(1, codigo);

	        int affectedRows = stmt.executeUpdate();

	        return affectedRows > 0;

	    } catch (SQLException e) {
	        throw new DAOException("Erro ao excluir o produto com código " + codigo, e);
	    }
	}

	@Override
	public boolean estaVinculadoAVenda(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Produto> buscarDesc(String termo) {
		String sql = "SELECT * FROM produto WHERE descricao ILIKE ?";
        try (Connection conexao = ConexaoBD.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, "%" + termo + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                List<Produto> produtos = new ArrayList<>();
                while (rs.next()) {
                	produtos.add(Produto.builder()
                            .codigo(rs.getInt("codigo"))
                            .descricao(rs.getString("descricao"))
                            .preco(rs.getDouble("preco"))
                            .build());
                }
                return produtos;
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao buscar produtos pela descricao nome", e);
        }
	}

}
