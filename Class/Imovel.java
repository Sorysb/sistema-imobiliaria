public class Imovel {
    private String tipo;
    private Endereco endereco;
    private double valor;
    private String status;

    public Imovel(String tipo, Endereco endereco, double valor, String status) {
        this.tipo = tipo;
        this.endereco = endereco;
        this.valor = valor;
        this.status = status;
    }
    @Override
    public String toString() {
        return "Tipo: " + tipo +
               ", Endereço: " + endereco +
               ", Valor: R$ " + String.format("%.2f", valor) +
               ", Status: " + status;
    }

    public String getTipo() {
        return tipo;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public double getValor() {
        return valor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
