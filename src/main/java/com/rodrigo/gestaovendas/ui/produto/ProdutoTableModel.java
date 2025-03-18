package com.rodrigo.gestaovendas.ui.produto;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.rodrigo.gestaovendas.domain.models.Produto;

public class ProdutoTableModel extends AbstractTableModel{

	private static final long serialVersionUID = 1L;
	private String[] colunas = {"COD", "Descrição do Produto", "Preço Unitário"};
	private List<Produto> produtos;
	

	public ProdutoTableModel(List<Produto> produtos) {
		this.produtos = produtos;
	}

	@Override
	public int getRowCount() {
		return produtos.size();
	}

	@Override
	public int getColumnCount() {
		return colunas.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Produto produto = produtos.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return produto.getCodigo();
            case 1:
                return produto.getDescricao();
            case 2:
                return produto.getPreco();
            default:
                return null;
        }
	}
	
	@Override
    public String getColumnName(int column) {
        return colunas[column];
    }

    public void atualizarDados(List<Produto> novosProdutos) {
        this.produtos = novosProdutos;
        fireTableDataChanged();
    }


}
