package com.example.imvcol;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class ArrayUtils {
    public static ArrayList<Object> convertToArrayList(JSONArray jArr, JSONObject begObject) {
        ArrayList<Object> list = new ArrayList<Object>();
        list.add(begObject);
        try {
            for (int i = 0, l = jArr.length(); i < l; i++) {
                list.add(jArr.get(i));
            }
        } catch (JSONException e) {
        }

        return list;
    }

    public static ArrayList<String[]> mapObjects(String nameId, String name, ArrayList objects) {
        try {
            String[] spinnerArray = new String[objects.size()];
            final HashMap<Integer, String> spinnerMap = new HashMap<Integer, String>();

            for (int i = 0; i < objects.size(); i++) {
                spinnerMap.put(i, ((JSONObject) objects.get(i)).getString(nameId));
                spinnerArray[i] = ((JSONObject) objects.get(i)).getString(nameId) + " - " + ((JSONObject) objects.get(i)).getString(name);
            }

            ArrayList result = new ArrayList();
            result.add(spinnerArray);
            result.add(spinnerMap);
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}