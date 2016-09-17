package neighthan.memory;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/*
TODO : Features
  - Add searching
  https://developer.android.com/guide/topics/search/search-dialog.html
  - sort memories by date
  - Add exporting / sharing of memories file (partially done)
  - show tags somewhere in detail view? maybe showable from a menu button?



  - Keep a local backup file (sync on, e.g., app opening); restore from this if there's an error

TODO : BUGS
   - creating a memory, editing it, then leaving the app causes the edited memory to disappear
       (it isn't being written to the file but the old memory is being deleted)
   - need to get target sdk back to 24 (have to figure out how to request storage permission)
   - Adding a memory no longer works without external storage (set the value to false and try)
   - Rotating in a detail view just shows the title "Memory Detail" instead of the date
 */


/**
 *
 */
public class Memory {
    public static final String DELIM = "<~>";
    public static final String TAG_DELIM = ",";
    public static final String DATE_EXTRA = "dateExtra";
    public static final String TAGS_EXTRA = "tagsExtra";
    public static final String TEXT_EXTRA = "textExtra";
    public static final String ID_EXTA = "idExtra";
    public static final String DUMMY_NEWLINE = "%newline%";
    public static final SimpleDateFormat DF = new SimpleDateFormat("M-d-yy h:mm a", Locale.US);

    private static int nextId;

    private static List<Memory> memories;

    private Date date;
    private List<String> tags;
    private String text;
    private int id;

    public static List<Memory> getAllMemories(Context ctx, String fileName) {
        if (memories != null) { return memories; }

        memories = new ArrayList<>();

        try(BufferedReader memoryReader = new BufferedReader(new FileReader(Constants.MEMORIES_FILE))) {
            String line;
            int i = 0;
            while ((line = memoryReader.readLine()) != null) {
                Log.d(Constants.LOG_TAG, "Loading memory " + i + ": " + line);
                String[] splits = line.split(DELIM);
                Memory memory = new Memory(splits[0], splits[1], splits[2], i++);
                memories.add(memory);
            }
            nextId = i;
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(Constants.LOG_TAG, "Finished loading memories.");
        return Collections.unmodifiableList(memories);
    }

    public static void removeMemory(int memoryId) {
        memories.remove(memoryId);
        // update IDs of all memories after this one to be their new list index
        for (int i = memoryId; i < memories.size(); i++) {
            memories.get(i).id = i;
        }
    }

    public static void addMemory(Memory memory) {
        memories.add(memory);
    }

    public static Memory getMemory(int id) {
        return memories.get(id);
    }

    public Memory(String memoryString) {
        this(memoryString.split(DELIM), nextId++);
    }

    public Memory(String[] fields, int id) {
        this(fields[0], fields[1], fields[2], id);
    }

    public Memory(String date, String tags, String text, int id) {
        this.tags = new ArrayList<>(2);
        for (String tag : tags.split(TAG_DELIM)) {
            if (! this.tags.contains(tag)) {
                this.tags.add(tag);
            }
        }
        this.text = text.replace(DUMMY_NEWLINE, "\n");
        this.id = id;
        try {
            this.date = DF.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Date date() {
        return date;
    }

    public String dateString() {
        return DF.format(date);
    }

    public List<String> tags() {
        return tags;
    }

    public String tagsString() {
        StringBuilder tagsString = new StringBuilder();
        for (String tag : tags) {
            tagsString.append(tag).append(TAG_DELIM);
        }
        return tagsString.substring(0, tagsString.length() - TAG_DELIM.length());
    }

    public String text() {
        return text;
    }

    public int id() {
        return id;
    }

    @Override
    public String toString() {
        return dateString() + Memory.DELIM + tagsString() + Memory.DELIM + text;
    }
}
