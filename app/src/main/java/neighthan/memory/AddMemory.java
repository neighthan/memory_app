package neighthan.memory;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.util.Date;

public class AddMemory extends AppCompatActivity {
    private static final String EDIT_ACTION_BAR_TITLE = "Edit Memory";
    private static final String ADD_ACTION_BAR_TITLE = "Add Memory";
    private static int editedMemoryId = -1;
    // not sure of a better way to save this information between onCreate and createMemory

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_memory);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent(); // with which this activity was started
        if (intent.hasExtra(Memory.DATE_EXTRA)) { // started from edit memory button
            getSupportActionBar().setTitle(EDIT_ACTION_BAR_TITLE);
            ((Button) findViewById(R.id.createMemoryButton)).setText(R.string.edit_memory_button);

            EditText dateText = (EditText) findViewById(R.id.dateText);
            EditText memoryText = (EditText) findViewById(R.id.memoryText);
            EditText tagsText = (EditText) findViewById(R.id.tagsText);

            dateText.setText(intent.getStringExtra(Memory.DATE_EXTRA));
            memoryText.setText(intent.getStringExtra(Memory.TEXT_EXTRA));
            tagsText.setText(intent.getStringExtra(Memory.TAGS_EXTRA));
            editedMemoryId = intent.getIntExtra(Memory.ID_EXTA, -1);
        } else {
            getSupportActionBar().setTitle(ADD_ACTION_BAR_TITLE);
            ((Button) findViewById(R.id.createMemoryButton)).setText(R.string.create_memory_button);
            EditText dateText = (EditText) findViewById(R.id.dateText);
            dateText.setText(Memory.DF.format(new Date()));
        }


    }

    public void createMemory(View view) {
        try(OutputStreamWriter toMemoriesFile = new OutputStreamWriter(
                openFileOutput(Constants.MEMORIES_FILE_NAME, MODE_APPEND))) {
            EditText dateText = (EditText) findViewById(R.id.dateText);
            EditText tagsText = (EditText) findViewById(R.id.tagsText);
            EditText memoryText = (EditText) findViewById(R.id.memoryText);

            // Process input texts to ensure all are valid
            String dateString = dateText.getText().toString().trim();
            Log.d(Constants.LOG_TAG, dateString);
            Memory.DF.parse(dateString); // ensure valid date

            String tagsString = tagsText.getText().toString().trim().replace("\n", Memory.TAG_DELIM).replace(Memory.DELIM, "");

            // Each line denotes a separate memory, so one memory must all be contained in a single line
            // Replace newlines with a dummy sequence that can then be swapped out once the memory is
            // read from the file
            String memoryString = memoryText.getText().toString().trim().replace(Memory.DELIM, "").replace("\n", Memory.DUMMY_NEWLINE);
            if (memoryString.length() == 0) {
                throw new IllegalArgumentException();
            }

            memoryString = dateString + Memory.DELIM + tagsString + Memory.DELIM + memoryString;
            toMemoriesFile.write(memoryString + "\n");

            Memory memory = new Memory(memoryString);
            Log.d(Constants.LOG_TAG, "Created memory: " + memory);
            Memory.addMemory(memory);

            if (getSupportActionBar().getTitle().equals(EDIT_ACTION_BAR_TITLE)) {
                FileUtils.deleteRow(this, Constants.MEMORIES_FILE_NAME, Memory.getMemory(editedMemoryId).toString());
                Memory.removeMemory(editedMemoryId);
            }

            navigateUpTo(new Intent(this, MemoryListActivity.class));
        } catch (java.io.IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            Snackbar.make(findViewById(R.id.addMemoryLayout), R.string.dateError, Snackbar.LENGTH_LONG).show();
        } catch (IllegalArgumentException e) {
            Snackbar.make(findViewById(R.id.addMemoryLayout), R.string.emptyMemoryError, Snackbar.LENGTH_LONG).show();
        }
    }
}
