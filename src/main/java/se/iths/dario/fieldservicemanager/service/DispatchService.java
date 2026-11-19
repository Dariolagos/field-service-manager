package se.iths.dario.fieldservicemanager.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;
import se.iths.dario.fieldservicemanager.model.Appliance;
import se.iths.dario.fieldservicemanager.model.Billable;
import se.iths.dario.fieldservicemanager.model.ServiceOrder;
import se.iths.dario.fieldservicemanager.model.SparePart;
import se.iths.dario.fieldservicemanager.model.WashingMachine;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DispatchService {
    private final List<ServiceOrder> orders;
    private final List<SparePart> inventory;
    private final Path ordersFile;
    private final Path inventoryFile;
    private final Gson gson;

    public DispatchService() {
        this(Path.of("orders.json"), Path.of("inventory.json"));
    }

    public DispatchService(Path ordersFile, Path inventoryFile) {
        this.orders = new ArrayList<>();
        this.inventory = new ArrayList<>();
        this.ordersFile = ordersFile;
        this.inventoryFile = inventoryFile;

        // Lär Gson att tolka abstrakta Appliance som WashingMachine vid inläsning
        JsonDeserializer<Appliance> applianceDeserializer = (json, typeOfT, context) ->
                context.deserialize(json, WashingMachine.class);

        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Appliance.class, applianceDeserializer)
                .create();

        loadData();
    }

    public void addSparePartToInventory(SparePart part) {
        inventory.add(part);
        saveData();
    }

    public ServiceOrder createOrder(String orderId, String customerName, Appliance appliance) {
        ServiceOrder newOrder = new ServiceOrder(orderId, customerName, appliance);
        orders.add(newOrder);
        saveData();
        return newOrder;
    }

    public void assignPartToOrder(String orderId, String articleNumber) {
        ServiceOrder order = findOrderById(orderId);
        SparePart part = findPartByArticleNumber(articleNumber);
        order.addSparePart(part);
        saveData();
    }

    public void completeOrder(String orderId) {
        ServiceOrder order = findOrderById(orderId);
        order.completeOrder();
        saveData();
    }

    public double calculateTotalRevenue() {
        double sum = 0.0;
        for (Billable item : orders) {
            sum += item.getCost();
        }
        return sum;
    }

    public List<ServiceOrder> getAllOrders() {
        return new ArrayList<>(orders);
    }

    public List<SparePart> getInventory() {
        return new ArrayList<>(inventory);
    }

    private void saveData() {
        try {
            String inventoryJson = gson.toJson(inventory);
            Files.writeString(inventoryFile, inventoryJson);

            String ordersJson = gson.toJson(orders);
            Files.writeString(ordersFile, ordersJson);
        } catch (IOException e) {
            System.out.println("Kunde inte spara JSON-data: " + e.getMessage());
        }
    }

    private void loadData() {
        try {
            if (Files.exists(inventoryFile)) {
                String json = Files.readString(inventoryFile);
                Type listType = new TypeToken<ArrayList<SparePart>>() {}.getType();
                List<SparePart> loadedInventory = gson.fromJson(json, listType);
                if (loadedInventory != null) {
                    inventory.addAll(loadedInventory);
                }
            }

            if (Files.exists(ordersFile)) {
                String json = Files.readString(ordersFile);
                Type listType = new TypeToken<ArrayList<ServiceOrder>>() {}.getType();
                List<ServiceOrder> loadedOrders = gson.fromJson(json, listType);
                if (loadedOrders != null) {
                    orders.addAll(loadedOrders);
                }
            }
        } catch (IOException e) {
            System.out.println("Kunde inte läsa sparad JSON-data: " + e.getMessage());
        }
    }

    private ServiceOrder findOrderById(String orderId) {
        for (ServiceOrder order : orders) {
            if (order.getOrderId().equalsIgnoreCase(orderId)) {
                return order;
            }
        }
        throw new IllegalArgumentException("Hittade ingen order med ID: " + orderId);
    }

    private SparePart findPartByArticleNumber(String articleNumber) {
        for (SparePart part : inventory) {
            if (part.getArticleNumber().equalsIgnoreCase(articleNumber)) {
                return part;
            }
        }
        throw new IllegalArgumentException("Hittade ingen reservdel med artikelnummer: " + articleNumber);
    }
}