// Importa a classe responsável por conectar ao banco de dados
import Class.Conexao;

// Importa as bibliotecas gráficas e utilitárias do Java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * TelaCadastroImovel
 * Tela gráfica para cadastrar um novo imóvel no sistema.
 */
public class TelaCadastroImovel extends JFrame {

    // Campos de entrada e seleção do formulário
    private JTextField campoRua, campoNumero, campoCidade, campoCep, campoValor;
    private JComboBox<String> comboEstado, comboTipo, comboStatus;
    private JButton btnSalvar;

    /**
     * Construtor da interface gráfica da tela de cadastro de imóvel.
     */
    public TelaCadastroImovel() {
        // Define título, tamanho e configurações da janela
        setTitle("Cadastro de Imóvel");
        setSize(400, 600);
        setLocationRelativeTo(null); // Centraliza a janela
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Fecha só esta janela

        // Criação do painel principal com layout flexível
        JPanel painel = new JPanel();
        painel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Espaçamento entre os componentes
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ===== Campo Tipo de Imóvel =====
        gbc.gridx = 0;
        gbc.gridy = 0;
        painel.add(new JLabel("Tipo de Imóvel:"), gbc);
        comboTipo = new JComboBox<>(new String[]{"Casa", "Apartamento", "Lote"});
        gbc.gridy++;
        painel.add(comboTipo, gbc);

        // ===== Campo Rua =====
        gbc.gridy++;
        painel.add(new JLabel("Rua:"), gbc);
        campoRua = new JTextField();
        gbc.gridy++;
        painel.add(campoRua, gbc);

        // ===== Campo Número =====
        gbc.gridy++;
        painel.add(new JLabel("Número:"), gbc);
        campoNumero = new JTextField();
        gbc.gridy++;
        painel.add(campoNumero, gbc);

        // ===== Campo Cidade =====
        gbc.gridy++;
        painel.add(new JLabel("Cidade:"), gbc);
        campoCidade = new JTextField();
        gbc.gridy++;
        painel.add(campoCidade, gbc);

        // ===== Combo Estado =====
        gbc.gridy++;
        painel.add(new JLabel("Estado:"), gbc);
        comboEstado = new JComboBox<>(new String[]{
            "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA",
            "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI", "RJ", "RN",
            "RS", "RO", "RR", "SC", "SP", "SE", "TO"
        });
        gbc.gridy++;
        painel.add(comboEstado, gbc);

        // ===== Campo CEP =====
        gbc.gridy++;
        painel.add(new JLabel("CEP:"), gbc);
        campoCep = new JTextField();
        gbc.gridy++;
        painel.add(campoCep, gbc);

        // ===== Campo Valor =====
        gbc.gridy++;
        painel.add(new JLabel("Valor (Ex: 300000.00):"), gbc);
        campoValor = new JTextField();
        gbc.gridy++;
        painel.add(campoValor, gbc);

        // ===== Combo Status =====
        gbc.gridy++;
        painel.add(new JLabel("Status:"), gbc);
        comboStatus = new JComboBox<>(new String[]{"Disponível", "Alugado", "Vendido"});
        gbc.gridy++;
        painel.add(comboStatus, gbc);

        // ===== Botão Salvar =====
        btnSalvar = new JButton("Salvar");
        gbc.gridy++;
        painel.add(btnSalvar, gbc);

        // Ação do botão de salvar
        btnSalvar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salvarImovel(); // Chama o método para salvar no banco
            }
        });

        // Adiciona o painel à janela e torna visível
        add(painel);
        setVisible(true);
    }

    /**
     * Método responsável por inserir os dados do imóvel no banco de dados.
     */
    private void salvarImovel() {
        try {
            // Captura os dados digitados pelo usuário
            String tipo = comboTipo.getSelectedItem().toString();
            String rua = campoRua.getText();
            int numero = Integer.parseInt(campoNumero.getText());
            String cidade = campoCidade.getText();
            String estado = comboEstado.getSelectedItem().toString();
            String cep = campoCep.getText();
            double valor = Double.parseDouble(campoValor.getText());
            String status = comboStatus.getSelectedItem().toString();

            // Prepara e executa o comando SQL
            try (Connection conn = Conexao.conectar()) {
                String sql = "INSERT INTO imovel (tipo, rua, numero, cidade, estado, cep, valor, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, tipo);
                ps.setString(2, rua);
                ps.setInt(3, numero);
                ps.setString(4, cidade);
                ps.setString(5, estado);
                ps.setString(6, cep);
                ps.setDouble(7, valor);
                ps.setString(8, status);
                ps.executeUpdate();

                // Mensagem de sucesso e fechamento da tela
                JOptionPane.showMessageDialog(this, "Imóvel cadastrado com sucesso!");
                dispose();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + ex.getMessage());
        }
    }

    /**
     * Método principal para testar a tela de forma independente.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaCadastroImovel());
    }
}
