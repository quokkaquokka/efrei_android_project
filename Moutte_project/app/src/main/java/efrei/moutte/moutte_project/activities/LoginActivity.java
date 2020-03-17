package efrei.moutte.moutte_project.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import efrei.moutte.moutte_project.R;
import efrei.moutte.moutte_project.models.Annonce;
import efrei.moutte.moutte_project.models.Authentification;

public class LoginActivity extends AppCompatActivity {
    private final String JSON_URL = "https://9kmebpt3vb-dsn.algolia.net/1/indexes/user?x-algolia-application-id=9KMEBPT3VB&x-algolia-api-key=6b3e5d8cef4bc3f48fd4b566b270dd0b" ;
    private JsonObjectRequest request ;
    private RequestQueue requestQueue ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // String getLogin = getIntent().getStringExtra("Login");
        // TextView displayTextView = (TextView) findViewById(R.id.textView4);
        // displayTextView.setText(getLogin);
    }


    public void submit(View view) throws JSONException {
        EditText mailEditText = (EditText) findViewById(R.id.mailEditText);
        String mail = mailEditText.getText().toString();

        EditText passwordEditText = (EditText) findViewById(R.id.PasswordEditText);
        String password = passwordEditText.getText().toString();

        jsonrequest(mail, password);
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);

    }

    private void jsonrequest(String email, String password) throws JSONException {
        JSONObject params = new JSONObject();
        params.put("x-algolia-application-id", "9KMEBPT3VB");
        params.put("x-algolia-api-key", "6b3e5d8cef4bc3f48fd4b566b270dd0b");
        params.put("filters", "email: " + email + " AND password: " + password );

        request = new JsonObjectRequest(Request.Method.GET, JSON_URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONObject jsonObject  = null ;
                try {
                    Log.i("submit", "response json");
                    JSONArray arr = response.getJSONArray("hits");
                    jsonObject = arr.getJSONObject(0);
                    String email = jsonObject.getString("email");
                    String password = jsonObject.getString("password");
                    String id = jsonObject.getString("objectID");

                    List<String> villes = new ArrayList<String>();
                    JSONArray jArray = jsonObject.getJSONArray("villes");;
                    if (jArray != null) {
                        for (int i = 0; i < jArray.length(); i++){
                            villes.add(jArray.getString(i));
                        }
                    }

                    List<Annonce> annonces = new ArrayList<Annonce>();
                    jArray = jsonObject.getJSONArray("annonces");
                    if (jArray != null) {
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject jsonObj = jArray.getJSONObject(i);
                            Annonce annonce = new Annonce();
                            annonce.setVille(jsonObj.getString("ville"));
                            annonce.setSource(jsonObj.getString("source"));
                            annonce.setType(jsonObj.getString("type"));
                            annonce.setLoyer(jsonObj.getInt("loyer"));
                            annonce.setPrix(jsonObj.getInt("prix"));
                            annonce.setCp(jsonObj.getString("cp"));
                            annonce.setSurface(jsonObj.getInt("surface"));
                            annonce.setPrixm2(jsonObj.getInt("prixm2"));
                            annonce.setRendement((float) jsonObj.getDouble("rendement"));
                            annonce.setInvestissement(jsonObj.getInt("investissement"));
                            annonce.setNbpieces(jsonObj.getInt("nbpieces"));
                            annonce.setNeuf(jsonObj.getBoolean("neuf"));
                            annonce.setPermalien(jsonObj.getString("permalien"));
                            annonce.setDescription(jsonObj.getString("description"));
                            annonce.setImg_url(jsonObj.getString("img"));
                            annonce.setTravaux(jsonObj.getString("travaux"));
                            annonce.setMontant(jsonObj.getString("montant"));

                            List<String> locations = new ArrayList<String>();
                            JSONArray jLocations = jsonObj.getJSONArray("locations");
                            if (jLocations != null) {
                                for (int j = 0; j < jLocations.length(); j++){
                                    locations.add(jLocations.getString(j));
                                }
                            }
                            annonce.setLocations(locations);
                            annonces.add(annonce);
                        }
                    }

                    Authentification.instanciate(email, password, id, villes, annonces);
                    Context context = getApplicationContext();
                    CharSequence text = "Vous êtes connecté!";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Context context = getApplicationContext();
                CharSequence text = "Votre email ou/et mot de passe sont faux!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

            }
        })
        {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Referer", "https://www.rendementlocatif.com/investissement/annonces");
                return params;
            }
        };

        requestQueue = Volley.newRequestQueue(LoginActivity.this);
        requestQueue.add(request) ;


    }
}