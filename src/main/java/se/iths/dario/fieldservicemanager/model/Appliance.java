package se.iths.dario.fieldservicemanager.model;

public abstract class Appliance {
    private final String brand;
    private final String modelName;
    private final String serialNumber;
    private final boolean underWarranty;

    public Appliance(String serialNumber, String brand, String modelName, boolean underWarranty) {
        if (serialNumber == null || serialNumber.isBlank()) {
            throw new IllegalArgumentException("Serienummer får inte vara tomt.");
        }
        this.serialNumber = serialNumber;
        this.brand = brand;
        this.modelName = modelName;
        this.underWarranty = underWarranty;
    }

    public abstract double calculateBaseServiceFee();
    public abstract String runDiagnostics();

    public String getSerialNumber() { return serialNumber; }
    public String getBrand() { return brand; }
    public String getModelName() { return modelName; }
    public boolean isUnderWarranty() { return underWarranty; }

    @Override
    public String toString() {
        return brand + " " + modelName + " (S/N: " + serialNumber + ") | Garanti: " + (underWarranty ? "Ja" : "Nej");
    }
}