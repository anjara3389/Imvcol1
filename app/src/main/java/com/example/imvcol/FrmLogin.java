package com.example.imvcol;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.CancellationException;



public class FrmLogin extends AppCompatActivity {

    private EditText usuario;
    private EditText contrasenia;
    private Button btnIngresar;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_login);

        usuario = findViewById(R.id.frm_login_txt_usuario);
        contrasenia = findViewById(R.id.frm_login_txt_contrasenia);
        btnIngresar = findViewById(R.id.frm_login_btn_ingresar);
        dialog= new ProgressDialog(this);
        dialog.setMessage("Cargando");
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(false);


        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (!dialog.isShowing()) {
                    dialog.show();
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                }
                try {
                    @SuppressLint("StaticFieldLeak") ExecuteRemoteQuery remoteQuery = new ExecuteRemoteQuery() {
                        @Override
                        public void receiveData(Object object) throws Exception {
                            ArrayList resultAsync = (ArrayList) object;
                            if (resultAsync.get(0) == null) {
                                throw new Exception("No se han podido cargar el usuario, intente nuevamente");
                            }
                            if (resultAsync.get(0).equals("[]")) {
                                closeDialog();
                                Toast.makeText(v.getContext(), "Usuario y/o contraseña incorrectos", Toast.LENGTH_LONG).show();
                            } else {
                                ExecuteRemoteQuery remote = new ExecuteRemoteQuery() {
                                    @Override
                                    public void receiveData(Object object) throws Exception {
                                        ArrayList resultsDatos = (ArrayList) object;
                                        if (resultsDatos.get(0) == null || resultsDatos.get(1) == null || resultsDatos.get(2) == null || resultsDatos.get(3) == null || resultsDatos.get(4) == null) {
                                            closeDialog();
                                            throw new Exception("No se han podido cargar los datos, intente nuevamente");

                                        } else {
                                            closeDialog();
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


                                };
                                remote.setContext(v.getContext());

                                ArrayList queryDatos = new ArrayList();
                                queryDatos.add("SELECT BODEGA, DESCRIPCION FROM BODEGAS");
                                queryDatos.add("SELECT * FROM REFERENCIAS_GRU");
                                queryDatos.add("SELECT * FROM REFERENCIAS_SUB");
                                queryDatos.add("SELECT * FROM REFERENCIAS_SUB2");
                                queryDatos.add("SELECT * FROM REFERENCIAS_SUB3");
                                queryDatos.add("SELECT * FROM referencias_cla");

                                remote.setQuery(queryDatos);
                                remote.execute();
                            }
                        }
                    };

                    remoteQuery.setContext(v.getContext());
                    ArrayList queryUsers = new ArrayList();

                    queryUsers.add("SELECT * " +
                            "FROM USUARIOS " +
                            "WHERE USUARIO=UPPER('" + usuario.getText() + "') " +
                            "AND CLAVE=UPPER('" + contrasenia.getText() + "')");

                    remoteQuery.setQuery(queryUsers);
                    remoteQuery.execute();

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

    private void closeDialog(){
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}




