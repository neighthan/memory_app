package neighthan.memory;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A fragment representing a single Memory detail screen.
 * This fragment is either contained in a {@link MemoryListActivity}
 * in two-pane mode (on tablets) or a {@link MemoryDetailActivity}
 * on handsets.
 */
public class MemoryDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    private Memory memory;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MemoryDetailFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            memory = Memory.getMemory(getArguments().getInt(ARG_ITEM_ID));

            Activity activity = this.getActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.memory_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (memory != null) {
            ((TextView) rootView.findViewById(R.id.memory_detail)).setText(memory.text());
        }

        return rootView;
    }
}
