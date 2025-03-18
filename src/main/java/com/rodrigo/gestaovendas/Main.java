package com.rodrigo.gestaovendas;

import com.rodrigo.gestaovendas.app.ClienteService;
import com.rodrigo.gestaovendas.app.ProdutoService;
import com.rodrigo.gestaovendas.app.VendaService;
import com.rodrigo.gestaovendas.domain.repositories.ClienteRepository;
import com.rodrigo.gestaovendas.domain.repositories.ProdutoRepository;
import com.rodrigo.gestaovendas.domain.repositories.VendaRepository;
import com.rodrigo.gestaovendas.infra.ClienteDAO;
import com.rodrigo.gestaovendas.infra.ProdutoDAO;
import com.rodrigo.gestaovendas.infra.VendaDAO;
import com.rodrigo.gestaovendas.ui.menu.MenuPrincipal;

public class Main {
    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            ClienteRepository clienteRepository = new ClienteDAO(); 
//            ClienteService clienteService = new ClienteService(clienteRepository);
//            new ClienteForm(clienteService);
//        });
    	
    	
    	ClienteRepository clienteRepository = new ClienteDAO();
    	ClienteService clienteService = new ClienteService(clienteRepository);
    	
    	ProdutoRepository produtoRepository = new ProdutoDAO();
    	ProdutoService produtoService = new ProdutoService(produtoRepository);

    	VendaRepository vendaRepository = new VendaDAO();
    	VendaService vendaService = new VendaService(vendaRepository, clienteRepository, produtoRepository);
        
        new MenuPrincipal(clienteService, produtoService, vendaService).setVisible(true);;

    }
}

