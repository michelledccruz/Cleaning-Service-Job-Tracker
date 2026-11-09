# Cleaning Service Job Tracker - Week 4
Michelle Cruz - SDC330 Course Project Database Implementation

## Run in VS Code
Extract All, then open the inner Michelle_Cruz_Project_Week4 folder in VS Code.
Open src/App.java and click Run. The lib folder includes sqlite-jdbc-3.36.0.3.jar,
the same driver used in the course guided practice. No separate download is needed.
If required, add that JAR through Java Projects > Referenced Libraries > +.

## Use
Add customers, then create appointments with an existing customer ID.
Customer and appointment menus support Add, List, Find by ID, Update, and Delete.
Appointments also support Find by Customer ID. Deletion asks for confirmation.
Dates: MM/dd/yyyy HH:mm, using two-digit hours and a 24-hour clock.
Prices: dollars with at most two decimal places; stored as whole cents.

Records are saved in cleaning_tracker.db in the working folder. Reopen the SAME
project folder to reuse it. There is no sample data inserted automatically.
Do not upload personal customer data or the database file to a public repository.

## Data design
Customers: customer_id, name, phone, email.
Appointments: appointment_id, customer_id, service_address, scheduled_at,
service_type, base_price_cents, additional_fee_cents, quoted_price_cents, status.
Foreign keys are enabled on every connection. Customer deletion is restricted
while jobs reference that customer. Queries bind values with prepared statements.
Service subclasses are reconstructed from stored service types and pricing fields.

## OOP mapping
Priceable interface; CleaningService abstract class; StandardCleaning and
DeepCleaning inheritance and polymorphism; Appointment composition; private fields,
validated public methods, constructors, and formatted terminal output.

## GitHub phase update
Update the eight Java files in the existing repository and add the lib folder.
Include README.md and .gitignore. If Java files are at the repository root,
keep that existing layout; the runtime classpath must include lib/*.jar.
Commit the Week 4 database implementation, then create tag Phase-2 on the new
commit and release title Phase #2 - Database Implementation.
Leave Phase-1 pointing to the earlier class implementation.

The course overview requests the Java ZIP and final proposal/design Word document.
The Week 4 page also requests the Phase-2 URL in the Website URL field.

## Third-party dependency
SQLite JDBC 3.36.0.3 by the Xerial project: https://github.com/xerial/sqlite-jdbc
Upstream license information is distributed inside the JAR.
