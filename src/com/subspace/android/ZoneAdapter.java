/*  Subspace Mobile - A Android Subspace Client
    Copyright (C) 2012 Kingsley Masters. All Rights Reserved.
    
    kingsley dot masters at gmail dot com

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.subspace.android;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.subspace.redemption.R;
import com.subspace.redemption.dataobjects.Zone;

public class ZoneAdapter extends ArrayAdapter<Zone> {
	private List<Zone> items;
	private boolean includePing;
	 private LayoutInflater inflator;

	public ZoneAdapter(Context context, int textViewResourceId,
			List<Zone> items, boolean includePing) {
		super(context, textViewResourceId, items);
		this.items = items;
		this.includePing = includePing;
		this.inflator= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ZoneViewItem holder;
		Zone o = items.get(position);
		if (convertView == null) {
			convertView = inflator.inflate(R.layout.zone_item, null);
			holder = new ZoneViewItem();
			holder.topText = (TextView) convertView.findViewById(R.id.toptext);
			holder.bottomText = (TextView) convertView.findViewById(R.id.bottomtext);
			convertView.setTag(holder);
		} else {
			holder = (ZoneViewItem) convertView.getTag();
		}

		if (o != null) {
			if (includePing) {
				holder.topText.setText(o.Name + " : Pinging...");

				if (o.Ping != 0) {
					holder.topText.setText(o.Name + " : " + o.Population
							+ " Players");
					holder.bottomText.setText("Ping " + Math.round(o.Ping / 10)
							* 10);
				}
				// change color depending on ping
				if (o.Ping < 0) {
					holder.topText.setTextColor(Color.DKGRAY);
					holder.bottomText.setTextColor(Color.DKGRAY);
					holder.topText.setText(o.Name);
					holder.bottomText.setText("Ping - Failed to connnect");
				} else if (o.Ping > 500) {
					holder.topText.setTextColor(Color.RED);
					holder.bottomText.setTextColor(Color.RED);
				} else if (o.Ping > 150) {
					holder.topText.setTextColor(Color.YELLOW);
					holder.bottomText.setTextColor(Color.YELLOW);
				} else {
					holder.topText.setTextColor(Color.GREEN);
					holder.bottomText.setTextColor(Color.GREEN);
				}
			} else {
				holder.topText.setText(Html.fromHtml("<font color='green'>" + o.Name
						+ "</font>  " + o.Population + " players"));
				holder.bottomText.setText(Html.fromHtml(o.Ip + ":" + o.Port));
			}
		}
		return convertView;
	}
}
