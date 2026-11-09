# Cleaning Service Job Tracker

**Michelle Cruz**

A Java terminal application for organizing a small cleaning business's customers and appointments. It supports creating, finding, listing, updating, and deleting records, with SQLite storage that keeps saved information available after the application closes.

## Demonstration

A video demonstration is not included. The terminal examples below provide a written walkthrough of the application.

## Features

- Manage customer names, phone numbers, and optional email addresses.
- Schedule appointments linked to existing customers.
- Select Standard or Deep cleaning and calculate a quoted price.
- Track Scheduled, Completed, or Cancelled jobs.
- Find appointments by job ID or customer ID.
- Confirm deletion and prevent deleting customers with linked appointments.
- Validate required fields, dates, menu choices, and prices.
- Save records in `cleaning_tracker.db` using prepared SQL statements.

## Files and Class Responsibilities

The GitHub repository stores the eight Java source files in its root folder.

| File | Responsibility |
| --- | --- |
| `App.java` | Terminal menus, input validation, and readable results and error messages |
| `Customer.java` | Customer identity and contact information |
| `Appointment.java` | Job details, customer and service composition, and formatted summaries |
| `Priceable.java` | Interface declaring `calculatePriceCents()` |
| `CleaningService.java` | Abstract superclass with shared base-price state |
| `StandardCleaning.java` | Standard service pricing |
| `DeepCleaning.java` | Base price plus a deep-cleaning fee |
| `CleaningRepository.java` | SQLite table creation, CRUD queries, and reconstruction of objects |
| `lib/sqlite-jdbc-3.36.0.3.jar` | SQLite JDBC driver |

## Setup and Run

1. Download the repository using **Code > Download ZIP**, extract it, and open the extracted folder containing `App.java` and `lib` in VS Code.
2. Use a Java JDK such as JDK 21 and VS Code's Extension Pack for Java.
3. Expand **Java Projects**, open **Referenced Libraries**, click **+**, and select `lib/sqlite-jdbc-3.36.0.3.jar` if it is not already listed.
4. Open `App.java` and select **Run** above `main`.
5. Add a customer before creating an appointment.

The earlier Week4 project ZIP places the source files inside `src`. When using that ZIP, open the inner project folder containing `src`, `lib`, and `.vscode`, then run `src/App.java`.

The database is created in the working folder. Run the application from the same folder each time to reuse saved records. The application does not insert demonstration records automatically. A native-access warning on newer Java versions does not necessarily prevent execution; the recorded classroom run successfully reached the menu after that warning.

## Example Terminal Input and Output

The following is an illustrative workflow. IDs depend on the records already saved.

```text
1. Customers
2. Appointments
0. Exit
Choose: 1

CUSTOMERS: 1 Add | 2 List | 3 Find by ID | 4 Update | 5 Delete | 0 Back
Choose: 1
Name: Test Customer
Phone: 703-555-0100
Email (optional): test@example.com
Customer 1 created successfully.
```

After returning to the main menu, choose **Appointments > Add** and provide:

| Prompt | Example input |
| --- | --- |
| Customer ID | `1` |
| Service address | `123 Example Street` |
| Date/time | `11/15/2026 09:00` |
| Service | `2` for Deep |
| Base price | `120.00` |
| Deep-cleaning fee | `50.00` |
| Status | `1` for Scheduled |

```text
Job ID: 1
Customer: Test Customer
Phone: 703-555-0100
Email: test@example.com
Service Address: 123 Example Street
Scheduled Date and Time: 11/15/2026 9:00 AM
Cleaning Type: Deep
Quoted Price: $170.00
Status: Scheduled
```

Dates use `MM/dd/yyyy HH:mm` input with a two-digit, 24-hour clock. Prices are entered in dollars and stored as integer cents. Standard cleaning at 12000 cents produces a $120.00 quote; Deep cleaning with a 5000-cent additional fee produces $170.00. These are project examples, not advertised business rates.

## Database Design

| Table | Fields |
| --- | --- |
| `customers` | `customer_id` primary key, `name`, `phone`, `email` |
| `appointments` | `appointment_id` primary key, `customer_id` foreign key, `service_address`, `scheduled_at`, `service_type`, `base_price_cents`, `additional_fee_cents`, `quoted_price_cents`, `status` |

One customer can have multiple appointments. Every appointment references an existing customer. Foreign keys are enabled for each connection, and `ON DELETE RESTRICT` protects linked records. An index supports searches by customer ID. SQLite checks permitted service types, statuses, and consistent nonnegative prices. Java validates date/time values and required text before saving. Appointment queries join both tables and rebuild the appropriate service subclass.

## Development Process and Project Tasks

Development followed the course's staged process:

