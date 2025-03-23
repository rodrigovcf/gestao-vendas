package com.rodrigo.gestaovendas.domain.repositories;

import java.util.Date;
import java.util.List;
import com.rodrigo.gestaovendas.domain.models.*;

public interface VendaRepository {
  
	int incluir(Venda venda); // Retorna o ID da venda criada
	void salvarItensVenda(List<ItemVenda> itens);
	List<VendaDTO> carregarDadosVendas();
    Venda consultar(int codigo);
    List<Venda> listarTodos();
    boolean excluir(int codigo);
    Venda alterar(Venda venda);
    
	List<Venda> buscarPorCliente(int clienteId);
	public List<Venda> buscarPorPeriodo(Date inicio, Date fim);
	List<VendaDTO> filtrarDados(String cliente, String produto, String dataInicio, String dataFim);

	
}