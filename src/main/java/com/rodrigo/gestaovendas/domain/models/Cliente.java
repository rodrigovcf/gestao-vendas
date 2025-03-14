package com.rodrigo.gestaovendas.domain.models;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {
    private int codigo;
    private String nome;
    private double limiteCompra;
    private int diaFechamentoFatura;
}