1. Propose a realistic cleaning appointment tracker.
2. Define classes, responsibilities, and a two-table storage design.
3. Implement domain classes and menus with temporary in-memory storage.
4. Replace temporary storage with SQLite and preserve the domain-class design.
5. Test pricing, validation, CRUD operations, linked records, and persistence.
6. Prepare the final documentation and written terminal examples.

Phase-1 records the initial class implementation. Phase-2 records database implementation. Create Phase-3 after the final documentation has been committed. The final release does not include a video.

## Validation

Verification during development covered customer and appointment CRUD operations, missing IDs, correct Standard and Deep pricing, subclass reconstruction from database records, updates to joined customer information, and blocked deletion of linked customers. A separate-process test closed the application, restarted it in the same working folder, and retrieved the saved appointment. Terminal tests also checked invalid menu input, required fields, date validation, and cancelled deletion. These were development checks; test harness files are not included in this repository.

## Requirement Mapping

| Course requirement | Design choice | Implementation evidence |
| --- | --- | --- |
| Realistic terminal input and output | Customer and appointment menus | `App.run()`, `manageCustomers()`, `manageAppointments()`, and `printAppointment()` |
| Interface | Shared pricing contract | `Priceable.calculatePriceCents()` |
| Abstract class | Shared service state and behavior | Abstract `CleaningService` implements `Priceable` |
| Inheritance | Distinct service types | `StandardCleaning` and `DeepCleaning` extend `CleaningService` |
| Composition | Jobs combine customer and service objects | Private `Customer` and `CleaningService` fields in `Appointment` |
| Polymorphism | Quotes depend on the actual service type | `Appointment.getQuotedPriceCents()` calls `service.calculatePriceCents()` |
| Constructors | Objects start with initialized data | Public domain constructors and protected `CleaningService` constructor |
| Access specifiers | Controlled state and subclass initialization | Private fields, public methods, and protected superclass constructor |
| SQLite create | Save new records | `createCustomer()` and `createAppointment()` |
| SQLite read | Retrieve individual, all, and customer-linked records | `findCustomer()`, `findAppointment()`, list methods, and joined queries |
| SQLite update | Revise existing records | `updateCustomer()` and `updateAppointment()` |
| SQLite delete | Remove records with relationship protection | `deleteCustomer()`, `deleteAppointment()`, and foreign-key constraints |
| Documentation | Explain class responsibilities and setup | Java file headers, method comments, and this README |

## Limitations and Future Improvements

The tracker runs locally in a terminal and has no login system, calendar interface, or multiuser scheduling support. It stores local date/time values without timezone conversion. Future improvements could add appointment reminders, scheduling-conflict checks, backups, and a graphical interface. Use fictional records for public demonstrations; keep databases containing customer information out of GitHub.

## Language and Tools

Java, JDBC, SQLite, Visual Studio Code, and GitHub. The driver is SQLite JDBC 3.36.0.3 from the [Xerial SQLite JDBC project](https://github.com/xerial/sqlite-jdbc); its license information is included in the JAR.

## Project Links

- [Repository](https://github.com/michelledccruz/Cleaning-Service-Job-Tracker)
- [Phase-1: Class Implementation](https://github.com/michelledccruz/Cleaning-Service-Job-Tracker/releases/tag/Phase-1)
- [Phase-2: Database Implementation](https://github.com/michelledccruz/Cleaning-Service-Job-Tracker/releases/tag/Phase-2)

## Project Summary

The Cleaning Service Job Tracker addresses the need to keep customer contact information and cleaning appointments together instead of organizing them through separate notes. The finished application supports customer and appointment management through terminal menus and saves the information in SQLite. The design connects each appointment to a customer and a cleaning-service object, keeping contact information separate from individual jobs.

A successful part of the project was keeping pricing behavior in the service classes. Standard and Deep cleaning share a superclass while calculating their quotes differently. This provides a practical demonstration of inheritance, abstraction, an interface, and polymorphism. Composition connects those objects inside an appointment. Private fields, constructors, and validated setters help keep objects in a valid state.

One challenge encountered during setup was the SQLite driver not being included in the active VS Code project's runtime libraries. The application initially reported that no suitable driver was found. Adding the JAR through Referenced Libraries resolved the connection issue. Date entry also required attention: the input parser expects two-digit hours, so `09:00` is accepted where `9:00` is rejected. Clear prompts and validation messages help users correct these issues.

The project progressed from a proposal and class design to a working class implementation, then to persistent database storage. Development checks verified CRUD operations, both service prices, missing records, input validation, relationships, and records surviving an application restart. Further work could improve the interface and add scheduling features, while the current application demonstrates the required Java object-oriented concepts and SQLite CRUD operations.
