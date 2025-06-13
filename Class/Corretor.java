public class Corretor {

    //Atributos
    private String nome;
    private String creci;
    private String telefone;

    // Construtor
    public Corretor(String nome, String creci, String telefone) {
        this.nome = nome;
        this.creci = creci;
        this.telefone = telefone;
    }

    // Getter pegar o nome
    public String getNome() {
        return nome;
    }
    // Setter mudar o nome
    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCreci() {
        return creci;
    }
    public void setCreci(String creci) {
        this.creci = creci;
    }

    public String getTelefone() {
        return telefone;
    }
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}
