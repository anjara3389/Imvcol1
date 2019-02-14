package com.example.imvcol;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imvcol.Item.LstItem;
import com.example.imvcol.Item.ProductosAdapter;

import java.util.ArrayList;

public class FrmInventarios extends AppCompatActivity {

    private ListView listaTotales;
    private TextView mensaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_inventarios);

        listaTotales = findViewById(R.id.frm_inventarios_list);
        mensaje = findViewById(R.id.frm_inventarios_mensaje);
        Bundle bundle = getIntent().getExtras();
        boolean diferencia = bundle.getBoolean("diferencia");
        try {
            SQLiteDatabase db = BaseHelper.getReadable(this);
            Usuario usuario = new Usuario().selectUsuario(db);
            Object[][] inventarios = new Inventario().selectInventariosTotales(db, diferencia, usuario.getCurrGrupo(), usuario.getCurrSubgr(), usuario.getCurrSubgr2(), usuario.getCurrSubgr3(), usuario.getCurrClase());
            BaseHelper.tryClose(db);

            if (inventarios == null) {
                mensaje.setVisibility(View.VISIBLE);
            } else {
                ArrayList<LstItem> data = new ArrayList();

                //final ArrayList<String> nombres = new ArrayList();
                System.out.println("CONTEO" + usuario.getCurrConteo());

                for (int i = 0; i < inventarios.length; i++) {
                    LstItem item = new LstItem(inventarios[i][0] + "-" + (inventarios[i][1] == null ? "Sin nombre" : inventarios[i][1]),
                            Integer.parseInt(inventarios[i][2].toString()),
                            inventarios[i][2 + usuario.getCurrConteo()] == null ? "Falta" : inventarios[i][2 + usuario.getCurrConteo()].toString());

                    data.add(item);
                   /* nombres.add(inventarios[i][0] + "-" + inventarios[i][1] + " (Cant:" + inventarios[i][2]
                            + ") C1=" + inventarios[i][3] + " "
                            + "C2=" + inventarios[i][4] + " "
                            + "C3=" + inventarios[i][5] + " ");*/
                }
                ProductosAdapter adapter = new ProductosAdapter(data, FrmInventarios.this);

                //ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(FrmInventarios.this, android.R.layout.simple_list_item_1, nombres);
                listaTotales.setAdapter(adapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG);
        }
    }
}
