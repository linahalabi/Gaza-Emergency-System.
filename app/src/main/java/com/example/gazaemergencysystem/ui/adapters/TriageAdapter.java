package com.example.gazaemergencysystem.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.gazaemergencysystem.R;

public class TriageAdapter extends ArrayAdapter<String> {
    private final String[] names;
    private final int[] colors;

    public TriageAdapter(Context context, String[] names, int[] colors) {
        super(context, R.layout.triage_spinner_row, names);
        this.names = names;
        this.colors = colors;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {
        View row = LayoutInflater.from(getContext()).inflate(R.layout.triage_spinner_row, parent, false);

        TextView label = row.findViewById(R.id.tv_triage_name);
        View colorBox = row.findViewById(R.id.v_color_indicator);

        label.setText(names[position]);
        colorBox.setBackgroundColor(colors[position]);

        return row;
    }
}
