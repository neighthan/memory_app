package neighthan.memory;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.util.Date;

public class AddMemory extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_memory);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent(); // with which this activity was started
        if (intent.hasExtra(Memory.DATE_EXTRA)) { // started from edit memory button
            getSupportActionBar().setTitle("Edit Memory");

            EditText dateText = (EditText) findViewById(R.id.dateText);
            EditText memoryText = (EditText) findViewById(R.id.memoryText);
            EditText tagsText = (EditText) findViewById(R.id.tagsText);

            dateText.setText(intent.getStringExtra(Memory.DATE_EXTRA));
            memoryText.setText(intent.getStringExtra(Memory.TEXT_EXTRA));
            tagsText.setText(intent.getStringExtra(Memory.TAGS_EXTRA));
            // todo delete old memory when you create a new one if you were editing
            // (probably change the function when you press "Create"; so don't set it in the layout,
            // get the button and set the onClick function programmatically
        } else {
            getSupportActionBar().setTitle("Add Memory");

            EditText dateText = (EditText) findViewById(R.id.dateText);
            dateText.setText(Memory.FILE_DF.format(new Date()));
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
            Memory.FILE_DF.parse(dateString); // ensure valid date

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
            Memory.memories.add(memory);

            finish();
        } catch (java.io.IOException e) {
            Log.d(Constants.LOG_TAG, "Invalid date!");
            e.printStackTrace();
        } catch (ParseException e) {
            Snackbar.make(findViewById(R.id.addMemoryLayout), R.string.dateError, Snackbar.LENGTH_LONG).show();
            // todo make a more helpful dateError message (show format acceptable)
        } catch (IllegalArgumentException e) {
            Snackbar.make(findViewById(R.id.addMemoryLayout), R.string.emptyMemoryError, Snackbar.LENGTH_LONG).show();
        }
    }
}
