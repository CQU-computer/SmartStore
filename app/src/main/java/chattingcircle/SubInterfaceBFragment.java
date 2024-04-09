package chattingcircle;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.smartstore.R;

public class SubInterfaceBFragment extends Fragment {

    private ListView listViewB;
    private String[] itemsB = {" ", " ", " ", " ", " "," "," "};

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sub_interface_b, container, false);

        listViewB = rootView.findViewById(R.id.list_view_b);
        setupListViewB();

        return rootView;
    }

    private void setupListViewB() {

        ArrayAdapter<String> adapterB = new ArrayAdapter<>(requireContext(), R.layout.chatting_my_content, R.id.chatting_my_content, itemsB);
        listViewB.setAdapter(adapterB);
    }
}