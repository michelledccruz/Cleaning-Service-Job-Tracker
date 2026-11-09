/*******************************************************************
 * Name: Michelle Cruz
 * Date: November 9, 2026
 * Assignment: SDC330 Course Project - Week 4 Database Implementation
 *
 * Combines customer and service objects into a scheduled cleaning job.
 */
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Appointment {
    private final int appointmentId;
    private Customer customer;
    private String serviceAddress;
    private LocalDateTime scheduledAt;
    private CleaningService service;
    private String status;

    public Appointment(int appointmentId, Customer customer, String serviceAddress,
                       LocalDateTime scheduledAt, CleaningService service, String status) {
        if (appointmentId < 0) throw new IllegalArgumentException("ID cannot be negative.");
        this.appointmentId = appointmentId;
        setCustomer(customer);
        setServiceAddress(serviceAddress);
        setScheduledAt(scheduledAt);
        setService(service);
        setStatus(status);
    }
    public int getAppointmentId() { return appointmentId; }
    public Customer getCustomer() { return customer; }
    public String getServiceAddress() { return serviceAddress; }
    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public CleaningService getService() { return service; }
    public String getStatus() { return status; }
    public void setCustomer(Customer value) {
        if (value == null) throw new IllegalArgumentException("Customer is required.");
        customer = value;
    }
    public void setServiceAddress(String value) {
        if (value == null || value.trim().isEmpty())
            throw new IllegalArgumentException("Service address is required.");
        serviceAddress = value.trim();
    }
    public void setScheduledAt(LocalDateTime value) {
        if (value == null) throw new IllegalArgumentException("Date is required.");
        scheduledAt = value;
    }
    public void setService(CleaningService value) {
        if (value == null) throw new IllegalArgumentException("Service is required.");
        service = value;
    }
    public void setStatus(String value) {
        if (!"Scheduled".equals(value) && !"Completed".equals(value) && !"Cancelled".equals(value))
            throw new IllegalArgumentException("Status must be Scheduled, Completed, or Cancelled.");
        status = value;
    }
    public int getQuotedPriceCents() { return service.calculatePriceCents(); }
    @Override
    public String toString() {
        return "Job ID: " + appointmentId + "\nCustomer: " + customer.getName()
            + "\nPhone: " + customer.getPhone() + "\nEmail: " + customer.getEmail()
            + "\nService Address: " + serviceAddress + "\nScheduled Date and Time: "
            + scheduledAt.format(DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a", Locale.US))
            + "\nCleaning Type: " + service.getServiceType()
            + String.format(Locale.US, "\nQuoted Price: $%.2f", getQuotedPriceCents() / 100.0)
            + "\nStatus: " + status;
    }
}
