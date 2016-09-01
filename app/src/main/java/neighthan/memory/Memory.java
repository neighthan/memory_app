package neighthan.memory;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/*
TODO
  - Editing should remove old memory and replace with new one (instead of having both)
    - should also return you to the master screen not the detail screen?
  - Add confirmation before deleting
   (- Make confirmation turn-off-able in settings)
  - test multi-line memory
  - Add searching
  - Add exporting / sharing of memories file



  - Keep a local backup file (sync on, e.g., app opening); restore from this if there's an error
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
    public static final String DUMMY_NEWLINE = "%newline%";
    public static final SimpleDateFormat FILE_DF = new SimpleDateFormat("M-d h:mm a", Locale.US); // todo
    public static final SimpleDateFormat DISPLAY_DF = new SimpleDateFormat("M-d h:mm a", Locale.US);

    private static int nextId;

    public static List<Memory> memories;

    private Date date;
    private List<String> tags;
    private String text;
    public int id;

    public static List<Memory> getAllMemories(Context ctx, String fileName) {
        if (memories != null) { return memories; }

        memories = new ArrayList<>();
        try(BufferedReader memoryReader = new BufferedReader(new InputStreamReader(ctx.openFileInput(fileName)))) {
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
        return memories;
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
            this.date = FILE_DF.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Date date() {
        return date;
    }

    public String dateString() {
        return FILE_DF.format(date);
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

    @Override
    public String toString() {
        return dateString() + Memory.DELIM + tagsString() + Memory.DELIM + text;
    }
}
