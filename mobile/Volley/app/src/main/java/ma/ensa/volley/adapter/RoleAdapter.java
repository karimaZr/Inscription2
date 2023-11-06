package ma.ensa.volley.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.List;

import ma.ensa.volley.R;
import ma.ensa.volley.beans.Role;

public class RoleAdapter extends RecyclerView.Adapter<RoleAdapter.RoleViewHolder> {
    private List<Role> roleList;
    private Context context;
    private FiliereAdapter adapter;
    private String deletetUrl = "http://192.168.8.103:8083/api/role/{id}";
    public RoleAdapter(List<Role> roleList, Context context) {
        this.roleList = roleList;
        this.context = context;
    }

    @NonNull
    @Override
    public RoleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filiere, parent, false);
        return new RoleViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RoleViewHolder holder,int position) {
        Role role = roleList.get(position);
        holder.idTextView.setText(String.valueOf(role.getId()));
        holder.nameTextView.setText(role.getName());


        holder.deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Confirmation")
                    .setMessage("Voulez-vous supprimer ce role ?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            deleteFiliere(role.getId());
                            roleList.remove(position); // Supprimer l'élément de la liste locale
                            notifyItemRemoved(position); // Notifier l'adaptateur de la suppression de l'élément à la position spécifiée
                            notifyItemRangeChanged(position,roleList.size()); // Notifier l'adaptateur du changement de la plage d'éléments
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });


    }

    @Override
    public int getItemCount() {
        return roleList.size();
    }

    static class RoleViewHolder extends RecyclerView.ViewHolder {
        TextView idTextView, nameTextView;
        Button updateButton, deleteButton;

        public RoleViewHolder(@NonNull View itemView) {
            super(itemView);
            idTextView = itemView.findViewById(R.id.id1);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            updateButton = itemView.findViewById(R.id.updateButton1);
            deleteButton = itemView.findViewById(R.id.deleteButton1);
        }
    }
    private void deleteFiliere(int id) {
        String deleteUrl = deletetUrl.replace("{id}", String.valueOf(id));
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, deleteUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Afficher une alerte pour indiquer que la filière a été supprimée
                        Toast.makeText(context, "Role supprimé", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Gérer les erreurs ici
                    }
                });

        // Ajout de la requête à la file d'attente de la requête
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

}

