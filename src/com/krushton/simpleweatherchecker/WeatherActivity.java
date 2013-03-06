package com.krushton.simpleweatherchecker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import android.app.Activity;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;

public class WeatherActivity extends Activity {
	
	LocationManager manager;
	WeatherData[] cityDataList; 
	WeatherListAdapter mAdapter;
	final String[] citiesList = {"San Francisco", "Seattle", "Chicago", "Los Angeles"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather);

		//initialize an empty array of the same length
		cityDataList = new WeatherData[citiesList.length];
		
		//create a new object to hold data about each city and add to array.
		//right now all data except city name and list index are placeholders
		for (int i = 0; i < citiesList.length; i++) {
			WeatherData cd = new WeatherData(citiesList[i],"Loading...","","",i);
			cityDataList[i] = cd;
		}
		
		//create an instance of our customized array adapter and attach it to the listview
		ListView listView = (ListView) findViewById(R.id.weatherList);
		mAdapter = new WeatherListAdapter(this, cityDataList);          
		listView.setAdapter(mAdapter);
		
		
		//for each city, kicks off async request to API
		for (WeatherData city : cityDataList) {
			
	    	WeatherCheckTask task = new WeatherCheckTask();
			task.execute(city.name);
    	}

	}
		

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_weather, menu);
		return true;
	}

	
    //called when an async task returns successfully. 
    private void updateWeather(Document doc) {
    	
    	XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		try {
			//create xpath expressions to pull out the data we want from the XML response from wunderground
			XPathExpression cityName = xpath.compile("/response/current_observation/display_location/city");
			XPathExpression time = xpath.compile("/response/current_observation/observation_time");
			XPathExpression temp = xpath.compile("/response/current_observation/temperature_string");
			XPathExpression weather = xpath.compile("/response/current_observation/weather");
			
			//locate the WeatherData object matching the returned XML document by comparing the city name
			for (WeatherData cd : cityDataList) {

				if (cd.name.equals(cityName.evaluate(doc))) {
					
					//if it matches update the properties of the object based on what was returned in XML
					cd.name = cityName.evaluate(doc);
					cd.updateTime = time.evaluate(doc);
					cd.temp = temp.evaluate(doc);
					cd.weatherInfo = weather.evaluate(doc);
					
					//let the list adapter know to redisplay the list.
					mAdapter.notifyDataSetChanged();
					break;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
    	
    }

    private class WeatherCheckTask extends AsyncTask<String, Void, String> {

    	private final String API_KEY = "my api key";
    	private final String endPoint = "http://api.wunderground.com/api/" + API_KEY + "/conditions/q/CA/";
    	private Document xmlDocument;

        public String doInBackground(String... city)
        {
           if (city != null) {
        	   		
        	   		//replace spaces in url with "_"
        		   String url = endPoint + city[0].replace(" ", "_") + ".xml";
        		   
        		   HttpClient httpclient = new DefaultHttpClient();
        	       HttpResponse response;
        	       String responseString = null;
        	       
        	       
        	       try {
        	            response = httpclient.execute(new HttpGet(url));
        	            StatusLine statusLine = response.getStatusLine();
        	            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
        	                ByteArrayOutputStream out = new ByteArrayOutputStream();
        	                response.getEntity().writeTo(out);
        	                out.close();
        	                responseString = out.toString();
        	            } else{
        	                //Closes the connection.
        	                response.getEntity().getContent().close();
        	                throw new IOException(statusLine.getReasonPhrase());
        	            }
        	        } catch (ClientProtocolException e) {
        	            //TODO Handle problems..
        	        } catch (IOException e) {
        	            //TODO Handle problems..
        	        }
        	        return responseString;
           }
           else {       
        	   throw new IllegalArgumentException("Must provide terms");
           }
        }
        
        @Override
        protected void onPostExecute(String results)
        {       
            if(results != null)
            {   
            	try {
    	        	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    	 		   	DocumentBuilder builder = factory.newDocumentBuilder();
    	 		   	InputSource is = new InputSource(new StringReader(results));
    	 	        xmlDocument = builder.parse(is); 
    	 	        if (xmlDocument != null) {
						updateWeather(xmlDocument);
					}
            	}
            	catch (Exception ex) {
            		//do something
            	}
            }
        }
        
    }
  
}
