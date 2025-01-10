package Map;

import Functions.DatabaseConnection;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class LocationSearchPanel extends JPanel {
    private final JTextField searchField;
    private final JPopupMenu suggestionPopup;
    private final JPanel suggestionPanel;
    private final JScrollPane scrollPane;
    private String selectedTruck = "No truck selected";

    public LocationSearchPanel() {
        setLayout(null);
        setPreferredSize(new Dimension(200, 500)); // Kích thước mặc định của panel

        addTransportButtons();

        // Tạo JTextField để nhập địa điểm với góc bo tròn
        searchField = new JTextField() {
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
        searchField.setBounds(20, 200, 260, 30); // Điều chỉnh chiều rộng và vị trí
        searchField.setOpaque(false);

        // Danh sách địa điểm từ database
        List<String> locations = fetchLocationsFromDatabase();

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
                        scrollPane.setPreferredSize(new Dimension(260, Math.min(suggestionPanel.getComponentCount() * 30, 150)));
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


        // Tạo JPopupMenu để hiển thị gợi ý với góc bo tròn
        suggestionPopup = new JPopupMenu() {
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
        suggestionPopup.setOpaque(true);
        suggestionPopup.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        scrollPane = new JScrollPane();
        suggestionPanel = new JPanel();
        suggestionPanel.setLayout(new BoxLayout(suggestionPanel, BoxLayout.Y_AXIS));
        scrollPane.setViewportView(suggestionPanel);
        scrollPane.setPreferredSize(new Dimension(260, 150)); // Kích thước của popup

        // Áp dụng giao diện hiện đại cho thanh cuộn
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new ModernScrollBarUI());

        // Loại bỏ đường viền của thanh cuộn để có giao diện gọn gàng hơn
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Tạo nút Generate với hình tròn
        // Vẽ hình tròn
        // Vẽ viền cho hình tròn
        JButton generateButton = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                if (g instanceof Graphics2D g2) {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    // Vẽ hình tròn
                    g2.setColor(getBackground());
                    g2.fillOval(0, 0, getWidth(), getHeight());
                }
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                if (g instanceof Graphics2D g2) {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    // Vẽ viền cho hình tròn
                    g2.setColor(Color.GRAY);
                    g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
                }
            }
        };
        generateButton.setBounds(125, 400, 50, 50); 
        generateButton.setBackground(new Color(59, 129, 182));
        generateButton.setFocusPainted(false);
        generateButton.setBorderPainted(false);
        generateButton.setContentAreaFilled(false);

        // Thêm hình ảnh vào nút
        ImageIcon icon = new ImageIcon(new ImageIcon("Images/Start_icon.png").getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
        generateButton.setIcon(icon);

        // Thêm sự kiện khi nhấn nút
//        generateButton.addActionListener(e -> {
//            String startLocation = searchField.getText();
//            if (startLocation.isEmpty()) {
//                JOptionPane.showMessageDialog(this, "Please enter a start location.");
//            } else {
//                try {
//                    // Tạo kết nối với database
//                    Connection connection = DatabaseConnection.getInstance().getConnection();
//                    String query = "SELECT Latitude, Longitude FROM Warehouse WHERE Warehouse_Name = ?";
//
//                    // Chuẩn bị statement
//                    PreparedStatement preparedStatement = connection.prepareStatement(query);
//                    preparedStatement.setString(1, startLocation);
//
//                    // Thực thi query
//                    ResultSet resultSet = preparedStatement.executeQuery();
//                    if (resultSet.next()) {
//                        // Lấy giá trị kinh độ và vĩ độ
//                        double latitude = resultSet.getDouble("Latitude");
//                        double longitude = resultSet.getDouble("Longitude");
//
//                        // Hiển thị thông tin
//                        JOptionPane.showMessageDialog(this, "Selected Truck: " + selectedTruck +
//                                "\nStart Location: " + startLocation +
//                                "\nLatitude: " + latitude +
//                                "\nLongitude: " + longitude);
//                    } else {
//                        JOptionPane.showMessageDialog(this, "Location not found in the database.", "Error", JOptionPane.ERROR_MESSAGE);
//                    }
//                    // Đóng tài nguyên
//                    resultSet.close();
//                    preparedStatement.close();
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                    JOptionPane.showMessageDialog(this, "Error retrieving location data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//                }
//            }
//        });
        generateButton.addActionListener(e -> {
            String startLocation = searchField.getText();
            if (startLocation.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a start location.");
                return;
            }

            try (Connection connection = DatabaseConnection.getInstance().getConnection()) {
                String query = "SELECT Warehouse_ID, Latitude, Longitude FROM Warehouse WHERE Warehouse_Name = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, startLocation);

                ResultSet resultSet = preparedStatement.executeQuery();
                if (!resultSet.next()) {
                    JOptionPane.showMessageDialog(this,
                            "Location not found in the database: " + startLocation,
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                int warehouseID = resultSet.getInt("Warehouse_ID");
                double latitude = resultSet.getDouble("Latitude");
                double longitude = resultSet.getDouble("Longitude");

                resultSet.close();
                preparedStatement.close();

                // Display selected truck information and location
                JOptionPane.showMessageDialog(this,
                        "Selected Truck: " + selectedTruck
                                + "\nStart Location: " + startLocation
                                + "\nLatitude: " + latitude
                                + "\nLongitude: " + longitude
                );

                // Call updated fetchOrderListForWarehouse with selectedTruck
                List<Object[]> orderData = fetchOrderListForWarehouse(warehouseID, selectedTruck);

                if (orderData.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "No orders found for warehouse " + startLocation
                                    + " (ID=" + warehouseID + ").",
                            "Order List",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    return;
                }

                // Build column names
                String[] columnNames = {
                        "Order ID",
                        "Customer Name",
                        "Delivery Address",
                        "Delivery Region",
                        "Total Weight" // New column for total weight
                };

                // Create 2D array for table data
                Object[][] rowData = new Object[orderData.size()][columnNames.length];
                for (int i = 0; i < orderData.size(); i++) {
                    rowData[i] = orderData.get(i);
                }

                // Create JTable and display it
                JTable table = new JTable(rowData, columnNames);
                JScrollPane scrollPaneForTable = new JScrollPane(table);
                scrollPaneForTable.setPreferredSize(new Dimension(500, 300));

                JOptionPane.showMessageDialog(
                        this,
                        scrollPaneForTable,
                        "Order List for " + startLocation,
                        JOptionPane.PLAIN_MESSAGE
                );

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error retrieving location data: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });


        // Thêm JTextField và nút Generate vào panel
        add(searchField);
        add(generateButton);
    }

    private void addTransportButtons() {
        JPanel transportPanel = new JPanel();
        transportPanel.setLayout(null); // Sử dụng layout tự do
        transportPanel.setPreferredSize(new Dimension(300, 100)); // Đặt kích thước panel

        // Tạo icon với kích thước phù hợp
        ImageIcon truck1Icon = new ImageIcon(new ImageIcon("Images/Truck_1.png")
                .getImage().getScaledInstance(50, 25, Image.SCALE_SMOOTH));
        ImageIcon truck2Icon = new ImageIcon(new ImageIcon("Images/Truck_2.png")
                .getImage().getScaledInstance(60, 25, Image.SCALE_SMOOTH));
        ImageIcon truck3Icon = new ImageIcon(new ImageIcon("Images/Truck_3.png")
                .getImage().getScaledInstance(90, 25, Image.SCALE_SMOOTH));

        // Tạo các nút và gán icon
        JButton truck1 = new JButton(truck1Icon);
        JButton truck2 = new JButton(truck2Icon);
        JButton truck3 = new JButton(truck3Icon);

        int panelWidth = 300; // Chiều rộng của panel
        int buttonHeight = 60; // Chiều cao của nút
        int spacing = 30; // Khoảng cách từ viền panel

        // Chiều rộng mỗi nút
        int truck1Width = 60;
        int truck2Width = 80;
        int truck3Width = 100;

        int totalWidth = truck1Width + truck2Width + truck3Width;
        int remainingSpace = panelWidth - totalWidth;
        int gap = remainingSpace / 4; // Khoảng cách giữa các nút và mép panel

        // Đặt vị trí các nút sao cho cách đều nhau và cách đều mép
        int x1 = gap; // Vị trí nút truck1
        int x2 = x1 + truck1Width + gap; // Vị trí nút truck2
        int x3 = x2 + truck2Width + gap; // Vị trí nút truck3

        // Thiết lập vị trí và kích thước cho từng nút
        truck1.setBounds(x1, spacing, truck1Width, buttonHeight);
        truck2.setBounds(x2, spacing, truck2Width, buttonHeight);
        truck3.setBounds(x3, spacing, truck3Width, buttonHeight);

        // Tạo JLabel để hiển thị thông báo
        JLabel messageLabel = new JLabel("");
        messageLabel.setBounds(10, spacing + buttonHeight + 20, panelWidth - 20, 30); // Vị trí và kích thước của JLabel
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        transportPanel.add(messageLabel);
        
        // Thêm sự kiện ActionListener cho các nút
        truck1.addActionListener(e -> {
            setSelectedTruck("Truck 1");
            messageLabel.setText("Truck 1 is selected with a load capacity of 1 tons!");
        });
        truck2.addActionListener(e -> {
            setSelectedTruck("Truck 2");
            messageLabel.setText("Truck 2 is selected with a load capacity of 10 tons!");
        });
        truck3.addActionListener(e -> {
            setSelectedTruck("Truck 3");
            messageLabel.setText("Truck 3 is selected with a load capacity of 30 tons!");
        });

        // Thêm các nút vào panel
        transportPanel.add(truck1);
        transportPanel.add(truck2);
        transportPanel.add(truck3);

        // Thêm panel vào chính giao diện của LocationSearchPanel
        transportPanel.setBounds(5, 20, 300, 150);
        add(transportPanel);
    }

    public void setSelectedTruck(String truck) {
        this.selectedTruck = truck;
    }

    public String getSearchFieldText() {
        return searchField.getText();
    }

    public interface SearchFieldListener {
        void onSearchFieldUpdate(String newText);
    }    
    
    private final List<SearchFieldListener> listeners = new ArrayList<>();

    public void addSearchFieldListener(SearchFieldListener listener) {
        listeners.add(listener);
    }

    public void removeSearchFieldListener(SearchFieldListener listener) {
        listeners.remove(listener);
    }

    private void notifySearchFieldListeners(String newText) {
        for (SearchFieldListener listener : listeners) {
            listener.onSearchFieldUpdate(newText);
        }
    }

    private List<String> fetchLocationsFromDatabase() {
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
            JOptionPane.showMessageDialog(this, "Error fetching locations from database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return locations;
    }

    private List<Object[]> fetchOrderListForWarehouse(int warehouseID, String selectedTruck) {
        List<Object[]> data = new ArrayList<>();
        String sql = """
        SELECT 
                o.Order_ID,
                c.Customer_Name,
                c.Address AS Delivery_Address,
                c.Address_Region AS Delivery_Region,
                SUM(od.Quantity * p.Weight) AS Total_Weight
            FROM Orders o
            JOIN Receives r       ON o.Order_ID = r.Order_ID
            JOIN Customer c       ON o.Customer_ID = c.Customer_ID
            JOIN Order_Detail od  ON o.Order_ID = od.Order_ID
            JOIN Product p        ON od.Product_ID = p.Product_ID
            WHERE r.Warehouse_ID = ?
            GROUP BY 
                o.Order_ID, 
                c.Customer_Name, 
                c.Address, 
                c.Address_Region
            ORDER BY SUM(od.Quantity * p.Weight) ASC;
    """;

        double maxCapacity; // Maximum weight capacity based on truck type
        switch (selectedTruck) {
            case "Truck 1":
                maxCapacity = 1_00.0; // 100 kg
                break;
            case "Truck 2":
                maxCapacity = 10_000.0; // 10 tons = 10000 kg
                break;
            case "Truck 3":
                maxCapacity = 30_000.0; // 30 tons = 30000 kg
                break;
            default:
                JOptionPane.showMessageDialog(
                        null,
                        "Invalid truck selection.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return data; // Return empty list
        }

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, warehouseID);
            try (ResultSet rs = ps.executeQuery()) {
                double sumSoFar = 0.0;
                while (rs.next()) {
                    int orderID       = rs.getInt("Order_ID");
                    String customer   = rs.getString("Customer_Name");
                    String address    = rs.getString("Delivery_Address");
                    String region     = rs.getString("Delivery_Region");
                    double totalWeight = rs.getDouble("Total_Weight");

                    // Stop adding orders if exceeding truck capacity
                    if (sumSoFar + totalWeight > maxCapacity) {
                        break;
                    }

                    // Accumulate weight and add order details
                    sumSoFar += totalWeight;
                    data.add(new Object[] {
                            orderID, customer, address, region, totalWeight
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    null,
                    "Error fetching order list: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
        return data;
    }

    public String getSelectedTruck() {
        return selectedTruck;
    }

    // Lớp ModernScrollBarUI để tùy chỉnh thanh cuộn
    static class ModernScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = new Color(50, 50, 50); // Màu thanh trượt (xám đậm)
            this.trackColor = new Color(200, 200, 200); // Màu nền thanh cuộn (xám nhạt)
        }

        @Override
        protected Dimension getMinimumThumbSize() {
            return new Dimension(2, 10); // Chiều rộng tối thiểu: 5 (hẹp hơn)
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createInvisibleButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createInvisibleButton();
        }

        private JButton createInvisibleButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0)); // Ẩn nút tăng/giảm
            return button;
        }
    }
}
