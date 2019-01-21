package com.example.imvcol;

import android.content.Context;
import android.os.AsyncTask;
import android.text.Layout;
import android.view.View;
import android.widget.ProgressBar;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class ExecuteRemoteQuery extends AsyncTask<URL, Integer, ArrayList> {

    private ArrayList query;
    private Context ctx;
    private final String USER_AGENT = "Mozilla/5.0";
    private ProgressBar progressBar;

    public void setQuery(ArrayList query) {
        this.query = query;
    }

    public void setContext(Context context) {
        this.ctx = context;
    }
    public void setBar(ProgressBar pBar) {
        this.progressBar = pBar;
        progressBar.setVisibility(View.VISIBLE);
    }

    protected ArrayList doInBackground(URL... urls) {
          //To show ProgressBar
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
        //progressBar.setVisibility(View.VISIBLE);  //To show ProgressBar



        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
          //      WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        //setProgressPercent(progress[0]);
        //prog = new ProgressDialog(ctx);
        //prog.setTitle("Cargando");
        //prog.setMessage("Por favor espere...");
        //prog.setCancelable(false); // disable dismiss by tapping outside of the dialog
        //prog.show();
// To dismiss the dialog

    }

    protected void onPostExecute(ArrayList result) {
        //prog.dismiss();
        //showDialog("Downloaded " + result + " bytes");
        //progressBar.setVisibility(View.GONE);     // To Hide ProgressBar
    }
}