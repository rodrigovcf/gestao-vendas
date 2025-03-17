package com.rodrigo.gestaovendas.ui.cliente;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.rodrigo.gestaovendas.app.ClienteService;
import com.rodrigo.gestaovendas.domain.models.Cliente;

public class ClienteForm extends JFrame {
    private static final long serialVersionUID = 1L;
	private ClienteService clienteService;
    private ClienteTableModel tableModel;
    private JTable table;
    private JTextField searchField;
    private JButton btnNovo, btnExcluir, btnEditar;

    public ClienteForm(ClienteService clienteService) {
        this.clienteService = clienteService;
        initUI();
    }

    private void initUI() {
        setTitle("Consulta de Clientes");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel panelTop = new JPanel(new BorderLayout());
        searchField = new JTextField();
        panelTop.add(new JLabel("🔍 Buscar Cliente:"), BorderLayout.WEST);
        panelTop.add(searchField, BorderLayout.CENTER);

        tableModel = new ClienteTableModel(clienteService.buscarTodosClientes());
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel panelBottom = new JPanel();
        btnNovo = new JButton("Novo Cliente");
        btnEditar = new JButton("Alterar Cliente");
        btnExcluir = new JButton("Excluir");
        panelBottom.add(btnNovo);
        panelBottom.add(btnEditar);
        panelBottom.add(btnExcluir);

        add(panelTop, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelBottom, BorderLayout.SOUTH);

        // 🔍 Busca dinâmica ao digitar
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filtrarClientes(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filtrarClientes(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filtrarClientes(); }
        });

        btnNovo.addActionListener(e -> new ClienteCadastroView(clienteService, null, this::atualizarTabela));


        btnEditar.addActionListener(e -> editarCliente());

//        btnEditar.addActionListener(e -> {
//            Integer idCliente = getClienteSelecionado();
//            if (idCliente == null) return;
//
//            Cliente cliente = clienteService.buscarClientePorId(idCliente);
//            new ClienteCadastroView(clienteService, cliente, this::atualizarTabela);
//        });

        
        
        // 🗑️ Evento para excluir cliente selecionado
        btnExcluir.addActionListener(e -> excluirCliente());

        setVisible(true);
    }

    private void filtrarClientes() {
        String termo = searchField.getText().trim();
        List<Cliente> clientesFiltrados = clienteService.buscarPorNome(termo);
        tableModel.atualizarDados(clientesFiltrados);
    }

    private void excluirCliente() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente para excluir!");
            return;
        }
        int idCliente = (int) table.getValueAt(row, 0);
        clienteService.excluirCliente(idCliente);
        filtrarClientes();
    }
    
    private void editarCliente() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente para editar!");
            return;
        }

        int idCliente = (int) table.getValueAt(row, 0);
        Cliente cliente = clienteService.buscarClientePorId(idCliente);

        // Passa o callback para atualizar a tabela após a edição
        new ClienteCadastroView(clienteService, cliente, this::atualizarTabela);
    }

    
    private void atualizarTabela() {
        List<Cliente> clientesAtualizados = clienteService.buscarTodosClientes();
        tableModel.atualizarDados(clientesAtualizados);
    }
    
//    private Integer getClienteSelecionado() {
//        int row = table.getSelectedRow(); // Obtém o índice da linha selecionada
//        if (row == -1) { // Verifica se nenhuma linha está selecionada
//            JOptionPane.showMessageDialog(this, "Selecione um cliente!");
//            return null;
//        }
//        // Supondo que a coluna 0 da tabela seja o ID do cliente
//        return (Integer) table.getValueAt(row, 0); 
//    }


}

