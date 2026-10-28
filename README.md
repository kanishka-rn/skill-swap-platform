# Skill Swap Platform

A Java 17 desktop application for students to share a skill they can teach,
choose a skill they want to learn, search for teachers, and find reciprocal
skill-exchange partners. The application uses Swing, JDBC, MySQL, and Maven.

## Features

- Register students with a name, unique email, teaching skill, learning skill,
  and optional comma-separated additional skills.
- Choose from Java, Python, C++, HTML, CSS, JavaScript, SQL, Photoshop,
  Web Design, and Data Science, or type a custom skill.
- Search by teaching skill.
- Find reciprocal matches: each student teaches the skill the other wants.
- View all records, refresh the data, and safely delete a selected record.
- Validate required information and email format; show a clear duplicate-email
  message when MySQL rejects an existing email.

## Project structure

```text
skill-swap-platform/
|-- .gitignore
|-- pom.xml
|-- README.md
|-- PROJECT_REPORT.md
|-- database/
|   |-- schema.sql
|   `-- sample_students.sql
`-- src/
    |-- main/java/
    |   |-- DatabaseConnection.java
    |   |-- Main.java
    |   |-- Person.java
    |   |-- Student.java
    |   |-- SkillStudent.java
    |   |-- SkillSwapManager.java
    |   `-- SkillSwapGUI.java
    `-- test/java/
        |-- SkillStudentTest.java
        `-- SkillSwapManagerTest.java
```

The Java files intentionally have no package declaration, so the beginner
project can be compiled directly from the requested `src/main/java` folder.

## Install the required software on Windows

Install these in order. Open a **new** VS Code terminal after changing Windows
environment variables so the terminal sees the new settings.

### 1. Install and verify Java 17

Install a Java 17 JDK (not only a JRE), for example
[Eclipse Temurin 17 for Windows x64](https://adoptium.net/temurin/releases/?version=17&os=windows&arch=x64&package=jdk)
or Microsoft Build of OpenJDK 17. In the installer, select the option to set
`JAVA_HOME` and add Java to `PATH` if offered.

In a new PowerShell terminal, verify both commands:

```powershell
java -version
javac -version
```

Both should report version 17. If either command is not found, configure
`JAVA_HOME` and `Path` as described in the next section before continuing.

### 2. Configure `JAVA_HOME` and `Path`

Find the JDK installation folder. It is the folder containing `bin\java.exe`
and `bin\javac.exe`, for example `C:\Program Files\Eclipse Adoptium\jdk-17...`.
Do not copy the example path literally; use the folder that is installed on
your computer.

1. Open Start and search for **Edit the system environment variables**.
2. Select **Environment Variables**.
3. Under **User variables**, choose **New**. Set the name to `JAVA_HOME` and
   the value to your JDK folder (do not include `\bin`).
4. Select the user `Path`, choose **Edit**, then **New**, and add `%JAVA_HOME%\bin`.
5. Confirm all dialogs, close existing terminals, and open a new PowerShell.
6. Run `echo $env:JAVA_HOME`, `java -version`, and `javac -version`.

If `java` still resolves to an older version, move `%JAVA_HOME%\bin` above the
older Java entry in `Path`.

### 3. Install and verify Maven

Install Apache Maven by following the
[official Maven installation guide](https://maven.apache.org/install.html).
Set `MAVEN_HOME` to the Maven folder if needed and add `%MAVEN_HOME%\bin` to
the user `Path`. Open a new PowerShell terminal and verify:

```powershell
mvn -version
```

The output should show Maven and Java 17.

### 4. Install and start MySQL

Install **MySQL Server 8.x** and MySQL Workbench using MySQL Installer. MySQL
Shell alone is a client tool; it does not prove that the database server is
installed or running. Start the MySQL80 service from Windows Services or
MySQL Installer.

Open `database/schema.sql` in MySQL Workbench, connect to your local server,
and execute the script. It creates `skill_swap_db`, the `students` table,
and three repeat-safe sample records. Alternatively, with MySQL Shell installed,
run this from the project root:

```powershell
mysqlsh --sql root@localhost:3306 --file database/schema.sql
```

Use your own MySQL account in place of `root` if required.

### 5. Set local database credentials

The application reads credentials from environment variables and does not
contain a database password. Set them in Windows **Environment Variables**:

- `SKILLSWAP_DB_USER`: your MySQL username
- `SKILLSWAP_DB_PASSWORD`: that account's password
- `SKILLSWAP_DB_URL` (optional): defaults to
  `jdbc:mysql://localhost:3306/skill_swap_db?serverTimezone=UTC`

Set these as **User variables** on your own computer, not in this project.
Close and reopen VS Code after saving. Do not commit credentials or paste them
into source files. You can confirm the variable names are present in PowerShell
without printing the password:

```powershell
if ($env:SKILLSWAP_DB_USER) { "Database username is set" }
if ($env:SKILLSWAP_DB_PASSWORD) { "Database password is set" }
```

### 6. Compile and run in VS Code

Open the project root folder in VS Code. Install the **Extension Pack for Java**
and **Maven for Java** extensions if they are not already installed. Open a new
terminal at the project root and run:

```powershell
mvn clean compile
mvn test
mvn exec:java
```

The first Maven run downloads the MySQL Connector/J dependency. The GUI opens
with Dashboard, Register Student, Search Skills, Find Matches, and Manage
Students tabs. Database work runs in the background so a slow query does not
block Swing's event-dispatch thread.

