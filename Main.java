import java.io.FileReader;
import java.io.IOException;

public class Main {
	static void imprimirTexto(char[] texto, int tamanho) {
		for (int i = 0; i < tamanho; i++) {
			System.out.print(texto[i]);
		}
	}
	
	// RF6
	static void consultar(Dados dados, String caminho) throws IOException {
		FileReader arquivo;
		try {
			arquivo = new FileReader(caminho);
		} catch (IOException e) {
			System.out.println("Arquivo nao existe: " + caminho);
			return;
		}
		char[] bloco = new char[4096];
		char[] linha = new char[Dados.CAPACIDADE];
		int tamLinha = 0;
		int lidos = arquivo.read(bloco, 0, 4096);
		while (lidos > 0 || tamLinha > 0) {
			int fimArquivo = 0;
			if (lidos <= 0) {
				fimArquivo = 1;
			}
			int i = 0;
			while (i < lidos || fimArquivo == 1) {
				char c = '\n';
				if (fimArquivo == 0) {
					c = bloco[i];
					i++;
				}
				if (c == '\n') {
					if (tamLinha > 0) {
						responderConsultaUsuarios(dados, linha, tamLinha);
						tamLinha = 0;
					}
					fimArquivo = 0;
					if (lidos <= 0) {
						break;
					}
				} else if (c != '\r' && c != ' ' && tamLinha < Dados.CAPACIDADE) {
					linha[tamLinha] = c;
					tamLinha++;
				}
			}
			if (lidos <= 0) {
				break;
			}
			lidos = arquivo.read(bloco, 0, 4096);
		}
		arquivo.close();
	}

	static void responderConsultaUsuarios(Dados dados, char[] id, int tamanho) {
		Nodo nodo = dados.tabelaUsuarios.buscar(id, tamanho);
		imprimirTexto(id, tamanho);
		if (nodo == null) {
			System.out.println(" | Usuario nao encontrado");
			return;
		}
		int u = nodo.indice;
		int k = dados.melhorLivro[u];
		System.out.print(" | profileName: ");
		if (dados.tamNomes[u] > 0) {
			imprimirTexto(dados.nomes[u], dados.tamNomes[u]);
		} else {
			System.out.print("(vazio)");
		}
		System.out.print(" | Melhor livro: ");
		imprimirTexto(dados.titulos[k], dados.tamTitulos[k]);
		System.out.println("");
	}

	// RF7
	static int[] ranking(Dados dados) {
		int[] topo = new int[10];
		int quantos = 0;
		for (int k = 0; k < dados.totalTitulos; k++) {
			if (dados.quantidade[k] < 100) {
				continue;
			}
			int pos = quantos;
			while (pos > 0 && dados.compararLivros(k, topo[pos - 1]) > 0) {
				pos--;
			}
			if (pos >= 10) {
				continue;
			}
			int ultimo = quantos;
			if (ultimo > 9) {
				ultimo = 9;
			}
			for (int j = ultimo; j > pos; j--) {
				topo[j] = topo[j - 1];
			}
			topo[pos] = k;
			if (quantos < 10) {
				quantos++;
			}
		}
		int[] resultado = new int[quantos + 1];
		resultado[0] = quantos; // posição 0 guarda a quantidade
		for (int i = 0; i < quantos; i++) {
			resultado[i + 1] = topo[i];
		}
		return resultado;
	}

