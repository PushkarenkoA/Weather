package com.pushkarenko.weather;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Fragmentfirst extends Fragment {

    private CallBack myInterface;
    private ListView listView;

    static ArrayList<HashMap<String, String>>  myForecastData;

    public Fragmentfirst() {  // empty constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            myInterface = (CallBack) context;
        } catch (ClassCastException exception) {
            throw new ClassCastException(context.toString() + " Must implement CallbackInterface");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false); // Inflate the Fragment
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Starting AsynkTask to download and parse data
        DataLoader weatherTask = new DataLoader();
        weatherTask.execute();
        listView = (ListView) view.findViewById(R.id.listview);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Listen to clicks ...
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myInterface.updateContent(position); // Interface implemented in WeatherMainActivity
            }
        });
    }

    // ------- AsyncTask ------- ///
    // Here we download and parse data
    public class DataLoader extends AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> {

        // Pull data from JSON String to JSON-Object to ArrayList of HashMaps
        private ArrayList<HashMap<String, String>> getWeatherDataFromJson(String forecastJsonStr) throws JSONException {
            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray("list");
            ArrayList<HashMap<String, String>> forecasts = new ArrayList<>();

            for (int i = 0; i < weatherArray.length(); i++) {
                JSONObject oneJSONForecast = weatherArray.getJSONObject(i);  // one forecast

                // Date
                Date date = new Date((long) oneJSONForecast.getInt("dt") * 1000);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                String week = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.ENGLISH);
                String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
                String month = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH);
                String year = String.valueOf(cal.get(Calendar.YEAR));
                String hour = new SimpleDateFormat("HH:mm").format(cal.getTime());
                // Temperatures
                String temperature = oneJSONForecast.getJSONObject("main").get("temp").toString();
                String temperatureMax = oneJSONForecast.getJSONObject("main").get("temp_max").toString();
                String temperatureMin = oneJSONForecast.getJSONObject("main").get("temp_min").toString();
                // Description and icon
                String main = oneJSONForecast.getJSONArray("weather").getJSONObject(0).getString("main");
                String icon = oneJSONForecast.getJSONArray("weather").getJSONObject(0).getString("icon");
                // Details
                String humidity = oneJSONForecast.getJSONObject("main").get("humidity").toString();
                String wind = oneJSONForecast.getJSONObject("wind").get("speed").toString();
                String direction = oneJSONForecast.getJSONObject("wind").get("deg").toString();

                // Push all the data to the HashMap
                HashMap<String, String> oneForecast = new HashMap<>();
                oneForecast.put("week", week);
                oneForecast.put("day", day);
                oneForecast.put("month", month);
                oneForecast.put("year", year);
                oneForecast.put("hour", hour);
                oneForecast.put("temperature", temperature);
                oneForecast.put("temperatureMax", temperatureMax);
                oneForecast.put("temperatureMin", temperatureMin);
                oneForecast.put("main", main);
                oneForecast.put("icon", icon);
                oneForecast.put("humidity", humidity);
                oneForecast.put("wind", wind);
                oneForecast.put("direction", direction);

                forecasts.add(i, oneForecast);
            }
            return forecasts;
        }

        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String forecastJsonStr = null;

            try {
                // Take the URL and get the data:
                URL url = new URL(getResources().getString(R.string.openweathermap_url));
                urlConnection = (HttpURLConnection) url.openConnection();  // Open the connection (should be closed late)
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream == null) {
                    return null;  // do nothing
                }
                StringBuilder buffer = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null)
                    buffer.append(line);
                if (buffer.length() == 0) { return null; }  // stream was empty
                forecastJsonStr = buffer.toString(); // JSON as String
            }
            catch (IOException e) { return null; }
            finally {
                if (urlConnection != null) { urlConnection.disconnect(); }  // Close connection
                if (reader != null) {
                    try { reader.close(); }
                    catch (final IOException e) { e.printStackTrace();} } // Close BufferedReader
            }
            try { return getWeatherDataFromJson(forecastJsonStr); }
            catch (JSONException e) { e.printStackTrace(); }
            return null; // if something was wrong above
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
            if (result != null) {
                myForecastData = result;
                // Adapter
                String[] from = {"day", "month", "year", "week", "hour", "temperature", "main"};
                int[] to = {R.id.day, R.id.month, R.id.year, R.id.week, R.id.hour, R.id.temperature, R.id.main};
                MySimpleAdapter adapter = new MySimpleAdapter(getContext(), result, R.layout.list_item_for_listview, from, to);
                listView.setAdapter(adapter);
            }
            super.onPostExecute(result);
        }
    }

    public class MySimpleAdapter extends SimpleAdapter {
        public MySimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = super.getView(position, convertView, parent);
            ImageView iconImageView = (ImageView) row.findViewById(R.id.icon);

            return row;
        }
    }

}