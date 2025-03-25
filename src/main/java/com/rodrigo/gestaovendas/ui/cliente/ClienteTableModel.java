package com.rodrigo.gestaovendas.ui.cliente;

import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.rodrigo.gestaovendas.domain.models.Cliente;

public class ClienteTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1L;
	private String[] colunas = {"ID", "Nome", "Limite de Crédito", "Fechamento Fatura"};
    private List<Cliente> clientes;

    public ClienteTableModel(List<Cliente> clientes) {
        this.clientes = clientes;
    }

    @Override
    public int getRowCount() {
        return clientes.size();
    }

    @Override
    public int getColumnCount() {
        return colunas.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Cliente cliente = clientes.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return cliente.getCodigo();
            case 1:
                return cliente.getNome();
            case 2:
                return cliente.getLimiteCompra();
            case 3: 
            	return cliente.getDiaFechamentoFatura() != null
                    ? cliente.getDiaFechamentoFatura().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    : "N/A"; // Dia Fechamento (formatado)
            default:
                return null;
        }
    }


    @Override
    public String getColumnName(int column) {
        return colunas[column];
    }

    public void atualizarDados(List<Cliente> novosClientes) {
        this.clientes = novosClientes;
        fireTableDataChanged();
    }
}

