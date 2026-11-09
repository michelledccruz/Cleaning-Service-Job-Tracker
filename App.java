/*******************************************************************
 * Name: Michelle Cruz
 * Date: November 9, 2026
 * Assignment: SDC330 Course Project - Week 3 Class Implementation
 *
 * Provides validated terminal menus for customer and appointment management.
 */
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class App {
    private final Scanner input;
    private final CleaningRepository repository;
    private static final DateTimeFormatter INPUT_DATE = DateTimeFormatter
        .ofPattern("MM/dd/uuuu HH:mm").withResolverStyle(ResolverStyle.STRICT);

    public App(Scanner input, CleaningRepository repository) {
        this.input = input;
        this.repository = repository;
    }
    public static void main(String[] args) {
        try (Scanner input = new Scanner(System.in)) {
            CleaningRepository repository = new CleaningRepository("jdbc:sqlite:cleaning_tracker.db");
            repository.initializeDatabase();
            new App(input, repository).run();
        }
    }
    public void run() {
        System.out.println("Michelle Cruz - Cleaning Service Job Tracker - Week 3");
        System.out.println("Records are kept in memory for this phase and reset when the program closes.");
        try {
            while (true) {
                showMenu();
                int choice = readInt("Choose: ");
                if (choice == 0) break;
                if (choice == 1) manageCustomers();
                else if (choice == 2) manageAppointments();
                else System.out.println("Choose 0, 1, or 2.");
            }
        } catch (NoSuchElementException exception) {
            System.out.println("Input ended.");
        }
        System.out.println("Goodbye.");
    }
    private void showMenu() {
        System.out.println("\n1. Customers\n2. Appointments\n0. Exit");
    }
    private void manageCustomers() {
        while (true) {
            System.out.println("\nCUSTOMERS: 1 Add | 2 List | 3 Find by ID | 4 Update | 5 Delete | 0 Back");
            int choice = readInt("Choose: ");
            if (choice == 0) return;
            try {
                if (choice == 1) {
                    Customer saved = repository.createCustomer(readCustomer());
                    System.out.println("Customer " + saved.getCustomerId() + " created successfully.");
                } else if (choice == 2) {
                    ArrayList<Customer> customers = repository.listCustomers();
                    if (customers.isEmpty()) System.out.println("No customers yet.");
                    for (Customer customer : customers) System.out.println(customer);
                } else if (choice >= 3 && choice <= 5) {
                    int id = readInt("Customer ID: ");
                    Customer current = repository.findCustomer(id).orElse(null);
                    if (current == null) { System.out.println("Customer not found."); continue; }
                    System.out.println(current);
                    if (choice == 4) {
                        System.out.println("Enter replacement contact details.");
                        Customer edited = readCustomer();
                        repository.updateCustomer(new Customer(id, edited.getName(), edited.getPhone(), edited.getEmail()));
                        System.out.println("Customer updated successfully.");
                    } else if (choice == 5 && confirmDelete()) {
                        repository.deleteCustomer(id);
                        System.out.println("Customer deleted successfully.");
                    }
                } else System.out.println("Choose a listed option.");
            } catch (IllegalArgumentException exception) {
                System.out.println("Unable to complete action: " + exception.getMessage());
            }
        }
    }
    private void manageAppointments() {
        while (true) {
            System.out.println("\nAPPOINTMENTS: 1 Add | 2 List | 3 Find by ID | 4 Update | 5 Delete | 6 Find by Customer ID | 0 Back");
            int choice = readInt("Choose: ");
            if (choice == 0) return;
            try {
                if (choice == 1) {
                    if (repository.listCustomers().isEmpty()) {
                        System.out.println("Add a customer first."); continue;
                    }
                    Appointment saved = repository.createAppointment(readAppointment());
                    System.out.println("Appointment " + saved.getAppointmentId() + " created successfully.");
                    printAppointment(saved);
                } else if (choice == 2 || choice == 6) {
                    ArrayList<Appointment> jobs = choice == 2 ? repository.listAppointments()
                        : repository.listAppointmentsByCustomer(readInt("Customer ID: "));
                    if (jobs.isEmpty()) System.out.println("No matching appointments.");
                    for (Appointment job : jobs) printAppointment(job);
                } else if (choice >= 3 && choice <= 5) {
                    int id = readInt("Appointment ID: ");
                    Appointment current = repository.findAppointment(id).orElse(null);
                    if (current == null) { System.out.println("Appointment not found."); continue; }
                    printAppointment(current);
                    if (choice == 4) {
                        System.out.println("Enter replacement appointment details.");
                        Appointment edited = readAppointment();
                        repository.updateAppointment(new Appointment(id, edited.getCustomer(), edited.getServiceAddress(),
                            edited.getScheduledAt(), edited.getService(), edited.getStatus()));
                        System.out.println("Appointment updated successfully.");
                    } else if (choice == 5 && confirmDelete()) {
                        repository.deleteAppointment(id);
                        System.out.println("Appointment deleted successfully.");
                    }
                } else System.out.println("Choose a listed option.");
            } catch (IllegalArgumentException exception) {
                System.out.println("Unable to complete action: " + exception.getMessage());
            }
        }
    }
    private Customer readCustomer() {
        return new Customer(0, readRequired("Name: "), readRequired("Phone: "), readLine("Email (optional): "));
    }
    private Appointment readAppointment() {
        Customer customer;
        while (true) {
            customer = repository.findCustomer(readInt("Customer ID: ")).orElse(null);
            if (customer != null) break;
            System.out.println("Customer not found. Enter an existing customer ID.");
        }
        String address = readRequired("Service address: ");
        LocalDateTime date = readDateTime("Date/time (MM/dd/yyyy HH:mm, 24-hour): ");
        int kind;
        do {
            kind = readInt("Service (1 Standard, 2 Deep): ");
            if (kind != 1 && kind != 2) System.out.println("Choose 1 or 2.");
        } while (kind != 1 && kind != 2);
        CleaningService service;
        while (true) {
            int base = readMoney("Base price in dollars (e.g. 120.00): ");
            int fee = kind == 2 ? readMoney("Deep-cleaning fee in dollars: ") : 0;
            try {
                service = kind == 1 ? new StandardCleaning(base) : new DeepCleaning(base, fee);
                break;
            } catch (IllegalArgumentException exception) {
                System.out.println(exception.getMessage() + " Enter prices again.");
            }
        }
        String status;
        while (true) {
            int choice = readInt("Status (1 Scheduled, 2 Completed, 3 Cancelled): ");
            if (choice >= 1 && choice <= 3) {
                status = choice == 1 ? "Scheduled" : choice == 2 ? "Completed" : "Cancelled";
                break;
            }
            System.out.println("Choose 1, 2, or 3.");
        }
        return new Appointment(0, customer, address, date, service, status);
    }
    private int readInt(String prompt) {
        while (true) {
            try { return Integer.parseInt(readLine(prompt)); }
            catch (NumberFormatException exception) { System.out.println("Enter a whole number."); }
        }
    }
    private LocalDateTime readDateTime(String prompt) {
        while (true) {
            try { return LocalDateTime.parse(readLine(prompt), INPUT_DATE); }
            catch (DateTimeParseException exception) {
                System.out.println("Enter a valid date/time such as 11/15/2026 09:30.");
            }
        }
    }
    private int readMoney(String prompt) {
        while (true) {
            try {
                int cents = new BigDecimal(readLine(prompt)).movePointRight(2).intValueExact();
                if (cents < 0) throw new ArithmeticException();
                return cents;
            } catch (NumberFormatException | ArithmeticException exception) {
                System.out.println("Enter a nonnegative dollar amount with at most two decimal places, within range.");
            }
        }
    }
    private String readRequired(String prompt) {
        while (true) {
            String value = readLine(prompt);
            if (!value.isEmpty()) return value;
            System.out.println("This field is required.");
        }
    }
    private String readLine(String prompt) {
        System.out.print(prompt);
        return input.nextLine().trim();
    }
    private boolean confirmDelete() {
        boolean confirmed = readLine("Delete this record? Type yes to confirm: ").equalsIgnoreCase("yes");
        if (!confirmed) System.out.println("Deletion cancelled.");
        return confirmed;
    }
    private void printAppointment(Appointment appointment) {
        System.out.println("\n" + appointment);
    }
}
