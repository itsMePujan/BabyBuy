package com.pujan.babybuy;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class SettingsFragment extends Fragment {
    RecyclerView recyclerView;
    List<DataClass> dataList;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    FloatingActionButton fab;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_settings, container, false);
        auth = FirebaseAuth.getInstance();
        fab = view.findViewById(R.id.floatingActionButton);
        firestore = FirebaseFirestore.getInstance();
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();
        dataList = new ArrayList<>();
        MyAdapter adapter = new MyAdapter(getActivity(), dataList);
        recyclerView.setAdapter(adapter);
        String userUid = auth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("BabyBuy");
        dialog.show();

   // fetch data from firestore where ispurchased is true ( ITEM PURCHASED

        firestore.collection("data").whereEqualTo("userId", userUid).whereEqualTo("ispurchased",true
        ).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                dataList.clear();
                for (DocumentChange dc : value.getDocumentChanges()) {
                    dataList.add(dc.getDocument().toObject(DataClass.class));
                }
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }
        });

/// using firebase realtime database

        fab.setOnClickListener(new View.OnClickListener() { // navigate to product add page when floating action button clicked
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddProductActivity.class));
            }

        });

    }

}