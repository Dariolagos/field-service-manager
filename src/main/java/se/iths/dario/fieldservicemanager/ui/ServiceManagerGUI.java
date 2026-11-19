package se.iths.dario.fieldservicemanager.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import se.iths.dario.fieldservicemanager.model.Appliance;
import se.iths.dario.fieldservicemanager.model.ServiceOrder;
import se.iths.dario.fieldservicemanager.model.SparePart;
import se.iths.dario.fieldservicemanager.model.WashingMachine;
import se.iths.dario.fieldservicemanager.service.DispatchService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ServiceManagerGUI extends JFrame {
    private final DispatchService service;
    private final DefaultTableModel orderTableModel;
    private final JTable orderTable;
    private final JLabel revenueLabel;

    public ServiceManagerGUI(DispatchService service) {
        this.service = service;

        setTitle("Field Service Manager");
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Toppmeny med rubrik och totala intäkter
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Field Service Manager – Arbetsordrar");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        revenueLabel = new JLabel("Totala intäkter: 0.0 kr");
        revenueLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(revenueLabel, BorderLayout.EAST);

        // Tabell i mitten för ordrar
        String[] columns = {"Order-ID", "Kund", "Maskin", "Garanti", "Status", "Kostnad (kr)"};
        orderTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        orderTable = new JTable(orderTableModel);
        orderTable.setRowHeight(28);
        JScrollPane scrollPane = new JScrollPane(orderTable);

        // Knappar längst ner
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JButton newOrderBtn = new JButton("+ Ny arbetsorder");
        JButton addPartBtn = new JButton("Montera reservdel");
        JButton completeBtn = new JButton("Klarmarkera order");
        JButton diagnosticsBtn = new JButton("Kör diagnostik");

        newOrderBtn.addActionListener(e -> openNewOrderDialog());
        addPartBtn.addActionListener(e -> assignPartToSelectedOrder());
        completeBtn.addActionListener(e -> completeSelectedOrder());
        diagnosticsBtn.addActionListener(e -> showDiagnosticsForSelectedOrder());

        buttonPanel.add(newOrderBtn);
        buttonPanel.add(addPartBtn);
        buttonPanel.add(completeBtn);
        buttonPanel.add(diagnosticsBtn);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        refreshTable();
    }

    private void refreshTable() {
        orderTableModel.setRowCount(0);
        for (ServiceOrder order : service.getAllOrders()) {
            Appliance app = order.getAppliance();
            String warrantyText = app.isUnderWarranty() ? "Ja" : "Nej";
            String statusText = order.isCompleted() ? "Klar" : "Pågående";

            orderTableModel.addRow(new Object[]{
                    order.getOrderId(),
                    order.getCustomerName(),
                    app.getBrand() + " " + app.getModelName(),
                    warrantyText,
                    statusText,
                    order.getCost()
            });
        }
        revenueLabel.setText("Totala intäkter: " + service.calculateTotalRevenue() + " kr");
    }

    private String getSelectedOrderId() {
        int row = orderTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Markera en arbetsorder i tabellen först!", "Ingen order vald", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return (String) orderTableModel.getValueAt(row, 0);
    }

    private void openNewOrderDialog() {
        JTextField idField = new JTextField("ORD-103");
        JTextField customerField = new JTextField();
        JTextField serialField = new JTextField();
        JTextField brandField = new JTextField();
        JTextField modelField = new JTextField();
        JCheckBox warrantyBox = new JCheckBox("Under garanti");
        JTextField rpmField = new JTextField("1400");

        Object[] fields = {
                "Kundnamn:", customerField,
                "Märke:", brandField,
                "Modell:", modelField,
                "Serienummer:", serialField,
                "Order-ID:", idField,
                warrantyBox,
                "Max centrifugering (rpm):", rpmField
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Skapa ny arbetsorder", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int rpm = Integer.parseInt(rpmField.getText());
                Appliance machine = new WashingMachine(serialField.getText(), brandField.getText(), modelField.getText(), warrantyBox.isSelected(), rpm);
                service.createOrder(idField.getText(), customerField.getText(), machine);
                refreshTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Fel: " + ex.getMessage(), "Kunde inte skapa order", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void assignPartToSelectedOrder() {
        String orderId = getSelectedOrderId();
        if (orderId == null) return;

        java.util.List<SparePart> inventory = service.getInventory();
        String[] options = new String[inventory.size()];
        for (int i = 0; i < inventory.size(); i++) {
            SparePart p = inventory.get(i);
            options[i] = p.getArticleNumber() + " - " + p.getName() + " (" + p.getCost() + " kr, " + p.getStockQuantity() + " i lager)";
        }

        String selected = (String) JOptionPane.showInputDialog(this, "Välj reservdel att montera:", "Montera reservdel", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (selected != null) {
            String articleNumber = selected.split(" - ")[0];
            try {
                service.assignPartToOrder(orderId, articleNumber);
                refreshTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Fel: " + ex.getMessage(), "Kunde inte montera reservdel", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void completeSelectedOrder() {
        String orderId = getSelectedOrderId();
        if (orderId == null) return;

        service.completeOrder(orderId);
        refreshTable();
    }

    private void showDiagnosticsForSelectedOrder() {
        String orderId = getSelectedOrderId();
        if (orderId == null) return;

        for (ServiceOrder order : service.getAllOrders()) {
            if (order.getOrderId().equals(orderId)) {
                JOptionPane.showMessageDialog(this, order.getAppliance().runDiagnostics(), "Diagnostik – " + orderId, JOptionPane.INFORMATION_MESSAGE);
                break;
            }
        }
    }

    public static void launch(DispatchService service) {
        FlatDarkLaf.setup();
        SwingUtilities.invokeLater(() -> {
            ServiceManagerGUI gui = new ServiceManagerGUI(service);
            gui.setVisible(true);
        });
    }
}