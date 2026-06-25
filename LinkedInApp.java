import java.util.List;

/**
 * Aplicação de demonstração do {@link LinkedInAnalyzer}.
 *
 * <p>Monta o cenário sugerido no enunciado do projeto e executa as cinco
 * missões, imprimindo os resultados de forma legível no console.
 *
 * <p>===================================================================
 * <p>INTEGRANTES DO GRUPO (preencher antes de entregar):
 * <ul>
 *   <li>Gabriel Lucas de Araujo Bandeira — 38821672</li>
 *   <li>Marcos Manoel — 38490137</li>
 *   <li>Lucas Orange — 38609606</li>
 * </ul>
 * <p>===================================================================
 */
public class LinkedInApp {

    public static void main(String[] args) {
        Grafo<String> rede = montarRede();
        LinkedInAnalyzer analyzer = new LinkedInAnalyzer(rede);

        System.out.println("==============================================");
        System.out.println("        LINKEDIN ANALYZER - DEMONSTRACAO      ");
        System.out.println("==============================================");

        demonstrarSugestaoDeConexoes(analyzer, "Ana");
        demonstrarGrauDeSeparacao(analyzer, "Ana", "Fernanda");
        demonstrarGrauDeSeparacao(analyzer, "Ana", "Gabriel"); // sub-redes diferentes -> -1
        demonstrarRotaDeMaiorAfinidade(analyzer, "Ana", "Fernanda");
        demonstrarRotaDeMaiorAfinidade(analyzer, "Ana", "Igor"); // inalcancavel -> -1
        demonstrarGruposIsolados(analyzer);
    }

    /**
     * Constrói a rede de testes sugerida no enunciado:
     * uma rede principal com seis pessoas e dois grupos isolados.
     */
    private static Grafo<String> montarRede() {
        Grafo<String> rede = new Grafo<>();

        // --- Rede principal ---
        rede.adicionarAresta("Ana", "Bruno", 1);      // trabalham muito proximos
        rede.adicionarAresta("Ana", "Carlos", 2);
        rede.adicionarAresta("Ana", "Daniela", 8);
        rede.adicionarAresta("Bruno", "Eduardo", 1);
        rede.adicionarAresta("Carlos", "Eduardo", 1);
        rede.adicionarAresta("Daniela", "Fernanda", 5);
        rede.adicionarAresta("Eduardo", "Fernanda", 1);

        // --- Grupos isolados ---
        rede.adicionarAresta("Gabriel", "Hugo", 1);   // sub-rede 1
        rede.adicionarAresta("Igor", "Juliana", 1);   // sub-rede 2

        return rede;
    }

    // ---------------------------------------------------------------------
    // Demonstrações de cada missão
    // ---------------------------------------------------------------------

    private static void demonstrarSugestaoDeConexoes(LinkedInAnalyzer analyzer, String pessoa) {
        imprimirTitulo("MISSAO 2 - Sugestao de conexoes para " + pessoa);
        List<LinkedInAnalyzer.Sugestao> sugestoes = analyzer.sugerirConexoes(pessoa);
        if (sugestoes.isEmpty()) {
            System.out.println("Nenhuma sugestao encontrada.");
        } else {
            for (LinkedInAnalyzer.Sugestao sugestao : sugestoes) {
                System.out.println("  - " + sugestao);
            }
        }
    }

    private static void demonstrarGrauDeSeparacao(LinkedInAnalyzer analyzer, String origem, String destino) {
        imprimirTitulo("MISSAO 3 - Grau de separacao entre " + origem + " e " + destino);
        int passos = analyzer.grauDeSeparacao(origem, destino);
        if (passos < 0) {
            System.out.println("  Nao ha conexao entre " + origem + " e " + destino + " (resultado: -1).");
        } else {
            System.out.println("  " + origem + " e " + destino + " estao a " + passos + " passo(s) de distancia.");
        }
    }

    private static void demonstrarRotaDeMaiorAfinidade(LinkedInAnalyzer analyzer, String origem, String destino) {
        imprimirTitulo("MISSAO 4 - Rota de maior afinidade entre " + origem + " e " + destino);
        Grafo.ResultadoCaminho<String> rota = analyzer.rotaDeMaiorAfinidade(origem, destino);
        if (!rota.isAlcancavel()) {
            System.out.println("  Perfis inalcancaveis. Caminho: " + rota.getCaminho() + " | custo: " + rota.getCusto());
        } else {
            System.out.println("  Melhor rota: " + rota.getCaminho());
            System.out.println("  Custo total (afinidade acumulada): " + rota.getCusto());
        }
    }

    private static void demonstrarGruposIsolados(LinkedInAnalyzer analyzer) {
        imprimirTitulo("MISSAO 5 - Grupos isolados (sub-redes / componentes conexos)");
        List<List<String>> subRedes = analyzer.mapearGruposIsolados();
        System.out.println("  Total de sub-redes encontradas: " + subRedes.size());
        int indice = 1;
        for (List<String> grupo : subRedes) {
            System.out.println("  Sub-rede " + indice + ": " + grupo);
            indice++;
        }
    }

    private static void imprimirTitulo(String titulo) {
        System.out.println();
        System.out.println("----------------------------------------------");
        System.out.println(titulo);
        System.out.println("----------------------------------------------");
    }
}
