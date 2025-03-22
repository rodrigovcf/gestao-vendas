package com.rodrigo.gestaovendas.domain.repositories;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data // Gera getters, setters, equals, hashCode e toString automaticamente
@NoArgsConstructor // Construtor sem argumentos
@AllArgsConstructor // Construtor com todos os argumentos
public class VendaDTO {
    private int id;              // ID da venda
    private String cliente;      // Nome do cliente
    private String produto;      // Nome do produto
    private int quantidade;      // Quantidade do produto
    private LocalDate data;      // Data da venda
    private double total;        // Total da venda
}

