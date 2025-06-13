import Class.Conexao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Scanner;
import javax.swing.SwingUtilities;

    public class Main {
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        private static final Locale BRAZIL = new Locale("pt", "BR");

       public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Escolha o modo de execução:");
        System.out.println("1 - Versão Console");
        System.out.println("2 - Interface Gráfica (Swing)");
        System.out.print("Digite sua escolha: ");

        int escolha = scanner.nextInt();
        scanner.nextLine();

        if (escolha == 1) {
            executarModoConsole(scanner);
        } else if (escolha == 2) {
            SwingUtilities.invokeLater(() -> new TelaMenuPrincipal().setVisible(true));
        } else {
            System.out.println("Opção inválida. Finalizando o sistema.");
        }

        scanner.close();
    }
    
    // METODO PARA EXECUTAR A VERSÃO CONSOLE
    private static void executarModoConsole(Scanner scanner) {
        int opcao;
        do {
            exibirMenu();
            if (scanner.hasNextInt()) {
                opcao = scanner.nextInt();
                scanner.nextLine();
                switch (opcao) {
                    case 1: cadastrarImovel(scanner); break;
                    case 2: listarImoveis(); break;
                    case 3: cadastrarCliente(scanner); break;
                    case 4: listarClientes(); break;
                    case 5: cadastrarCorretor(scanner); break;
                    case 6: listarCorretores(); break;
                    case 7: agendarVisita(scanner); break;
                    case 8: listarVisitas(); break;
                    case 9: enviarProposta(scanner); break;
                    case 10: listarPropostas(); break;
                    case 0: System.out.println("Saindo..."); break;
                    default: System.out.println("Opção inválida. Tente novamente.");
                }
            } else {
                System.out.println("Digite um número válido.");
                scanner.nextLine();
                opcao = -1;
            }
        } while (opcao != 0);
    }

    private static void exibirMenu() {
        System.out.println("\n===== MENU =====");
        System.out.println("1  - Cadastrar Imóvel");
        System.out.println("2  - Listar Imóveis");
        System.out.println("3  - Cadastrar Cliente");
        System.out.println("4  - Listar Clientes");
        System.out.println("5  - Cadastrar Corretor");
        System.out.println("6  - Listar Corretores");
        System.out.println("7  - Agendar Visita");
        System.out.println("8  - Listar Visitas");
        System.out.println("9  - Enviar Proposta");
        System.out.println("10 - Listar Propostas");
        System.out.println("0  - Sair");
        System.out.print("Escolha uma opção: ");
    }

    // --- IMÓVEL ---
    private static void cadastrarImovel(Scanner scanner) {
        System.out.print("Tipo (Casa/Apartamento): ");
        String tipo = scanner.nextLine();
        System.out.print("Rua: ");
        String rua = scanner.nextLine();
        System.out.print("Número: ");
        int numero = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Cidade: ");
        String cidade = scanner.nextLine();
        System.out.print("Estado: ");
        String estado = scanner.nextLine();
        System.out.print("CEP: ");
        String cep = scanner.nextLine();
        System.out.print("Valor: ");
        double valor = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Status (Disponível/Alugado/Vendido): ");
        String status = scanner.nextLine();

        String sql = "INSERT INTO imovel (tipo, rua, numero, cidade, estado, cep, valor, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tipo);
            ps.setString(2, rua);
            ps.setInt(3, numero);
            ps.setString(4, cidade);
            ps.setString(5, estado);
            ps.setString(6, cep);
            ps.setDouble(7, valor);
            ps.setString(8, status);
            ps.executeUpdate();
            System.out.println("Imóvel cadastrado com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar imóvel: " + e.getMessage());
        }
    }

    private static void listarImoveis() {
        System.out.println("=== Lista de Imóveis ===");
        String sql = "SELECT * FROM imovel";
        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            NumberFormat nf = NumberFormat.getCurrencyInstance(BRAZIL);
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id")
                        + ", Tipo: " + rs.getString("tipo")
                        + ", Endereço: " + rs.getString("rua") + ", " + rs.getInt("numero") + " - "
                        + rs.getString("cidade") + "/" + rs.getString("estado")
                        + ", CEP: " + rs.getString("cep")
                        + ", Valor: " + nf.format(rs.getDouble("valor"))
                        + ", Status: " + rs.getString("status"));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar imóveis: " + e.getMessage());
        }
    }

    // --- CLIENTE ---
    private static void cadastrarCliente(Scanner scanner) {
        System.out.print("Nome do Cliente: ");
        String nome = scanner.nextLine();
        String cpf;
        while (true) {
            System.out.print("CPF (11 dígitos): ");
            cpf = scanner.nextLine().replaceAll("\\D", "");
            if (cpf.length() == 11) {
                cpf = cpf.replaceFirst("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
                break;
            }
            System.out.println("CPF inválido. Tente novamente.");
        }
        System.out.print("Telefone: ");
        String telefone = scanner.nextLine();

        String sql = "INSERT INTO cliente (nome, cpf, telefone) VALUES (?, ?, ?)";
        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nome);
            ps.setString(2, cpf);
            ps.setString(3, telefone);
            ps.executeUpdate();
            System.out.println("Cliente cadastrado com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar cliente: " + e.getMessage());
        }
    }

    private static void listarClientes() {
        System.out.println("=== Lista de Clientes ===");
        String sql = "SELECT * FROM cliente";
        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id")
                        + ", Nome: " + rs.getString("nome")
                        + ", CPF: " + rs.getString("cpf")
                        + ", Tel: " + rs.getString("telefone"));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar clientes: " + e.getMessage());
        }
    }

    // --- CORRETOR ---
    private static void cadastrarCorretor(Scanner scanner) {
        System.out.print("Nome do Corretor: ");
        String nome = scanner.nextLine();
        System.out.print("CRECI: ");
        String creci = scanner.nextLine();
        System.out.print("Telefone: ");
        String telefone = scanner.nextLine();

        String sql = "INSERT INTO corretor (nome, creci, telefone) VALUES (?, ?, ?)";
        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nome);
            ps.setString(2, creci);
            ps.setString(3, telefone);
            ps.executeUpdate();
            System.out.println("Corretor cadastrado com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar corretor: " + e.getMessage());
        }
    }

    private static void listarCorretores() {
        System.out.println("=== Lista de Corretores ===");
        String sql = "SELECT * FROM corretor";
        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id")
                        + ", Nome: " + rs.getString("nome")
                        + ", CRECI: " + rs.getString("creci")
                        + ", Tel: " + rs.getString("telefone"));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar corretores: " + e.getMessage());
        }
    }

    // --- VISITA ---
    private static void agendarVisita(Scanner scanner) {
        listarClientes();
        System.out.print("ID do Cliente: ");
        int clienteId = scanner.nextInt();
        scanner.nextLine();
        listarImoveis();
        System.out.print("ID do Imóvel: ");
        int imovelId = scanner.nextInt();
        scanner.nextLine();

        LocalDateTime dataHora;
        while (true) {
            try {
                System.out.print("Data e hora (dd/MM/yyyy HH:mm): ");
                String entrada = scanner.nextLine();
                dataHora = LocalDateTime.parse(entrada, FORMATTER);
                if (dataHora.isBefore(LocalDateTime.now())) {
                    System.out.println("Data no passado. Escolha outra.");
                } else {
                    break;
                }
            } catch (Exception e) {
                System.out.println("Formato inválido. Tente novamente.");
            }
        }

        String sql = "INSERT INTO visita (id_cliente, id_imovel, data_visita) VALUES (?, ?, ?)";
        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clienteId);
            ps.setInt(2, imovelId);
            ps.setTimestamp(3, Timestamp.valueOf(dataHora));
            ps.executeUpdate();
            System.out.println("Visita agendada com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao agendar visita: " + e.getMessage());
        }
    }

    private static void listarVisitas() {
        System.out.println("=== Lista de Visitas ===");
        String sql = "SELECT v.id, c.nome AS cliente, i.tipo AS imovel, v.data_visita " +
                     "FROM visita v " +
                     "JOIN cliente c ON v.id_cliente = c.id " +
                     "JOIN imovel i ON v.id_imovel = i.id";
        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                LocalDateTime dt = rs.getTimestamp("data_visita").toLocalDateTime();
                System.out.println("ID: " + rs.getInt("id")
                        + ", Cliente: " + rs.getString("cliente")
                        + ", Imóvel: " + rs.getString("imovel")
                        + ", Data: " + dt.format(FORMATTER));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar visitas: " + e.getMessage());
        }
    }

    // --- PROPOSTA ---
    private static void enviarProposta(Scanner scanner) {
        listarClientes();
        System.out.print("ID do Cliente: ");
        int clienteId = scanner.nextInt();
        scanner.nextLine();
        listarImoveis();
        System.out.print("ID do Imóvel: ");
        int imovelId = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Valor da Proposta: ");
        double valor = scanner.nextDouble();
        scanner.nextLine();

        String sql = "INSERT INTO proposta (id_cliente, id_imovel, valor_proposta, status) VALUES (?, ?, ?, 'Enviada')";
        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clienteId);
            ps.setInt(2, imovelId);
            ps.setDouble(3, valor);
            ps.executeUpdate();
            System.out.println("Proposta enviada com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao enviar proposta: " + e.getMessage());
        }
    }

    private static void listarPropostas() {
        System.out.println("=== Lista de Propostas ===");
        String sql = "SELECT p.id, c.nome AS cliente, i.tipo AS imovel, p.valor_proposta, p.status " +
                     "FROM proposta p " +
                     "JOIN cliente c ON p.id_cliente = c.id " +
                     "JOIN imovel i ON p.id_imovel = i.id";
        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            NumberFormat nf = NumberFormat.getCurrencyInstance(BRAZIL);
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id")
                        + ", Cliente: " + rs.getString("cliente")
                        + ", Imóvel: " + rs.getString("imovel")
                        + ", Valor: " + nf.format(rs.getDouble("valor_proposta"))
                        + ", Status: " + rs.getString("status"));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar propostas: " + e.getMessage());
        }
    }
}