package com.example.entrainement_obl;

import android.content.Context;
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
        super(context, 0, entrainementTypeList);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        EntrainementAspect aspect= null;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.items,parent,false);
        }
        aspect = new EntrainementAspect();
        aspect.Name=(TextView) convertView.findViewById(R.id.NameE);
        aspect.Rep = (TextView) convertView.findViewById(R.id.Repp);
        aspect.min=(TextView) convertView.findViewById(R.id.Mins);
        aspect.sec=(TextView) convertView.findViewById(R.id.Secs);
        aspect.rec=(TextView) convertView.findViewById(R.id.Recc);

        EntrainementType item = getItem(position);
        if (item != null) {
            aspect.Name.setText(item.name);
            aspect.Rep.setText(item.repetitions);
            aspect.min.setText(item.minutes);
            aspect.sec.setText(item.secondes);
            aspect.rec.setText(item.recuperation);

        }
        return convertView;
    }
    private static class EntrainementAspect{
        public TextView Name;
        public TextView Rep;
        public TextView min;
        public TextView sec;
        public TextView rec;

    }
}
