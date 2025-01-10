package Map;

import Routing.RoutingData;
import Routing.RoutingService;

import java.awt.Point;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.VirtualEarthTileFactoryInfo;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCenter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.WaypointPainter;
import Functions.DatabaseConnection;
import wayPoint.MyWaypoint;
import wayPoint.WaypointRender;
import wayPoint.EventsWaypoint;
import wayPoint.Warehouse_Position;

public class MapMain extends JFrame {

    private final Set<MyWaypoint> waypoints = new HashSet<>();
    private final List<RoutingData> routingData = new ArrayList<>();
    private EventsWaypoint event;
    private Point mousePosition;
    private static  LocationSearchPanel locationSearchPanel;
    private final Map<GeoPosition, String> warehouseNames = new HashMap<>();

    
    public MapMain() {
        this.initComponents();
        this.init();

        menuHandler = new SlideMenuHandler(this, MenuButton);
    }
    
    private void init(){
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        this.jXMapViewer.setTileFactory(tileFactory);
        GeoPosition geo = new GeoPosition(10.8020094,106.6645009);
        this.jXMapViewer.setAddressLocation(geo);
        this.jXMapViewer.setZoom(10);
        
        MouseInputListener mm = new PanMouseInputListener(this.jXMapViewer);
        this.jXMapViewer.addMouseListener(mm);
        this.jXMapViewer.addMouseMotionListener(mm);
        this.jXMapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCenter(this.jXMapViewer));
        this.event = this.getEvent();
    }
    
    private void addWaypoint(MyWaypoint waypoint) {
        // Xóa phần code xóa waypoint cũ dựa trên PointType

        for (MyWaypoint d : this.waypoints) {
            this.jXMapViewer.remove(d.getButton());
        }

        // Thêm waypoint mới vào danh sách
        this.waypoints.add(waypoint);
        this.initWaypoint();
    }

    
private void initWaypoint() {
    WaypointPainter<MyWaypoint> wp = new WaypointRender();
    wp.setWaypoints(this.waypoints);
    this.jXMapViewer.setOverlayPainter(wp);

    for (MyWaypoint d : waypoints) {
        this.jXMapViewer.add(d.getButton());
    }

    // Tìm đường đi ngắn nhất nếu có điểm START
    GeoPosition start = null;
    List<GeoPosition> destinations = new ArrayList<>();

    for (MyWaypoint w : waypoints) {
        if (w.getPointType() == MyWaypoint.PointType.START) {
            start = w.getPosition();
        } else {
            destinations.add(w.getPosition());
        }
    }

    if (start != null && !destinations.isEmpty()) {
        findShortestPaths(start, destinations);
    }
}

