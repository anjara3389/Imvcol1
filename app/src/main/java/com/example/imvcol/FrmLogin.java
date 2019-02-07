package com.example.imvcol;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.imvcol.Utils.DialogUtils;
import com.example.imvcol.WebserviceConnection.ExecuteRemoteQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.CancellationException;


public class FrmLogin extends AppCompatActivity {

    private EditText usuario, contrasenia;
    private Button btnIngresar;
    private JSONObject zeroBodega, zeroGrupo, zeroSubgrupo, zeroSubgrupo2, zeroSubgrupo3, zeroClase;
    private DialogUtils dialogUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_login);

        usuario = findViewById(R.id.frm_login_txt_usuario);
        contrasenia = findViewById(R.id.frm_login_txt_contrasenia);
        btnIngresar = findViewById(R.id.frm_login_btn_ingresar);
        dialogUtils = new DialogUtils(this, "Cargando");


        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String pathDatabase = getDatabasePath("imvcol.db").getAbsolutePath();
                Toast.makeText(v.getContext(), pathDatabase, Toast.LENGTH_LONG).show();
                try {
                    dialogUtils.showDialog(getWindow());
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
    }


    private void checkUserOnWebService(final View v) {
        @SuppressLint("StaticFieldLeak") ExecuteRemoteQuery remoteQuery = new ExecuteRemoteQuery() {
            @Override
            public void receiveData(Object object) throws Exception {
                ArrayList resultAsync = (ArrayList) object;
                if (resultAsync.get(0) == null) {
                    throw new Exception("No se han podido cargar el usuario, intente nuevamente");
                }
                if (resultAsync.get(0).equals("[]")) {
                    dialogUtils.dissmissDialog();
                    Toast.makeText(v.getContext(), "Usuario y/o contraseña incorrectos", Toast.LENGTH_LONG).show();
                } else {
                    SQLiteDatabase db = BaseHelper.getReadable(getApplicationContext());

                    new Usuario().delete(db);
                    Usuario u = new Usuario(usuario.getText().toString(), contrasenia.getText().toString(), null, null, null, null, null, null, 1);
                    u.insert(db);

                    if (new Bodega().countBodegas(db) == 0) {
                        getWebserviceData(v, db);
                    } else {
                        Intent i = new Intent(v.getContext(), FrmSelectBodega.class);
                        // i.putExtra("datos", resultsDatos);
                        startActivityForResult(i, 1);
                        BaseHelper.tryClose(db);
                        dialogUtils.dissmissDialog();
                        finish();
                    }
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
    }

    private void getWebserviceData(final View v, final SQLiteDatabase db) {
        @SuppressLint("StaticFieldLeak") ExecuteRemoteQuery remote = new ExecuteRemoteQuery() {
            @Override
            public void receiveData(Object object) throws Exception {
                ArrayList resultsDatos = (ArrayList) object;
                dialogUtils.dissmissDialog();
                if (resultsDatos.get(0) == null || resultsDatos.get(1) == null || resultsDatos.get(2) == null || resultsDatos.get(3) == null || resultsDatos.get(4) == null) {
                    throw new Exception("No se han podido cargar los datos, intente nuevamente");
                } else {
                    fillDatabase(db, resultsDatos);
                }
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

    private void fillDatabase(SQLiteDatabase db, ArrayList resultsDatos) throws JSONException {
        putZeros();

        System.out.println("bodegas1" + (String) resultsDatos.get(0));
        System.out.println("grupos" + (String) resultsDatos.get(1));
        System.out.println("rawSubgrupos" + (String) resultsDatos.get(2));
        System.out.println("rawSubgrupos2" + (String) resultsDatos.get(3));
        System.out.println("rawSubgrupos3" + (String) resultsDatos.get(4));
        System.out.println("rawClases" + (String) resultsDatos.get(5));
        ArrayList rawBodegas = ArrayUtils.convertToArrayList(new JSONArray((String) resultsDatos.get(0)), zeroBodega, this);
        ArrayList rawGrupos = ArrayUtils.convertToArrayList(new JSONArray((String) resultsDatos.get(1)), zeroGrupo, this);
        ArrayList rawSubgrupos = ArrayUtils.convertToArrayList(new JSONArray((String) resultsDatos.get(2)), zeroSubgrupo, this);
        ArrayList rawSubgrupos2 = ArrayUtils.convertToArrayList(new JSONArray((String) resultsDatos.get(3)), zeroSubgrupo2, this);
        ArrayList rawSubgrupos3 = ArrayUtils.convertToArrayList(new JSONArray((String) resultsDatos.get(4)), zeroSubgrupo3, this);
        ArrayList rawClases = ArrayUtils.convertToArrayList(new JSONArray((String) resultsDatos.get(5)), zeroClase, this);


        for (int i = 0; i < rawBodegas.size(); i++) {
            JSONObject rawBodega = ((JSONObject) rawBodegas.get(i));
            Bodega bodega = new Bodega(rawBodega.getString("BODEGA"), rawBodega.getString("DESCRIPCION"));
            bodega.insert(db);
        }
        for (int i = 0; i < rawGrupos.size(); i++) {
            JSONObject rawGrupo = ((JSONObject) rawGrupos.get(i));
            Grupo grupo = new Grupo(rawGrupo.getString("grupo"), rawGrupo.getString("descripcion"));
            grupo.insert(db);
        }
        for (int i = 0; i < rawSubgrupos.size(); i++) {
            JSONObject rawSubgrupo = ((JSONObject) rawSubgrupos.get(i));
            Subgrupo subgrupo = new Subgrupo(rawSubgrupo.getString("subgrupo"), rawSubgrupo.getString("grupo"), rawSubgrupo.getString("descripcion"));
            subgrupo.insert(db);
        }
        for (int i = 0; i < rawSubgrupos2.size(); i++) {
            JSONObject rawSubgrupo2 = ((JSONObject) rawSubgrupos2.get(i));
            Subgrupo2 subgrupo2 = new Subgrupo2(rawSubgrupo2.getString("subgrupo2"), rawSubgrupo2.getString("grupo"), rawSubgrupo2.getString("subgrupo"), rawSubgrupo2.getString("descripcion"));
            subgrupo2.insert(db);
        }
        for (int i = 0; i < rawSubgrupos3.size(); i++) {
            JSONObject rawSubgrupo3 = ((JSONObject) rawSubgrupos3.get(i));
            Subgrupo3 subgrupo3 = new Subgrupo3(rawSubgrupo3.getString("subgrupo3"), rawSubgrupo3.getString("grupo"), rawSubgrupo3.getString("subgrupo"), rawSubgrupo3.getString("subgrupo2"), rawSubgrupo3.getString("descripcion"));
            subgrupo3.insert(db);
        }
        for (int i = 0; i < rawClases.size(); i++) {
            JSONObject rawClase = ((JSONObject) rawClases.get(i));
            Clase clase = new Clase(rawClase.getString("clase"), rawClase.getString("descripcion"));
            clase.insert(db);
        }

        Intent i = new Intent(this, FrmSelectBodega.class);
        // i.putExtra("datos", resultsDatos);
        startActivityForResult(i, 1);
        BaseHelper.tryClose(db);
        dialogUtils.dissmissDialog();
        finish();
    }



    private void putZeros() throws JSONException {
        zeroBodega = new JSONObject();
        zeroBodega.put("BODEGA", "-1");
        zeroBodega.put("DESCRIPCION", "Seleccione una bodega");
        zeroGrupo = new JSONObject();
        zeroGrupo.put("grupo", "-1");
        zeroGrupo.put("descripcion", "Seleccione un grupo");
        zeroSubgrupo = new JSONObject();
        zeroSubgrupo.put("grupo", "-1");
        zeroSubgrupo.put("subgrupo", "-1");
        zeroSubgrupo.put("descripcion", "Seleccione un subgrupo");
        zeroSubgrupo2 = new JSONObject();
        zeroSubgrupo2.put("grupo", -1);
        zeroSubgrupo2.put("subgrupo", -1);
        zeroSubgrupo2.put("subgrupo2", -1);
        zeroSubgrupo2.put("descripcion", "Seleccione un subgrupo2");
        zeroSubgrupo3 = new JSONObject();
        zeroSubgrupo3.put("grupo", -1);
        zeroSubgrupo3.put("subgrupo", -1);
        zeroSubgrupo3.put("subgrupo2", -1);
        zeroSubgrupo3.put("subgrupo3", -1);
        zeroSubgrupo3.put("descripcion", "Seleccione un subgrupo3");
        zeroClase = new JSONObject();
        zeroClase.put("clase", -1);
        zeroClase.put("descripcion", "Seleccione una clase");
    }
}




