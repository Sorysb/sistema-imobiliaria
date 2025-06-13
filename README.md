
#  Sistema Imobiliário - Projeto Integrador

##  Descrição
Este projeto consiste em um sistema de gerenciamento para uma imobiliária, desenvolvido em **Java** com interface gráfica utilizando **Java Swing** e conexão com **MySQL**. O sistema permite o cadastro, gerenciamento e visualização de dados de clientes, corretores, imóveis, visitas, propostas e relatórios detalhados.

---

##  Funcionalidades Implementadas

###  Cadastro e Gerenciamento
- Clientes
- Corretores
- Imóveis (com filtros por tipo, status e localização)
- Visitas (com verificação de conflitos de data e hora)
- Propostas

###  Listagens
- Listagens gerais por entidade
- Visualização e cancelamento de visitas com autenticação
- Relatórios com contagens e resumos de dados

###  Relatórios e Indicadores
- Imóveis por tipo e status
- Clientes cadastrados
- Visitas agendadas e canceladas
- Vendas finalizadas no mês
- Corretores ativos

---

## Tecnologias Utilizadas
- **Java 17**
- **Java Swing (GUI)**
- **MySQL Workbench**
- **JDBC (MySQL Connector)**
- **NetBeans IDE 17**

---

## Banco de Dados

### Tabelas
- `cliente`: id, nome, cpf, telefone
- `corretor`: id, nome, creci, telefone
- `imovel`: id, tipo, rua, numero, cidade, estado, cep, valor, status
- `visita`: id, id_cliente, id_imovel, id_corretor, data_visita, status
- `proposta`: id, id_cliente, id_imovel, valor_proposta, status, data_proposta

### Relacionamentos
- Todas as tabelas estão normalizadas e as tabelas `visita` e `proposta` utilizam chaves estrangeiras com `cliente`, `corretor` e `imovel`.

---

##  Execução do Projeto

###  Pré-requisitos:
- NetBeans ou Visual Studio Code com suporte a Java
- MySQL Server e MySQL Workbench
- Adicionar o `mysql-connector-j-8.x.x.jar` ao classpath do projeto

### Execução:
1. Importar o projeto no NetBeans.
2. Executar o script `imobiliaria.sql` para criação do banco.
3. Rodar a classe `Main` e escolher:
   - `1` para executar a **versão Console**
   - `2` para abrir a **interface gráfica**

---

##  Login e Segurança
- Algumas funções (como cancelamento de visitas) exigem autenticação com senha (`Acesso@1`) que é possivel alteração direto no código.

---

## Objetivos Acadêmicos
Este projeto foi desenvolvido como parte do **projeto integrador do curso de Sistemas de Informação**, com o objetivo de aplicar na prática os seguintes conhecimentos:
- Estrutura de dados
- Conexão com banco de dados (JDBC)
- Desenvolvimento de interface gráfica (Swing)
- Boas práticas de programação Java
- Relacionamento de tabelas no banco de dados

---

## 👨‍💻 Desenvolvedor
- **Nome**: Pietro Brugnaro
- **E-mail**: pietro.brugnaro@live.com
- **GitHub**: [github.com/Sorysb](https://github.com/Sorysb)
- **Linkedin**: [https://www.linkedin.com/in/pietro-brugnaro/]

## Participantes do projeto

-   Arthur Ruan
-   Leandro Lucas
-   Pedro Henrique
-   Pietro Brugnaro
-   Ruan Gomes
-   Victor Augusto
---

*Última atualização: 13/06/2025*
