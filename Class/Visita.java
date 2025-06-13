import java.time.LocalDateTime;

public class Visita {
    private Cliente cliente;
    private Imovel imovel;
    private LocalDateTime dataHora;

    public Visita(Cliente cliente, Imovel imovel, LocalDateTime dataHora) {
        this.cliente = cliente;
        this.imovel = imovel;
        this.dataHora = dataHora;
    }

    public Cliente getCliente() { return cliente; }
    public Imovel getImovel() { return imovel; }
    public LocalDateTime getDataHora() { return dataHora; }
}
