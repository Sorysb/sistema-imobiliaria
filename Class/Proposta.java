public class Proposta {
    private Cliente cliente;
    private Imovel imovel;
    private double valorProposta;
    private String status;

    public Proposta(Cliente cliente, Imovel imovel, double valorProposta, String status) {
        this.cliente = cliente;
        this.imovel = imovel;
        this.valorProposta = valorProposta;
        this.status = status;
    }

    public Cliente getCliente() { return cliente; }
    public Imovel getImovel() { return imovel; }
    public double getValorProposta() { return valorProposta; }
    public String getStatus() { return status; }
}
