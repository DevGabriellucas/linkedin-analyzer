# 🎥 Roteiro do Vídeo — LinkedIn Analyzer

> Dica de gravação: fale com calma, mostre o arquivo na tela e vá rolando conforme explica.
> Tempo alvo: 8 a 15 minutos. Não precisa decorar — leia com suas palavras.
> A ordem ideal é: **1) Abertura → 2) Grafo.java → 3) LinkedInAnalyzer.java → 4) LinkedInApp.java → 5) Rodar e mostrar a saída → 6) Encerramento.**

---

## 1) Abertura (30–60 segundos)

> "Olá! Esse é o nosso projeto final de Estrutura de Dados: o **LinkedIn Analyzer**.
> O grupo é formado por **Gabriel Lucas de Araujo Bandeira**, **Marcos Manoel** e **Lucas Orange**.
>
> A ideia do projeto é construir um motor de análises para uma rede social profissional,
> tipo o LinkedIn, usando **grafos**. A gente modelou a rede como um **grafo não-direcionado
> e ponderado**: cada **pessoa é um vértice**, cada **conexão é uma aresta**, e o **peso da
> aresta é a afinidade** — quanto menor o peso, mais próximas as pessoas são.
>
> O projeto tem três arquivos: a classe `Grafo`, que é a estrutura de dados; a classe
> `LinkedInAnalyzer`, que é o cérebro das análises com as cinco missões; e a `LinkedInApp`,
> que tem o `main` e roda o cenário de teste. Vamos começar pela base, o `Grafo`."

---

## 2) Grafo.java — a estrutura de dados

### 2.1 — A classe e o atributo principal

> "A classe `Grafo` é **genérica**, com esse `<T>` aqui. Isso significa que ela aceita
> qualquer tipo de vértice — no nosso caso vamos usar `String` com os nomes, mas poderia ser
> um ID, um número, etc. A única exigência é que o tipo tenha `equals` e `hashCode`, e a
> `String` já garante isso.
>
> O coração da classe é esse atributo `adjacencia`, que é um **mapa de listas de adjacência**:
> o formato é `vértice -> (vizinho -> peso)`. Ou seja, pra cada pessoa eu guardo um outro mapa
> com os vizinhos dela e o peso de cada conexão.
>
> Escolhemos **lista de adjacência** em vez de matriz porque rede social é um grafo **esparso**
> — cada pessoa tem poucas conexões perto do total. Assim o consumo de memória é proporcional
> ao número de arestas, e não ao número de pessoas ao quadrado. E consultar o peso de uma
> conexão fica **O(1)**."

> "No construtor a gente usa um `LinkedHashMap`. Poderia ser um `HashMap` comum, mas o
> `LinkedHashMap` **preserva a ordem de inserção**. Isso deixa as saídas determinísticas —
> os vizinhos e os grupos sempre saem na mesma ordem — o que facilita testar, e não custa
> nada a mais em desempenho."

### 2.2 — Construção do grafo (adicionarVertice / adicionarAresta)

> "O `adicionarVertice` insere uma pessoa isolada. Ele é **idempotente**: usa `putIfAbsent`,
> então se a pessoa já existe, não acontece nada — não apaga as conexões dela. E logo no começo
> tem `Objects.requireNonNull`, que é uma **programação defensiva**: se passarem `null`, a
> gente avisa com uma mensagem clara em vez de quebrar lá na frente."

> "O `adicionarAresta` é o método principal de construção. Ele cria uma conexão **com peso**
> entre duas pessoas. Repare em três validações:
> - primeiro garante que origem e destino não são nulos;
> - depois exige que o **peso seja positivo** — não faz sentido afinidade zero ou negativa;
> - e por fim **proíbe laço**, ou seja, conectar a pessoa nela mesma.
>
> Como o grafo é **não-direcionado**, a parte mais importante são essas duas últimas linhas:
> a gente registra a aresta **nos dois sentidos** — coloca o destino na lista da origem **e**
> a origem na lista do destino. É isso que faz 'se a Ana está conectada ao Bruno, o Bruno está
> conectado à Ana'."

### 2.3 — Consultas (contemVertice, saoVizinhos, getVertices, getVizinhos, getPeso)

