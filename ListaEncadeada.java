public class ListaEncadeada {
	Nodo inicio;
	int tamanho = 0;
	int ultimaSondagem;

	static boolean chavesIguais(char[] a, int tamanhoA, char[] b, int tamanhoB) {
		if (tamanhoA != tamanhoB) {
			return false;
		}
		for (int i = 0; i < tamanhoA; i++) {
			if (a[i] != b[i]) {
				return false;
			}
		}
		return true;
	}

	Nodo buscar(char[] chave, int tamanhoChave) {
		ultimaSondagem = 0;
		Nodo atual = inicio;
		while (atual != null) {
			ultimaSondagem++;
			if (chavesIguais(atual.chave, atual.tamanhoChave, chave, tamanhoChave)) {
				return atual;
			}
			atual = atual.proximo;
		}
		return null;
	}

	void inserirNoInicio(char[] chave, int tamanhoChave, int indice) {
		Nodo novoNodo = new Nodo(chave, tamanhoChave, indice);
		novoNodo.proximo = inicio;
		inicio = novoNodo;
		tamanho++;
	}

	@Override
	public String toString() {
		String texto = "";
		Nodo atual = inicio;
		while (atual != null) {
			for (int i = 0; i < atual.tamanhoChave; i++) {
				texto += atual.chave[i];
			}
			if (atual.proximo != null) {
				texto += " => ";
			}
			atual = atual.proximo;
		}
		return texto;
	}
}