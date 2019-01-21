package com.example.imvcol;

import android.content.Context;
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
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class ExecuteRemoteQuery extends AsyncTask<URL, Integer, String> {

    private String query;
    private Context ctx;
    private final String USER_AGENT = "Mozilla/5.0";

    public void setQuery(String query) {
        this.query = query;
    }

    public void setContext(Context context) {
        this.ctx = context;
    }

    protected String doInBackground(URL... urls) {
        String json = "No";
        try {
            URL url = new URL("http://190.66.24.90:4111/w1/webservices.php");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            //add reuqest header
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            String urlParameters = query;

            // Send post request
            con.setDoOutput(true);
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
            json = response.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    protected void onProgressUpdate(Integer... progress) {
        //setProgressPercent(progress[0]);
    }

    protected void onPostExecute(String result) {
        //showDialog("Downloaded " + result + " bytes");
        Toast.makeText(ctx, "POST EXECUTE " + result, Toast.LENGTH_LONG).show();
        System.out.print("resultado!!!" + result);
    }
}