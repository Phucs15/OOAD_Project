package Map;

import Functions.DatabaseConnection;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LocationSearchFrame {
    public static void main(String[] args) {
        // Tạo JFrame
        JFrame frame = new JFrame("Warehouse Location Search");
        frame.setSize(400, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);

        // Tạo JTextField để nhập địa điểm với góc bo tròn
        JTextField searchField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                if (g instanceof Graphics2D g2) {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
                }
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                if (g instanceof Graphics2D g2) {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getForeground());
                    g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 20, 20));
                }
            }
        };
        searchField.setBounds(20, 20, 350, 30);
        searchField.setOpaque(false);

        // Tạo JPopupMenu để hiển thị gợi ý với góc bo tròn
        JPopupMenu suggestionPopup = new JPopupMenu() {
            @Override
            public void paintComponent(Graphics g) {
                if (g instanceof Graphics2D g2) {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                } else {
                    super.paintComponent(g);
                }
            }
        };
        suggestionPopup.setOpaque(false);
        suggestionPopup.setBorder(BorderFactory.createEmptyBorder());

        JScrollPane scrollPane = new JScrollPane();
        JPanel suggestionPanel = new JPanel();
        suggestionPanel.setLayout(new BoxLayout(suggestionPanel, BoxLayout.Y_AXIS));
        scrollPane.setViewportView(suggestionPanel);
        scrollPane.setPreferredSize(new Dimension(350, 150));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Danh sách địa điểm từ database
        List<String> locations = new ArrayList<>();
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT Warehouse_Name FROM Warehouse");
            while (resultSet.next()) {
                locations.add(resultSet.getString("Warehouse_Name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error fetching locations from database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Thêm DocumentListener để cập nhật gợi ý khi người dùng nhập
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            private void updateSuggestions() {
                String text = searchField.getText().toLowerCase();
                suggestionPanel.removeAll();

                if (!text.isEmpty()) {
                    for (String location : locations) {
                        if (location.toLowerCase().contains(text)) {
                            JPanel itemPanel = new JPanel();
                            itemPanel.setLayout(new BorderLayout());
                            itemPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

                            // Thêm biểu tượng hoặc nội dung hiển thị
                            JLabel iconLabel = new JLabel(new ImageIcon("clock-icon.png")); // Thay đường dẫn biểu tượng
                            JLabel locationLabel = new JLabel(location);
                            itemPanel.add(iconLabel, BorderLayout.WEST);
                            itemPanel.add(locationLabel, BorderLayout.CENTER);

                            itemPanel.setOpaque(true);
                            itemPanel.setBackground(Color.WHITE);
                            itemPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                                @Override
                                public void mouseClicked(java.awt.event.MouseEvent evt) {
                                    searchField.setText(location);
                                    suggestionPopup.setVisible(false);
                                    SwingUtilities.invokeLater(searchField::requestFocusInWindow);
                                }
                            });

                            suggestionPanel.add(itemPanel);
                        }
                    }

                    if (suggestionPanel.getComponentCount() > 0) {
                        scrollPane.setPreferredSize(new Dimension(350, Math.min(suggestionPanel.getComponentCount() * 30, 150)));
                        suggestionPopup.removeAll();
                        suggestionPopup.add(scrollPane);

                        SwingUtilities.invokeLater(() -> {
                            suggestionPopup.show(searchField, 0, searchField.getHeight());
                            searchField.requestFocusInWindow();
                        });
                    } else {
                        suggestionPopup.setVisible(false);
                    }
                } else {
                    suggestionPopup.setVisible(false);
                }

                suggestionPanel.revalidate();
                suggestionPanel.repaint();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateSuggestions();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateSuggestions();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateSuggestions();
            }
        });

        // Thêm JTextField vào JFrame
        frame.add(searchField);

        // Hiển thị JFrame
        frame.setVisible(true);
    }
}