> "Aqui temos os métodos de consulta. `contemVertice` diz se a pessoa existe; `saoVizinhos`
> diz se há conexão direta entre duas pessoas.
>
> Um detalhe de qualidade: o `getVertices` e o `getVizinhos` retornam coleções
> **imutáveis**, com `unmodifiableSet` e `unmodifiableMap`. Assim quem chama esses métodos
> **consegue ler, mas não consegue alterar** a estrutura interna do grafo por fora. Isso
> protege a integridade dos dados.
>
> O `getPeso` devolve o peso de uma conexão e, se a conexão não existir, lança uma exceção
> com mensagem explicativa em vez de retornar um valor errado."

### 2.4 — Dijkstra (caminhoMinimo) — a parte mais importante

> "Esse é o algoritmo clássico de menor caminho que o enunciado pediu: o **Dijkstra**.
> Ele acha o caminho de **menor soma de pesos** entre duas pessoas.
>
> Por que Dijkstra e não outro? Porque **todos os nossos pesos são positivos**. Nessa
> condição, o Dijkstra é a escolha certa e eficiente, com complexidade **O((V + E) · log V)**.
>
> Vou explicar a lógica:
> - Criamos um mapa `distancia`, que guarda o menor custo conhecido até cada vértice.
>   Começa todo mundo com `Integer.MAX_VALUE`, que representa 'infinito', e só a origem
>   começa com **0**.
> - Criamos também o mapa `predecessor`, que guarda de onde a gente chegou em cada vértice.
>   É ele que vai permitir **remontar o caminho** no final.
> - Usamos uma **fila de prioridade** (`PriorityQueue`), que é um **min-heap**, ordenada pelo
>   custo. Ela sempre entrega primeiro o vértice de **menor distância** conhecida — esse é o
>   pulo do gato do Dijkstra.
>
> Dentro do laço:
> - tiramos da fila o vértice de menor custo;
> - se aquela entrada for **obsoleta** (já achamos um caminho melhor pra ele), ignoramos —
>   é essa checagem `distU > distancia.get(u)`;
> - se chegamos no **destino**, podemos parar, porque nesse ponto o custo já é o mínimo;
> - senão, fazemos o **relaxamento**: pra cada vizinho, se passar por `u` der um custo menor
>   do que o conhecido, a gente **atualiza** a distância, marca o predecessor e joga o vizinho
>   de volta na fila.
>
> No final, se a distância até o destino continuar 'infinita', é porque ele é **inalcançável**,
> e retornamos um resultado especial. Senão, chamamos o `reconstruirCaminho`."

> "O `reconstruirCaminho` é simples: ele começa no destino e vai voltando pelo mapa de
> predecessores até a origem. Como ele monta de trás pra frente, a gente usa `addFirst`
> numa `LinkedList`, que insere no começo — assim o caminho já sai na ordem certa, da origem
> pro destino."

### 2.5 — A classe interna ResultadoCaminho

> "Pra retornar o resultado do Dijkstra de forma organizada, criamos essa classe
> `ResultadoCaminho`. Ela é **imutável** e guarda duas coisas: a **lista de vértices** do
> caminho e o **custo total**.
>
> Repare que no construtor a gente faz uma **cópia defensiva** com `new ArrayList<>` e ainda
> envolve com `unmodifiableList`. Isso garante que ninguém de fora consiga alterar o caminho
> depois que ele foi calculado.
>
> Tem também o método de fábrica `inalcancavel()`, que cria o caso especial com custo **-1**
> e caminho vazio, e o `isAlcancavel()`, que diz se existe caminho válido. Isso deixa o código
> de quem usa bem mais limpo e legível."

---

## 3) LinkedInAnalyzer.java — o cérebro das 5 missões

> "Agora a classe `LinkedInAnalyzer`. Ela **não modifica** a rede — só consulta. Por isso ela
> recebe o grafo pronto no construtor e guarda como uma referência só de leitura, com `final`."

### Missão 1 — Construtor

> "A **Missão 1** é o construtor. Ele recebe a rede já montada e guarda na variável `rede`.
> O `Objects.requireNonNull` garante que ninguém crie um analisador com uma rede nula."

### Missão 2 — Sugestão de conexões (amigos de 2º grau)

