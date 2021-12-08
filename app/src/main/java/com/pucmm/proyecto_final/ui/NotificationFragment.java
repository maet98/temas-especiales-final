package com.pucmm.proyecto_final.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.pucmm.proyecto_final.R;
import com.pucmm.proyecto_final.databinding.FragmentNotificationBinding;
import com.pucmm.proyecto_final.utils.Session;

import java.util.ArrayList;

public class NotificationFragment extends Fragment {

    private FragmentNotificationBinding binding;

    public NotificationFragment() {
    }

    public static NotificationFragment newInstance() {
        NotificationFragment fragment = new NotificationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotificationBinding.inflate(inflater, container,false);
        View root = binding.getRoot();
        Session session = new Session(getContext());
        ArrayList<String> notifications = session.getNotifications();
        System.out.println(notifications.size());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, notifications);
        binding.notifications.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                adapter.notifyDataSetChanged();
            }
        });
        binding.notifications.setAdapter(adapter);
        return root;
    }
}