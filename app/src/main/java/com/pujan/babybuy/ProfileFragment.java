package com.pujan.babybuy;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.concurrent.Executor;

public class ProfileFragment extends Fragment {
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    List<UserClass> dataList;
    TextView name, phone, email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        name = view.findViewById(R.id.fullname);
        email = view.findViewById(R.id.email);
        phone = view.findViewById(R.id.phone);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        String userid = auth.getCurrentUser().getUid();
        // fetch data from firestore and set on textview
       firestore.collection("userInfo")
               .whereEqualTo("userid",userid)
               .get()
               .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                                for (QueryDocumentSnapshot document : task.getResult()){
                                    name.setText(document.getString("name").toString());
                                    email.setText(document.getString("email").toString());
                                    phone.setText(document.getString("phone").toString());
                                }
                        }
                   }
               });
        return view;

    }

}