> "A **Missão 2** sugere conexões: os famosos 'amigos de amigos'.
>
> Primeiro pego os **contatos diretos** da pessoa. Depois faço dois laços: pra cada amigo
> direto, olho os amigos **dele**. Cada um desses 'amigos do amigo' é um candidato.
>
> Aplico as três regras do enunciado:
> - se o candidato for a **própria pessoa**, pulo (regra 2);
> - se o candidato **já é contato direto**, pulo (regra 1);
> - senão, conto ele como sugestão. Uso o `merge` aqui pra somar: cada amigo em comum que
>   leva até esse candidato soma **+1** na contagem de amigos em comum.
>
> No final, transformo isso em uma lista de `Sugestao` e **ordeno**: primeiro quem tem
> **mais amigos em comum** (decrescente), e em caso de empate, por **ordem alfabética** —
> isso é a regra 3 e também deixa a saída previsível."

### Missão 3 — Grau de separação (BFS)

> "A **Missão 3** é o grau de separação: quantos **passos** separam duas pessoas.
>
> Aqui o ponto mais importante de justificar: usamos **BFS, busca em largura, e NÃO Dijkstra**.
> Por quê? Porque aqui o que importa é o **número de saltos**, não o peso. Cada conexão vale
> 'um passo', independente da afinidade. E a BFS é exatamente o algoritmo que acha o caminho
> com **menos arestas**, em **O(V + E)**.
>
> A lógica: começo pela origem com nível 0, vou visitando os vizinhos camada por camada usando
> uma **fila**. Quando encontro o destino, retorno o nível dele — que é o número de passos.
> Se a fila esvaziar sem achar, retorno **-1**, que significa que eles estão em sub-redes
> diferentes, sem conexão. E se origem e destino forem a mesma pessoa, retorno 0."

### Missão 4 — Rota de maior afinidade (Dijkstra)

> "A **Missão 4** acha a rota de **maior afinidade**, que é o caminho de **menor soma de pesos**.
> Aqui a gente **reaproveita** o Dijkstra que implementamos na classe `Grafo` — esse método só
> valida as pessoas e delega pro `caminhoMinimo`.
>
> E é aqui que está a sacada do projeto: a rota de maior afinidade **pode ter mais passos** que
> o grau de separação. O caminho com menos saltos nem sempre é o de menor custo. Vou provar isso
> daqui a pouco quando rodar o programa."

### Missão 5 — Grupos isolados (componentes conexos)

> "A **Missão 5** mapeia os **grupos isolados**, que em teoria dos grafos são os
> **componentes conexos**.
>
> A ideia: percorro todos os vértices; pra cada um que ainda **não foi visitado**, eu disparo
> uma BFS que explora **tudo que é alcançável** a partir dele — isso forma um grupo. Marco todo
> mundo como visitado e parto pro próximo não-visitado, que vai ser um novo grupo. No final
> tenho a lista de todas as sub-redes. Complexidade **O(V + E)**.
>
> Um detalhe elegante: uso `visitados.add(vizinho)` direto no `if`. O método `add` de um `Set`
> retorna `true` quando o elemento era novo — então em uma linha eu já testo e marco como
> visitado."

### Missão 6 (BÔNUS) — Ranking de influência

> "Como extra, a gente adicionou uma **Missão 6**: um **ranking de influência**. A ideia é a
> métrica mais clássica de redes sociais, a **centralidade de grau**: quanto **mais conexões
> diretas** uma pessoa tem, mais influente/central ela é na rede.
>
> O método percorre todos os vértices, conta o número de vizinhos de cada um — que é o **grau**
> daquele vértice — e monta a lista ordenada do mais conectado pro menos conectado, com desempate
> alfabético. É um O(V) bem simples. No nosso cenário, a Ana e o Eduardo lideram com 3 conexões
> cada."

### As classes internas Sugestao e Influencia

> "Por fim, as classes `Sugestao` e `Influencia`. Ambas são objetos imutáveis: a `Sugestao`
> guarda o nome e a **quantidade de amigos em comum**; a `Influencia` guarda o nome e o
> **número de conexões**. O `toString` das duas ainda trata singular e plural só pra saída
> ficar bem escrita."

