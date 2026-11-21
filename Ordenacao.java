public class Ordenacao {
	Dados dados;

	public Ordenacao(Dados dados) {
		this.dados = dados;
	}
	
	// Quicksort
	void ordenar(int[] vetor, int comeco, int fim) {
		if (comeco < fim) {
			int indice = pedaco(vetor, comeco, fim);

			ordenar(vetor, comeco, indice-1);
			ordenar(vetor, indice+1, fim);
		}
	}

	private int pedaco(int[] vetor, int comeco, int fim) {
		int pivo = vetor[fim];
		int i = (comeco-1);

		for (int j = comeco; j < fim; j++) {
			if (dados.compararUsuarios(vetor[j], pivo) < 0) {
				i++;

				int x = vetor[i];
				vetor[i] = vetor[j];
				vetor[j] = x;
			}
		}

		int x = vetor[i+1];
		vetor[i+1] = vetor[fim];
		vetor[fim] = x;

		return i+1;
	}
}