private void findShortestPaths(GeoPosition start, List<GeoPosition> destinations) {
    RoutingService routingService = RoutingService.getInstance(); // Giả sử bạn có dịch vụ tìm đường
    List<RoutingData> allRoutes = new ArrayList<>();

    for (GeoPosition destination : destinations) {
        // Tính đường đi từ điểm xuất phát đến từng kho
        List<RoutingData> route = routingService.routing(
            start.getLatitude(), start.getLongitude(),
            destination.getLatitude(), destination.getLongitude()
        );

        if (route != null) {
            allRoutes.addAll(route); // Thêm dữ liệu đường đi
        }
    }

    // Hiển thị tất cả các tuyến đường
    this.jXMapViewer.setRoutingData(allRoutes);
    JOptionPane.showMessageDialog(this, "Shortest paths to warehouses have been calculated.");
}
   
    
    
    private void clearWaypoint(){
        for(MyWaypoint d : waypoints){
            jXMapViewer.remove(d.getButton());
        }
        routingData.clear();
        waypoints.clear();
        initWaypoint();
    }
    
    private EventsWaypoint getEvent(){
        return new EventsWaypoint() {
            @Override
            public void selected(MyWaypoint waypoint) {
                JOptionPane.showMessageDialog(MapMain.this, waypoint.getName());
            }
        };
    }
 
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        JPopupMenu jPopupMenu1 = new JPopupMenu();
        JMenuItem menuEnd = new JMenuItem();
        JMenuItem menuStart = new JMenuItem();
        jXMapViewer = new Routing.JXMapViewerCustom();
        JButton cmdClear = new JButton();
        comboMapType = new JComboBox<>();
        // Variables declaration - do not modify//GEN-BEGIN:variables
        JButton closeButton = new JButton();
        JButton showWareHouse = new JButton();
        JButton WHFindShortestWay = new JButton();
        nextPosition = new JButton();
        MenuButton = new JButton();

        menuEnd.setText("End");
        menuEnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuEndActionPerformed(evt);
            }
        });
        jPopupMenu1.add(menuEnd);

        menuStart.setText("Start");
        menuStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuStartActionPerformed(evt);
            }
        });
        jPopupMenu1.add(menuStart);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        jXMapViewer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jXMapViewerMouseReleased(evt);
            }
        });

        cmdClear.setText("Clear Waypoint");
        cmdClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdClearActionPerformed(evt);
            }
        });

        comboMapType.setModel(new DefaultComboBoxModel<>(new String[] { "Road Map", "Hybrid", "Satellite" }));
        comboMapType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboMapTypeActionPerformed(evt);
            }
        });

        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CloseButtonActionPerformed(evt);
            }
        });

        showWareHouse.setText("Show WareHouse");
        showWareHouse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ShowWareHouseActionPerformed(evt);
            }
        });

        WHFindShortestWay.setText("Show The Ways");
        WHFindShortestWay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                WHFindShortestWayActionPerformed(evt);
            }
        });

        nextPosition.setText("Next Destination ");
        nextPosition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextPositionActionPerformed(evt);
            }
        });

        MenuButton.setText("☰");
        MenuButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuButtonActionPerformed(evt);
            }
        });

        GroupLayout jXMapViewerLayout = new GroupLayout(jXMapViewer);
        jXMapViewer.setLayout(jXMapViewerLayout);
        jXMapViewerLayout.setHorizontalGroup(
            jXMapViewerLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, jXMapViewerLayout.createSequentialGroup()
                .addComponent(MenuButton, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 265, Short.MAX_VALUE)
                .addComponent(cmdClear)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(showWareHouse)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(WHFindShortestWay)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nextPosition)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboMapType, GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(closeButton)
                .addGap(12, 12, 12))
        );
        jXMapViewerLayout.setVerticalGroup(
            jXMapViewerLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jXMapViewerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jXMapViewerLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(comboMapType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(closeButton)
                    .addComponent(nextPosition)
                    .addComponent(WHFindShortestWay)
                    .addComponent(showWareHouse)
                    .addComponent(cmdClear)
                    .addComponent(MenuButton))
                .addContainerGap(537, Short.MAX_VALUE))
        );

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jXMapViewer, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jXMapViewer, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void comboMapTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboMapTypeActionPerformed
        TileFactoryInfo info;
        int index = comboMapType.getSelectedIndex();
        if (index == 0){
            info = new OSMTileFactoryInfo();
        }
        else if (index == 2){
            info = new VirtualEarthTileFactoryInfo(VirtualEarthTileFactoryInfo.HYBRID);
        }else{
            info = new VirtualEarthTileFactoryInfo(VirtualEarthTileFactoryInfo.SATELLITE);
        }
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        jXMapViewer.setTileFactory(tileFactory);
    }//GEN-LAST:event_comboMapTypeActionPerformed

    private void cmdClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdClearActionPerformed
        clearWaypoint();
    }//GEN-LAST:event_cmdClearActionPerformed

    private void menuEndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuEndActionPerformed
//        GeoPosition geop = jXMapViewer.convertPointToGeoPosition(mousePosition);
//        MyWaypoint wayPoint = new MyWaypoint("End Location", MyWaypoint.PointType.END, event, new GeoPosition(geop.getLatitude(), geop.getLongitude()));
//        addWaypoint(wayPoint);
    }//GEN-LAST:event_menuEndActionPerformed

    private void menuStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuStartActionPerformed
