package com.rodrigo.gestaovendas.domain.repositories;

import java.util.List;
import com.rodrigo.gestaovendas.domain.models.*;

public interface VendaRepository {
  
	int incluir(Venda venda); // Retorna o ID da venda criada
	void salvarItensVenda(List<ItemVenda> itens);
    Venda consultar(int codigo);
    List<Venda> listarTodos();
    boolean excluir(int codigo);
    Venda alterar(Venda venda);
    
	List<Venda> buscarPorCliente(int clienteId);
	List<VendaDTO> filtrarDados(String cliente, String produto, String dataInicio, String dataFim);
	List<Venda> buscarPorPeriodo(java.sql.Date inicio, java.sql.Date fim, int clienteId);
	Venda buscarVendaPorId(int idVenda);
	List<VendaDTO> carregarDadosVendas();
	
}