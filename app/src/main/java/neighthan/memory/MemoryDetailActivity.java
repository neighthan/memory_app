package neighthan.memory;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

/**
 * An activity representing a single Memory detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link MemoryListActivity}.
 */
public class MemoryDetailActivity extends AppCompatActivity {

    private int memoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            memoryId = getIntent().getIntExtra(MemoryDetailFragment.ARG_ITEM_ID, -1);
            Bundle arguments = new Bundle();
            arguments.putInt(MemoryDetailFragment.ARG_ITEM_ID, memoryId);
            MemoryDetailFragment fragment = new MemoryDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.memory_detail_container, fragment)
                    .commit();
        }
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
            case R.id.action_delete:
                FileUtils.deleteRow(this, Constants.MEMORIES_FILE_NAME, Memory.memories.get(memoryId).toString());
                Memory.memories.remove(memoryId);
                navigateUpTo(new Intent(this, MemoryListActivity.class));
                return true;
            default:
                Log.d(Constants.LOG_TAG, "Unknown menu item was clicked in AddMemory (did you " +
                        "forget to add an id?)");
                return super.onOptionsItemSelected(item);
        }
    }

    public void editMemory(View view) {
        Intent intent = new Intent(this, AddMemory.class);
        Memory memory = Memory.memories.get(memoryId);
        intent.putExtra(Memory.DATE_EXTRA, memory.dateString());
        intent.putExtra(Memory.TAGS_EXTRA, memory.tagsString());
        intent.putExtra(Memory.TEXT_EXTRA, memory.text());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.memory_detail, menu);
        return true;
    }
}
