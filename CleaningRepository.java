/*******************************************************************
 * Name: Michelle Cruz
 * Date: November 9, 2026
 * Assignment: SDC330 Course Project - Week 3 Class Implementation
 *
 * Provides temporary in-memory storage; JDBC implementation is deferred to Week 4.
 */
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Optional;

public class CleaningRepository {
    // Preserve the planned constructor and CRUD API for the database phase.
    private final String databaseUrl;
    private final LinkedHashMap<Integer, Customer> customers = new LinkedHashMap<>();
    private final LinkedHashMap<Integer, Appointment> appointments = new LinkedHashMap<>();
    private int nextCustomerId = 1;
    private int nextAppointmentId = 1;

    public CleaningRepository(String databaseUrl) { this.databaseUrl = databaseUrl; }
    public void initializeDatabase() {
        // Week 3 deliberately performs no SQL and creates no database file.
    }
    public Customer createCustomer(Customer value) {
        Customer saved = new Customer(nextCustomerId++, value.getName(), value.getPhone(), value.getEmail());
        customers.put(saved.getCustomerId(), saved);
        return saved;
    }
    public Optional<Customer> findCustomer(int id) { return Optional.ofNullable(customers.get(id)); }
    public ArrayList<Customer> listCustomers() { return new ArrayList<>(customers.values()); }
    public boolean updateCustomer(Customer value) {
        Customer stored = customers.get(value.getCustomerId());
        if (stored == null) return false;
        stored.setName(value.getName());
        stored.setPhone(value.getPhone());
        stored.setEmail(value.getEmail());
        return true;
    }
    public boolean deleteCustomer(int id) {
        if (!listAppointmentsByCustomer(id).isEmpty())
            throw new IllegalArgumentException("Remove or reassign this customer's appointments first.");
        return customers.remove(id) != null;
    }
    public Appointment createAppointment(Appointment value) {
        Appointment saved = copyAppointment(nextAppointmentId, value);
        nextAppointmentId++;
        appointments.put(saved.getAppointmentId(), saved);
        return saved;
    }
    public Optional<Appointment> findAppointment(int id) { return Optional.ofNullable(appointments.get(id)); }
    public ArrayList<Appointment> listAppointments() { return new ArrayList<>(appointments.values()); }
    public ArrayList<Appointment> listAppointmentsByCustomer(int id) {
        ArrayList<Appointment> result = new ArrayList<>();
        for (Appointment job : appointments.values()) {
            if (job.getCustomer().getCustomerId() == id) result.add(job);
        }
        return result;
    }
    public boolean updateAppointment(Appointment value) {
        if (!appointments.containsKey(value.getAppointmentId())) return false;
        appointments.put(value.getAppointmentId(), copyAppointment(value.getAppointmentId(), value));
        return true;
    }
    public boolean deleteAppointment(int id) { return appointments.remove(id) != null; }
    private Appointment copyAppointment(int id, Appointment value) {
        Customer owner = findCustomer(value.getCustomer().getCustomerId())
            .orElseThrow(() -> new IllegalArgumentException("Choose an existing customer."));
        CleaningService original = value.getService();
        CleaningService copy;
        if (original instanceof DeepCleaning) {
            copy = new DeepCleaning(original.getBasePriceCents(),
                ((DeepCleaning) original).getAdditionalFeeCents());
        } else {
            copy = new StandardCleaning(original.getBasePriceCents());
        }
        return new Appointment(id, owner, value.getServiceAddress(), value.getScheduledAt(), copy, value.getStatus());
    }
}
