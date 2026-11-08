
package service;

import model.Subject;

public class AttendanceCalculator {

    // Calculate how many more classes can be missed
    public int calculateSafeBunks(
            Subject subject, double minimumPercentage) {

        double minimum = minimumPercentage / 100.0;

        if (minimum <= 0 || minimum >= 1) {
            return 0;
        }

        int attended = subject.getAttendedClasses();
        int total = subject.getTotalClasses();

        int safeBunks = (int) Math.floor(
                attended / minimum - total + 0.0000001
        );

        return Math.max(0, safeBunks);
    }

    // Calculate classes required to recover attendance
    public int calculateClassesToRecover(
            Subject subject, double minimumPercentage) {

        double minimum = minimumPercentage / 100.0;

        if (minimum <= 0 || minimum >= 1) {
            return 0;
        }

        int attended = subject.getAttendedClasses();
        int total = subject.getTotalClasses();

        if (total == 0) {
            return 0;
        }

        if (subject.getAttendancePercentage()
                >= minimumPercentage) {
            return 0;
        }

        return (int) Math.ceil(
                (minimum * total - attended) / (1 - minimum)
                - 0.0000001
        );
    }

    // Predict attendance after future classes
    public double calculateForecastPercentage(
            Subject subject,
            int futureAttended,
            int futureAbsent) {

        if (futureAttended < 0 || futureAbsent < 0) {
            throw new IllegalArgumentException(
                    "Future class counts cannot be negative."
            );
        }

        int currentAttended = subject.getAttendedClasses();
        int currentTotal = subject.getTotalClasses();

        int predictedAttended = currentAttended + futureAttended;

        int predictedTotal = currentTotal
                + futureAttended + futureAbsent;

        if (predictedTotal == 0) {
            return 0.0;
        }

        return (double) predictedAttended
                / predictedTotal * 100;
    }

    // Calculate the attendance after attending the next N classes
    public double forecastIfAttendAll(
            Subject subject, int numberOfClasses) {

        return calculateForecastPercentage(
                subject, numberOfClasses, 0
        );
    }

    // Calculate the attendance after missing the next N classes
    public double forecastIfMissAll(
            Subject subject, int numberOfClasses) {

        return calculateForecastPercentage(
                subject, 0, numberOfClasses
        );
    }

    // Check whether a predicted percentage meets the target
    public boolean meetsTarget(
            double forecastPercentage,
            double minimumPercentage) {

        return forecastPercentage >= minimumPercentage;
    }
}
