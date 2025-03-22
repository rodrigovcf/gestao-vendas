package com.rodrigo.gestaovendas.ui.venda;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
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
import javax.swing.table.DefaultTableModel;

import com.rodrigo.gestaovendas.app.ClienteService;
import com.rodrigo.gestaovendas.app.ProdutoService;
import com.rodrigo.gestaovendas.app.VendaService;
import com.rodrigo.gestaovendas.domain.models.Cliente;
import com.rodrigo.gestaovendas.domain.models.ItemVenda;
import com.rodrigo.gestaovendas.domain.models.Produto;
import com.rodrigo.gestaovendas.domain.models.Venda;
import com.rodrigo.gestaovendas.domain.repositories.ClienteRepository;
import com.rodrigo.gestaovendas.domain.repositories.ProdutoRepository;
import com.rodrigo.gestaovendas.domain.repositories.VendaRepository;
import com.rodrigo.gestaovendas.infra.ClienteDAO;
import com.rodrigo.gestaovendas.infra.ProdutoDAO;
import com.rodrigo.gestaovendas.infra.VendaDAO;
import com.rodrigo.gestaovendas.utils.ValidacaoUtil;

public class VendaCadastroView extends JFrame {
    private static final long serialVersionUID = 1L;
    
 // Declaração global dos campos
    private JTextField txtNomeCliente;
    private JTextField txtLimiteCompra;
    private JTextField txtFechamentoFatura;
    private JTextField txtBuscaCodigo;
    private JTextField txtDescricao;
    private JTextField txtPreco;
    private JTextField txtQtd;
    private JTextField txtTotalVenda;
    private JTable tabelaCarrinho;

	private JButton btnAlterarQuantidade;

	private AbstractButton btnExcluirProduto;

	private static double subtotal = 0.0;

	private VendaRepository vendaRepository;
    private Venda venda; // Armazena a venda para edição, se necessário

    public VendaCadastroView(VendaRepository vendaRepository, Venda venda) {
        this.vendaRepository = vendaRepository;
        this.venda = venda;

        inicializarComponentes();

        // Se a venda não for nula, carregue os dados para edição
        if (venda != null) {
            carregarDadosParaEdicao();
        }
    }
    
    private void inicializarComponentes() {
        // Configuração dos componentes da interface (labels, inputs, tabelas, etc.)
    }

    private void carregarDadosParaEdicao() {
        // Exemplo de preenchimento de campos de texto e tabela com os dados da venda
        txtNomeCliente.setText(venda.getCliente().getNome());
        DefaultTableModel modelo = (DefaultTableModel) tabelaCarrinho.getModel();
        modelo.setRowCount(0); // Limpa os dados existentes

        for (ItemVenda item : venda.getItens()) {
            modelo.addRow(new Object[]{
                item.getCodigoProduto(),
                item.getProduto().getDescricao(),
                item.getQuantidade(),
                item.getPrecoUnitario()
            });
        }

        txtTotalVenda.setText(String.format("%.2f", venda.getValorTotal()));
    }

