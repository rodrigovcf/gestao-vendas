package com.rodrigo.gestaovendas.ui.venda;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private JButton btnPesquisarCliente;
	private JButton btnPesquisarProduto;
	private JButton btnAdicionarItem;
	private JButton btnExcluirProduto;
	private JButton btnSalvar;
	private JButton btnCancelar;

	//private AbstractButton btnExcluirProduto;

	private static double subtotal = 0.0;

	private Venda venda; // Armazena a venda para edição, se necessário

	private VendaRepository vendaRepository;
	private VendaService vendaService;

	private void configurarJanela() {
	    setTitle("PDV");
	    setSize(950, 500);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    setLocationRelativeTo(null);
	    setLayout(new BorderLayout());
	    setVisible(true);
	}

	public VendaCadastroView() {
	    configurarJanela();
	    inicializarComponentes();
	}

	private boolean modoEdicao; // True se for uma edição, false se for cadastro

	private ProdutoService produtoService;

	private ProdutoDAO produtoRepository;

	public VendaCadastroView(VendaRepository vendaRepository, Venda venda) {
	    this.vendaRepository = vendaRepository;
	    this.venda = venda;
	    this.modoEdicao = (venda != null); // Se a venda não for nula, estamos editando

	    configurarJanela();
	    inicializarComponentes();

	    // Se a venda não for nula, carregar os dados para edição
	    if (modoEdicao) {
	        carregarDadosParaEdicao();
	    }
	}

    
    private void inicializarComponentes() {
    	 adicionarHeader();
         adicionarPainelPrincipal();
         configurarEventos();
    }

    private void carregarDadosParaEdicao() {
        if (venda == null) {
            throw new RuntimeException("A venda não foi carregada corretamente!");
        }

        if (venda.getCliente() != null) {
            txtNomeCliente.setText(venda.getCliente().getNome()); // Nome do cliente
            txtLimiteCompra.setText(String.valueOf(venda.getCliente().getLimiteCompra()));
            txtFechamentoFatura.setText(String.valueOf(venda.getCliente().getDiaFechamentoFatura()));
            
        } else {
            txtNomeCliente.setText("Cliente não encontrado");
        }

        DefaultTableModel modelo = (DefaultTableModel) tabelaCarrinho.getModel();
        modelo.setRowCount(0); // Limpa os dados existentes

        for (ItemVenda item : venda.getItens()) {
            if (item.getProduto() != null) {
                modelo.addRow(new Object[]{
                    item.getCodigoProduto(),
                    item.getProduto().getDescricao(),
                    item.getQuantidade(),
                    item.getPrecoUnitario(),
                    item.getQuantidade() * item.getPrecoUnitario() // Subtotal
                });
            } else {
                System.err.println("Produto não encontrado para o código: " + item.getCodigoProduto());
                modelo.addRow(new Object[]{
                    item.getCodigoProduto(),
                    "Produto não encontrado",
                    item.getQuantidade(),
                    item.getPrecoUnitario(),
                    item.getQuantidade() * item.getPrecoUnitario()
                });
            }
        }

        txtTotalVenda.setText(String.format("%.2f", venda.getValorTotal()));
    }


    private void adicionarHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(255, 153, 102));
        headerPanel.setPreferredSize(new Dimension(0, 80));

        JLabel headerLabel = new JLabel("INCLUIR VENDA", SwingConstants.LEFT);
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        headerPanel.add(headerLabel, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);
    }

    private void adicionarPainelPrincipal() {
        JPanel painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.add(adicionarPainelEsquerdo(), BorderLayout.WEST);
        painelPrincipal.add(adicionarPainelDireito(), BorderLayout.CENTER);
        add(painelPrincipal, BorderLayout.CENTER);
    }

    private JPanel adicionarPainelEsquerdo() {
        JPanel painelEsquerdo = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        painelEsquerdo.add(adicionarPainelCliente(), gbc);
        gbc.gridy = 1;
        painelEsquerdo.add(adicionarPainelProduto(), gbc);
        
        return painelEsquerdo;
    }

    private JPanel adicionarPainelCliente() {
        JPanel painelCliente = new JPanel(new GridBagLayout());
        painelCliente.setBorder(BorderFactory.createTitledBorder("Dados do Cliente"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        txtNomeCliente = new JTextField(15);
        btnPesquisarCliente = new JButton("Pesquisar");
        txtLimiteCompra = new JTextField(5);
        txtFechamentoFatura = new JTextField(8);
        
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 0; gbc.gridy = 0;
        painelCliente.add(new JLabel("Nome:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1;
        painelCliente.add(txtNomeCliente, gbc);
        gbc.gridx = 2;
        painelCliente.add(btnPesquisarCliente, gbc);
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 0; gbc.gridy = 1;
        painelCliente.add(new JLabel("Limite Compra:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1;
        painelCliente.add(txtLimiteCompra, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        painelCliente.add(new JLabel("Fechamento Fatura:"), gbc);
        gbc.gridx = 1;
        painelCliente.add(txtFechamentoFatura, gbc);
        
        return painelCliente;
    }

    private JPanel adicionarPainelProduto() {
        JPanel painelProduto = new JPanel(new GridBagLayout());
        painelProduto.setBorder(BorderFactory.createTitledBorder("Dados do Produto"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        txtBuscaCodigo = new JTextField(10);
        btnPesquisarProduto = new JButton("Pesquisar");
        txtDescricao = new JTextField(25);
        txtPreco = new JTextField(5);
        txtQtd = new JTextField(5);
        btnAdicionarItem = new JButton("Adicionar Item");
        btnAlterarQuantidade = new JButton("Alterar Quantidade");
        btnExcluirProduto = new JButton("Excluir Item");
        
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 0; gbc.gridy = 0;
        painelProduto.add(new JLabel("Busca por Código:"), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1;
        painelProduto.add(txtBuscaCodigo, gbc);
        gbc.gridx = 2;
        painelProduto.add(btnPesquisarProduto, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        painelProduto.add(new JLabel("Descrição:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        painelProduto.add(txtDescricao, gbc);
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 2;
        painelProduto.add(new JLabel("Preço:"), gbc);
        gbc.gridx = 1;
        painelProduto.add(txtPreco, gbc);
        gbc.gridx = 2;
        painelProduto.add(new JLabel("Qtd:"), gbc);
        gbc.gridx = 3;
        painelProduto.add(txtQtd, gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        painelProduto.add(btnAdicionarItem, gbc);
        gbc.gridx = 2;
        painelProduto.add(btnAlterarQuantidade, gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        painelProduto.add(btnExcluirProduto, gbc);
        
        return painelProduto;
    }

    private JPanel adicionarPainelDireito() {
        JPanel painelDireito = new JPanel(new BorderLayout());
        painelDireito.add(adicionarPainelCarrinho(), BorderLayout.CENTER);
        painelDireito.add(adicionarPainelTotal(), BorderLayout.SOUTH);
        return painelDireito;
    }

    private JPanel adicionarPainelCarrinho() {
        JPanel painelCarrinho = new JPanel(new BorderLayout());
        painelCarrinho.setBorder(BorderFactory.createTitledBorder("Carrinho de Compras"));
        String[] colunas = {"Código", "Descrição", "Qtd", "Preço", "Subtotal"};
        DefaultTableModel model = new DefaultTableModel(colunas, 0);
        tabelaCarrinho = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(tabelaCarrinho);
        painelCarrinho.add(scrollPane, BorderLayout.CENTER);
        return painelCarrinho;
    }

    private JPanel adicionarPainelTotal() {
        JPanel painelTotal = new JPanel(new GridBagLayout());
        painelTotal.setBorder(BorderFactory.createTitledBorder("Total da Venda"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 0; gbc.gridy = 0;
        painelTotal.add(new JLabel("TOTAL: "), gbc);
        gbc.gridx = 1;
        txtTotalVenda = new JTextField(10);
        painelTotal.add(txtTotalVenda, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        btnSalvar = new JButton("Salvar");
        painelTotal.add(btnSalvar, gbc);
        gbc.gridx = 1;
        btnCancelar = new JButton("Cancelar");
        painelTotal.add(btnCancelar, gbc);
        
        return painelTotal;
    }
    
    
    private void configurarEventos() {
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


      produtoRepository = new ProdutoDAO(); // Usa a implementação do DAO
      produtoService = new ProdutoService(produtoRepository);
      
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
      
      vendaRepository = new VendaDAO();
      vendaService = new VendaService(vendaRepository, clienteRepository, produtoRepository);

      
      btnSalvar.addActionListener(e -> {
    	    try {
    	        // Valida se o nome do cliente foi fornecido
    	        String nomeCliente = txtNomeCliente.getText();
    	        if (nomeCliente.isEmpty()) {
    	            JOptionPane.showMessageDialog(VendaCadastroView.this,
    	                    "Selecione um cliente antes de cadastrar ou alterar a venda!",
    	                    "Aviso",
    	                    JOptionPane.WARNING_MESSAGE);
    	            return;
    	        }

    	        // Busca o cliente pelo nome
    	        Cliente cliente = clienteService.buscarUmPorNome(nomeCliente);
    	        if (cliente == null) {
    	            JOptionPane.showMessageDialog(VendaCadastroView.this,
    	                    "Cliente inválido. Verifique os dados!",
    	                    "Erro",
    	                    JOptionPane.ERROR_MESSAGE);
    	            return;
    	        }

    	        // Verifica se há itens no carrinho
    	        int rowCount = tabelaCarrinho.getRowCount();
    	        if (rowCount == 0) {
    	            JOptionPane.showMessageDialog(VendaCadastroView.this,
    	                    "Adicione ao menos um produto ao carrinho antes de salvar a venda!",
    	                    "Aviso",
    	                    JOptionPane.WARNING_MESSAGE);
    	            return;
    	        }

    	        // Inicializa o valor total e o mapa de produtos
    	        double valorTotal = 0.0;
    	        Map<Integer, Integer> produtosQuantidade = new HashMap<>();

    	        // Percorre a tabela e monta os itens do carrinho
    	        for (int i = 0; i < rowCount; i++) {
    	            int codigoProduto = Integer.parseInt(tabelaCarrinho.getValueAt(i, 0).toString());
    	            int quantidade = Integer.parseInt(tabelaCarrinho.getValueAt(i, 2).toString());
    	            double preco = Double.parseDouble(tabelaCarrinho.getValueAt(i, 3).toString());

    	            produtosQuantidade.put(codigoProduto, quantidade);
    	            valorTotal += quantidade * preco; // Calcula o subtotal para o total geral
    	        }

    	        // Valida o limite de crédito com o valor total da venda
    	        vendaService.verificarLimiteCredito(cliente.getCodigo(), valorTotal);

    	        // Identifica se é uma edição ou um cadastro
    	        if (modoEdicao) {
    	            // Atualização de uma venda existente
    	            venda.setItens(convertToItensVenda(produtosQuantidade)); // Atualiza os itens da venda
    	            venda.setValorTotal(valorTotal); // Atualiza o valor total recalculado
    	            vendaService.alterar(venda); // Chama o serviço para atualizar a venda

    	            JOptionPane.showMessageDialog(VendaCadastroView.this,
    	                    "Venda alterada com sucesso!",
    	                    "Sucesso",
    	                    JOptionPane.INFORMATION_MESSAGE);
    	            carregarDadosParaEdicao();
    	        } else {
    	            // Cadastro de uma nova venda
    	            vendaService.registrarVenda(cliente.getCodigo(), produtosQuantidade);
    	            JOptionPane.showMessageDialog(VendaCadastroView.this,
    	                    "Venda cadastrada com sucesso!",
    	                    "Sucesso",
    	                    JOptionPane.INFORMATION_MESSAGE);
    	            //atualizarTabela();
    	        }

    	        limparCamposVenda(); // Limpa os campos após salvar
    	        dispose(); // Fecha a janela após salvar

    	    } catch (IllegalStateException ex) {
    	        JOptionPane.showMessageDialog(VendaCadastroView.this,
    	                ex.getMessage(),
    	                "Limite Excedido",
    	                JOptionPane.ERROR_MESSAGE);
    	    } catch (Exception ex) {
    	        JOptionPane.showMessageDialog(VendaCadastroView.this,
    	                "Erro ao salvar venda: " + ex.getMessage(),
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
    
    private void atualizarTabela() {
        DefaultTableModel modelo = (DefaultTableModel) tabelaCarrinho.getModel();
        modelo.setRowCount(0); // Limpa os dados existentes

        List<Venda> vendasAtualizadas = vendaService.buscarTodasVendas(); // Recupera todas as vendas

        for (Venda venda : vendasAtualizadas) {
            modelo.addRow(new Object[]{
                venda.getCodigo(), // Código da venda
                venda.getCliente().getNome(), // Nome do cliente
                venda.getItens().size(), // Número de itens na venda
                venda.getValorTotal() // Valor total da venda
            });
        }
    }


	private List<ItemVenda> convertToItensVenda(Map<Integer, Integer> produtosQuantidade) {
    List<ItemVenda> itensVenda = new ArrayList<>();
    for (Map.Entry<Integer, Integer> entry : produtosQuantidade.entrySet()) {
        Produto produto = produtoService.buscarProdutoPorCodigo(entry.getKey());
        if (produto != null) {
            ItemVenda item = new ItemVenda();
            item.setProduto(produto);
            item.setCodigoProduto(produto.getCodigo());
            item.setQuantidade(entry.getValue());
            item.setPrecoUnitario(produto.getPreco());
            itensVenda.add(item);
        }
    }
    return itensVenda;
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
	
	private void limpaCamposProdutos() {
    	txtBuscaCodigo.setText("");
        txtDescricao.setText("");
        txtPreco.setText("");
        txtQtd.setText("");
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