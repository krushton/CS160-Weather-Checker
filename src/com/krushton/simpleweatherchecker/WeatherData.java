package com.krushton.simpleweatherchecker;


public class WeatherData {
	String name;
	String weatherInfo;
	String temp;
	String updateTime;
	int id;
	
	public WeatherData(String name, String weatherInfo, String temp, String updateTime, int id) {
		this.name = name;
		this.weatherInfo = weatherInfo;
		this.temp = temp;
		this.updateTime = updateTime;
		this.id = id;
	}

}


