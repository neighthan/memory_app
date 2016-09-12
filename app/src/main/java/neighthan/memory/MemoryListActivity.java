package neighthan.memory;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * An activity representing a list of Memories. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MemoryDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MemoryListActivity extends AppCompatActivity {

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

        View recyclerView = findViewById(R.id.memory_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.memory_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts (res/values-w900dp).
            // If this view is present, then the activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new MemoryRecyclerViewAdapter(Memory.getAllMemories(this, Constants.MEMORIES_FILE_NAME)));
    }

    public class MemoryRecyclerViewAdapter extends RecyclerView.Adapter<MemoryRecyclerViewAdapter.ViewHolder> {

        private final List<Memory> memories;

        public MemoryRecyclerViewAdapter(List<Memory> memories) {
            this.memories = memories;
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
            // show only the date (not time of day) and truncate tags and memory text
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

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public Memory memory;

            public ViewHolder(View view) {
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
