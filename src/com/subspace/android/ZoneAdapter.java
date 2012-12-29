package com.subspace.android;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.subspace.redemption.R;
import com.subspace.redemption.dataobjects.Zone;

public class ZoneAdapter extends ArrayAdapter<Zone> {
	private ArrayList<Zone> items;        

    public ZoneAdapter(Context context, int textViewResourceId, ArrayList<Zone> items) {
            super(context, textViewResourceId, items);
            this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	ZoneViewItem holder;
    	Zone o = items.get(position);
    	if(convertView==null)
    	{
    		convertView = View.inflate(getContext(), R.layout.zone_item, null);
    		holder = new ZoneViewItem();
    		holder.topText = (TextView)convertView.findViewById(R.id.toptext);
    		holder.bottomText = (TextView)convertView.findViewById(R.id.bottomtext);
    		convertView.setTag(holder);
    	}
    	else {
    		holder = (ZoneViewItem)convertView.getTag();
    	}
    	
    	if(o!=null)
    	{
    		holder.topText.setText(o.Name + " : " + o.Population + " Players");
    		if(o.Ping!=0)
    		{
    			holder.bottomText.setText("Ping " + o.Ping);
    		}
    	}
        return convertView;
    }
}
