package com.gaiabit.gaiabit.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gaiabit.gaiabit.Model.ShelterModel;

import com.gaiabit.gaiabit.R;
import com.gaiabit.gaiabit.adapter.ShelterAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class Shelter extends Fragment {


    RecyclerView recyclerView;
    List<ShelterModel> apilist;

    private static String JSON_URL = "https://api.jsonserve.com/6HTZh5";
    private ProgressDialog progressDialog;

    public Shelter() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getActivity().getSharedPreferences("HomeFragmentPrefs", Context.MODE_PRIVATE);
        boolean isDataLoaded = prefs.getBoolean("isDataLoaded", false);

        apilist = new ArrayList<>();
        if (!isDataLoaded) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("載入中...");
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();


        }
    }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            //Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.fragment_shelter, container, false);
            recyclerView = view.findViewById(R.id.rec1);


            init(view);

        if (apilist == null || apilist.isEmpty()) {
        //如果apilist是空的，我们显示ProgressDialog并开始新的GetData任务
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("載入中...");
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();

            GetData getData = new GetData();
            getData.execute();
            }else{
     // 如果apilist已有数据，我们直接将数据填充到RecyclerView
            PutDataIntoRecyclerView(apilist);
            }
            return view;
        }

    private void PutDataIntoRecyclerView(List<ShelterModel> apilist) {
        ShelterAdapter shelterAdapter = new ShelterAdapter(getContext(),apilist);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(shelterAdapter);

    }
    private void init(View view){
        Toolbar toolbar =view.findViewById(R.id.toolbar);
            if (getActivity()!=null)
            ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
    }


    private class GetData extends AsyncTask<String,String,String> {

          @Override
          protected String doInBackground(String... strings) {
              String current = "";
              try {
                  URL url;
                  HttpURLConnection urlConnection = null;
                  try {
                      url = new URL(JSON_URL);
                      urlConnection = (HttpURLConnection) url.openConnection();

                      InputStream is = urlConnection.getInputStream();
                      InputStreamReader isr = new InputStreamReader(is);

                      int data = isr.read();
                      while (data != -1) {
                          current += (char) data;
                          data = isr.read();
                      }
                      return current;

                  } catch (IOException e) {
                      e.printStackTrace();
                  } finally {
                      if (urlConnection != null) {
                          urlConnection.disconnect();
                      }
                  }

              } catch (Exception e) {
                  e.printStackTrace();
              }
              return current;

    }

         @Override
         protected void onPostExecute(String s) {
           try {
             JSONObject jsonObject = new JSONObject(s);
             JSONArray jsonArray = jsonObject.getJSONArray("Shelter");

              for (int i = 0; i < jsonArray.length(); i++) {
               JSONObject jsonObject1 = jsonArray.getJSONObject(i);
//
             ShelterModel model = new ShelterModel();
              model.setShelter_name(jsonObject1.getString("shelter_name"));
               model.setShelter_address(jsonObject1.getString("shelter_address"));
            model.setAlbum_file(jsonObject1.getString("album_file"));
//
              apilist.add(model);
            }

        } catch (Exception e) {
           e.printStackTrace();
        }

        PutDataIntoRecyclerView(apilist);

       SharedPreferences prefs = getActivity().getSharedPreferences("HomeFragmentPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isDataLoaded", true);
         editor.apply();

        progressDialog.dismiss();
       }


            private void PutDataIntoRecyclerView(List<ShelterModel> apilist) {
            ShelterAdapter shelterAdapter = new ShelterAdapter(getContext(),apilist);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(shelterAdapter);
        }


    }

}