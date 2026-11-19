package se.iths.dario.fieldservicemanager.model;

public class WashingMachine extends Appliance {
    private final int maxSpinSpeedRpm;

    public WashingMachine(String serialNumber, String brand, String modelName, boolean underWarranty, int maxSpinSpeedRpm) {
        super(serialNumber, brand, modelName, underWarranty);
        this.maxSpinSpeedRpm = maxSpinSpeedRpm;
    }

    @Override
    public double calculateBaseServiceFee() {
        return isUnderWarranty() ? 0.0 : 850.0;
    }

    @Override
    public String runDiagnostics() {
        return "Tvättmaskin (" + maxSpinSpeedRpm + " rpm): Kontrollerar tömningspump och stötdämpare.";
    }

    public int getMaxSpinSpeedRpm() {
        return maxSpinSpeedRpm;
    }
}