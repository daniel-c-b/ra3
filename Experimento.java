import java.io.IOException;

public class Experimento {
	static final int EXECUCOES = 3;
	static final String[] TIPOS_DE_HASH = {"FNV1a", "Polinomial 31", "djb2"};

	static final int COLISOES = 0;
	static final int SONDAGENS_SUCESSO = 1;
	static final int SONDAGENS_FALHA = 2;
	static final int PIOR_CASO = 3;
	static final int TEMPO_INSERCAO = 4;
	static final int TEMPO_BUSCA = 5;

	static void medir(char[][] chaves, int[] tamanhos, int n, int funcao, double[] resultado) {
		double somaTempoInsercao = 0;
		double somaTempoBusca = 0;
		char[] estendida = new char[LeitorCSV.MAX_TITULO + 2];

		for (int execucao = 0; execucao < EXECUCOES; execucao++) {
			TabelaHash tabela = new TabelaHash(Dados.CAPACIDADE, funcao);

			// mede inserçao
			long inicio = System.nanoTime();
			for (int i = 0; i < n; i++) {
				tabela.inserir(chaves[i], tamanhos[i], i);
			}
			long fim = System.nanoTime();
			somaTempoInsercao += (fim - inicio) / 1000000.0;

			// mede buscas com sucesso
			double totalSucesso = 0;
			inicio = System.nanoTime();
			for (int i = 0; i < n; i++) {
				tabela.buscar(chaves[i], tamanhos[i]);
				totalSucesso += tabela.sondagemUltimaOp;
			}
			fim = System.nanoTime();
			somaTempoBusca += (fim - inicio) / 1000000.0;

			// mede buscas sem sucesso
			double totalFalha = 0;
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < tamanhos[i]; j++) {
					estendida[j] = chaves[i][j];
				}
				estendida[tamanhos[i]] = (char) 1;
				tabela.buscar(estendida, tamanhos[i] + 1);
				totalFalha += tabela.sondagemUltimaOp;
			}

			// guarda resultados em um array
			resultado[COLISOES] = tabela.colisoes;
			resultado[SONDAGENS_SUCESSO] = totalSucesso / n;
			resultado[SONDAGENS_FALHA] = totalFalha / n;
			resultado[PIOR_CASO] = tabela.piorCaso;
		}
		resultado[TEMPO_INSERCAO] = somaTempoInsercao / EXECUCOES;
		resultado[TEMPO_BUSCA] = somaTempoBusca / EXECUCOES;
	}

	static void executarConjunto(String nomeConjunto, char[][] chaves, int[] tamanhos, int n, Escritor arquivo) throws IOException {
		String cabecalho = "Chaves: " + nomeConjunto + " (" + n + " distintas), m = " + Dados.CAPACIDADE;
		String colunas = "Funcao | Colisoes | Sond. sucesso | Sond. falha | Pior caso | Insercao em ms | Busca em ms";
		System.out.println(cabecalho);
		System.out.println(colunas);
		arquivo.adicionarString(cabecalho + "\n" + colunas + "\n");

		double[] resultado = new double[6];
		for (int f = 0; f < 3; f++) {
			medir(chaves, tamanhos, n, f, resultado);
			String linha = TIPOS_DE_HASH[f]
					+ " | " + (int) resultado[COLISOES]
					+ " | " + Utilidade.formataNumero(resultado[SONDAGENS_SUCESSO], 4)
					+ " | " + Utilidade.formataNumero(resultado[SONDAGENS_FALHA], 4)
					+ " | " + (int) resultado[PIOR_CASO]
					+ " | " + Utilidade.formataNumero(resultado[TEMPO_INSERCAO], 2)
					+ " | " + Utilidade.formataNumero(resultado[TEMPO_BUSCA], 2);
			System.out.println(linha);
			arquivo.adicionarString(linha + "\n");
		}
		System.out.println();
		arquivo.adicionarString("\n");
	}

	static void executar(Dados dados, String caminhoSaida) throws IOException {
		Escritor arquivo = new Escritor(caminhoSaida);
		executarConjunto("User_ids", dados.idsUsuarios, dados.tamIds, dados.totalUsuarios, arquivo);
		executarConjunto("Titles", dados.titulos, dados.tamTitulos, dados.totalTitulos, arquivo);
		arquivo.fechar();
	}
}
