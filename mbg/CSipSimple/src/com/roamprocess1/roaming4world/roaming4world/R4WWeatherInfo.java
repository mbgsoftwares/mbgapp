package com.roamprocess1.roaming4world.roaming4world;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.roamprocess1.roaming4world.R;

public class R4WWeatherInfo extends Activity {

	View rootView;
	boolean unknownhost = false;
	private Typeface nexaBold, nexaNormal, bebas;
	int weekdayNum;
	String city_name = "frag", hum = "0", currentdate, longitude, latitude,
			provider, monthName, dateDay;
	String todayDate, TodayMonth, TodayWeekday;
	String date[] = new String[7];
	String temp_min[] = new String[7];
	String temp_max[] = new String[7];
	String humidity[] = new String[7];
	String climate_main[] = new String[7];
	String climate_desrcption[] = new String[7];
	String Dateis[] = new String[7];
	String WeekdayIs[] = new String[7];
	ListAdapter ListAdapterhor;

	LinearLayout ll_weatherbg;
	
	boolean gps_enabled = false;
	boolean network_enabled = false;

	HorizontalScrollView Scroll;
	protected LocationManager locationManager;
	protected LocationListener locationListener;
	Location location;
	TextView month, weekday, humidity_main, temp_min_value, temp_max_value,date_main, tv, climateMain, climateDesc, tempMin, tempMax;
	String WeekDays[] = { "SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY","THURSDAY", "FRIDAY", "SATURDAY" };

	ListView horizontalListView;
	TextView humidity_1, temp_1,weekday1, weekday2, weekday3, weekday4, weekday5, weekday6,weekday7, cityName,
	temp_min1, temp_min2, temp_min3, temp_min4, temp_min5, temp_min6,temp_min7,temp_max1, temp_max2, temp_max3, temp_max4, temp_max5, temp_max6,
			temp_max7;
	ImageView imageView1, imageView2, imageView3, imageView4, imageView5,imageView6, imageView7, main_iv;