	public static void main(String[] args) throws IOException {
		String caminhoBase = "Books_rating.csv";
		String caminhoConsulta = "user_id.txt";
		try {
			caminhoBase = args[0];
		} catch (ArrayIndexOutOfBoundsException e) {}
		try {
			caminhoConsulta = args[1];
		} catch (ArrayIndexOutOfBoundsException e) {}


	
		Dados dados = new Dados();
		long inicio;
		long fim;
		long tLeitura, tCalculo, tOrdenacao, tGravacao, tConsulta;
		
		// leitura e indexaçao
		System.out.println("Lendo " + caminhoBase + " ...");
		inicio = System.nanoTime();
		dados.carregar(caminhoBase);
		fim = System.nanoTime();
		tLeitura = fim - inicio;
		System.out.println("Linhas lidas: " + dados.linhasLidas
				+ " | aproveitadas: " + dados.totalAvaliacoes
				+ " | descartadas: " + dados.linhasDescartadas
				+ " (sem User_id: " + dados.semUsuario
				+ ", sem Title: " + dados.semTitulo
				+ ", nota invalida: " + dados.notaInvalida + ")");
		System.out.println("Usuarios distintos: " + dados.totalUsuarios
				+ " | Titulos distintos: " + dados.totalTitulos
				+ " | caracteres truncados: " + dados.camposTruncados);

		// melhor livro de cada usuario
		inicio = System.nanoTime();
		dados.calcularMelhores();
		fim = System.nanoTime();
		tCalculo = fim - inicio;

		// ordenaçao (quicksort)
		int[] ordem = new int[dados.totalUsuarios];
		for (int u = 0; u < dados.totalUsuarios; u++) {
			ordem[u] = u;
		}
		Ordenacao ordenacao = new Ordenacao(dados);
		inicio = System.nanoTime();
		ordenacao.ordenar(ordem, 0, dados.totalUsuarios - 1);
		fim = System.nanoTime();
		tOrdenacao = fim - inicio;

		// cria arquivo user_id.txt se nao existir
		try {
			FileReader teste = new FileReader(caminhoConsulta);
			teste.close();
		} catch (IOException e) {
			System.out.println("Criando " + caminhoConsulta + " ...");
			Escritor userIds = new Escritor(caminhoConsulta);
			for (int i = 0; i < dados.totalUsuarios; i++) {
				dados.escreverLinhaUserID(userIds, ordem[i]);
			}
			userIds.fechar();
		}
		
		// grava melhor livro de cada usuario em um arquivo
		inicio = System.nanoTime();
		Escritor saida = new Escritor("usuario_melhor_livro.csv");
		saida.adicionarString("User_id,profileName,Title,media_livro,N_livro,nota_usuario\n");
		for (int u = 0; u < dados.totalUsuarios; u++) {
			dados.escreverLinhaUsuario(saida, u);
		}
		saida.fechar();

		Escritor saidaOrdenada = new Escritor("usuario_melhor_livro_ordenado.csv");
		saidaOrdenada.adicionarString("User_id,profileName,Title,media_livro,N_livro,nota_usuario\n");
		for (int i = 0; i < dados.totalUsuarios; i++) {
			dados.escreverLinhaUsuario(saidaOrdenada, ordem[i]);
		}
		saidaOrdenada.fechar();
		fim = System.nanoTime();
		tGravacao = fim - inicio;

		// RF6
		System.out.println();
		System.out.println("Consultas (" + caminhoConsulta + "):");
		inicio = System.nanoTime();
		consultar(dados, caminhoConsulta);
		fim = System.nanoTime();
		tConsulta = fim - inicio;

		// RF7
		System.out.println();
		System.out.println("Top 10 livros por media (N >= 100):");
		int[] topo = ranking(dados);
		Escritor saidaRanking = new Escritor("ranking.csv");
		saidaRanking.adicionarString("posicao,Title,media_livro,N_livro\n");
		for (int i = 1; i <= topo[0]; i++) {
			int k = topo[i];
			double media = (double) dados.somaNotas[k] / dados.quantidade[k];
			System.out.print("  " + i + ". ");
			imprimirTexto(dados.titulos[k], dados.tamTitulos[k]);
			System.out.println(" - media " + Utilidade.formataNumero(media, 4) + ", N = " + dados.quantidade[k]);
			saidaRanking.adicionarInt(i);
			saidaRanking.adicionarChar(',');
			saidaRanking.adicionarCampo(dados.titulos[k], dados.tamTitulos[k]);
			saidaRanking.adicionarChar(',');
			saidaRanking.adicionarDouble(media, 4);
			saidaRanking.adicionarChar(',');
			saidaRanking.adicionarInt(dados.quantidade[k]);
			saidaRanking.fimLinha();
		}
		saidaRanking.fechar();

		System.out.println();
		System.out.println("Metragem:");
		System.out.println("Leitura e indexacao: " + Utilidade.formataNumero(tLeitura / 1000000.0, 2) + " ms");
		System.out.println("Calculo do melhor livro: " + Utilidade.formataNumero(tCalculo / 1000000.0, 2) + " ms");
		System.out.println("Ordenacao: " + Utilidade.formataNumero(tOrdenacao / 1000000.0, 2) + " ms");
		System.out.println("Gravacao dos arquivos: " + Utilidade.formataNumero(tGravacao / 1000000.0, 2) + " ms");
		System.out.println("Consultas: " + Utilidade.formataNumero(tConsulta / 1000000.0, 2) + " ms");

		// Parte experimental
		System.out.println();
		System.out.println("Experimento com 3 funcoes hash:");
		Experimento.executar(dados, "resultados_experimento.txt");
	}
}
