package com.example.imvcol;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FrmOpciones extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_opciones);


            Bundle bundle = getIntent().getExtras();
            ArrayList datos = bundle.getParcelableArrayList("datos");
            //JSONObject jsonObject = null;
            //jsonObject = new JSONObject((String) datos.get(0));
            //jsonObject.getJSONArray("");



    }
}
