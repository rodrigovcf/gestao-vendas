package com.rodrigo.gestaovendas.ui.menu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.rodrigo.gestaovendas.app.ClienteService;
import com.rodrigo.gestaovendas.app.ProdutoService;
import com.rodrigo.gestaovendas.app.VendaService;
import com.rodrigo.gestaovendas.ui.cliente.ClienteForm;
import com.rodrigo.gestaovendas.ui.produto.ProdutoForm;
import com.rodrigo.gestaovendas.ui.venda.VendaForm;

public class MenuPrincipal extends JFrame {

    private static final long serialVersionUID = 1L;

    public MenuPrincipal(ClienteService clienteService, ProdutoService produtoService, VendaService vendaService) {        

        // Configuração do frame
        setTitle("VR Software");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Painel de cabeçalho
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(new Color(255, 153, 102));
        headerPanel.setPreferredSize(new Dimension(0, 80));

        JLabel headerLabel = new JLabel("GESTÃO DE VENDAS", SwingConstants.LEFT);
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0)); // Adiciona margem esquerda
        headerPanel.add(headerLabel, BorderLayout.CENTER);


        JButton logoutButton = new JButton(new ImageIcon(getClass().getClassLoader().getResource("images/sair-1.png")));
        logoutButton.setContentAreaFilled(false);
        logoutButton.setBorderPainted(false);
        headerPanel.add(logoutButton, BorderLayout.EAST);

        // Painel de menu lateral
        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(new Color(240, 240, 240));
        menuPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridx = 0;

        // Botões do menu lateral
        JButton clientesButton = new JButton("Clientes", new ImageIcon(getClass().getClassLoader().getResource("images/cliente.png"))
);
        clientesButton.setPreferredSize(new Dimension(120, 40));
        clientesButton.setHorizontalAlignment(SwingConstants.LEFT);
        clientesButton.setContentAreaFilled(false);
        clientesButton.setBorderPainted(false);
        gbc.gridy = 0;
        menuPanel.add(clientesButton, gbc);

        JButton produtosButton = new JButton("Produtos", new ImageIcon(getClass().getClassLoader().getResource("images/produto.png"))
);
        produtosButton.setPreferredSize(new Dimension(120, 40));
        produtosButton.setHorizontalAlignment(SwingConstants.LEFT);
        produtosButton.setContentAreaFilled(false);
        produtosButton.setBorderPainted(false);
        gbc.gridy = 1;
        menuPanel.add(produtosButton, gbc);

        JButton vendasButton = new JButton("Vendas", new ImageIcon(getClass().getClassLoader().getResource("images/vendas.png"))
);
        vendasButton.setPreferredSize(new Dimension(120, 40));
        vendasButton.setHorizontalAlignment(SwingConstants.LEFT);
        vendasButton.setContentAreaFilled(false);
        vendasButton.setBorderPainted(false);
        gbc.gridy = 2;
        menuPanel.add(vendasButton, gbc);

        // Espaço vazio para empurrar os botões para cima
        JPanel emptySpace = new JPanel();
        emptySpace.setOpaque(false);
        gbc.gridy = 3;
        gbc.weighty = 1.0;
        menuPanel.add(emptySpace, gbc);

        // Painel de conteúdo principal
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        JLabel logoLabel = new JLabel(new ImageIcon("vr_software_logo.png"));
        contentPanel.add(logoLabel);

        // Adicionar os painéis ao frame
        add(headerPanel, BorderLayout.NORTH);
        add(menuPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        // Ações dos botões
        clientesButton.addActionListener(e -> {
                new ClienteForm(clienteService); // Passa o ClienteService ao abrir a janela
        });
        
        produtosButton.addActionListener(e -> {
                new ProdutoForm(produtoService); // Passa o ProdutoService ao abrir a janela
        });

        vendasButton.addActionListener(e -> {
        		new VendaForm();
        });
        
        logoutButton.addActionListener(e -> {
            System.exit(0); // Fecha completamente o programa
        });
        
        clientesButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                clientesButton.setBackground(new Color(230, 230, 230)); // Cor de fundo ao passar o mouse
                clientesButton.setOpaque(true);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                clientesButton.setBackground(null); // Volta à cor original
            }
        });
        
        produtosButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
            	produtosButton.setBackground(new Color(230, 230, 230)); // Cor de fundo ao passar o mouse
            	produtosButton.setOpaque(true);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
            	produtosButton.setBackground(null); // Volta à cor original
            }
        });
        
        vendasButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
            	vendasButton.setBackground(new Color(230, 230, 230)); // Cor de fundo ao passar o mouse
            	vendasButton.setOpaque(true);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
            	vendasButton.setBackground(null); // Volta à cor original
            }
        });
        
        logoutButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                logoutButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/sair-2.png")));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                logoutButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/sair-1.png")));
            }
        });
        
    }

}
