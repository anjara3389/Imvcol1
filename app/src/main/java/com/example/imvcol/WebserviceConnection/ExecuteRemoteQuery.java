package com.example.imvcol.WebserviceConnection;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public abstract class ExecuteRemoteQuery extends AsyncTask<URL, Integer, ArrayList> {

    private ArrayList query;
    private Context ctx;

    private final String USER_AGENT = "Mozilla/5.0";

    public abstract void receiveData(Object object) throws Exception;

    public ExecuteRemoteQuery() {

    }

    public void setQuery(ArrayList query) {
        this.query = query;
    }

    public void setContext(Context context) {
        this.ctx = context;
    }

    protected void onPreExecute() {

    }

    protected ArrayList doInBackground(URL... urls) {
        ArrayList respuestas = new ArrayList();
        try {

            for (int i = 0; i < query.size(); i++) {
                URL url = new URL("http://190.66.24.90:4111/w1/webservices.php");
                //"http://190.66.24.90:4111/w1/webservices.php"
                //http://192.68.1.217/w1/webservices.php
                HttpURLConnection con = (HttpURLConnection) url.openConnection();


                //añade request header
                con.setRequestMethod("POST");
                con.setRequestProperty("User-Agent", USER_AGENT);
                con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                con.setDoOutput(true);
                String urlParameters = (String) query.get(i);
                System.out.println("CONECTÓ!!!");

                // Envía post request

                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                int responseCode = con.getResponseCode();

                if (responseCode != 200) {
                    throw new Exception("Error " + responseCode);
                } else {
                    System.out.println("\nSending 'POST' request to URL : " + "http://190.66.24.90:4111/w1/webservices_copia.php");
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
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(ctx, "Error de entrada/salida /" + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(ctx, "Error/" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return respuestas;
    }

    protected void onProgressUpdate(Integer... progress) {


    }

    protected void onPostExecute(ArrayList result) {
        try {
            if (result != null) {
                receiveData(result);
            } else {
                throw new Exception("Datos vacíos");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(ctx, "Error/" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}