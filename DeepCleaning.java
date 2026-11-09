/*******************************************************************
 * Name: Michelle Cruz
 * Date: November 9, 2026
 * Assignment: SDC330 Course Project - Week 3 Class Implementation
 *
 * Adds a nonnegative fee to the base cleaning price.
 */
public class DeepCleaning extends CleaningService {
    private int additionalFeeCents;
    public DeepCleaning(int basePriceCents, int additionalFeeCents) {
        super(basePriceCents);
        setAdditionalFeeCents(additionalFeeCents);
    }
    public int getAdditionalFeeCents() { return additionalFeeCents; }
    public void setAdditionalFeeCents(int value) {
        checkTotal(getBasePriceCents(), value);
        additionalFeeCents = value;
    }
    @Override
    public void setBasePriceCents(int value) {
        checkTotal(value, additionalFeeCents);
        super.setBasePriceCents(value);
    }
    private static void checkTotal(int base, int fee) {
        if (base < 0 || fee < 0 || (long) base + fee > Integer.MAX_VALUE)
            throw new IllegalArgumentException("Prices must be nonnegative and their total must fit in an int.");
    }
    @Override
    public String getServiceType() { return "Deep"; }
    @Override
    public int calculatePriceCents() { return getBasePriceCents() + additionalFeeCents; }
}
