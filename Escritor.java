import java.io.FileWriter;
import java.io.IOException;

public class Escritor {
    FileWriter arquivo;
	
    char[] linha = new char[65536];
    char[] digitos = new char[16];
	
    int pos = 0;

    public Escritor(String caminho) throws IOException {
        arquivo = new FileWriter(caminho);
    }

    void adicionarChar(char c) {
        linha[pos] = c;
        pos++;
    }

    void adicionarString(String s) throws IOException {
        descarregar();
        arquivo.write(s);
    }

    void adicionarCampo(char[] texto, int tamanho) {
        int precisaAspas = 0;
        for (int i = 0; i < tamanho; i++) {
            char c = texto[i];
            if (c == ',' || c == '"' || c == '\n' || c == '\r') {
                precisaAspas = 1;
            }
        }
        if (precisaAspas == 0) {
            for (int i = 0; i < tamanho; i++) {
                adicionarChar(texto[i]);
            }
            return;
        }
        adicionarChar('"');
        for (int i = 0; i < tamanho; i++) {
            if (texto[i] == '"') {
                adicionarChar('"');
            }
            adicionarChar(texto[i]);
        }
        adicionarChar('"');
    }

    void adicionarInt(int valor) {
        if (valor == 0) {
            adicionarChar('0');
            return;
        }
        int n = 0;
        while (valor > 0) {
            digitos[n] = (char) ('0' + valor % 10);
            valor = valor / 10;
            n++;
        }
        while (n > 0) {
            n--;
            adicionarChar(digitos[n]);
        }
    }

    void adicionarDouble(double v, int casas) {
        int escala = 1;
        for (int i = 0; i < casas; i++) {
            escala = escala * 10;
        }
        int total = (int) (v * escala + 0.5);
        adicionarInt(total / escala);
        if (casas > 0) {
            adicionarChar('.');
            int fracao = total % escala;
            int limite = escala / 10;
            while (limite > 1 && fracao < limite) {
                adicionarChar('0');
                limite = limite / 10;
            }
            adicionarInt(fracao);
        }
    }

    void fimLinha() throws IOException {
        adicionarChar('\n');
        descarregar();
    }

    void descarregar() throws IOException {
        if (pos > 0) {
            arquivo.write(linha, 0, pos);
            pos = 0;
        }
    }

    void fechar() throws IOException {
        descarregar();
        arquivo.close();
    }
}
