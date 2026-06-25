# Projeto Prático Final: O Nosso LinkedIn Analyzer 🚀

E aí, pessoal! Prontos para colocar a mão na massa e resolver um problema do mundo real usando grafos?

Nesse projeto final, o desafio de vocês é construir um motor de análises e recomendações para uma rede social de conexões profissionais (estilo o LinkedIn). A gente vai usar como base a estrutura de grafos que fomos construindo nas nossas aulas.

---

## 🛠️ Como a nossa rede funciona?

A rede de contatos vai ser modelada como um **Grafo Não-Direcionado e Ponderado**:

- **Quem são as pessoas (Vértices):** Perfis dos usuários.
- **Conexões (Arestas):** Relacionamentos de amizade ou de trabalho (se a Ana tá conectada com o Bruno, o Bruno tá conectado com a Ana).
- **Intensidade da conexão (Pesos):** Representam a afinidade ou a "proximidade" entre as pessoas:
  - **Peso 1 (Muita Afinidade):** Galera que trabalha junto na mesma equipe ou se fala todo dia.
  - **Peso 5 (Pouca Afinidade):** Se adicionaram por educação na rede, mas quase não interagem.

---

## 📋 As Missões do Projeto

Vocês vão precisar criar uma classe chamada `LinkedInAnalyzer`. A ideia aqui é criar o cérebro das análises.
Lembrem-se: **uma das etapas iniciais será implementar um algoritmo clássico de menor caminho para grafos ponderados na classe `Grafo`**, já que vocês vão precisar dele para resolver algumas das missões abaixo.

Olha só o que vocês precisam implementar (escolham os melhores nomes de métodos e tipos de retorno para cada funcionalidade):

### 1. Construtor da Análise

- **O que recebe (Input):** A instância do grafo (`Grafo`) que representa a rede social.
- **O que faz:** Guarda essa instância para que as outras missões possam usá-la.

### 2. Sugestão de Conexões (Amigos de 2º Grau)

- **O que recebe (Input):** O nome de uma pessoa (ex: `"Ana"`).
- **O que faz:** Descobre quem são aquelas pessoas que são "amigas de amigas", mas que o usuário ainda não adicionou direto.
- **Regras:**
  1.  Não vale sugerir quem o usuário já tem como contato direto (1º grau).
  2.  Não vale sugerir o próprio usuário para ele mesmo.
  3.  A lista final precisa vir bem organizada: coloque no topo quem tem mais amigos em comum com o usuário (ordem decrescente).
- **Retorno esperado:** Uma estrutura contendo os nomes das pessoas sugeridas e a quantidade de amigos em comum com cada uma.

### 3. Grau de Separação (Quantos "passos" de distância?)

- **O que recebe (Input):** O nome de duas pessoas (Origem e Destino).
- **O que faz:** Descobre a quantos "passos" de conexão direta/indireta essas pessoas estão uma da outra (ex: 1 se forem contatos diretos, 2 se for amigo de amigo, etc.).
- **O desafio:** Pensem em como navegar de forma otimizada para achar o caminho com o menor número de conexões intermediárias.
- **Retorno esperado:** O número de passos (inteiro), ou `-1` se os dois perfis forem totalmente isolados (sem conexão).

### 4. Rota e Custo de Maior Afinidade

- **O que recebe (Input):** O nome de duas pessoas (Origem e Destino).
- **O que faz:** Encontra a melhor rota (a de maior afinidade acumulada, ou seja, com a menor soma de pesos das conexões) entre a origem e o destino, e calcula o custo total dessa rota.
- **Retorno esperado:** Vocês devem retornar tanto a sequência ordenada de nomes que formam esse melhor caminho quanto o custo acumulado (a soma dos pesos). Podem fazer isso usando métodos separados ou uma estrutura unificada. Se os perfis forem inalcançáveis, deve indicar custo `-1` e caminho vazio.

### 5. Mapear Grupos Isolados (Sub-redes)

