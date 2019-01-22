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

import java.lang.reflect.Array;
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

            wholeBodegas = ArrayUtils.convertToArrayList(new JSONArray((String) datos.get(0)));
            wholeGrupos = ArrayUtils.convertToArrayList(new JSONArray((String) datos.get(1)));
            wholeSubgrupos = ArrayUtils.convertToArrayList(new JSONArray((String) datos.get(2)));
            wholeSubgrupos2 = ArrayUtils.convertToArrayList(new JSONArray((String) datos.get(3)));


            prepareBodegas();
            prepareGrupos();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void prepareBodegas() throws JSONException {

        rawBodegas = ArrayUtils.mapObjects("BODEGA", "DESCRIPCION", wholeBodegas);
        dataSpnBodegas = (String[]) rawBodegas.get(0);

        adapterBodegas = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, dataSpnBodegas);
        adapterBodegas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnBodega.setAdapter(adapterBodegas);

        spnBodega.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> spn, android.view.View v, int posicion, long id) {
                        Toast.makeText(spn.getContext(), "Has seleccionado " +
                                        spn.getItemAtPosition(posicion).toString() + ((HashMap<Integer, String>) rawBodegas.get(1)).get(spnBodega.getSelectedItemPosition()),
                                Toast.LENGTH_LONG).show();
                        //String name = spnBodega.getSelectedItem().toString();
                        //String id = spinnerMap.get(spnBodega.getSelectedItemPosition());
                    }

                    public void onNothingSelected(AdapterView<?> spn) {
                    }
                });

    }

    private void prepareGrupos() throws JSONException {
        rawGrupos = ArrayUtils.mapObjects("grupo", "descripcion", wholeGrupos);
        dataSpnGrupos = (String[]) rawGrupos.get(0);

        adapterGrupos = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, dataSpnGrupos);
        adapterGrupos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnGrupo.setAdapter(adapterGrupos);

        spnGrupo.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> spn, android.view.View v, int posicion, long id) {
                        try {
                            Toast.makeText(spn.getContext(), "Has seleccionado " +
                                            spn.getItemAtPosition(posicion).toString() + ((HashMap<Integer, String>) rawGrupos.get(1)).get(spnGrupo.getSelectedItemPosition()),
                                    Toast.LENGTH_LONG).show();
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
            if (((JSONObject) wholeSubgrupos.get(i)).getString("grupo").equals(grupoId)) {
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

    private void prepareSubgrupos2(String grupoId, String subgrupoId) throws JSONException {

        ArrayList selected = new ArrayList();

        for (int i = 0; i < wholeSubgrupos2.size(); i++) {
            if (((JSONObject) wholeSubgrupos2.get(i)).getString("grupo").equals(grupoId)&&((JSONObject) wholeSubgrupos2.get(i)).getString("subgrupo").equals(subgrupoId)) {
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
                        Toast.makeText(spn.getContext(), "Has seleccionado " +
                                        spn.getItemAtPosition(posicion).toString() + ((HashMap<Integer, String>) rawSubgrupos2.get(1)).get(spnSubgrupo2.getSelectedItemPosition()),
                                Toast.LENGTH_LONG).show();
                        //String name = spnBodega.getSelectedItem().toString();
                        //String id = spinnerMap.get(spnBodega.getSelectedItemPosition());
                    }

                    public void onNothingSelected(AdapterView<?> spn) {
                    }
                });

    }

    private void prepareSubgrupos3() throws JSONException {
        wholeSubgrupos3 = ArrayUtils.convertToArrayList(new JSONArray((String) datos.get(4)));
        rawSubgrupos3 = ArrayUtils.mapObjects("BODEGA", "SUBGRUPO3", wholeSubgrupos2);
        dataSpnSubgrupos3 = (String[]) rawSubgrupos3.get(0);

        adapterSubgrupos3 = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, dataSpnSubgrupos2);
        adapterSubgrupos3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSubgrupo3.setAdapter(adapterSubgrupos3);

        spnSubgrupo3.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> spn, android.view.View v, int posicion, long id) {
                        Toast.makeText(spn.getContext(), "Has seleccionado " +
                                        spn.getItemAtPosition(posicion).toString() + ((HashMap<Integer, String>) rawSubgrupos3.get(1)).get(spnSubgrupo3.getSelectedItemPosition()),
                                Toast.LENGTH_LONG).show();
                        //String name = spnBodega.getSelectedItem().toString();
                        //String id = spinnerMap.get(spnBodega.getSelectedItemPosition());
                    }

                    public void onNothingSelected(AdapterView<?> spn) {
                    }
                });

    }

    private void prepareClases() throws JSONException {
        wholeClases = ArrayUtils.convertToArrayList(new JSONArray((String) datos.get(5)));
        rawClases = ArrayUtils.mapObjects("Clase", "DESCRIPCION", wholeClases);
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
