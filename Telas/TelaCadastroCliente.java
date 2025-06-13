// Importa a classe de conexão com o banco
import Class.Conexao;

// Importações para a interface gráfica Swing
import javax.swing.*;

// Importações de layout e eventos de ação
import java.awt.*;
import java.awt.event.*;

// Importações para uso do banco de dados
import java.sql.*;

/**
 * Tela de cadastro de clientes utilizando Java Swing.
 * Permite preencher nome, CPF e telefone, e salva no banco de dados.
 */
public class TelaCadastroCliente extends JFrame {

    // Campos de entrada
    private JTextField campoNome, campoCpf, campoTelefone;

    // Botão para salvar
    private JButton btnSalvar;

    /**
     * Construtor da tela.
     * Define título, tamanho, layout e componentes.
     */
    public TelaCadastroCliente() {
        setTitle("Cadastro de Cliente");             // Título da janela
        setSize(450, 350);                           // Tamanho da tela
        setLocationRelativeTo(null);                 // Centraliza na tela
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Fecha apenas essa janela

        // Painel principal com layout em grade
        JPanel painel = new JPanel();
        painel.setLayout(new GridBagLayout());       // Layout flexível
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);      // Espaçamento entre os componentes
        gbc.fill = GridBagConstraints.HORIZONTAL;    // Expandir horizontalmente
        gbc.gridwidth = 2;

        // Título no topo
        JLabel titulo = new JLabel("Cadastro de Cliente", SwingConstants.CENTER);
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

        // ==== Campo CPF ====
        gbc.gridx = 0;
        gbc.gridy++;
        painel.add(new JLabel("CPF:"), gbc);
        gbc.gridx = 1;
        campoCpf = new JTextField(20);
        painel.add(campoCpf, gbc);

        // ==== Campo Telefone ====
        gbc.gridx = 0;
        gbc.gridy++;
        painel.add(new JLabel("Telefone:"), gbc);
        gbc.gridx = 1;
        campoTelefone = new JTextField(20);
        painel.add(campoTelefone, gbc);

        // ==== Botão Salvar ====
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        btnSalvar = new JButton("Salvar");
        painel.add(btnSalvar, gbc);

        // Evento ao clicar no botão Salvar
        btnSalvar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salvarCliente();
            }
        });

        // Adiciona o painel na janela e exibe
        add(painel);
        setVisible(true);
    }

    /**
     * Método que realiza a validação, formatação e gravação dos dados no banco.
     */
    private void salvarCliente() {
        // Captura os valores dos campos
        String nome = campoNome.getText();
        String cpf = campoCpf.getText().replaceAll("[^0-9]", "");         // Remove qualquer caractere que não for número
        String telefone = campoTelefone.getText().replaceAll("[^0-9]", "");

        // Formata CPF para o padrão 000.000.000-00
        if (cpf.length() == 11) {
            cpf = cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-" + cpf.substring(9);
        }

        // Formata Telefone para (00) 000000000
        if (telefone.length() == 11) {
            telefone = "(" + telefone.substring(0, 2) + ") " + telefone.substring(2);
        }

        // Insere os dados no banco
        try (Connection conn = Conexao.conectar()) {
            String sql = "INSERT INTO cliente (nome, cpf, telefone) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nome);
            ps.setString(2, cpf);
            ps.setString(3, telefone);
            ps.executeUpdate();

            // Mensagem de sucesso e fecha a tela
            JOptionPane.showMessageDialog(this, "Cliente cadastrado com sucesso!");
            dispose(); // Fecha a janela atual
        } catch (Exception ex) {
            // Mensagem de erro caso falhe a conexão ou SQL
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + ex.getMessage());
        }
    }

    /**
     * Método principal para executar essa tela de forma independente.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaCadastroCliente());
    }
}
