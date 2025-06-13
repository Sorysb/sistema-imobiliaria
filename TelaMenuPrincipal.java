import Class.Conexao;                                // Classe utilitária para conectar ao banco via JDBC
import Telas.TelaCadastroCorretor;                   // Tela de cadastro de corretores
import javax.swing.*;                                // Componentes Swing (JFrame, JButton, JLabel, etc.)
import java.awt.*;                                   // Layouts, Insets, Font, etc.
import java.awt.event.*;                             // Eventos de ação (ActionListener)
import java.sql.*;                                   // JDBC (Connection, Statement, ResultSet)
import java.time.LocalDate;                          // Representação de data (para contar por mês)

/**
 * Tela principal do sistema imobiliário em Swing.
 * Exibe botões de navegação para cadastros, propostas, visitas, listagens e relatórios.
 */
public class TelaMenuPrincipal extends JFrame {

    // Labels que poderão exibir contagens dinâmicas, se desejar
    private JLabel labelTitulo,
                   labelContagemClientes,
                   labelContagemImoveis,
                   labelContagemCorretores,
                   labelContagemPropostas,
                   labelContagemVisitas;

    // Botões principais do menu
    private JButton btnCadastro,
                    btnProposta,
                    btnVisitas,
                    btnListagens,
                    btnRelatorios,
                    btnVisitasAgendadas,
                    btnSair;

    public TelaMenuPrincipal() {
        // Configurações da janela principal
        setTitle("Sistema Imobiliaria - Menu Principal");
        setExtendedState(JFrame.MAXIMIZED_BOTH);              // Abre em tela cheia
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);       // Fecha a aplicação ao clicar no X
        setLayout(new GridBagLayout());                       // Layout flexível em grade

        // Constraints compartilhadas para todos os componentes
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);               // Espaçamento ao redor
        gbc.gridx = 0;                                         // Coluna zero
        gbc.gridwidth = 2;                                     // Ocupa 2 colunas
        gbc.fill = GridBagConstraints.HORIZONTAL;              // Componentes expandem horizontalmente

        // ==== TÍTULO ====
        labelTitulo = new JLabel("Menu Principal do Sistema", SwingConstants.CENTER);
        labelTitulo.setFont(new Font("Arial", Font.BOLD, 26)); // Fonte maior e em negrito
        gbc.gridy = 0;                                         // Linha zero
        add(labelTitulo, gbc);                                 // Adiciona à janela

        // ==== BOTÕES ====

        // 1) Cadastros
        btnCadastro = new JButton("Cadastros");
        gbc.gridy++;                                           // Próxima linha
        add(btnCadastro, gbc);

        // 2) Propostas
        btnProposta = new JButton("Propostas");
        gbc.gridy++;
        add(btnProposta, gbc);

        // 3) Visitas
        btnVisitas = new JButton("Visitas");
        gbc.gridy++;
        add(btnVisitas, gbc);

        // 4) Listagens Gerais
        btnListagens = new JButton("Listagens Gerais");
        gbc.gridy++;
        add(btnListagens, gbc);

        // 5) Relatórios do Sistema
        btnRelatorios = new JButton("Relatorios do Sistema");
        gbc.gridy++;
        add(btnRelatorios, gbc);

        // 6) Visitas Agendadas
        btnVisitasAgendadas = new JButton("Visitas Agendadas");
        gbc.gridy++;
        add(btnVisitasAgendadas, gbc);

        // 7) Sair
        btnSair = new JButton("Sair");
        gbc.gridy++;
        add(btnSair, gbc);

        // ==== AÇÕES DOS BOTÕES ====

        // Botão de Cadastros: abre um dialog para escolher entre Cliente/Corretor/Imóvel
        btnCadastro.addActionListener((ActionEvent e) -> {
            String[] opcoes = {"Cliente", "Corretor", "Imovel"};
            String escolha = (String) JOptionPane.showInputDialog(
                null,
                "Escolha o cadastro:",
                "Cadastros",
                JOptionPane.QUESTION_MESSAGE,
                null,
                opcoes,
                opcoes[0]
            );
            if (escolha != null) {
                switch (escolha) {
                    case "Cliente":
                        new TelaCadastroCliente().setVisible(true);
                        break;
                    case "Corretor":
                        new TelaCadastroCorretor().setVisible(true);
                        break;
                    case "Imovel":
                        new TelaCadastroImovel().setVisible(true);
                        break;
                }
            }
        });

        // Botão Propostas: abre a tela de envio de propostas
        btnProposta.addActionListener(e -> new TelaRegistrarProposta().setVisible(true));

        // Botão Visitas: abre a tela de agendamento de visitas
        btnVisitas.addActionListener(e -> new TelaRegistrarVisita().setVisible(true));

        // Botão Listagens Gerais: abre listagem de clientes, corretores e imóveis
        btnListagens.addActionListener(e -> new TelaListagemGeral().setVisible(true));

        // Botão Relatórios: abre painel de relatórios do sistema
        btnRelatorios.addActionListener(e -> new TelaRelatorio().setVisible(true));

        // Botão Visitas Agendadas: abre listagem avançada de visitas
        btnVisitasAgendadas.addActionListener(e -> new TelaVisitasAgendadas().setVisible(true));

        // Botão Sair: encerra a aplicação
        btnSair.addActionListener(e -> System.exit(0));
    }

    /**
     * Conta quantos registros existem em uma determinada tabela.
     * @param tabela Nome da tabela no banco.
     * @return Quantidade de linhas.
     */
    private int contar(String tabela) {
        int total = 0;
        String sql = "SELECT COUNT(*) FROM " + tabela;
        try (Connection conn = Conexao.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("Erro ao contar " + tabela + ": " + e.getMessage());
        }
        return total;
    }

    /**
     * Conta quantos registros de uma tabela foram criados no mês atual,
     * usando um campo DATETIME para filtrar mês e ano.
     *
     * @param tabela   Nome da tabela.
     * @param campoData Campo DATETIME a ser filtrado (ex: data_visita, data_proposta).
     * @return Quantidade de registros no mês atual.
     */
    private int contarPorMes(String tabela, String campoData) {
        int total = 0;
        String sql = "SELECT COUNT(*) FROM " + tabela +
                     " WHERE MONTH(" + campoData + ") = MONTH(CURDATE())" +
                     "   AND YEAR("  + campoData + ") = YEAR(CURDATE())";
        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("Erro ao contar por mes em " + tabela + ": " + e.getMessage());
        }
        return total;
    }

    /**
     * Ponto de entrada da aplicação. Garante que a interface será
     * criada na Event Dispatch Thread, que é a thread recomendada para Swing.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaMenuPrincipal().setVisible(true));
    }
}
