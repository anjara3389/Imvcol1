package com.example.imvcol;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
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

public class FrmOpciones extends AppCompatActivity {

    private ArrayList datos;
    DialogUtils dialogUtils;

    private Button btnAceptar;
    private Spinner spnBodega, spnGrupo, spnSubgrupo, spnSubgrupo3, spnSubgrupo2, spnClase;
    private Object[][] wholeBodegas, wholeGrupos, wholeSubgrupos, wholeSubgrupos3, wholeSubgrupos2, wholeClases;
    private ArrayList rawBodegas, rawGrupos, rawSubgrupos, rawSubgrupos3, rawSubgrupos2, rawClases;
    private String[] dataSpnBodegas, dataSpnGrupos, dataSpnSubgrupos, dataSpnSubgrupos3, dataSpnSubgrupos2, dataSpnClases;
    private ArrayAdapter<String> adapterBodegas, adapterGrupos, adapterSubgrupos, adapterSubgrupos3, adapterSubgrupos2, adapterClases;
    private Usuario currUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_opciones);

        btnAceptar = findViewById(R.id.frm_opciones_btn_aceptar);
        spnBodega = findViewById(R.id.frm_opciones_spn_bodega);
        spnGrupo = findViewById(R.id.frm_opciones_spn_grupo);
        spnSubgrupo = findViewById(R.id.frm_opciones_spn_subgrupo);
        spnSubgrupo2 = findViewById(R.id.frm_opciones_spn_subgrupo_2);
        spnSubgrupo3 = findViewById(R.id.frm_opciones_spn_subgrupo_3);
        spnClase = findViewById(R.id.frm_opciones_spn_clase);

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<Integer, String> mapBodega = (HashMap<Integer, String>) rawBodegas.get(1);
                HashMap<Integer, String> mapGrupo = (HashMap<Integer, String>) rawGrupos.get(1);
                HashMap<Integer, String> mapSubgrupo = (HashMap<Integer, String>) rawSubgrupos.get(1);
                HashMap<Integer, String> mapSubgrupo2 = (HashMap<Integer, String>) rawSubgrupos2.get(1);
                HashMap<Integer, String> mapSubgrupo3 = (HashMap<Integer, String>) rawSubgrupos3.get(1);
                HashMap<Integer, String> mapClase = (HashMap<Integer, String>) rawClases.get(1);
                //Toast.makeText(FrmOpciones.this, spnBodega.getSelectedItem() + " ------" + spnBodega.getSelectedItemId(), Toast.LENGTH_LONG).show();
                if (changeValue(mapBodega.get(spnBodega.getSelectedItemPosition())) == null) {
                    Toast.makeText(FrmOpciones.this, "Debe seleccionar una bodega", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        dialogUtils = new DialogUtils(FrmOpciones.this, "Cargando");
                        dialogUtils.showDialog(getWindow());
                        SQLiteDatabase db = BaseHelper.getReadable(getApplicationContext());

                        Usuario currUsu = new Usuario(null, null, changeValue(mapBodega.get(spnBodega.getSelectedItemPosition())),
                                changeValue(mapGrupo.get(spnGrupo.getSelectedItemPosition())),
                                changeValue(mapSubgrupo.get(spnSubgrupo.getSelectedItemPosition())),
                                changeValue(mapSubgrupo2.get(spnSubgrupo2.getSelectedItemPosition())),
                                changeValue(mapSubgrupo3.get(spnSubgrupo3.getSelectedItemPosition())),
                                changeValue(mapClase.get(spnClase.getSelectedItemPosition())), 1);
                        currUsu.updateCurrent(db);


                        currUser = currUsu.selectUsuario(db);
                        getWebserviceProducts(v, db);
                    } catch (Exception e) {
                        Toast.makeText(FrmOpciones.this, "Error" + e.getMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }
        });
        try {
            SQLiteDatabase db = BaseHelper.getReadable(this);
            wholeBodegas = new Bodega().selectBodegas(db);
            wholeGrupos = new Grupo().selectGrupos(db);
            wholeSubgrupos = new Subgrupo().selectSubgrupos(db);
            wholeSubgrupos2 = new Subgrupo2().selectSubgrupos2(db);
            wholeSubgrupos3 = new Subgrupo3().selectSubgrupos3(db);
            wholeClases = new Clase().selectClases(db);
//            System.out.println("wholeClases/////////////////////" + wholeClases[0][0]);
            //          System.out.println("wholeClases/////////////////////" + wholeClases[0][1]);
            if (wholeBodegas != null && wholeGrupos != null && wholeSubgrupos != null) {
                prepareBodegas();
                prepareClases();
                prepareGrupos();
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

    private void prepareBodegas() {
        rawBodegas = ArrayUtils.mapObjects(wholeBodegas);
        dataSpnBodegas = (String[]) rawBodegas.get(0);

        adapterBodegas = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, dataSpnBodegas);
        adapterBodegas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnBodega.setAdapter(adapterBodegas);

        spnBodega.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> spn, android.view.View v, int posicion, long id) {
                        /*Toast.makeText(spn.getContext(), "Has seleccionado " +
                                        spn.getItemAtPosition(posicion).toString() + ((HashMap<Integer, String>) rawBodegas.get(1)).get(spnBodega.getSelectedItemPosition()),
                                Toast.LENGTH_LONG).show();*/
                        //String name = spnBodega.getSelectedItem().toString();
                        //String id = spinnerMap.get(spnBodega.getSelectedItemPosition());
                    }

                    public void onNothingSelected(AdapterView<?> spn) {
                    }
                });

    }

    private void prepareGrupos() {
        rawGrupos = ArrayUtils.mapObjects(wholeGrupos);
        dataSpnGrupos = (String[]) rawGrupos.get(0);

        adapterGrupos = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, dataSpnGrupos);
        adapterGrupos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnGrupo.setAdapter(adapterGrupos);

        spnGrupo.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> spn, android.view.View v, int posicion, long id) {
                            /*Toast.makeText(spn.getContext(), "Has seleccionado " +
                                            spn.getItemAtPosition(posicion).toString() + ((HashMap<Integer, String>) rawGrupos.get(1)).get(spnGrupo.getSelectedItemPosition()),
                                    Toast.LENGTH_LONG).show();*/
                        //String name = spnGrupo.getSelectedItem().toString();
                        //String id = spinnerMap.get(spnGrupo.getSelectedItemPosition());
                        prepareSubgrupos(((HashMap<Integer, String>) rawGrupos.get(1)).get(spnGrupo.getSelectedItemPosition()));

                    }

                    public void onNothingSelected(AdapterView<?> spn) {
                    }
                });
    }

    private void prepareSubgrupos(final String grupoId) {
        ArrayList selected = new ArrayList();

        for (int i = 0; i < wholeSubgrupos.length; i++) {
            Object[] subgrupo = wholeSubgrupos[i];
            if (subgrupo[0].equals("-1") || subgrupo[2].equals(grupoId)) {
                selected.add(subgrupo);
            }
        }

        rawSubgrupos = ArrayUtils.mapObjects(selected);
        dataSpnSubgrupos = (String[]) rawSubgrupos.get(0);

        adapterSubgrupos = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, dataSpnSubgrupos);
        adapterSubgrupos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSubgrupo.setAdapter(adapterSubgrupos);

        spnSubgrupo.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> spn, android.view.View v, int posicion, long id) {
                        prepareSubgrupos2(grupoId, ((HashMap<Integer, String>) rawSubgrupos.get(1)).get(spnSubgrupo.getSelectedItemPosition()));
                        //String name = spnSubgrupo.getSelectedItem().toString();
                        //String id = spinnerMap.get(spnSubgrupo.getSelectedItemPosition());
                    }

                    public void onNothingSelected(AdapterView<?> spn) {
                    }
                });

    }

    private void prepareSubgrupos2(final String grupoId, final String subgrupoId) {

        ArrayList selected = new ArrayList();

        for (int i = 0; i < wholeSubgrupos2.length; i++) {
            Object[] subgrupo2 = wholeSubgrupos2[i];
            if (subgrupo2[0].equals("-1") || (subgrupo2[2].equals(grupoId) && subgrupo2[3].equals(subgrupoId))) {
                selected.add(subgrupo2);
            }
        }

        rawSubgrupos2 = ArrayUtils.mapObjects(selected);
        dataSpnSubgrupos2 = (String[]) rawSubgrupos2.get(0);

        adapterSubgrupos2 = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, dataSpnSubgrupos2);
        adapterSubgrupos2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSubgrupo2.setAdapter(adapterSubgrupos2);

        spnSubgrupo2.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> spn, android.view.View v, int posicion, long id) {
                        try {
                           /* Toast.makeText(spn.getContext(), "Has seleccionado " +
                                            spn.getItemAtPosition(posicion).toString() + ((HashMap<Integer, String>) rawSubgrupos2.get(1)).get(spnSubgrupo2.getSelectedItemPosition()),
                                    Toast.LENGTH_LONG).show();*/
                            //String name = spnBodega.getSelectedItem().toString();
                            //String id = spinnerMap.get(spnBodega.getSelectedItemPosition());
                            prepareSubgrupos3(grupoId, subgrupoId, ((HashMap<Integer, String>) rawSubgrupos2.get(1)).get(spnSubgrupo2.getSelectedItemPosition()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Error/" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    public void onNothingSelected(AdapterView<?> spn) {
                    }
                });

    }

    private void prepareSubgrupos3(String grupoId, String subgrupoId, String subgrupo2Id) throws JSONException {
        ArrayList selected = new ArrayList();

        for (int i = 0; i < wholeSubgrupos3.length; i++) {
            Object[] subgrupo3 = wholeSubgrupos3[i];
            if (subgrupo3[0].equals("-1") || (subgrupo3[2].equals(grupoId) && (subgrupo3[3].equals(subgrupoId) && subgrupo3[4].equals(subgrupo2Id)))) {
                selected.add(subgrupo3);
            }
        }
        rawSubgrupos3 = ArrayUtils.mapObjects(selected);
        dataSpnSubgrupos3 = (String[]) rawSubgrupos3.get(0);

        adapterSubgrupos3 = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, dataSpnSubgrupos3);
        adapterSubgrupos3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSubgrupo3.setAdapter(adapterSubgrupos3);

        spnSubgrupo3.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> spn, android.view.View v, int posicion, long id) {
                        /*Toast.makeText(spn.getContext(), "Has seleccionado " +
                                        spn.getItemAtPosition(posicion).toString() + ((HashMap<Integer, String>) rawSubgrupos3.get(1)).get(spnSubgrupo3.getSelectedItemPosition()),
                                Toast.LENGTH_LONG).show();*/
                        //String name = spnBodega.getSelectedItem().toString();
                        //String id = spinnerMap.get(spnBodega.getSelectedItemPosition());
                    }

                    public void onNothingSelected(AdapterView<?> spn) {
                    }
                });

    }

    private void prepareClases() throws JSONException {

        rawClases = ArrayUtils.mapObjects(wholeClases);
        dataSpnClases = (String[]) rawClases.get(0);

        adapterClases = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, dataSpnClases);
        adapterClases.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnClase.setAdapter(adapterClases);

        spnClase.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> spn, android.view.View v, int posicion, long id) {
                        Toast.makeText(spn.getContext(), "Has seleccionado " +
                                        spn.getItemAtPosition(posicion).toString() + ((HashMap<Integer, String>) rawClases.get(1)).get(spnClase.getSelectedItemPosition()),
                                Toast.LENGTH_LONG).show();
                        //String name = spnClase.getSelectedItem().toString();

                    }

                    public void onNothingSelected(AdapterView<?> spn) {
                    }
                });

    }

    private void getWebserviceProducts(final View v, final SQLiteDatabase db) {
        @SuppressLint("StaticFieldLeak") ExecuteRemoteQuery remote = new ExecuteRemoteQuery() {
            @Override
            public void receiveData(Object object) throws Exception {
                ArrayList resultsDatos = (ArrayList) object;
                System.out.println("productos1" + resultsDatos.get(0));
                ArrayList rawProductos = ArrayUtils.convertToArrayList(new JSONArray((String) resultsDatos.get(0)), null, FrmOpciones.this);
                if (resultsDatos.get(0).equals("[]")) {
                    dialogUtils.dissmissDialog();
                    BaseHelper.tryClose(db);
                    Toast.makeText(v.getContext(), "No se han podido cargar los datos, intente nuevamente", Toast.LENGTH_LONG).show();
                } else {
                    fillDatabase(rawProductos);
                    dialogUtils.dissmissDialog();
                    Intent i = new Intent(v.getContext(), FrmInventario.class);
                    //i.putExtra("datos", resultsDatos);
                    startActivityForResult(i, 1);
                    finish();
                }
            }
        };
        remote.setContext(v.getContext());

        ArrayList queryDatos = new ArrayList();

        String query = "SELECT r.codigo,r.descripcion,s.stock,a.alterno " +
                "FROM v_referencias_sto s " +
                "JOIN referencias r on r.codigo=s.codigo " +
                "JOIN referencias_alt a on r.codigo=a.codigo " +
                "WHERE s.bodega='" + currUser.getCurrBodega() + "' " +
                " AND s.ano=YEAR(getdate()) " +
                " AND s.mes=MONTH(getdate()) ";

        if (currUser.getCurrGrupo() != null) {
            query += "AND r.grupo='" + currUser.getCurrGrupo() + "' ";
        }
        if (currUser.getCurrSubgr() != null) {
            query += "AND r.subgrupo='" + currUser.getCurrSubgr() + "' ";
        }
        if (currUser.getCurrSubgr2() != null) {
            query += "AND r.subgrupo2='" + currUser.getCurrSubgr2() + "' ";
        }
        if (currUser.getCurrSubgr3() != null) {
            query += "AND r.subgrupo3='" + currUser.getCurrSubgr3() + "' ";
        }
        if (currUser.getCurrClase() != null) {
            query += "AND r.clase='" + currUser.getCurrClase() + "'";
        }

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
            Producto producto = new Producto(rawProducto.getString("codigo"), rawProducto.getString("descripcion"), rawProducto.getString("stock"), rawProducto.getString("alterno"));
            producto.insert(db);
        }
        BaseHelper.tryClose(db);
    }
}
