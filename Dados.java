import java.io.IOException;

public class Dados {
	static final int CAPACIDADE = 4000000;

	TabelaHash tabelaTitulos = new TabelaHash(CAPACIDADE, 0);
	TabelaHash tabelaUsuarios = new TabelaHash(CAPACIDADE, 0);

	// livros
	char[][] titulos = new char[CAPACIDADE][];
	int[] tamTitulos = new int[CAPACIDADE];
	int[] somaNotas = new int[CAPACIDADE];
	int[] quantidade = new int[CAPACIDADE];
	int totalTitulos = 0;

	// usuarios
	char[][] idsUsuarios = new char[CAPACIDADE][];
	int[] tamIds = new int[CAPACIDADE];
	char[][] nomes = new char[CAPACIDADE][];
	int[] tamNomes = new int[CAPACIDADE];
	int[] melhorLivro = new int[CAPACIDADE];
	int[] notaUsuario = new int[CAPACIDADE];
	int totalUsuarios = 0;

	// avaliacoes
	int[] avUsuario = new int[CAPACIDADE];
	int[] avLivro = new int[CAPACIDADE];
	int[] avNota = new int[CAPACIDADE];
	int totalAvaliacoes = 0;


	static char[] copiar(char[] origem, int tamanho) {
		char[] copia = new char[tamanho];
		for (int i = 0; i < tamanho; i++) {
			copia[i] = origem[i];
		}
		return copia;
	}

	static int compararTexto(char[] a, int tamA, char[] b, int tamB) {
		int menor = tamA;
		if (tamB < menor) {
			menor = tamB;
		}
		for (int i = 0; i < menor; i++) {
			if (a[i] < b[i]) {
				return -1;
			}
			if (a[i] > b[i]) {
				return 1;
			}
		}
		if (tamA < tamB) {
			return -1;
		}
		if (tamA > tamB) {
			return 1;
		}
		return 0;
	}

	// RF3/RF4
	int linhasLidas = 0;
	int linhasDescartadas = 0;
	int semUsuario = 0;
	int semTitulo = 0;
	int notaInvalida = 0;
	int camposTruncados = 0;
	
	void carregar(String caminho) throws IOException {
		LeitorCSV leitor = new LeitorCSV(caminho);
		leitor.lerLinha(); // cabeçalho

		while (leitor.lerLinha() == 1) {
			linhasLidas++;
			int nota = leitor.notaComoInt();
			int invalida = 0;
			if (leitor.tamUsuario == 0) { semUsuario++; invalida = 1; }
			if (leitor.tamTitulo == 0) { semTitulo++; invalida = 1; }
			if (nota == 0) { notaInvalida++; invalida = 1; }
			if (invalida == 1) {
				linhasDescartadas++;
				continue;
			}
			if (totalAvaliacoes >= CAPACIDADE) {
				throw new IOException("Mais avaliações que o limite de capacidade");
			}

			int k;
			Nodo nodoLivro = tabelaTitulos.buscar(leitor.titulo, leitor.tamTitulo);
			if (nodoLivro == null) {
				if (totalTitulos >= CAPACIDADE) {
					throw new IOException("Mais títulos que o limite de capacidade");
				}
				char[] copia = copiar(leitor.titulo, leitor.tamTitulo);
				k = totalTitulos;
				titulos[k] = copia;
				tamTitulos[k] = leitor.tamTitulo;
				tabelaTitulos.inserirSemBusca(copia, leitor.tamTitulo, k);
				totalTitulos++;
			} else {
				k = nodoLivro.indice;
			}
			somaNotas[k] += nota;
			quantidade[k]++;

			int u;
			Nodo nodoUsuario = tabelaUsuarios.buscar(leitor.usuario, leitor.tamUsuario);
			if (nodoUsuario == null) {
				if (totalUsuarios >= CAPACIDADE) {
					throw new IOException("Mais usuários que o limite de capacidade");
				}
				char[] copia = copiar(leitor.usuario, leitor.tamUsuario);
				u = totalUsuarios;
				idsUsuarios[u] = copia;
				tamIds[u] = leitor.tamUsuario;
				nomes[u] = null;
				tamNomes[u] = 0;
				tabelaUsuarios.inserirSemBusca(copia, leitor.tamUsuario, u);
				totalUsuarios++;
			} else {
				u = nodoUsuario.indice;
			}
			
			if (tamNomes[u] == 0 && leitor.tamNome > 0) {
				nomes[u] = copiar(leitor.nome, leitor.tamNome);
				tamNomes[u] = leitor.tamNome;
			}

			avUsuario[totalAvaliacoes] = u;
			avLivro[totalAvaliacoes] = k;
			avNota[totalAvaliacoes] = nota;
			totalAvaliacoes++;
		}
		camposTruncados = leitor.camposTruncados;
		leitor.fechar();
	}