//        GeoPosition geop = jXMapViewer.convertPointToGeoPosition(mousePosition);
//        MyWaypoint wayPoint = new MyWaypoint("Start Location", MyWaypoint.PointType.START, event, new GeoPosition(geop.getLatitude(), geop.getLongitude()));
//        addWaypoint(wayPoint);
    }//GEN-LAST:event_menuStartActionPerformed

    private void jXMapViewerMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jXMapViewerMouseReleased
//        if(SwingUtilities.isRightMouseButton(evt)){
//            mousePosition = evt.getPoint();
//            jPopupMenu1.show(jXMapViewer, evt.getX(), evt.getY());
//        }
    }//GEN-LAST:event_jXMapViewerMouseReleased

    private void CloseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CloseButtonActionPerformed
        this.dispose(); 
    }//GEN-LAST:event_CloseButtonActionPerformed

    private void ShowWareHouseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ShowWareHouseActionPerformed
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseConnection.getInstance().getConnection();
            String query = "SELECT Warehouse_Name, Latitude, Longitude FROM Warehouse";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
        
            // Danh sách kho hàng
            List<Warehouse_Position> warehouses = new ArrayList<>();
            while (resultSet.next()) {
                String name = resultSet.getString("Warehouse_Name");
                double latitude = resultSet.getDouble("Latitude");
                double longitude = resultSet.getDouble("Longitude");

                // Tạo GeoPosition
                GeoPosition gp = new GeoPosition(latitude, longitude);

                // Thêm vào danh sách warehouse (nếu bạn vẫn cần)
                warehouses.add(new Warehouse_Position(name, latitude, longitude));

                // Lưu tên kho vào map (có thể thêm "'s warehouse" để in cho đẹp)
                warehouseNames.put(gp, name + "'s warehouse");
            }
        
            // Thêm các waypoint từ danh sách
            for (Warehouse_Position warehouse : warehouses) {
                addWaypoint(warehouse.toWaypoint(event));
            }
        
            JOptionPane.showMessageDialog(this, "Warehouses loaded on the map.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading warehouses: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                // Đóng các tài nguyên
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error closing database resources: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_ShowWareHouseActionPerformed



    private void WHFindShortestWayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_WHFindShortestWayActionPerformed
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Get selected truck and its capacity
            String selectedTruck = menuHandler.getLocationSearchPanel().getSelectedTruck();
            double maxCapacity; // Truck's max capacity
            switch (selectedTruck) {
                case "Truck 1":
                    maxCapacity = 1_00.0; // 100 kg
                    break;
                case "Truck 2":
                    maxCapacity = 10_000.0; // 10 tons
                    break;
                case "Truck 3":
                    maxCapacity = 30_000.0; // 30 tons
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "Invalid truck selection.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
            }

            String startWarehouse = menuHandler.getLocationSearchPanel().getSearchFieldText();
            if (startWarehouse == null || startWarehouse.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a starting warehouse in the search field.");
                return;
            }

            // 1) Connect to DB
            connection = DatabaseConnection.getInstance().getConnection();

            // 2) Get start warehouse details
            String startQuery = """
            SELECT Warehouse_ID, Latitude, Longitude 
            FROM Warehouse 
            WHERE Warehouse_Name = ?
        """;
            statement = connection.prepareStatement(startQuery);
            statement.setString(1, startWarehouse);
            resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                JOptionPane.showMessageDialog(this, "Starting warehouse not found in the database.");
                return;
            }

            int startWarehouseID = resultSet.getInt("Warehouse_ID");
            double startLatitude = resultSet.getDouble("Latitude");
            double startLongitude = resultSet.getDouble("Longitude");
            GeoPosition startPos = new GeoPosition(startLatitude, startLongitude);

            resultSet.close();
            statement.close();

            // 3) Get customer regions and their total weights
            String regionQuery = """
            SELECT DISTINCT c.Address_Region, SUM(od.Quantity * p.Weight) AS Total_Weight
            FROM Orders o
            JOIN Receives r  ON o.Order_ID = r.Order_ID
            JOIN Customer c  ON o.Customer_ID = c.Customer_ID
            JOIN Order_Detail od ON o.Order_ID = od.Order_ID
            JOIN Product p  ON od.Product_ID = p.Product_ID
            WHERE r.Warehouse_ID = ?
            GROUP BY c.Address_Region
            HAVING SUM(od.Quantity * p.Weight) <= ?
        """;
            statement = connection.prepareStatement(regionQuery);
            statement.setInt(1, startWarehouseID);
            statement.setDouble(2, maxCapacity);
            resultSet = statement.executeQuery();

            Set<String> neededRegions = new HashSet<>();
            while (resultSet.next()) {
                String region = resultSet.getString("Address_Region");
                if (region != null && !region.isBlank()) {
                    neededRegions.add(region.trim());
                }
            }
            resultSet.close();
            statement.close();

            if (neededRegions.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No customer regions found or exceed truck capacity for the selected warehouse.",
                        "No Regions",
                        JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }

            // 4) Find intermediate warehouses matching regions
            List<GeoPosition> intermediatePoints = new ArrayList<>();
            List<Warehouse_Position> warehouses = new ArrayList<>();

            for (String region : neededRegions) {
                String warehouseQuery = """
                SELECT Warehouse_Name, Latitude, Longitude
                FROM Warehouse
                WHERE Warehouse_Name = ?
                AND Warehouse_Name != ?
            """;
                PreparedStatement ps2 = connection.prepareStatement(warehouseQuery);
                ps2.setString(1, region);
                ps2.setString(2, startWarehouse);

                try (ResultSet rs2 = ps2.executeQuery()) {
                    while (rs2.next()) {
                        String name = rs2.getString("Warehouse_Name");
                        double lat = rs2.getDouble("Latitude");
                        double lon = rs2.getDouble("Longitude");
                        GeoPosition pos = new GeoPosition(lat, lon);

                        intermediatePoints.add(pos);
                        warehouses.add(new Warehouse_Position(name, lat, lon));
                    }
                }
                ps2.close();
            }

            if (intermediatePoints.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No intermediate warehouses found matching the customers' regions.",
                        "No Warehouses",
                        JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }

            // Route through warehouses
            this.finalDestination = null; // Reset final destination
            this.finalDestination = intermediatePoints.getLast();
            findPathThroughWarehouses(startPos, intermediatePoints, this.finalDestination);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading warehouses: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error closing database resources: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_WHFindShortestWayActionPerformed

    private void nextPositionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextPositionActionPerformed
        showNextRoute();
    }//GEN-LAST:event_nextPositionActionPerformed

    private void MenuButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuButtonActionPerformed
        menuHandler.toggleMenu();
    }//GEN-LAST:event_MenuButtonActionPerformed
    
    
