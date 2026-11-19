package se.iths.dario.fieldservicemanager.model;

import java.util.ArrayList;
import java.util.List;

public class ServiceOrder implements Billable {
    private final String orderId;
    private final String customerName;
    private final Appliance appliance;
    private final List<SparePart> usedParts;
    private boolean completed;

    public ServiceOrder(String orderId, String customerName, Appliance appliance) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.appliance = appliance;
        this.usedParts = new ArrayList<>();
        this.completed = false;
    }

    public void addSparePart(SparePart part) {
        part.reduceStock(1);
        usedParts.add(part);
    }

    public void addLoadedSparePart(SparePart part) {
        usedParts.add(part);
    }

    public void completeOrder() {
        this.completed = true;
    }

    @Override
    public double getCost() {
        double total = appliance.calculateBaseServiceFee();
        for (SparePart part : usedParts) {
            total += part.getCost();
        }
        return total;
    }

    @Override
    public String getDescription() {
        String status = completed ? "Klar" : "Pågående";
        return "Order " + orderId + " | Kund: " + customerName + " | Maskin: " + appliance.getBrand() + " " + appliance.getModelName() + " | Status: " + status;
    }

    public String getOrderId() { return orderId; }
    public String getCustomerName() { return customerName; }
    public Appliance getAppliance() { return appliance; }
    public List<SparePart> getUsedParts() { return new ArrayList<>(usedParts); }
    public boolean isCompleted() { return completed; }
}