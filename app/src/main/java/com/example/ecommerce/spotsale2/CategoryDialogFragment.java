package com.example.ecommerce.spotsale2;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.List;

public class CategoryDialogFragment extends DialogFragment {

    private static ListAdapter adapter;
    private static List<String> categories;
    final CharSequence[] categories1 = {"1", "2", "3"};

    public CategoryDialogFragment(){
        FirebaseDatabase.getInstance().getReference().child("Categories")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String cat = (String) snapshot.getKey();
                           categories.add(cat);
                            //adapter.notify();
                        }

                    }

                    public void onCancelled( DatabaseError databaseError ) {

                        //Toast.makeText(CategoryActivity.this,"error",Toast.LENGTH_LONG).show();
                    }
                });

    }

    public CategoryDialogFragment(ListAdapter adapter, List categories) {
        this.adapter = adapter;
        this.categories = categories;
    }
    public static CategoryDialogFragment newInstance(String dialogMessage){
        CategoryDialogFragment fragment = new CategoryDialogFragment(adapter,categories);
        Bundle args = new Bundle();

        return fragment;
    }
/*AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle(studentNames.toArray
        (new String[studentNames.size()]),
        new DialogInterface.OnClickListener(){*/
//CharSequence[] cs = categories.toArray(new CharSequence[categories.size()]);

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Choose Category").setItems(categories1,new DialogInterface.OnClickListener(){
                //.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity(), categories.get(which), Toast.LENGTH_LONG).show();
                    }
                });
        return builder.create();
    }
}