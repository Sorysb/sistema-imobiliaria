// Importa a classe de conexão com o banco de dados
import Class.Conexao;

// Importações da biblioteca Swing para componentes gráficos
import javax.swing.*;
// Importações para tabelas
import javax.swing.table.DefaultTableModel;
// Importações para layout e formatação
import java.awt.*;
import java.sql.*;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * TelaListagemGeral
 * Tela principal de listagem de registros: Clientes, Corretores, Imóveis e Propostas (com senha).
 */
public class TelaListagemGeral extends JFrame {

    // Componentes da interface
    private JTabbedPane abas;
    private JTable tabelaClientes, tabelaCorretores, tabelaImoveis, tabelaPropostas;
    private DefaultTableModel modeloClientes, modeloCorretores, modeloImoveis, modeloPropostas;

    // Construtor da tela
    public TelaListagemGeral() {
        setTitle("Listagem Geral"); // Título da janela
        setSize(1000, 600); // Tamanho da janela
        setLocationRelativeTo(null); // Centraliza a janela
        setLayout(new BorderLayout()); // Layout principal

        // Criação das abas
        abas = new JTabbedPane();

        // Tabela de clientes
        modeloClientes = new DefaultTableModel(new String[]{"ID", "Nome", "CPF", "Telefone"}, 0);
        tabelaClientes = new JTable(modeloClientes);
        abas.addTab("Clientes", new JScrollPane(tabelaClientes));

        // Tabela de corretores
        modeloCorretores = new DefaultTableModel(new String[]{"ID", "Nome", "CRECI", "Telefone"}, 0);
        tabelaCorretores = new JTable(modeloCorretores);
        abas.addTab("Corretores", new JScrollPane(tabelaCorretores));

        // Tabela de imóveis
        modeloImoveis = new DefaultTableModel(new String[]{"ID", "Tipo", "Endereço", "Valor", "Status"}, 0);
        tabelaImoveis = new JTable(modeloImoveis);
        abas.addTab("Imóveis", new JScrollPane(tabelaImoveis));

        // Botão para abrir a aba de propostas com senha
        JButton btnAbrirPropostas = new JButton("Abrir Propostas (com senha)");
        btnAbrirPropostas.addActionListener(e -> solicitarSenha());

        // Adiciona as abas e o botão ao layout principal
        add(abas, BorderLayout.CENTER);
        add(btnAbrirPropostas, BorderLayout.SOUTH);

        // Carrega os dados do banco
        carregarClientes();
        carregarCorretores();
        carregarImoveis();

        setVisible(true); // Torna a janela visível
    }

    /**
     * Solicita a senha ao usuário para visualizar a aba de propostas.
     * Se a senha for correta, exibe a aba com os dados carregados.
     */
    private void solicitarSenha() {
        String senha = JOptionPane.showInputDialog(this, "Digite a senha para visualizar propostas:", "Autenticação", JOptionPane.WARNING_MESSAGE);
        if ("Acess0@20".equals(senha)) {
            if (modeloPropostas == null) {
                // Criação da aba de propostas apenas uma vez
                modeloPropostas = new DefaultTableModel(new String[]{"ID", "Cliente", "Imóvel", "Valor", "Status", "Data"}, 0);
                tabelaPropostas = new JTable(modeloPropostas);
                abas.addTab("Propostas", new JScrollPane(tabelaPropostas));
                carregarPropostas(); // Carrega os dados do banco
            } else {
                JOptionPane.showMessageDialog(this, "A aba de propostas já está visível.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Senha incorreta!", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Carrega os dados da tabela cliente do banco de dados
     */
    private void carregarClientes() {
        try (Connection conn = Conexao.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM cliente")) {

            while (rs.next()) {
                modeloClientes.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("cpf"),
                    rs.getString("telefone")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar clientes: " + e.getMessage());
        }
    }

    /**
     * Carrega os dados da tabela corretor
     */
    private void carregarCorretores() {
        try (Connection conn = Conexao.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM corretor")) {

            while (rs.next()) {
                modeloCorretores.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("creci"),
                    rs.getString("telefone")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar corretores: " + e.getMessage());
        }
    }

    /**
     * Carrega os dados dos imóveis cadastrados
     */
    private void carregarImoveis() {
        try (Connection conn = Conexao.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM imovel")) {

            while (rs.next()) {
                // Monta o endereço completo com cidade e estado
                String endereco = rs.getString("rua") + ", " + rs.getString("numero") + " - " + rs.getString("cidade") + "/" + rs.getString("estado");

                // Formata o valor para o padrão BRL (ex: R$ 300.000,00)
                double valor = rs.getDouble("valor");
                String valorFormatado = NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(valor);

                modeloImoveis.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("tipo"),
                    endereco,
                    valorFormatado,
                    rs.getString("status")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar imóveis: " + e.getMessage());
        }
    }

    /**
     * Carrega as propostas (visível somente após senha correta)
     */
    private void carregarPropostas() {
        try (Connection conn = Conexao.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT p.id, c.nome AS cliente, i.tipo AS imovel, p.valor_proposta, p.status, p.data_proposta " +
                     "FROM proposta p JOIN cliente c ON p.id_cliente = c.id JOIN imovel i ON p.id_imovel = i.id")) {

            while (rs.next()) {
                double valor = rs.getDouble("valor_proposta");
                String valorFormatado = NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(valor);

                modeloPropostas.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("cliente"),
                    rs.getString("imovel"),
                    valorFormatado,
                    rs.getString("status"),
                    rs.getTimestamp("data_proposta")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar propostas: " + e.getMessage());
        }
    }

    /**
     * Método principal para execução independente da tela.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaListagemGeral());
    }
}
