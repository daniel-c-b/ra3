class Utilidade {
	static String formataNumero(double v, int casas) {
		int escala = 1;
		for (int i = 0; i < casas; i++) {
			escala = escala * 10;
		}
		int total = (int) (v * escala + 0.5);
		int inteira = total / escala;
		int fracao = total % escala;
		String zeros = "";
		int limite = escala / 10;
		while (limite > 1 && fracao < limite) {
			zeros = zeros + "0";
			limite = limite / 10;
		}
		if (casas == 0) {
			return "" + inteira;
		}
		return inteira + "." + zeros + fracao;
	}
}
