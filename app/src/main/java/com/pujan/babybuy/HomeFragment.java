package com.pujan.babybuy;

import android.annotation.SuppressLint;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private FloatingActionButton fab;
    RecyclerView recyclerView;
    List<DataClass> dataList;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    FirebaseAuth auth;
    FirebaseFirestore firestore;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        fab = view.findViewById(R.id.floatingActionButton);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
     //   getFragmentManager().beginTransaction().detach(this).attach(this).commit();

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
        // retrive added data from firebase
        firestore.collection("data")
                .whereEqualTo("userId", userUid).whereEqualTo("ispurchased",false)//not purchased for landing
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                dataList.clear();
                for (DocumentChange dc : value.getDocumentChanges()) {
                    dataList.add(dc.getDocument().toObject(DataClass.class));
                    adapter.notifyDataSetChanged();
                }
                dialog.dismiss();
            }

            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }
        });

//        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                dataList.clear();
//                for (DataSnapshot itemSnapshot: snapshot.getChildren()){
//                    DataClass dataClass = itemSnapshot.getValue(DataClass.class);
//                    dataClass.setKey(itemSnapshot.getKey());
//                    dataList.add(dataClass);
//                }
//                adapter.notifyDataSetChanged();
//                dialog.dismiss();
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                dialog.dismiss();
//            }
//        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddProductActivity.class)); // redirected to addproduct page when floating action buttion clicked
            }

        });
    }


}