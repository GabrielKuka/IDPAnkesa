package com.ankesa.idp.idpankesa;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

 class CustomAdapter extends ArrayAdapter<Model> {

    private Model[] modelItems = null;
    private Context context;


    CustomAdapter(Context c, Model[] resource) {
        super(c, R.layout.item_layout, resource);

        this.context = c;
        this.modelItems = resource;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.item_layout, parent, false);

        ImageView type = (ImageView) convertView.findViewById(R.id.typeOfFileId);
        TextView url = (TextView) convertView.findViewById(R.id.locationId);

        url.setText(modelItems[position].getUrl());


        if(modelItems[position].getFolder())
            type.setImageResource(R.drawable.folder);
        else
            type.setImageResource(R.drawable.file);


        return convertView;
    }


}
