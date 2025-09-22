package my.edu.utar.utarit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import my.edu.utar.utarit.adapter.EventAdapter;
import my.edu.utar.utarit.model.Event;

public class EventFragment extends Fragment {

    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private List<Event> events = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);

        recyclerView = view.findViewById(R.id.recyclerView_events);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new EventAdapter(getContext(), events);
        recyclerView.setAdapter(adapter);

        loadDummyEvents();
        return view;
    }

    private void loadDummyEvents() {

        events.add(new Event("1", "Tech Talk", "AI and Future Tech", "2025-10-01", "UTAR Kampar"));
        events.add(new Event("2", "Coding Workshop", "Android Development", "2025-10-05", "UTAR Kampar Lab"));
        events.add(new Event("3", "Sports Day", "Football & Badminton", "2025-10-12", "UTAR Field"));

        adapter.notifyDataSetChanged();
    }
}
