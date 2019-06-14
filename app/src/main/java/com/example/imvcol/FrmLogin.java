package com.example.imvcol;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imvcol.Email.SendEmailAsyncTask;
import com.example.imvcol.Utils.NetUtils;
import com.example.imvcol.WebserviceConnection.ExecuteRemoteQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CancellationException;


public class FrmLogin extends AppCompatActivity {

    private EditText txtUsuario, contrasenia;
    private TextView olvidoContrasenia;
    private Button btnIngresar;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.frm_login);

        txtUsuario = findViewById(R.id.frm_login_txt_usuario);
        contrasenia = findViewById(R.id.frm_login_txt_contrasenia);
        btnIngresar = findViewById(R.id.frm_login_btn_ingresar);
        olvidoContrasenia = findViewById(R.id.frm_login_lbl_olvido_clave);


        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                try {
                    checkUserOnWebService(v);
                } catch (CancellationException e) {
                    e.printStackTrace();
                    Toast.makeText(v.getContext(), "Operación cancelada /" + e.getMessage(), Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

        });
        olvidoContrasenia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!txtUsuario.getText().toString().equals("")) {
                    getMailFromWservice();
                } else {
                    Toast.makeText(FrmLogin.this, "Escriba un usuario", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    /**
     * Verifica el usuario a travéz del wserver en la base de datos DMS
     *
     * @param v vista
     * @throws Exception
     */
    private void checkUserOnWebService(final View v) throws Exception {
        if (!NetUtils.isOnlineNet(FrmLogin.this)) {
            throw new Exception("No hay conexión a internet");
        } else {
            @SuppressLint("StaticFieldLeak") ExecuteRemoteQuery remoteQuery = new ExecuteRemoteQuery() {
                @Override
                public void receiveData(Object object) throws Exception {
                    ArrayList resultAsync = (ArrayList) object;
                    if (resultAsync.get(0) == null) {
                        throw new Exception("No se han podido cargar el usuario, intente nuevamente");
                    }
                    if (resultAsync.get(0).equals("[]")) {
                        //dialogUtils.dissmissDialog();
                        Toast.makeText(v.getContext(), "Usuario y/o contraseña incorrectos", Toast.LENGTH_LONG).show();
                    } else {
                        SQLiteDatabase db = BaseHelper.getReadable(getApplicationContext());

                        new Usuario().delete(db);

                        SimpleDateFormat dformat = new SimpleDateFormat("yyyy-MM-dd");
                        String date = dformat.format(new Date());

                        usuario = new Usuario(txtUsuario.getText().toString(),
                                contrasenia.getText().toString(),
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                1,
                                null,
                                false,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                date);
                        usuario.insert(db);
                        usuario = usuario.selectUsuario(db);

                        if (new Bodega().countBodegas(db) == 0) {
                            getDataFromWebservice(v, db);
                        } else {
                            Intent i = new Intent(v.getContext(), FrmSelectBodega.class);
                            startActivityForResult(i, 1);
                            BaseHelper.tryClose(db);
                            finish();
                        }
                    }
                }
            };

            remoteQuery.init(v.getContext(), this.getWindow(), "Validando usuario");
            ArrayList queryUsers = new ArrayList();

            queryUsers.add("SELECT * " +
                    "FROM USUARIOS " +
                    "WHERE USUARIO=UPPER('" + txtUsuario.getText() + "') " +
                    "AND CLAVE=UPPER('" + contrasenia.getText() + "') " +
                    "AND BLOQUEADO IS NULL");
            queryUsers.add(usuario.getQueryInsertLog("Inicio de sesión"));

            remoteQuery.setQuery(queryUsers);
            remoteQuery.execute();
        }
    }

    /**
     * Consulta el email de un usuario dado a travez del webservice
     */
    private void getMailFromWservice() {
        @SuppressLint("StaticFieldLeak") ExecuteRemoteQuery remote = new ExecuteRemoteQuery() {
            @Override
            public void receiveData(Object object) throws Exception {
                SQLiteDatabase db = BaseHelper.getWritable(FrmLogin.this);
                ArrayList resultsDatos = (ArrayList) object;

                ArrayList rawResults = ArrayUtils.convertToArrayList(new JSONArray((String) resultsDatos.get(0)), FrmLogin.this);
                if (resultsDatos.get(0).equals("[]")) {
                    //dialogUtils.dissmissDialog();
                    BaseHelper.tryClose(db);
                    Toast.makeText(FrmLogin.this, "El usuario no exíste.", Toast.LENGTH_LONG).show();
                } else {
                    JSONObject jsonResults = ((JSONObject) rawResults.get(0));

                    if (jsonResults.isNull("mail")) {
                        Toast.makeText(FrmLogin.this, "El usuario no tiene un email registrado.", Toast.LENGTH_LONG).show();
                    } else if (jsonResults.isNull("clave")) {
                        Toast.makeText(FrmLogin.this, "El usuario no tiene una contraseña válida.", Toast.LENGTH_LONG).show();
                    } else {
                        SendEmailAsyncTask emailTask = new SendEmailAsyncTask();
                        emailTask.init(FrmLogin.this, getWindow(), jsonResults.getString("mail"), jsonResults.getString("clave"));
                        emailTask.execute();
                    }
                    // dialogUtils.dissmissDialog();
                }
            }
        };
        remote.init(FrmLogin.this, this.getWindow(), "Cargando");

        ArrayList queryDatos = new ArrayList();

        String query = "SELECT t.mail,u.clave " +
                "FROM USUARIOS u " +
                "LEFT JOIN terceros t ON t.nit=u.nit " +
                "WHERE u.usuario=UPPER('" + txtUsuario.getText() + "') " +
                "AND u.bloqueado IS NULL";

        queryDatos.add(query);
        System.out.println("QUERYYYYYY///" + query);
        remote.setQuery(queryDatos);
        remote.execute();
    }

    /**
     * Trae bodegas,grupos, sub1,sub2,sub3,clases y la cantidad de cada uno de ellos desde base de datos DMS.
     *
     * @param v  View
     * @param db Base de datos
     * @throws Exception
     */
    private void getDataFromWebservice(final View v, final SQLiteDatabase db) throws Exception {
        if (!NetUtils.isOnlineNet(FrmLogin.this)) {
            //dialogUtils.dissmissDialog();
            throw new Exception("No hay conexión a internet");
        } else {
            @SuppressLint("StaticFieldLeak") ExecuteRemoteQuery remote = new ExecuteRemoteQuery() {
                @Override
                public void receiveData(Object object) throws Exception {
                    ArrayList resultsDatos = (ArrayList) object;
                    //dialogUtils.dissmissDialog();
                    if (resultsDatos.get(0) == null ||
                            resultsDatos.get(1) == null ||
                            resultsDatos.get(2) == null ||
                            resultsDatos.get(3) == null ||
                            resultsDatos.get(4) == null) {
                        throw new Exception("No se han podido cargar los datos, intente nuevamente");
                    } else {
                        fillDatabase(db, resultsDatos);
                    }
                }
            };
            remote.init(v.getContext(), this.getWindow(), "Cargando");

            ArrayList queryDatos = new ArrayList();
            queryDatos.add("SELECT COUNT(*) AS COUNT " +
                    "FROM BODEGAS " +
                    "WHERE inactiva IS NULL OR " +
                    "inactiva='N'");
            queryDatos.add("SELECT COUNT(*) AS COUNT FROM REFERENCIAS_GRU");
            queryDatos.add("SELECT COUNT(*) AS COUNT FROM REFERENCIAS_SUB");
            queryDatos.add("SELECT COUNT(*) AS COUNT FROM REFERENCIAS_SUB2");
            queryDatos.add("SELECT COUNT(*) AS COUNT FROM REFERENCIAS_SUB3");
            queryDatos.add("SELECT COUNT(*) AS COUNT FROM referencias_cla");
            queryDatos.add("SELECT BODEGA, DESCRIPCION, MENSAJE " +
                    "FROM BODEGAS " +
                    "WHERE inactiva IS NULL OR " +
                    "inactiva='N'");
            queryDatos.add("SELECT * FROM REFERENCIAS_GRU");
            queryDatos.add("SELECT * FROM REFERENCIAS_SUB");
            queryDatos.add("SELECT * FROM REFERENCIAS_SUB2");
            queryDatos.add("SELECT * FROM REFERENCIAS_SUB3");
            queryDatos.add("SELECT * FROM referencias_cla");

            remote.setQuery(queryDatos);
            remote.execute();
        }
    }

    /**
     * Llena base de datos local con datos obtenidos en getDataFromWebservice
     *
     * @param db           base de datos
     * @param resultsDatos datos obtenidos
     * @throws JSONException
     */
    private void fillDatabase(SQLiteDatabase db, ArrayList resultsDatos) throws JSONException {
        new Bodega().insertEmpty(db);
        new Grupo().insertEmpty(db);
        new Subgrupo().insertEmpty(db);
        new Subgrupo2().insertEmpty(db);
        new Subgrupo3().insertEmpty(db);
        new Clase().insertEmpty(db);

        for (int i = 0; i < resultsDatos.size(); i++) {
            ArrayList raw = ArrayUtils.convertToArrayList(new JSONArray((String) resultsDatos.get(i)), this);
            System.out.println("ESTE ES COUNT " + raw.get(0));
            if (i < 6) {
                int count = ((JSONObject) raw.get(0)).getInt("COUNT");
                if (i == 0) {
                    usuario.setnBodegas(count);
                } else if (i == 1) {
                    usuario.setnGrupos(count);
                } else if (i == 2) {
                    usuario.setnSubgrupos(count);
                } else if (i == 3) {
                    usuario.setnSubgrupos2(count);
                } else if (i == 4) {
                    usuario.setnSubgrupos3(count);
                } else if (i == 5) {
                    usuario.setnClases(count);
                }
            } else {
                for (int j = 0; j < raw.size(); j++) {
                    JSONObject rawJson = ((JSONObject) raw.get(j));
                    if (i == 6) {
                        Bodega bodega = new Bodega(rawJson.getString("BODEGA"),
                                rawJson.getString("DESCRIPCION"),
                                rawJson.getString("MENSAJE"));
                        bodega.insert(db);
                    } else if (i == 7) {
                        Grupo grupo = new Grupo(rawJson.getString("grupo"),
                                rawJson.getString("descripcion"));
                        grupo.insert(db);
                    } else if (i == 8) {
                        Subgrupo subgrupo = new Subgrupo(rawJson.getString("subgrupo"),
                                rawJson.getString("grupo"),
                                rawJson.getString("descripcion"));
                        subgrupo.insert(db);
                    } else if (i == 9) {
                        Subgrupo2 subgrupo2 = new Subgrupo2(rawJson.getString("subgrupo2"),
                                rawJson.getString("grupo"),
                                rawJson.getString("subgrupo"),
                                rawJson.getString("descripcion"));
                        subgrupo2.insert(db);
                    } else if (i == 10) {
                        Subgrupo3 subgrupo3 = new Subgrupo3(rawJson.getString("subgrupo3"),
                                rawJson.getString("grupo"),
                                rawJson.getString("subgrupo"),
                                rawJson.getString("subgrupo2"),
                                rawJson.getString("descripcion"));
                        subgrupo3.insert(db);
                    } else if (i == 11) {
                        Clase clase = new Clase(rawJson.getString("clase"),
                                rawJson.getString("descripcion"));
                        clase.insert(db);
                    }
                }
            }
        }
        usuario.updateCurrent(db);
        Intent i = new Intent(this, FrmSelectBodega.class);
        startActivityForResult(i, 1);
        BaseHelper.tryClose(db);
        //dialogUtils.dissmissDialog();
        finish();
    }
}