---

## 4) LinkedInApp.java — o main e o menu interativo

> "Por último a `LinkedInApp`, que tem o `main`. No `montarRede` eu cadastro exatamente o
> cenário sugerido no enunciado: a rede principal com Ana, Bruno, Carlos, Daniela, Eduardo e
> Fernanda, e dois grupos isolados — Gabriel com Hugo, e Igor com Juliana — cada um com seus pesos.
>
> Em vez de só rodar um cenário fixo, a gente montou um **menu interativo**. Tem um laço que
> mostra as opções e lê a escolha do usuário com um `Scanner`. As opções 1, 2 e 3 pedem os
> nomes das pessoas; a opção 6 roda a **demonstração completa** do cenário sugerido de uma vez —
> que é a que prova todos os casos, inclusive os de borda.
>
> Um cuidado importante: a chamada de cada opção está dentro de um `try-catch`. Se o usuário
> digitar um nome que não existe na rede, o analisador lança uma exceção, a gente captura,
> mostra uma mensagem de erro amigável e o programa **continua rodando** em vez de quebrar.
>
> E na opção 6 eu testo de propósito os **casos de borda**: o grau de separação entre Ana e
> Gabriel, que estão em sub-redes diferentes e dá **-1**; e a rota da Ana até o Igor, que é
> **inalcançável**."

---

## 5) Rodar e mostrar a saída (parte mais importante para a nota!)

> "Agora vou compilar e executar pra mostrar funcionando."

Mostre na tela rodando:
```
javac Grafo.java LinkedInAnalyzer.java LinkedInApp.java
java LinkedInApp
```

> "Aqui aparece o menu. Vou escolher a opção **6**, que roda a demonstração completa do cenário."

Digite `6` e, conforme a saída aparece, comente:

> "Na **Missão 2**, as sugestões pra Ana saem ordenadas: o Eduardo aparece primeiro porque tem
> 2 amigos em comum com a Ana, depois a Fernanda com 1.
>
> Na **Missão 3**, Ana e Fernanda estão a 2 passos. E Ana e Gabriel dá **-1**, porque estão em
> redes separadas — exatamente o caso de borda.
>
> Na **Missão 4**, olha a prova: a rota de maior afinidade de Ana até Fernanda é
> `Ana → Bruno → Eduardo → Fernanda`, com custo **3**. Repare que ela tem **3 saltos**, mas é
> mais barata que a rota curta `Ana → Daniela → Fernanda`, que teria só 2 saltos mas custaria
> **13** (8 + 5). Isso prova que **menos passos não é a mesma coisa que maior afinidade** —
> é a diferença entre a BFS e o Dijkstra. E a rota de Ana até Igor dá inalcançável, custo -1.
>
> Na **Missão 5**, ele identifica corretamente **3 sub-redes**: a rede principal com 6 pessoas,
> e os dois grupos isolados com 2 pessoas cada.
>
> E na **Missão 6**, o ranking de influência mostra Ana e Eduardo no topo, cada um com 3 conexões —
> são as pessoas mais centrais da rede."

> "E pra mostrar a parte interativa: vou voltar ao menu e escolher a opção **2**, digitar dois
> nomes, e também testar um nome que não existe pra mostrar que o programa trata o erro sem quebrar."

---

## 6) Encerramento (15–30 segundos)

> "E é isso! A gente resolveu as cinco missões usando duas estratégias de busca diferentes —
> **BFS** quando importa o número de passos, e **Dijkstra** quando importa o custo ponderado —
> tudo apoiado numa estrutura de **grafo genérica, com listas de adjacência**. O código está no
> repositório público do GitHub no link da descrição. Valeu!"

---

### ✅ Checklist antes de gravar
- [ ] Ter o **JDK instalado** e ter testado `javac`/`java` ANTES de gravar (pra não travar no vídeo).
- [ ] Deixar os arquivos abertos no VS Code, com fonte grande (Ctrl + "+") pra dar pra ler.
- [ ] Falar o **nome e RGM** de todos os integrantes logo no começo.
- [ ] Mostrar o programa **rodando de verdade** na tela.
- [ ] Depois de gravar: subir no YouTube como **público ou não listado** e colar o link no README.
