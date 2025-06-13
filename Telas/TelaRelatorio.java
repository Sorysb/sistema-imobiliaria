// Importações necessárias
import Class.Conexao; // Classe responsável por fornecer conexão com o banco de dados
import javax.swing.*; // Componentes Swing para interface gráfica
import java.awt.*; // Gerenciamento visual (layout, fonte, etc.)
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Classe TelaRelatorio
 * Apresenta uma visão geral de dados da imobiliária como total de imóveis, status, visitas, clientes e corretores.
 */
public class TelaRelatorio extends JFrame {

    // Construtor da interface
    public TelaRelatorio() {
        setTitle("Relatórios - Visão Geral");       // Define o título da janela
        setSize(600, 500);                           // Define o tamanho da janela
        setLocationRelativeTo(null);                 // Centraliza a janela na tela
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Fecha apenas essa janela ao clicar em "X"

        // Painel principal com layout em coluna
        JPanel painelPrincipal = new JPanel();
        painelPrincipal.setLayout(new BoxLayout(painelPrincipal, BoxLayout.Y_AXIS)); // Layout vertical
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));   // Margens internas

        // Título da tela
        JLabel labelTitulo = new JLabel("Relatórios - Visão Geral", SwingConstants.CENTER);
        labelTitulo.setFont(new Font("Arial", Font.BOLD, 26));
        labelTitulo.setAlignmentX(Component.CENTER_ALIGNMENT); // Centraliza o título
        painelPrincipal.add(labelTitulo);

        painelPrincipal.add(Box.createVerticalStrut(20)); // Espaço entre título e conteúdo

        // Declaração dos labels onde os dados serão exibidos
        JLabel labelImoveis = new JLabel();
        JLabel labelImoveisStatus = new JLabel();
        JLabel labelClientes = new JLabel();
        JLabel labelVisitas = new JLabel();
        JLabel labelVisitasCanceladas = new JLabel();
        JLabel labelCorretores = new JLabel();

        // Define uma fonte padrão e adiciona os labels no painel
        Font fonte = new Font("Segoe UI", Font.PLAIN, 16);
        for (JLabel label : new JLabel[]{labelImoveis, labelImoveisStatus, labelClientes, labelVisitas, labelVisitasCanceladas, labelCorretores}) {
            label.setFont(fonte);
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            painelPrincipal.add(label);
            painelPrincipal.add(Box.createVerticalStrut(10)); // Espaço entre os labels
        }

        // Chama o método que carrega os dados do banco e atualiza os labels
        carregarDados(labelImoveis, labelImoveisStatus, labelClientes, labelVisitas, labelVisitasCanceladas, labelCorretores);

        // Adiciona o painel principal à janela
        add(painelPrincipal);
        setVisible(true); // Exibe a janela
    }

    /**
     * Método responsável por consultar o banco e preencher os dados nos labels
     */
    private void carregarDados(JLabel labelImoveis, JLabel labelImoveisStatus, JLabel labelClientes, JLabel labelVisitas, JLabel labelVisitasCanceladas, JLabel labelCorretores) {
        try (Connection conn = Conexao.conectar()) {

            // Consultas SQL para obter os contadores
            int totalImoveis       = consultarCount(conn, "SELECT COUNT(*) FROM imovel");
            int totalCasas         = consultarCount(conn, "SELECT COUNT(*) FROM imovel WHERE tipo = 'Casa'");
            int totalApartamentos = consultarCount(conn, "SELECT COUNT(*) FROM imovel WHERE tipo = 'Apartamento'");
            int disponiveis        = consultarCount(conn, "SELECT COUNT(*) FROM imovel WHERE status = 'Disponível'");
            int alugados           = consultarCount(conn, "SELECT COUNT(*) FROM imovel WHERE status = 'Alugado'");
            int vendidos           = consultarCount(conn, "SELECT COUNT(*) FROM imovel WHERE status = 'Vendido'");
            int totalClientes      = consultarCount(conn, "SELECT COUNT(*) FROM cliente");
            int totalVisitas       = consultarCount(conn, "SELECT COUNT(*) FROM visita WHERE status = 'Agendada'");
            int canceladas         = consultarCount(conn, "SELECT COUNT(*) FROM visita WHERE status = 'Cancelada'");
            int totalCorretores    = consultarCount(conn, "SELECT COUNT(*) FROM corretor");

            // Exibição dos dados formatados nos labels
            labelImoveis.setText("• Imóveis cadastrados: " + totalImoveis + " (Casas: " + totalCasas + ", Apts: " + totalApartamentos + ")");
            labelImoveisStatus.setText("• Status: Disponíveis: " + disponiveis + " | Alugados: " + alugados + " | Vendidos: " + vendidos);
            labelClientes.setText("• Clientes cadastrados: " + totalClientes);
            labelVisitas.setText("• Visitas agendadas: " + totalVisitas);
            labelVisitasCanceladas.setText("• Visitas canceladas: " + canceladas);
            labelCorretores.setText("• Corretores ativos: " + totalCorretores);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados: " + e.getMessage());
        }
    }

    /**
     * Método utilitário que executa um SELECT COUNT(*) e retorna o valor
     */
    private int consultarCount(Connection conn, String sql) {
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            System.out.println("Erro na query: " + sql);
        }
        return 0;
    }

    /**
     * Método main para teste isolado da tela
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaRelatorio());
    }
}
