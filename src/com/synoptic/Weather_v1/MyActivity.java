package com.synoptic.Weather_v1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */

    final static String URL_WEATHER = "http://api.openweathermap.org/data/2.5/weather?q=Dnipropetrovsk,Ukraine";
    public EditText enterWeatherData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Create a view control
        enterWeatherData = (EditText)findViewById(R.id.editText);

        // Create a button
        Button buttonCalculate = (Button)findViewById(R.id.button);
        buttonCalculate.setOnClickListener(new Button.OnClickListener() {

            public void onClick( View v )
            {
                new HttpRequestTask().execute();
            }
        });
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, Void> {

        String textResult;

        @Override
        protected Void doInBackground(Void ... params){
            URL url;

            try{
                url = new URL(URL_WEATHER);
                BufferedReader bufferedReader = new BufferedReader(
                  new InputStreamReader(url.openStream()));

                String stringBuffer;
                String stringText = "";
                while ((stringBuffer = bufferedReader.readLine()) != null){
                    stringText += stringBuffer;
                }
                bufferedReader.close();
                textResult = stringText;

            } catch (MalformedURLException  ex){
                ex.printStackTrace();
            } catch (IOException ex){
                ex.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            double temperature;
            String temperString;
            String humidity;
            String temp;

            if(textResult == null || textResult.isEmpty())
            {
                showAlert( "Связь с сетью Internet отсутствует!", "OK", "Проверка связи с Internet");
                return;
            }

            int idx1 = textResult.indexOf("\"temp\":") + 7;
            int idx2 = textResult.indexOf( ",", idx1 );

            if(idx2 <= idx1)
                return;

            try{
                temperature = Double.parseDouble(textResult.substring(idx1, idx1 + (idx2 - idx1))) - 274.15; // temperature in Kelvins
                temperString = String.format( "%4.1f", temperature);

                idx1 = textResult.indexOf("humidity\":") + 10;
                idx2 = textResult.indexOf( "}", idx1);

                if( idx2 <= idx1 )
                    return;

                humidity = textResult.substring(idx1, idx1 + (idx2 - idx1));

                temperString = "Погода в Днепропетровске.\nТемпература воздуха: " + temperString + "\u00b0" +"\n";
                temperString += "Влажность воздуха: " + humidity + "%";
            } catch (ArithmeticException ex){
                ex.getStackTrace();
                temperString = "Погода не определена.";
           }

           enterWeatherData.setText(temperString);

           super.onPostExecute(result);
        }
    }

    //
    void showAlert(String s, String buttonText, String msg)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setMessage( s );
        builder.setPositiveButton( buttonText, null);
        builder.setTitle( msg );
        builder.create().show();
    }
}
