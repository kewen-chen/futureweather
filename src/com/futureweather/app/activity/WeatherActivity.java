package com.futureweather.app.activity;


import com.futureweather.app.R;
import com.futureweather.app.util.HttpCallbackListener;
import com.futureweather.app.util.HttpUtil;
import com.futureweather.app.util.Utility;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity implements OnClickListener {

	
	private LinearLayout weatherInfoLayout;
	/**
	 * 用于显示城市名
	 */
	private TextView cityNameText;
	/**
	 * 用于显示发布时间
	 */
	private TextView publishText;
	/**
	 * 用于显示天气描述信息
	 */
	private TextView weatherDespText;
	/**
	 * 用于显示气温1
	 */
	private TextView temp1;
	/**
	 * 用于显示气温2
	 */
	private TextView temp2;
	/**
	 * 用于显示当前日期
	 */
	private TextView currentDateText;
	/**
	 * 用于显示切换城市
	 */
//	private Button switchCity;
	/**
	 * 用于显示更新天气信息
	 */
//	private Button refreshWeather;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		//初始化各个控件
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText = (TextView) findViewById(R.id.city_name);
		publishText = (TextView) findViewById(R.id.publish_text);
		weatherDespText = (TextView) findViewById(R.id.weatger_desp);
		temp1 = (TextView) findViewById(R.id.temp1);
		temp2 = (TextView) findViewById(R.id.temp2);
		currentDateText = (TextView) findViewById(R.id.current_date);
//		switchCity = (Button) findViewById(R.id.swtch_city);
//		refreshWeather = (Button) findViewById(R.id.refresh_weather);
		String countyCode = getIntent().getStringExtra("county_code");
		
		if(!TextUtils.isEmpty(countyCode)) {
			//有县级代号就去查询天气
			publishText.setText("同步中...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		}else {
			//没有县级代号就直接显示本地天气
			showWeather();
		}
		
//		switchCity.setOnClickListener(this);
//		refreshWeather.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.swtch_city:
//			Intent intent = new Intent(this,ChooseAreaActivity.class);
//			intent.putExtra("from_weather_activity", true);
//			startActivity(intent);
//			finish();
//			
//			break;
//		case R.id.refresh_weather:
//			publishText.setText("同步中...");
//			SharedPreferences prefs = 
//					PreferenceManager.getDefaultSharedPreferences(this);
//			String weatherCode = prefs.getString("weather_code", "");
//			if(!TextUtils.isEmpty(weatherCode)) {
//				queryWeatherInfo(weatherCode);
//				
//			}
//			break;
//		default:
//			break;
//		}
		
	}
	
	/**
	 * 查询县级代号所对应的天气代号
	 * @param countyCode
	 */
	private void queryWeatherCode(String countyCode) {
		String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
		queryFromServer(address, "countyCode");
	}
	/**
	 * 查询天气代号所对应的天气
	 * @param weatherCode
	 */
	private void queryWeatherInfo(String weatherCode) {
		String address = "http://www.weather.com.cn/data/cityinfo/"  + 
	weatherCode + ".html";
		queryFromServer(address, "weatherCode");
	}

	private void queryFromServer(final String address, final String type) {
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(final String response) {

				if("countyCode".equals(type)) {
					if(!TextUtils.isEmpty(response)) {
						//从服务器返回的信息中解析出天气代号
						String[] array = response.split("\\|");
						if(array != null || array.length == 2) {
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				}else if("weatherCode".equals(type)) {
					//处理服务器返回的天气信息
					Utility.handleWeatherResponse(WeatherActivity.this, response);
				
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							
							showWeather();
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
					
						publishText.setText("同步失败");
					}
				});
			}
		});
	}


	/**
	 * 从SharedPreferences文件中读取储存的天气信息，并显示到界面上
	 */

	private void showWeather() {
		
		SharedPreferences prefs = 
				PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText(prefs.getString("city_ame", ""));
		temp1.setText(prefs.getString("temp1", ""));
		temp2.setText(prefs.getString("temp2", ""));
		weatherDespText.setText(prefs.getString("weather_desp", ""));
		publishText.setText("今天" + prefs.getString("publish_time", "") + "发布");
		currentDateText.setText(prefs.getString("current_date", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		
	}


}
