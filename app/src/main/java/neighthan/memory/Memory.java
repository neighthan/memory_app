package neighthan.memory;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class Memory {
    public static final String DELIM = "<*>";
    public static final String TAG_DELIM = ",";

    private Date date;
    private Set<String> tags;
    private String text;

    public Memory(String tags, String text) {
        this.tags = new HashSet<>();
        Collections.addAll(this.tags, tags.split(TAG_DELIM));
        this.text = text;
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