	int compararLivros(int a, int b) {
		if (a == b) {
			return 0;
		}
		double esquerda = (double) somaNotas[a] * quantidade[b];
		double direita = (double) somaNotas[b] * quantidade[a];
		if (esquerda > direita) {
			return 1;
		}
		if (esquerda < direita) {
			return -1;
		}
		if (quantidade[a] > quantidade[b]) {
			return 1;
		}
		if (quantidade[a] < quantidade[b]) {
			return -1;
		}
		int c = compararTexto(titulos[a], tamTitulos[a], titulos[b], tamTitulos[b]);
		if (c < 0) {
			return 1; 
		}
		if (c > 0) {
			return -1;
		}
		return 0;
	}

	void calcularMelhores() {
		for (int u = 0; u < totalUsuarios; u++) {
			melhorLivro[u] = -1;
			notaUsuario[u] = 0;
		}
		for (int i = 0; i < totalAvaliacoes; i++) {
			int u = avUsuario[i];
			int k = avLivro[i];
			int nota = avNota[i];
			int atual = melhorLivro[u];
			if (atual == -1) {
				melhorLivro[u] = k;
				notaUsuario[u] = nota;
			} else if (atual == k) {
				if (nota > notaUsuario[u]) {
					notaUsuario[u] = nota;
				}
			} else if (compararLivros(k, atual) > 0) {
				melhorLivro[u] = k;
				notaUsuario[u] = nota;
			}
		}
	}

	int compararUsuarios(int u, int v) {
		int a = melhorLivro[u];
		int b = melhorLivro[v];
		if (a != b) {
			double esquerda = (double) somaNotas[a] * quantidade[b];
			double direita = (double) somaNotas[b] * quantidade[a];
			if (esquerda > direita) {
				return -1;
			}
			if (esquerda < direita) {
				return 1;
			}
			if (quantidade[a] > quantidade[b]) {
				return -1;
			}
			if (quantidade[a] < quantidade[b]) {
				return 1;
			}
		}
		return compararTexto(idsUsuarios[u], tamIds[u], idsUsuarios[v], tamIds[v]);
	}

	void escreverLinhaUsuario(Escritor saida, int u) throws IOException {
		int k = melhorLivro[u];
		saida.adicionarCampo(idsUsuarios[u], tamIds[u]);
		saida.adicionarChar(',');
		if (tamNomes[u] > 0) {
			saida.adicionarCampo(nomes[u], tamNomes[u]);
		}
		saida.adicionarChar(',');
		saida.adicionarCampo(titulos[k], tamTitulos[k]);
		saida.adicionarChar(',');
		saida.adicionarDouble((double) somaNotas[k] / quantidade[k], 4);
		saida.adicionarChar(',');
		saida.adicionarInt(quantidade[k]);
		saida.adicionarChar(',');
		saida.adicionarInt(notaUsuario[u]);
		saida.fimLinha();
	}

	void escreverLinhaUserID(Escritor saida, int u) throws IOException {
		saida.adicionarCampo(idsUsuarios[u], tamIds[u]);
		saida.fimLinha();
	}
}
