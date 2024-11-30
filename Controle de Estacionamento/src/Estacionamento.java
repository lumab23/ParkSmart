import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class Estacionamento extends JFrame {

    // Variáveis globais
    private JTextField entradaPlacaTextField;
    private JTextField entradaHorarioTextField;
    private JTextField saidaPlacaTextField;
    private JTextField saidaHorarioTextField;
    private JLabel resultadoLabel;
    private JTable tabelaEstacionamento;
    private DefaultTableModel tabelaModel;

    // Matriz do estacionamento
    private String[][] estacionamento;
    private final int LINHAS = 5;
    private final int COLUNAS = 5;

    // Tarifas
    private final double TARIFA_ATE_3_HORAS = 10.00;
    private final double TARIFA_EXTRA = 5.00;
    private final int TOLERANCIA_MINUTOS = 15;

    public Estacionamento() {
        estacionamento = new String[LINHAS][COLUNAS]; // Inicializa matriz vazia

        // Configuração da janela
        setTitle("Estacionamento");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Painel esquerdo com a logo
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(0, 128, 128));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        JLabel logoLabel = new JLabel();
        logoLabel.setHorizontalAlignment(JLabel.CENTER);
        ImageIcon icon = new ImageIcon(getClass().getResource("/img/parksmart.png"));
        Image image = icon.getImage();
        Image scaledImage = image.getScaledInstance(290, 390, Image.SCALE_SMOOTH);
        logoLabel.setIcon(new ImageIcon(scaledImage));
        leftPanel.add(logoLabel);

        // Painel direito com os inputs e a matriz
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Configuração dos campos de texto
        entradaPlacaTextField = createStyledTextField("Placa do veículo (Entrada)");
        entradaHorarioTextField = createStyledTextField("Horário de entrada (HH:mm)");
        saidaPlacaTextField = createStyledTextField("Placa do veículo (Saída)");
        saidaHorarioTextField = createStyledTextField("Horário de saída (HH:mm)");

        // Botões
        JButton entradaButton = new JButton("Registrar Entrada");
        entradaButton.addActionListener(e -> registrarEntrada());

        JButton saidaButton = new JButton("Registrar Saída");
        saidaButton.addActionListener(e -> registrarSaida());

        resultadoLabel = new JLabel("Bem-vindo ao estacionamento.");
        resultadoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Painel para inputs
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.add(entradaPlacaTextField);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        inputPanel.add(entradaHorarioTextField);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        inputPanel.add(saidaPlacaTextField);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        inputPanel.add(saidaHorarioTextField);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Painel para botões
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(entradaButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(saidaButton);
        inputPanel.add(buttonPanel);

        inputPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        inputPanel.add(resultadoLabel);

        // Configuração da tabela
        tabelaModel = new DefaultTableModel(new Object[LINHAS][COLUNAS], new String[]{"1", "2", "3", "4", "5"});
        tabelaEstacionamento = new JTable(tabelaModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                JLabel label = (JLabel) comp;
                label.setHorizontalAlignment(SwingConstants.CENTER);
        
                // Define a cor baseada no status da vaga
                if (estacionamento[row][column] == null) {
                    label.setBackground(new Color(144, 238, 144));
                    label.setForeground(Color.BLACK);
                } else {
                    label.setBackground(new Color(240, 128, 128)); // Vaga ocupada
                    label.setForeground(Color.WHITE);
                }
        
                return label;
            }
        };
        

        tabelaEstacionamento.setRowHeight(40);
        tabelaEstacionamento.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tabelaEstacionamento.setShowGrid(false);
        tabelaEstacionamento.setIntercellSpacing(new Dimension(0, 0));
        tabelaEstacionamento.setFocusable(false);
        tabelaEstacionamento.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(200, 200, 200)));
                return label;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tabelaEstacionamento);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));


        // Adicionando os componentes ao painel direito
        rightPanel.add(inputPanel, BorderLayout.NORTH);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        // Adicionando painéis principais
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField textField = new JTextField(placeholder);
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        textField.setForeground(Color.GRAY);
        textField.setHorizontalAlignment(JTextField.CENTER);

        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(Color.GRAY);
                }
            }
        });

        return textField;
    }

    private void registrarEntrada() {
        String placa = entradaPlacaTextField.getText().toUpperCase();
        if (!placa.matches("[A-Z]{3}[0-9][A-Z][0-9]{2}")) {
            resultadoLabel.setText("Erro: Formato de placa inválido.");
            return;
        }

        for (int i = 0; i < LINHAS; i++) {
            for (int j = 0; j < COLUNAS; j++) {
                if (estacionamento[i][j] == null) {
                    estacionamento[i][j] = placa;
                    resultadoLabel.setText("Entrada registrada: " + placa + " na vaga (" + i + ", " + j + ").");
                    atualizarTabela();
                    return;
                }
            }
        }
        resultadoLabel.setText("Erro: Estacionamento cheio.");
    }

    private void registrarSaida() {
        String placa = saidaPlacaTextField.getText().toUpperCase();
        String horarioEntrada = entradaHorarioTextField.getText();
        String horarioSaida = saidaHorarioTextField.getText();

        for (int i = 0; i < LINHAS; i++) {
            for (int j = 0; j < COLUNAS; j++) {
                if (placa.equals(estacionamento[i][j])) {
                    estacionamento[i][j] = null;
                    calcularTarifa(placa, verificarPlacaPorEstado(placa), horarioEntrada, horarioSaida);
                    atualizarTabela();
                    return;
                }
            }
        }
        resultadoLabel.setText("Erro: Placa não encontrada.");
    }

    private void atualizarTabela() {
        for (int i = 0; i < LINHAS; i++) {
            for (int j = 0; j < COLUNAS; j++) {
                tabelaModel.setValueAt(estacionamento[i][j] == null ? "" : estacionamento[i][j], i, j);
            }
        }
    }

    private void calcularTarifa(String placa, String estado, String entrada, String saida) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime entradaTime = LocalTime.parse(entrada, formatter);
            LocalTime saidaTime = LocalTime.parse(saida, formatter);

            long minutos = Duration.between(entradaTime, saidaTime).toMinutes();
            double tarifa = minutos <= TOLERANCIA_MINUTOS ? 0 : minutos <= 180 ? TARIFA_ATE_3_HORAS : TARIFA_ATE_3_HORAS + Math.ceil((minutos - 180) / 60.0) * TARIFA_EXTRA;

            resultadoLabel.setText("<html>Ticket:<br>Placa: " + placa + "<br>Estado: " + estado + "<br>Tarifa: R$ " + String.format("%.2f", tarifa) + "</html>");
        } catch (Exception e) {
            resultadoLabel.setText("Erro: formato inválido.");
        }
    }

    private String verificarPlacaPorEstado(String placa) {
        String prefixo = placa.substring(0, 3).toUpperCase();

        if (prefixo.compareTo("AAA") >= 0 && prefixo.compareTo("BEZ") <= 0) {
            return "Paraná";
        } else if (prefixo.compareTo("IAQ") >= 0 && prefixo.compareTo("JDO") <= 0) {
            return "Rio Grande do Sul";
        } else if (prefixo.compareTo("LWR") >= 0 && prefixo.compareTo("MMM") <= 0) {
            return "Santa Catarina";
        } else {
            return "Estado não reconhecido.";
        }
    }

}
