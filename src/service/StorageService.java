
package service;

import model.Subject;

import java.io.*;
import java.util.ArrayList;

public class StorageService {

    private static final String FILE_PATH =
            "data/bunkmate.dat";

    public void saveSubjects(ArrayList<Subject> subjects)
            throws IOException {

        File file = new File(FILE_PATH);

        File parent = file.getParentFile();

        if (parent != null) {
            parent.mkdirs();
        }

        try (ObjectOutputStream output =
                     new ObjectOutputStream(
                             new FileOutputStream(file))) {

            output.writeObject(subjects);
        }
    }

    @SuppressWarnings("unchecked")
    public ArrayList<Subject> loadSubjects()
            throws IOException, ClassNotFoundException {

        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream input =
                     new ObjectInputStream(
                             new FileInputStream(file))) {

            return (ArrayList<Subject>) input.readObject();
        }
    }
}
