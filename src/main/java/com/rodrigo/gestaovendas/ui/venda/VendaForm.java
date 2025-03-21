package com.rodrigo.gestaovendas.ui.venda;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.format.DateTimeFormatter;
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
    private JButton btnIncluir, btnExcluir, btnAlterar, btnConsultar, btnVisualizarPorCliente, btnVisualizarPorProduto, btnFiltrar;
    private VendaRepository vendaRepository;
    
    public VendaForm(VendaRepository vendaRepository) {
        this.vendaRepository = vendaRepository;

        setTitle("Gerenciamento de Vendas");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        vendaRepository = new VendaDAO();
        inicializarComponentes();

        // Carrega os dados da tabela ao abrir o formulário
        carregarDadosTabela();
    }



    private void inicializarComponentes() {
        // Configurar layout principal
        setLayout(new BorderLayout());

        // Barra de Ferramentas com botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnIncluir = new JButton("Incluir");
        btnExcluir = new JButton("Excluir");
        btnAlterar = new JButton("Alterar");
        btnConsultar = new JButton("Consultar");
        btnVisualizarPorCliente = new JButton("Por Cliente");
        btnVisualizarPorProduto = new JButton("Por Produto");
        btnFiltrar = new JButton("Filtrar");
        painelBotoes.add(btnIncluir);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnAlterar);
        painelBotoes.add(btnConsultar);
        painelBotoes.add(btnVisualizarPorCliente);
        painelBotoes.add(btnVisualizarPorProduto);
        painelBotoes.add(btnFiltrar);

        // Tabela para exibir vendas
        String[] colunas = {"ID", "Cliente", "Produto", "Quantidade", "Data", "Total"};
        modeloTabela = new DefaultTableModel(colunas, 0);
        tabelaVendas = new JTable(modeloTabela);
        JScrollPane scrollPane = new JScrollPane(tabelaVendas);

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

        painelFiltros.add(btnFiltrar);

        // Adicionando componentes ao JFrame
        add(painelBotoes, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(painelFiltros, BorderLayout.SOUTH);

        // Configurar eventos dos botões
        configurarEventos();
    }

    private void configurarEventos() {
        // Evento para incluir venda
        btnIncluir.addActionListener(e -> incluirVenda());

        // Evento para excluir venda
        btnExcluir.addActionListener(e -> excluirVenda());

        // Evento para alterar venda
        btnAlterar.addActionListener(e -> alterarVenda());

        // Evento para consultar vendas
        btnConsultar.addActionListener(e -> consultarVendas());

        // Evento para visualizar por cliente
        btnVisualizarPorCliente.addActionListener(e -> visualizarPorCliente());

        // Evento para visualizar por produto
        btnVisualizarPorProduto.addActionListener(e -> visualizarPorProduto());

        // Evento para filtrar vendas
        btnFiltrar.addActionListener(e -> filtrarVendas());
    }

    // Método para incluir venda (apenas um exemplo básico)
    private void incluirVenda() {
        new VendaCadastroView();
    }

    private void excluirVenda() {
        int linhaSelecionada = tabelaVendas.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma venda para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obtém o ID da venda selecionada
        int idVenda = (int) tabelaVendas.getValueAt(linhaSelecionada, 0);

        // Confirmação de exclusão
        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Tem certeza de que deseja excluir esta venda?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION);

        if (confirmacao == JOptionPane.YES_OPTION) {
            boolean sucesso = vendaRepository.excluir(idVenda); // Chama o método excluir

            if (sucesso) {
                // Recarrega os dados na tabela
                carregarDadosTabela();
                JOptionPane.showMessageDialog(this, "Venda excluída com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao excluir venda.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    // Método para alterar venda
    private void alterarVenda() {
        int linhaSelecionada = tabelaVendas.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma venda para alterar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obter o ID da venda selecionada
        int idVenda = (int) tabelaVendas.getValueAt(linhaSelecionada, 0);

        // Consultar os dados da venda no banco de dados
        Venda venda = vendaRepository.consultar(idVenda);

        if (venda == null) {
            JOptionPane.showMessageDialog(this, "Não foi possível encontrar os dados da venda.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Abrir a tela de edição
        VendaCadastroView cadastroView = new VendaCadastroView(vendaRepository, venda);
        cadastroView.setVisible(true);

        // Após a edição e salvamento, a venda alterada será retornada
        Venda vendaAlterada = vendaRepository.alterar(venda);

        if (vendaAlterada != null) {
            carregarDadosTabela(); // Atualiza a tabela
            JOptionPane.showMessageDialog(this, "Venda alterada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao alterar venda.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }


    // Método para consultar vendas
    private void consultarVendas() {
        JOptionPane.showMessageDialog(this, "Função de consulta implementada aqui.");
    }

    // Método para visualizar vendas por cliente
    private void visualizarPorCliente() {
        JOptionPane.showMessageDialog(this, "Função de visualização por cliente implementada aqui.");
    }

    // Método para visualizar vendas por produto
    private void visualizarPorProduto() {
        JOptionPane.showMessageDialog(this, "Função de visualização por produto implementada aqui.");
    }

    // Método para filtrar vendas
    private void filtrarVendas() {
        String cliente = txtFiltroCliente.getText();
        String produto = txtFiltroProduto.getText();
        String dataInicio = txtFiltroDataInicio.getText();
        String dataFim = txtFiltroDataFim.getText();

        JOptionPane.showMessageDialog(this, String.format("Filtro aplicado:\nCliente: %s\nProduto: %s\nPeríodo: %s a %s",
                cliente, produto, dataInicio, dataFim));
    }
    
    private void carregarDadosTabela() {
        DefaultTableModel modeloTabela = (DefaultTableModel) tabelaVendas.getModel();
        modeloTabela.setRowCount(0); // Limpa os dados atuais da tabela

        List<VendaDTO> vendas = vendaRepository.carregarDadosVendas();
        for (VendaDTO venda : vendas) {
            modeloTabela.addRow(new Object[]{
                venda.getId(),
                venda.getCliente(),
                venda.getProduto(),
                venda.getQuantidade(),
                venda.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                String.format("%.2f", venda.getTotal())
            });
        }
    }


    public static void main(String[] args) {
    	SwingUtilities.invokeLater(() -> {
            VendaRepository vendaRepository = new VendaDAO();
            VendaForm vendaForm = new VendaForm(vendaRepository);
            vendaForm.setVisible(true);
        });
    }
}

