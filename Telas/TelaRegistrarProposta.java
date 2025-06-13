// Importa a classe de conexão com o banco de dados
import Class.Conexao;

// Importa bibliotecas gráficas do Swing
import javax.swing.*;

// Importa recursos gráficos de layout
import java.awt.*;

// Importa classes para trabalhar com banco de dados
import java.sql.*;

// Importa data e hora atual
import java.time.LocalDateTime;

/**
 * TelaRegistrarProposta
 * Tela responsável pelo registro de propostas entre clientes e imóveis no sistema.
 */
public class TelaRegistrarProposta extends JFrame {

    // Declaração dos componentes visuais da tela
    private JComboBox<String> comboCliente, comboImovel;
    private JTextField campoValor;
    private JComboBox<String> comboStatus;
    private JButton btnRegistrar;

    // Construtor da tela
    public TelaRegistrarProposta() {
        setTitle("Registrar Proposta");               // Título da janela
        setSize(400, 300);                            // Tamanho da janela
        setLocationRelativeTo(null);                  // Centraliza na tela
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());               // Layout com GridBagLayout

        GridBagConstraints gbc = new GridBagConstraints(); // Define os espaçamentos e posições dos componentes
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        // Título da tela
        JLabel titulo = new JLabel("Registrar Proposta", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        add(titulo, gbc);

        // Campo de seleção de cliente
        gbc.gridwidth = 1;
        gbc.gridy++;
        add(new JLabel("Cliente:"), gbc);
        gbc.gridx = 1;
        comboCliente = new JComboBox<>();
        carregarClientes(); // Carrega os clientes do banco
        add(comboCliente, gbc);

        // Campo de seleção de imóvel
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Imóvel:"), gbc);
        gbc.gridx = 1;
        comboImovel = new JComboBox<>();
        carregarImoveis(); // Carrega os imóveis do banco
        add(comboImovel, gbc);

        // Campo para digitar o valor da proposta
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Valor da Proposta:"), gbc);
        gbc.gridx = 1;
        campoValor = new JTextField();
        add(campoValor, gbc);

        // ComboBox de status da proposta
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Status da Proposta:"), gbc);
        gbc.gridx = 1;
        comboStatus = new JComboBox<>(new String[]{"Enviada", "Aceita", "Recusada"});
        add(comboStatus, gbc);

        // Botão para registrar proposta
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        btnRegistrar = new JButton("Registrar Proposta");
        add(btnRegistrar, gbc);

        // Ação do botão
        btnRegistrar.addActionListener(e -> registrarProposta());

        setVisible(true); // Exibe a tela
    }

    /**
     * Carrega os clientes do banco no ComboBox
     */
    private void carregarClientes() {
        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement("SELECT id, nome FROM cliente");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                comboCliente.addItem(rs.getInt("id") + " - " + rs.getString("nome"));
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar clientes: " + e.getMessage());
        }
    }

    /**
     * Carrega os imóveis do banco no ComboBox
     */
    private void carregarImoveis() {
        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement("SELECT id, tipo FROM imovel");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                comboImovel.addItem(rs.getInt("id") + " - " + rs.getString("tipo"));
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar imóveis: " + e.getMessage());
        }
    }

    /**
     * Registra a proposta no banco de dados
     */
    private void registrarProposta() {
        try {
            // Recupera dados do formulário
            String clienteStr = (String) comboCliente.getSelectedItem();
            String imovelStr = (String) comboImovel.getSelectedItem();
            int clienteId = Integer.parseInt(clienteStr.split(" - ")[0]);
            int imovelId = Integer.parseInt(imovelStr.split(" - ")[0]);
            double valor = Double.parseDouble(campoValor.getText());
            String status = (String) comboStatus.getSelectedItem();

            try (Connection conn = Conexao.conectar()) {

                // Insere proposta
                String sql = "INSERT INTO proposta (id_cliente, id_imovel, valor_proposta, status, data_proposta) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, clienteId);
                ps.setInt(2, imovelId);
                ps.setDouble(3, valor);
                ps.setString(4, status);
                ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                ps.executeUpdate();

                // Se a proposta foi aceita, altera o status do imóvel para "Vendido"
                if ("Aceita".equals(status)) {
                    PreparedStatement updateStatus = conn.prepareStatement("UPDATE imovel SET status = 'Vendido' WHERE id = ?");
                    updateStatus.setInt(1, imovelId);
                    updateStatus.executeUpdate();
                }

                JOptionPane.showMessageDialog(this, "Proposta registrada com sucesso!");
                dispose(); // Fecha a janela

            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao registrar proposta: " + e.getMessage());
        }
    }

    /**
     * Método principal para executar a tela de forma isolada
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaRegistrarProposta());
    }
}
