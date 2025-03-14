package com.rodrigo.gestaovendas.domain.models;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ItemVenda {
    private Produto produto;
    private int quantidade;
}
