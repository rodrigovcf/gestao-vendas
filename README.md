# Sistema de Gestão de Vendas

Bem-vindo ao **Sistema de Gestão de Vendas**, uma aplicação desenvolvida para gerenciar **Clientes**, **Produtos** e **Vendas**. O projeto inclui funcionalidades de **cadastro**, **consulta** e **controle** de entidades-chave, bem como uma validação de limite de crédito para vendas.

## 🛠️ Tecnologias Utilizadas

- **Java (Swing)**: Para desenvolvimento da interface gráfica.
- **PostgreSQL**: Banco de dados relacional utilizado.
- **JUnit 5**: Para criação de testes unitários.
- **Mockito**: Para mocks e simulação em testes.
- **Maven**: Gerenciador de dependências.

## ✨ Funcionalidades

- **Clientes**:
  - Cadastro de clientes com nome, limite de crédito e dia de fechamento de fatura.
  - Consulta e atualização de informações.
- **Produtos**:
  - Gerenciamento de produtos com nome e preço.
  - Consulta de produtos.
- **Vendas**:
  - Cadastro de vendas com validação de limite de crédito.
  - Consulta de vendas por cliente ou período.
  - Cálculo automático do valor total de vendas.

---

## 🚀 Executando a Aplicação

### Pré-Requisitos

- **Java 17+**
- **PostgreSQL 13+**
- **Maven**

### Passo a Passo

1. **Clone o Repositório**:
   ```bash
   git clone https://github.com/seu-repositorio/gestao-vendas.git
   cd gestao-vendas
   ```

2. **Configuração do Banco de Dados**:

   Crie um banco de dados no PostgreSQL:
   ```sql
   CREATE DATABASE gestao_vendas;
   ```

   Configure o arquivo `application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/gestao_vendas
   spring.datasource.username=seu_usuario
   spring.datasource.password=sua_senha
   spring.jpa.hibernate.ddl-auto=update
   ```

3. **Execute a Aplicação**:

   Compile o projeto:
   ```bash
   mvn clean install
   ```

   Inicie a aplicação executando o arquivo principal no diretório `src/main/java/com/rodrigo/gestaovendas`.

4. **Acesse o Sistema**:

   Interaja com a interface gráfica Swing assim que a janela principal for aberta.

---

## ✅ Cobertura de Testes Unitários

Testes unitários foram implementados para cobrir a lógica principal da aplicação, incluindo:

- Validação do limite de crédito em vendas.
- Cadastro e consulta de clientes, produtos e vendas.

### Executando os Testes

Para rodar os testes unitários:
```bash
mvn test
```

---

## 📂 Estrutura do Projeto

```
src
├── main
│   ├── java/com/rodrigo/gestaovendas
│   │   ├── app          # Camada principal da aplicação
│   │   ├── domain       # Entidades e modelos
│   │   ├── infra        # Acesso ao banco de dados
│   │   ├── repositories # Interfaces e repositórios
│   │   └── ui           # Interface e lógica de apresentação
├── test
│   ├── java/com/rodrigo/gestaovendas
│   │   ├── service      # Testes de lógica de negócio
│   │   ├── util         # Testes de utilitários
│   │   └── dao          # Testes de acesso ao banco de dados
```

---

## 📋 Script de Criação do Banco de Dados

Segue o script SQL para criar as tabelas utilizadas no projeto:

```sql
-- Tabela de Clientes
CREATE TABLE clientes (
    codigo SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    limite_compra DECIMAL(10, 2) NOT NULL,
    dia_fechamento_fatura DATE
);

-- Tabela de Produtos
CREATE TABLE produtos (
    codigo SERIAL PRIMARY KEY,
    descricao VARCHAR(100) NOT NULL,
    preco DECIMAL(10, 2) NOT NULL
);

-- Tabela de Vendas
CREATE TABLE venda (
    codigo SERIAL PRIMARY KEY,
    codigo_cliente INT NOT NULL,
    valor_total DECIMAL(10, 2) NOT NULL,
    data_venda DATE NOT NULL,
    FOREIGN KEY (codigo_cliente) REFERENCES clientes (codigo)
);

-- Tabela de Itens de Venda
CREATE TABLE venda_produto (
    codigo SERIAL PRIMARY KEY,
    codigo_venda INT NOT NULL,
    codigo_produto INT NOT NULL,
    quantidade INT NOT NULL,
    FOREIGN KEY (codigo_venda) REFERENCES venda (codigo),
    FOREIGN KEY (codigo_produto) REFERENCES produtos (codigo)
);
```

---

## 🌟 Contato

Desenvolvido por **Rodrigo Valença**. 
