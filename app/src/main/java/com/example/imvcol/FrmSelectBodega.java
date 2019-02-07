package com.example.imvcol;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.imvcol.Utils.DialogUtils;
import com.example.imvcol.WebserviceConnection.ExecuteRemoteQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class FrmSelectBodega extends AppCompatActivity {
    DialogUtils dialogUtils;
    private Spinner spnBodega;
    private Object[][] wholeBodegas;
    private ArrayList rawBodegas;
    private Usuario currUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_select_bodega);

        Button btnSiguiente = findViewById(R.id.frm_select_bodega_btn_siguiente);
        spnBodega = findViewById(R.id.frm_select_bodega_spn_bodega);

        btnSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<Integer, String> mapBodega = (HashMap<Integer, String>) rawBodegas.get(1);
                if (changeValue(mapBodega.get(spnBodega.getSelectedItemPosition())) == null) {
                    Toast.makeText(FrmSelectBodega.this, "Debe seleccionar una bodega", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        dialogUtils = new DialogUtils(FrmSelectBodega.this, "Cargando");
                        dialogUtils.showDialog(getWindow());
                        SQLiteDatabase db = BaseHelper.getReadable(getApplicationContext());

                        Usuario currUsu = new Usuario(null, null, changeValue(mapBodega.get(spnBodega.getSelectedItemPosition())),
                                null,
                                null,
                                null,
                                null,
                                null, 1);
                        currUsu.updateCurrent(db);
                        currUser = currUsu.selectUsuario(db);
                        getWebserviceProducts(v, db);
                    } catch (Exception e) {
                        Toast.makeText(FrmSelectBodega.this, "Error" + e.getMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }
        });
        try {
            SQLiteDatabase db = BaseHelper.getReadable(this);
            wholeBodegas = new Bodega().selectBodegas(db);
            if (wholeBodegas != null) {
                prepareBodegasSpn();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error/" + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error/" + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private String changeValue(Object object) {
        if (object.toString().equals("-1")) {
            return null;
        } else {
            return object.toString();
        }

    }

    private void prepareBodegasSpn() {
        rawBodegas = ArrayUtils.mapObjects(wholeBodegas);
        String[] dataSpnBodegas = (String[]) rawBodegas.get(0);

        ArrayAdapter<String> adapterBodegas = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, dataSpnBodegas);
        adapterBodegas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnBodega.setAdapter(adapterBodegas);
    }

    private void getWebserviceProducts(final View v, final SQLiteDatabase db) {
        @SuppressLint("StaticFieldLeak") ExecuteRemoteQuery remote = new ExecuteRemoteQuery() {
            @Override
            public void receiveData(Object object) throws Exception {
                ArrayList resultsDatos = (ArrayList) object;
                System.out.println("productos1" + resultsDatos.get(0));
                ArrayList rawProductos = ArrayUtils.convertToArrayList(new JSONArray((String) resultsDatos.get(0)), null, FrmSelectBodega.this);
                if (resultsDatos.get(0).equals("[]")) {
                    dialogUtils.dissmissDialog();
                    BaseHelper.tryClose(db);
                    Toast.makeText(v.getContext(), "No se han podido cargar los datos, intente nuevamente", Toast.LENGTH_LONG).show();
                } else {
                    fillDatabase(rawProductos);
                    dialogUtils.dissmissDialog();
                    Intent i = new Intent(v.getContext(), FrmOpciones.class);
                    //i.putExtra("datos", resultsDatos);
                    startActivityForResult(i, 1);
                    finish();
                }
            }
        };
        remote.setContext(v.getContext());

        ArrayList queryDatos = new ArrayList();

        String query = "SELECT r.codigo,r.descripcion,s.stock,a.alterno,r.grupo,r.subgrupo,r.subgrupo2,r.subgrupo3,r.clase " +
                "FROM v_referencias_sto s " +
                "JOIN referencias r on r.codigo=s.codigo " +
                "JOIN referencias_alt a on r.codigo=a.codigo " +
                "WHERE s.bodega='" + currUser.getCurrBodega() + "' " +
                " AND s.ano=YEAR(getdate()) " +
                " AND s.mes=MONTH(getdate()) ";
        queryDatos.add(query);
        System.out.println("QUERYYYYYY///" + query);
        remote.setQuery(queryDatos);
        remote.execute();
    }
    private void fillDatabase(ArrayList rawProductos) throws JSONException {
        SQLiteDatabase db = BaseHelper.getWritable(this);
        System.out.println("CARGANDOOOOO" + rawProductos.get(0));
        new Producto().delete(db);

        for (int i = 0; i < rawProductos.size(); i++) {
            JSONObject rawProducto = ((JSONObject) rawProductos.get(i));
            Producto producto = new Producto(rawProducto.getString("codigo"),
                    rawProducto.getString("descripcion"),
                    rawProducto.getString("stock"),
                    rawProducto.getString("alterno"),
                    rawProducto.getString("grupo"),
                    rawProducto.getString("subgrupo"),
                    rawProducto.getString("subgrupo2"),
                    rawProducto.getString("subgrupo3"),
                    rawProducto.getString("clase"));
            producto.insert(db);
        }
        BaseHelper.tryClose(db);
    }
}
