package com.example.federicobernardi.guesscelebrities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    String[] images = new String[100], names = new String[100];

    //Both number are decremented by one because they are as indexes of arrays.
    final int CELEBRITIES = 99, OPTIONS = 3;

    //Contains the indexes of the already played celebrities.
    ArrayList<Integer> played = new ArrayList<Integer>();

    int currentCorrectOption, currentIndex;

    public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {

            try {

                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                Bitmap myBitmap;

                connection.connect();
                InputStream inputStream = connection.getInputStream();
                myBitmap = BitmapFactory.decodeStream(inputStream);

                return myBitmap;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public class DownloadTextTask extends AsyncTask<String, Void, String> {

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

        DownloadTextTask task = new DownloadTextTask();

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

            this._nextCelebrity();


        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    private void _nextCelebrity() {
        this.currentIndex = getCelebrityIndex();
        this.setCelebImage(this.currentIndex);
        this.setValidOption(this.currentIndex);

    }

    public void nextCelebrity(View view) {
        this.checkIfCorrect(Integer.valueOf((String)view.getTag()));
        this._nextCelebrity();
    }

    private void checkIfCorrect(int selectedOption) {
        Log.i("Seleced Option", Integer.toString(selectedOption));
        Log.i("Correct Option", Integer.toString(this.currentCorrectOption));

        if (selectedOption == this.currentCorrectOption) {
            Toast.makeText(getApplicationContext(), "Correct !", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(
                getApplicationContext(),
                "Incorrect, it is " + this.names[this.currentIndex] + "!",
                Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void setValidOption(int correctIndex) {
        Button op0 = (Button)findViewById(R.id.button),
               op1 = (Button)findViewById(R.id.button1),
               op2 = (Button)findViewById(R.id.button2),
               op3 = (Button)findViewById(R.id.button3);

        Random rand = new Random();
        int correctOption = rand.nextInt(this.OPTIONS);
        int[] options = new int[4];

        for (int i = 0; i < this.OPTIONS + 1; i++) {
            if (i == correctOption) {
                options[i] = correctIndex;
            } else {
                options[i] = rand.nextInt(this.CELEBRITIES);
            }
        }

        op0.setText(this.names[options[0]]);
        op1.setText(this.names[options[1]]);
        op2.setText(this.names[options[2]]);
        op3.setText(this.names[options[3]]);

        this.currentCorrectOption = correctOption;

    }


    private void setCelebImage(int index) {
        ImageView celebImage = (ImageView)findViewById(R.id.celebrity);
        DownloadImageTask task = new DownloadImageTask();
        Bitmap celebImageBitMap;

        try {

            celebImageBitMap = task.execute(this.images[index]).get();
            celebImage.setImageBitmap(celebImageBitMap);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getCelebrityIndex() {
        Random rand = new Random();
        int index = 0;
        boolean flag = true;

        while (flag) {
            index = rand.nextInt(99);
            flag = played.contains(index);
        }

        played.add(index);

        return index;
    }

    private void showArray(String[] a) {
        for(int i = 0; i < a.length; i++) {
            Log.i(Integer.toString(i), a[i]);
        }
    }
}








