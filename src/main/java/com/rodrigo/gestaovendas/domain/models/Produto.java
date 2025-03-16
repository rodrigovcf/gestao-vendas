package com.rodrigo.gestaovendas.domain.models;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Produto {
	
    private int codigo;
    private String descricao;
    private double preco;
}
