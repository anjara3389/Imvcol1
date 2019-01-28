package com.example.imvcol;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class FrmOpciones extends AppCompatActivity {

    private ArrayList datos;

    private Button btnAceptar;
    private Spinner spnBodega, spnGrupo, spnSubgrupo, spnSubgrupo3, spnSubgrupo2, spnClase;
    private Object[][] wholeBodegas, wholeGrupos, wholeSubgrupos, wholeSubgrupos3, wholeSubgrupos2, wholeClases;
    private ArrayList rawBodegas, rawGrupos, rawSubgrupos, rawSubgrupos3, rawSubgrupos2, rawClases;
    private String[] dataSpnBodegas, dataSpnGrupos, dataSpnSubgrupos, dataSpnSubgrupos3, dataSpnSubgrupos2, dataSpnClases;
    private ArrayAdapter<String> adapterBodegas, adapterGrupos, adapterSubgrupos, adapterSubgrupos3, adapterSubgrupos2, adapterClases;

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
                    SQLiteDatabase db = BaseHelper.getReadable(getApplicationContext());

                    Usuario currUsu = new Usuario(null, null, changeValue(mapBodega.get(spnBodega.getSelectedItemPosition())),
                            changeValue(mapGrupo.get(spnGrupo.getSelectedItemPosition())),
                            changeValue(mapSubgrupo.get(spnSubgrupo.getSelectedItemPosition())),
                            changeValue(mapSubgrupo2.get(spnSubgrupo2.getSelectedItemPosition())),
                            changeValue(mapSubgrupo3.get(spnSubgrupo3.getSelectedItemPosition())),
                            changeValue(mapClase.get(spnClase.getSelectedItemPosition())));
                    currUsu.updateCurrent(db);
                    try {
                        System.out.println("AQUIIIIIIII/////"+currUsu.selectUsuario(db)[0]);
                        System.out.println("AQUIIIIIIII/////"+currUsu.selectUsuario(db)[1]);
                        System.out.println("AQUIIIIIIII/////"+currUsu.selectUsuario(db)[2]);
                        System.out.println("AQUIIIIIIII/////"+currUsu.selectUsuario(db)[3]);
                        System.out.println("AQUIIIIIIII/////"+currUsu.selectUsuario(db)[4]);
                        System.out.println("AQUIIIIIIII/////"+currUsu.selectUsuario(db)[5]);
                        System.out.println("AQUIIIIIIII/////"+currUsu.selectUsuario(db)[6]);
                        System.out.println("AQUIIIIIIII/////"+currUsu.selectUsuario(db)[7]);


                    } catch (Exception e) {
                        System.out.println("AQUIIIIIIII/////NOOOOOOOOOOOOOOO");
                        e.printStackTrace();
                    }
                    BaseHelper.tryClose(db);



                    Intent i = new Intent(v.getContext(), FrmInventario.class);
                    //i.putExtra("datos", resultsDatos);
                    startActivityForResult(i, 1);
                }
            }
        });

        try {
            Bundle bundle = getIntent().getExtras();
            datos = bundle.getParcelableArrayList("datos");


            wholeBodegas = (Object[][]) datos.get(0);
            wholeGrupos = (Object[][]) datos.get(1);
            wholeSubgrupos = (Object[][]) datos.get(2);
            wholeSubgrupos2 = (Object[][]) datos.get(3);
            wholeSubgrupos3 = (Object[][]) datos.get(4);
            wholeClases = (Object[][]) datos.get(5);
            System.out.println("BODEGAS/////////////////////" + wholeBodegas[0][0]);
            System.out.println("BODEGAS/////////////////////" + wholeBodegas[0][1]);

            prepareBodegas();
            prepareClases();
            prepareGrupos();

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error/" + e.getMessage(), Toast.LENGTH_LONG).show();
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


}
