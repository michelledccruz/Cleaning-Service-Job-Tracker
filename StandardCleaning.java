/*******************************************************************
 * Name: Michelle Cruz
 * Date: November 9, 2026
 * Assignment: SDC330 Course Project - Week 4 Database Implementation
 *
 * Implements standard cleaning pricing through inheritance.
 */
public class StandardCleaning extends CleaningService {
    public StandardCleaning(int basePriceCents) { super(basePriceCents); }
    @Override
    public String getServiceType() { return "Standard"; }
    @Override
    public int calculatePriceCents() { return getBasePriceCents(); }
}
