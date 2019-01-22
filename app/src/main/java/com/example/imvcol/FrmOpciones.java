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

    private Spinner spnBodega;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_opciones);


        spnBodega = findViewById(R.id.frm_opciones_spn_bodega);


        try {
            Bundle bundle = getIntent().getExtras();
            ArrayList datos = bundle.getParcelableArrayList("datos");


            ArrayList bodegas = ArrayUtils.convert(new JSONArray((String) datos.get(0)));
            ArrayList grupos = ArrayUtils.convert(new JSONArray((String) datos.get(1)));
            ArrayList subgrupos = ArrayUtils.convert(new JSONArray((String) datos.get(2)));
            ArrayList subgrupos1 = ArrayUtils.convert(new JSONArray((String) datos.get(3)));
            ArrayList subgrupos2 = ArrayUtils.convert(new JSONArray((String) datos.get(4)));

            //spnBodega.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, bodegas));
            System.out.println("BODEGAS" + bodegas.get(0));
            System.out.println("grupos" + grupos.get(0));
            System.out.println("subgrupos" + subgrupos.get(0));
            System.out.println("subgrupos1" + subgrupos1.get(0));
            System.out.println("subgrupos2" + subgrupos2.get(0));

            String[] spinnerArray = new String[bodegas.size()];
            final HashMap<Integer, String> spinnerMap = new HashMap<Integer, String>();
            for (int i = 0; i < bodegas.size(); i++) {
                spinnerMap.put(i, ((JSONObject) bodegas.get(i)).getString("BODEGA"));
                spinnerArray[i] = ((JSONObject) bodegas.get(i)).getString("DESCRIPCION");
            }



            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, spinnerArray);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnBodega.setAdapter(adapter);

            //String name = spnBodega.getSelectedItem().toString();
            //String id = spinnerMap.get(spnBodega.getSelectedItemPosition());

            spnBodega.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> spn, android.view.View v, int posicion, long id) {
                            Toast.makeText(spn.getContext(), "Has seleccionado " +
                                            spn.getItemAtPosition(posicion).toString() + spinnerMap.get(spnBodega.getSelectedItemPosition()),
                                    Toast.LENGTH_LONG).show();
                        }

                        public void onNothingSelected(AdapterView<?> spn) {
                        }
                    });


            //Toast.makeText(this.getApplicationContext(),"AQUÍ"+bodegas.get(0),Toast.LENGTH_LONG).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
