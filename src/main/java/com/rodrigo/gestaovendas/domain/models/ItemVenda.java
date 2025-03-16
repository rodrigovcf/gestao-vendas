package com.rodrigo.gestaovendas.domain.models;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemVenda {
    
    private Produto produto;   
    private int quantidade;    
    private double precoUnitario;
    
    public double getValorTotal() {
        return precoUnitario * quantidade; 
    }
}

