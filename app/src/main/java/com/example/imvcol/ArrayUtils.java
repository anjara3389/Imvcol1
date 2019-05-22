package com.example.imvcol;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ArrayUtils {
    public static ArrayList<Object> convertToArrayList(JSONArray jArr, Context ctx) {
        try {
            ArrayList<Object> list = new ArrayList<Object>();
           /* if (begObject != null) {
                list.add(begObject);
            }*/
            for (int i = 0, l = jArr.length(); i < l; i++) {
                list.add(jArr.get(i));
            }
            return list;
        } catch (JSONException e) {
            Toast.makeText(ctx, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        return null;
    }

    public static ArrayList<String[]> mapObjects(Object[][] objects) {
        String[] spinnerArray = new String[objects.length];
        final HashMap<Integer, String> spinnerMap = new HashMap<Integer, String>();

        for (int i =0; i < objects.length; i++) {
            spinnerMap.put(i, (String) objects[i][0]);
            spinnerArray[i] = objects[i][0] + " - " + objects[i][1];
        }

        ArrayList result = new ArrayList();
        result.add(spinnerArray);
        result.add(spinnerMap);
        return result;
    }


    public static ArrayList<String[]> mapObjects(ArrayList objects) {
        String[] spinnerArray = new String[objects.size()];
        final HashMap<Integer, String> spinnerMap = new HashMap<Integer, String>();

        for (int i = 0; i < objects.size(); i++) {
            String id = (String) ((Object[]) objects.get(i))[0];
            String descripcion = (String) ((Object[]) objects.get(i))[1];
            spinnerMap.put(i, id);
            spinnerArray[i] = id + " - " + descripcion;
        }

        ArrayList result = new ArrayList();
        result.add(spinnerArray);
        result.add(spinnerMap);
        return result;
    }
}