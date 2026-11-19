package se.iths.dario.fieldservicemanager;

import se.iths.dario.fieldservicemanager.model.SparePart;
import se.iths.dario.fieldservicemanager.model.WashingMachine;
import se.iths.dario.fieldservicemanager.service.DispatchService;
import se.iths.dario.fieldservicemanager.ui.ServiceManagerGUI;

public class Main {
    public static void main(String[] args) {
        DispatchService service = new DispatchService();

        if (service.getInventory().isEmpty() && service.getAllOrders().isEmpty()) {
            service.addSparePartToInventory(new SparePart("MIE-4092", "Tömningspump", 650.0, 4));
            service.addSparePartToInventory(new SparePart("BOS-1102", "Värmeelement", 920.0, 2));
            service.addSparePartToInventory(new SparePart("ELE-3050", "Drivrem", 340.0, 5));

            service.createOrder("ORD-101", "Anna Andersson",
                    new WashingMachine("MIE-9921", "Miele", "W1 TwinDos", false, 1600));
            service.createOrder("ORD-102", "Erik Svensson",
                    new WashingMachine("BOS-5540", "Bosch", "Serie 6", true, 1400));
        }

        ServiceManagerGUI.launch(service);
    }
}