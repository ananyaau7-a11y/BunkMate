package model;

/**
 * The three possible outcomes for a scheduled class.
 * CANCELLED classes are excluded from attendance calculations.
 */
public enum AttendanceStatus {
    PRESENT,
    ABSENT,
    CANCELLED
}
