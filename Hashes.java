class Hashes {
	static int hashFnv1a(char[] chave, int tamanho) {
		int h = 0x811C9DC5; // h0
		for (int i = 0; i < tamanho; i++) {
			h = h ^ chave[i]; // XOR com o caractere
			h = h * 16777619; // multiplica pelo primo FNV
		}
		return h;
	}
	
	
	static int hashPolinomialBase31(char[] chave, int tamanho) {
		int h = 0; // h0
		for (int i = 0; i < tamanho; i++) {
			h = 31 * h + chave[i]; // h(k+1) = 31 * h(k) + c(k+1)
		}
		return h;
	}

	static int hashdjb2(char[] chave, int tamanho) {
		int h = 5381; // h0
		for (int i = 0; i < tamanho; i++) {
			h = 33 * h + chave[i]; // h(k+1) = 33 * h(k) + c(k+1)
		}
		return h;
	}
	
	static int posicaoNaTabela(int valorHashado) {
		int m = 1000003;
		return (h & 0x7FFFFFFF) % m;
	}
}