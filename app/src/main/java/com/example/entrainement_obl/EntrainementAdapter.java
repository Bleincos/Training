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
        if (convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.items,parent,false);
        }
        aspect = new EntrainementAspect();
        aspect.Name=(TextView) convertView.findViewById(R.id.Name);
        aspect.Rep = (TextView) convertView.findViewById(R.id.Rep);
        aspect.min=(TextView) convertView.findViewById(R.id.min);
        aspect.sec=(TextView) convertView.findViewById(R.id.sec);
        return convertView;
    }
    private class EntrainementAspect{
        public TextView Name;
        public TextView Rep;
        public TextView min;
        public TextView sec;

    }
}
