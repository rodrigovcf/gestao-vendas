package com.rodrigo.gestaovendas.utils;
import java.time.LocalDate;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import com.rodrigo.gestaovendas.domain.models.Venda;

public class ValidacaoUtil {

    /**
     * Calcula o total das compras realizadas após o fechamento.
     *
     * @param vendas Lista de vendas do cliente após o fechamento.
     * @return O total das compras realizadas após o fechamento.
     */
    public static double calcularComprasAposFechamento(List<Venda> vendas) {
        return vendas.stream()
                .mapToDouble(Venda::getValorTotal)
                .sum();
    }

    /**
     * Calcula a data do próximo fechamento com base no dia de fechamento.
     *
     * @param diaFechamentoFatura A data de fechamento da fatura.
     * @return A data do próximo fechamento.
     */
    public static LocalDate calcularProximoFechamento(LocalDate diaFechamentoFatura) {
        LocalDate dataAtual = LocalDate.now();
        LocalDate proximoFechamento = diaFechamentoFatura.withYear(dataAtual.getYear()).withMonth(dataAtual.getMonthValue());
        if (proximoFechamento.isBefore(dataAtual) || proximoFechamento.isEqual(dataAtual)) {
            // Próximo fechamento será no mês seguinte
            proximoFechamento = proximoFechamento.plusMonths(1);
        }
        return proximoFechamento;
    }

    /**
     * Verifica se o limite de crédito será excedido.
     *
     * @param limiteCredito Limite de crédito do cliente.
     * @param totalVendas Total das compras após o fechamento.
     * @param valorCompraAtual Valor da compra que está sendo realizada.
     * @return true se o limite será excedido, false caso contrário.
     */
    public static boolean verificarSeLimiteExcedido(double limiteCredito, double totalVendas, double valorCompraAtual) {
        return (totalVendas + valorCompraAtual) > limiteCredito;
    }

    /**
     * Calcula o valor disponível no limite.
     *
     * @param limiteCredito Limite de crédito do cliente.
     * @param totalVendas Total das compras após o fechamento.
     * @return O valor disponível no limite de crédito.
     */
    public static double calcularValorDisponivel(double limiteCredito, double totalVendas) {
        return limiteCredito - totalVendas;
    }
    
    
    /**
     * Verifica se o produto já está no carrinho.
     *
     * @param modeloTabela O modelo da tabela de carrinho.
     * @param codigoProduto O código do produto a verificar.
     * @return O índice da linha do produto se ele estiver no carrinho; -1 caso contrário.
     */
    public static int verificarProdutoDuplicado(DefaultTableModel modeloTabela, String codigoProduto) {
        for (int i = 0; i < modeloTabela.getRowCount(); i++) {
            if (modeloTabela.getValueAt(i, 0).toString().equals(codigoProduto)) {
                return i; // Retorna o índice da linha onde o produto foi encontrado
            }
        }
        return -1; // Produto não encontrado
    }

    /**
     * Atualiza a quantidade de um produto no carrinho.
     *
     * @param modeloTabela O modelo da tabela de carrinho.
     * @param indiceLinha O índice da linha do produto no carrinho.
     * @param quantidadeAdicional A quantidade a ser adicionada ao produto.
     */
    public static void atualizarQuantidadeProduto(DefaultTableModel modeloTabela, int indiceLinha, int quantidadeAdicional) {
        int quantidadeAtual = Integer.parseInt(modeloTabela.getValueAt(indiceLinha, 2).toString());
        int novaQuantidade = quantidadeAtual + quantidadeAdicional;

        // Atualiza a quantidade
        modeloTabela.setValueAt(novaQuantidade, indiceLinha, 2);

        // Atualiza o subtotal
        double precoUnitario = Double.parseDouble(modeloTabela.getValueAt(indiceLinha, 3).toString());
        double novoSubtotal = novaQuantidade * precoUnitario;
        modeloTabela.setValueAt(novoSubtotal, indiceLinha, 4);
    }

    /**
     * Exclui um produto do carrinho.
     *
     * @param modeloTabela O modelo da tabela de carrinho.
     * @param indiceLinha O índice da linha do produto no carrinho.
     */
    public static void excluirProduto(DefaultTableModel modeloTabela, int indiceLinha) {
        modeloTabela.removeRow(indiceLinha); // Remove a linha do produto selecionado
    }
}