	@SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.r4wweatherinfo);
		Initilizer();

		android.app.ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);

		View customView = getLayoutInflater().inflate(R.layout.r4wvarificationheader, null);
		ImageButton imgbackbtn = (ImageButton) customView.findViewById(R.id.imgbackbtn);
		imgbackbtn.setClickable(true);
		imgbackbtn.setVisibility(View.VISIBLE);
		actionBar.setCustomView(customView);
		imgbackbtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		try {

			locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
			try {
				gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			} catch (Exception ex) {}
				try {
					network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
				} catch (Exception ex) {
			}

			if (network_enabled) {
				location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				} else {
				location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			}
			longitude = String.valueOf(location.getLongitude());
			latitude = String.valueOf(location.getLatitude());
		} catch (Exception e) {
			e.printStackTrace();
			Log.d("ErrorFromLocation", "ErrorFromLocation");
		}

		new MyAsyncTaskWeatherInfo().execute();
}
	
	
	private void convertDate() {
		// TODO Auto-generated method stub
		Log.d("Convert Date", "Enter");
		for (int q = 0; q < date.length; q++) {
			try {
				Calendar calendar = Calendar.getInstance();
				TimeZone tz = TimeZone.getDefault();

				int timeinmillis = Integer.parseInt(date[q]);

				calendar.setTimeInMillis(Integer.parseInt(date[q]) * 1000L);

				calendar.add(Calendar.MILLISECOND,
						tz.getOffset(calendar.getTimeInMillis()));
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

				Dateis[q] = formatter.format(calendar.getTime());

				String datetrip[] = Dateis[0].split("-");
				dateDay = datetrip[2];
				WeekdayIs[q] = new SimpleDateFormat("EEE").format(calendar
						.getTime());

				Log.d("Dateis[q]", Dateis[q] + " @");
				// SimpleDateFormat month_date = new SimpleDateFormat("MMM");
				// monthName = month_date.format(calendar.getTime());
				monthName = new SimpleDateFormat("MMM").format(calendar
						.getTime());
				Log.d("monthName", monthName + " A");
				Log.d("WeekdayIs[q]", WeekdayIs[q] + " @");
			} catch (Exception e) {
			}
		}

	}

	private void Initilizer() {
		// TODO Auto-generated method stub
		tv = (TextView) findViewById(R.id.tv2);
		weekday = (TextView) findViewById(R.id.tvweekday);
		month = (TextView) findViewById(R.id.tvmonth);
		date_main = (TextView) findViewById(R.id.tvdate);
		humidity_main = (TextView) findViewById(R.id.tvhumidity);
		climateMain = (TextView) findViewById(R.id.tvclimateMain);
		climateDesc = (TextView) findViewById(R.id.tvclimateDesc);
		tempMin = (TextView) findViewById(R.id.tvtempMin);
		Log.d("tempMin@", tempMin.toString() + " @1");
		tempMax = (TextView) findViewById(R.id.tvtempMax);
		Log.d("weekday@", " @1");

		humidity_1 = (TextView) findViewById(R.id.tv2);
		temp_1 = (TextView) findViewById(R.id.textView1);

		weekday1 = (TextView) findViewById(R.id.tvweekday1);
		cityName = (TextView) findViewById(R.id.tvcityName);
		weekday2 = (TextView) findViewById(R.id.tvweekday2);
		weekday3 = (TextView) findViewById(R.id.tvweekday3);
		weekday4 = (TextView) findViewById(R.id.tvweekday4);
		weekday5 = (TextView) findViewById(R.id.tvweekday5);
		weekday6 = (TextView) findViewById(R.id.tvweekday6);
		weekday7 = (TextView) findViewById(R.id.tvweekday7);

		temp_min1 = (TextView) findViewById(R.id.tvtempmin1);
		temp_min2 = (TextView) findViewById(R.id.tvtempmin2);
		temp_min3 = (TextView) findViewById(R.id.tvtempmin3);
		temp_min4 = (TextView) findViewById(R.id.tvtempmin4);
		temp_min5 = (TextView) findViewById(R.id.tvtempmin5);
		temp_min6 = (TextView) findViewById(R.id.tvtempmin6);
		temp_min7 = (TextView) findViewById(R.id.tvtempmin7);

		temp_max1 = (TextView) findViewById(R.id.tvtempmax1);
		temp_max2 = (TextView) findViewById(R.id.tvtempmax2);
		temp_max3 = (TextView) findViewById(R.id.tvtempmax3);
		temp_max4 = (TextView) findViewById(R.id.tvtempmax4);
		temp_max5 = (TextView) findViewById(R.id.tvtempmax5);
		temp_max6 = (TextView) findViewById(R.id.tvtempmax6);
		temp_max7 = (TextView) findViewById(R.id.tvtempmax7);

		imageView1 = (ImageView) findViewById(R.id.imageView1);
		imageView2 = (ImageView) findViewById(R.id.imageView2);
		imageView3 = (ImageView) findViewById(R.id.imageView3);
		imageView4 = (ImageView) findViewById(R.id.imageView4);
		imageView5 = (ImageView) findViewById(R.id.imageView5);
		imageView6 = (ImageView) findViewById(R.id.imageView6);
		imageView7 = (ImageView) findViewById(R.id.imageView7);
		
		ll_weatherbg = (LinearLayout) findViewById(R.id.ll_weatherbg);
		main_iv = (ImageView) findViewById(R.id.iv_mainTemp);
	}

	class MyAsyncTaskWeatherInfo extends AsyncTask<Void, Void, Boolean> {

		ProgressDialog mProgressDialog4;

		@Override
		protected void onPreExecute() {
			mProgressDialog4 = ProgressDialog.show(R4WWeatherInfo.this,getResources().getString(R.string.loading), getResources().getString(R.string.data_loading));
		}

		@Override
		protected Boolean doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			if (webServiceFlightInfo()) {
				Log.d("doInBackgroud", "doInBackground");
				return true;
			} else {
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			convertDate();
			tempMin.setText(temp_min[0]);
			tempMax.setText(temp_max[0]);
			humidity_main.setText(":  " + humidity[0]);
			cityName.setText(city_name);
			month.setText(monthName + ",");
			month.setTextColor(Color.RED);
			weekday.setText(WeekDays[weekdayNum] + ",");
			weekday.setTextColor(Color.RED);
			date_main.setText(dateDay);
			climateMain.setText(climate_main[0] + " : ");
			climateMain.setTextColor(Color.RED);
			climateDesc.setText(climate_desrcption[0]);

			imageviewSet();
			setMinTemp();
			setMaxTemp();
			setWeekday();
			// setFont();
			mProgressDialog4.dismiss();

		}

		private void setMaxTemp() {
			// TODO Auto-generated method stub
			try {
				temp_max1.setText(temp_max[0]);
				temp_max2.setText(temp_max[1]);
				temp_max3.setText(temp_max[2]);
				temp_max4.setText(temp_max[3]);
				temp_max5.setText(temp_max[4]);
				temp_max6.setText(temp_max[5]);
				temp_max7.setText(temp_max[6]);
			} catch (Exception e) {
				// TODO: handle exception
			}

		}

		private void setWeekday() {
			// TODO Auto-generated method stub
			try {
				weekday1.setText(WeekdayIs[0]);
				weekday2.setText(WeekdayIs[1]);
				weekday3.setText(WeekdayIs[2]);
				weekday4.setText(WeekdayIs[3]);
				weekday5.setText(WeekdayIs[4]);
				weekday6.setText(WeekdayIs[5]);
				weekday7.setText(WeekdayIs[6]);
			} catch (Exception e) {
				// TODO: handle exception
			}

		}

		private void setMinTemp() {
			// TODO Auto-generated method stub
			try {
				temp_min1.setText(temp_min[0]);
				temp_min2.setText(temp_min[1]);
				temp_min3.setText(temp_min[2]);
				temp_min4.setText(temp_min[3]);
				temp_min5.setText(temp_min[4]);
				temp_min6.setText(temp_min[5]);
				temp_min7.setText(temp_min[6]);
			} catch (Exception e) {
				// TODO: handle exception
			}

		}

		public void imageviewSet() {
			imageSet(main_iv, climate_main[0]);
			backgroundSet(ll_weatherbg, climate_main[0]);
			
			imageSet(imageView1, climate_main[0]);
			imageSet(imageView2, climate_main[1]);
			imageSet(imageView3, climate_main[2]);
			imageSet(imageView4, climate_main[3]);
			imageSet(imageView5, climate_main[4]);
			imageSet(imageView6, climate_main[5]);
			imageSet(imageView7, climate_main[6]);
		}

		public void imageSet(ImageView climate, String cli) {
			try {

				if (cli.equals("Clear")) {
					Log.d("Clear", "Clear 0");
					climate.setImageResource(R.drawable.clearbig);
					Log.d("Clear", "Clear 1");
				} else if (cli.equals("Clouds")) {
					Log.d("Clouds", "Clouds 0");
					climate.setImageResource(R.drawable.cloud);
					Log.d("Clouds", "Clouds 1");
				} else if (cli.equals("Rain")) {
					Log.d("Rain", "Rain 0");
					climate.setImageResource(R.drawable.heavyrain);
					Log.d("Rain", "Rain 1");
				} else {
					Log.d("else", "else 0");
					climate.setImageResource(R.drawable.clearbig);
					Log.d("else", "else 1");
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
		
		public void backgroundSet(LinearLayout climate, String cli) {
			try {

				if (cli.equals("Clear")) {
					Log.d("Clear", "Clear 0");
					climate.setBackgroundResource(R.drawable.sunnybig);
					Log.d("Clear", "Clear 1");
				} else if (cli.equals("Clouds")) {
					Log.d("Clouds", "Clouds 0");
					climate.setBackgroundResource(R.drawable.cloudbig);
					Log.d("Clouds", "Clouds 1");
				} else if (cli.equals("Rain")) {
					Log.d("Rain", "Rain 0");
					climate.setBackgroundResource(R.drawable.rainbig);
					Log.d("Rain", "Rain 1");
				} else {
					Log.d("else", "else 0");
					climate.setBackgroundResource(R.drawable.sunnybig);
					Log.d("else", "else 1");
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
		

	}

	private void setFont() {
		nexaBold = Typeface.createFromAsset(this.getAssets(),"fonts/NexaBold.otf");
		nexaNormal = Typeface.createFromAsset(this.getAssets(),"fonts/NexaLight.otf");
		bebas = Typeface.createFromAsset(this.getAssets(),"fonts/BebasNeue.otf");
		tempMin.setTypeface(nexaNormal);
		tempMax.setTypeface(nexaNormal);
		humidity_main.setTypeface(nexaNormal);
		cityName.setTypeface(nexaBold);
		month.setTypeface(nexaBold);
		weekday.setTypeface(nexaBold);
		date_main.setTypeface(nexaBold);
		climateMain.setTypeface(nexaBold);
		climateDesc.setTypeface(nexaNormal);
		humidity_1.setTypeface(nexaBold);
		temp_1.setTypeface(nexaBold);

		temp_max1.setTypeface(bebas);
		temp_max2.setTypeface(bebas);
		temp_max3.setTypeface(bebas);
		temp_max4.setTypeface(bebas);
		temp_max5.setTypeface(bebas);
		temp_max6.setTypeface(bebas);
		temp_max7.setTypeface(bebas);

		temp_min1.setTypeface(bebas);
		temp_min2.setTypeface(bebas);
		temp_min3.setTypeface(bebas);
		temp_min4.setTypeface(bebas);
		temp_min5.setTypeface(bebas);
		temp_min6.setTypeface(bebas);
		temp_min7.setTypeface(bebas);

		weekday1.setTypeface(bebas);
		weekday2.setTypeface(bebas);
		weekday3.setTypeface(bebas);
		weekday4.setTypeface(bebas);
		weekday5.setTypeface(bebas);
		weekday6.setTypeface(bebas);
		weekday7.setTypeface(bebas);
	}

	@SuppressLint("NewApi") @SuppressWarnings("code")
	public boolean webServiceFlightInfo() {

		try {
			Log.d("webServiceFlightInfo", "called");
			HttpParams p = new BasicHttpParams();
			p.setParameter("user", "1");

			HttpClient httpclient = new DefaultHttpClient(p);
			String url = "http://api.openweathermap.org/data/2.5/forecast/daily?+lat=";
			url += latitude;
			url += "&lon=";
			url += longitude;
			url += "&mode=json&units=metric&cnt=7";

			Log.d("Service URL - Weather Info", url);

			Log.d("httppost", "before @");
			HttpGet httpget = new HttpGet(url);

			Log.d("httppost", httpget.toString() + " @");

			try {

				ResponseHandler<String> responseHandler;
				String responseBody;
				responseHandler = new BasicResponseHandler();
				responseBody = httpclient.execute(httpget, responseHandler);
				JSONObject json = new JSONObject(responseBody);
				JSONObject CITY = json.getJSONObject("city");
				city_name = CITY.getString("name");
				JSONArray listarray = json.getJSONArray("list");
				for (int i = 0; i < listarray.length(); i++) {
					JSONObject day = listarray.getJSONObject(i);
					date[i] = day.getString("dt");
					humidity[i] = day.getString("humidity");
					JSONObject temparray = day.getJSONObject("temp");
					temp_min[i] = temparray.getString("min") + "\u00B0";
					temp_max[i] = temparray.getString("max") + "\u00B0";
					JSONArray weatherarray = day.getJSONArray("weather");
					for (int j = 0; j < weatherarray.length(); j++) {
						JSONObject weather = weatherarray.getJSONObject(j);
						climate_main[i] = weather.getString("main");
						climate_desrcption[i] = weather.getString("description");
					}

				}

				Time today = new Time(Time.getCurrentTimezone());
				Calendar cal = Calendar.getInstance();
				today.setToNow();
				TodayMonth = String.valueOf(today.MONTH);
				todayDate = String.valueOf(today.monthDay);
				weekdayNum = today.weekDay;
				String month1 = "05";
				String month = cal.getDisplayName(Integer.parseInt(month1), 2,Locale.US);

			} catch (Exception e) {
				// TODO: handle exception
			}

			return true;

		} catch (Throwable t) {

			t.printStackTrace();

			return false;
		}
	}

	@SuppressLint("NewApi") class MySimpleArrayAdapter extends ArrayAdapter<String> {
		private final Context context;
		private final String[] values;
		private final String[] img;
		private final String[] tempMin;
		private final String[] tempMax;
		private int weekDayNumber;
		private int positionweekday;

		public MySimpleArrayAdapter(Context context, String[] values,
				String[] img, String[] tempMin, String[] tempMax,
				int weekDayNumber) {
			super(context, R.layout.r4wweatherinfo, values);
			this.context = context;
			this.values = values;
			this.img = img;
			this.tempMin = tempMin;
			this.tempMax = tempMax;
			this.weekDayNumber = weekDayNumber;

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// rootView = inflater.inflate(R.layout.horizontallist, parent,
			// false);
			TextView weekday = (TextView) rootView.findViewById(R.id.tvweekday1);
			ImageView imageView = (ImageView) rootView
					.findViewById(R.id.imageView1);
			TextView tempmin = (TextView) rootView
					.findViewById(R.id.tvtempmin1);
			TextView tempmax = (TextView) rootView
					.findViewById(R.id.tvtempmax1);

			weekday.setText(values[position]);
			String s = img[position] + "";
			if (s.equals("Rain")) {
				imageView.setImageResource(R.drawable.heavyrain);
			} else {
				imageView.setImageResource(R.drawable.heavyrain);
			}

			tempmin.setText(tempMin[position]);
			tempmax.setText(tempMax[position]);
			return rootView;
		}

	}

}
