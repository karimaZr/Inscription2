package ma.ensa.volley;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ma.ensa.volley.adapter.FiliereAdapter;
import ma.ensa.volley.beans.Filiere;


public class FiliereFragment extends Fragment implements View.OnClickListener  {

    private Button addButton, updateButton, deleteButton;
    private List<Filiere> filiereList;
    private EditText code, libelle;
    private Button bnAdd;
    RequestQueue requestQueue;
    private RecyclerView recyclerView;
    private static final String TAG = "FiliereFragment";
    private String insertUrl = "http://192.168.8.103:8083/api/v1/filieres";
    private FiliereAdapter adapter;
    public FiliereFragment() {
        // Required empty public constructor
    }

    public static FiliereFragment newInstance() {
        return new FiliereFragment();
    }
    private void updateFiliereList() {
        filiereList.clear(); // Clear the current list
        fetchFiliereData();  // Fetch and populate the list again
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filiere, container, false);


        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.filiereRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        filiereList = new ArrayList<>();
        adapter = new FiliereAdapter(filiereList, requireContext());
        recyclerView.setAdapter(adapter);
        code = view.findViewById(R.id.code);
        libelle = view.findViewById(R.id.libelle);
        bnAdd = view.findViewById(R.id.bnAdd);
        bnAdd.setOnClickListener(this);

        // Fetch data from the API
        fetchFiliereData();


        // Récupérer les boutons updateButton et deleteButton


        return view;
    }


    private void fetchFiliereData() {
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, insertUrl,
                response -> {
                    Log.d(TAG, "Raw JSON Response: " + response);
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            int id = jsonObject.getInt("id");
                            String code = jsonObject.getString("code");
                            String libelle = jsonObject.getString("libelle");
                            Filiere filiere = new Filiere(id, code, libelle);
                            filiereList.add(filiere);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.e(TAG, "Error converting response to JSON array: " + e.getMessage());
                    }
                },
                error -> {
                    if (error.networkResponse == null) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            // Show timeout error message to the user
                            Log.e(TAG, "TimeoutError: " + error.toString());
                        }
                    } else {
                        // Handle other network errors here
                        Log.e(TAG, "Error loading filieres: " + error.toString());
                    }
                }
        );

        requestQueue.add(stringRequest);
    }

    @Override
    public void onClick(View view) {
        JSONObject jsonBody = new JSONObject();
        try {
            String inputCode = code.getText().toString();
            String inputLibelle = libelle.getText().toString();
            Log.d("MyApp", "Code: " + inputCode);
            Log.d("MyApp", "Libellé: " + inputLibelle);

            jsonBody.put("code", inputCode);
            jsonBody.put("libelle", inputLibelle);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        requestQueue = Volley.newRequestQueue(requireContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                insertUrl, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("resultat", response.toString());
                // Refresh the list in the same fragment
                updateFiliereList();
                // Show a success dialog
                new AlertDialog.Builder(requireContext())
                        .setTitle("Succès")
                        .setMessage("Filière ajoutée avec succès")
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            // Handle your desired action after success
                        })
                        .show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Erreur", error.toString());
            }
        });
        requestQueue.add(request);
    }
}


