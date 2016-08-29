package neighthan.memory;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 */
public class Memory {
    public static final String DELIM = "<*>";
    public static final String TAG_DELIM = ",";

    public static List<Memory> memories;

    private Date date;
    private Set<String> tags;
    private String text;
    public String id;

    public static List<Memory> getAllMemories(Context ctx, String fileName) {
        memories = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(ctx.getAssets().open(fileName)));
            String line;
            int i = 0;
            while ((line = br.readLine()) != null) {
                String[] splits = line.split(DELIM);
                Memory memory = new Memory(splits[0], splits[1], String.valueOf(i++));
                memories.add(memory);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return memories;
    }

    public Memory(String tags, String text, String id) {
        this.date = new Date();
        this.tags = new HashSet<>();
        Collections.addAll(this.tags, tags.split(TAG_DELIM));
        this.text = text;
        this.id = id;
    }

    public Date date() {
        return date;
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
