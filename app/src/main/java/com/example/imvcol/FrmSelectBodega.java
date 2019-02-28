package com.example.imvcol;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.imvcol.Utils.DialogUtils;
import com.example.imvcol.Utils.NetUtils;
import com.example.imvcol.WebserviceConnection.ExecuteRemoteQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class FrmSelectBodega extends AppCompatActivity implements YesNoDialogFragment.MyDialogDialogListener {
    DialogUtils dialogUtils;
    private Spinner spnBodega;
    private Spinner spnModo;
    private Object[][] wholeBodegas;
    private ArrayList rawBodegas;
    private Usuario currUser;
    private static final int FINALIZAR_INVENTARIO = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_select_bodega);

        Button btnSiguiente = findViewById(R.id.frm_select_bodega_btn_siguiente);
        spnBodega = findViewById(R.id.frm_select_bodega_spn_bodega);
        spnModo = findViewById(R.id.frm_select_bodega_spn_modo);

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
                        currUser = new Usuario().selectUsuario(db);
                        currUser.setCurrBodega(mapBodega.get(spnBodega.getSelectedItemPosition()));
                        currUser.setCurrConteo(1);

                        System.out.println("POSICIÓN!!!!!!!!" + spnModo.getSelectedItemPosition());
                        currUser.setModo(spnModo.getSelectedItemPosition());
                        currUser.setDatosEnviados(false);
                        currUser.updateCurrent(db);

                        getWebserviceProducts(v, db);
                    } catch (Exception e) {
                        Toast.makeText(FrmSelectBodega.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }
        });
        try {
            SQLiteDatabase db = BaseHelper.getReadable(this);
            wholeBodegas = new Bodega().selectBodegas(db);
            if (wholeBodegas != null) {
                prepareSpinners();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
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

    private void prepareSpinners() {
        rawBodegas = ArrayUtils.mapObjects(wholeBodegas);
        String[] dataSpnBodegas = (String[]) rawBodegas.get(0);

        ArrayAdapter<String> adapterBodegas = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, dataSpnBodegas);
        adapterBodegas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnBodega.setAdapter(adapterBodegas);

        String[] dataSpnModo = new String[2];
        dataSpnModo[0] = "Listado";
        dataSpnModo[1] = "Código de Barras";

        ArrayAdapter<String> adapterModo = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, dataSpnModo);
        adapterModo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnModo.setAdapter(adapterModo);
    }

    private void getWebserviceProducts(final View v, final SQLiteDatabase db) throws Exception {
        if (!NetUtils.isOnlineNet(FrmSelectBodega.this)) {
            dialogUtils.dissmissDialog();
            throw new Exception("No hay conexión a internet");
        } else {
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
                        fillProductsOnDatabase(rawProductos);
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
                    "FROM referencias_fis F " +
                    "JOIN referencias r on r.codigo=F.codigo " +
                    "JOIN v_referencias_sto s on f.codigo=s.codigo AND F.bodega=s.bodega " +
                    "LEFT JOIN referencias_alt a on r.codigo=a.codigo " +
                    "WHERE s.bodega='" + currUser.getCurrBodega() + "' " +
                    "AND s.ano=YEAR(getdate()) " +
                    "AND s.mes=MONTH(getdate()) " +
                    "AND (a.cantidad_alt=1 OR a.cantidad_alt IS NULL) ";
            queryDatos.add(query);
            System.out.println("QUERYYYYYY///" + query);
            remote.setQuery(queryDatos);
            remote.execute();
        }
    }

    private void fillProductsOnDatabase(ArrayList rawProductos) throws JSONException {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action_bar, menu);

        menu.findItem(R.id.action_diferencias).setVisible(false);
        menu.findItem(R.id.action_enviar_datos).setVisible(false);
        menu.findItem(R.id.action_finalizar_conteo).setVisible(false);
        menu.findItem(R.id.action_liberar_seleccion).setVisible(false);
        menu.findItem(R.id.action_totales).setVisible(false);
        setTitle("INVFISCOL 1.0");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_finalizar_inventario:
                YesNoDialogFragment dial2 = new YesNoDialogFragment();
                dial2.setInfo(FrmSelectBodega.this, FrmSelectBodega.this, "Finalizar inventario", "¿Está seguro de finalizar el inventario? Los datos que no se hayan enviado se perderán. ", FINALIZAR_INVENTARIO);
                dial2.show(getSupportFragmentManager(), "MyDialog");
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onFinishDialog(boolean ans, int code) {
        if (code == FINALIZAR_INVENTARIO) {
            try {
                SQLiteDatabase db = BaseHelper.getWritable(this);
                new Usuario().delete(db);
                new Inventario().delete(db);
                new Bodega().delete(db);
                new Producto().delete(db);
                new Grupo().delete(db);
                new Subgrupo().delete(db);
                new Subgrupo2().delete(db);
                new Subgrupo3().delete(db);
                new Clase().delete(db);
                BaseHelper.tryClose(db);
                //dialogUtils.dissmissDialog();

                Intent i = new Intent(FrmSelectBodega.this, FrmLogin.class);
                startActivityForResult(i, 1);
                BaseHelper.tryClose(db);
                Toast.makeText(FrmSelectBodega.this, "El inventario finalizó exitosamente", Toast.LENGTH_LONG).show();
                finish();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error: " + e, Toast.LENGTH_LONG);
            }
        }
    }
}
