package se.iths.dario.fieldservicemanager.ui;

import se.iths.dario.fieldservicemanager.model.Appliance;
import se.iths.dario.fieldservicemanager.model.ServiceOrder;
import se.iths.dario.fieldservicemanager.model.SparePart;
import se.iths.dario.fieldservicemanager.model.WashingMachine;
import se.iths.dario.fieldservicemanager.service.DispatchService;

import java.util.Scanner;

public class ConsoleMenu {
    private final DispatchService service;
    private final Scanner scanner;

    public ConsoleMenu(DispatchService service) {
        this.service = service;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1" -> createNewOrder();
                    case "2" -> assignSparePart();
                    case "3" -> completeOrder();
                    case "4" -> showAllOrders();
                    case "5" -> showInventoryAndRevenue();
                    case "0" -> {
                        running = false;
                        System.out.println("Avslutar Field Service Manager...");
                    }
                    default -> System.out.println("Ogiltigt val, försök igen.");
                }
            } catch (Exception e) {
                System.out.println("Fel: " + e.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.println("\n=== FIELD SERVICE MANAGER ===");
        System.out.println("1. Skapa ny arbetsorder (Tvättmaskin)");
        System.out.println("2. Montera reservdel på order");
        System.out.println("3. Klarmarkera arbetsorder");
        System.out.println("4. Visa alla arbetsordrar & diagnostik");
        System.out.println("5. Visa reservdelslager & totala intäkter");
        System.out.println("0. Avsluta");
        System.out.print("Välj ett alternativ: ");
    }

    private void createNewOrder() {
        System.out.print("Order-ID (t.ex. ORD-103): ");
        String orderId = scanner.nextLine();

        System.out.print("Kundnamn: ");
        String customerName = scanner.nextLine();

        System.out.print("Serienummer: ");
        String serialNumber = scanner.nextLine();

        System.out.print("Märke (t.ex. Miele): ");
        String brand = scanner.nextLine();

        System.out.print("Modell (t.ex. W1): ");
        String modelName = scanner.nextLine();

        System.out.print("Har maskinen garanti? (ja/nej): ");
        boolean underWarranty = scanner.nextLine().equalsIgnoreCase("ja");

        System.out.print("Max centrifugering i rpm (t.ex. 1600): ");
        int rpm = Integer.parseInt(scanner.nextLine());

        Appliance machine = new WashingMachine(serialNumber, brand, modelName, underWarranty, rpm);
        service.createOrder(orderId, customerName, machine);
        System.out.println("Arbetsorder " + orderId + " skapad!");
    }

    private void assignSparePart() {
        System.out.print("Ange Order-ID: ");
        String orderId = scanner.nextLine();

        System.out.print("Ange artikelnummer på reservdel (t.ex. MIE-4092): ");
        String articleNumber = scanner.nextLine();

        service.assignPartToOrder(orderId, articleNumber);
        System.out.println("Reservdel monterad på order " + orderId + "!");
    }

    private void completeOrder() {
        System.out.print("Ange Order-ID att klarmarkera: ");
        String orderId = scanner.nextLine();

        service.completeOrder(orderId);
        System.out.println("Order " + orderId + " är nu klarmarkerad!");
    }

    private void showAllOrders() {
        System.out.println("\n--- AKTIVA & KLARA ORDRAR ---");
        if (service.getAllOrders().isEmpty()) {
            System.out.println("Inga ordrar registrerade ännu.");
            return;
        }
        for (ServiceOrder order : service.getAllOrders()) {
            System.out.println(order.getDescription() + " | Kostnad: " + order.getCost() + " kr");
            System.out.println("   -> Diagnostik: " + order.getAppliance().runDiagnostics());
        }
    }

    private void showInventoryAndRevenue() {
        System.out.println("\n--- RESERVDELSLAGER ---");
        for (SparePart part : service.getInventory()) {
            System.out.println(part.getDescription() + " | Pris: " + part.getCost() + " kr | I lager: " + part.getStockQuantity() + " st");
        }
        System.out.println("\nTotala intäkter: " + service.calculateTotalRevenue() + " kr");
    }
}