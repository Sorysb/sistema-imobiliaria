// Importa a classe de conexão com o banco de dados
import Class.Conexao;

// Importa bibliotecas da interface gráfica
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

// Importa bibliotecas de banco e manipulação de datas
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Classe que representa a tela de visualização, filtro e edição de visitas agendadas.
 */
public class TelaVisitasAgendadas extends JFrame {

    // Componentes principais da tela
    private JTable tabela;
    private JComboBox<String> comboCorretor;
    private JTextField campoData;
    private DefaultTableModel modelo;

    /**
     * Construtor da interface de visitas agendadas.
     */
    public TelaVisitasAgendadas() {
        setTitle("Visitas Agendadas");
        setSize(1000, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Cria o painel de filtros na parte superior
        JPanel painelFiltro = new JPanel(new FlowLayout());

        comboCorretor = new JComboBox<>();
        carregarCorretores(); // Carrega os corretores do banco no comboBox
        painelFiltro.add(new JLabel("Corretor:"));
        painelFiltro.add(comboCorretor);

        campoData = new JTextField(10);
        painelFiltro.add(new JLabel("Data (dd/MM/yyyy):"));
        painelFiltro.add(campoData);

        // Botão para aplicar filtros de pesquisa
        JButton btnFiltrar = new JButton("Filtrar");
        btnFiltrar.addActionListener(e -> carregarVisitas());
        painelFiltro.add(btnFiltrar);

        // Botão para editar ou cancelar visitas
        JButton btnEditar = new JButton("Alterar ou Cancelar Visita");
        btnEditar.addActionListener(e -> editarOuCancelar());
        painelFiltro.add(btnEditar);

        add(painelFiltro, BorderLayout.NORTH);

        // Cria a tabela onde as visitas serão exibidas
        tabela = new JTable();
        JScrollPane scrollPane = new JScrollPane(tabela);
        add(scrollPane, BorderLayout.CENTER);

        // Carrega as visitas no momento da abertura da tela
        carregarVisitas();

        setVisible(true);
    }

    /**
     * Carrega os corretores cadastrados no banco para o combo de filtro.
     */
    private void carregarCorretores() {
        comboCorretor.addItem("Todos"); // Opção padrão
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

    /**
     * Carrega e exibe as visitas filtradas conforme os campos preenchidos.
     */
    private void carregarVisitas() {
        modelo = new DefaultTableModel(new Object[]{"ID", "Cliente", "Imóvel", "Endereço", "Corretor", "Data e Hora", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        try (Connection conn = Conexao.conectar()) {
            StringBuilder sql = new StringBuilder(
                "SELECT v.id, c.nome AS cliente, i.tipo AS imovel, " +
                "CONCAT(i.rua, ', ', i.numero, ' - ', i.cidade) AS endereco, " +
                "r.nome AS corretor, v.dataVisita, v.status " +
                "FROM visita v " +
                "JOIN cliente c ON v.id_cliente = c.id " +
                "JOIN imovel i ON v.id_imovel = i.id " +
                "JOIN corretor r ON v.id_corretor = r.id WHERE 1=1 "
            );

            // Aplica o filtro de corretor, se selecionado
            if (!comboCorretor.getSelectedItem().equals("Todos")) {
                int corretorId = Integer.parseInt(comboCorretor.getSelectedItem().toString().split(" - ")[0]);
                sql.append("AND v.id_corretor = ").append(corretorId).append(" ");
            }

            // Aplica o filtro de data, se preenchido
            if (!campoData.getText().trim().isEmpty()) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    LocalDate dataFiltro = LocalDate.parse(campoData.getText().trim(), formatter);
                    sql.append("AND DATE(v.dataVisita) = '").append(dataFiltro).append("' ");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Data inválida. Use o formato dd/MM/yyyy.");
                    return;
                }
            }

            // Ordena os resultados por data e hora
            sql.append("ORDER BY v.dataVisita ASC");

            // Executa a query e preenche a tabela
            try (PreparedStatement ps = conn.prepareStatement(sql.toString());
                 ResultSet rs = ps.executeQuery()) {

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

                while (rs.next()) {
                    Object[] linha = new Object[]{
                        rs.getInt("id"),
                        rs.getString("cliente"),
                        rs.getString("imovel"),
                        rs.getString("endereco"),
                        rs.getString("corretor"),
                        formatter.format(rs.getTimestamp("dataVisita").toLocalDateTime()),
                        rs.getString("status")
                    };
                    modelo.addRow(linha);
                }

                tabela.setModel(modelo);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar visitas: " + e.getMessage());
        }
    }

    /**
     * Permite alterar a data ou cancelar uma visita selecionada, mediante senha.
     */
    private void editarOuCancelar() {
        int linhaSelecionada = tabela.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma visita na tabela.");
            return;
        }

        // Solicita senha de segurança
        String senha = JOptionPane.showInputDialog(this, "Digite a senha de administrador:");
        if (!"Acesso@1".equals(senha)) {
            JOptionPane.showMessageDialog(this, "Senha incorreta.");
            return;
        }

        int idVisita = (int) tabela.getValueAt(linhaSelecionada, 0);

        // Opções disponíveis ao usuário
        String[] opcoes = {"Alterar Data", "Cancelar Visita"};
        int escolha = JOptionPane.showOptionDialog(this, "O que deseja fazer?", "Ação",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opcoes, opcoes[0]);

        // Alterar a data da visita
        if (escolha == 0) {
            String novaData = JOptionPane.showInputDialog(this, "Nova data (dd/MM/yyyy HH:mm):");
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                LocalDateTime novaDataHora = LocalDateTime.parse(novaData, formatter);

                String sql = "UPDATE visita SET dataVisita = ? WHERE id = ?";
                try (Connection conn = Conexao.conectar();
                     PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setTimestamp(1, Timestamp.valueOf(novaDataHora));
                    ps.setInt(2, idVisita);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Data atualizada com sucesso.");
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Data inválida.");
            }

        } else if (escolha == 1) { // Cancelar visita
            String sql = "UPDATE visita SET status = 'Cancelada' WHERE id = ?";
            try (Connection conn = Conexao.conectar();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idVisita);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Visita cancelada com sucesso.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao cancelar: " + e.getMessage());
            }
        }

        carregarVisitas(); // Recarrega a tabela após ação
    }

    /**
     * Método principal (para testes isolados da tela).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaVisitasAgendadas());
    }
}
