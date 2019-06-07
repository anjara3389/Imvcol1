package com.example.imvcol;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imvcol.WebserviceConnection.ExecuteRemoteQuery;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FrmGetInfoCodigo extends AppCompatActivity {

    private EditText codigo;
    private TextView info;
    private Button btnInfo;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frm_get_info_codigo);
        try {
            codigo = findViewById(R.id.frm_get_info_codigo_txt_codigo);
            info = findViewById(R.id.frm_get_info_codigo_lbl_info);
            btnInfo = findViewById(R.id.frm_get_info_codigo_btn_info);
            SQLiteDatabase db = BaseHelper.getReadable(getApplicationContext());
            usuario = new Usuario().selectUsuario(db);
            BaseHelper.tryClose(db);

            btnInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(FrmGetInfoCodigo.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    /**Retorna grupo,subgr,subgr2,subgr3,clase,ubicación del código de un producto.
     *
     */
    private void getInfoReferenciaFromWebservice() {
        @SuppressLint("StaticFieldLeak") ExecuteRemoteQuery remote = new ExecuteRemoteQuery() {
            @Override
            public void receiveData(Object object) throws Exception {
                SQLiteDatabase db = BaseHelper.getWritable(FrmGetInfoCodigo.this);
                ArrayList resultsDatos = (ArrayList) object;

                ArrayList rawResults = ArrayUtils.convertToArrayList(new JSONArray((String) resultsDatos.get(0)), FrmGetInfoCodigo.this);
                if (resultsDatos.get(0).equals("[]")) {
                    //dialogUtils.dissmissDialog();
                    BaseHelper.tryClose(db);
                    Toast.makeText(FrmGetInfoCodigo.this, "No se han encontrado datos", Toast.LENGTH_LONG).show();
                } else {
                    for (int i = 0; i < rawResults.size(); i++) {
                        JSONObject fisico = ((JSONObject) rawResults.get(i));
                        if (fisico.getInt("fisico") == 0) {
                      //      validar = false;
                        }
                    }


                }
            }
        };
        remote.setContext(FrmGetInfoCodigo.this);

        ArrayList queryDatos = new ArrayList();

        String query = "SELECT r.grupo,r.subgrupo,r.subgrupo2,r.subgrupo3,r.clase,r.ubicacion " +
                "FROM referencias_fis f " +
                "JOIN referencias r on r.codigo=f.codigo " +
                "JOIN v_referencias_sto s on f.codigo=s.codigo AND f.bodega=s.bodega " +
                "WHERE f.bodega='" + usuario.getCurrBodega() + "' " +
                "AND s.ano=YEAR(getdate()) " +
                "AND s.mes=MONTH(getdate()) ";

        queryDatos.add(query);
        System.out.println("QUERYYYYYY///" + query);
        remote.setQuery(queryDatos);
        remote.execute();
    }
}
