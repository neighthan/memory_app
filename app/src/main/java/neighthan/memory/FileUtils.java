package neighthan.memory;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 *
 */
public class FileUtils {
    private static final String TMP_FILE_NAME = "tmpFile.csv";
    // todo make this work with local file too
    private static final File TMP_FILE = new File(Environment.getExternalStorageDirectory(), TMP_FILE_NAME);

    public static void deleteRow(Context ctx, String toDelete) {
        boolean successful = false;
        Log.d(Constants.LOG_TAG, "Deleting <" + toDelete + "> from " + Constants.MEMORIES_FILE_NAME);
        try (BufferedReader fileReader = new BufferedReader(new FileReader(Constants.MEMORIES_FILE));
             BufferedWriter fileWriter = new BufferedWriter(new FileWriter(TMP_FILE))) {
            String line;
            while ((line = fileReader.readLine()) != null) {
                if (line.equals(toDelete)) {
//                    Log.d(Constants.LOG_TAG, "Deleting: " + line);
                    successful = true;
                    continue;
                }
//                Log.d(Constants.LOG_TAG, line);

                fileWriter.write(line);
                fileWriter.newLine();
            }
            fileWriter.flush();
        } catch (java.io.IOException e) {
            Log.e(Constants.LOG_TAG, "Error deleting row.", e);
        }
        
        successful = successful && TMP_FILE.renameTo(Constants.MEMORIES_FILE);
        Log.d(Constants.LOG_TAG, "<" + toDelete + "> deleted successfully? " + successful);
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
