# Gestão de Vendas

Este projeto é um sistema de gestão de vendas desenvolvido em Java com Swing para a interface gráfica e PostgreSQL como banco de dados. Ele permite o cadastro de clientes, produtos, e a gestão de vendas, incluindo um carrinho de compras.

## Tecnologias Utilizadas
- **Java** (Swing para interface gráfica)
- **PostgreSQL** (Banco de dados relacional)
- **Maven** (Gerenciamento de dependências)
- **JDBC** (Conexão com banco de dados)

## Funcionalidades
- Cadastro de clientes
- Cadastro de produtos
- Registro de vendas
- Adição de itens ao carrinho
- Cálculo automático do total da venda

## Estrutura do Banco de Dados

O projeto utiliza as seguintes tabelas no PostgreSQL:

```sql
CREATE TABLE public.clientes (
    codigo SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    limite_compra NUMERIC(10,2) NOT NULL,
    dia_fechamento_fatura DATE NOT NULL
);

CREATE TABLE public.produto (
    codigo SERIAL PRIMARY KEY,
    descricao VARCHAR(255) NOT NULL,
    preco NUMERIC(10,2) NOT NULL
);

CREATE TABLE public.venda (
    codigo SERIAL PRIMARY KEY,
    codigo_cliente INTEGER NOT NULL,
    data_venda DATE NOT NULL,
    valor_total NUMERIC(10,2) NOT NULL,
    CONSTRAINT fk_venda_cliente FOREIGN KEY (codigo_cliente)
        REFERENCES public.clientes (codigo)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE public.venda_produto (
    codigo_venda INTEGER NOT NULL,
    codigo_produto INTEGER NOT NULL,
    quantidade INTEGER NOT NULL,
    PRIMARY KEY (codigo_venda, codigo_produto),
    CONSTRAINT fk_venda_produto_venda FOREIGN KEY (codigo_venda)
        REFERENCES public.venda (codigo)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_venda_produto FOREIGN KEY (codigo_produto)
        REFERENCES public.produto (codigo)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
```

## Como Executar o Projeto

1. Clone este repositório:
   ```sh
   git clone https://github.com/rodrigovcf/gestao-vendas.git
   ```
2. Configure o banco de dados PostgreSQL e execute os scripts acima.
3. Importe o projeto em sua IDE preferida (Eclipse, IntelliJ, etc.).
4. Configure a conexão com o banco de dados no arquivo de propriedades.
5. Execute a aplicação Java.

## Contribuição
Sinta-se à vontade para abrir issues e enviar pull requests para melhorias.
