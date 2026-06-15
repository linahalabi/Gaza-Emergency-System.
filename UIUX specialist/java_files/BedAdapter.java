package com.example.gazaemergencysystem.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.gazaemergencysystem.R;
import com.example.gazaemergencysystem.data.AppDatabase;
import com.example.gazaemergencysystem.data.model.Bed;
import com.example.gazaemergencysystem.data.model.Patient;

import java.util.List;

public class BedAdapter extends BaseAdapter {
    private final Context context;
    private final List<Bed> bedList;
    private final AppDatabase db;

    public BedAdapter(Context context, List<Bed> bedList) {
        this.context = context;
        this.bedList = bedList;
        this.db = AppDatabase.getInstance(context);
    }

    @Override
    public int getCount() { return bedList.size(); }
    @Override
    public Object getItem(int i) { return bedList.get(i); }
    @Override
    public long getItemId(int i) { return i; }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.bed_item, viewGroup, false);
        }

        com.google.android.material.card.MaterialCardView card = view.findViewById(R.id.card_bed);
        ImageView bedIcon = view.findViewById(R.id.img_bed_status);
        TextView bedNumber = view.findViewById(R.id.tv_bed_number);
        TextView patientNameText = view.findViewById(R.id.tv_patient_name);
        TextView subStatus = view.findViewById(R.id.tv_bed_substatus);
        TextView detailLink = view.findViewById(R.id.tv_action_link);
        Button btnAllocate = view.findViewById(R.id.btn_allocate);

        Bed bed = bedList.get(i);
        bedNumber.setText(bed.bedNumber != null ? "سرير " + bed.bedNumber : "سرير " + (i + 1));

        if (bed.is_occupied) {
            // حالة السرير المشغول
            card.setStrokeColor(ContextCompat.getColor(context, R.color.triage_red));
            bedIcon.setColorFilter(ContextCompat.getColor(context, R.color.triage_red));
            bedIcon.getBackground().mutate().setTint(ContextCompat.getColor(context, R.color.bg_red_light));
            
            subStatus.setText("حالة حرجة");
            subStatus.setTextColor(ContextCompat.getColor(context, R.color.text_gray));
            
            btnAllocate.setVisibility(View.GONE);
            detailLink.setVisibility(View.VISIBLE);

            // جلب اسم المريض من قاعدة البيانات
            if (bed.patient_id != null && bed.patient_id != 0) {
                Patient p = db.patientDao().getById(bed.patient_id);
                patientNameText.setText(p != null ? p.name : "رقم حالة مجهولة");
            } else {
                patientNameText.setText("رقم حالة مجهولة");
            }
            patientNameText.setTextColor(ContextCompat.getColor(context, R.color.triage_red));

        } else {
            // حالة السرير المتاح
            card.setStrokeColor(ContextCompat.getColor(context, R.color.triage_green));
            bedIcon.setColorFilter(ContextCompat.getColor(context, R.color.triage_green));
            bedIcon.getBackground().mutate().setTint(ContextCompat.getColor(context, R.color.bg_green_light));
            
            patientNameText.setText("متاح");
            patientNameText.setTextColor(ContextCompat.getColor(context, R.color.triage_green));
            
            subStatus.setText("جاهز للاستقبال");
            subStatus.setTextColor(ContextCompat.getColor(context, R.color.triage_green));
            
            btnAllocate.setVisibility(View.VISIBLE);
            detailLink.setVisibility(View.GONE);
        }

        return view;
    }
}
