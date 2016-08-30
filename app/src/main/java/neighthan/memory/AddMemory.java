package neighthan.memory;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.EditText;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
        getSupportActionBar().setTitle("Add Memory");

        EditText dateText = (EditText) findViewById(R.id.dateText);
        dateText.setText(Memory.DF.format(new Date()));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void createMemory(View view) {
        try(OutputStreamWriter toMemoriesFile = new OutputStreamWriter(
                openFileOutput(MemoryListActivity.MEMORIES_FILE_NAME, MODE_APPEND))) {
            EditText dateText = (EditText) findViewById(R.id.dateText);
            EditText tagsText = (EditText) findViewById(R.id.tagsText);
            EditText memoryText = (EditText) findViewById(R.id.memoryText);

            // Process input texts to ensure all are valid
            String dateString = dateText.getText().toString().trim();
            Log.d(MemoryListActivity.APP_TAG, dateString);
            Memory.DF.parse(dateString); // ensure valid date

            String tagsString = tagsText.getText().toString().trim().replace("\n", Memory.TAG_DELIM).replace(Memory.DELIM, "");

            // Each line denotes a separate memory, so one memory must all be contained in a single line
            // Replace newlines with a dummy sequence that can then be swapped out once the memory is
            // read from the file
            String memoryString = memoryText.getText().toString().trim().replace(Memory.DELIM, "").replace("\n", Memory.DUMMY_NEWLINE);

            if (memoryString.length() == 0) {
                throw new IllegalArgumentException();
            }

            String memory = dateString + Memory.DELIM + tagsString + Memory.DELIM + memoryString + "\n";
            toMemoriesFile.write(memory);
            toMemoriesFile.flush();
            Log.d(MemoryListActivity.APP_TAG, "Wrote memory: " + memory);

            Intent intent = new Intent(this, MemoryListActivity.class);
            startActivity(intent); // is there a better way than this? Just go back?
        } catch (java.io.IOException e) {
            Log.d(MemoryListActivity.APP_TAG, "Invalid date!");
            e.printStackTrace();
        } catch (ParseException e) {
            Snackbar.make(findViewById(R.id.addMemoryLayout), R.string.dateError, Snackbar.LENGTH_LONG).show();
            // todo make a more helpful dateError message (show format acceptable)
        } catch (IllegalArgumentException e) {
            Snackbar.make(findViewById(R.id.addMemoryLayout), R.string.emptyMemoryError, Snackbar.LENGTH_LONG).show();
        }
    }


}
