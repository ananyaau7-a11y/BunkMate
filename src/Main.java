import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

import java.util.ArrayList;

import model.Subject;
import service.StorageService;
import ui.MainFrame;

public class Main {

    public static void main(String[] args) {

        StorageService storage = new StorageService();

        ArrayList<Subject> subjects;

        try {
            subjects = storage.loadSubjects();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Could not load attendance data: " + e.getMessage(),
                    "Loading Error",
                    JOptionPane.ERROR_MESSAGE
            );

            subjects = new ArrayList<>();
        }

        ArrayList<Subject> finalSubjects = subjects;

        SwingUtilities.invokeLater(() -> {
            new MainFrame(finalSubjects, storage);
        });
    }
}