The automated unit tests cover student field validation and reciprocal matching
without requiring a live MySQL database. The manual feature checks below still
require a working MySQL connection.

The submission report is [PROJECT_REPORT.md](PROJECT_REPORT.md). No application
screenshots are currently included. After running the GUI with sample data,
capture each tab with **Win+Shift+S** and save genuine screenshots under
`docs/screenshots/`; the report lists suggested filenames.

## Load fictional demonstration profiles

`database/sample_students.sql` contains ten entirely fictional college
profiles with reserved `example.com` email addresses. Their skills are arranged
to demonstrate five reciprocal exchanges: Java/Python, Web Development/UI/UX
Design, SQL/Excel, HTML/CSS, and Data Science/Machine Learning. Other skills,
including C++, Photoshop, and Digital Marketing, appear as additional skills.

The script uses `INSERT IGNORE` inside a transaction. It never updates or
deletes existing rows; if one of its email addresses is already present, that
profile is skipped. It reports the number inserted and the number skipped in
this run, then displays the seeded rows and reciprocal pairs. A separate result set lists
any pre-existing sample email whose stored profile differs from the intended
fictional profile, so conflicts are visible without overwriting data. An empty
discrepancy result means the existing sample profiles match the seed. Running
the script again inserts zero additional rows when all ten addresses are
already present.

**MySQL Workbench:** open your existing local MySQL connection, choose
**File > Open SQL Script**, select `database/sample_students.sql`, then click
the lightning-bolt **Execute** button. Confirm the result tab reports the
insert count and displays the ten sample rows.

**PowerShell with MySQL Shell:** from the project root, substitute your local
MySQL username for `YOUR_MYSQL_USER`. MySQL Shell prompts for the password;
do not place the password in the command. If the command is on `PATH`:

```powershell
mysqlsh --sql -u YOUR_MYSQL_USER -p -h localhost -P 3306 --database=skill_swap_db --file database/sample_students.sql
```

For the default Windows installation shown in this workspace, run:

```powershell
& 'C:\Program Files\MySQL\MySQL Shell 8.0\bin\mysqlsh.exe' --sql -u YOUR_MYSQL_USER -p -h localhost -P 3306 --database=skill_swap_db --file database/sample_students.sql
```

Replace `YOUR_MYSQL_USER` with your username. The password prompt is interactive;
never add the password to the command.

In the application, select **Find Matches** and click **Find compatible
exchanges**. It should include the five pairs above, provided both profiles in
each pair were inserted and no conflicting existing records use those sample
emails. The database query at the end of the script independently lists
reciprocal pairs among the demonstration profiles only.

## Testing the application manually

1. On **Register Student**, register two students with different email
   addresses: one teaches Java and wants SQL; the other teaches SQL and wants
   Java. Click **Register student** after entering each record.
2. On **Search Skills**, type `Java` and click **Search**. The first student
   should appear.
3. On **Find Matches**, click **Find compatible exchanges**. The two students
   should appear as a reciprocal pair.
4. On **Manage Students**, select a record and choose **Delete selected
   student**. Confirm the prompt and check that the record is removed.
5. Try registering the same email again to see the duplicate-email warning.

## Common errors

| Message or symptom | What to check |
|---|---|
| `java` or `javac` is not recognized | Install a JDK 17, set `JAVA_HOME`, add `%JAVA_HOME%\bin` to `Path`, and open a new terminal. |
| `mvn` is not recognized | Install Maven, add its `bin` folder to `Path`, and open a new terminal. |
| `Access denied for user` | Verify `SKILLSWAP_DB_USER` and `SKILLSWAP_DB_PASSWORD` and that the account can connect to MySQL. |
| `Communications link failure` | Start MySQL Server and check the host, port, and optional `SKILLSWAP_DB_URL`. |
| `Unknown database 'skill_swap_db'` | Execute `database/schema.sql` in MySQL first. |
| Duplicate email warning | Email addresses must be unique; register with a different address or delete the old record. |
| MySQL driver not found | Run Maven from the project root with internet access so it can download dependencies. |

## OOP concepts for a viva

- **Abstraction:** `Person` is abstract. It keeps shared identity details and
  requires subclasses to implement `getDisplayInfo()`.
- **Inheritance:** `Student extends Person`, and `SkillStudent extends Student`.
  The specialized class reuses the common name and email fields.
- **Encapsulation:** fields are private; callers use getters and validated
  setters instead of changing skill values directly.
- **Polymorphism:** `SkillStudent` overrides `getDisplayInfo()`. A call to that
  method on a `SkillStudent` object uses its skill-aware implementation.
- **Collections:** `SkillSwapManager` uses `List<SkillStudent>` and
  `List<SkillMatch>` to hold query results and calculate possible exchanges.
- **Exception handling:** validation raises `IllegalArgumentException`, JDBC
  errors are reported as `SQLException`, and duplicate email error code 1062
  receives a specific message in the GUI.
- **JDBC and prepared statements:** the manager uses `PreparedStatement` for
  insert, search, and delete queries, and closes database resources with
  try-with-resources.

## Viva questions to practice

1. Why is `Person` abstract, and what common fields does it contain?
2. How does `SkillStudent` demonstrate inheritance and method overriding?
3. Why are fields private, and how do getters and setters support encapsulation?
4. How does the matching feature decide whether two students can exchange skills?
5. Why use a `PreparedStatement` instead of joining user input into SQL text?
6. What does the unique email constraint do, and how does the program respond
   when it is violated?
7. Why are JDBC connections, statements, and result sets closed with
   try-with-resources?
