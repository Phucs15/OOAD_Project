package Map;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class SlideMenuHandler {
    private boolean menuVisible = false; 
    private JPanel overlayPanel; 
    private JPanel underlayPanel; 
    private JPanel sideMenu; 
    private final JFrame frame;
    private final JButton menuButton;
    private final LocationSearchPanel locationSearchPanel;

    public SlideMenuHandler(JFrame frame, JButton menuButton) {
        this.frame = frame;
        this.menuButton = menuButton;

        // Tạo LocationSearchPanel 1 lần, gán cho biến toàn cục
        this.locationSearchPanel = new LocationSearchPanel();

        initializePanels();
    }

    private void initializePanels() {
        // Tạo lớp mờ dưới menu
        underlayPanel = new JPanel();
        underlayPanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        underlayPanel.setBackground(new Color(0, 0, 0, 50)); 
        underlayPanel.setLayout(null);
        underlayPanel.setVisible(false); 
        frame.getContentPane().add(underlayPanel);
        frame.getContentPane().setComponentZOrder(underlayPanel, 0); 

        // Tạo menu trượt
        sideMenu = new JPanel();
        sideMenu.setBounds(-4000, 0, 400, frame.getHeight()); 
        sideMenu.setBackground(Color.WHITE);
        sideMenu.setLayout(new BoxLayout(sideMenu, BoxLayout.Y_AXIS));
        sideMenu.setVisible(false); // Ban đầu ẩn
        
        // add locationSearchPanel đã gán ở constructor
        sideMenu.add(Box.createVerticalStrut(0));
        sideMenu.add(locationSearchPanel);

        frame.getContentPane().add(sideMenu);
        frame.getContentPane().setComponentZOrder(sideMenu, 0); // Đặt menu trên lớp mờ dưới

        // Tạo lớp mờ trên bản đồ
        overlayPanel = new JPanel();
        overlayPanel.setBounds(0, 0, frame.getWidth(), frame.getHeight()); // Bao phủ toàn bộ frame
        overlayPanel.setBackground(new Color(0, 0, 0, 100)); // Màu đen mờ với độ trong suốt
        overlayPanel.setLayout(null);
        overlayPanel.setVisible(false); // Ban đầu ẩn
        overlayPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                hideMenu(); // Ẩn menu khi nhấn vào lớp mờ
            }
        });
        frame.getContentPane().add(overlayPanel);
        frame.getContentPane().setComponentZOrder(overlayPanel, 1); // Đặt lớp mờ ở trên cùng
    }

    public void toggleMenu() {
        if (menuVisible) {
            hideMenu();
        } else {
            showMenu();
            menuButton.setVisible(false); // Ẩn nút MenuButton
        }
    }

    private void showMenu() {
        if (menuVisible) return; // Ngăn việc gọi lại khi menu đã hiển thị

        underlayPanel.setVisible(true);
        overlayPanel.setVisible(true);
        sideMenu.setVisible(true);

        // Hiệu ứng trượt
        Timer slideTimer = new Timer(5, new ActionListener() {
            int x = -300;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (x < 0) {
                    x += 20; // Tăng tốc độ trượt
                    sideMenu.setBounds(x, 0, 300, frame.getHeight());
                } else {
                    ((Timer) e.getSource()).stop();
                    menuVisible = true; // Cập nhật trạng thái menu
                }
            }
        });
        slideTimer.start();
    }

    private void hideMenu() {
        if (!menuVisible) return; // Ngăn việc gọi lại khi menu đã ẩn

        // Hiệu ứng trượt ngược
        Timer slideTimer = new Timer(5, new ActionListener() {
            int x = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (x > -300) {
                    x -= 20; // Tăng tốc độ trượt
                    sideMenu.setBounds(x, 0, 300, frame.getHeight());
                } else {
                    ((Timer) e.getSource()).stop();
                    sideMenu.setVisible(false);
                    overlayPanel.setVisible(false);
                    underlayPanel.setVisible(false);
                    menuButton.setVisible(true); // Hiển thị lại nút MenuButton
                    menuVisible = false; // Cập nhật trạng thái menu
                }
            }
        });
        slideTimer.start();
    }

    public LocationSearchPanel getLocationSearchPanel() {
        return this.locationSearchPanel;
    }    

}
