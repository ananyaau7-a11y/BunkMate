package model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Stores subject details and all attendance information for that subject.
 * Quick/manual totals are retained separately from dated class records.
 */
public class Subject implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final double DEFAULT_MINIMUM_ATTENDANCE = 75.0;

    private String subjectName;
    private ArrayList<AttendanceRecord> records;

    // Existing quick-entry counts. Retained for old saved data compatibility.
    private int manualAttended;
    private int manualAbsent;

    // New subject-specific attendance requirement.
    private double minimumAttendancePercentage;

    public Subject(String subjectName) {
        setSubjectName(subjectName);
        this.records = new ArrayList<>();
        this.manualAttended = 0;
        this.manualAbsent = 0;
        this.minimumAttendancePercentage = DEFAULT_MINIMUM_ATTENDANCE;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        if (subjectName == null || subjectName.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject name cannot be empty.");
        }
        this.subjectName = subjectName.trim();
    }

    public double getMinimumAttendancePercentage() {
        return minimumAttendancePercentage;
    }

    public void setMinimumAttendancePercentage(double percentage) {
        if (Double.isNaN(percentage) || Double.isInfinite(percentage)
                || percentage <= 0.0 || percentage > 100.0) {
            throw new IllegalArgumentException(
                    "Minimum attendance must be greater than 0 and at most 100%."
            );
        }
        this.minimumAttendancePercentage = percentage;
    }

    /**
     * Existing UI compatibility: returns the underlying list.
     * New UI code should prefer the add/update/delete methods below.
     */
    public ArrayList<AttendanceRecord> getRecords() {
        return records;
    }

    // ---------- Quick/manual attendance entry ----------

    /** Adds another batch of historical counts without assigning dates. */
    public void addClassCounts(int attended, int total) {
        validateClassCounts(attended, total);
        manualAttended += attended;
        manualAbsent += total - attended;
    }

    /** Replaces all quick-entry counts while leaving dated records untouched. */
    public void setManualClassCounts(int attended, int total) {
        validateClassCounts(attended, total);
        manualAttended = attended;
        manualAbsent = total - attended;
    }

    public int getManualAttended() {
        return manualAttended;
    }

    public int getManualAbsent() {
        return manualAbsent;
    }

    public int getManualTotal() {
        return manualAttended + manualAbsent;
    }

    private void validateClassCounts(int attended, int total) {
        if (attended < 0 || total < 0 || attended > total) {
            throw new IllegalArgumentException(
                    "Counts must be non-negative and attended cannot exceed total."
            );
        }
    }

    // ---------- Individual class attendance ----------

    /**
     * New method: records a class session. Multiple classes on one date
     * are allowed, which supports double periods and lab sessions.
     */
    public void addAttendanceRecord(LocalDate date, AttendanceStatus status) {
        records.add(new AttendanceRecord(date, status));
    }

    /**
     * Legacy method used by the existing UI. It retains the old duplicate-date
     * check until the UI is upgraded to support multiple sessions per day.
     */
    public boolean markAttendance(LocalDate date, boolean present) {
        Objects.requireNonNull(date, "Date cannot be null.");

        for (AttendanceRecord record : records) {
            if (record.getDate().equals(date)) {
                return false;
            }
        }

        addAttendanceRecord(
                date,
                present ? AttendanceStatus.PRESENT : AttendanceStatus.ABSENT
        );
        return true;
    }

    public void updateAttendance(int index, boolean present) {
        if (isValidRecordIndex(index)) {
            records.get(index).setPresent(present);
        }
    }

    public void updateAttendance(int index, AttendanceStatus status) {
        if (isValidRecordIndex(index)) {
            records.get(index).setStatus(status);
        }
    }

    public void deleteAttendance(int index) {
        if (isValidRecordIndex(index)) {
            records.remove(index);
        }
    }

    private boolean isValidRecordIndex(int index) {
        return index >= 0 && index < records.size();
    }

    // ---------- Attendance totals ----------

    /** Conducted classes exclude cancelled sessions. */
    public int getTotalClasses() {
        return getManualTotal() + getDatedConductedClasses();
    }

    public int getAttendedClasses() {
        int count = manualAttended;
        for (AttendanceRecord record : records) {
            if (record.getStatus() == AttendanceStatus.PRESENT) {
                count++;
            }
        }
        return count;
    }

    public int getAbsentClasses() {
        int count = manualAbsent;
        for (AttendanceRecord record : records) {
            if (record.getStatus() == AttendanceStatus.ABSENT) {
                count++;
            }
        }
        return count;
    }

    public int getCancelledClasses() {
        int count = 0;
        for (AttendanceRecord record : records) {
            if (record.getStatus() == AttendanceStatus.CANCELLED) {
                count++;
            }
        }
        return count;
    }

    private int getDatedConductedClasses() {
        int count = 0;
        for (AttendanceRecord record : records) {
            if (record.getStatus() != AttendanceStatus.CANCELLED) {
                count++;
            }
        }
        return count;
    }

    public double getAttendancePercentage() {
        int total = getTotalClasses();
        if (total == 0) {
            return 0.0;
        }
        return (double) getAttendedClasses() / total * 100.0;
    }

    /**
     * Backward-compatible migration for subjects saved before custom targets
     * and three-state attendance were introduced.
     */
    private void readObject(ObjectInputStream input)
            throws IOException, ClassNotFoundException {
        input.defaultReadObject();

        if (records == null) {
            records = new ArrayList<>();
        }
        if (minimumAttendancePercentage <= 0.0
                || minimumAttendancePercentage > 100.0) {
            minimumAttendancePercentage = DEFAULT_MINIMUM_ATTENDANCE;
        }
        if (manualAttended < 0) {
            manualAttended = 0;
        }
        if (manualAbsent < 0) {
            manualAbsent = 0;
        }
    }
}
