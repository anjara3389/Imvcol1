package com.example.imvcol;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class FrmLogin extends AppCompatActivity {

    private EditText usuario;
    private EditText contrasenia;
    private Button btnIngresar;
    private ProgressBar progresBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_login);

        usuario = findViewById(R.id.frm_login_txt_usuario);
        contrasenia = findViewById(R.id.frm_login_txt_contrasenia);
        btnIngresar = findViewById(R.id.frm_login_btn_ingresar);
        progresBar = findViewById(R.id.frm_login_progressbar);
        progresBar.setVisibility(View.GONE);

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //progresBar.setVisibility(v.VISIBLE);
                try {
                    ExecuteRemoteQuery remoteQuery = new ExecuteRemoteQuery();
                    remoteQuery.setContext(v.getContext());
                    remoteQuery.setBar(progresBar);

                    ArrayList queryUsers = new ArrayList();

                    queryUsers.add("SELECT * " +
                            "FROM USUARIOS " +
                            "WHERE USUARIO=UPPER('" + usuario.getText() + "') " +
                            "AND CLAVE=UPPER('" + contrasenia.getText() + "')");

                    remoteQuery.setQuery(queryUsers);
                    ArrayList resultAsync = remoteQuery.execute().get();
                    if (resultAsync.get(0) == null) {
                        throw new Exception("No se han podido cargar el usuario, intente nuevamente");
                    }
                    if (resultAsync.get(0).equals("[]")) {
                        //progresBar.setVisibility(View.GONE);
                        Toast.makeText(v.getContext(), "Usuario y/o contraseña incorrectos", Toast.LENGTH_LONG).show();
                        progresBar.setVisibility(View.GONE);
                    } else {
                        //progresBar.setVisibility(View.GONE);

                        ExecuteRemoteQuery remote = new ExecuteRemoteQuery();
                        remote.setContext(v.getContext());

                        ArrayList queryDatos = new ArrayList();
                        queryDatos.add("SELECT BODEGA, DESCRIPCION FROM BODEGAS");
                        queryDatos.add("SELECT * FROM REFERENCIAS_GRU");
                        queryDatos.add("SELECT * FROM REFERENCIAS_SUB");
                        queryDatos.add("SELECT * FROM REFERENCIAS_SUB2");
                        queryDatos.add("SELECT * FROM REFERENCIAS_SUB3");
                        queryDatos.add("SELECT * FROM referencias_cla");

                        remote.setQuery(queryDatos);

                        ArrayList resultsDatos = remote.execute().get();
                        System.out.println("RESULT/////////");

                        if (resultsDatos.get(0) == null || resultsDatos.get(1) == null || resultsDatos.get(2) == null || resultsDatos.get(3) == null || resultsDatos.get(4) == null) {
                            throw new Exception("No se han podido cargar los datos, intente nuevamente");
                        } else {
                            Intent i = new Intent(v.getContext(), FrmOpciones.class);
                            i.putExtra("datos", resultsDatos);
                            startActivityForResult(i, 1);
                        }

                        /*
                        SELECT *
                        FROM REFERENCIAS_FIS F
                        LEFT JOIN REFERENCIAS R ON R.CODIGO=F.CODIGO
                        WHERE F.BODEGA=104;*/
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    Toast.makeText(v.getContext(), "Error de ejecución /" + e.getMessage(), Toast.LENGTH_LONG).show();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(v.getContext(), "Error de interrupción /" + e.getMessage(), Toast.LENGTH_LONG).show();
                } catch (CancellationException e) {
                    e.printStackTrace();
                    Toast.makeText(v.getContext(), "Operación cancelada /" + e.getMessage(), Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

        });
    }
}




