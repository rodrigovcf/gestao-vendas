package com.rodrigo.gestaovendas.domain.repositories;

import java.util.Date;
import java.util.List;
import com.rodrigo.gestaovendas.domain.models.*;

public interface VendaRepository {
   
	void incluir(Venda venda);
    Venda consultar(int codigo);
    List<Venda> listarTodos();
    boolean excluir(int codigo);
    Venda alterar(Venda venda);
    
	List<Venda> buscarPorCliente(int clienteId);
	List<Venda> buscarPorPeriodo(Date inicio, Date fim);
}