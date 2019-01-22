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

    private Spinner spnBodega;
    private ArrayList wholeBodegas;
    private ArrayList wholeGrupos;
    private ArrayList wholeSubgrupos;
    private ArrayList wholeSubgrupos1;
    private ArrayList wholeSubgrupos2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_opciones);


        spnBodega = findViewById(R.id.frm_opciones_spn_bodega);


        try {
            Bundle bundle = getIntent().getExtras();
            ArrayList datos = bundle.getParcelableArrayList("datos");

            ArrayList wholeBodegas = ArrayUtils.convertToArrayList(new JSONArray((String) datos.get(0)));
            final ArrayList rawBodegas = ArrayUtils.mapObjects("BODEGA", "DESCRIPCION", wholeBodegas);
            String[] spnBodegas = (String[]) rawBodegas.get(0);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, spnBodegas);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnBodega.setAdapter(adapter);


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


            ArrayList wholeGrupos = ArrayUtils.convertToArrayList(new JSONArray((String) datos.get(1)));
            ArrayList wholeSubgrupos = ArrayUtils.convertToArrayList(new JSONArray((String) datos.get(2)));
            ArrayList wholeSubgrupos1 = ArrayUtils.convertToArrayList(new JSONArray((String) datos.get(3)));
            ArrayList wholeSubgrupos2 = ArrayUtils.convertToArrayList(new JSONArray((String) datos.get(4)));

            //spnBodega.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, bodegas));
            //System.out.println("BODEGAS" + bodegas.get(0));
            //System.out.println("grupos" + grupos.get(0));
            //System.out.println("subgrupos" + subgrupos.get(0));
            //System.out.println("subgrupos1" + subgrupos1.get(0));
            //System.out.println("subgrupos2" + subgrupos2.get(0));


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
