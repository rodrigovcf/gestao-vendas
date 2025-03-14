package com.rodrigo.gestaovendas.domain.repositories;

import java.util.List;
import com.rodrigo.gestaovendas.domain.models.*;

interface VendaRepository {
   
	void salvar(Venda venda);
    Venda buscarPorCodigo(int codigo);
    List<Venda> listarTodos();
}