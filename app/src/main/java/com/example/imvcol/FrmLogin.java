package com.example.imvcol;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

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

                try {
                    ExecuteRemoteQuery remoteQuery = new ExecuteRemoteQuery();
                    remoteQuery.setContext(v.getContext());

                    ArrayList queryUsers=new ArrayList();

                    queryUsers.add("SELECT * " +
                            "FROM USUARIOS " +
                            "WHERE USUARIO='" + usuario.getText() + "' " +
                            "AND CLAVE='" + contrasenia.getText() + "'");

                    remoteQuery.setQuery(queryUsers);
                    ArrayList resultAsync = remoteQuery.execute().get();

                    if (resultAsync.get(0).equals("[]")) {
                        Toast.makeText(v.getContext(), "Usuario y/o contraseña incorrectos", Toast.LENGTH_LONG).show();
                    } else {
                        //Toast.makeText(v.getContext(), "Si"+resultAsync, Toast.LENGTH_LONG).show();
                        ExecuteRemoteQuery remote = new ExecuteRemoteQuery();
                        remote.setContext(v.getContext());

                        ArrayList queryDatos = new ArrayList();
                        queryDatos.add("SELECT * FROM BODEGAS");
                        queryDatos.add("SELECT * FROM REFERENCIAS_GRU");
                        queryDatos.add("SELECT * FROM REFERENCIAS_SUB");
                        queryDatos.add("SELECT * FROM REFERENCIAS_SUB2");
                        queryDatos.add("SELECT * FROM REFERENCIAS_SUB3");

                        remote.setQuery(queryDatos);

                        ArrayList resultsDatos = remote.execute().get();
                        System.out.println("RESULT/////////");

                        /*for (int i=0;i<resultsDatos.size();i++){
                            System.out.println("RESULT/////////"+resultsDatos.get(i));
                        }*/

                        Intent i = new Intent(v.getContext(), FrmOpciones.class);
                        i.putExtra("datos", resultsDatos);
                        startActivityForResult(i, 1);
                        /*
                        SELECT *
                        FROM REFERENCIAS_FIS F
                        LEFT JOIN REFERENCIAS R ON R.CODIGO=F.CODIGO
                        WHERE F.BODEGA=104;


                        */
                    }
                    //System.out.print("resultado!!!" + result);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });
    }
}




