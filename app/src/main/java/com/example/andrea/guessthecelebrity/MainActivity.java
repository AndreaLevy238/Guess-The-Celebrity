package com.example.andrea.guessthecelebrity;

import android.graphics.Bitmap;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

   public class DownloadTask extends AsyncTask<String, Void, String>{
      @Override
      protected String doInBackground(String... urls) {
         String result = "";
         try {
            URL url = new URL(urls[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            InputStream in = urlConnection.getInputStream();
            InputStreamReader reader = new InputStreamReader(in);
            int data = reader.read();
            while(data != -1) {
               char current = (char) data;
               result += current;
               data = reader.read();
            }
            return result;
         }
         catch (IOException e) {
            Log.e("downloadTaskError","unable to open url connection");
            return null;
         }
      }
   }

   private Button[] buttons;
   private int correctAnswerLocation;
   private int correctAnswerIndex;
   private Celebrity[] celebrities;
   private ImageView imageView;
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      String result = downloadSiteCode();
      celebrities = createCelebrities(result);
      buttons = new Button[4];
      buttons[0] = (Button) findViewById(R.id.button0);
      buttons[1] = (Button) findViewById(R.id.button1);
      buttons[2] = (Button) findViewById(R.id.button2);
      buttons[3] = (Button) findViewById(R.id.button3);
      imageView = (ImageView) findViewById(R.id.imageView);
      prepareNextRound();
   }

   public String downloadSiteCode()
   {
      DownloadTask downloadTask = new DownloadTask();
      String result;
      try {
         result = downloadTask.execute("http://www.posh24.com/celebrities").get();
         return result;
      } catch (Exception e) {
         Log.e("onCreateError", "unable to pull from website");
         return null;
      }
   }

   public Celebrity[] createCelebrities(String result)
   {
      ArrayList<String> celebImages = new ArrayList<>(100);
      ArrayList<String> celebNames = new ArrayList<>(100);
      Celebrity[] celebrities = new Celebrity[100];
      String[] splitResult = result.split("<div class=\"sidebarContainer\">");
      Pattern p = Pattern.compile("img src=\"(.*?)\"");
      Matcher m = p.matcher(splitResult[0]);
      while (m.find())
      {
         celebImages.add(m.group(1));
      }
      p = Pattern.compile("alt=\"(.*?)\"");
      m = p.matcher(splitResult[0]);
      while (m.find())
      {
         celebNames.add(m.group(1));
      }
      for (int i = 0; i < 100; i++)
      {
         celebrities[i] = new Celebrity(celebNames.get(i),celebImages.get(i));
      }
      return celebrities;
   }

   public void chooseAnswer(View view)
   {
      if(view.getTag().toString().equals(String.valueOf(correctAnswerLocation)))
      {
         Toast.makeText(this,"Correct!",Toast.LENGTH_SHORT).show();
      }
      else
      {
         String msg = "Wrong! The correct answer is " + celebrities[correctAnswerIndex].name;
         Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
      }
      prepareNextRound();
   }

   public void prepareButtons()
   {
      Random rand = new Random();
      correctAnswerLocation = rand.nextInt(4);
      correctAnswerIndex = rand.nextInt(100);
      for (int i = 0; i < 4; i++)
      {
         if (correctAnswerLocation == i)
         {
            buttons[i].setText(celebrities[correctAnswerIndex].name);
         }
         else
         {
            int j = rand.nextInt(100);
            while (j == correctAnswerIndex)
            {
               j = rand.nextInt(100);
            }

            buttons[i].setText(celebrities[j].name);
         }
      }
   }
   public void prepareNextRound()
   {
      prepareButtons();
      Bitmap img = celebrities[correctAnswerIndex].downloadImage();
      imageView.setImageBitmap(img);
   }
}
