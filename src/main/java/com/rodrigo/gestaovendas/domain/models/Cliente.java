package com.rodrigo.gestaovendas.domain.models;

import java.time.LocalDate;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Cliente {
    
	private int codigo;
    private String nome;
    private double limiteCompra;
    private LocalDate diaFechamentoFatura;
    
    public boolean podeComprar(double valorCompra) {
        return valorCompra <= limiteCompra;
    }
}

