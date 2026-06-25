import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Grafo genérico, <b>não-direcionado</b> e <b>ponderado</b>, representado por
 * listas de adjacência.
 *
 * <p>Internamente usamos um mapa de adjacências no formato
 * {@code vértice -> (vizinho -> peso)}. Essa escolha de estrutura nos dá:
 * <ul>
 *   <li>consulta do peso de uma aresta em tempo O(1);</li>
 *   <li>iteração eficiente sobre os vizinhos de um vértice;</li>
 *   <li>consumo de memória proporcional ao número de arestas (ideal para
 *       grafos esparsos, como redes sociais).</li>
 * </ul>
 *
 * <p>A classe é <b>genérica</b> ({@code <T>}): pode armazenar qualquer tipo de
 * vértice (nomes, IDs, etc.), desde que {@code T} implemente {@code equals} e
 * {@code hashCode} de forma coerente — para {@code String} isso já é garantido.
 *
 * @param <T> tipo do identificador de cada vértice
 */
public class Grafo<T> {

    /** Mapa de adjacências: vértice -> (vizinho -> peso da aresta). */
    private final Map<T, Map<T, Integer>> adjacencia;

    public Grafo() {
        // LinkedHashMap preserva a ordem de inserção. Isso torna as saídas
        // (componentes conexos, vizinhos, etc.) determinísticas e fáceis de
        // testar, sem custo assintótico adicional.
        this.adjacencia = new LinkedHashMap<>();
    }

    // ---------------------------------------------------------------------
    // Construção do grafo
    // ---------------------------------------------------------------------

    /**
     * Adiciona um vértice isolado ao grafo. Operação idempotente: se o vértice
     * já existir, nada acontece.
     *
     * @param vertice identificador do vértice (não nulo)
     */
    public void adicionarVertice(T vertice) {
        Objects.requireNonNull(vertice, "O vertice nao pode ser nulo.");
        adjacencia.putIfAbsent(vertice, new LinkedHashMap<>());
    }

    /**
     * Cria uma aresta não-direcionada e ponderada entre dois vértices. Os
     * vértices são criados automaticamente caso ainda não existam.
     *
     * <p>Como o grafo é não-direcionado, a aresta é registrada nos dois
     * sentidos: se Ana está conectada a Bruno, Bruno também está conectado a Ana.
     *
     * @param origem  uma das pontas da conexão
     * @param destino a outra ponta da conexão
     * @param peso    intensidade da conexão (afinidade); deve ser positivo
     * @throws IllegalArgumentException se o peso não for positivo ou se
     *                                  {@code origem.equals(destino)} (laço)
     */
    public void adicionarAresta(T origem, T destino, int peso) {
        Objects.requireNonNull(origem, "A origem nao pode ser nula.");
        Objects.requireNonNull(destino, "O destino nao pode ser nulo.");
        if (peso <= 0) {
            throw new IllegalArgumentException(
                    "O peso da conexao deve ser positivo. Recebido: " + peso);
        }
        if (origem.equals(destino)) {
            throw new IllegalArgumentException(
                    "Nao e permitido conectar um vertice a ele mesmo (laco): " + origem);
        }
        adicionarVertice(origem);
        adicionarVertice(destino);
        adjacencia.get(origem).put(destino, peso);
        adjacencia.get(destino).put(origem, peso);
    }

    // ---------------------------------------------------------------------
    // Consultas
    // ---------------------------------------------------------------------

    /** @return {@code true} se o vértice existir no grafo. */
    public boolean contemVertice(T vertice) {
        return adjacencia.containsKey(vertice);
    }

    /** @return {@code true} se houver uma aresta direta entre os dois vértices. */
    public boolean saoVizinhos(T origem, T destino) {
        return contemVertice(origem) && adjacencia.get(origem).containsKey(destino);
    }

    /** @return conjunto imutável com todos os vértices do grafo. */
    public Set<T> getVertices() {
        return Collections.unmodifiableSet(adjacencia.keySet());
    }

    /**
     * Retorna os vizinhos diretos de um vértice junto com o peso de cada aresta.
     *
     * @param vertice vértice a ser consultado
     * @return mapa imutável {@code (vizinho -> peso)}; vazio se não houver conexões
     * @throws NoSuchElementException se o vértice não existir no grafo
     */
    public Map<T, Integer> getVizinhos(T vertice) {
        exigirVertice(vertice);
        return Collections.unmodifiableMap(adjacencia.get(vertice));
    }

    /**
     * @return o peso da aresta entre {@code origem} e {@code destino}.
     * @throws NoSuchElementException se a aresta não existir
     */
    public int getPeso(T origem, T destino) {
        if (!saoVizinhos(origem, destino)) {
            throw new NoSuchElementException(
                    "Nao existe conexao direta entre " + origem + " e " + destino + ".");
        }
        return adjacencia.get(origem).get(destino);
    }

    // ---------------------------------------------------------------------
    // Algoritmo de menor caminho ponderado (Dijkstra)
    // ---------------------------------------------------------------------

