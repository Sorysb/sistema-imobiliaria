// Pacote onde a tela está localizada
package Telas;

// Importa a classe responsável por conectar ao banco de dados
import Class.Conexao;

// Bibliotecas gráficas Swing
import javax.swing.*;
import javax.swing.text.MaskFormatter;

// Bibliotecas de layout, eventos e SQL
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * TelaCadastroCorretor
 * Tela para cadastrar novos corretores no sistema imobiliário.
 */
public class TelaCadastroCorretor extends JFrame {

    // Campos da tela
    private JTextField campoNome;
    private JFormattedTextField campoCreci, campoTelefone;
    private JButton btnSalvar;

    /**
     * Construtor da tela de cadastro de corretor.
     */
    public TelaCadastroCorretor() {
        // Define título, tamanho, centraliza e comportamento de fechamento
        setTitle("Cadastro de Corretor");
        setSize(450, 400);
        setLocationRelativeTo(null); // Centraliza a tela
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Fecha apenas essa tela

        // Define layout principal
        JPanel painel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;

        // Título da tela
        JLabel titulo = new JLabel("Cadastro de Corretor", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 22));
        gbc.gridx = 0;
        gbc.gridy = 0;
        painel.add(titulo, gbc);

        // ==== Campo Nome ====
        gbc.gridwidth = 1;
        gbc.gridy++;
        painel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1;
        campoNome = new JTextField(20);
        painel.add(campoNome, gbc);

        // ==== Campo CRECI ====
        gbc.gridx = 0;
        gbc.gridy++;
        painel.add(new JLabel("CRECI:"), gbc);
        gbc.gridx = 1;
        try {
            // Máscara para o CRECI no formato "CRECI-MG 123456"
            MaskFormatter creciFormatter = new MaskFormatter("CRECI-MG ######");
            creciFormatter.setPlaceholderCharacter('_');
            campoCreci = new JFormattedTextField(creciFormatter);
            campoCreci.setColumns(20);
        } catch (Exception e) {
            campoCreci = new JFormattedTextField(); // Fallback se a máscara falhar
        }
        painel.add(campoCreci, gbc);

        // ==== Campo Telefone ====
        gbc.gridx = 0;
        gbc.gridy++;
        painel.add(new JLabel("Telefone:"), gbc);
        gbc.gridx = 1;
        try {
            // Máscara para telefone no formato (99) 99999-9999
            MaskFormatter telefoneFormatter = new MaskFormatter("(##) #####-####");
            telefoneFormatter.setPlaceholderCharacter('_');
            campoTelefone = new JFormattedTextField(telefoneFormatter);
            campoTelefone.setColumns(20);
        } catch (Exception e) {
            campoTelefone = new JFormattedTextField(); // Fallback
        }
        painel.add(campoTelefone, gbc);

        // ==== Botão Salvar ====
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        btnSalvar = new JButton("Salvar");
        painel.add(btnSalvar, gbc);

        // Ação ao clicar no botão "Salvar"
        btnSalvar.addActionListener((ActionEvent e) -> {
            salvarCorretor();
        });

        // Adiciona o painel à tela e exibe
        add(painel);
        setVisible(true);
    }

    /**
     * Método que salva o corretor no banco de dados.
     */
    private void salvarCorretor() {
        // Captura os dados digitados
        String nome = campoNome.getText().trim();
        String creci = campoCreci.getText().trim();
        String telefone = campoTelefone.getText().trim();

        try (Connection conn = Conexao.conectar()) {
            // Query de inserção
            String sql = "INSERT INTO corretor (nome, creci, telefone) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nome);
            ps.setString(2, creci);
            ps.setString(3, telefone);
            ps.executeUpdate();

            // Mensagem de sucesso
            JOptionPane.showMessageDialog(this, "Corretor cadastrado com sucesso!");
            dispose(); // Fecha a tela
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + ex.getMessage());
        }
    }

    /**
     * Método principal para testar a tela de forma independente.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaCadastroCorretor());
    }
}
