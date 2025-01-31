package lobby;

import core.Game;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class WindowSettings {

    public static void setupMainWindow(JFrame frame) {
        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new GridBagLayout());
        frame.setContentPane(backgroundPanel);

        JPanel contentPanel = createContentPanel();
        backgroundPanel.add(contentPanel, new GridBagConstraints());
    }

    private static JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        panel.setOpaque(false);

        // Заголовок
        JLabel titleLabel = new JLabel("Лобби игры", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.YELLOW);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Основные компоненты
        JComponent formPanel = createFormPanel();
        panel.add(formPanel, BorderLayout.CENTER);

        return panel;
    }

    private static JComponent createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        Font labelFont = new Font("Arial", Font.BOLD, 16);
        Color textColor = Color.WHITE;

        // Поле имени игрока
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        namePanel.setOpaque(false);
        JLabel nameLabel = createStyledLabel("Имя игрока:", labelFont, textColor);
        JTextField nameField = createStyledTextField("Игрок 1", textColor);
        namePanel.add(nameLabel);
        namePanel.add(nameField);
        formPanel.add(namePanel);

        // Поле IP адреса
        JPanel ipPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        ipPanel.setOpaque(false);
        JLabel ipLabel = createStyledLabel("IP сервера:", labelFont, textColor);
        JTextField ipField = createStyledTextField("localhost", textColor);
        ipPanel.add(ipLabel);
        ipPanel.add(ipField);
        formPanel.add(ipPanel);

        // Поле порта
        JPanel portPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        portPanel.setOpaque(false);
        JLabel portLabel = createStyledLabel("Port сервера:", labelFont, textColor);
        JTextField portField = createStyledTextField("5000", textColor);
        portPanel.add(portLabel);
        portPanel.add(portField);
        formPanel.add(portPanel);

        // Чекбокс хоста
        JPanel hostPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        hostPanel.setOpaque(false);
        JLabel hostLabel = createStyledLabel("Хост игры?", labelFont, textColor);
        JCheckBox hostCheckBox = createStyledCheckBox();
        hostPanel.add(hostLabel);
        hostPanel.add(hostCheckBox);
        formPanel.add(hostPanel);

        // Кнопка
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        JButton button = new JButton("НАЧАТЬ ИГРУ!");
        button.setPreferredSize(new Dimension(200, 50));

        // Стилизация кнопки
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setForeground(Color.BLACK);
        button.setBackground(Color.YELLOW);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.ORANGE, 2),
                BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);

        // Обработчик клика
        button.addActionListener(e -> {
            // Создаем новое окно для игры
            JFrame gameWindow = new JFrame("Space Game");
            gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            gameWindow.setResizable(false);

            boolean isHost = hostCheckBox.isSelected();
            String ipAddress = ipField.getText();

            // Создаем и добавляем игровую панель
            Game game = new Game(isHost, ipAddress);
            gameWindow.add(game.getPanel());
            game.start();

            // Подгоняем размер окна под размер панели
            gameWindow.pack();

            // Размещаем окно по центру экрана
            gameWindow.setLocationRelativeTo(null);

            // Делаем окно видимым и запускаем игровой поток
            gameWindow.setVisible(true);

            // Закрываем окно лобби
            SwingUtilities.getWindowAncestor(button).dispose();
        });

        buttonPanel.add(button);
        formPanel.add(buttonPanel);

        return formPanel;
    }

    private static JLabel createStyledLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }

    private static JTextField createStyledTextField(String placeholder, Color color) {
        JTextField field = new JTextField(placeholder);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setForeground(color);
        field.setCaretColor(color);
        field.setOpaque(false);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.YELLOW, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private static JCheckBox createStyledCheckBox() {
        JCheckBox checkBox = new JCheckBox();
        checkBox.setOpaque(false);
        checkBox.setHorizontalAlignment(SwingConstants.CENTER);
        return checkBox;
    }

    static class BackgroundPanel extends JPanel {
        private BufferedImage background;

        public BackgroundPanel() {
            try {
                background = ImageIO.read(getClass().getResource("/img/space_lobby_bg.jpg"));
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Ошибка загрузки фона", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (background != null) {
                g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}
