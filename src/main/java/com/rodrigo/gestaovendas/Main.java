package com.rodrigo.gestaovendas;

import com.rodrigo.gestaovendas.app.ClienteService;
import com.rodrigo.gestaovendas.domain.repositories.ClienteRepository;
import com.rodrigo.gestaovendas.infra.ClienteDAO;
import com.rodrigo.gestaovendas.ui.cliente.ClienteForm;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClienteRepository clienteRepository = new ClienteDAO(); 
            ClienteService clienteService = new ClienteService(clienteRepository);
            new ClienteForm(clienteService);
        });
    }
}

