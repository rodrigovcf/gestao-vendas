package com.rodrigo.gestaovendas.ui.produto;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.rodrigo.gestaovendas.app.ProdutoService;
import com.rodrigo.gestaovendas.domain.models.Produto;

public class ProdutoCadastroView extends JFrame{

	private static final long serialVersionUID = 1L;
	private ProdutoService produtoService;
	private Produto produtoEdicao;
	private JTextField descricaoField, precoField;
	private JButton btnSalvar, btnCancelar;
	private Runnable onSaveCallback;

	// Construtor para novo produto
    public ProdutoCadastroView(ProdutoService produtoService) {
        this(produtoService, null, null);
    }

    // Construtor para edição de produto
    public ProdutoCadastroView(ProdutoService produtoService, Produto produto) {
        this(produtoService, produto, null);
    }
    
    // Construtor com callback para atualização da tabela
	public ProdutoCadastroView(ProdutoService produtoService, Produto produto, Runnable onSaveCallback) {
		this.produtoService = produtoService;
		this.produtoEdicao = produto;
		this.onSaveCallback = onSaveCallback;
		initUI();
		
		if(produto != null) {
			preencherCampos(produto);
		}
	}
	
	
	private void initUI() {
        setTitle(produtoEdicao == null ? "Novo Cliente" : "Editar Cliente");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(4, 2, 10, 10));

        // Labels e campos
        add(new JLabel("Descrição do Produto:"));
        descricaoField = new JTextField();
        add(descricaoField);

        add(new JLabel("Preço do Produto:"));
        precoField = new JTextField();
        add(precoField);

        // Botões
        btnSalvar = new JButton("Salvar");
        btnCancelar = new JButton("Cancelar");
        add(btnSalvar);
        add(btnCancelar);

        // Eventos dos botões
        btnSalvar.addActionListener(e -> salvarProduto());
        btnCancelar.addActionListener(e -> dispose());

        setVisible(true);
    }
	
	private void preencherCampos(Produto produto) {
		descricaoField.setText(produto.getDescricao());
        precoField.setText(String.valueOf(produto.getPreco()));
		
	}
	
	private void salvarProduto() {
        try {
            // Validação de campos
            String descricao = descricaoField.getText().trim();
            if (descricao.isEmpty()) {
                throw new IllegalArgumentException("O campo 'Descrição' é obrigatório.");
            }

            double preco = Double.parseDouble(precoField.getText().trim());
            if (preco <= 0) {
                throw new IllegalArgumentException("O limite de crédito deve ser um valor positivo.");
            }

            // Inicialização ou atualização do cliente
            if (produtoEdicao == null) {
            	produtoEdicao = new Produto();
            }

            produtoEdicao.setDescricao(descricao);
            produtoEdicao.setPreco(preco);

            if (produtoEdicao.getCodigo() == 0) { // Código 0 indica novo cliente
            	produtoService.cadastrarProduto(descricao, preco);
                JOptionPane.showMessageDialog(this, "Novo cliente cadastrado com sucesso!");
            } else {
            	produtoService.atualizarProduto(produtoEdicao);
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
