package model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents one individual class session for a subject.
 * Multiple records may share the same date (for example, two lab periods).
 *
 * The legacy 'present' field and serialVersionUID are intentionally retained
 * so attendance files created by earlier BunkMate versions remain readable.
 */
public class AttendanceRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private LocalDate date;

    // Kept for backward compatibility with the original serialized format.
    private boolean present;

    // New field used by the upgraded application.
    private AttendanceStatus status;

    /** Creates a record using the new three-state attendance model. */
    public AttendanceRecord(LocalDate date, AttendanceStatus status) {
        this.date = Objects.requireNonNull(date, "Date cannot be null.");
        setStatus(status);
    }

    /** Legacy constructor retained for compatibility with existing code. */
    public AttendanceRecord(LocalDate date, boolean present) {
        this(date, present ? AttendanceStatus.PRESENT : AttendanceStatus.ABSENT);
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = Objects.requireNonNull(date, "Date cannot be null.");
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = Objects.requireNonNull(status, "Status cannot be null.");
        this.present = status == AttendanceStatus.PRESENT;
    }

    /** Returns true only for a Present class. */
    public boolean isPresent() {
        return status == AttendanceStatus.PRESENT;
    }

    /** Legacy setter retained so the current UI continues to work. */
    public void setPresent(boolean present) {
        setStatus(present ? AttendanceStatus.PRESENT : AttendanceStatus.ABSENT);
    }

    /**
     * Old serialized records have no 'status' field. Convert their boolean
     * value into the equivalent new status while loading them.
     */
    private void readObject(ObjectInputStream input)
            throws IOException, ClassNotFoundException {
        input.defaultReadObject();

        if (status == null) {
            status = present
                    ? AttendanceStatus.PRESENT
                    : AttendanceStatus.ABSENT;
        } else {
            present = status == AttendanceStatus.PRESENT;
        }
    }
}
