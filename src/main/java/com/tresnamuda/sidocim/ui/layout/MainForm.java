/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tresnamuda.sidocim.ui.layout;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.util.UIScale;
import com.tresnamuda.sidocim.App;
import com.tresnamuda.sidocim.ui.menu.Menu;
import com.tresnamuda.sidocim.ui.pages.OuterPanel;
import com.tresnamuda.sidocim.ui.pages.dashboard.DashboardPage;
import com.tresnamuda.sidocim.ui.pages.stamp_depo.StampDepoProcessPage;
import com.tresnamuda.sidocim.ui.pages.stamp_depo.StampDepoTemplatePage;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author dzil
 */
public class MainForm extends JLayeredPane {

    private Menu menu;
    private JPanel panelBody;
    private JButton menuButton;
    private OuterPanel outerPanel = null;

    /**
     * Creates new form MainForm
     */
    public MainForm() {
        init();
    }

    private void init() {

        setBorder(new EmptyBorder(5, 5, 5, 5));
        setLayout(new MainForm.MainFormLayout());
        
        menu = new Menu();
        panelBody = new JPanel(new BorderLayout());
        
        menuButton = new JButton(new FlatSVGIcon("img/menu_left.svg", 0.8f));
        menuButton.putClientProperty(FlatClientProperties.STYLE, ""
                + "background:$Menu.button.background;"
                + "arc:999;"
                + "focusWidth:0;"
                + "borderWidth:0");
        menuButton.addActionListener((ActionEvent e) -> {
            setMenuFull(!menu.isMenuFull());
        });

        initMenuEvent();
        setLayer(menuButton, JLayeredPane.POPUP_LAYER);
        add(menuButton);
        add(menu);
        add(panelBody);
        
        this.showForm(new OuterPanel(new DashboardPage(), "Dashboard"));
    }

    private void setMenuFull(boolean full) {
        String icon = full ? "menu_left.svg" : "menu_right.svg";
        menuButton.setIcon(new FlatSVGIcon("img/" + icon, 0.8f));
        menu.setMenuFull(full);
        revalidate();
    }

    public void showForm(Component component) {
        panelBody.removeAll();
        panelBody.add(component);
        panelBody.repaint();
        panelBody.revalidate();
    }

    private void initMenuEvent() {
        menu.addMenuEvent((int index, int subIndex) -> {
            switch (index) {
                case 0 -> outerPanel = new OuterPanel(new DashboardPage(), "Dashboard");
                case 1 -> outerPanel = new OuterPanel(new StampDepoProcessPage(), "Stamp Depo Process");
                default -> {
                }
            }
            App.showForm(outerPanel);
        });

    }

    private class MainFormLayout implements LayoutManager {

        @Override
        public void addLayoutComponent(String name, Component comp) {
        }

        @Override
        public void removeLayoutComponent(Component comp) {
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                return new Dimension(5, 5);
            }
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                return new Dimension(0, 0);
            }
        }

        @Override
        public void layoutContainer(Container parent) {
            synchronized (parent.getTreeLock()) {
                Insets insets = UIScale.scale(parent.getInsets());
                int x = insets.left;
                int y = insets.top;
                int width = parent.getWidth() - (insets.left + insets.right);
                int height = parent.getHeight() - (insets.top + insets.bottom);
                int menuWidth = UIScale.scale(menu.isMenuFull() ? menu.getMenuMaxWidth() : menu.getMenuMinWidth());
                menu.setBounds(x, y, menuWidth, height);
                int menuButtonWidth = menuButton.getPreferredSize().width;
                int menuButtonHeight = menuButton.getPreferredSize().height;
                int menuX = (int) (x + menuWidth - (menuButtonWidth * (menu.isMenuFull() ? 0.5f : 0.3f)));
                menuButton.setBounds(menuX, UIScale.scale(30), menuButtonWidth, menuButtonHeight);

                int gap = UIScale.scale(5);
                int bodyWidth = width - menuWidth - gap;
                int bodyHeight = height;
                int bodyx = x + menuWidth + gap;
                int bodyy = y;
                panelBody.setBounds(bodyx, bodyy, bodyWidth, bodyHeight);
            }
        }
    }

}
