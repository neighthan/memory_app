package neighthan.memory;

import android.support.v7.util.SortedList;
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
  - voice recording
  - show displayTags somewhere in detail view? maybe showable from a menu button?
  - settings menu ( for what settings? )
  - help menu (quick walk-through; especially things like tag: in the search bar)


  - use data binding : https://developer.android.com/topic/libraries/data-binding/index.html
  - Keep a local backup file (sync on, e.g., app opening); restore from this if there's an error

    - pictures?

TODO : BUGS
   - need to get target sdk back to 24 (have to figure out how to request storage permission)
             https://developer.android.com/training/permissions/requesting.html
   - Adding a memory no longer works without external storage (set the value to false and try)
 */


/**
 *
 */
class Memory {
    static final String DELIM = "<~>";
    static final String TAG_DELIM = ",";
    static final String DATE_EXTRA = "dateExtra";
    static final String TAGS_EXTRA = "tagsExtra";
    static final String TEXT_EXTRA = "textExtra";
    static final String ID_EXTA = "idExtra";
    static final String DUMMY_NEWLINE = "%newline%";
    static final SimpleDateFormat DF = new SimpleDateFormat("M-d-yy h:mm a", Locale.US);

    private static int nextId;

    private static SortedList<Memory> memories; // this will be used for searching; some memories will
    // be removed to fit the search, so we need a second list that always contains all of the memories

    private static List<Memory> allMemories; // maintain memory.id == allMemories.indexOf(memory)
    // and memory.id == row in memories file containing this memory

    private Date date;
    private List<String> displayTags; // displayed just as the user entered them (case-sensitive)
    private List<String> queryTags; // optimized for searching (all lowercase)
    private String displayTagsString;
    private String text;
    private int id;

    /**
     * Gives Memory a static reference to mems so that mems can be updated by the static methods
     * of Memory.
     * @param mems empty sorted list where memories for the RecyclerView should be stored
     */
    static void setMemoriesList(SortedList<Memory> mems) {
        memories = mems;
    }

    /**
     * @return an unmodifiable view of the list of all memories
     */
    static List<Memory> getAllMemories(){
        return Collections.unmodifiableList(allMemories);
    }

    /**
     * Reads all memories in from the file specified in Constants.
     * These memories are added to the list of all memories (returned by Memory.getAllMemories())
     * and to the SortedList which is shown by the RecyclerView.
     */
    static void addMemoriesFromFile() {
        if (allMemories != null) {
            memories.addAll(allMemories);
            return;
        }

        try(BufferedReader memoryReader = new BufferedReader(new FileReader(Constants.MEMORIES_FILE))) {
            allMemories = new ArrayList<>();
            String line;
            int i = 0;
            while ((line = memoryReader.readLine()) != null) {
                Log.d(Constants.LOG_TAG, "Loading memory " + i + ": " + line);
                String[] splits = line.split(DELIM);
                Memory memory = new Memory(splits[0], splits[1], splits[2], i++);
                allMemories.add(memory);
            }
            nextId = i;
            memories.addAll(allMemories);
            Log.d(Constants.LOG_TAG, "Finished loading memories.");
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, "Memories were not loaded from a file.", e);
        }
    }

    //        OutputStreamWriter toMemoriesFile = new OutputStreamWriter(
//                openFileOutput(Constants.MEMORIES_FILE_NAME, MODE_APPEND))
    /**
     * Creates a new memory: adds it to the file of memories, the list of all memories, and the
     * list of shown memories.
     * @param memoryString String representation of the Memory to be created
     */
    static void createMemory(String memoryString) {
        FileUtils.addRow(memoryString);
        final Memory memory = new Memory(memoryString);
        allMemories.add(memory);
        memories.add(memory);
    }

    static void editMemory(int id, String newMemoryString) {
        FileUtils.editRow(id, newMemoryString);
        allMemories.get(id).update(newMemoryString);
    }

    /**
     * Deletes a memory - removes it from the RecyclerView's SortedList and from the list of all
     * memories. It is also removed from the file where memories are saved.
     * @param id of the memory to be deleted
     */
    static void deleteMemory(int id) {
        FileUtils.deleteRow(id);

        Memory toDelete = allMemories.get(id);
        memories.remove(toDelete);
        allMemories.remove(toDelete);
        for (int i = id; i < allMemories.size(); i++) { // shift id's to still be the index
            allMemories.get(i).id = i;
        }
    }

    static void updateVisibleMemories(List<Memory> mems) {
        memories.beginBatchedUpdates();
        for (int i = memories.size() - 1; i >= 0; i--) {
            final Memory memory = memories.get(i);
            if (!mems.contains(memory)) {
                memories.remove(memory);
            }
        }
        memories.addAll(mems);
        memories.endBatchedUpdates();
    }

    static Memory getMemory(int id) {
        return allMemories.get(id);
    }

    private Memory(String memoryString) {
        this(memoryString, nextId++);
    }

    private Memory(String memoryString, int id) {
        this(memoryString.split(DELIM), id);
    }

    private Memory(String[] fields, int id) {
        this(fields[0], fields[1], fields[2], id);
    }

    private Memory(String date, String tags, String text, int id) {
        this.id = id;
        update(date, tags, text);
    }

    Date date() {
        return date;
    }

    String dateString() {
        return DF.format(date);
    }

    List<String> displayTags() {
        return displayTags;
    }

    List<String> queryTags() {
        return queryTags;
    }

    String tagsString() {
        if (displayTagsString != null) { return displayTagsString; }

        StringBuilder displayTagsStringBuilder = new StringBuilder();
        for (String tag : displayTags) {
            displayTagsStringBuilder.append(tag).append(TAG_DELIM);
        }
        displayTagsString = displayTagsStringBuilder.substring(0, displayTagsStringBuilder.length() - TAG_DELIM.length());
        return displayTagsString;
    }

    public String text() {
        return text;
    }

    public int id() {
        return id;
    }

    private void update(String newMemoryString) {
        String[] splits = newMemoryString.split(DELIM);
        update(splits[0], splits[1], splits[2]);
    }

    private void update(String date, String tags, String text) {
        this.displayTags = new ArrayList<>(2);
        this.queryTags = new ArrayList<>(2);
        for (String tag : tags.split(TAG_DELIM)) {
            tag = tag.trim();
            if (! this.displayTags.contains(tag)) {
                this.displayTags.add(tag);
                this.queryTags.add(tag.toLowerCase());
            }
        }
        this.text = text.replace(DUMMY_NEWLINE, "\n");
        try {
            this.date = DF.parse(date);
        } catch (ParseException e) {
            Log.e(Constants.LOG_TAG, "Error parsing date when creating or updating memory", e);
        }
    }

    @Override
    public String toString() {
        return dateString() + DELIM + tagsString() + DELIM + text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Memory memory = (Memory) o;

        return id == memory.id && date.equals(memory.date) && displayTags.equals(memory.displayTags) && text.equals(memory.text);

    }

    @Override
    public int hashCode() {
        int result = date.hashCode();
        result = 31 * result + displayTags.hashCode();
        result = 31 * result + text.hashCode();
        result = 31 * result + id;
        return result;
    }
}
