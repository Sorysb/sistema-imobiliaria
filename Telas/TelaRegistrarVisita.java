// Importação de bibliotecas necessárias
import Class.Conexao; // Classe responsável pela conexão com o banco de dados
import javax.swing.*; // Componentes da interface gráfica
import javax.swing.text.MaskFormatter; // Máscara de formatação para campo de data/hora
import java.awt.*; // Layouts e containers
import java.awt.event.*; // Eventos de ação
import java.sql.*; // Acesso ao banco de dados
import java.time.LocalDateTime; // Trabalhar com datas e horas
import java.time.format.DateTimeFormatter; // Formatar/parsear datas

/**
 * TelaRegistrarVisita
 * Tela gráfica que permite agendar visitas de clientes a imóveis com um corretor específico.
 * Garante que não haja conflitos de horário para o corretor nem para o imóvel.
 */
public class TelaRegistrarVisita extends JFrame {

    // Declaração dos componentes da tela
    private JComboBox<String> comboCliente, comboImovel, comboCorretor;
    private JFormattedTextField campoDataHora;
    private final JButton btnRegistrar;

    // Construtor da interface
    public TelaRegistrarVisita() {
        // Configurações iniciais da janela
        setTitle("Registro de Visita");
        setSize(450, 350);
        setLocationRelativeTo(null); // Centraliza na tela
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout()); // Layout baseado em grids

        GridBagConstraints gbc = new GridBagConstraints(); // Gerencia o posicionamento dos componentes
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        // Título da tela
        JLabel titulo = new JLabel("Registrar Visita", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        add(titulo, gbc);

        // Seção: Cliente
        gbc.gridwidth = 1;
        gbc.gridy++;
        add(new JLabel("Cliente:"), gbc);
        gbc.gridx = 1;
        comboCliente = new JComboBox<>();
        carregarClientes();
        add(comboCliente, gbc);

        // Seção: Imóvel
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Imóvel:"), gbc);
        gbc.gridx = 1;
        comboImovel = new JComboBox<>();
        carregarImoveis();
        add(comboImovel, gbc);

        // Seção: Corretor
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Corretor:"), gbc);
        gbc.gridx = 1;
        comboCorretor = new JComboBox<>();
        carregarCorretores();
        add(comboCorretor, gbc);

        // Seção: Data e Hora (com máscara de formatação)
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Data e Hora (dd/MM/yyyy HH:mm):"), gbc);
        gbc.gridx = 1;
        try {
            MaskFormatter formatter = new MaskFormatter("##/##/#### ##:##");
            formatter.setPlaceholderCharacter('_');
            campoDataHora = new JFormattedTextField(formatter);
            campoDataHora.setColumns(10);
        } catch (Exception e) {
            campoDataHora = new JFormattedTextField();
        }
        add(campoDataHora, gbc);

        // Botão para registrar a visita
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        btnRegistrar = new JButton("Registrar Visita");
        add(btnRegistrar, gbc);

        // Ação do botão
        btnRegistrar.addActionListener(e -> registrarVisita());

        // Exibe a interface
        setVisible(true);
    }

    // Carrega todos os clientes cadastrados no banco
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

    // Carrega todos os imóveis cadastrados
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

    // Carrega todos os corretores cadastrados
    private void carregarCorretores() {
        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement("SELECT id, nome FROM corretor");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                comboCorretor.addItem(rs.getInt("id") + " - " + rs.getString("nome"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar corretores: " + e.getMessage());
        }
    }

    // Extrai o ID do item selecionado no comboBox (formato: "1 - Nome")
    private int extrairId(String item) {
        return Integer.parseInt(item.split(" - ")[0]);
    }

    // Método principal para registrar a visita no banco
    private void registrarVisita() {
        try {
            // Validação de preenchimento da data/hora
            if (campoDataHora.getText().contains("_")) {
                JOptionPane.showMessageDialog(this, "Preencha corretamente a data e hora.");
                return;
            }

            // Obtenção de dados
            int clienteId = extrairId((String) comboCliente.getSelectedItem());
            int imovelId = extrairId((String) comboImovel.getSelectedItem());
            int corretorId = extrairId((String) comboCorretor.getSelectedItem());
            String dataHoraStr = campoDataHora.getText();

            // Conversão para LocalDateTime
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            LocalDateTime dataHora = LocalDateTime.parse(dataHoraStr, formatter);

            try (Connection conn = Conexao.conectar()) {

                // Verifica se o corretor já tem visita nesse horário
                String verifCorretor = "SELECT COUNT(*) FROM visita WHERE id_corretor = ? AND dataVisita = ?";
                try (PreparedStatement psVerif = conn.prepareStatement(verifCorretor)) {
                    psVerif.setInt(1, corretorId);
                    psVerif.setTimestamp(2, Timestamp.valueOf(dataHora));
                    ResultSet rs = psVerif.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(this, "O corretor já tem uma visita marcada neste horário.");
                        return;
                    }
                }

                // Verifica se o imóvel já tem visita nesse horário
                String verifImovel = "SELECT COUNT(*) FROM visita WHERE id_imovel = ? AND dataVisita = ?";
                try (PreparedStatement psVerif = conn.prepareStatement(verifImovel)) {
                    psVerif.setInt(1, imovelId);
                    psVerif.setTimestamp(2, Timestamp.valueOf(dataHora));
                    ResultSet rs = psVerif.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(this, "Este imóvel já possui uma visita neste horário.");
                        return;
                    }
                }

                // Insere a visita no banco
                String sql = "INSERT INTO visita (id_cliente, id_imovel, id_corretor, dataVisita) VALUES (?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, clienteId);
                    ps.setInt(2, imovelId);
                    ps.setInt(3, corretorId);
                    ps.setTimestamp(4, Timestamp.valueOf(dataHora));
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Visita registrada com sucesso!");
                    dispose(); // Fecha a janela
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao registrar visita: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Executa a tela individualmente
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaRegistrarVisita());
    }
}