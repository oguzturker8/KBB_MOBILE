package com.example.staj;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Otopark extends AppCompatActivity {
    // Otopark Doluluk Durumu Aktivitesi

    // ========================== GLOBAL DEGISKENLER ===============================
    public String Url = "API_URL",token = "API_KEY";
    public ArrayList<ArrayList<String>> requestResult = new ArrayList<>();
    public String[] seperated;
    public Spinner spinner;
    private LinearLayout resultLayout;
    // ========================== GLOBAL DEGISKENLER ===============================

    // Aktivite basladiginda calisan ilk dongu.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otopark);
        resultLayout = findViewById(R.id.layoutResult);
        spinner = findViewById(R.id.otoSecim); // Ui elemanlarini burada kullanabilmek icin oncreatede tanimliyoruz.
        Request(); // Otopark doluluk durumu post istegi
    }

    //===========================================

    // Volley Post Istegi Fonksiyonu
    public  void Request(){
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, Url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response", response);
                try {
                    requestResult = parseRequest(response); // Attigimiz post isteginden donen result i once istedigimiz bilesenlere ayirip yeni bir degiskende tutuyoruz
                    seperateResult(); // ayrilan degiskenleri arayuzde kullaniyoruz
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError { // Post icin gerekli Header fonksiyonu
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", token);
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }

    // Duz string olarak donen cevabimizi istedigimiz basliklarda ayikliyoruz.
    public ArrayList<ArrayList<String>> parseRequest(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        JSONArray tempArray = (JSONArray) jsonObject.get("Result");
        ArrayList<ArrayList<String>> result = new ArrayList<>();

        ArrayList<String> OtoparkAdi = new ArrayList<>();
        ArrayList<String> PoiId = new ArrayList<>();
        ArrayList<String> Kapasite = new ArrayList<>();
        ArrayList<String> Dolu = new ArrayList<>();
        ArrayList<String> Bos = new ArrayList<>();

        for(int i=0; i<tempArray.length(); i++){
            OtoparkAdi.add(tempArray.getJSONObject(i).getString("OtoparkAdi"));
            PoiId.add(tempArray.getJSONObject(i).getString("PoiId"));
            Kapasite.add(tempArray.getJSONObject(i).getString("Kapasite"));
            Dolu.add(tempArray.getJSONObject(i).getString("Dolu"));
            Bos.add(tempArray.getJSONObject(i).getString("Bos"));
        }
        result.add(OtoparkAdi);
        result.add(PoiId);
        result.add(Kapasite);
        result.add(Dolu);
        result.add(Bos);
        return result;
    }

    // 2d arraylistten bilgileri ayirip tek dizide topluyoruz.
    public void seperateResult() {

        int size = requestResult.get(0).size();
        seperated = new String[size*5];
        String[] OtoparkAdi = new String[size];
        for (int i = 0; i < size; i++) {
            seperated[i] = requestResult.get(0).get(i);
            OtoparkAdi[i] = requestResult.get(0).get(i);
            seperated[i+1] = requestResult.get(1).get(i);
            seperated[i+2] = requestResult.get(2).get(i);
            seperated[i+3] = requestResult.get(3).get(i);
            seperated[i+4] = requestResult.get(4).get(i);
        }
        setResults(OtoparkAdi, seperated);
        // Bilgilerin UI da gosterilmesini saglayan fonksiyon.
    }

    public void setResults(String[] OtoparkAdi, final String[] seperated){



        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spintext, OtoparkAdi);
        adapter.setDropDownViewResource(R.layout.spintext);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) { // Listelenen otoparklarda secilen otoparkin bilgilerini ekranda gosteren fonksyion.

                TextView otoRec = findViewById(R.id.otoRec);
                TextView poiRec = findViewById(R.id.poiRec);
                TextView kapasiteRec = findViewById(R.id.kapasiteRec);
                TextView doluRec = findViewById(R.id.doluRec);
                TextView bosRec = findViewById(R.id.bosRec);

                int selected = spinner.getSelectedItemPosition();
                otoRec.setText(seperated[selected*5]);
                poiRec.setText(seperated[selected*5+1]);
                kapasiteRec.setText(seperated[selected*5+2]);
                doluRec.setText(seperated[selected*5+3]);
                bosRec.setText(seperated[selected*5+4]);
                checkCapacity(Integer.parseInt(seperated[selected*5+2]), Integer.parseInt(seperated[selected*5+3]), Integer.parseInt(seperated[selected*5+4]));
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
        resultLayout.setVisibility(View.VISIBLE);
    }

    // Otopark kapasitesini kontrol edip kullaniciyi bilgilendiren fonksyion
    public void checkCapacity(int kapasite, int dolu, int bos){
        if ( kapasite == dolu || bos <= 0){ Toast.makeText(getApplicationContext(),"Secili otoparkta hic bos yer yoktur.",Toast.LENGTH_LONG).show();}
    }
}




