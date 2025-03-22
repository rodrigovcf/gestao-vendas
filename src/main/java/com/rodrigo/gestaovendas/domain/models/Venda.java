package com.rodrigo.gestaovendas.domain.models;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Venda {
    
    private int codigo;
    private int CodigoCliente;
    private Cliente cliente;
    private List<ItemVenda> itens; 
    private LocalDate data;
    private double valorTotal;

    public void calcularValorTotal() {
        valorTotal = itens.stream()
                          .mapToDouble(ItemVenda::getValorTotal)
                          .sum(); 
    }
}
