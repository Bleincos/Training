package com.example.entrainement_obl;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class EntrainementAdapter extends ArrayAdapter<EntrainementType> {
        public EntrainementAdapter(@NonNull Context context, int ressource, List<EntrainementType> entrainementTypeList){
        super(context, ressource, entrainementTypeList);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        EntrainementAspect aspect= null;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView==null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.items, parent, false);
        }
            aspect = new EntrainementAspect();
            aspect.Name=(TextView) convertView.findViewById(R.id.txtName);
            aspect.Rep = (TextView) convertView.findViewById(R.id.txtRep);
            aspect.Min=(TextView) convertView.findViewById(R.id.txtMins);
            aspect.Sec=(TextView) convertView.findViewById(R.id.txtSecs);
            aspect.Rec=(TextView) convertView.findViewById(R.id.txtRec);
        EntrainementType item = getItem(position);
        if (item != null) {
            aspect.Name.setText(item.name);
            Log.i("ArrayAdapter","Rep:"+item.repetitions);
            aspect.Rep.setText(""+item.repetitions);
            aspect.Min.setText(""+item.minutes);
            aspect.Sec.setText(""+item.secondes);
            aspect.Rec.setText(""+item.recuperation);

        }
        return convertView;
    }
    private static class EntrainementAspect{
        public TextView Name;
        public TextView Rep;
        public TextView Min;
        public TextView Sec;
        public TextView Rec;

    }
}
