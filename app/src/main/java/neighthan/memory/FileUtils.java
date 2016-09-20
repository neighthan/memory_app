package neighthan.memory;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 *
 */
class FileUtils {
    private static final String TMP_FILE_NAME = "tmpFile.csv";
    // todo make this work with internal storage file too
    private static final File TMP_FILE = new File(Environment.getExternalStorageDirectory(), TMP_FILE_NAME);

    static void addRow(String toAdd) {
        try (FileWriter toMemoriesFile = new FileWriter(Constants.MEMORIES_FILE, true)) { // append mode
            toMemoriesFile.write(toAdd + "\n");
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, "IOException in FileUtils::addRow", e);
        }
    }

    static void editRow(int rowToChange, String newMemoryString) {
        boolean successful = false;
        boolean editing = !newMemoryString.isEmpty();

        if (editing) {
            Log.d(Constants.LOG_TAG, "Editing row <" + rowToChange + "> from " + Constants.MEMORIES_FILE_NAME);
        } else {
            Log.d(Constants.LOG_TAG, "Deleting row <" + rowToChange + "> from " + Constants.MEMORIES_FILE_NAME);
        }

        try (BufferedReader fileReader = new BufferedReader(new FileReader(Constants.MEMORIES_FILE));
             BufferedWriter fileWriter = new BufferedWriter(new FileWriter(TMP_FILE))) {
            String line;
            int currentRow = 0;
            while ((line = fileReader.readLine()) != null) {
                if (currentRow == rowToChange) {
                    if (editing) {
                        fileWriter.write(newMemoryString);
                        fileWriter.newLine();
                    }
                    // if deleting, we just don't write anything back for this row
                    successful = true;
                    currentRow++;
                } else {
                    fileWriter.write(line);
                    fileWriter.newLine();
                    currentRow++;
                }
            }
            fileWriter.flush();
        } catch (IOException e) {
            if (editing) {
                Log.e(Constants.LOG_TAG, "Error editing row.", e);
            } else {
                Log.e(Constants.LOG_TAG, "Error deleting row.", e);
            }
        }

        successful = successful && TMP_FILE.renameTo(Constants.MEMORIES_FILE);

        if (editing) {
            Log.d(Constants.LOG_TAG, "Row <" + rowToChange + "> edited successfully? " + successful);
        } else {
            Log.d(Constants.LOG_TAG, "Row <" + rowToChange + "> deleted successfully? " + successful);
        }
    }

    static void deleteRow(int rowToDelete) {
        editRow(rowToDelete, "");
    }

    @SuppressWarnings("unused")
    public static void updateFile(Context ctx, String fileName) {
        Log.d(Constants.LOG_TAG, "Updating " + fileName);
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(ctx.openFileInput(fileName)));
             BufferedWriter fileWriter = new BufferedWriter(new OutputStreamWriter(ctx.openFileOutput(TMP_FILE_NAME, Context.MODE_PRIVATE)))) {
            String line;
            while ((line = fileReader.readLine()) != null) {
                // set update operation here
                Log.d(Constants.LOG_TAG, line);
                fileWriter.write(line.replaceAll("<\\*>", "<~>"));
                fileWriter.newLine();
            }
            fileWriter.flush();
        } catch (java.io.IOException e) {
            Log.e(Constants.LOG_TAG, "Error updating file", e);
        }
        boolean successful = ctx.getFileStreamPath(TMP_FILE_NAME).renameTo(ctx.getFileStreamPath(fileName));
        Log.d(Constants.LOG_TAG, fileName + " updated successfully? " + successful);
    }
}
