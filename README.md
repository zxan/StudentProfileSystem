# Student Profile and Skill Development Management System - Q1 (JDBC)

Plain JDBC + MySQL, no frameworks.

## Run
1. Start MySQL in XAMPP (port 3306, user root, blank password).
2. Add `mysql-connector-j` to the build path (Eclipse: Build Path > Add External JARs).
3. Run `com.kiet.studentjdbc.MainApp`. The database `student_jdbc_db` and its tables are created automatically.

## Files (src/com/kiet/studentjdbc)
- `Student.java` - POJO with validation and business methods
- `StudentAnalytics.java` - merit list, same-skills grouping
- `DBConnection.java` - connection setup
- `StudentDAO.java` - PreparedStatement CRUD, search, transactions
- `MainApp.java` - demo of all 8 rules
