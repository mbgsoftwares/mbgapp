package com.roamprocess1.roaming4world.roaming4world;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.roamprocess1.roaming4world.R;

public class CustomListAdapterSetting extends ArrayAdapter<String> {
	private final Activity context;
	private final String[] web;
	private final Integer[] imageId;

	public CustomListAdapterSetting(Activity context, String[] web,
			Integer[] imageId) {
		super(context, R.layout.r4wsetting_row, web);
		this.context = context;
		this.web = web;
		this.imageId = imageId;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.r4wsetting_row, null, true);
		TextView txtTitle = (TextView) rowView.findViewById(R.id.tv_settings_option);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.iv_settings_option);
		txtTitle.setText(web[position]);
		imageView.setImageResource(imageId[position]);
		return rowView;
	}
}