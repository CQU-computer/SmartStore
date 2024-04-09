package chattingcircle;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.smartstore.R;

public class SubInterfaceAFragment extends Fragment {

    private ListView listViewA;
    private String[] itemsA = {" ", " ", " ", " ", " "};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sub_interface_a, container, false);

        listViewA = rootView.findViewById(R.id.list_view_a);
        setupListViewA();

        return rootView;
    }

    private void setupListViewA() {

        ArrayAdapter<String> adapterA = new ArrayAdapter<>(requireContext(), R.layout.chatting_my_content, R.id.chatting_my_content, itemsA);
        listViewA.setAdapter(adapterA);
    }
}

