/*******************************************************************
 * Name: Michelle Cruz
 * Date: November 9, 2026
 * Assignment: SDC330 Course Project - Week 3 Class Implementation
 *
 * Provides shared service state and polymorphic pricing behavior.
 */
import java.util.Locale;

public abstract class CleaningService implements Priceable {
    private int basePriceCents;
    protected CleaningService(int basePriceCents) { setBasePriceCents(basePriceCents); }
    public int getBasePriceCents() { return basePriceCents; }
    public void setBasePriceCents(int value) {
        if (value < 0) throw new IllegalArgumentException("Base price cannot be negative.");
        basePriceCents = value;
    }
    public abstract String getServiceType();
    public abstract int calculatePriceCents();
    @Override
    public String toString() {
        return String.format(Locale.US, "%s cleaning - $%.2f",
            getServiceType(), calculatePriceCents() / 100.0);
    }
}
