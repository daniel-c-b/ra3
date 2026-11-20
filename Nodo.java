public class Nodo {
	char[] chave;
	int tamanhoChave;
	int indice;
	Nodo proximo;

	public Nodo(char[] chave, int tamanhoChave, int indice) {
		this.chave = chave;
		this.tamanhoChave = tamanhoChave;
		this.indice = indice;
	}
}