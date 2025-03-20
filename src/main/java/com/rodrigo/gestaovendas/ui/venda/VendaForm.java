package com.rodrigo.gestaovendas.ui.venda;

import com.rodrigo.gestaovendas.app.ClienteService;
import com.rodrigo.gestaovendas.app.ProdutoService;
import com.rodrigo.gestaovendas.app.VendaService;
import com.rodrigo.gestaovendas.domain.models.Cliente;
import com.rodrigo.gestaovendas.domain.models.Produto;
import com.rodrigo.gestaovendas.domain.models.Venda;
import com.rodrigo.gestaovendas.utils.VendaCadastroViewBKP_CODE;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class VendaForm extends JFrame {
    private static final long serialVersionUID = 1L;

    private VendaService vendaService;

    private JTable tabelaVendas;
    private DefaultTableModel modeloTabela;
    private JTextField campoBuscaCliente, campoBuscaProduto;
    private JButton btnNovaVenda, btnEditar, btnExcluir, btnFiltrar;

	private ClienteService clienteService;

	private ProdutoService produtoService;

    public VendaForm(VendaService vendaService, ClienteService clienteService, ProdutoService produtoService) {
        this.vendaService = vendaService;
        this.clienteService = clienteService;
        this.produtoService = produtoService;
        initUI();
    }

    private void initUI() {
        setTitle("Consulta de Vendas");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Painel Superior - Busca e Filtros
        JPanel painelBusca = new JPanel(new GridLayout(2, 3, 5, 5));
        campoBuscaCliente = new JTextField();
        campoBuscaProduto = new JTextField();
        JTextField campoDataInicio = new JTextField();
        JTextField campoDataFim = new JTextField();

        painelBusca.add(new JLabel("Cliente:"));
        painelBusca.add(campoBuscaCliente);
        painelBusca.add(new JLabel("Produto:"));
        painelBusca.add(campoBuscaProduto);
        painelBusca.add(new JLabel("Data Início:"));
        painelBusca.add(campoDataInicio);
        painelBusca.add(new JLabel("Data Fim:"));
        painelBusca.add(campoDataFim);

        btnFiltrar = new JButton("Filtrar");
        painelBusca.add(btnFiltrar);

        // Tabela de Vendas
        String[] colunas = {"Código", "Cliente", "Data", "Valor Total"};
        modeloTabela = new DefaultTableModel(colunas, 0);
        tabelaVendas = new JTable(modeloTabela);
        JScrollPane scrollTabela = new JScrollPane(tabelaVendas);

        // Painel Inferior - Botões
        JPanel painelBotoes = new JPanel();
        btnNovaVenda = new JButton("Nova Venda");
        btnEditar = new JButton("Editar");
        btnExcluir = new JButton("Excluir");

        btnEditar.setEnabled(false);
        btnExcluir.setEnabled(false);

        painelBotoes.add(btnNovaVenda);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);

        add(painelBusca, BorderLayout.NORTH);
        add(scrollTabela, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);

        // Listeners de Eventos
        btnNovaVenda.addActionListener(e -> novaVenda());
        btnEditar.addActionListener(e -> editarVenda());
        btnExcluir.addActionListener(e -> excluirVenda());
        btnFiltrar.addActionListener(e -> filtrarVendas(campoBuscaCliente.getText(),
                                                        campoBuscaProduto.getText(),
                                                        campoDataInicio.getText(),
                                                        campoDataFim.getText()));

        tabelaVendas.getSelectionModel().addListSelectionListener(e -> {
            boolean selecionado = tabelaVendas.getSelectedRow() != -1;
            btnEditar.setEnabled(selecionado);
            btnExcluir.setEnabled(selecionado);
        });

//        atualizarTabela(vendaService.listarTodos());
        atualizarTabela();

        setVisible(true);
    }

    public void atualizarTabela() {
        List<Venda> vendasAtualizadas = vendaService.listarTodos();
        modeloTabela.setRowCount(0); // Limpa a tabela
        for (Venda venda : vendasAtualizadas) {
            modeloTabela.addRow(new Object[]{
                venda.getCodigo(),
                venda.getCliente().getNome(),
                venda.getData(),
                venda.getValorTotal()
            });
        }
    }


    private void novaVenda() {
		new VendaCadastroViewBKP_CODE(vendaService, clienteService, produtoService, null, this::atualizarTabela); // Cadastro de nova venda
    }


    private void editarVenda() {
        int linhaSelecionada = tabelaVendas.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma venda para editar!");
            return;
        }
        int codigoVenda = (int) tabelaVendas.getValueAt(linhaSelecionada, 0);
        Venda venda = vendaService.consultar(codigoVenda);
        new VendaCadastroViewBKP_CODE(vendaService, clienteService, produtoService, null, this::atualizarTabela); // Edição da venda
    }

    private void excluirVenda() {
        int linhaSelecionada = tabelaVendas.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma venda para excluir!");
            return;
        }
        int codigoVenda = (int) tabelaVendas.getValueAt(linhaSelecionada, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Deseja excluir esta venda?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            vendaService.excluir(codigoVenda);
            atualizarTabela();
        }
    }

    private void filtrarVendas(String cliente, String produto, String dataInicio, String dataFim) {
        // Implemente o método no serviço para realizar os filtros
        LocalDate inicio = dataInicio.isEmpty() ? null : LocalDate.parse(dataInicio);
        LocalDate fim = dataFim.isEmpty() ? null : LocalDate.parse(dataFim);
        List<Venda> vendasFiltradas = vendaService.buscarPorFiltros(cliente, produto, inicio, fim);
        //atualizarTabela(vendasFiltradas);
    }
}
