/*******************************************************************
 * Name: Michelle Cruz
 * Date: November 9, 2026
 * Assignment: SDC330 Course Project - Week 4 Database Implementation
 *
 * Stores customer contact information with controlled access.
 */
public class Customer {
    private final int customerId;
    private String name;
    private String phone;
    private String email;

    public Customer(int customerId, String name, String phone, String email) {
        if (customerId < 0) throw new IllegalArgumentException("ID cannot be negative.");
        this.customerId = customerId;
        setName(name);
        setPhone(phone);
        setEmail(email);
    }
    public int getCustomerId() { return customerId; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public void setName(String value) { name = required(value, "Name"); }
    public void setPhone(String value) { phone = required(value, "Phone"); }
    public void setEmail(String value) { email = value == null ? "" : value.trim(); }
    private static String required(String value, String label) {
        if (value == null || value.trim().isEmpty())
            throw new IllegalArgumentException(label + " is required.");
        return value.trim();
    }
    @Override
    public String toString() {
        return "Customer ID: " + customerId + " | " + name + " | Phone: " + phone
            + " | Email: " + (email.isEmpty() ? "Not provided" : email);
    }
}
