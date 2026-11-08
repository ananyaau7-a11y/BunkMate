# BunkMate – Smart College Attendance Tracker

BunkMate is a Java Swing desktop application designed to help college students manage their attendance, track subject-wise attendance percentages, and make smarter decisions about attending or missing classes.

The application provides an easy-to-use interface with attendance tracking, calculations, calendar history, and future attendance forecasting.

## ✨ Features

### 1. Dashboard
- Overall attendance summary.
- Subject-wise attendance overview.
- Low-attendance warnings.
- Safe bunks information.
- Quick attendance marking for today's classes.

### 2. Subject Management
- Add and manage subjects.
- Set individual minimum attendance percentages.
- View subject-wise attendance statistics.

### 3. Attendance Tracking
- Mark attendance as Present, Absent, or Cancelled.
- Maintain attendance records.
- Edit and delete attendance entries.
- View attendance history.

### 4. Attendance Calculator
- Calculate the number of classes that can safely be missed.
- Calculate the number of classes required to recover attendance.
- Use individual subject attendance requirements.

### 5. Calendar
- View attendance records in a calendar interface.
- Track attendance by date.

### 6. What-if Forecasting
- Predict future attendance percentages.
- Calculate attendance if all upcoming classes are attended.
- Calculate attendance if upcoming classes are missed.
- Check whether the required attendance percentage can be maintained.

### 7. Offline Data Storage
- Automatically save attendance data locally.
- Access attendance information without an internet connection.

## 🛠️ Tech Stack

- **Programming Language:** Java
- **GUI Framework:** Java Swing
- **Date and Time:** Java Time API
- **Data Storage:** Java Serialization
- **IDE:** Visual Studio Code

## 📁 Project Structure

```text
BunkMate/
│
├── Main.java
│
├── src/
│   ├── model/
│   │   ├── Subject.java
│   │   ├── AttendanceRecord.java
│   │   └── AttendanceStatus.java
│   │
│   ├── service/
│   │   ├── AttendanceCalculator.java
│   │   └── StorageService.java
│   │
│   ├── ui/
│   │   └── MainFrame.java
│   │
│   ├── storage/
│   └── util/
│
└── data/
    └── bunkmate.dat
```

## ⚙️ Requirements

- Java JDK 17 or later.
- Visual Studio Code or any Java-supported IDE.
- Java Extension Pack (recommended for VS Code).

## ▶️ How to Run

### Using Visual Studio Code

1. Clone or download the repository.
2. Open the BunkMate folder in Visual Studio Code.
3. Ensure Java is installed and configured.
4. Open `Main.java`.
5. Click **Run Java**.

### Using Terminal

Open the terminal in the project root directory.

Compile:

```powershell
New-Item -ItemType Directory -Force out
javac -d out Main.java src/model/*.java src/service/*.java src/ui/*.java
```

Run:

```powershell
java -cp out Main
```

## 📊 Attendance Calculation

BunkMate follows these attendance rules:

| Attendance Status | Conducted Classes | Attended Classes |
|---|---:|---:|
| Present | +1 | +1 |
| Absent | +1 | +0 |
| Cancelled | +0 | +0 |

Attendance percentage:

```text
Attendance Percentage =
(Attended Classes / Conducted Classes) × 100
```

Cancelled classes are excluded from the calculation.

## 🔮 Future Enhancements

Planned improvements include:

- Weekly timetable and daily class schedule.
- Semester dates and holiday management.
- Attendance reminders.
- Combined daily bunk calculator.
- Attendance charts and reports.
- CSV/PDF export.
- Data backup and restore.
- Packaged desktop application.

## 🔒 Data Storage

BunkMate stores attendance information locally on the user's computer.

The default storage file is:

```text
data/bunkmate.dat
```

Users should keep a backup of their data folder to preserve attendance records.

## 📌 Project Status

BunkMate is an actively developed Java Swing desktop application created as a college academic project.

## 👨‍💻 Author

Developed as a Java application development project.

---

**BunkMate – Track Smart. Attend Wisely.**