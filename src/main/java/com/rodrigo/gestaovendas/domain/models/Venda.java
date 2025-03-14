package com.rodrigo.gestaovendas.domain.models;

import java.util.List;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Venda {
    private int codigo;
    private Cliente cliente;
    private List<ItemVenda> itens;
}