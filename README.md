# Gestão de Vendas

Este projeto é uma aplicação Java desenvolvida para gerenciar vendas, permitindo o cadastro e a gestão de clientes, produtos e transações comerciais.

## Funcionalidades

- **Cadastro de Clientes**: Adicione, edite e remova informações de clientes.
- **Cadastro de Produtos**: Gerencie o catálogo de produtos disponíveis para venda.
- **Processamento de Vendas**: Registre e acompanhe as vendas realizadas.

## Tecnologias Utilizadas

- **Linguagem**: Java
- **Interface Gráfica**: Swing
- **Persistência de Dados**: PostgreSQL

## Pré-requisitos

- Java Development Kit (JDK) 8 ou superior
- PostgreSQL 9.6 ou superior

## Configuração do Banco de Dados

1. Instale o PostgreSQL e crie um banco de dados para a aplicação.
2. Configure a conexão com o banco de dados no arquivo `DatabaseConfig.java`, fornecendo a URL do banco, usuário e senha.

## Instalação e Execução

1. Clone o repositório:

   ```bash
   git clone https://github.com/rodrigovcf/gestao-vendas.git
   ```

2. Navegue até o diretório do projeto:

   ```bash
   cd gestao-vendas
   ```

3. Compile o código-fonte:

   ```bash
   javac -d bin src/**/*.java
   ```

4. Execute a aplicação:

   ```bash
   java -cp bin com.rodrigo.gestaovendas.Main
   ```

## Estrutura do Projeto

- **src/**: Contém o código-fonte da aplicação.
- **bin/**: Diretório para os arquivos compilados.
- **lib/**: Bibliotecas externas utilizadas no projeto.

## Contribuições

Contribuições são bem-vindas! Sinta-se à vontade para abrir issues e pull requests.

## Licença

Este projeto está licenciado sob a [MIT License](LICENSE).
