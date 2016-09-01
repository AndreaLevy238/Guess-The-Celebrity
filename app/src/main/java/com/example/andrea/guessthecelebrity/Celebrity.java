package com.example.andrea.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;


public class Celebrity {
   public String name;
   public String imageUrl;
   public Celebrity(String name, String imageUrl)
   {
      this.name = name;
      this.imageUrl = imageUrl;
   }

   public class DownloadImage extends AsyncTask<String, Void, Bitmap>
   {
      @Override
      protected Bitmap doInBackground(String... urls) {
         try {
            URL url = new URL(urls[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            return BitmapFactory.decodeStream(inputStream);

         } catch (IOException e) {
            e.printStackTrace();
            return null;

         }
      }
   }

   public Bitmap downloadImage()
   {
      DownloadImage downloadImage = new DownloadImage();
      try {
         return downloadImage.execute(imageUrl).get();
      } catch (InterruptedException | ExecutionException e) {
         e.printStackTrace();
         return null;
      }
   }


}
