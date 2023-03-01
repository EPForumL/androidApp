package com.github.romainlogean.sdp_bootcamp;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class MainFragment extends Fragment {

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void greet(View view){
        Intent intent = new Intent(view.getContext(), GreetingActivity.class);
        TextView nameTV = getActivity().findViewById(R.id.mainName);
        String name = nameTV.getText().toString();
        intent.putExtra("NAME", name);
        view.getContext().startActivity(intent);
    }

    public void map(View view) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fc, MapsFragment.newInstance())
                .commitNow();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        Button button = view.findViewById(R.id.mainGoButton);
        button.setOnClickListener(this::greet);
        Button button2 = view.findViewById(R.id.mapB);
        button2.setOnClickListener(this::map);
        return view;
    }
}