import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

/**
 * Motor de análises e recomendações para uma rede social profissional
 * (estilo LinkedIn) modelada como um {@link Grafo} não-direcionado e ponderado.
 *
 * <p>Os vértices são os nomes das pessoas e o peso de cada aresta representa a
 * <b>afinidade</b> entre elas: quanto menor o peso, maior a proximidade.
 *
 * <p>Esta classe não modifica a rede; ela apenas a consulta. Por isso recebe o
 * grafo já montado no construtor e o mantém como referência somente leitura.
 */
public class LinkedInAnalyzer {

    private final Grafo<String> rede;

    // =====================================================================
    // Missão 1 — Construtor da Análise
    // =====================================================================

    /**
     * @param rede instância já populada da rede social (não nula)
     */
    public LinkedInAnalyzer(Grafo<String> rede) {
        this.rede = Objects.requireNonNull(rede, "A rede (grafo) nao pode ser nula.");
    }

    // =====================================================================
    // Missão 2 — Sugestão de Conexões (amigos de 2º grau)
    // =====================================================================

    /**
     * Sugere novos contatos com base em "amigos de amigos": pessoas a dois
     * passos de distância com as quais o usuário ainda não tem conexão direta.
     *
     * <p>Regras aplicadas:
     * <ol>
     *   <li>contatos de 1º grau (já conectados) não são sugeridos;</li>
     *   <li>o próprio usuário nunca é sugerido a si mesmo;</li>
     *   <li>o resultado é ordenado de forma decrescente pela quantidade de
     *       amigos em comum (desempate alfabético, para saída determinística).</li>
     * </ol>
     *
     * @param pessoa nome do usuário para quem queremos gerar sugestões
     * @return lista de {@link Sugestao} ordenada do maior para o menor número de
     *         amigos em comum (vazia se não houver candidatos)
     * @throws NoSuchElementException se a pessoa não existir na rede
     */
    public List<Sugestao> sugerirConexoes(String pessoa) {
        exigirPessoa(pessoa);

        Set<String> contatosDiretos = rede.getVizinhos(pessoa).keySet();

        // candidato -> quantidade de amigos em comum com 'pessoa'.
        Map<String, Integer> amigosEmComum = new LinkedHashMap<>();
        for (String amigo : contatosDiretos) {
            for (String amigoDoAmigo : rede.getVizinhos(amigo).keySet()) {
                if (amigoDoAmigo.equals(pessoa)) {
                    continue; // Regra 2: nao sugerir o proprio usuario.
                }
                if (contatosDiretos.contains(amigoDoAmigo)) {
                    continue; // Regra 1: ja e contato de 1o grau.
                }
                // Cada 'amigo' (contato direto) que tambem conhece o candidato
                // representa exatamente um amigo em comum.
                amigosEmComum.merge(amigoDoAmigo, 1, Integer::sum);
            }
        }

        List<Sugestao> sugestoes = new ArrayList<>();
        for (Map.Entry<String, Integer> entrada : amigosEmComum.entrySet()) {
            sugestoes.add(new Sugestao(entrada.getKey(), entrada.getValue()));
        }
        // Regra 3: mais amigos em comum primeiro; empate resolvido pelo nome.
        sugestoes.sort(Comparator.comparingInt(Sugestao::getAmigosEmComum).reversed()
                .thenComparing(Sugestao::getNome));
        return sugestoes;
    }

    // =====================================================================
    // Missão 3 — Grau de Separação (menor número de passos)
    // =====================================================================

    /**
     * Calcula o grau de separação entre duas pessoas, isto é, o <b>menor número
     * de conexões (saltos)</b> necessárias para ir de uma à outra.
     *
     * <p>Usamos uma <b>BFS (busca em largura)</b>, e não Dijkstra, porque aqui
     * o que importa é a quantidade de passos — todas as arestas valem "1 salto",
     * independentemente do peso. A BFS encontra o caminho com menos arestas em
     * O(V + E).
     *
     * @param origem  nome da pessoa de partida
     * @param destino nome da pessoa de chegada
     * @return número de passos (1 = contato direto, 2 = amigo de amigo, ...),
     *         {@code 0} se origem e destino forem a mesma pessoa, ou {@code -1}
     *         se não houver caminho entre elas
     * @throws NoSuchElementException se origem ou destino não existirem
     */
    public int grauDeSeparacao(String origem, String destino) {
        exigirPessoa(origem);
        exigirPessoa(destino);
        if (origem.equals(destino)) {
            return 0;
        }

        Map<String, Integer> nivel = new LinkedHashMap<>();
        Queue<String> fila = new LinkedList<>();
        nivel.put(origem, 0);
        fila.add(origem);

        while (!fila.isEmpty()) {
            String atual = fila.poll();
            int distanciaAtual = nivel.get(atual);
            for (String vizinho : rede.getVizinhos(atual).keySet()) {
                if (nivel.containsKey(vizinho)) {
                    continue; // ja visitado
                }
                if (vizinho.equals(destino)) {
                    return distanciaAtual + 1;
                }
                nivel.put(vizinho, distanciaAtual + 1);
                fila.add(vizinho);
            }
        }
        return -1; // perfis em sub-redes diferentes (sem conexao)
    }

    // =====================================================================
    // Missão 4 — Rota e Custo de Maior Afinidade
    // =====================================================================

