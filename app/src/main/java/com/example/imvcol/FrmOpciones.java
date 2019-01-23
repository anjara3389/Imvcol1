package com.example.imvcol;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class FrmOpciones extends AppCompatActivity {

    private ArrayList datos;

    private Spinner spnBodega, spnGrupo, spnSubgrupo, spnSubgrupo3, spnSubgrupo2, spnClase;
    private ArrayList wholeBodegas, wholeGrupos, wholeSubgrupos, wholeSubgrupos3, wholeSubgrupos2, wholeClases;
    private ArrayList rawBodegas, rawGrupos, rawSubgrupos, rawSubgrupos3, rawSubgrupos2, rawClases;
    private String[] dataSpnBodegas, dataSpnGrupos, dataSpnSubgrupos, dataSpnSubgrupos3, dataSpnSubgrupos2, dataSpnClases;
    private ArrayAdapter<String> adapterBodegas, adapterGrupos, adapterSubgrupos, adapterSubgrupos3, adapterSubgrupos2, adapterClases;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_opciones);


        spnBodega = findViewById(R.id.frm_opciones_spn_bodega);
        spnGrupo = findViewById(R.id.frm_opciones_spn_grupo);
        spnSubgrupo = findViewById(R.id.frm_opciones_spn_subgrupo);
        spnSubgrupo2 = findViewById(R.id.frm_opciones_spn_subgrupo_2);
        spnSubgrupo3 = findViewById(R.id.frm_opciones_spn_subgrupo_3);
        spnClase = findViewById(R.id.frm_opciones_spn_clase);

        try {
            Bundle bundle = getIntent().getExtras();
            datos = bundle.getParcelableArrayList("datos");

            JSONObject zeroBodega = new JSONObject();
            zeroBodega.put("BODEGA", "-1");
            zeroBodega.put("DESCRIPCION", "Seleccione una bodega");
            JSONObject zeroGrupo = new JSONObject();
            zeroGrupo.put("grupo", "-1");
            zeroGrupo.put("descripcion", "Seleccione un grupo");
            JSONObject zeroSubgrupo = new JSONObject();
            zeroSubgrupo.put("grupo", "-1");
            zeroSubgrupo.put("subgrupo", "-1");
            zeroSubgrupo.put("descripcion", "Seleccione un subgrupo");
            JSONObject zeroSubgrupo2 = new JSONObject();
            zeroSubgrupo2.put("grupo", -1);
            zeroSubgrupo2.put("subgrupo", -1);
            zeroSubgrupo2.put("subgrupo2", -1);
            zeroSubgrupo2.put("descripcion", "Seleccione un subgrupo2");
            JSONObject zeroSubgrupo3 = new JSONObject();
            zeroSubgrupo3.put("grupo", -1);
            zeroSubgrupo3.put("subgrupo", -1);
            zeroSubgrupo3.put("subgrupo2", -1);
            zeroSubgrupo3.put("subgrupo3", -1);
            zeroSubgrupo3.put("descripcion", "Seleccione un subgrupo3");
            JSONObject zeroClase = new JSONObject();
            zeroClase.put("clase", -1);
            zeroClase.put("descripcion", "Seleccione una clase");


            wholeBodegas = ArrayUtils.convertToArrayList(new JSONArray((String) datos.get(0)), zeroBodega);
            wholeGrupos = ArrayUtils.convertToArrayList(new JSONArray((String) datos.get(1)), zeroGrupo);
            wholeSubgrupos = ArrayUtils.convertToArrayList(new JSONArray((String) datos.get(2)), zeroSubgrupo);
            wholeSubgrupos2 = ArrayUtils.convertToArrayList(new JSONArray((String) datos.get(3)), zeroSubgrupo2);
            wholeSubgrupos3 = ArrayUtils.convertToArrayList(new JSONArray((String) datos.get(4)), zeroSubgrupo3);
            System.out.println("wholeSubgrupos3" + wholeSubgrupos3);
            wholeClases = ArrayUtils.convertToArrayList(new JSONArray((String) datos.get(5)), zeroClase);

            prepareBodegas();
            prepareClases();
            prepareGrupos();

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error/" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void prepareBodegas() {

        rawBodegas = ArrayUtils.mapObjects("BODEGA", "DESCRIPCION", wholeBodegas);
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
        rawGrupos = ArrayUtils.mapObjects("grupo", "descripcion", wholeGrupos);
        dataSpnGrupos = (String[]) rawGrupos.get(0);

        adapterGrupos = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, dataSpnGrupos);
        adapterGrupos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnGrupo.setAdapter(adapterGrupos);

        spnGrupo.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> spn, android.view.View v, int posicion, long id) {
                        try {
                            /*Toast.makeText(spn.getContext(), "Has seleccionado " +
                                            spn.getItemAtPosition(posicion).toString() + ((HashMap<Integer, String>) rawGrupos.get(1)).get(spnGrupo.getSelectedItemPosition()),
                                    Toast.LENGTH_LONG).show();*/
                            //String name = spnGrupo.getSelectedItem().toString();
                            //String id = spinnerMap.get(spnGrupo.getSelectedItemPosition());
                            prepareSubgrupos(((HashMap<Integer, String>) rawGrupos.get(1)).get(spnGrupo.getSelectedItemPosition()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    public void onNothingSelected(AdapterView<?> spn) {
                    }
                });

    }

    private void prepareSubgrupos(final String grupoId) throws JSONException {
        ArrayList selected = new ArrayList();

        for (int i = 0; i < wholeSubgrupos.size(); i++) {
            JSONObject subgrupo = ((JSONObject) wholeSubgrupos.get(i));
            if (subgrupo.getString("subgrupo").equals("-1") || subgrupo.getString("grupo").equals(grupoId)) {
                selected.add(wholeSubgrupos.get(i));
            }
        }

        rawSubgrupos = ArrayUtils.mapObjects("subgrupo", "descripcion", selected);
        dataSpnSubgrupos = (String[]) rawSubgrupos.get(0);

        adapterSubgrupos = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, dataSpnSubgrupos);
        adapterSubgrupos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSubgrupo.setAdapter(adapterSubgrupos);

        spnSubgrupo.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> spn, android.view.View v, int posicion, long id) {
                        try {
                            prepareSubgrupos2(grupoId, ((HashMap<Integer, String>) rawSubgrupos.get(1)).get(spnSubgrupo.getSelectedItemPosition()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //String name = spnSubgrupo.getSelectedItem().toString();
                        //String id = spinnerMap.get(spnSubgrupo.getSelectedItemPosition());
                    }

                    public void onNothingSelected(AdapterView<?> spn) {
                    }
                });

    }

    private void prepareSubgrupos2(final String grupoId, final String subgrupoId) throws JSONException {

        ArrayList selected = new ArrayList();

        for (int i = 0; i < wholeSubgrupos2.size(); i++) {
            JSONObject subgrupo2 = (JSONObject) wholeSubgrupos2.get(i);
            if (subgrupo2.getString("subgrupo2").equals("-1") ||
                    (subgrupo2.getString("grupo").equals(grupoId) && subgrupo2.getString("subgrupo").equals(subgrupoId))) {
                selected.add(wholeSubgrupos2.get(i));
            }
        }

        rawSubgrupos2 = ArrayUtils.mapObjects("subgrupo2", "descripcion", selected);
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
                        }
                    }

                    public void onNothingSelected(AdapterView<?> spn) {
                    }
                });

    }

    private void prepareSubgrupos3(String grupoId, String subgrupoId, String subgrupo2Id) throws JSONException {
        ArrayList selected = new ArrayList();
        for (int i = 0; i < wholeSubgrupos3.size(); i++) {
            JSONObject subgrupo3 = (JSONObject) wholeSubgrupos3.get(i);
            if (subgrupo3.getString("subgrupo3").equals("-1")||(subgrupo3.getString("grupo").equals(grupoId) &&
                    (subgrupo3.getString("subgrupo").equals(subgrupoId) &&
                            subgrupo3.getString("subgrupo2").equals(subgrupo2Id)))) {
                selected.add(wholeSubgrupos3.get(i));
            }
        }
        rawSubgrupos3 = ArrayUtils.mapObjects("subgrupo3", "descripcion", selected);
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

        rawClases = ArrayUtils.mapObjects("clase", "descripcion", wholeClases);
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
                        //String id = spinnerMap.get(spnClase.getSelectedItemPosition());
                    }

                    public void onNothingSelected(AdapterView<?> spn) {
                    }
                });

    }
}