    /**
     * Calcula o caminho de <b>menor custo acumulado</b> (menor soma de pesos)
     * entre dois vértices usando o algoritmo de <b>Dijkstra</b>.
     *
     * <p>Dijkstra é a escolha correta aqui porque todos os pesos são positivos.
     * Usamos uma fila de prioridade (min-heap) para sempre expandir primeiro o
     * vértice de menor distância conhecida, resultando em complexidade
     * O((V + E) · log V).
     *
     * @param origem  vértice de partida
     * @param destino vértice de chegada
     * @return um {@link ResultadoCaminho} com a sequência de vértices e o custo
     *         total; se o destino for inalcançável, retorna custo {@code -1} e
     *         caminho vazio (ver {@link ResultadoCaminho#inalcancavel()}).
     * @throws NoSuchElementException se origem ou destino não existirem
     */
    public ResultadoCaminho<T> caminhoMinimo(T origem, T destino) {
        exigirVertice(origem);
        exigirVertice(destino);

        // distancia[v]  = menor custo conhecido de 'origem' ate 'v'.
        // predecessor[v] = de qual vertice chegamos a 'v' no melhor caminho.
        Map<T, Integer> distancia = new HashMap<>();
        Map<T, T> predecessor = new HashMap<>();
        for (T v : adjacencia.keySet()) {
            distancia.put(v, Integer.MAX_VALUE);
        }
        distancia.put(origem, 0);

        // Fila de prioridade ordenada pelo custo acumulado (par vertice/custo).
        PriorityQueue<Map.Entry<T, Integer>> fila =
                new PriorityQueue<>(Comparator.comparingInt(Map.Entry::getValue));
        fila.add(new AbstractMap.SimpleEntry<>(origem, 0));

        while (!fila.isEmpty()) {
            Map.Entry<T, Integer> atual = fila.poll();
            T u = atual.getKey();
            int distU = atual.getValue();

            // Entrada obsoleta: ja encontramos um caminho melhor ate 'u'.
            if (distU > distancia.get(u)) {
                continue;
            }
            // Otimizacao: assim que o destino e finalizado, o custo e o minimo.
            if (u.equals(destino)) {
                break;
            }
            // Relaxamento das arestas que saem de 'u'.
            for (Map.Entry<T, Integer> aresta : adjacencia.get(u).entrySet()) {
                T v = aresta.getKey();
                int novaDistancia = distU + aresta.getValue();
                if (novaDistancia < distancia.get(v)) {
                    distancia.put(v, novaDistancia);
                    predecessor.put(v, u);
                    fila.add(new AbstractMap.SimpleEntry<>(v, novaDistancia));
                }
            }
        }

        if (distancia.get(destino) == Integer.MAX_VALUE) {
            return ResultadoCaminho.inalcancavel();
        }
        return new ResultadoCaminho<>(reconstruirCaminho(predecessor, destino),
                                      distancia.get(destino));
    }

    /** Refaz o caminho do destino até a origem usando o mapa de predecessores. */
    private List<T> reconstruirCaminho(Map<T, T> predecessor, T destino) {
        LinkedList<T> caminho = new LinkedList<>();
        T passo = destino;
        while (passo != null) {
            caminho.addFirst(passo);
            passo = predecessor.get(passo);
        }
        return caminho;
    }

    // ---------------------------------------------------------------------
    // Apoio interno
    // ---------------------------------------------------------------------

    private void exigirVertice(T vertice) {
        if (!contemVertice(vertice)) {
            throw new NoSuchElementException("Vertice inexistente no grafo: " + vertice);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Grafo {\n");
        for (Map.Entry<T, Map<T, Integer>> e : adjacencia.entrySet()) {
            sb.append("  ").append(e.getKey()).append(" -> ").append(e.getValue()).append('\n');
        }
        return sb.append('}').toString();
    }

    // =====================================================================
    // Tipo de retorno do algoritmo de menor caminho
    // =====================================================================

    /**
     * Resultado imutável de uma busca de menor caminho: a sequência ordenada de
     * vértices que forma o caminho e o custo total acumulado.
     *
     * @param <T> tipo dos vértices
     */
    public static final class ResultadoCaminho<T> {

        private final List<T> caminho;
        private final int custo;

        public ResultadoCaminho(List<T> caminho, int custo) {
            // Copia defensiva + imutabilidade: o resultado nao pode ser alterado
            // por quem o recebe.
            this.caminho = Collections.unmodifiableList(new ArrayList<>(caminho));
            this.custo = custo;
        }

        /** Fábrica para o caso de destino inalcançável (custo -1, caminho vazio). */
        public static <T> ResultadoCaminho<T> inalcancavel() {
            return new ResultadoCaminho<>(Collections.<T>emptyList(), -1);
        }

        public List<T> getCaminho() {
            return caminho;
        }

        public int getCusto() {
            return custo;
        }

        /** @return {@code true} se existe um caminho válido entre origem e destino. */
        public boolean isAlcancavel() {
            return custo >= 0 && !caminho.isEmpty();
        }

        @Override
        public String toString() {
            if (!isAlcancavel()) {
                return "Caminho inexistente (custo = -1)";
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < caminho.size(); i++) {
                if (i > 0) {
                    sb.append(" -> ");
                }
                sb.append(caminho.get(i));
            }
            return sb.append(" | custo total = ").append(custo).toString();
        }
    }
}
