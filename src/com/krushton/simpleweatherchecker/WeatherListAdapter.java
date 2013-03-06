package com.krushton.simpleweatherchecker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class WeatherListAdapter extends ArrayAdapter<WeatherData>{

	  private final Context context;
	  private final WeatherData[] values;

	  public WeatherListAdapter(Context context, WeatherData[] values) {
		  
	  //call the super class constructor and provide the ID of the resource to use instead of the default list view item
	    super(context, R.layout.list_adapter_item, values);
	    this.context = context;
	    this.values = values;
	  }
	  
	  //this method is called once for each item in the list
	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {

	    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View listItem = inflater.inflate(R.layout.list_adapter_item, parent, false);
	    
	    TextView tv = (TextView) listItem.findViewById(R.id.cityName);
	    tv.setText(values[position].name);
	    
	    TextView infoView = (TextView) listItem.findViewById(R.id.weatherInfo);
	    infoView.setText(values[position].weatherInfo + "\n" + values[position].temp + "\n" + values[position].updateTime);

	    return listItem;
	  
	  }
	  
}