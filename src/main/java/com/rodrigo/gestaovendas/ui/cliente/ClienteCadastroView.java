package com.rodrigo.gestaovendas.ui.cliente;

import java.awt.GridLayout;
import java.time.LocalDate;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.rodrigo.gestaovendas.app.ClienteService;
import com.rodrigo.gestaovendas.domain.models.Cliente;

public class ClienteCadastroView extends JFrame {
    private static final long serialVersionUID = 1L;
    private ClienteService clienteService;
    private JTextField nomeField, limiteCreditoField, diaFechamentoField;
    private JButton btnSalvar, btnCancelar;
    private Cliente clienteEdicao;
    private Runnable onSaveCallback;

    // Construtor para novo cliente
    public ClienteCadastroView(ClienteService clienteService) {
        this(clienteService, null, null);
    }

    // Construtor para edição de cliente
    public ClienteCadastroView(ClienteService clienteService, Cliente cliente) {
        this(clienteService, cliente, null);
    }

    // Construtor com callback para atualização da tabela
    public ClienteCadastroView(ClienteService clienteService, Cliente cliente, Runnable onSaveCallback) {
        this.clienteService = clienteService;
        this.clienteEdicao = cliente;
        this.onSaveCallback = onSaveCallback;
        initUI();

        // Preencher campos se for edição
        if (cliente != null) {
            preencherCampos(cliente);
        }
    }

    private void initUI() {
        setTitle(clienteEdicao == null ? "Novo Cliente" : "Editar Cliente");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(4, 2, 10, 10));

        // Labels e campos
        add(new JLabel("Nome:"));
        nomeField = new JTextField();
        add(nomeField);

        add(new JLabel("Limite de Crédito:"));
        limiteCreditoField = new JTextField();
        add(limiteCreditoField);

        add(new JLabel("Dia de Fechamento:"));
        diaFechamentoField = new JTextField();
        add(diaFechamentoField);

        // Botões
        btnSalvar = new JButton("Salvar");
        btnCancelar = new JButton("Cancelar");
        add(btnSalvar);
        add(btnCancelar);

        // Eventos dos botões
        btnSalvar.addActionListener(e -> salvarCliente());
        btnCancelar.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void preencherCampos(Cliente cliente) {
        nomeField.setText(cliente.getNome());
        limiteCreditoField.setText(String.valueOf(cliente.getLimiteCompra()));
        diaFechamentoField.setText(cliente.getDiaFechamentoFatura() != null
            ? String.valueOf(cliente.getDiaFechamentoFatura().getDayOfMonth())
            : "");
    }

    private void salvarCliente() {
        try {
            // Validação de campos
            String nome = nomeField.getText().trim();
            if (nome.isEmpty()) {
                throw new IllegalArgumentException("O campo 'Nome' é obrigatório.");
            }

            double limiteCredito = Double.parseDouble(limiteCreditoField.getText().trim());
            if (limiteCredito <= 0) {
                throw new IllegalArgumentException("O limite de crédito deve ser um valor positivo.");
            }

            int diaFechamento = Integer.parseInt(diaFechamentoField.getText().trim());
            if (diaFechamento < 1 || diaFechamento > 31) {
                throw new IllegalArgumentException("O dia de fechamento deve estar entre 1 e 31.");
            }

            // Gerar LocalDate
            int mesFechamento = 4; // Abril
            int anoFechamento = 2025;
            LocalDate diaFechamentoFatura = LocalDate.of(anoFechamento, mesFechamento, diaFechamento);

            // Inicialização ou atualização do cliente
            if (clienteEdicao == null) {
                clienteEdicao = new Cliente();
            }

            clienteEdicao.setNome(nome);
            clienteEdicao.setLimiteCompra(limiteCredito);
            clienteEdicao.setDiaFechamentoFatura(diaFechamentoFatura);

            if (clienteEdicao.getCodigo() == 0) { // Código 0 indica novo cliente
                clienteService.cadastrarCliente(nome, limiteCredito, diaFechamentoFatura);
                JOptionPane.showMessageDialog(this, "Novo cliente cadastrado com sucesso!");
            } else {
                clienteService.atualizarCliente(clienteEdicao);
                JOptionPane.showMessageDialog(this, "Cliente atualizado com sucesso!");
            }

            // Callback para atualizar a lista
            if (onSaveCallback != null) {
                onSaveCallback.run();
            }

            dispose(); // Fechar a janela
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(),
                    "Validação de Dados", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar cliente: " + ex.getMessage(),
                    "Erro Interno", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