private void showNextRoute() {
    if (remainingWarehouses == null || remainingWarehouses.isEmpty()) {
        if (finalDestination != null && !currentWarehouse.equals(finalDestination)) {
            // Nếu chưa đến điểm cuối, tạo tuyến đường đến điểm cuối
            List<RoutingData> routeToEnd = RoutingService.getInstance().routing(
                currentWarehouse.getLatitude(), currentWarehouse.getLongitude(),
                finalDestination.getLatitude(), finalDestination.getLongitude()
            );

            if (routeToEnd != null && !routeToEnd.isEmpty()) {
                this.jXMapViewer.setRoutingData(routeToEnd);

                // Lấy tên cho finalDestination
                String finalName = warehouseNames.getOrDefault(finalDestination, "the final warehouse");
                JOptionPane.showMessageDialog(this, 
                    "Route to the final destination (" + finalName + ") displayed."
                );
                currentWarehouse = finalDestination;
                return;
            }else{
                JOptionPane.showMessageDialog(this, 
                    "No route data available to the final destination."
                );
                return;
            }
        }

        JOptionPane.showMessageDialog(this, "All warehouses have been visited.");
        nextPosition.setVisible(false);
        return;
    }

    // Tìm kho gần nhất từ kho hiện tại
    GeoPosition nextWarehouse = findNearestWarehouse(currentWarehouse, remainingWarehouses);

    if (nextWarehouse == null) {
        JOptionPane.showMessageDialog(this, "No valid warehouses left to visit.");
        nextPosition.setVisible(false);
        return;
    }

    List<RoutingData> route = RoutingService.getInstance().routing(
        currentWarehouse.getLatitude(), currentWarehouse.getLongitude(),
        nextWarehouse.getLatitude(), nextWarehouse.getLongitude()
    );

    if (route == null || route.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No route data available for the next warehouse.");
        return;
    }

    // Hiển thị tuyến đường lên bản đồ
    this.jXMapViewer.setRoutingData(route);
    String currentName = warehouseNames.getOrDefault(currentWarehouse, "Current warehouse");
    String nextName    = warehouseNames.getOrDefault(nextWarehouse, "Next warehouse");

    // In thông báo
    JOptionPane.showMessageDialog(this,
        "Route to the " + currentName + " has been displayed.\n"
      + "And next warehouse is " + nextName + "."
    );

    // Cập nhật kho hiện tại và loại bỏ kho đã đến khỏi danh sách
    currentWarehouse = nextWarehouse;
    remainingWarehouses.remove(nextWarehouse);
}

