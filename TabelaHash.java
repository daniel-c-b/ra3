public class TabelaHash {
    ListaEncadeada[] listas; 
    int capacidade;
    int funcao;

    int colisoes = 0;
    int piorCaso = 0;
    int sondagemUltimaOp = 0;

    public TabelaHash(int capacidade, int funcao) {
        this.capacidade = capacidade;
        this.funcao = funcao;
        this.listas = new ListaEncadeada[capacidade];
    }

    int calcularHash(char[] chave, int tamanho) {
        if (funcao == 0) {
            return Hashes.hashFnv1a(chave, tamanho);
        } else if (funcao == 1) {
            return Hashes.hashPolinomialBase31(chave, tamanho);
        } else {
            return Hashes.hashdjb2(chave, tamanho);
        }
    }

    void sondagem(int sondagens) {
        sondagemUltimaOp = sondagens;
        if (sondagens > piorCaso) {
            piorCaso = sondagens;
        }
    }

    Nodo buscar(char[] chave, int tamanho) {
		int chaveHashada = calcularHash(chave, tamanho);
		
        ListaEncadeada lista = listas[Hashes.posicaoNaTabela(chaveHashada)];
        if (lista == null) {
            sondagem(0);
            return null;
        }
        Nodo nodo = lista.buscar(chave, tamanho);
        sondagem(lista.ultimaSondagem);
        return nodo;
    }

    boolean inserir(char[] chave, int tamanho, int indice) {
		int chaveHashada = calcularHash(chave, tamanho);
		
        int posicao = Hashes.posicaoNaTabela(chaveHashada);
        if (listas[posicao] == null) {
            listas[posicao] = new ListaEncadeada();
        }
        ListaEncadeada lista = listas[posicao];

        if (lista.inicio != null) {
            colisoes++;
        }

        Nodo existente = lista.buscar(chave, tamanho);
        sondagem(lista.ultimaSondagem);
        if (existente != null) {
            return false;
        }
        lista.inserirNoInicio(chave, tamanho, indice);
        return true;
    }

    void inserirSemBusca(char[] chave, int tamanho, int indice) {
		int chaveHashada = calcularHash(chave, tamanho);
		
        int posicao = Hashes.posicaoNaTabela(chaveHashada);
        if (listas[posicao] == null) {
            listas[posicao] = new ListaEncadeada();
        }
        listas[posicao].inserirNoInicio(chave, tamanho, indice);
    }
}