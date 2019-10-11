package com.example.ecommerce.spotsale2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.appyvet.materialrangebar.RangeBar;

public class FilterDialogFragment extends DialogFragment {

    public int priceMultiplier;
    public int tickInterval;
    public int tickEnd;

    public FilterDialogFragment(int priceMultiplier, int tickInterval, int tickEnd, OnApplyListener listener) {
        this.priceMultiplier = priceMultiplier;
        this.tickInterval = tickInterval;
        this.tickEnd = tickEnd;
        this.listener = listener;
    }

    public interface OnApplyListener {
        void OnApplyFilters(double rangeMin, double rangeMax);
    }
    private final OnApplyListener listener;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_filter, null);

        final RangeBar rangeBar = (RangeBar) view.findViewById(R.id.range_bar);
        rangeBar.setTickInterval(tickInterval);
        rangeBar.setTickStart(0);
        rangeBar.setTickEnd(tickEnd);

        (rangeBar).setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
                ((TextView)view.findViewById(R.id.range_start)).setText(String.valueOf(leftPinIndex * priceMultiplier));
                ((TextView)view.findViewById(R.id.range_end)).setText(String.valueOf(rightPinIndex * priceMultiplier));
            }

            @Override
            public void onTouchStarted(RangeBar rangeBar) {

            }

            @Override
            public void onTouchEnded(RangeBar rangeBar) {

            }
        });

        builder.setView(view);
        builder.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(getActivity(), "Apply Filters not yet available", Toast.LENGTH_LONG).show();
                listener.OnApplyFilters(Double.valueOf(rangeBar.getLeftPinValue()), Double.valueOf(rangeBar.getRightPinValue()));
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(), "Cancel Filtering", Toast.LENGTH_SHORT).show();
            }
        });

        return builder.create();
    }
}