    /**
     * Encontra a rota de <b>maior afinidade</b> entre duas pessoas, ou seja, o
     * caminho com a <b>menor soma de pesos</b>, e o respectivo custo total.
     *
     * <p>Delega ao algoritmo de Dijkstra implementado na classe {@link Grafo}.
     * Repare que esta rota pode ter mais passos que o grau de separação
     * (Missão 3): o caminho com menos saltos nem sempre é o de menor custo
     * ponderado.
     *
     * @param origem  nome da pessoa de partida
     * @param destino nome da pessoa de chegada
     * @return resultado com a sequência de nomes e o custo acumulado; caso sejam
     *         inalcançáveis, custo {@code -1} e caminho vazio
     * @throws NoSuchElementException se origem ou destino não existirem
     */
    public Grafo.ResultadoCaminho<String> rotaDeMaiorAfinidade(String origem, String destino) {
        exigirPessoa(origem);
        exigirPessoa(destino);
        return rede.caminhoMinimo(origem, destino);
    }

    // =====================================================================
    // Missão 5 — Mapear Grupos Isolados (componentes conexos)
    // =====================================================================

    /**
     * Identifica todas as sub-redes da rede social, ou seja, os
     * <b>componentes conexos</b> do grafo: grupos de pessoas conectadas entre si
     * mas totalmente isoladas dos demais grupos.
     *
     * <p>Percorremos cada vértice ainda não visitado e expandimos toda a sua
     * componente com uma BFS. Complexidade total: O(V + E).
     *
     * @return lista de sub-redes; cada sub-rede é a lista de nomes que a compõem
     */
    public List<List<String>> mapearGruposIsolados() {
        Set<String> visitados = new HashSet<>();
        List<List<String>> subRedes = new ArrayList<>();

        for (String inicio : rede.getVertices()) {
            if (visitados.contains(inicio)) {
                continue;
            }
            // Nova componente: explora tudo que e alcancavel a partir de 'inicio'.
            List<String> componente = new ArrayList<>();
            Queue<String> fila = new LinkedList<>();
            visitados.add(inicio);
            fila.add(inicio);

            while (!fila.isEmpty()) {
                String atual = fila.poll();
                componente.add(atual);
                for (String vizinho : rede.getVizinhos(atual).keySet()) {
                    if (visitados.add(vizinho)) { // add() retorna true se era novo
                        fila.add(vizinho);
                    }
                }
            }
            subRedes.add(componente);
        }
        return subRedes;
    }

    // =====================================================================
    // Missão 6 (bônus) — Ranking de Influência (centralidade de grau)
    // =====================================================================

    /**
     * Gera o ranking de influência da rede com base na <b>centralidade de grau</b>:
     * a métrica mais simples de influência em redes sociais. Quanto mais conexões
     * diretas uma pessoa tem, mais influente/central ela é considerada.
     *
     * <p>Percorremos todos os vértices e contamos o número de vizinhos (grau) de
     * cada um. Complexidade O(V).
     *
     * @return lista de {@link Influencia} ordenada do mais influente para o menos
     *         influente (desempate alfabético, para saída determinística)
     */
    public List<Influencia> rankingDeInfluencia() {
        List<Influencia> ranking = new ArrayList<>();
        for (String pessoa : rede.getVertices()) {
            int conexoes = rede.getVizinhos(pessoa).size();
            ranking.add(new Influencia(pessoa, conexoes));
        }
        // Mais conexoes primeiro; empate resolvido pelo nome.
        ranking.sort(Comparator.comparingInt(Influencia::getNumeroDeConexoes).reversed()
                .thenComparing(Influencia::getNome));
        return ranking;
    }

    // ---------------------------------------------------------------------
    // Apoio interno
    // ---------------------------------------------------------------------

    private void exigirPessoa(String pessoa) {
        Objects.requireNonNull(pessoa, "O nome da pessoa nao pode ser nulo.");
        if (!rede.contemVertice(pessoa)) {
            throw new NoSuchElementException("Pessoa inexistente na rede: " + pessoa);
        }
    }

    // =====================================================================
    // Tipo de retorno da Missão 2
    // =====================================================================

    /**
     * Representa uma sugestão de conexão: o nome da pessoa sugerida e quantos
     * amigos em comum ela tem com o usuário consultado. Imutável.
     */
    public static final class Sugestao {

        private final String nome;
        private final int amigosEmComum;

        public Sugestao(String nome, int amigosEmComum) {
            this.nome = nome;
            this.amigosEmComum = amigosEmComum;
        }

        public String getNome() {
            return nome;
        }

        public int getAmigosEmComum() {
            return amigosEmComum;
        }

        @Override
        public String toString() {
            String plural = amigosEmComum == 1 ? "amigo em comum" : "amigos em comum";
            return nome + " (" + amigosEmComum + " " + plural + ")";
        }
    }

    // =====================================================================
    // Tipo de retorno da Missão 6 (bônus)
    // =====================================================================

    /**
     * Representa a posição de uma pessoa no ranking de influência: o nome e o
     * número de conexões diretas (grau). Imutável.
     */
    public static final class Influencia {

        private final String nome;
        private final int numeroDeConexoes;

        public Influencia(String nome, int numeroDeConexoes) {
            this.nome = nome;
            this.numeroDeConexoes = numeroDeConexoes;
        }

        public String getNome() {
            return nome;
        }

        public int getNumeroDeConexoes() {
            return numeroDeConexoes;
        }

        @Override
        public String toString() {
            String plural = numeroDeConexoes == 1 ? "conexao" : "conexoes";
            return nome + " (" + numeroDeConexoes + " " + plural + ")";
        }
    }
}
