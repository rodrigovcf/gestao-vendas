package com.rodrigo.gestaovendas.ui.produto;

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

import com.rodrigo.gestaovendas.app.ProdutoService;
import com.rodrigo.gestaovendas.domain.models.Produto;

public class ProdutoForm extends JFrame {
    private static final long serialVersionUID = 1L;
    private ProdutoService produtoService;
    private ProdutoTableModel tableModel;
    private JTable table;
    private JTextField searchField;
    private JButton btnNovo, btnExcluir, btnEditar;

    public ProdutoForm(ProdutoService produtoService) {
        this.produtoService = produtoService;
        initUI();
    }

    private void initUI() {
        setTitle("Consulta de Produtos");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Painel superior para busca
        JPanel panelTop = new JPanel(new BorderLayout());
        searchField = new JTextField();
        panelTop.add(new JLabel("🔍 Buscar Produto:"), BorderLayout.WEST);
        panelTop.add(searchField, BorderLayout.CENTER);

        // Tabela de produtos
        tableModel = new ProdutoTableModel(produtoService.buscarTodosProdutos());
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        table.setAutoCreateRowSorter(true); //Ordena tabela direto da interface

        // Painel inferior com botões
        JPanel panelBottom = new JPanel();
        btnNovo = new JButton("Novo Produto");
        btnEditar = new JButton("Alterar Produto");
        btnExcluir = new JButton("Excluir");
        btnEditar.setEnabled(false);
        btnExcluir.setEnabled(false);
        panelBottom.add(btnNovo);
        panelBottom.add(btnEditar);
        panelBottom.add(btnExcluir);

        add(panelTop, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelBottom, BorderLayout.SOUTH);

        // Busca dinâmica ao digitar
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filtrarProdutos(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filtrarProdutos(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filtrarProdutos(); }
        });

        // Listener para habilitar/desabilitar botões
        table.getSelectionModel().addListSelectionListener(e -> {
            boolean selecionado = table.getSelectedRow() != -1;
            btnEditar.setEnabled(selecionado);
            btnExcluir.setEnabled(selecionado);
        });

        // Botões
        btnNovo.addActionListener(e -> new ProdutoCadastroView(produtoService, null, this::atualizarTabela));
        btnEditar.addActionListener(e -> editarProduto());
        btnExcluir.addActionListener(e -> excluirProduto());

        setVisible(true);
    }

    private void filtrarProdutos() {
        String descricao = searchField.getText().trim();
        List<Produto> produtosFiltrados = produtoService.buscarPorDescricao(descricao);
        tableModel.atualizarDados(produtosFiltrados);
    }

    private Integer getProdutoSelecionado() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto!");
            return null;
        }
        return (Integer) table.getValueAt(row, 0); // Supondo que a coluna 0 seja o código do produto
    }

    private void excluirProduto() {
        Integer idProduto = getProdutoSelecionado();
        if (idProduto == null) return;

        if (produtoService.estaVinculadoAVenda(idProduto)) {
            JOptionPane.showMessageDialog(this, "Não é possível excluir este produto, pois ele está vinculado a uma venda.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir este produto?",
                "Confirmação", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            produtoService.excluirProduto(idProduto);
            atualizarTabela();
        }
    }

    private void editarProduto() {
    	int row = table.getSelectedRow();
    	if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto para editar!");
            return;
        }
    	 
    	int idProduto = (int) table.getValueAt(row, 0);
        Produto produto = produtoService.buscarProdutoPorCodigo(idProduto);
        
        new ProdutoCadastroView(produtoService, produto, this::atualizarTabela);
    }

    private void atualizarTabela() {
        List<Produto> produtosAtualizados = produtoService.buscarTodosProdutos();
        tableModel.atualizarDados(produtosAtualizados);
    }
}
