package neighthan.memory;

import android.os.Environment;

import java.io.File;

/**
 *
 */
public class Constants {
    public static final String MEMORIES_FILE_NAME = "memories.csv";
    public static final String LOG_TAG = "MemoryApp";
    public static final boolean EXTERNAL_STORAGE_WRITABLE =
            Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    public static final File MEMORIES_FILE = EXTERNAL_STORAGE_WRITABLE ?
            new File(Environment.getExternalStorageDirectory(), MEMORIES_FILE_NAME) :
            new File(Environment.getDataDirectory(), MEMORIES_FILE_NAME);

}
