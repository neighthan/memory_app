package neighthan.memory;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 *
 */
public class FileUtils {
    private static final String TMP_FILE_NAME = "tmpFile.csv";

    public static void deleteRow(Context ctx, String fileName, String toDelete) {
        Log.d(Constants.LOG_TAG, "Deleting " + toDelete + " from " + fileName);
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(ctx.openFileInput(fileName)));
             BufferedWriter fileWriter = new BufferedWriter(new OutputStreamWriter(ctx.openFileOutput(TMP_FILE_NAME, Context.MODE_PRIVATE)))) {
            String line;
            while ((line = fileReader.readLine()) != null) {
                Log.d(Constants.LOG_TAG, line);
                if (line.equals(toDelete)) { continue; }

                fileWriter.write(line);
                fileWriter.newLine();
            }
            fileWriter.flush();
        } catch (java.io.IOException e) {
            Log.e(Constants.LOG_TAG, "Error deleting row.", e);
        }
        boolean successful = ctx.getFileStreamPath(TMP_FILE_NAME).renameTo(ctx.getFileStreamPath(fileName));
        Log.d(Constants.LOG_TAG, toDelete + " deleted successfully? " + successful);
    }

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
