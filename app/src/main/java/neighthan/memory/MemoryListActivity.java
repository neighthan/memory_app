package neighthan.memory;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * An activity representing a list of Memories. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MemoryDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MemoryListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private static final int MEMORY_TEXT_LENGTH = 55; // todo set this based on the screen width
    private static final int TAGS_TEXT_LENGTH = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_list);

//        FileUtils.updateFile(this, MEMORIES_FILE_NAME); // todo how to have a flag to run this only once,
        // then not again until another update where it is needed? Can write the flag to a local file,
        // check it each time the app is loaded; if true, update then set flag to false. But how do
        // we set it to true each time a new "version" is released where an update is required?

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.memory_list);
        assert recyclerView != null;
        setupRecyclerView(recyclerView);

        if (findViewById(R.id.memory_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts (res/values-w900dp).
            // If this view is present, then the activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.memory_list, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    /**
     * This is called every time that the text in the search bar changes; thus we will get updates
     * while typing to only show the matching memories. When one is done searching, the query is
     * the empty String at the last change, so then all memories match and are added back again.
     */
    public boolean onQueryTextChange(String query) {
        query = query.toLowerCase();
        final List<Memory> filteredMemories;
        if (query.startsWith(Constants.QUERY_TAG_PREFIX)) {
            query = query.substring(Constants.QUERY_TAG_PREFIX.length());
            String[] tags = query.split(Memory.TAG_DELIM);
            if (tags.length == 0) {
                filteredMemories = new ArrayList<>(0);
            } else {
                List<String> tagsList = new ArrayList<>(tags.length);
                for (String tag : tags) {
                    tag = tag.trim();
                    if (!tag.isEmpty()) {tagsList.add(tag); }
                }
                filteredMemories = filterTags(Memory.getAllMemories(), tagsList);
            }
        } else {
            filteredMemories = filter(Memory.getAllMemories(), query);
        }
        Memory.updateVisibleMemories(filteredMemories);
        ((RecyclerView) findViewById(R.id.memory_list)).scrollToPosition(0);
        // so you can see the memories better while searching
        return true;
    }

    private List<Memory> filter(List<Memory> memories, String query) {
        final List<Memory> filteredMemories = new ArrayList<>();
        for (Memory memory : memories) {
            if (memory.toString().toLowerCase().contains(query)) {
                filteredMemories.add(memory);
            }
        }
        return filteredMemories;
    }

    private List<Memory> filterTags(List<Memory> memories, List<String> tags) {
        final List<Memory> filteredMemories = new ArrayList<>();
        for (Memory memory : memories) {
            Log.d(Constants.LOG_TAG, "mem queryTags: " + memory.queryTags());
            Log.d(Constants.LOG_TAG, "query tags: " + tags);
            if (memory.queryTags().containsAll(tags)) {
                filteredMemories.add(memory);
            }
        }
        return filteredMemories;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                navigateUpTo(new Intent(this, MemoryListActivity.class));
                return true;

            case R.id.action_export:
                if (! Constants.EXTERNAL_STORAGE_WRITABLE) {
                    Snackbar.make(findViewById(R.id.memory_list),
                            "You must have external storage to export the memories file", Snackbar.LENGTH_LONG).show();
                    return true;
                }

                Intent emailIntent = new Intent(Intent.ACTION_SEND)
                        .setType("message/rfc822")
                        .putExtra(Intent.EXTRA_STREAM, Uri.fromFile(Constants.MEMORIES_FILE));
                startActivity(Intent.createChooser(emailIntent, "Export memories as csv attachment"));
                return true;

            default:
                Log.d(Constants.LOG_TAG, "Unknown menu item was clicked in AddMemory (did you " +
                        "forget to add an id?)");
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new MemoryRecyclerViewAdapter());
    }

    public class MemoryRecyclerViewAdapter extends RecyclerView.Adapter<MemoryRecyclerViewAdapter.ViewHolder> {

        private final Comparator<Memory> DATE_ORDER = new Comparator<Memory>() {
            @Override
            public int compare(Memory mem1, Memory mem2) {
                return mem1.date().compareTo(mem2.date());
            }
        };

        /* The sorted list is very useful with a recycler view; the callback will handle all of the
        * notifications to the recycler view (to update the items shown). This works very well with
        * using a search view too! You can update based on the query text.
        */
        private final SortedList<Memory> memories = new SortedList<>(Memory.class, new SortedList.Callback<Memory>() {
            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }

            @Override
            public int compare(Memory mem1, Memory mem2) {
                return DATE_ORDER.compare(mem1, mem2);
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            /**
             * This method is used to determine if two items represent the same memory.
             * A sorted list will not add an item if it is already in the list (as determined by
             * this method). Instead, it will check if the contents of the two items are the same
             * (as determined by the below method). If not, it will update the list to contain the
             * new item instead of the old one.
             * Thus for editing a memory, you don't have to remove the old one and add the new one.
             * Instead, add the new one, and the sorted list will see they are the same and update.
             */
            public boolean areItemsTheSame(Memory mem1, Memory mem2) {
                return mem1.id() == mem2.id();
            }

            @Override
            public boolean areContentsTheSame(Memory oldMem, Memory newMem) {
                return oldMem.equals(newMem);
            }
        });

        MemoryRecyclerViewAdapter() {
            Memory.setMemoriesList(memories); // so updates can be done through Memory's methods
            Memory.addMemoriesFromFile();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.memory_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.memory = memories.get(position);
            // show only the date (not time of day) and truncate displayTags and memory text
            holder.mIdView.setText(holder.memory.dateString().split(" ")[0] + "\n" +
                    holder.memory.tagsString().substring(0, Math.min(TAGS_TEXT_LENGTH, holder.memory.tagsString().length())));
            holder.mContentView.setText(holder.memory.text()
                    .substring(0, Math.min(MEMORY_TEXT_LENGTH, holder.memory.text().length())));

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putInt(MemoryDetailFragment.ARG_ITEM_ID, holder.memory.id());
                        MemoryDetailFragment fragment = new MemoryDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.memory_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, MemoryDetailActivity.class);
                        intent.putExtra(MemoryDetailFragment.ARG_ITEM_ID, holder.memory.id());

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return memories.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final View mView;
            final TextView mIdView;
            final TextView mContentView;
            public Memory memory;

            ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }

    public void addMemory(View view) {
        final Intent intent = new Intent(this, AddMemory.class);
        startActivity(intent);
    }
}
