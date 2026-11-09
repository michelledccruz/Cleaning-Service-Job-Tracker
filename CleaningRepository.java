/*******************************************************************
 * Name: Michelle Cruz
 * Date: November 9, 2026
 * Assignment: SDC330 Course Project - Week 4 Database Implementation
 *
 * Stores customers and appointments in SQLite using JDBC.
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

public class CleaningRepository {
    private final String databaseUrl;
    private static final String JOB_QUERY = "SELECT a.*, c.name, c.phone, c.email "
        + "FROM appointments a JOIN customers c ON a.customer_id=c.customer_id";

    public CleaningRepository(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }

    // SQLite foreign-key enforcement must be enabled for every connection.
    private Connection openConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(databaseUrl);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys=ON");
        } catch (SQLException exception) {
            conn.close();
            throw exception;
        }
        return conn;
    }

    public void initializeDatabase() throws SQLException {
        try (Connection conn = openConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS customers ("
                + "customer_id INTEGER PRIMARY KEY, "
                + "name TEXT NOT NULL CHECK(length(trim(name))>0), "
                + "phone TEXT NOT NULL CHECK(length(trim(phone))>0), "
                + "email TEXT NOT NULL DEFAULT '')");
            stmt.execute("CREATE TABLE IF NOT EXISTS appointments ("
                + "appointment_id INTEGER PRIMARY KEY, "
                + "customer_id INTEGER NOT NULL REFERENCES customers(customer_id) ON DELETE RESTRICT, "
                + "service_address TEXT NOT NULL CHECK(length(trim(service_address))>0), "
                + "scheduled_at TEXT NOT NULL, "
                + "service_type TEXT NOT NULL CHECK(service_type IN ('Standard','Deep')), "
                + "base_price_cents INTEGER NOT NULL CHECK(base_price_cents>=0), "
                + "additional_fee_cents INTEGER NOT NULL DEFAULT 0 CHECK(additional_fee_cents>=0), "
                + "quoted_price_cents INTEGER NOT NULL CHECK(quoted_price_cents BETWEEN 0 AND 2147483647), "
                + "status TEXT NOT NULL CHECK(status IN ('Scheduled','Completed','Cancelled')), "
                + "CHECK(service_type<>'Standard' OR additional_fee_cents=0), "
                + "CHECK(quoted_price_cents=base_price_cents+additional_fee_cents))");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_appointments_customer ON appointments(customer_id)");
        }
    }

    public Customer createCustomer(Customer value) throws SQLException {
        String sql = "INSERT INTO customers(name,phone,email) VALUES(?,?,?)";
        try (Connection conn = openConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, value.getName());
            pst.setString(2, value.getPhone());
            pst.setString(3, value.getEmail());
            pst.executeUpdate();
            return new Customer(insertedId(conn), value.getName(), value.getPhone(), value.getEmail());
        }
    }

    public Optional<Customer> findCustomer(int id) throws SQLException {
        try (Connection conn = openConnection();
             PreparedStatement pst = conn.prepareStatement("SELECT * FROM customers WHERE customer_id=?")) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next() ? Optional.of(mapCustomer(rs)) : Optional.empty();
            }
        }
    }

    public ArrayList<Customer> listCustomers() throws SQLException {
        ArrayList<Customer> result = new ArrayList<>();
        try (Connection conn = openConnection(); Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM customers ORDER BY customer_id")) {
            while (rs.next()) result.add(mapCustomer(rs));
        }
        return result;
    }

    public boolean updateCustomer(Customer value) throws SQLException {
        String sql = "UPDATE customers SET name=?,phone=?,email=? WHERE customer_id=?";
        try (Connection conn = openConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, value.getName());
            pst.setString(2, value.getPhone());
            pst.setString(3, value.getEmail());
            pst.setInt(4, value.getCustomerId());
            return pst.executeUpdate() == 1;
        }
    }

    public boolean deleteCustomer(int id) throws SQLException {
        try (Connection conn = openConnection();
             PreparedStatement pst = conn.prepareStatement("DELETE FROM customers WHERE customer_id=?")) {
            pst.setInt(1, id);
            try {
                return pst.executeUpdate() == 1;
            } catch (SQLException exception) {
                if (exception.getMessage() != null && exception.getMessage().contains("FOREIGN KEY")) {
                    throw new SQLException("Remove or reassign this customer's appointments first.", exception);
                }
                throw exception;
            }
        }
    }

    public Appointment createAppointment(Appointment value) throws SQLException {
        String sql = "INSERT INTO appointments(customer_id,service_address,scheduled_at,service_type,"
            + "base_price_cents,additional_fee_cents,quoted_price_cents,status) VALUES(?,?,?,?,?,?,?,?)";
        try (Connection conn = openConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {
            setAppointmentFields(pst, value);
            pst.executeUpdate();
            return new Appointment(insertedId(conn), value.getCustomer(), value.getServiceAddress(),
                value.getScheduledAt(), value.getService(), value.getStatus());
        }
    }

    public Optional<Appointment> findAppointment(int id) throws SQLException {
        try (Connection conn = openConnection();
             PreparedStatement pst = conn.prepareStatement(JOB_QUERY + " WHERE a.appointment_id=?")) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next() ? Optional.of(mapAppointment(rs)) : Optional.empty();
            }
        }
    }

    public ArrayList<Appointment> listAppointments() throws SQLException {
        ArrayList<Appointment> result = new ArrayList<>();
        try (Connection conn = openConnection(); Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(JOB_QUERY + " ORDER BY a.appointment_id")) {
            while (rs.next()) result.add(mapAppointment(rs));
        }
        return result;
    }

    public ArrayList<Appointment> listAppointmentsByCustomer(int id) throws SQLException {
        ArrayList<Appointment> result = new ArrayList<>();
        try (Connection conn = openConnection();
             PreparedStatement pst = conn.prepareStatement(JOB_QUERY + " WHERE a.customer_id=? ORDER BY a.appointment_id")) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) result.add(mapAppointment(rs));
            }
        }
        return result;
    }

    public boolean updateAppointment(Appointment value) throws SQLException {
        String sql = "UPDATE appointments SET customer_id=?,service_address=?,scheduled_at=?,service_type=?,"
            + "base_price_cents=?,additional_fee_cents=?,quoted_price_cents=?,status=? WHERE appointment_id=?";
        try (Connection conn = openConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {
            setAppointmentFields(pst, value);
            pst.setInt(9, value.getAppointmentId());
            return pst.executeUpdate() == 1;
        }
    }

    public boolean deleteAppointment(int id) throws SQLException {
        try (Connection conn = openConnection();
             PreparedStatement pst = conn.prepareStatement("DELETE FROM appointments WHERE appointment_id=?")) {
            pst.setInt(1, id);
            return pst.executeUpdate() == 1;
        }
    }

    private int insertedId(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
            if (!rs.next()) throw new SQLException("Could not retrieve the saved record ID.");
            return rs.getInt(1);
        }
    }

    private void setAppointmentFields(PreparedStatement pst, Appointment value) throws SQLException {
        CleaningService service = value.getService();
        int fee = service instanceof DeepCleaning ? ((DeepCleaning) service).getAdditionalFeeCents() : 0;
        pst.setInt(1, value.getCustomer().getCustomerId());
        pst.setString(2, value.getServiceAddress());
        pst.setString(3, value.getScheduledAt().toString());
        pst.setString(4, service.getServiceType());
        pst.setInt(5, service.getBasePriceCents());
        pst.setInt(6, fee);
        pst.setInt(7, value.getQuotedPriceCents());
        pst.setString(8, value.getStatus());
    }

    private Customer mapCustomer(ResultSet rs) throws SQLException {
        return new Customer(rs.getInt("customer_id"), rs.getString("name"),
            rs.getString("phone"), rs.getString("email"));
    }

    private Appointment mapAppointment(ResultSet rs) throws SQLException {
        try {
            String type = rs.getString("service_type");
            CleaningService service;
            if ("Deep".equals(type)) {
                service = new DeepCleaning(rs.getInt("base_price_cents"), rs.getInt("additional_fee_cents"));
            } else if ("Standard".equals(type)) {
                service = new StandardCleaning(rs.getInt("base_price_cents"));
            } else throw new IllegalArgumentException("Unknown service type.");
            return new Appointment(rs.getInt("appointment_id"), mapCustomer(rs),
                rs.getString("service_address"), LocalDateTime.parse(rs.getString("scheduled_at")),
                service, rs.getString("status"));
        } catch (RuntimeException exception) {
            throw new SQLException("Stored appointment data is invalid: " + exception.getMessage(), exception);
        }
    }
}
