import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Aplicação de console do {@link LinkedInAnalyzer}.
 *
 * <p>Monta o cenário sugerido no enunciado e oferece um <b>menu interativo</b>
 * para o usuário executar cada missão informando os nomes, além de uma opção que
 * roda a demonstração completa do cenário de testes de uma só vez.
 *
 * <p>===================================================================
 * <p>INTEGRANTES DO GRUPO:
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
        System.out.println("            LINKEDIN ANALYZER                 ");
        System.out.println("==============================================");

        try (Scanner scanner = new Scanner(System.in)) {
            boolean executando = true;
            while (executando) {
                imprimirMenu(rede);
                String opcao = scanner.nextLine().trim();
                try {
                    executando = processarOpcao(opcao, analyzer, scanner);
                } catch (NoSuchElementException e) {
                    // Nome digitado nao existe na rede: avisa e mantem o programa vivo.
                    System.out.println("  [ERRO] " + e.getMessage());
                }
            }
        }
        System.out.println("\nEncerrando. Ate mais!");
    }

    /**
     * Trata a opção escolhida no menu.
     *
     * @return {@code false} quando o usuário pede para sair; {@code true} caso contrário
     */
    private static boolean processarOpcao(String opcao, LinkedInAnalyzer analyzer, Scanner scanner) {
        switch (opcao) {
            case "1":
                demonstrarSugestaoDeConexoes(analyzer, lerNome(scanner, "Digite o nome da pessoa: "));
                break;
            case "2":
                demonstrarGrauDeSeparacao(analyzer,
                        lerNome(scanner, "Pessoa de origem: "),
                        lerNome(scanner, "Pessoa de destino: "));
                break;
            case "3":
                demonstrarRotaDeMaiorAfinidade(analyzer,
                        lerNome(scanner, "Pessoa de origem: "),
                        lerNome(scanner, "Pessoa de destino: "));
                break;
            case "4":
                demonstrarGruposIsolados(analyzer);
                break;
            case "5":
                demonstrarRankingDeInfluencia(analyzer);
                break;
            case "6":
                rodarDemonstracaoCompleta(analyzer);
                break;
            case "0":
                return false;
            default:
                System.out.println("  Opcao invalida. Escolha um numero do menu.");
        }
        return true;
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

    /**
     * Executa todas as missões de uma vez, com o cenário sugerido no enunciado.
     * (Garante o requisito de "rodar o cenario de testes" em um unico comando.)
     */
    private static void rodarDemonstracaoCompleta(LinkedInAnalyzer analyzer) {
        System.out.println("\n##############################################");
        System.out.println("#       DEMONSTRACAO COMPLETA DO CENARIO      #");
        System.out.println("##############################################");
        demonstrarSugestaoDeConexoes(analyzer, "Ana");
        demonstrarGrauDeSeparacao(analyzer, "Ana", "Fernanda");
        demonstrarGrauDeSeparacao(analyzer, "Ana", "Gabriel"); // sub-redes diferentes -> -1
        demonstrarRotaDeMaiorAfinidade(analyzer, "Ana", "Fernanda");
        demonstrarRotaDeMaiorAfinidade(analyzer, "Ana", "Igor"); // inalcancavel -> -1
        demonstrarGruposIsolados(analyzer);
        demonstrarRankingDeInfluencia(analyzer);
    }

    // ---------------------------------------------------------------------
    // Demonstrações de cada missão
    // ---------------------------------------------------------------------

    private static void demonstrarSugestaoDeConexoes(LinkedInAnalyzer analyzer, String pessoa) {
        imprimirTitulo("MISSAO 2 - Sugestao de conexoes para " + pessoa);
        List<LinkedInAnalyzer.Sugestao> sugestoes = analyzer.sugerirConexoes(pessoa);
        if (sugestoes.isEmpty()) {
            System.out.println("  Nenhuma sugestao encontrada.");
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

    private static void demonstrarRankingDeInfluencia(LinkedInAnalyzer analyzer) {
        imprimirTitulo("MISSAO 6 (BONUS) - Ranking de influencia (mais conectados)");
        List<LinkedInAnalyzer.Influencia> ranking = analyzer.rankingDeInfluencia();
        int posicao = 1;
        for (LinkedInAnalyzer.Influencia influencia : ranking) {
            System.out.println("  " + posicao + "o lugar: " + influencia);
            posicao++;
        }
    }

    // ---------------------------------------------------------------------
    // Apoio de interface (menu e leitura de entrada)
    // ---------------------------------------------------------------------

    private static void imprimirMenu(Grafo<String> rede) {
        System.out.println("\n----------------- MENU -----------------");
        System.out.println("Pessoas na rede: " + rede.getVertices());
        System.out.println("  1 - Sugerir conexoes (amigos de 2o grau)");
        System.out.println("  2 - Grau de separacao entre duas pessoas");
        System.out.println("  3 - Rota de maior afinidade entre duas pessoas");
        System.out.println("  4 - Mapear grupos isolados (sub-redes)");
        System.out.println("  5 - Ranking de influencia");
        System.out.println("  6 - Rodar demonstracao completa do cenario");
        System.out.println("  0 - Sair");
        System.out.print("Escolha uma opcao: ");
    }

    private static String lerNome(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static void imprimirTitulo(String titulo) {
        System.out.println();
        System.out.println("----------------------------------------------");
        System.out.println(titulo);
        System.out.println("----------------------------------------------");
    }
}
