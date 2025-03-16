package com.rodrigo.gestaovendas;

import com.rodrigo.gestaovendas.infra.ConexaoBD;

public class Main {

	public static void main(String[] args) {
		System.out.println("Testando conexão com o banco...");
        ConexaoBD.testaConexao();

	}

}
