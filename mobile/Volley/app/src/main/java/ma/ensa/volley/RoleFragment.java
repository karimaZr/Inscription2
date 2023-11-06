package ma.ensa.volley;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
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
import ma.ensa.volley.adapter.RoleAdapter;
import ma.ensa.volley.beans.Filiere;
import ma.ensa.volley.beans.Role;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RoleFragment} factory method to
 * create an instance of this fragment.
 */
public class RoleFragment extends Fragment implements View.OnClickListener  {

    private EditText name;
    private Button bnAdd;
    RequestQueue requestQueue;
    private List<Role> roles;
    private RecyclerView recyclerView;
    private RoleAdapter adapter;
    private static final String TAG = "RoleFragment";

    String insertUrl = "http://192.168.8.103:8083/api/role";
    public RoleFragment() {
        // Required empty public constructor
    }

    public static RoleFragment newInstance() {
        return new RoleFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_role, container, false);
        name = view.findViewById(R.id.role);
        bnAdd = view.findViewById(R.id.bnAdd1);
        bnAdd.setOnClickListener(this);
        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.roleRecyclerView1);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        roles = new ArrayList<>();
        adapter = new RoleAdapter(roles, requireContext());
        recyclerView.setAdapter(adapter);

        // Fetch data from the API
       fetchRoleData();

        return view;
    }

    private void fetchRoleData() {
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, insertUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Raw JSON Response: " + response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                int id = jsonObject.getInt("id");
                                String name = jsonObject.getString("name"); // Vérifiez le nom exact de votre clé dans la réponse JSON
                                Role role = new Role(id, name);
                                roles.add(role);
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            Log.e(TAG, "Error converting response to JSON array: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse == null) {
                            if (error.getClass().equals(TimeoutError.class)) {
                                // Show timeout error message to the user
                                Log.e(TAG, "TimeoutError: " + error.toString());
                            }
                        } else {
                            // Handle other network errors here
                            Log.e(TAG, "Error loading roles: " + error.toString());
                        }
                    }
                }
        );

        requestQueue.add(stringRequest);
    }

    @Override
    public void onClick(View view) {
        JSONObject jsonBody = new JSONObject();
        try {
            String inputCode = name.getText().toString();
            Log.d("MyApp", "name: " + inputCode);

            jsonBody.put("name", inputCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        requestQueue = Volley.newRequestQueue(requireContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                insertUrl, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("resultat", response.toString());
                // Afficher une alerte pour indiquer que la filière a été ajoutée avec succès
                new AlertDialog.Builder(requireContext())
                        .setTitle("Succès")
                        .setMessage("Role ajouté avec succès")
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            // Revenir à FiliereFragment
                            getParentFragmentManager().popBackStack();
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




