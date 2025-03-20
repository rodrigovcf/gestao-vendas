package com.rodrigo.gestaovendas.ui.venda;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.rodrigo.gestaovendas.app.ClienteService;
import com.rodrigo.gestaovendas.app.ProdutoService;
import com.rodrigo.gestaovendas.domain.models.Cliente;
import com.rodrigo.gestaovendas.domain.models.Produto;
import com.rodrigo.gestaovendas.domain.repositories.ClienteRepository;
import com.rodrigo.gestaovendas.domain.repositories.ProdutoRepository;
import com.rodrigo.gestaovendas.infra.ClienteDAO;
import com.rodrigo.gestaovendas.infra.ProdutoDAO;

public class VendaCadastroView extends JFrame {
    private static final long serialVersionUID = 1L;

    public VendaCadastroView() {
        setTitle("VENDAS");
        setSize(950, 500); // Tamanho da janela
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        setLocationRelativeTo(null);
        
        // Painel de cabeçalho
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(new Color(255, 153, 102));
        headerPanel.setPreferredSize(new Dimension(0, 80));
        
        JLabel headerLabel = new JLabel("INCLUIR VENDA", SwingConstants.LEFT);
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0)); // Adiciona margem esquerda
        headerPanel.add(headerLabel, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Painel Principal
        JPanel painelPrincipal = new JPanel(new BorderLayout());

        // Painel Esquerdo (Cliente e Produto)
        JPanel painelEsquerdo = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Painel Dados do Cliente
        JPanel painelCliente = new JPanel(new GridBagLayout());
        painelCliente.setBorder(BorderFactory.createTitledBorder("Dados do Cliente"));
        JTextField txtNomeCliente = new JTextField(15);
        JButton btnPesquisarCliente = new JButton("Pesquisar");
        JTextField txtLimiteCompra = new JTextField(5);
        JTextField txtFechamentoFatura = new JTextField(8);

        // Configurando os labels para alinhamento à direita
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 0; gbc.gridy = 0;
        painelCliente.add(new JLabel("Nome:"), gbc);
        gbc.anchor = GridBagConstraints.WEST; // Alinhamento à esquerda para os JTextFields
        gbc.gridx = 1;
        painelCliente.add(txtNomeCliente, gbc);
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 2;
        painelCliente.add(btnPesquisarCliente, gbc);
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 0; gbc.gridy = 1;
        painelCliente.add(new JLabel("Limite Compra:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1;
        painelCliente.add(txtLimiteCompra, gbc);
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 0; gbc.gridy = 2;
        painelCliente.add(new JLabel("Fechamento Fatura:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1;
        painelCliente.add(txtFechamentoFatura, gbc);

        // Painel Dados do Produto
        JPanel painelProduto = new JPanel(new GridBagLayout());
        painelProduto.setBorder(BorderFactory.createTitledBorder("Dados do Produto"));
        JTextField txtBuscaCodigo = new JTextField(10);
        JButton btnPesquisarProduto = new JButton("Pesquisar");
        JTextField txtDescricao = new JTextField(25);
        JTextField txtPreco = new JTextField(5);
        JTextField txtQtd = new JTextField(5);
        JButton btnAdicionarItem = new JButton("Adicionar Item");

        // Configurando os labels para alinhamento à direita
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 0; gbc.gridy = 0;
        painelProduto.add(new JLabel("Busca por Código:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1;
        painelProduto.add(txtBuscaCodigo, gbc);
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 2;
        painelProduto.add(btnPesquisarProduto, gbc);
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 0; gbc.gridy = 1;
        painelProduto.add(new JLabel("Descrição:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1; gbc.gridwidth = 2;
        painelProduto.add(txtDescricao, gbc);
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 2;
        painelProduto.add(new JLabel("Preço:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1;
        painelProduto.add(txtPreco, gbc);
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 1;
        painelProduto.add(new JLabel("Qtd:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 2;
        painelProduto.add(txtQtd, gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 2;
        painelProduto.add(btnAdicionarItem, gbc);

        // Adicionando Cliente e Produto ao painel esquerdo
        gbc.gridx = 0; gbc.gridy = 0;
        painelEsquerdo.add(painelCliente, gbc);
        gbc.gridy = 1;
        painelEsquerdo.add(painelProduto, gbc);

        // Painel Direito (Carrinho de Compras e Total da Venda)
        JPanel painelDireito = new JPanel(new BorderLayout());
        JPanel painelCarrinho = new JPanel(new BorderLayout());
        painelCarrinho.setBorder(BorderFactory.createTitledBorder("Carrinho de Compras"));
        String[] colunas = {"Código", "Descrição", "Qtd", "Preço", "Subtotal"};
        JTable tabelaCarrinho = new JTable(new Object[10][5], colunas);
        JScrollPane scrollPane = new JScrollPane(tabelaCarrinho);
        painelCarrinho.add(scrollPane, BorderLayout.CENTER);

        // Painel Total com Salvar e Cancelar
        JPanel painelTotal = new JPanel(new GridBagLayout());
        painelTotal.setBorder(BorderFactory.createTitledBorder("Total da Venda"));
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 0; gbc.gridy = 0;
        painelTotal.add(new JLabel("TOTAL: "), gbc);
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 2;
        JTextField txtTotalVenda = new JTextField(10);
        painelTotal.add(txtTotalVenda, gbc);

        // Adicionando botões
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; gbc.gridy = 1;
        JButton btnSalvar = new JButton("Salvar");
        painelTotal.add(btnSalvar, gbc);
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 2;
        JButton btnCancelar = new JButton("Cancelar");
        painelTotal.add(btnCancelar, gbc);

        // Adicionando Carrinho e Total ao painel direito
        painelDireito.add(painelCarrinho, BorderLayout.CENTER);
        painelDireito.add(painelTotal, BorderLayout.SOUTH);

        // Adicionando os painéis principais ao painelPrincipal
        painelPrincipal.add(painelEsquerdo, BorderLayout.WEST); // Alinhando Cliente e Produto à esquerda
        painelPrincipal.add(painelDireito, BorderLayout.EAST); // Alinhando Carrinho e Total à direita

        // Adicionando painelPrincipal ao JFrame
        add(painelPrincipal, BorderLayout.CENTER);
        
        ClienteRepository clienteRepository = new ClienteDAO(); // Usa a implementação do DAO
        ClienteService clienteService = new ClienteService(clienteRepository);

        btnPesquisarCliente.addActionListener(e -> {
            String termoBusca = txtNomeCliente.getText();

            if (termoBusca.isEmpty()) {
                JOptionPane.showMessageDialog(VendaCadastroView.this,
                        "Por favor, digite um termo para busca!",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                // Busca clientes pelo termo
                List<Cliente> clientesEncontrados = clienteService.buscarPorNome(termoBusca);

                if (clientesEncontrados.isEmpty()) {
                    JOptionPane.showMessageDialog(VendaCadastroView.this,
                            "Nenhum cliente encontrado para o termo informado!",
                            "Aviso",
                            JOptionPane.INFORMATION_MESSAGE);
                } else if (clientesEncontrados.size() == 1) {
                    // Apenas um cliente encontrado
                    Cliente cliente = clientesEncontrados.get(0);
                    txtLimiteCompra.setText(String.valueOf(cliente.getLimiteCompra()));
                    txtFechamentoFatura.setText(cliente.getDiaFechamentoFatura().toString());
                } else {
                    // Mais de um cliente encontrado, exibe o diálogo para seleção
                    exibirDialogoSelecaoClientes(clientesEncontrados, txtNomeCliente, txtLimiteCompra, txtFechamentoFatura);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(VendaCadastroView.this,
                        "Erro ao buscar clientes: " + ex.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        ProdutoRepository produtoRepository = new ProdutoDAO(); // Usa a implementação do DAO
        ProdutoService produtoService = new ProdutoService(produtoRepository);
        
        btnPesquisarProduto.addActionListener(e -> {
            int codigoProduto = Integer.parseInt(txtBuscaCodigo.getText());

            if (codigoProduto == 0) {
                JOptionPane.showMessageDialog(VendaCadastroView.this,
                        "Por favor, digite o código ou parte do código do produto!",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                // Busca produto pelo código
                Produto produtoEncontrado = produtoService.buscarProdutoPorCodigo(codigoProduto);

                if (produtoEncontrado != null) {
                    // Produto encontrado: Preenche os campos correspondentes
                    txtDescricao.setText(produtoEncontrado.getDescricao());
                    txtPreco.setText(String.valueOf(produtoEncontrado.getPreco()));
                } else {
                    // Produto não encontrado
                    JOptionPane.showMessageDialog(VendaCadastroView.this,
                            "Produto não encontrado para o código informado!",
                            "Aviso",
                            JOptionPane.INFORMATION_MESSAGE);
                    txtDescricao.setText("");
                    txtPreco.setText("");
                }
            } catch (Exception ex) {
                // Tratamento de erro
                JOptionPane.showMessageDialog(VendaCadastroView.this,
                        "Erro ao buscar o produto: " + ex.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        });


        setVisible(true);
    }
    
    
    private void exibirDialogoSelecaoClientes(List<Cliente> clientes, JTextField txtNomeCliente,
            JTextField txtLimiteCompra, JTextField txtFechamentoFatura) {
    	JDialog dialog = new JDialog(this, "Selecionar Cliente", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);

        // Configurando a tabela de clientes
        String[] colunas = {"Código", "Nome", "Limite de Compra", "Fechamento Fatura"};
        Object[][] dados = new Object[clientes.size()][4];

        for (int i = 0; i < clientes.size(); i++) {
            Cliente cliente = clientes.get(i);
            dados[i][0] = cliente.getCodigo();
            dados[i][1] = cliente.getNome();
            dados[i][2] = cliente.getLimiteCompra();
            dados[i][3] = cliente.getDiaFechamentoFatura();
        }

        JTable tabelaClientes = new JTable(dados, colunas);
        JScrollPane scrollPane = new JScrollPane(tabelaClientes);

        // Botão de Selecionar
        JButton btnSelecionar = new JButton("Selecionar");
        btnSelecionar.addActionListener(e -> {
            int linhaSelecionada = tabelaClientes.getSelectedRow();
            if (linhaSelecionada == -1) {
                JOptionPane.showMessageDialog(dialog,
                        "Por favor, selecione um cliente!",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Cliente clienteSelecionado = clientes.get(linhaSelecionada);
            txtNomeCliente.setText(clienteSelecionado.getNome());
            txtLimiteCompra.setText(String.valueOf(clienteSelecionado.getLimiteCompra()));
            txtFechamentoFatura.setText(clienteSelecionado.getDiaFechamentoFatura().toString());

            dialog.dispose(); // Fecha o diálogo
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(btnSelecionar, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
	}


	public static void main(String[] args) {
        SwingUtilities.invokeLater(VendaCadastroView::new);
    }
}
