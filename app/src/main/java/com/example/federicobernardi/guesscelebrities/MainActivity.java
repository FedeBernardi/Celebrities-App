package com.example.federicobernardi.guesscelebrities;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    String[] images = new String[100], names = new String[100];

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                String result = "";
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.connect();

                InputStream inputStream = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);

                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;

                    result += current;

                    data = reader.read();
                }

                return result;

            } catch (Exception e) {
                Log.i("Error", ":(((((((((((( !");
                e.printStackTrace();

            }
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String url = "http://www.posh24.com/celebrities";

        DownloadTask task = new DownloadTask();
        try {
            //Gets the html from the page and gets the celebrities and their images.
            String html = task.execute(url).get();
            Pattern pattern = Pattern.compile("src=\"(.*)\" alt=\"(.*)\"");
            Matcher matcher = pattern.matcher(html);

            int counter = 0;
            while (matcher.find()) {
                images[counter] = matcher.group(1);
                names[counter] = matcher.group(2);
                counter++;
            }

            Log.i("Images", Arrays.toString(images));
            Log.i("Names", Arrays.toString(names));

        } catch (Exception e) {
            Log.i("Error", "Errorrrrr !");
            e.printStackTrace();

        }
    }
}