    public VendaCadastroView() {
        setTitle("PDV");
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
        txtNomeCliente = new JTextField(15);
        JButton btnPesquisarCliente = new JButton("Pesquisar");
        txtLimiteCompra = new JTextField(5);
        txtFechamentoFatura = new JTextField(8);

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
        txtBuscaCodigo = new JTextField(10);
        JButton btnPesquisarProduto = new JButton("Pesquisar");
        txtDescricao = new JTextField(25);
        txtPreco = new JTextField(5);
        txtQtd = new JTextField(5);
        JButton btnAdicionarItem = new JButton("Adicionar Item");
        btnAlterarQuantidade = new JButton("Alterar Quantidade");
        btnExcluirProduto = new JButton("Excluir Item");

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
        gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 1;
        painelProduto.add(btnAdicionarItem, gbc);
        
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 2; gbc.gridy = 3; gbc.gridwidth = 1;
        painelProduto.add(btnAlterarQuantidade, gbc);
        
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1; gbc.gridy = 4; gbc.gridwidth = 1;
        painelProduto.add(btnExcluirProduto, gbc);

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
        DefaultTableModel model = new DefaultTableModel(colunas, 0); // Define colunas e inicializa com 0 linhas
        tabelaCarrinho = new JTable(model);
        
        
        JScrollPane scrollPane = new JScrollPane(tabelaCarrinho);
        painelCarrinho.add(scrollPane, BorderLayout.CENTER);

        // Painel Total com Salvar e Cancelar
        JPanel painelTotal = new JPanel(new GridBagLayout());
        painelTotal.setBorder(BorderFactory.createTitledBorder("Total da Venda"));
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 0; gbc.gridy = 0;
        painelTotal.add(new JLabel("TOTAL: "), gbc);
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 3;
        txtTotalVenda = new JTextField(10);
        painelTotal.add(txtTotalVenda, gbc);

        // Adicionando botões
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; gbc.gridy = 1;
        JButton btnSalvar = new JButton("Salvar");
        painelTotal.add(btnSalvar, gbc);
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 3;
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
            try {
                // Obtendo o nome digitado pelo usuário
                String termoBusca = txtNomeCliente.getText();
                if (termoBusca.isEmpty()) {
                    JOptionPane.showMessageDialog(VendaCadastroView.this,
                            "Por favor, digite um termo para busca!",
                            "Aviso",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Busca clientes que correspondam ao termo
                List<Cliente> clientesEncontrados = clienteService.buscarPorNome(termoBusca);

                if (clientesEncontrados.isEmpty()) {
                    JOptionPane.showMessageDialog(VendaCadastroView.this,
                            "Nenhum cliente encontrado para o termo informado!",
                            "Aviso",
                            JOptionPane.INFORMATION_MESSAGE);
                    txtNomeCliente.setText(""); // Limpa o campo, já que nada foi encontrado
                } else if (clientesEncontrados.size() == 1) {
                    // Um único cliente encontrado: preenche o campo com o nome completo
                    Cliente cliente = clientesEncontrados.get(0);
                    txtNomeCliente.setText(cliente.getNome());
                    txtLimiteCompra.setText(String.valueOf(cliente.getLimiteCompra()));
                    txtFechamentoFatura.setText(String.valueOf(cliente.getDiaFechamentoFatura()));
                } else {
                    // Vários clientes encontrados: exibe o diálogo para seleção
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
        
        VendaRepository vendaRepository = new VendaDAO();
        VendaService vendaService = new VendaService(vendaRepository, clienteRepository, produtoRepository);

        
        btnSalvar.addActionListener(e -> {
            try {
                String nomeCliente = txtNomeCliente.getText();
                if (nomeCliente.isEmpty()) {
                    JOptionPane.showMessageDialog(VendaCadastroView.this,
                            "Selecione um cliente antes de cadastrar a venda!",
                            "Aviso",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Cliente cliente = clienteService.buscarUmPorNome(nomeCliente);
                if (cliente == null) {
                    JOptionPane.showMessageDialog(VendaCadastroView.this,
                            "Cliente inválido. Verifique os dados!",
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int rowCount = tabelaCarrinho.getRowCount();
                if (rowCount == 0) {
                    JOptionPane.showMessageDialog(VendaCadastroView.this,
                            "Adicione ao menos um produto ao carrinho antes de cadastrar a venda!",
                            "Aviso",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                double valorTotal = 0.0;
                Map<Integer, Integer> produtosQuantidade = new HashMap<>();

                for (int i = 0; i < rowCount; i++) {
                    int codigoProduto = Integer.parseInt(tabelaCarrinho.getValueAt(i, 0).toString());
                    int quantidade = Integer.parseInt(tabelaCarrinho.getValueAt(i, 2).toString());
                    double preco = Double.parseDouble(tabelaCarrinho.getValueAt(i, 3).toString());

                    produtosQuantidade.put(codigoProduto, quantidade);
                    valorTotal += quantidade * preco;
                }

                // Validar limite de crédito com o valor total da compra
                vendaService.verificarLimiteCredito(cliente.getCodigo(), valorTotal);

                vendaService.registrarVenda(cliente.getCodigo(), produtosQuantidade);

                JOptionPane.showMessageDialog(VendaCadastroView.this,
                        "Venda cadastrada com sucesso!",
                        "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);

               limparCamposVenda();

            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(VendaCadastroView.this,
                        ex.getMessage(),
                        "Limite Excedido",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(VendaCadastroView.this,
                        "Erro ao cadastrar venda: " + ex.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        });


        
        btnAdicionarItem.addActionListener(e -> {
            try {
                // Valida se os campos de produto foram preenchidos
                String codigoProduto = txtBuscaCodigo.getText();
                String descricaoProduto = txtDescricao.getText();
                String precoProduto = txtPreco.getText();
                String quantidadeProduto = txtQtd.getText();

                if (codigoProduto.isEmpty() || descricaoProduto.isEmpty() || precoProduto.isEmpty() || quantidadeProduto.isEmpty()) {
                    JOptionPane.showMessageDialog(VendaCadastroView.this,
                            "Por favor, preencha todos os campos do produto!",
                            "Aviso",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                
                DefaultTableModel tabelaModelo = (DefaultTableModel) tabelaCarrinho.getModel();
                int indiceProdutoDuplicado = ValidacaoUtil.verificarProdutoDuplicado(tabelaModelo, codigoProduto);

                if (indiceProdutoDuplicado != -1) {
                    // Produto já existe no carrinho
                    int opcao = JOptionPane.showConfirmDialog(VendaCadastroView.this,
                            "Este produto já está no carrinho. Deseja atualizar a quantidade?",
                            "Produto Duplicado",
                            JOptionPane.YES_NO_OPTION);

                    if (opcao == JOptionPane.YES_OPTION) {
                        // Atualiza a quantidade do produto existente
                        int quantidadeAdicional = Integer.parseInt(quantidadeProduto);
                        ValidacaoUtil.atualizarQuantidadeProduto(tabelaModelo, indiceProdutoDuplicado, quantidadeAdicional);
                    }

                    return; // Não adiciona o produto novamente
                }
                
                // Valida se a quantidade é válida
                int quantidade;
                try {
                    quantidade = Integer.parseInt(quantidadeProduto);
                    if (quantidade <= 0) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(VendaCadastroView.this,
                            "Por favor, insira uma quantidade válida (número inteiro maior que 0)!",
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Caso o produto não esteja no carrinho, adiciona-o
                double preco = Double.parseDouble(precoProduto);
                quantidade = Integer.parseInt(quantidadeProduto);
                subtotal = quantidade * preco;
                
                // Valida se o preço é válido
                try {
                    preco = Double.parseDouble(precoProduto);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(VendaCadastroView.this,
                            "Por favor, insira um preço válido!",
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Adiciona o item ao JTable (Carrinho)
                tabelaModelo.addRow(new Object[]{codigoProduto, descricaoProduto, quantidade, preco, subtotal});

                // Limpa os campos de produto após adicionar ao carrinho
                limpaCamposProdutos();

                // Exibe mensagem de sucesso
                JOptionPane.showMessageDialog(VendaCadastroView.this,
                        "Produto adicionado ao carrinho com sucesso!",
                        "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
                
                atualizarTotalVenda();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(VendaCadastroView.this,
                        "Erro ao adicionar item ao carrinho: " + ex.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        

        btnAlterarQuantidade.addActionListener(e -> {
            try {
                int linhaSelecionada = tabelaCarrinho.getSelectedRow();
                if (linhaSelecionada == -1) {
                    JOptionPane.showMessageDialog(VendaCadastroView.this,
                            "Por favor, selecione um produto para alterar a quantidade!",
                            "Aviso",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String novaQuantidadeStr = JOptionPane.showInputDialog(VendaCadastroView.this,
                        "Digite a nova quantidade:",
                        "Alterar Quantidade",
                        JOptionPane.QUESTION_MESSAGE);

                if (novaQuantidadeStr == null || novaQuantidadeStr.isEmpty()) {
                    JOptionPane.showMessageDialog(VendaCadastroView.this,
                            "Nenhuma quantidade foi informada.",
                            "Aviso",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int novaQuantidade = Integer.parseInt(novaQuantidadeStr);
                if (novaQuantidade <= 0) {
                    throw new NumberFormatException();
                }

                DefaultTableModel tabelaModelo = (DefaultTableModel) tabelaCarrinho.getModel();
                double precoUnitario = Double.parseDouble(tabelaModelo.getValueAt(linhaSelecionada, 3).toString());
                double novoSubtotal = novaQuantidade * precoUnitario;

                tabelaModelo.setValueAt(novaQuantidade, linhaSelecionada, 2); // Atualiza a quantidade
                tabelaModelo.setValueAt(novoSubtotal, linhaSelecionada, 4); // Atualiza o subtotal

                // Atualiza o total da venda
                atualizarTotalVenda();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(VendaCadastroView.this,
                        "Por favor, insira uma quantidade válida.",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        
        btnExcluirProduto.addActionListener(e -> {
            int linhaSelecionada = tabelaCarrinho.getSelectedRow();
            if (linhaSelecionada == -1) {
                JOptionPane.showMessageDialog(VendaCadastroView.this,
                        "Por favor, selecione um produto para excluir!",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirmacao = JOptionPane.showConfirmDialog(VendaCadastroView.this,
                    "Tem certeza de que deseja excluir este produto?",
                    "Excluir Produto",
                    JOptionPane.YES_NO_OPTION);

            if (confirmacao == JOptionPane.YES_OPTION) {
                try {
                    DefaultTableModel modeloTabela = (DefaultTableModel) tabelaCarrinho.getModel();
                    modeloTabela.removeRow(linhaSelecionada); // Remove a linha do produto selecionado

                    // Recalcula e atualiza o total da venda
                    atualizarTotalVenda();

                    JOptionPane.showMessageDialog(VendaCadastroView.this,
                            "Produto excluído com sucesso!",
                            "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(VendaCadastroView.this,
                            "Erro ao excluir o produto: " + ex.getMessage(),
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });



        btnCancelar.addActionListener(e -> dispose());

        setVisible(true);
        txtTotalVenda.setEditable(false);

    }
    
    
    private void limpaCamposProdutos() {
    	txtBuscaCodigo.setText("");
        txtDescricao.setText("");
        txtPreco.setText("");
        txtQtd.setText("");
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

    private void limparCamposVenda() {
        // Limpa os campos de texto relacionados ao cliente e produto
        txtNomeCliente.setText("");
        txtLimiteCompra.setText("");
        txtFechamentoFatura.setText("");
        txtBuscaCodigo.setText("");
        txtDescricao.setText("");
        txtPreco.setText("");
        txtQtd.setText("");
        txtTotalVenda.setText("");


        // Limpa os itens do carrinho (tabela)
        ((DefaultTableModel) tabelaCarrinho.getModel()).setRowCount(0); // Remove todas as linhas da tabela
    }
    
    private void atualizarTotalVenda() {
        double totalVenda = 0.0;

        // Percorre a tabela e soma os subtotais de todos os produtos
        DefaultTableModel modeloTabela = (DefaultTableModel) tabelaCarrinho.getModel();
        for (int i = 0; i < modeloTabela.getRowCount(); i++) {
            double subtotal = Double.parseDouble(modeloTabela.getValueAt(i, 4).toString());
            totalVenda += subtotal;
        }

        // Atualiza o campo txtTotalVenda com o total formatado
        txtTotalVenda.setText(String.format("%.2f", totalVenda));
    }


	public static void main(String[] args) {
        SwingUtilities.invokeLater(VendaCadastroView::new);
    }
}
