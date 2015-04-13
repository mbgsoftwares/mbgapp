package com.roamprocess1.roaming4world.roaming4world;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.roamprocess1.roaming4world.R;
public class ViewPagerAdapter extends PagerAdapter {
	Context context;
	String[] rank;
	String[] country;
	String[] population;
	int[] flag;
	LayoutInflater inflater;

	public ViewPagerAdapter(Context context, String[] rank, String[] country,
			String[] population, int[] flag) {
		this.context = context;
		this.rank = rank;
		this.country = country;
		this.population = population;
		this.flag = flag;
	}

	@Override
	public int getCount() {
		return rank.length;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((RelativeLayout) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		TextView txtrank;
		TextView txtcountry;
		TextView txtpopulation;
		ImageView imgflag;

		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View itemView = inflater.inflate(R.layout.viewpager_item, container,false);
		txtrank = (TextView) itemView.findViewById(R.id.rank);
		txtcountry = (TextView) itemView.findViewById(R.id.country);
		txtpopulation = (TextView) itemView.findViewById(R.id.population);
		txtrank.setText(rank[position]);
		txtcountry.setText(country[position]);
		txtpopulation.setText(population[position]);
		imgflag = (ImageView) itemView.findViewById(R.id.flag);
		imgflag.setImageResource(flag[position]);
		((ViewPager) container).addView(itemView);

		return itemView;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// Remove viewpager_item.xml from ViewPager
		((ViewPager) container).removeView((RelativeLayout) object);

	}
}
