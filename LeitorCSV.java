import java.io.FileReader;
import java.io.IOException;

public class LeitorCSV {
    static final int TAM_BLOCO = 65536;
    static final int MAX_TITULO = 2048;
    static final int MAX_USUARIO = 2048;
    static final int MAX_NOME = 2048;
    static final int MAX_NOTA = 2048;

    FileReader arquivo;
    char[] bloco = new char[TAM_BLOCO];
    int posBloco = 0;
    int tamBloco = 0;

    char[] titulo = new char[MAX_TITULO];
    char[] usuario = new char[MAX_USUARIO];
    char[] nome = new char[MAX_NOME];
    char[] nota = new char[MAX_NOTA];

    int tamTitulo;
    int tamUsuario;
    int tamNome;
    int tamNota;
	
    int camposTruncados = 0;

    public LeitorCSV(String caminho) throws IOException {
        arquivo = new FileReader(caminho);
    }

    void fechar() throws IOException {
        arquivo.close();
    }
	
    int proximoChar() throws IOException {
        if (posBloco >= tamBloco) {
            tamBloco = arquivo.read(bloco, 0, TAM_BLOCO);
            posBloco = 0;
            if (tamBloco <= 0) {
                tamBloco = 0;
                return -1;
            }
        }
        char c = bloco[posBloco];
        posBloco++;
        return c;
    }

    void adicionar(int coluna, char c) {
        if (coluna == 1) {
            if (tamTitulo < MAX_TITULO) { titulo[tamTitulo] = c; tamTitulo++; }
            else { camposTruncados++; }
        } else if (coluna == 3) {
            if (tamUsuario < MAX_USUARIO) { usuario[tamUsuario] = c; tamUsuario++; }
            else { camposTruncados++; }
        } else if (coluna == 4) {
            if (tamNome < MAX_NOME) { nome[tamNome] = c; tamNome++; }
            else { camposTruncados++; }
        } else if (coluna == 6) {
            if (tamNota < MAX_NOTA) { nota[tamNota] = c; tamNota++; }
            else { camposTruncados++; }
        }
    }

    int lerLinha() throws IOException {
        tamTitulo = 0;
        tamUsuario = 0;
        tamNome = 0;
        tamNota = 0;

        int coluna = 0;
        int emAspas = 0;
        int aspaFechou = 0;
        int inicioCampo = 1;
        int vazio = 1;

        for (;;) {
            int c = proximoChar();
            if (c == -1) {
                if (vazio == 1) {
                    return 0;
                }
                return 1;
            }
            char ch = (char) c;

            if (ch == '"') {
                vazio = 0;
                if (emAspas == 1) {
                    emAspas = 0;
                    aspaFechou = 1;
                } else if (aspaFechou == 1) {
                    emAspas = 1;
                    aspaFechou = 0;
                    adicionar(coluna, '"');
                } else if (inicioCampo == 1) {
                    emAspas = 1;
                    aspaFechou = 0;
                } else {
                    adicionar(coluna, '"');
                }
                inicioCampo = 0;
                continue;
            }
            aspaFechou = 0;

            if (emAspas == 0) {
                if (ch == '\r') {
                    continue;
                }
                if (ch == ',') {
                    coluna++;
                    vazio = 0;
                    inicioCampo = 1;
                    continue;
                }
                if (ch == '\n') {
                    if (vazio == 1) {
                        continue;
                    }
                    return 1;
                }
            }
            vazio = 0;
            inicioCampo = 0;
            adicionar(coluna, ch);
        }
    }

    int notaComoInt() {
		// trata notas com .0 no final bem como notas sem .0
        if ((tamNota == 3 && nota[1] == '.' && nota[2] == '0' && nota[0] >= '1' && nota[0] <= '5') || (tamNota == 1 && nota[0] >= '1' && nota[0] <= '5')) {
            return nota[0] - '0';
        }
        return 0;
    }
}
