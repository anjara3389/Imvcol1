package com.example.imvcol;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import com.example.imvcol.FrmOpciones;


import javax.net.ssl.HttpsURLConnection;

public class FrmLogin extends AppCompatActivity {

    private EditText usuario;
    private EditText contrasenia;
    private Button btnIngresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_login);

        usuario = findViewById(R.id.frm_login_txt_usuario);
        contrasenia = findViewById(R.id.frm_login_txt_contrasenia);
        btnIngresar = findViewById(R.id.frm_login_btn_ingresar);

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(v.getContext(), "entra", Toast.LENGTH_LONG).show();
                //ExecuteRemoteQuery("",v.getContext());
                ExecuteRemoteQuery remoteQuery = new ExecuteRemoteQuery();
                remoteQuery.setContext(v.getContext());
                remoteQuery.setQuery("SELECT * FROM USUARIOS WHERE USUARIO='AROSERO'");
                remoteQuery.execute();

                //Intent i = new Intent(v.getContext(), FrmOpciones.class);
                //i.putExtra("isNew",true);
                //startActivityForResult(i, 1);
            }
        });
    }

    private void ExecuteRemoteQuery(final String query, final Context ctx) throws IOException {
        // final Context ctx= this;
        System.out.print("entra");
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://190.66.24.90:4111/w1/webservices.php");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    try {
                        String response = "";

                        urlConnection.setDoOutput(true);
                        urlConnection.setChunkedStreamingMode(0);

                        OutputStream os = urlConnection.getOutputStream();

                        OutputStream out = new BufferedOutputStream(os);
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                        BufferedWriter writer = new BufferedWriter(
                                new OutputStreamWriter(os, "UTF-8"));
                        writer.write(query);

                        writer.flush();
                        writer.close();
                        //os.close();
                        int responseCode = urlConnection.getResponseCode();

                        if (responseCode == HttpsURLConnection.HTTP_OK) {
                            String line;
                            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                            while ((line = br.readLine()) != null) {
                                response += line;
                            }
                            //Toast.makeText(ctx, "entra", Toast.LENGTH_LONG);
                            System.out.print("HERE........ " + response);
                        } else {
                            response = "";

                            //Toast.makeText(ctx, "NO", Toast.LENGTH_LONG);
                            System.out.print("NO");
                        }
                    } finally {
                        urlConnection.disconnect();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            public void onResul() {
                Toast.makeText(ctx, "NO", Toast.LENGTH_LONG);
            }


        });


    }
}




