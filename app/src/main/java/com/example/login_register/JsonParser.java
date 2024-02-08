package com.example.login_register;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonParser {

    private HashMap<String, String> parseJsonObject(JSONObject object) throws JSONException {
        HashMap<String, String> dataList = new HashMap<>();
        try {
            String name = object.getString("name");
            String latitude = object.getJSONObject("geometry")
                    .getJSONObject("location").getString("lat");
            String longitude = object.getJSONObject("geometry")
                    .getJSONObject("location").getString("lng");
            dataList.put("name", name);
            dataList.put("lat", latitude);
            dataList.put("lng", longitude);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    private List<HashMap<String, String>> parseJsonArray(JSONArray jsonArray) {
        List<HashMap<String, String>> dataList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                HashMap<String, String> data = parseJsonObject(jsonArray.getJSONObject(i));
                dataList.add(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return dataList;
    }

    public List<HashMap<String, String>> parseResult(JSONObject object) {
        try {
            JSONArray jsonArray = object.getJSONArray("results");
            return parseJsonArray(jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Updated method to handle parsing from a string
    public List<HashMap<String, String>> parseResult(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            return parseResult(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}