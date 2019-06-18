package com.example.abdo.geussthecelebtry;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> sourrcImg = new ArrayList<String>();
    ArrayList<String> nameOfImage = new ArrayList<String>();
    String[] choseAnswers = new String[4];
    int correctAnswer;
    Button button0;
    Button button1;
    Button button2;
    Button button3;
    ImageView imageView;

    public class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        String strImg;

        @Override
        protected Bitmap doInBackground(String... urls) {
            URL url;
            HttpURLConnection httpURLConnection=null;
            try {
                url = new URL(urls[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();
                InputStream inputStream = httpURLConnection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }


        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        String strImg = "";

        @Override
        protected String doInBackground(String... urls) {
            URL url;
            HttpURLConnection httpURLConnection=null;
            try {
                url = new URL(urls[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                int data = inputStreamReader.read();
                while (data != -1) {
                    char current = (char) data;
                    strImg += current;
                    data = inputStreamReader.read();
                }
                return strImg;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    int randImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView2);
        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        DownloadTask downloadTask = new DownloadTask();
        String strContent = "";
        try {
            strContent = downloadTask.execute("http://www.posh24.se").get();
            Pattern p = Pattern.compile("<img src=\"(.*?)\"");
            Matcher m = p.matcher(strContent);

            while (m.find()) {
                sourrcImg.add(m.group(1));
            }
//"alt=\"(.*?)\""
            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(strContent);
            while (m.find()) {
                nameOfImage.add(m.group(1));
            }
            createNewQuition();
///< Start DownloadTask>

        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e1) {
            e1.printStackTrace();
        }
    }

    public void btnChose(View view) {

        if (view.getTag().toString().equals(Integer.toString(correctAnswer))) {
            Toast.makeText(getApplicationContext(), "correct", Toast.LENGTH_SHORT).show();
            createNewQuition();
        } else {
            Toast.makeText(getApplicationContext(), "incorrect ", Toast.LENGTH_LONG).show();
        }
    }

    public void createNewQuition() {

            Random random = new Random();
            randImg = random.nextInt(nameOfImage.size());
            DownloadImage downloadImage = new DownloadImage();
        Bitmap bitmapImage = null;
        try {
            bitmapImage = downloadImage.execute(sourrcImg.get(randImg)).get();
        } catch (ExecutionException e1) {
            e1.printStackTrace();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        imageView.setImageBitmap(bitmapImage);
            correctAnswer = random.nextInt(choseAnswers.length);

             Log.i("===>",Integer.toString(correctAnswer));
            for (int i = 0; i < choseAnswers.length; i++) {

                if (correctAnswer == i) {
                    choseAnswers[i] = nameOfImage.get(randImg);
                } else {
                    int incooerct = random.nextInt(nameOfImage.size());
                    while (incooerct == randImg) {
                        incooerct = random.nextInt(nameOfImage.size());
                    }
                    choseAnswers[i] = nameOfImage.get(incooerct);
                }

            }
            button0.setText(choseAnswers[0]);
            Log.i("TEXT ==>",choseAnswers[0]);
            button1.setText(choseAnswers[1]);
            button2.setText(choseAnswers[2]);
            button3.setText(choseAnswers[3]);
    }
}
