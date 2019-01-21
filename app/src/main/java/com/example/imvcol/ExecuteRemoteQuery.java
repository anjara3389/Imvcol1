package com.example.imvcol;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;


public class ExecuteRemoteQuery extends AsyncTask<URL, Integer, ArrayList> {

    private ArrayList query;
    private Context ctx;
    private final String USER_AGENT = "Mozilla/5.0";

    public void setQuery(ArrayList query) {
        this.query = query;
    }

    public void setContext(Context context) {
        this.ctx = context;
    }

    protected ArrayList doInBackground(URL... urls) {
        //String json = "No";

        ArrayList respuestas = new ArrayList();
        try {
            for (int i = 0; i < query.size(); i++) {

                URL url = new URL("http://190.66.24.90:4111/w1/webservices.php");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();


                //añade request header
                con.setRequestMethod("POST");
                con.setRequestProperty("User-Agent", USER_AGENT);
                con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                con.setDoOutput(true);
                String urlParameters = (String) query.get(i);

                // Envía post request

                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                int responseCode = con.getResponseCode();
                System.out.println("\nSending 'POST' request to URL : " + "http://190.66.24.90:4111/w1/webservices.php");
                System.out.println("Post parameters : " + urlParameters);
                System.out.println("Response Code : " + responseCode);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                respuestas.add(response.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return respuestas;
    }

    protected void onProgressUpdate(Integer... progress) {
        //setProgressPercent(progress[0]);
    }

    protected void onPostExecute(String result) {
        //showDialog("Downloaded " + result + " bytes");

    }
}