private void findPathThroughWarehouses(GeoPosition start, List<GeoPosition> waypoints, GeoPosition end) {
    RoutingService routingService = RoutingService.getInstance();
    if (start == null || waypoints == null || waypoints.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please define a valid start point and waypoints.");
        return;
    }

    // GÁN THẲNG 2 BIẾN CLASS-LEVEL Ở ĐÂY:
    this.currentWarehouse = start;              // => Dùng cho showNextRoute
    this.remainingWarehouses = new ArrayList<>(waypoints);

    // Tìm warehouse nearest
    GeoPosition nearest = findNearestWarehouse(start, waypoints);
    if (nearest == null) {
        JOptionPane.showMessageDialog(this, "No valid warehouses to route.");
        return;
    }

    // Tính route
    List<RoutingData> route = routingService.routing(
        start.getLatitude(), start.getLongitude(),
        nearest.getLatitude(), nearest.getLongitude()
    );

    if (route == null || route.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No route data available.");
        return;
    }

    // Vẽ route
    jXMapViewer.setRoutingData(route);
    JOptionPane.showMessageDialog(this, "Route to the nearest warehouse displayed.");

    // Cập nhật currentWarehouse = nearest
    this.currentWarehouse = nearest;

    // Xóa kho vừa đến khỏi danh sách
    this.remainingWarehouses.remove(nearest);

    // Nếu không còn kho nào & nearest != end => add end
    if (this.remainingWarehouses.isEmpty() && !nearest.equals(end)) {
        this.remainingWarehouses.add(end);
    }
}


// Tìm kho gần nhất
private GeoPosition findNearestWarehouse(GeoPosition current, List<GeoPosition> warehouses) {
    GeoPosition nearest = null;
    double shortestDistance = Double.MAX_VALUE;

    for (GeoPosition warehouse : warehouses) {
        double distance = calculateDistance(current, warehouse);
        if (distance < shortestDistance) {
            shortestDistance = distance;
            nearest = warehouse;
        }
    }

    return nearest;
}

// Tính khoảng cách giữa hai điểm
private double calculateDistance(GeoPosition p1, GeoPosition p2) {
    double latDiff = Math.toRadians(p2.getLatitude() - p1.getLatitude());
    double lonDiff = Math.toRadians(p2.getLongitude() - p1.getLongitude());
    double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
               Math.cos(Math.toRadians(p1.getLatitude())) * Math.cos(Math.toRadians(p2.getLatitude())) *
               Math.sin(lonDiff / 2) * Math.sin(lonDiff / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    double earthRadius = 6371; // km
    return earthRadius * c;
}


    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MapMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                MapMain map = new MapMain();
                // map.WHFindShortestWay.doClick(); 
                map.setVisible(true);
            }
        });
    }

    private GeoPosition currentWarehouse;
    private List<GeoPosition> remainingWarehouses;
    private GeoPosition finalDestination;
    private final boolean menuVisible = false; // Biến cờ để kiểm tra trạng thái menu
    private JPanel overlayPanel; // Lớp mờ
    private JPanel sideMenu; // Menu trượt
    private final SlideMenuHandler menuHandler;
    private JButton MenuButton;
    private JComboBox<String> comboMapType;
    private Routing.JXMapViewerCustom jXMapViewer;
    private JButton nextPosition;
    // End of variables declaration//GEN-END:variables
}
