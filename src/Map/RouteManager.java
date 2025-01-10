package Map;

import Routing.RoutingData;
import Routing.RoutingService;
import org.jxmapviewer.viewer.GeoPosition;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class RouteManager {

    private GeoPosition currentWarehouse;
    private List<GeoPosition> remainingWarehouses;
    private final GeoPosition finalDestination;
    private final Routing.JXMapViewerCustom mapViewer;

    public RouteManager(Routing.JXMapViewerCustom mapViewer, GeoPosition finalDestination) {
        this.mapViewer = mapViewer;
        this.finalDestination = finalDestination;
    }

    public void showNextRoute() {
        if (remainingWarehouses == null || remainingWarehouses.isEmpty()) {
            if (!currentWarehouse.equals(finalDestination)) {
                // Nếu chưa đến điểm cuối, tạo tuyến đường đến điểm cuối
                List<RoutingData> routeToEnd = RoutingService.getInstance().routing(
                    currentWarehouse.getLatitude(), currentWarehouse.getLongitude(),
                    finalDestination.getLatitude(), finalDestination.getLongitude()
                );

                if (routeToEnd != null && !routeToEnd.isEmpty()) {
                    mapViewer.setRoutingData(routeToEnd);
                    JOptionPane.showMessageDialog(null, "Route to the final destination displayed.");
                    currentWarehouse = finalDestination;
                    return;
                } else {
                    JOptionPane.showMessageDialog(null, "No route data available to the final destination.");
                    return;
                }
            }

            JOptionPane.showMessageDialog(null, "All warehouses have been visited.");
            return;
        }

        // Tìm kho gần nhất từ kho hiện tại
        GeoPosition nextWarehouse = findNearestWarehouse(currentWarehouse, remainingWarehouses);

        if (nextWarehouse == null) {
            JOptionPane.showMessageDialog(null, "No valid warehouses left to visit.");
            return;
        }

        List<RoutingData> route = RoutingService.getInstance().routing(
            currentWarehouse.getLatitude(), currentWarehouse.getLongitude(),
            nextWarehouse.getLatitude(), nextWarehouse.getLongitude()
        );

        if (route == null || route.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No route data available for the next warehouse.");
            return;
        }

        // Hiển thị tuyến đường lên bản đồ
        mapViewer.setRoutingData(route);
        JOptionPane.showMessageDialog(null, "Route to the next warehouse displayed.");

        // Cập nhật kho hiện tại và loại bỏ kho đã đến khỏi danh sách
        currentWarehouse = nextWarehouse;
        remainingWarehouses.remove(nextWarehouse);
    }

    public void findPathThroughWarehouses(GeoPosition start, List<GeoPosition> waypoints, GeoPosition end) {
        RoutingService routingService = RoutingService.getInstance();

        if (start == null || waypoints == null || waypoints.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please define a valid start point and waypoints.");
            return;
        }

        // Tìm kho gần nhất và hiển thị tuyến đường đầu tiên
        GeoPosition nearest = findNearestWarehouse(start, waypoints);
        if (nearest != null) {
            List<RoutingData> route = routingService.routing(
                start.getLatitude(), start.getLongitude(),
                nearest.getLatitude(), nearest.getLongitude()
            );

            if (route != null && !route.isEmpty()) {
                mapViewer.setRoutingData(route);
                JOptionPane.showMessageDialog(null, "Route to the nearest warehouse displayed.");

                currentWarehouse = nearest;

                // Xóa kho đã đến khỏi danh sách (trừ điểm cuối)
                remainingWarehouses = new ArrayList<>(waypoints);
                remainingWarehouses.remove(nearest);

                // Nếu không còn kho nào và điểm cuối chưa được xử lý, thêm điểm cuối vào danh sách
                if (remainingWarehouses.isEmpty() && !nearest.equals(end)) {
                    remainingWarehouses.add(end);
                }
            } else {
                JOptionPane.showMessageDialog(null, "No route data available.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "No valid warehouses to route.");
        }
    }

    public void findShortestWay(List<GeoPosition> waypoints) {
        GeoPosition start = waypoints.removeFirst(); // Điểm bắt đầu
        GeoPosition end = waypoints.getLast(); // Điểm kết thúc
        findPathThroughWarehouses(start, waypoints, end);
    }

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

    public void setRemainingWarehouses(List<GeoPosition> warehouses) {
        this.remainingWarehouses = warehouses;
    }

    public void setCurrentWarehouse(GeoPosition warehouse) {
        this.currentWarehouse = warehouse;
    }
}
