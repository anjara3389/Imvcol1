package com.example.imvcol;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class FrmInventarios extends AppCompatActivity {

    private ListView listaTotales;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_inventarios);

        listaTotales = findViewById(R.id.frm_inventarios_list);
        try {
            SQLiteDatabase db = BaseHelper.getReadable(this);
            Object[][] inventarios = new Inventario().selectInventariosTotales(db, true);
            BaseHelper.tryClose(db);

            final ArrayList<String> nombres = new ArrayList();

            for (int i = 0; i < inventarios.length; i++) {
                nombres.add(inventarios[i][0] + "-" + inventarios[i][1] + " (Cant:" + inventarios[i][2]
                        + ") C1=" + inventarios[i][3] +" "
                        + "C2=" + inventarios[i][4]+" "
                        + "C3=" + inventarios[i][5]+" ");
            }

            ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(FrmInventarios.this, android.R.layout.simple_list_item_1, nombres);
            listaTotales.setAdapter(itemsAdapter);


        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG);
        }
    }
}
