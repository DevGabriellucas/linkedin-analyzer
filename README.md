# LinkedIn Analyzer 🚀

Motor de análises e recomendações para uma rede social profissional (estilo LinkedIn),
construído sobre uma estrutura de **grafos**. Projeto final da disciplina de Estrutura de Dados.

A rede é modelada como um **grafo não-direcionado e ponderado**:

- **Vértices:** perfis das pessoas.
- **Arestas:** conexões de amizade/trabalho (se A está conectado a B, B está conectado a A).
- **Pesos:** intensidade da conexão (afinidade). Quanto **menor** o peso, **maior** a proximidade
  (peso 1 = trabalham muito próximos; peso alto = quase não interagem).

---

## 👥 Integrantes do grupo

| Nome completo | RGM |
|---|---|
| Gabriel Lucas de Araujo Bandeira | 38821672 |
| Marcos Manoel | 38490137 |
| Lucas Orange | 38609606 |

---

## 📋 Funcionalidades (as 5 missões)

| # | Funcionalidade | Método | Algoritmo |
|---|---|---|---|
| 1 | Construtor da análise (recebe e guarda o grafo) | `LinkedInAnalyzer(Grafo<String>)` | — |
| 2 | Sugestão de conexões (amigos de 2º grau) | `sugerirConexoes(String)` | Varredura de vizinhos |
| 3 | Grau de separação (menor nº de passos) | `grauDeSeparacao(String, String)` | **BFS** |
| 4 | Rota e custo de maior afinidade | `rotaDeMaiorAfinidade(String, String)` | **Dijkstra** |
| 5 | Mapear grupos isolados (sub-redes) | `mapearGruposIsolados()` | Componentes conexos (BFS) |
| 6 ⭐ | Ranking de influência (bônus) | `rankingDeInfluencia()` | Centralidade de grau |

> **Por que dois algoritmos diferentes?**
> A Missão 3 usa **BFS** porque o que importa é o número de saltos (todas as arestas valem 1 passo).
> A Missão 4 usa **Dijkstra** porque o que importa é a soma dos pesos. O caminho com menos
> saltos **nem sempre** é o de menor custo ponderado.

---

## 🗂️ Estrutura do projeto

```
.
├── Grafo.java              # Grafo genérico, não-direcionado e ponderado + Dijkstra
├── LinkedInAnalyzer.java   # As 5 missões (cérebro das análises)
├── LinkedInApp.java        # Classe main com o cenário de testes
└── README.md
```

---

## ▶️ Como compilar e executar

Pré-requisito: **JDK 17+** instalado (`java -version` deve responder).

```bash
javac Grafo.java LinkedInAnalyzer.java LinkedInApp.java
java LinkedInApp
```

A aplicação abre um **menu interativo** no console:

```
----------------- MENU -----------------
  1 - Sugerir conexoes (amigos de 2o grau)
  2 - Grau de separacao entre duas pessoas
  3 - Rota de maior afinidade entre duas pessoas
  4 - Mapear grupos isolados (sub-redes)
  5 - Ranking de influencia
  6 - Rodar demonstracao completa do cenario
  0 - Sair
```

> A opção **6** roda todas as missões com o cenário sugerido de uma só vez.
> As opções **1, 2 e 3** pedem os nomes das pessoas. Nomes inexistentes são
> tratados com uma mensagem de erro, sem encerrar o programa.

---

## 🧪 Cenário de testes

**Rede principal:** Ana, Bruno, Carlos, Daniela, Eduardo, Fernanda
**Grupos isolados:** Gabriel ↔ Hugo · Igor ↔ Juliana

Conexões (pesos):

| Conexão | Peso |
|---|---|
| Ana ↔ Bruno | 1 |
| Ana ↔ Carlos | 2 |
| Ana ↔ Daniela | 8 |
| Bruno ↔ Eduardo | 1 |
| Carlos ↔ Eduardo | 1 |
| Daniela ↔ Fernanda | 5 |
| Eduardo ↔ Fernanda | 1 |
| Gabriel ↔ Hugo | 1 |
| Igor ↔ Juliana | 1 |

---

## 📤 Saída esperada

```
----------------------------------------------
MISSAO 2 - Sugestao de conexoes para Ana
----------------------------------------------
  - Eduardo (2 amigos em comum)
  - Fernanda (1 amigo em comum)

----------------------------------------------
MISSAO 3 - Grau de separacao entre Ana e Fernanda
----------------------------------------------
  Ana e Fernanda estao a 2 passo(s) de distancia.

----------------------------------------------
MISSAO 3 - Grau de separacao entre Ana e Gabriel
----------------------------------------------
  Nao ha conexao entre Ana e Gabriel (resultado: -1).

----------------------------------------------
MISSAO 4 - Rota de maior afinidade entre Ana e Fernanda
----------------------------------------------
  Melhor rota: [Ana, Bruno, Eduardo, Fernanda]
  Custo total (afinidade acumulada): 3

----------------------------------------------
MISSAO 4 - Rota de maior afinidade entre Ana e Igor
----------------------------------------------
  Perfis inalcancaveis. Caminho: [] | custo: -1

----------------------------------------------
MISSAO 5 - Grupos isolados (sub-redes / componentes conexos)
----------------------------------------------
  Total de sub-redes encontradas: 3
  Sub-rede 1: [Ana, Bruno, Carlos, Daniela, Eduardo, Fernanda]
  Sub-rede 2: [Gabriel, Hugo]
  Sub-rede 3: [Igor, Juliana]

----------------------------------------------
MISSAO 6 (BONUS) - Ranking de influencia (mais conectados)
----------------------------------------------
  1o lugar: Ana (3 conexoes)
  2o lugar: Eduardo (3 conexoes)
  3o lugar: Bruno (2 conexoes)
  ...
```

> 💡 Repare na Missão 4: a rota mais curta em **saltos** seria `Ana → Daniela → Fernanda`
> (custo 8 + 5 = **13**). Mas o Dijkstra encontra `Ana → Bruno → Eduardo → Fernanda`
> (custo 1 + 1 + 1 = **3**), provando que menos passos ≠ maior afinidade.

---

## 🎥 Vídeo explicativo

[Link do vídeo no YouTube] _(adicionar antes da entrega — público ou não listado)_
