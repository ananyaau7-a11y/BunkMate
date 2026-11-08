
package ui;

import model.Subject;
import model.AttendanceRecord;
import model.AttendanceStatus;
import service.AttendanceCalculator;
import service.StorageService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

public class MainFrame extends JFrame {

    private final ArrayList<Subject> subjects;
    private final StorageService storage;
    private final AttendanceCalculator calculator;

    private final Color NAVY = new Color(25, 35, 65);
    private final Color BLUE = new Color(75, 105, 220);
    private final Color BACKGROUND = new Color(245, 247, 252);
    private final Color TEXT = new Color(45, 52, 75);
    private final Color MUTED = new Color(120, 128, 148);
    private final Color GREEN = new Color(35, 160, 110);
    private final Color ORANGE = new Color(225, 145, 45);
    private final Color RED = new Color(210, 75, 75);

    private static final double DEFAULT_MINIMUM_ATTENDANCE = 75.0;
    private JPanel contentPanel;
    private JLabel pageTitle;
    private JLabel pageSubtitle;

    public MainFrame(ArrayList<Subject> subjects,
            StorageService storage) {

        this.subjects = subjects;
        this.storage = storage;
        this.calculator = new AttendanceCalculator();

        setTitle("BunkMate | Smart Attendance Manager");
        setSize(1200, 760);
        setMinimumSize(new Dimension(950, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        buildInterface();
        showDashboard();

        setVisible(true);
    }

    // MAIN INTERFACE

    private void buildInterface() {

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BACKGROUND);

        root.add(createSidebar(), BorderLayout.WEST);

        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setBackground(BACKGROUND);

        mainArea.add(createTopBar(), BorderLayout.NORTH);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BACKGROUND);
        contentPanel.setBorder(new EmptyBorder(25, 30, 30, 30));

        mainArea.add(contentPanel, BorderLayout.CENTER);
        root.add(mainArea, BorderLayout.CENTER);

        setContentPane(root);
    }

    private JPanel createSidebar() {

        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(235, 760));
        sidebar.setBackground(NAVY);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(30, 18, 25, 18));

        JLabel logo = new JLabel("  BunkMate");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 25));
        logo.setForeground(Color.WHITE);

        JLabel tagline = new JLabel("  Smart Attendance Manager");
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tagline.setForeground(new Color(180, 190, 215));

        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(tagline);
        sidebar.add(Box.createVerticalStrut(55));

        JLabel menuLabel = new JLabel("  MENU");
        menuLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        menuLabel.setForeground(new Color(155, 165, 195));

        sidebar.add(menuLabel);
        sidebar.add(Box.createVerticalStrut(15));

        sidebar.add(createNavButton("Dashboard", "dashboard"));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(createNavButton("My Subjects", "subjects"));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(createNavButton("Bunk Calculator", "calculator"));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(createNavButton("Attendance Calendar", "calendar"));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(createNavButton("What-if Forecast", "forecast"));

        sidebar.add(Box.createVerticalGlue());

        JLabel footer = new JLabel("  Made for smarter attendance");
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        footer.setForeground(new Color(155, 165, 195));

        sidebar.add(footer);

        return sidebar;
    }

    private JButton createNavButton(String text, String page) {

        JButton button = new JButton(text);

        button.setMaximumSize(new Dimension(200, 46));
        button.setPreferredSize(new Dimension(200, 46));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(NAVY);
        button.setBorder(new EmptyBorder(10, 16, 10, 10));
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addActionListener(e -> {
            if (page.equals("dashboard")) {
                showDashboard();
            } else if (page.equals("subjects")) {
                showSubjects();
            } else if (page.equals("calculator")) {
                showCalculator();
            } else if (page.equals("calendar")) {
                showCalendar();
            } else if (page.equals("forecast")) {
                showForecast();
            }
        });

        return button;
    }

    private JPanel createTopBar() {

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(new EmptyBorder(18, 30, 18, 30));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        pageTitle = new JLabel("Dashboard");
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 23));
        pageTitle.setForeground(NAVY);

        pageSubtitle = new JLabel("Your attendance at a glance");
        pageSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pageSubtitle.setForeground(MUTED);

        titlePanel.add(pageTitle);
        titlePanel.add(Box.createVerticalStrut(3));
        titlePanel.add(pageSubtitle);

        JLabel profile = new JLabel("Student");
        profile.setFont(new Font("Segoe UI", Font.BOLD, 13));
        profile.setForeground(BLUE);

        topBar.add(titlePanel, BorderLayout.WEST);
        topBar.add(profile, BorderLayout.EAST);

        return topBar;
    }

    private void setPageHeading(String title, String subtitle) {
        pageTitle.setText(title);
        pageSubtitle.setText(subtitle);
    }

    // DASHBOARD

