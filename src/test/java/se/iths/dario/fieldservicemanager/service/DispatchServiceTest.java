package se.iths.dario.fieldservicemanager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.iths.dario.fieldservicemanager.model.Appliance;
import se.iths.dario.fieldservicemanager.model.ServiceOrder;
import se.iths.dario.fieldservicemanager.model.SparePart;
import se.iths.dario.fieldservicemanager.model.WashingMachine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class DispatchServiceTest {
    private DispatchService service;
    private SparePart pump;

    @BeforeEach
    void setUp() throws IOException {
        Path testOrders = Path.of("target/test-orders.json");
        Path testInventory = Path.of("target/test-inventory.json");
        Files.deleteIfExists(testOrders);
        Files.deleteIfExists(testInventory);

        service = new DispatchService(testOrders, testInventory);
        pump = new SparePart("MIE-4092", "Tömningspump", 650.0, 3);
        service.addSparePartToInventory(pump);
    }

    @Test
    void shouldCalculateTotalCostWithBaseFeeAndSparePart() {
        Appliance machine = new WashingMachine("MIE-9921", "Miele", "W1", false, 1600);
        ServiceOrder order = service.createOrder("ORD-101", "Anna Andersson", machine);

        service.assignPartToOrder("ORD-101", "MIE-4092");

        assertEquals(1500.0, order.getCost());
        assertEquals(2, pump.getStockQuantity());
    }

    @Test
    void shouldWaiveBaseFeeWhenApplianceIsUnderWarranty() {
        Appliance machine = new WashingMachine("BOS-5540", "Bosch", "Serie 6", true, 1400);
        ServiceOrder order = service.createOrder("ORD-102", "Erik Svensson", machine);

        service.assignPartToOrder("ORD-102", "MIE-4092");

        assertEquals(650.0, order.getCost());
    }

    @Test
    void shouldThrowExceptionWhenOrderIdDoesNotExist() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.completeOrder("FINNS-INTE");
        });
    }
}