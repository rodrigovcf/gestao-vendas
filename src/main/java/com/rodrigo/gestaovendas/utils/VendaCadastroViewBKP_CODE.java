package com.rodrigo.gestaovendas.utils;

import com.rodrigo.gestaovendas.app.ClienteService;
import com.rodrigo.gestaovendas.app.ProdutoService;
import com.rodrigo.gestaovendas.app.VendaService;
import com.rodrigo.gestaovendas.domain.models.Cliente;
import com.rodrigo.gestaovendas.domain.models.ItemVenda;
import com.rodrigo.gestaovendas.domain.models.Produto;
import com.rodrigo.gestaovendas.domain.models.Venda;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VendaCadastroViewBKP_CODE extends JFrame {
    private static final long serialVersionUID = 1L;

    private VendaService vendaService;
    private ClienteService clienteService;
    private ProdutoService produtoService;
    private Venda venda;
    private Runnable atualizarTabelaCallback;

    // Componentes da interface
    private JComboBox<Cliente> comboClientes;
    private JTextField campoLimiteCompra, campoFechamentoFatura;
    private JTextField campoCodigoProduto, campoDescricaoProduto, campoPrecoProduto, campoQuantidade;
    private JTable tabelaCarrinho;
    private DefaultTableModel modeloCarrinho;
    private JLabel labelTotalVenda;

    private List<ItemVenda> itensVenda = new ArrayList<>();

    public VendaCadastroViewBKP_CODE(VendaService vendaService, ClienteService clienteService, ProdutoService produtoService,
                             Venda vendaExistente, Runnable atualizarTabelaCallback) {
        this.vendaService = vendaService;
        this.clienteService = clienteService;
        this.produtoService = produtoService;
        this.venda = vendaExistente;
        this.atualizarTabelaCallback = atualizarTabelaCallback;

        initUI();
    }

    private void initUI() {
        setTitle(venda == null ? "Nova Venda" : "Editar Venda");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout(10, 10));

        // Painel Superior - Dados do Cliente
        JPanel painelCliente = new JPanel(new GridLayout(2, 3, 10, 10));
        comboClientes = new JComboBox<>(clienteService.buscarTodosClientes().toArray(new Cliente[0]));
        campoLimiteCompra = new JTextField();
        campoLimiteCompra.setEditable(false);
        campoFechamentoFatura = new JTextField();
        campoFechamentoFatura.setEditable(false);

        painelCliente.add(new JLabel("Nome do Cliente:"));
        painelCliente.add(comboClientes);
        painelCliente.add(new JButton("Pesquisar")); // Botão para buscar cliente
        painelCliente.add(new JLabel("Limite Compra:"));
        painelCliente.add(campoLimiteCompra);
        painelCliente.add(new JLabel("Fechamento Fatura:"));
        painelCliente.add(campoFechamentoFatura);

        add(painelCliente, BorderLayout.NORTH);

        // Painel Central - Dados do Produto e Carrinho
        JPanel painelCentral = new JPanel(new BorderLayout(10, 10));

        // Painel de entrada de dados do produto
        JPanel painelProduto = new JPanel(new GridLayout(2, 4, 10, 10));
        campoCodigoProduto = new JTextField();
        campoDescricaoProduto = new JTextField();
        campoDescricaoProduto.setEditable(false);
        campoPrecoProduto = new JTextField();
        campoPrecoProduto.setEditable(false);
        campoQuantidade = new JTextField("1");

        painelProduto.add(new JLabel("Código:"));
        painelProduto.add(campoCodigoProduto);
        painelProduto.add(new JLabel("Descrição:"));
        painelProduto.add(campoDescricaoProduto);
        painelProduto.add(new JLabel("Preço:"));
        painelProduto.add(campoPrecoProduto);
        painelProduto.add(new JLabel("Quantidade:"));
        painelProduto.add(campoQuantidade);

        JButton btnAdicionarProduto = new JButton("Adicionar Item");
        painelProduto.add(btnAdicionarProduto);

        painelCentral.add(painelProduto, BorderLayout.NORTH);

        // Tabela de carrinho de compras
        String[] colunasCarrinho = {"Código", "Descrição", "Qtd", "Preço", "Subtotal"};
        modeloCarrinho = new DefaultTableModel(colunasCarrinho, 0);
        tabelaCarrinho = new JTable(modeloCarrinho);
        JScrollPane scrollCarrinho = new JScrollPane(tabelaCarrinho);

        painelCentral.add(scrollCarrinho, BorderLayout.CENTER);

        add(painelCentral, BorderLayout.CENTER);

        // Painel Inferior - Total e Botões de Ação
        JPanel painelInferior = new JPanel(new BorderLayout(10, 10));
        JPanel painelTotal = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        labelTotalVenda = new JLabel("TOTAL DA VENDA: R$ 0,00");
        painelTotal.add(labelTotalVenda);
        painelInferior.add(painelTotal, BorderLayout.NORTH);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalvar = new JButton("Salvar Venda");
        JButton btnCancelar = new JButton("Cancelar");
        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnCancelar);
        painelInferior.add(painelBotoes, BorderLayout.SOUTH);

        add(painelInferior, BorderLayout.SOUTH);

        // Listeners
        comboClientes.addActionListener(e -> atualizarDadosCliente());
        btnAdicionarProduto.addActionListener(e -> adicionarProduto());
        btnSalvar.addActionListener(e -> salvarVenda());
        btnCancelar.addActionListener(e -> dispose());

        if (venda != null) {
            carregarVenda();
        }

        setVisible(true);
    }

    private void atualizarDadosCliente() {
        Cliente clienteSelecionado = (Cliente) comboClientes.getSelectedItem();
        if (clienteSelecionado != null) {
            campoLimiteCompra.setText(String.format("R$ %.2f", clienteSelecionado.getLimiteCompra()));
            campoFechamentoFatura.setText(clienteSelecionado.getDiaFechamentoFatura().toString());
        }
    }

    private void adicionarProduto() {
        int codigo;
        try {
            codigo = Integer.parseInt(campoCodigoProduto.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "O código do produto deve ser um número!");
            return;
        }

        Produto produto = produtoService.buscarProdutoPorCodigo(codigo);
        if (produto == null) {
            JOptionPane.showMessageDialog(this, "Produto com o código informado não foi encontrado!");
            return;
        }

        campoDescricaoProduto.setText(produto.getDescricao());
        campoPrecoProduto.setText(String.valueOf(produto.getPreco()));

        int quantidade;
        try {
            quantidade = Integer.parseInt(campoQuantidade.getText());
            if (quantidade <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quantidade inválida!");
            return;
        }

        double subtotal = produto.getPreco() * quantidade;
        //itensVenda.add(new ItemVenda(produto, quantidade, subtotal));

        modeloCarrinho.addRow(new Object[]{
                produto.getCodigo(),
                produto.getDescricao(),
                quantidade,
                produto.getPreco(),
                subtotal
        });

        atualizarTotal();
    }

    private void atualizarTotal() {
        double total = itensVenda.stream().mapToDouble(ItemVenda::getValorTotal).sum();
        labelTotalVenda.setText(String.format("TOTAL DA VENDA: R$ %.2f", total));
    }

    private void salvarVenda() {
        Cliente cliente = (Cliente) comboClientes.getSelectedItem();
        if (cliente == null) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente!");
            return;
        }
        if (itensVenda.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Adicione pelo menos um produto ao carrinho!");
            return;
        }

        if (venda == null) {
            venda = new Venda();
        }
        venda.setCliente(cliente);
        venda.setItens(itensVenda);
        venda.setData(LocalDate.now());
        venda.calcularValorTotal();

        if (venda.getCodigo() == 0) {
            vendaService.incluir(venda);
        } else {
            vendaService.alterar(venda);
        }

        atualizarTabelaCallback.run();
        dispose();
    }

    private void carregarVenda() {
        comboClientes.setSelectedItem(venda.getCliente());
        venda.getItens().forEach(item -> modeloCarrinho.addRow(new Object[]{
                item.getProduto().getCodigo(),
                item.getProduto().getDescricao(),
                item.getQuantidade(),
                item.getProduto().getPreco(),
                item.getValorTotal()
        }));
        atualizarTotal();
    }
    
   
}
