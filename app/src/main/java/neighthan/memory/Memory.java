package neighthan.memory;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 *
 */
public class Memory {
    public static final String DELIM = "<*>";
    public static final String TAG_DELIM = ",";
    public static final String DUMMY_NEWLINE = "%newline%";
    public static final SimpleDateFormat DF = new SimpleDateFormat("M-d h:mm a", Locale.US);

    public static List<Memory> memories;

    private Date date;
    private Set<String> tags;
    private String text;
    public String id;

    public static List<Memory> getAllMemories(Context ctx, String fileName) {
        memories = new ArrayList<>();
        try(BufferedReader memoryReader = new BufferedReader(new InputStreamReader(ctx.openFileInput(fileName)))) {
            String line;
            int i = 0;
            while ((line = memoryReader.readLine()) != null) {
                Log.d(MemoryListActivity.APP_TAG, "Loading memory " + i + ": " + line);
                String[] splits = line.split(DELIM);
                Memory memory = new Memory(splits[0], splits[1], splits[2], String.valueOf(i++));
                memories.add(memory);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(MemoryListActivity.APP_TAG, "Finished loading memories.");
        return memories;
    }

    public Memory(String date, String tags, String text, String id) {
        this.tags = new HashSet<>();
        Collections.addAll(this.tags, tags.split(TAG_DELIM));
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

    public Set<String> tags() {
        return tags;
    }

    public String text() {
        return text;
    }

    @Override
    public String toString() {
        return text;
    }
}
