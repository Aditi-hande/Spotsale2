package com.example.ecommerce.spotsale2;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.List;

public class CategoryDialogFragment extends DialogFragment {

    private ListAdapter adapter;
    private List<String> categories;

    public CategoryDialogFragment(ListAdapter adapter, List categories) {
        this.adapter = adapter;
        this.categories = categories;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("CHOOSE CATEGORY")
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity(), categories.get(which), Toast.LENGTH_LONG).show();
                    }
                });
        return builder.create();
    }
}