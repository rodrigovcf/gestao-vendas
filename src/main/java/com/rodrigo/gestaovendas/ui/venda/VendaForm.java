package com.rodrigo.gestaovendas.ui.venda;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.rodrigo.gestaovendas.domain.models.Venda;
import com.rodrigo.gestaovendas.domain.repositories.VendaDTO;
import com.rodrigo.gestaovendas.domain.repositories.VendaRepository;
import com.rodrigo.gestaovendas.infra.VendaDAO;

public class VendaForm extends JFrame {

    private static final long serialVersionUID = 1L;

    // Componentes principais
    private JTable tabelaVendas;
    private DefaultTableModel modeloTabela;
    private JTextField txtFiltroCliente;
    private JTextField txtFiltroProduto;
    private JTextField txtFiltroDataInicio;
    private JTextField txtFiltroDataFim;
    private JButton btnIncluir, btnExcluir, btnAlterar, btnFiltrar, btnSair;

    private VendaRepository vendaRepository;

    public VendaForm() {
        vendaRepository = new VendaDAO(); // Inicializa o repositório
        initUI();
        inicializarComponentes();
        carregarDadosTabela(); // Carrega os dados ao abrir
    }

    private void initUI() {
        setTitle("Gerenciamento de Vendas");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        // Barra de Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnIncluir = new JButton("Incluir");
        btnExcluir = new JButton("Excluir");
        btnAlterar = new JButton("Alterar");
        btnFiltrar = new JButton("Filtrar");
        btnSair = new JButton("Sair");
        painelBotoes.add(btnIncluir);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnAlterar);
        painelBotoes.add(btnFiltrar);
        painelBotoes.add(btnSair);

        // Painel de Filtros
        JPanel painelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelFiltros.add(new JLabel("Cliente:"));
        txtFiltroCliente = new JTextField(10);
        painelFiltros.add(txtFiltroCliente);

        painelFiltros.add(new JLabel("Produto:"));
        txtFiltroProduto = new JTextField(10);
        painelFiltros.add(txtFiltroProduto);

        painelFiltros.add(new JLabel("Data Início:"));
        txtFiltroDataInicio = new JTextField(8);
        painelFiltros.add(txtFiltroDataInicio);

        painelFiltros.add(new JLabel("Data Fim:"));
        txtFiltroDataFim = new JTextField(8);
        painelFiltros.add(txtFiltroDataFim);

        // Tabela de vendas
        String[] colunas = {"ID", "Cliente", "Produto", "Quantidade", "Data", "Total"};
        modeloTabela = new DefaultTableModel(colunas, 0);
        tabelaVendas = new JTable(modeloTabela);
        JScrollPane scrollPane = new JScrollPane(tabelaVendas);

        // Adicionando componentes ao JFrame
        add(painelBotoes, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(painelFiltros, BorderLayout.SOUTH);

        // Configurar eventos dos botões
        configurarEventos();
    }

    private void configurarEventos() {
        btnIncluir.addActionListener(e -> incluirVenda());
        btnExcluir.addActionListener(e -> excluirVenda());
        btnAlterar.addActionListener(e -> alterarVenda());
        btnFiltrar.addActionListener(e -> filtrarVendas());
        btnSair.addActionListener(e -> sairDaVenda());
    }

    private void sairDaVenda() {
        dispose(); // Fecha a janela atual
    }

    private void incluirVenda() {
        // Exemplo para incluir nova venda
        new VendaCadastroView(); // Abre o cadastro de venda
    }

    private void excluirVenda() {
        int linhaSelecionada = tabelaVendas.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma venda para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idVenda = (int) tabelaVendas.getValueAt(linhaSelecionada, 0); // ID da venda
        int confirmacao = JOptionPane.showConfirmDialog(this, "Tem certeza de que deseja excluir esta venda?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);

        if (confirmacao == JOptionPane.YES_OPTION) {
            boolean sucesso = vendaRepository.excluir(idVenda); // Exclui a venda no repositório

            if (sucesso) {
                carregarDadosTabela(); // Atualiza a tabela após exclusão
                JOptionPane.showMessageDialog(this, "Venda excluída com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao excluir venda.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void alterarVenda() {
        int linhaSelecionada = tabelaVendas.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma venda para alterar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idVenda = (int) tabelaVendas.getValueAt(linhaSelecionada, 0);
        Venda venda = vendaRepository.consultar(idVenda);

        if (venda == null) {
            JOptionPane.showMessageDialog(this, "Não foi possível encontrar os dados da venda.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Edita a venda
        new VendaCadastroView(vendaRepository,venda); // Passa a venda para edição
        carregarDadosTabela(); // Atualiza a tabela após edição
    }

    private void filtrarVendas() {
        String cliente = txtFiltroCliente.getText();
        String produto = txtFiltroProduto.getText();
        String dataInicio = txtFiltroDataInicio.getText();
        String dataFim = txtFiltroDataFim.getText();

        List<VendaDTO> vendasFiltradas = vendaRepository.filtrarDados(cliente, produto, dataInicio, dataFim);

        modeloTabela.setRowCount(0); // Limpa a tabela

        for (VendaDTO venda : vendasFiltradas) {
            modeloTabela.addRow(new Object[]{
                venda.getId(),
                venda.getCliente(),
                venda.getProduto(),
                venda.getQuantidade(),
                venda.getData(),
                String.format("%.2f", venda.getTotal())
            });
        }
    }

    private void carregarDadosTabela() {
        modeloTabela.setRowCount(0); // Limpa os dados existentes na tabela

        List<VendaDTO> vendas = vendaRepository.carregarDadosVendas(); // Busca todas as vendas

        for (VendaDTO venda : vendas) {
            modeloTabela.addRow(new Object[]{
                venda.getId(),
                venda.getCliente(),
                venda.getProduto(),
                venda.getQuantidade(),
                venda.getData(),
                String.format("%.2f", venda.getTotal())
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VendaForm vendaForm = new VendaForm();
            vendaForm.setVisible(true);
        });
    }
}