private void showDashboard() {

    setPageHeading("Dashboard", "Your attendance at a glance");

    contentPanel.removeAll();
    contentPanel.setLayout(new BorderLayout(20, 20));

    int totalAttended = 0;
    int totalClasses = 0;
    int totalSafeBunks = 0;
    int lowSubjects = 0;

    // Calculate dashboard statistics using each subject's target
    for (Subject subject : subjects) {

        double target = subject.getMinimumAttendancePercentage();

        totalAttended += subject.getAttendedClasses();
        totalClasses += subject.getTotalClasses();

        totalSafeBunks += calculator.calculateSafeBunks(
                subject, target);

        if (subject.getTotalClasses() > 0
                && subject.getAttendancePercentage() < target) {
            lowSubjects++;
        }
    }

    double overall = totalClasses == 0
            ? 0
            : (double) totalAttended / totalClasses * 100;

    JPanel cards = new JPanel(new GridLayout(1, 5, 12, 0));
    cards.setOpaque(false);
    cards.setPreferredSize(new Dimension(800, 125));

    cards.add(createStatCard(
            "Overall Attendance",
            String.format("%.1f%%", overall),
            "Across all subjects",
            BLUE));

    cards.add(createStatCard(
            "Total Subjects",
            String.valueOf(subjects.size()),
            "Subjects you're tracking",
            new Color(130, 90, 205)));

    cards.add(createStatCard(
            "Classes Attended",
            String.valueOf(totalAttended),
            "Out of " + totalClasses + " classes",
            GREEN));

    cards.add(createStatCard(
            "Safe Bunks",
            String.valueOf(totalSafeBunks),
            "Across all subjects",
            ORANGE));

    cards.add(createStatCard(
            "Subjects Below Target",
            String.valueOf(lowSubjects),
            "Need your attention",
            lowSubjects > 0 ? RED : GREEN));

    JPanel lower = new JPanel(new BorderLayout(15, 15));
    lower.setOpaque(false);

    JPanel overview = createWhitePanel();
    overview.setLayout(new BoxLayout(overview, BoxLayout.Y_AXIS));
    overview.setBorder(new EmptyBorder(22, 22, 22, 22));

    JLabel overviewTitle = new JLabel("Subject Overview");
    overviewTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
    overviewTitle.setForeground(NAVY);

    overview.add(overviewTitle);
    overview.add(Box.createVerticalStrut(18));

    if (subjects.isEmpty()) {
        JLabel empty = new JLabel(
                "No subjects yet. Add your first subject to get started.");
        empty.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        empty.setForeground(MUTED);
        overview.add(empty);
    } else {
        for (Subject subject : subjects) {
            overview.add(createSubjectProgressRow(subject));
            overview.add(Box.createVerticalStrut(18));
        }
    }

    JScrollPane scroll = new JScrollPane(overview);
    scroll.setBorder(null);
    scroll.getViewport().setBackground(BACKGROUND);

    JPanel rightPanel = createWhitePanel();
    rightPanel.setPreferredSize(new Dimension(285, 420));
    rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
    rightPanel.setBorder(new EmptyBorder(22, 20, 22, 20));

    JLabel quickTitle = new JLabel("Quick Summary");
    quickTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
    quickTitle.setForeground(NAVY);

    rightPanel.add(quickTitle);
    rightPanel.add(Box.createVerticalStrut(22));

    addSummaryLine(rightPanel, "Attendance targets", "Individual");
    addSummaryLine(rightPanel, "Combined safe bunks",
            String.valueOf(totalSafeBunks));
    addSummaryLine(rightPanel, "Subjects below target",
            String.valueOf(lowSubjects));

    rightPanel.add(Box.createVerticalStrut(8));

    JLabel quickMarkTitle = new JLabel("Quick Mark Today's Classes");
    quickMarkTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
    quickMarkTitle.setForeground(NAVY);
    rightPanel.add(quickMarkTitle);
    rightPanel.add(Box.createVerticalStrut(8));

    JPanel quickMarkList = new JPanel();
    quickMarkList.setLayout(new BoxLayout(quickMarkList, BoxLayout.Y_AXIS));
    quickMarkList.setOpaque(false);

    if (subjects.isEmpty()) {
        JLabel noSubjects = new JLabel("Add a subject to mark attendance.");
        noSubjects.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        noSubjects.setForeground(MUTED);
        quickMarkList.add(noSubjects);
    } else {
        for (Subject subject : subjects) {
            JPanel actionRow = new JPanel(new BorderLayout(4, 4));
            actionRow.setOpaque(false);
            actionRow.setBorder(new EmptyBorder(5, 0, 5, 0));

            JLabel subjectName = new JLabel(subject.getSubjectName());
            subjectName.setFont(new Font("Segoe UI", Font.BOLD, 11));
            subjectName.setForeground(TEXT);

            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
            buttons.setOpaque(false);
            JButton presentButton = createPrimaryButton("P");
            JButton absentButton = createDangerButton("A");
            JButton cancelledButton = createSecondaryButton("C");
            presentButton.setToolTipText("Mark Present today");
            absentButton.setToolTipText("Mark Absent today");
            cancelledButton.setToolTipText("Mark class Cancelled today");
            presentButton.setPreferredSize(new Dimension(42, 27));
            absentButton.setPreferredSize(new Dimension(42, 27));
            cancelledButton.setPreferredSize(new Dimension(42, 27));

            presentButton.addActionListener(e ->
                    quickMarkAttendance(subject, AttendanceStatus.PRESENT));
            absentButton.addActionListener(e ->
                    quickMarkAttendance(subject, AttendanceStatus.ABSENT));
            cancelledButton.addActionListener(e ->
                    quickMarkAttendance(subject, AttendanceStatus.CANCELLED));

            buttons.add(presentButton);
            buttons.add(absentButton);
            buttons.add(cancelledButton);
            actionRow.add(subjectName, BorderLayout.NORTH);
            actionRow.add(buttons, BorderLayout.CENTER);
            quickMarkList.add(actionRow);
        }
    }

    JScrollPane quickMarkScroll = new JScrollPane(quickMarkList);
    quickMarkScroll.setBorder(null);
    quickMarkScroll.setPreferredSize(new Dimension(245, 190));
    quickMarkScroll.getViewport().setBackground(Color.WHITE);
    rightPanel.add(quickMarkScroll);

    rightPanel.add(Box.createVerticalStrut(10));

    JButton manageButton = createPrimaryButton("Manage Subjects");
    manageButton.addActionListener(e -> showSubjects());

    rightPanel.add(manageButton);

    lower.add(scroll, BorderLayout.CENTER);
    lower.add(rightPanel, BorderLayout.EAST);

    contentPanel.add(cards, BorderLayout.NORTH);
    contentPanel.add(lower, BorderLayout.CENTER);

    refreshContent();
}

    

    private void quickMarkAttendance(Subject subject, AttendanceStatus status) {
        try {
            // Quick actions always record a new session for today's date.
            // Multiple sessions on the same date are supported by BunkMate.
            subject.addAttendanceRecord(LocalDate.now(), status);
            saveData();
            showDashboard();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not mark attendance: " + ex.getMessage(),
                    "Attendance Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createStatCard(
            String title, String value, String description, Color color) {

        JPanel card = createWhitePanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(17, 17, 17, 17));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(MUTED);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 27));
        valueLabel.setForeground(color);

        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        descriptionLabel.setForeground(MUTED);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(descriptionLabel);

        return card;
    }

    private JPanel createSubjectProgressRow(Subject subject) {

        JPanel row = new JPanel(new BorderLayout(10, 8));
        row.setOpaque(false);

        JLabel name = new JLabel(subject.getSubjectName());
        name.setFont(new Font("Segoe UI", Font.BOLD, 14));
        name.setForeground(TEXT);

        double percentage = subject.getAttendancePercentage();
        double target = subject.getMinimumAttendancePercentage();

        JLabel percentageLabel = new JLabel(
                String.format("%.1f%%", percentage));
        percentageLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        percentageLabel.setForeground(getStatusColor(percentage, target));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(name, BorderLayout.WEST);
        top.add(percentageLabel, BorderLayout.EAST);

        JProgressBar progress = new JProgressBar(0, 100);
        progress.setValue((int) Math.round(Math.max(0, Math.min(100, percentage))));
        progress.setStringPainted(false);
        progress.setPreferredSize(new Dimension(100, 9));
        progress.setBorderPainted(false);
        progress.setForeground(getStatusColor(percentage, target));
        progress.setBackground(new Color(232, 235, 242));

        String status;
        if (subject.getTotalClasses() == 0) {
            status = "No attendance recorded yet | Target: " + target + "%";
        } else if (percentage < target) {
            status = calculator.calculateClassesToRecover(subject, target)
                    + " classes needed to reach " + target + "%";
        } else {
            status = calculator.calculateSafeBunks(subject, target)
                    + " safe bunks available | Target: " + target + "%";
        }

        JLabel statusLabel = new JLabel(status);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(MUTED);

        JPanel details = new JPanel(new BorderLayout(0, 5));
        details.setOpaque(false);
        details.add(progress, BorderLayout.NORTH);
        details.add(statusLabel, BorderLayout.SOUTH);

        row.add(top, BorderLayout.NORTH);
        row.add(details, BorderLayout.CENTER);

        return row;
    }

    private void addSummaryLine(JPanel panel, String title, String value) {

        JPanel line = new JPanel(new BorderLayout());
        line.setOpaque(false);
        line.setMaximumSize(new Dimension(250, 35));

        JLabel left = new JLabel(title);
        left.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        left.setForeground(MUTED);

        JLabel right = new JLabel(value);
        right.setFont(new Font("Segoe UI", Font.BOLD, 13));
        right.setForeground(TEXT);

        line.add(left, BorderLayout.WEST);
        line.add(right, BorderLayout.EAST);

        panel.add(line);
        panel.add(Box.createVerticalStrut(13));
    }

    // SUBJECT MANAGEMENT

    private void showSubjects() {

        setPageHeading(
                "My Subjects",
                "Add subjects and manage your attendance");

        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout(15, 20));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel info = new JLabel(
                "Keep track of every subject in one place.");
        info.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        info.setForeground(MUTED);

        JButton addButton = createPrimaryButton("+ Add Subject");
        addButton.addActionListener(e -> addSubject());

        top.add(info, BorderLayout.WEST);
        top.add(addButton, BorderLayout.EAST);

        String[] columns = {
                "Subject", "Attended", "Total Classes", "Cancelled",
                "Attendance", "Target", "Safe Bunks", "Status"
        };

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Subject subject : subjects) {
            double percentage = subject.getAttendancePercentage();
            double target = subject.getMinimumAttendancePercentage();

            String status;
            if (subject.getTotalClasses() == 0) {
                status = "Not started";
            } else if (percentage < target) {
                status = "Below target";
            } else {
                status = "On track";
            }

            model.addRow(new Object[] {
                    subject.getSubjectName(),
                    subject.getAttendedClasses(),
                    subject.getTotalClasses(),
                    subject.getCancelledClasses(),
                    String.format("%.1f%%", percentage),
                    String.format("%.1f%%", target),
                    calculator.calculateSafeBunks(subject, target),
                    status
            });
        }

        JTable table = new JTable(model);
        table.setRowHeight(42);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(TEXT);
        table.setGridColor(new Color(235, 237, 243));
        table.setSelectionBackground(new Color(225, 232, 255));
        table.setSelectionForeground(NAVY);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(8, 0));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        table.getTableHeader().setFont(
                new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(
                new Color(240, 243, 249));
        table.getTableHeader().setForeground(NAVY);
        table.getTableHeader().setPreferredSize(
                new Dimension(100, 42));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(
                new Color(230, 233, 240)));

        JPanel actions = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 8, 5));
        actions.setOpaque(false);

        JButton markButton = createPrimaryButton("Mark Attendance");
        JButton addCountsButton = createPrimaryButton("Add Class Counts");
        JButton editCountsButton = createSecondaryButton("Edit Class Counts");
        JButton historyButton = createSecondaryButton("View History");
        JButton editButton = createSecondaryButton("Edit Subject");
        JButton deleteButton = createDangerButton("Delete");

        markButton.addActionListener(e -> markAttendance(table.getSelectedRow()));
        addCountsButton.addActionListener(e -> addClassCounts(table.getSelectedRow()));
        editCountsButton.addActionListener(e -> editClassCounts(table.getSelectedRow()));
        historyButton.addActionListener(e -> showHistory(table.getSelectedRow()));
        editButton.addActionListener(e -> editSubject(table.getSelectedRow()));
        deleteButton.addActionListener(e -> deleteSubject(table.getSelectedRow()));

        actions.add(markButton);
        actions.add(addCountsButton);
        actions.add(editCountsButton);
        actions.add(historyButton);
        actions.add(editButton);
        actions.add(deleteButton);

        contentPanel.add(top, BorderLayout.NORTH);
        contentPanel.add(scroll, BorderLayout.CENTER);
        contentPanel.add(actions, BorderLayout.SOUTH);

        refreshContent();
    }

    private Subject getSelectedSubject(int row) {

        if (row < 0 || row >= subjects.size()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a subject first.",
                    "No Subject Selected",
                    JOptionPane.INFORMATION_MESSAGE);
            return null;
        }

        return subjects.get(row);
    }

    private void addSubject() {

        JTextField nameField = new JTextField();
        JTextField targetField = new JTextField(
                String.valueOf(DEFAULT_MINIMUM_ATTENDANCE));

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 12));
        panel.add(new JLabel("Subject name:"));
        panel.add(nameField);
        panel.add(new JLabel("Minimum attendance target (%):"));
        panel.add(targetField);

        int choice = JOptionPane.showConfirmDialog(
                this, panel, "Add Subject",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a subject name.");
            return;
        }

        for (Subject existing : subjects) {
            if (existing.getSubjectName().equalsIgnoreCase(name)) {
                JOptionPane.showMessageDialog(this, "This subject already exists.");
                return;
            }
        }

        try {
            double target = Double.parseDouble(targetField.getText().trim());
            validateAttendanceTarget(target);

            Subject subject = new Subject(name);
            subject.setMinimumAttendancePercentage(target);
            subjects.add(subject);

            saveData();
            showSubjects();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    this, "Enter a valid attendance target between 1 and 99.");
        }
    }

    private void editSubject(int row) {

        Subject subject = getSelectedSubject(row);
        if (subject == null) {
            return;
        }

        JTextField nameField = new JTextField(subject.getSubjectName());
        JTextField targetField = new JTextField(
                String.valueOf(subject.getMinimumAttendancePercentage()));

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 12));
        panel.add(new JLabel("Subject name:"));
        panel.add(nameField);
        panel.add(new JLabel("Minimum attendance target (%):"));
        panel.add(targetField);

        int choice = JOptionPane.showConfirmDialog(
                this, panel, "Edit Subject",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Subject name cannot be empty.");
            return;
        }

        for (Subject existing : subjects) {
            if (existing != subject
                    && existing.getSubjectName().equalsIgnoreCase(name)) {
                JOptionPane.showMessageDialog(this, "This subject already exists.");
                return;
            }
        }

        try {
            double target = Double.parseDouble(targetField.getText().trim());
            validateAttendanceTarget(target);

            subject.setSubjectName(name);
            subject.setMinimumAttendancePercentage(target);

            saveData();
            showSubjects();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    this, "Enter a valid attendance target between 1 and 99.");
        }
    }

    private void validateAttendanceTarget(double target) {
        if (Double.isNaN(target) || Double.isInfinite(target)
                || target < 1 || target > 99) {
            throw new IllegalArgumentException("Invalid attendance target");
        }
    }


    private void deleteSubject(int row) {

        Subject subject = getSelectedSubject(row);

        if (subject == null)
            return;

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Delete " + subject.getSubjectName()
                        + " and all its attendance records?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            subjects.remove(subject);
            saveData();
            showSubjects();
        }
    }

    // QUICK ATTENDANCE ENTRY

    private void addClassCounts(int row) {

        Subject subject = getSelectedSubject(row);

        if (subject == null)
            return;

        JTextField totalField = new JTextField();
        JTextField attendedField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 12));

        panel.add(new JLabel("Total classes conducted:"));
        panel.add(totalField);
        panel.add(new JLabel("Classes attended:"));
        panel.add(attendedField);

        int choice = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Add Attendance - " + subject.getSubjectName(),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (choice != JOptionPane.OK_OPTION)
            return;

        try {

            int total = Integer.parseInt(totalField.getText().trim());
            int attended = Integer.parseInt(attendedField.getText().trim());

            if (total <= 0 || attended < 0 || attended > total) {
                throw new IllegalArgumentException();
            }

            subject.addClassCounts(attended, total);

            saveData();
            showSubjects();

            JOptionPane.showMessageDialog(
                    this, "Attendance counts added successfully!");

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Enter valid numbers. Attended classes cannot exceed total classes.");
        }
    }

    private void editClassCounts(int row) {

        Subject subject = getSelectedSubject(row);

        if (subject == null)
            return;

        JTextField totalField = new JTextField(
                String.valueOf(subject.getManualTotal()));

        JTextField attendedField = new JTextField(
                String.valueOf(subject.getManualAttended()));

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 12));

        panel.add(new JLabel("Total classes conducted:"));
        panel.add(totalField);
        panel.add(new JLabel("Classes attended:"));
        panel.add(attendedField);

        int choice = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Edit Attendance - " + subject.getSubjectName(),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (choice != JOptionPane.OK_OPTION)
            return;

        try {

            int total = Integer.parseInt(totalField.getText().trim());
            int attended = Integer.parseInt(attendedField.getText().trim());

            if (total < 0 || attended < 0 || attended > total) {
                throw new IllegalArgumentException();
            }

            subject.setManualClassCounts(attended, total);

            saveData();
            showSubjects();

            JOptionPane.showMessageDialog(
                    this, "Attendance counts updated successfully!");

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Enter valid numbers. Attended classes cannot exceed total classes.");
        }
    }

    // INDIVIDUAL ATTENDANCE

    private void markAttendance(int row) {

        Subject subject = getSelectedSubject(row);
        if (subject == null) {
            return;
        }

        JTextField dateField = new JTextField(LocalDate.now().toString());
        JPanel datePanel = new JPanel(new BorderLayout(8, 0));
        datePanel.add(new JLabel("Class date (YYYY-MM-DD):"), BorderLayout.WEST);
        datePanel.add(dateField, BorderLayout.CENTER);

        int dateChoice = JOptionPane.showConfirmDialog(
                this, datePanel, "Add Attendance Session",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (dateChoice != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            LocalDate date = LocalDate.parse(dateField.getText().trim());

            if (date.isAfter(LocalDate.now())) {
                JOptionPane.showMessageDialog(
                        this, "Future dates are not allowed.");
                return;
            }

            String[] options = {"Present", "Absent", "Cancelled", "Back"};
            int choice = JOptionPane.showOptionDialog(
                    this,
                    "Select the status for this " + subject.getSubjectName()
                            + " class session.",
                    "Mark Attendance",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);

            AttendanceStatus status;
            switch (choice) {
                case 0:
                    status = AttendanceStatus.PRESENT;
                    break;
                case 1:
                    status = AttendanceStatus.ABSENT;
                    break;
                case 2:
                    status = AttendanceStatus.CANCELLED;
                    break;
                default:
                    return;
            }

            // Multiple sessions on the same date are intentionally allowed.
            subject.addAttendanceRecord(date, status);
            saveData();
            showSubjects();

        } catch (java.time.format.DateTimeParseException ex) {
            JOptionPane.showMessageDialog(
                    this, "Invalid date. Please use YYYY-MM-DD.",
                    "Invalid Date", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(
                    this, "Could not add attendance: " + ex.getMessage(),
                    "Attendance Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showHistory(int row) {

        Subject subject = getSelectedSubject(row);
        if (subject == null) {
            return;
        }

        JDialog dialog = new JDialog(
                this,
                "Attendance History - " + subject.getSubjectName(),
                true);

        dialog.setSize(650, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(12, 12));

        JLabel quickSummary = new JLabel(
                "Quick entry: " + subject.getManualAttended()
                        + " present, " + subject.getManualAbsent()
                        + " absent | Dated cancelled sessions: "
                        + subject.getCancelledClasses());

        quickSummary.setBorder(new EmptyBorder(12, 12, 5, 12));
        quickSummary.setFont(new Font("Segoe UI", Font.BOLD, 13));
        quickSummary.setForeground(BLUE);

        String[] columns = {"Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (AttendanceRecord record : subject.getRecords()) {
            model.addRow(new Object[]{
                    record.getDate(),
                    formatStatus(record.getStatus())
            });
        }

        JTable table = new JTable(model);
        table.setRowHeight(34);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JButton editButton = createPrimaryButton("Edit Status");
        JButton deleteButton = createDangerButton("Delete Record");
        JButton closeButton = createSecondaryButton("Close");

        editButton.addActionListener(e -> {
            int selected = table.getSelectedRow();
            if (selected < 0) {
                JOptionPane.showMessageDialog(dialog, "Select a record first.");
                return;
            }

            AttendanceRecord record = subject.getRecords().get(selected);
            String[] options = {"Present", "Absent", "Cancelled", "Back"};

            int choice = JOptionPane.showOptionDialog(
                    dialog,
                    "Update session on " + record.getDate() + ".",
                    "Edit Attendance Status",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);

            AttendanceStatus newStatus;
            switch (choice) {
                case 0:
                    newStatus = AttendanceStatus.PRESENT;
                    break;
                case 1:
                    newStatus = AttendanceStatus.ABSENT;
                    break;
                case 2:
                    newStatus = AttendanceStatus.CANCELLED;
                    break;
                default:
                    return;
            }

            subject.updateAttendance(selected, newStatus);
            saveData();
            model.setValueAt(formatStatus(newStatus), selected, 1);
            quickSummary.setText(
                    "Quick entry: " + subject.getManualAttended()
                            + " present, " + subject.getManualAbsent()
                            + " absent | Dated cancelled sessions: "
                            + subject.getCancelledClasses());
            showSubjects();
        });

        deleteButton.addActionListener(e -> {
            int selected = table.getSelectedRow();
            if (selected < 0) {
                JOptionPane.showMessageDialog(dialog, "Select a record first.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    dialog,
                    "Delete the selected attendance session?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                subject.deleteAttendance(selected);
                saveData();
                model.removeRow(selected);
                quickSummary.setText(
                        "Quick entry: " + subject.getManualAttended()
                                + " present, " + subject.getManualAbsent()
                                + " absent | Dated cancelled sessions: "
                                + subject.getCancelledClasses());
                showSubjects();
            }
        });

        closeButton.addActionListener(e -> dialog.dispose());

        JPanel buttons = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttons.add(editButton);
        buttons.add(deleteButton);
        buttons.add(closeButton);

        JPanel historyTop = new JPanel(new BorderLayout());
        historyTop.add(quickSummary, BorderLayout.NORTH);

        dialog.add(historyTop, BorderLayout.NORTH);
        dialog.add(new JScrollPane(table), BorderLayout.CENTER);
        dialog.add(buttons, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private String formatStatus(AttendanceStatus status) {
        if (status == null) {
            return "Unknown";
        }
        switch (status) {
            case PRESENT:
                return "Present";
            case ABSENT:
                return "Absent";
            case CANCELLED:
                return "Cancelled";
            default:
                return "Unknown";
        }
    }

    // BUNK CALCULATOR

    private void showCalculator() {

        setPageHeading(
                "Bunk Calculator",
                "Plan your attendance before deciding to bunk");

        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        JPanel calculatorPanel = createWhitePanel();
        calculatorPanel.setPreferredSize(new Dimension(570, 410));
        calculatorPanel.setBorder(new EmptyBorder(30, 35, 30, 35));
        calculatorPanel.setLayout(new BoxLayout(
                calculatorPanel, BoxLayout.Y_AXIS));

        JLabel heading = new JLabel("Calculate Your Safe Bunks");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 21));
        heading.setForeground(NAVY);

        JLabel description = new JLabel(
                "Choose a subject to see how many classes you can miss.");
        description.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        description.setForeground(MUTED);

        calculatorPanel.add(heading);
        calculatorPanel.add(Box.createVerticalStrut(8));
        calculatorPanel.add(description);
        calculatorPanel.add(Box.createVerticalStrut(28));

        JLabel subjectLabel = new JLabel("Select Subject");
        subjectLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        subjectLabel.setForeground(TEXT);

        JComboBox<Subject> subjectBox = new JComboBox<>();

        for (Subject subject : subjects) {
            subjectBox.addItem(subject);
        }

        subjectBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subjectBox.setMaximumSize(new Dimension(
                Integer.MAX_VALUE, 40));

        subjectBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {

                super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

                if (value instanceof Subject) {
                    setText(((Subject) value).getSubjectName());
                }

                return this;
            }
        });

        JLabel targetLabel = new JLabel("Minimum Attendance Target (%)");
        targetLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        targetLabel.setForeground(TEXT);

        double initialTarget = subjects.isEmpty()
                ? DEFAULT_MINIMUM_ATTENDANCE
                : subjects.get(0).getMinimumAttendancePercentage();
        JTextField targetField = new JTextField(String.valueOf(initialTarget));
        targetField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        targetField.setMaximumSize(new Dimension(
                Integer.MAX_VALUE, 40));

        subjectBox.addActionListener(e -> {
            Subject selectedSubject = (Subject) subjectBox.getSelectedItem();
            if (selectedSubject != null) {
                targetField.setText(String.valueOf(
                        selectedSubject.getMinimumAttendancePercentage()));
            }
        });

        JButton calculateButton = createPrimaryButton(
                "Calculate Attendance");

        JLabel result = new JLabel(
                "Your results will appear here.");
        result.setFont(new Font("Segoe UI", Font.BOLD, 14));
        result.setForeground(BLUE);

        JLabel detail = new JLabel(" ");
        detail.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detail.setForeground(MUTED);

        calculateButton.addActionListener(e -> {

            Subject subject = (Subject) subjectBox.getSelectedItem();

            if (subject == null) {
                JOptionPane.showMessageDialog(
                        this, "Please add a subject first.");
                return;
            }

            try {

                double target = Double.parseDouble(
                        targetField.getText().trim());

                if (target <= 0 || target >= 100) {
                    throw new NumberFormatException();
                }

                int safeBunks = calculator.calculateSafeBunks(
                        subject, target);

                int recovery = calculator.calculateClassesToRecover(
                        subject, target);

                double current = subject.getAttendancePercentage();

                result.setText(
                        String.format("Current attendance: %.1f%%", current));

                if (subject.getTotalClasses() == 0) {
                    detail.setText(
                            "No classes recorded yet. Start by marking attendance.");
                } else if (current >= target) {
                    detail.setText(
                            "You can miss " + safeBunks
                                    + " more classes and stay at or above "
                                    + target + "%.");
                } else {
                    detail.setText(
                            "Attend " + recovery
                                    + " consecutive classes to reach "
                                    + target + "%.");
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Enter a valid target between 1 and 99.");
            }
        });

        calculatorPanel.add(subjectLabel);
        calculatorPanel.add(Box.createVerticalStrut(8));
        calculatorPanel.add(subjectBox);
        calculatorPanel.add(Box.createVerticalStrut(20));
        calculatorPanel.add(targetLabel);
        calculatorPanel.add(Box.createVerticalStrut(8));
        calculatorPanel.add(targetField);
        calculatorPanel.add(Box.createVerticalStrut(22));
        calculatorPanel.add(calculateButton);
        calculatorPanel.add(Box.createVerticalStrut(25));
        calculatorPanel.add(result);
        calculatorPanel.add(Box.createVerticalStrut(8));
        calculatorPanel.add(detail);

        wrapper.add(calculatorPanel);

        contentPanel.add(wrapper, BorderLayout.CENTER);

        refreshContent();
    }

    // ATTENDANCE CALENDAR

    private void showCalendar() {
        setPageHeading("Attendance Calendar",
                "Explore your recorded attendance day by day");
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout(18, 18));

        JPanel top = new JPanel(new BorderLayout(12, 8));
        top.setOpaque(false);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        controls.setOpaque(false);
        JButton previous = createSecondaryButton("< Previous");
        JButton next = createSecondaryButton("Next >");
        JButton today = createSecondaryButton("Today");
        JLabel monthLabel = new JLabel();
        monthLabel.setFont(new Font("Segoe UI", Font.BOLD, 19));
        monthLabel.setForeground(NAVY);

        JComboBox<Object> subjectFilter = new JComboBox<>();
        subjectFilter.addItem("All Subjects");
        for (Subject subject : subjects) {
            subjectFilter.addItem(subject);
        }
        subjectFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subjectFilter.setPreferredSize(new Dimension(190, 36));
        subjectFilter.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list,
                    Object value, int index, boolean selected, boolean focus) {
                super.getListCellRendererComponent(list, value, index, selected, focus);
                setText(value instanceof Subject
                        ? ((Subject) value).getSubjectName() : "All Subjects");
                return this;
            }
        });

        controls.add(previous);
        controls.add(next);
        controls.add(today);
        controls.add(monthLabel);
        controls.add(Box.createHorizontalStrut(15));
        controls.add(new JLabel("Subject:"));
        controls.add(subjectFilter);
        top.add(controls, BorderLayout.WEST);

        JPanel calendarCard = createWhitePanel();
        calendarCard.setLayout(new BorderLayout(8, 8));
        calendarCard.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel weekdays = new JPanel(new GridLayout(1, 7, 6, 6));
        weekdays.setOpaque(false);
        String[] weekdayNames = {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
        for (String name : weekdayNames) {
            JLabel label = new JLabel(name, SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 11));
            label.setForeground(MUTED);
            weekdays.add(label);
        }

        JPanel daysGrid = new JPanel(new GridLayout(6, 7, 6, 6));
        daysGrid.setOpaque(false);
        calendarCard.add(weekdays, BorderLayout.NORTH);
        calendarCard.add(daysGrid, BorderLayout.CENTER);

        JLabel legend = new JLabel(
                "Present / Absent / Cancelled markers • Select a date to view sessions • Quick-entry totals have no dates");
        legend.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        legend.setForeground(MUTED);
        calendarCard.add(legend, BorderLayout.SOUTH);

        final YearMonth[] displayedMonth = {YearMonth.now()};
        Runnable renderCalendar = () -> {
            YearMonth month = displayedMonth[0];
            monthLabel.setText(month.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                    + " " + month.getYear());
            daysGrid.removeAll();

            int leading = month.atDay(1).getDayOfWeek().getValue() - 1;
            int daysInMonth = month.lengthOfMonth();
            for (int cell = 0; cell < 42; cell++) {
                int dayNumber = cell - leading + 1;
                if (dayNumber < 1 || dayNumber > daysInMonth) {
                    JPanel empty = new JPanel();
                    empty.setOpaque(false);
                    daysGrid.add(empty);
                    continue;
                }

                LocalDate date = month.atDay(dayNumber);
                ArrayList<AttendanceRecord> dayRecords = getRecordsForDate(
                        date, subjectFilter.getSelectedItem());
                int present = 0, absent = 0, cancelled = 0;
                for (AttendanceRecord record : dayRecords) {
                    if (record.getStatus() == AttendanceStatus.PRESENT) present++;
                    else if (record.getStatus() == AttendanceStatus.ABSENT) absent++;
                    else if (record.getStatus() == AttendanceStatus.CANCELLED) cancelled++;
                }

                JButton dayButton = new JButton();
                String marker = dayRecords.isEmpty() ? " " :
                        "<font color='#23A06E'>●" + present
                        + "</font> <font color='#D24B4B'>●" + absent
                        + "</font> <font color='#E1912D'>●" + cancelled + "</font>";
                dayButton.setText("<html><div style='text-align:center;'><b>"
                        + dayNumber + "</b><br>" + marker + "</div></html>");
                dayButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                dayButton.setForeground(TEXT);
                dayButton.setBackground(dayRecords.isEmpty()
                        ? new Color(248, 249, 252) : new Color(239, 243, 255));
                dayButton.setFocusPainted(false);
                dayButton.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(date.equals(LocalDate.now())
                                ? BLUE : new Color(232, 235, 242),
                                date.equals(LocalDate.now()) ? 2 : 1),
                        new EmptyBorder(5, 2, 5, 2)));
                dayButton.setToolTipText(dayRecords.size() + " recorded session(s)");
                dayButton.addActionListener(e -> showCalendarDayDetails(
                        date, subjectFilter.getSelectedItem()));
                daysGrid.add(dayButton);
            }
            daysGrid.revalidate();
            daysGrid.repaint();
        };

        previous.addActionListener(e -> {
            displayedMonth[0] = displayedMonth[0].minusMonths(1);
            renderCalendar.run();
        });
        next.addActionListener(e -> {
            displayedMonth[0] = displayedMonth[0].plusMonths(1);
            renderCalendar.run();
        });
        today.addActionListener(e -> {
            displayedMonth[0] = YearMonth.now();
            renderCalendar.run();
        });
        subjectFilter.addActionListener(e -> renderCalendar.run());

        JPanel summary = createWhitePanel();
        summary.setLayout(new GridLayout(1, 4, 12, 0));
        summary.setBorder(new EmptyBorder(15, 18, 15, 18));
        int totalPresent = 0, totalAbsent = 0, totalCancelled = 0;
        for (Subject subject : subjects) {
            totalPresent += subject.getAttendedClasses() - subject.getManualAttended();
            totalAbsent += subject.getAbsentClasses() - subject.getManualAbsent();
            totalCancelled += subject.getCancelledClasses();
        }
        summary.add(createMiniSummary("Present sessions", String.valueOf(totalPresent), GREEN));
        summary.add(createMiniSummary("Absent sessions", String.valueOf(totalAbsent), RED));
        summary.add(createMiniSummary("Cancelled sessions", String.valueOf(totalCancelled), ORANGE));
        summary.add(createMiniSummary("Tracked subjects", String.valueOf(subjects.size()), BLUE));

        JPanel center = new JPanel(new BorderLayout(0, 14));
        center.setOpaque(false);
        center.add(calendarCard, BorderLayout.CENTER);
        center.add(summary, BorderLayout.SOUTH);
        contentPanel.add(top, BorderLayout.NORTH);
        contentPanel.add(center, BorderLayout.CENTER);
        renderCalendar.run();
        refreshContent();
    }

    private ArrayList<AttendanceRecord> getRecordsForDate(
            LocalDate date, Object selectedFilter) {
        ArrayList<AttendanceRecord> result = new ArrayList<>();
        for (Subject subject : subjects) {
            if (selectedFilter instanceof Subject && selectedFilter != subject) {
                continue;
            }
            for (AttendanceRecord record : subject.getRecords()) {
                if (date.equals(record.getDate())) result.add(record);
            }
        }
        return result;
    }

    private void showCalendarDayDetails(LocalDate date, Object selectedFilter) {
        JDialog dialog = new JDialog(this, "Attendance on " + date, true);
        dialog.setSize(520, 360);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Subject", "Status"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        for (Subject subject : subjects) {
            if (selectedFilter instanceof Subject && selectedFilter != subject) continue;
            for (AttendanceRecord record : subject.getRecords()) {
                if (date.equals(record.getDate())) {
                    model.addRow(new Object[]{subject.getSubjectName(),
                            formatStatus(record.getStatus())});
                }
            }
        }

        JLabel heading = new JLabel("  " + date.getDayOfWeek() + ", " + date);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 16));
        heading.setForeground(NAVY);
        JTable table = new JTable(model);
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        JButton close = createSecondaryButton("Close");
        close.addActionListener(e -> dialog.dispose());
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(close);
        dialog.add(heading, BorderLayout.NORTH);
        dialog.add(new JScrollPane(table), BorderLayout.CENTER);
        dialog.add(bottom, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JPanel createMiniSummary(String heading, String value, Color color) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel number = new JLabel(value);
        number.setFont(new Font("Segoe UI", Font.BOLD, 22));
        number.setForeground(color);
        JLabel label = new JLabel(heading);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        label.setForeground(MUTED);
        panel.add(number);
        panel.add(Box.createVerticalStrut(3));
        panel.add(label);
        return panel;
    }

    // WHAT-IF FORECAST

    private void showForecast() {
        setPageHeading("What-if Forecast", 
                "See how future attendance choices affect your percentage");
        contentPanel.removeAll();
        contentPanel.setLayout(new GridBagLayout());

        JPanel outer = new JPanel(new BorderLayout(15, 15));
        outer.setOpaque(false);
        outer.setPreferredSize(new Dimension(760, 530));

        JPanel form = createWhitePanel();
        form.setBorder(new EmptyBorder(25, 30, 25, 30));
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Plan Your Upcoming Classes");
        title.setFont(new Font("Segoe UI", Font.BOLD, 21));
        title.setForeground(NAVY);
        JLabel description = new JLabel(
                "Try different attendance scenarios before making a decision.");
        description.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        description.setForeground(MUTED);
        form.add(title);
        form.add(Box.createVerticalStrut(6));
        form.add(description);
        form.add(Box.createVerticalStrut(24));

        JLabel subjectLabel = new JLabel("Select Subject");
        subjectLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        subjectLabel.setForeground(TEXT);
        JComboBox<Subject> subjectBox = new JComboBox<>();
        for (Subject subject : subjects) subjectBox.addItem(subject);
        subjectBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subjectBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        subjectBox.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list,
                    Object value, int index, boolean selected, boolean focus) {
                super.getListCellRendererComponent(list, value, index, selected, focus);
                if (value instanceof Subject) setText(((Subject) value).getSubjectName());
                return this;
            }
        });

        JPanel counts = new JPanel(new GridLayout(1, 2, 18, 0));
        counts.setOpaque(false);
        JSpinner attendSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 200, 1));
        JSpinner missSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 200, 1));
        counts.add(createSpinnerField("Future classes to attend", attendSpinner));
        counts.add(createSpinnerField("Future classes to miss", missSpinner));

        JLabel currentLabel = new JLabel("Current attendance: —");
        currentLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        currentLabel.setForeground(BLUE);
        JLabel targetLabel = new JLabel("Minimum target: —");
        targetLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        targetLabel.setForeground(MUTED);

        JButton calculate = createPrimaryButton("Calculate Forecast");
        calculate.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel resultCard = createWhitePanel();
        resultCard.setBorder(new EmptyBorder(20, 22, 20, 22));
        resultCard.setLayout(new BoxLayout(resultCard, BoxLayout.Y_AXIS));
        JLabel forecastValue = new JLabel("Your projected attendance will appear here.");
        forecastValue.setFont(new Font("Segoe UI", Font.BOLD, 19));
        forecastValue.setForeground(NAVY);
        JLabel forecastStatus = new JLabel(" ");
        forecastStatus.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JLabel forecastDetail = new JLabel(" ");
        forecastDetail.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        forecastDetail.setForeground(MUTED);
        resultCard.add(forecastValue);
        resultCard.add(Box.createVerticalStrut(8));
        resultCard.add(forecastStatus);
        resultCard.add(Box.createVerticalStrut(6));
        resultCard.add(forecastDetail);

        Runnable updateCurrent = () -> {
            Subject selected = (Subject) subjectBox.getSelectedItem();
            if (selected == null) {
                currentLabel.setText("Current attendance: —");
                targetLabel.setText("Minimum target: —");
            } else {
                currentLabel.setText(String.format(Locale.ENGLISH,
                        "Current attendance: %.2f%% (%d / %d classes)",
                        selected.getAttendancePercentage(), selected.getAttendedClasses(),
                        selected.getTotalClasses()));
                targetLabel.setText(String.format(Locale.ENGLISH,
                        "Minimum target: %.1f%%", selected.getMinimumAttendancePercentage()));
            }
            forecastValue.setText("Your projected attendance will appear here.");
            forecastStatus.setText(" ");
            forecastDetail.setText(" ");
        };
        subjectBox.addActionListener(e -> updateCurrent.run());

        calculate.addActionListener(e -> {
            Subject selected = (Subject) subjectBox.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please add a subject first.");
                return;
            }
            int futureAttend = (Integer) attendSpinner.getValue();
            int futureMiss = (Integer) missSpinner.getValue();
            double projected = calculator.calculateForecastPercentage(
                    selected, futureAttend, futureMiss);
            double current = selected.getAttendancePercentage();
            double target = selected.getMinimumAttendancePercentage();
            boolean meets = calculator.meetsTarget(projected, target);
            forecastValue.setText(String.format(Locale.ENGLISH,
                    "Projected attendance: %.2f%%", projected));
            forecastStatus.setText(meets
                    ? "You will meet your attendance target."
                    : "You will fall below your attendance target.");
            forecastStatus.setForeground(meets ? GREEN : RED);
            int projectedAttended = selected.getAttendedClasses() + futureAttend;
            int projectedTotal = selected.getTotalClasses() + futureAttend + futureMiss;
            forecastDetail.setText(String.format(Locale.ENGLISH,
                    "Projected total: %d / %d classes  |  Change: %+.2f percentage points  |  Target: %.1f%%",
                    projectedAttended, projectedTotal, projected - current, target));
        });

        form.add(subjectLabel);
        form.add(Box.createVerticalStrut(7));
        form.add(subjectBox);
        form.add(Box.createVerticalStrut(20));
        form.add(counts);
        form.add(Box.createVerticalStrut(18));
        form.add(currentLabel);
        form.add(Box.createVerticalStrut(5));
        form.add(targetLabel);
        form.add(Box.createVerticalStrut(18));
        form.add(calculate);

        outer.add(form, BorderLayout.CENTER);
        outer.add(resultCard, BorderLayout.SOUTH);
        contentPanel.add(outer);
        updateCurrent.run();
        refreshContent();
    }

    private JPanel createSpinnerField(String labelText, JSpinner spinner) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(TEXT);
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        spinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        panel.add(label);
        panel.add(Box.createVerticalStrut(7));
        panel.add(spinner);
        return panel;
    }

    // UI HELPERS

    private JPanel createWhitePanel() {

        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);

        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(232, 235, 242)),
                new EmptyBorder(5, 5, 5, 5)));

        return panel;
    }

    private JButton createPrimaryButton(String text) {

        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(BLUE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(11, 18, 11, 18));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private JButton createSecondaryButton(String text) {

        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(NAVY);
        button.setBackground(new Color(235, 239, 250));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(11, 16, 11, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private JButton createDangerButton(String text) {

        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(RED);
        button.setBackground(new Color(255, 238, 238));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(11, 16, 11, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private Color getStatusColor(double percentage, double target) {

        if (percentage >= target) {
            return GREEN;
        } else if (percentage >= target - 10) {
            return ORANGE;
        } else {
            return RED;
        }
    }

    private void refreshContent() {
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // SAVE DATA

    private void saveData() {

        try {
            storage.saveSubjects(subjects);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not save data: " + e.getMessage(),
                    "Storage Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
