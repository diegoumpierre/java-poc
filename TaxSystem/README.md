# Solution
(1) Build a TAX system where different products have different tax per state and year.
- Use like an API
- provide one swagger for the tests

## Fee
- insert fee
- remove fee
- list all fee

## Product
- insert product
- remove product
- list all product

# Design for the solution patterns 
- Specification
- Chain

# Features
- adicionar os taxes de um ano seguinte, 
  - ex: adicionar todos os taxes de 2025. 
    - Isto tem que ser uma manutenção super simples de fazer no código,
    - tem que pensar na forma mais simples de fazer isso, ou seja, mexer o menos possível 
    - no código pra adicionar taxes.
- remover taxes de anos anteriores de forma super simples, com o menor esforço possível.
- não pode usar qualquer tipo de persistência de dados, os dados estão presente nas classes.

- add taxes per year
- remove taxes before the year in parameter

# Versão 2
- Estudar o padrão specification
- Nada de controller nem subir servidor com o padrão specification
- core com no máximo 4 a 5 classes