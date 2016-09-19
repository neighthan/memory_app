package neighthan.memory;

import android.os.Environment;

import java.io.File;

/**
 *
 */
class Constants {
    static final String MEMORIES_FILE_NAME = "memories.csv";
    static final String LOG_TAG = "MemoryApp";
    static final boolean EXTERNAL_STORAGE_WRITABLE =
            Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    static final File MEMORIES_FILE = EXTERNAL_STORAGE_WRITABLE ?
            new File(Environment.getExternalStorageDirectory(), MEMORIES_FILE_NAME) :
            new File(Environment.getDataDirectory(), MEMORIES_FILE_NAME);
    static final String QUERY_TAG_PREFIX = "tag:";
}