- **O que recebe (Input):** Nenhum parâmetro (faz a varredura na rede inteira).
- **O que faz:** Acha todos os grupos de pessoas que estão conectadas entre si, mas totalmente isoladas dos outros grupos (componentes conexos).
- **Retorno esperado:** Uma lista ou coleção agrupando os usuários de cada sub-rede identificada.

---

## 💡 Sugestão de Cenário para Testes

Para ajudar vocês a validarem o código, aqui está uma sugestão de rede para cadastrar no `main`:

- **Pessoas na Rede Principal:** Ana, Bruno, Carlos, Daniela, Eduardo e Fernanda.
- **Grupo Isolado 1:** Gabriel e Hugo (só conversam entre si).
- **Grupo Isolado 2:** Igor e Juliana (só conversam entre si).

### Conexões e Afinidades (Pesos):

1.  **Ana** <-> **Bruno** (Peso 1 - Trabalham muito próximos)
2.  **Ana** <-> **Carlos** (Peso 2)
3.  **Ana** <-> **Daniela** (Peso 8)
4.  **Bruno** <-> **Eduardo** (Peso 1)
5.  **Carlos** <-> **Eduardo** (Peso 1)
6.  **Daniela** <-> **Fernanda** (Peso 5)
7.  **Eduardo** <-> **Fernanda** (Peso 1)
8.  **Gabriel** <-> **Hugo** (Peso 1)
9.  **Igor** <-> **Juliana** (Peso 1)

**Por que esse cenário é legal?**
Se você pedir a rota de maior afinidade de **Ana** para **Fernanda**:

- A rota mais curta em passos seria `Ana -> Daniela -> Fernanda` (com apenas 2 conexões/saltos, ou seja, 1 intermediário).
- Mas se você somar os pesos dessa rota curta, o custo é 8 + 5 = 13.
- Já a rota `Ana -> Bruno -> Eduardo -> Fernanda` é mais longa em número de passos (3 conexões/saltos, com 2 intermediários), porém seu custo é 1 + 1 + 1 = 3.
- O algoritmo de menor caminho ponderado (Dijkstra) deve encontrar a rota de custo 3, provando que a rota mais curta em número de conexões (passos) nem sempre é a de maior afinidade (menor custo ponderado)!

---

## 📂 O que vocês precisam entregar?

Para que o projeto seja avaliado, o grupo deve entregar:

1.  **Código-Fonte**:
    - **A classe `Grafo` atualizada**: Contendo a implementação do algoritmo de menor caminho em grafos ponderados que vocês desenvolveram.
    - **`LinkedInAnalyzer.java`**: Com as missões descritas acima implementadas.
    - **`LinkedInApp.java`**: Uma classe com o método `main` configurada para rodar o cenário de testes sugerido.
2.  **Link de um Vídeo Explicativo (YouTube)**:
    - Vocês precisam gravar um vídeo explicando detalhadamente o código, **linha por linha**, justificando e demonstrando que entendem tudo o que foi produzido.
    - Esse vídeo deve ser enviado através de um link do **YouTube**.
    - ⚠️ **Atenção:** Garantam que o vídeo está acessível (público ou não listado). Se o link estiver quebrado ou o vídeo inacessível na correção, a nota do grupo será **zero**.
3.  **Link do Repositório (GitHub)**:
    - Enviem o link do repositório no GitHub contendo o código exato utilizado na apresentação do vídeo.
    - ⚠️ **Atenção:** O repositório deve ser **público**. Se estiver privado, não conseguirei avaliar e a nota também será **zero**.

---

## 👥 Regras de Formação e Envio

- **Tamanho dos grupos:** No máximo **5 pessoas**. Grupos com mais de 5 pessoas **não serão aceitos**.
- **Identificação:** A entrega deve conter o nome completo e o **RGM** de todos os integrantes do grupo.
- **Envio:** Apenas **um membro** do grupo precisa realizar o envio contendo os links e as informações de todos.

---

Bora codar! Qualquer dúvida, chamem.
