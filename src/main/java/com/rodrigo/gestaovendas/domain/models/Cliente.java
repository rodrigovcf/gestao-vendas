package com.rodrigo.gestaovendas.domain.models;

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
    private int diaFechamentoFatura;
    
    public boolean podeComprar(double valorCompra) {
        return valorCompra <= limiteCompra;
    }
}

