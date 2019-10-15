package com.example.staj;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import com.google.android.gms.location.FusedLocationProviderClient;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AracCekme extends AppCompatActivity implements OnMapReadyCallback{

    // ========================== GLOBAL DEGISKENLER ===============================
    public Button sorgula,harita,resimler,yeniArac,kayitliProfil,search,deleteAll;
    public EditText plaka,id;
    public String Url = "API_URL" , token = "API_TOKEN",ububibo;
    public ArrayList<ArrayList<String>> requestResult = new ArrayList<>();
    private List<ProfileData> profil_List = new ArrayList<>();
    public TextView otopark,tarih1,tarih2,telefon;
    public LocationRequest mLocationRequest;
    private double currentLatitude,currentLongitude,carLatitude,carLongtitude;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int LOCATION_REQUEST_CODE = 101;
    private LinearLayout profilAracLayout, yeniAracLayout,resimlerLayout,haritaLayout,sonucLayout,switchLayout,headersLayout;
    private boolean isUpdated = false, isListed = false, isLoaded = false;
    private SharedPreferences pref;
    private ArrayList<String> allAdSoyad,allTelefon,allPlaka, allEposta;
    private RecyclerView recyclerView;
    private static AracCekme instance;
    private static Context context;

    // ========================== GLOBAL DEGISKENLER ===============================

    // Aktivite basladiginda calisan ilk dongu.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arac_cekme_sorgulama);
        sorgula = findViewById(R.id.sorgula);
        plaka = findViewById(R.id.plaka);
        harita= findViewById(R.id.harita);
        resimler=findViewById(R.id.pics);
        sonucLayout = findViewById(R.id.sonucLayout);
        haritaLayout = findViewById(R.id.haritaLayout);
        resimlerLayout = findViewById(R.id.resimlerLayout);
        kayitliProfil = findViewById(R.id.kayitliProfil);
        yeniArac = findViewById(R.id.yeniArac);
        yeniAracLayout = findViewById(R.id.yeniAracLayout);
        profilAracLayout = findViewById(R.id.profilAracLayout);
        recyclerView = findViewById(R.id.recylerView2);
        search = findViewById(R.id.search);
        switchLayout = findViewById(R.id.switchLayout);
        headersLayout = findViewById(R.id.headersLayout);
        otopark = findViewById(R.id.otopark);
        tarih1 = findViewById(R.id.tarih1);
        tarih2 = findViewById(R.id.tarih2);
        telefon = findViewById(R.id.telefon);
        deleteAll = findViewById(R.id.deleteAll);

        context = getApplicationContext();

        // Kayitli profil liste arayuzunu acan buton fonskiyonu bir defa tum verileri cekip okur.
        kayitliProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( !isUpdated ) updateAll();
                toggleLayouts(1);
                isUpdated = true;
            }
        });

        // Yeni arac sorgu ekranini acan fonksiyon.
        yeniArac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleLayouts(0);
            }
        });

        // Kullanici verileriyle post atip donen cevaba gore sorgu ekranini acar.
        sorgula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    resimler.setBackgroundColor(Color.parseColor("#704774F1"));
                    harita.setBackgroundColor(Color.parseColor("#704774F1"));
                    switchLayouts(0);
            }
        });

        // Harita fragmentini gorunur kilar
        harita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchLayouts(1);

                if ( !isLoaded ){
                    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
                    if (ActivityCompat.checkSelfPermission(AracCekme.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AracCekme.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AracCekme.this, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                        return;
                    }
                    // Son aktif lokasyonu alan fonksiyon.
                    fetchLastLocation();
                    mLocationRequest = LocationRequest.create()
                            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                            .setInterval(10 * 1000)
                            .setFastestInterval(1 * 1000);
                    //MapFragment mapFragment =  getSupportFragmentManager().findFragmentById(R.id.fragment2);
                    SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
                    mapFragment.getMapAsync(AracCekme.this);
                }isLoaded = true;
            }
        });

        // Resimler fragmentini kontrol eden fonksiyon.
        resimler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchLayouts(2);
                if ( requestResult.get(1).size() > 0 && !isListed ){
                    LinearLayout layout = findViewById(R.id.photoGallery);
                    for ( int i = 0 ; i < requestResult.get(1).size(); i++){
                        ububibo = requestResult.get(1).get(i);
                        final ImageView image  = new ImageView(getApplicationContext());
                        Picasso.get().load(requestResult.get(1).get(i)).into(image);
                        layout.addView(image);
                    }

                } isListed = true;
            }
        });


        // Kullanicinin girdigi verileriyle sorgu yapan fonksiyon
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    try {
                        if ( plaka.getText().toString().length() > 0 && plaka.getText().toString() != null) Request(plaka.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }
        });

        deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAllDatas();
            }
        });

        instance = this;
    }

    public void onDestroy() {
        super.onDestroy();
      /*
        allAdSoyad.clear();
                allTelefon.clear();
        allPlaka.clear();
        allEposta.clear();
       */

    }

    // Arayuz bolumlerini toggle den fonksiyon.
    public void switchFrames(){
        switchLayout.setVisibility(View.INVISIBLE);
        yeniAracLayout.setVisibility(View.INVISIBLE);
        profilAracLayout.setVisibility(View.INVISIBLE);
        headersLayout.setVisibility(View.VISIBLE);
        sonucLayout.setVisibility(View.VISIBLE);
    }

    // Arayuz bolumlerini toggle den fonksiyon.
    private void switchLayouts(int index){
        switch (index){
            case 0:
                sonucLayout.setVisibility(View.VISIBLE);
                haritaLayout.setVisibility(View.INVISIBLE);
                resimlerLayout.setVisibility(View.INVISIBLE);
                sorgula.setBackgroundColor(Color.parseColor("#4774F1"));
                harita.setBackgroundColor(Color.parseColor("#704774F1"));
                resimler.setBackgroundColor(Color.parseColor("#704774F1"));
                break;
            case 1:
                sonucLayout.setVisibility(View.INVISIBLE);
                haritaLayout.setVisibility(View.VISIBLE);
                resimlerLayout.setVisibility(View.INVISIBLE);
                sorgula.setBackgroundColor(Color.parseColor("#704774F1"));
                harita.setBackgroundColor(Color.parseColor("#4774F1"));
                resimler.setBackgroundColor(Color.parseColor("#704774F1"));
                break;
            case 2:
                sonucLayout.setVisibility(View.INVISIBLE);
                haritaLayout.setVisibility(View.INVISIBLE);
                resimlerLayout.setVisibility(View.VISIBLE);
                sorgula.setBackgroundColor(Color.parseColor("#704774F1"));
                harita.setBackgroundColor(Color.parseColor("#704774F1"));
                resimler.setBackgroundColor(Color.parseColor("#4774F1"));;
                break;
        }
        }

        // Son aktif lokasyonu ceken fonksiyon.
        private void fetchLastLocation(){
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if ( location != null){
                    currentLatitude =  location.getLatitude();
                    currentLongitude = location.getLongitude();
                }else{
                    Toast.makeText(getApplicationContext(),"Aktif konum bulunamadi.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // GPS izni kontrol fonksiyonu.
    @Override
    public void onRequestPermissionsResult (int requestCode, String[] permissions, int[] grantResult){
        switch (requestCode){
            case LOCATION_REQUEST_CODE:
            if(grantResult.length > 0  && grantResult[0] == PackageManager.PERMISSION_GRANTED) fetchLastLocation();
            else  Toast.makeText(getApplicationContext(),"Location permission missing",Toast.LENGTH_SHORT).show();
            break;
        }
    }

    // Harita bilesenlerini yaratan/ekleyen fonksiyon.
    @Override
    public void onMapReady(GoogleMap googleMap){
        if ( carLongtitude != 0 && carLatitude != 0){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                LatLng carDestination = new LatLng(carLatitude, carLongtitude); // Post ile donen latlong.
                LatLng currentLocation = new LatLng(currentLatitude, currentLongitude); // GPS ile alinan latlong.
                // Mevcut konum ve arac konumunu haritada gosterir.
                googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Buradasiniz").snippet("PROFILE"));
                googleMap.addMarker(new MarkerOptions().position(carDestination).title("Arac").snippet(" ARAC INFOSU"));
                // Kamera zoom ayarlari.
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12));
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                googleMap.getUiSettings().setCompassEnabled(true);
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                // Iki konumu ekrana sigdirmak icin ayar.
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(currentLocation);
                builder.include(carDestination);
                LatLngBounds bounds = builder.build();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 222));
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                googleMap.setTrafficEnabled(true);
                return;
            }

        }else Toast.makeText(getApplicationContext(),"Arac konumu belirtilmemistir.",Toast.LENGTH_LONG).show();


    }
    // Post istegi
    public  void Request(String plaka) throws JSONException {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("Plaka",plaka);
        final String RequestBody = jsonBody.toString();

        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, Url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("AracCekme", response);
                if ( response.contains("Result") ) {
                    try {
                        switchFrames();
                        requestResult = parseRequest(response);
                        setResults(requestResult);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(getApplicationContext(), "Bilgilerle eslesen bir arac yok.",Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", token);
                params.put("Content-Type", "application/json");
                return params;
            }

            @Override
            public byte[] getBody() throws AuthFailureError{
                try {
                    return RequestBody == null ? null : RequestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    Log.d("Error","Some shit happened");
                    return null;
                }
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }


    public ArrayList<ArrayList<String>> parseRequest(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        JSONObject jsonObject2 = jsonObject.getJSONObject("Result");
        JSONArray tempImages = (JSONArray) jsonObject2.get("Resimler");

        ArrayList<String> result = new ArrayList<>();
        ArrayList<String> Resimler = new ArrayList<>();
        ArrayList<ArrayList<String>> resultWithImages = new ArrayList<>();

            result.add(jsonObject2.getString("OtoparkAdi"));
            result.add(jsonObject2.getString("IslemTarihi"));
            result.add(jsonObject2.getString("IslemTarihiStr"));
            result.add(jsonObject2.getString("PlakaNo"));
            result.add(jsonObject2.getString("Telefon"));
            result.add(jsonObject2.getString("Latitude"));
            result.add(jsonObject2.getString("Longitude"));


            for ( int i = 0 ; i < tempImages.length() ; i++) Resimler.add(tempImages.getString(i));
            resultWithImages.add(result);
            resultWithImages.add(Resimler);
        return resultWithImages;
    }

    public void setResults( ArrayList<ArrayList<String>> result ) {
             switchLayout.setVisibility(View.INVISIBLE);
             sonucLayout.setVisibility(View.VISIBLE);
             headersLayout.setVisibility(View.VISIBLE);

             otopark.setText(result.get(0).get(0).toString());
             tarih1.setText(result.get(0).get(2).toString());
             tarih2.setText(result.get(0).get(3).toString());
             telefon.setText(result.get(0).get(4).toString());

             String x = result.get(0).get(5).toString();
             String y = result.get(0).get(6).toString();

             if( (x != null && y !=null) && (!x.contains("null") && !y.contains("null"))){
                 carLatitude = Double.parseDouble(result.get(0).get(5).toString());
                 carLongtitude = Double.parseDouble(result.get(0).get(6).toString());
             }else {
                 carLatitude = 0;
                 carLongtitude = 0;
             }



    }

    public void toggleLayouts(int index){
        switch (index){
            case 0:
                //yeniArac.setBackgroundColor(Color.parseColor("#4774F1"));
                //kayitliProfil.setBackgroundColor(Color.parseColor("#9AB3F7"));
                yeniAracLayout.setVisibility(View.VISIBLE);
                profilAracLayout.setVisibility(View.INVISIBLE);
                break;
            case 1:
                //yeniArac.setBackgroundColor(Color.parseColor("#9AB3F7"));
                //kayitliProfil.setBackgroundColor(Color.parseColor("#4774F1"));
                yeniAracLayout.setVisibility(View.INVISIBLE);
                profilAracLayout.setVisibility(View.VISIBLE);
                break;
        }
    }

       public void updateAll(){
        pref = getSharedPreferences("Profiles", MODE_PRIVATE);
        Map<String,?> keys = pref.getAll();

        allAdSoyad = new ArrayList<>();
        allTelefon = new ArrayList<>();
        allPlaka = new ArrayList<>();
        allEposta = new ArrayList<>();
        List<String> plakalar = new ArrayList<>();


        for(Map.Entry<String,?> entry : keys.entrySet())if ( entry.getKey().contains("___2"))  plakalar.add(entry.getValue().toString());

            for ( int i = 0 ; i < plakalar.size() ; i++ ){
                allAdSoyad.add( pref.getString(plakalar.get(i)+"___1", "") );
                allPlaka.add( pref.getString(plakalar.get(i)+"___2", "") );
                allTelefon.add( pref.getString(plakalar.get(i)+"___3", "") );
                allEposta.add( pref.getString(plakalar.get(i)+"___4", "") );
            }

        for ( int i = 0 ; i < allAdSoyad.size(); i++){ updateProfiles(allAdSoyad.get(i), allPlaka.get(i), allTelefon.get(i), allEposta.get(i)); }
    }

    public void updateProfiles(String adSoyad, String plaka, String telefon, String eposta){
        recyclerView = findViewById(R.id.recylerView2);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        recyclerView.setLayoutManager(layoutManager);

        profil_List.add(new ProfileData(adSoyad,telefon,plaka,eposta));

        RecyclerViewAdapter adapter_items = new RecyclerViewAdapter(profil_List){
            @Override
            public void onItemClick(View v, int position) {
                ProfileData profil = profil_List.get(position);
            }
        };

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter_items);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        toggleLayouts(1);
    }

    public void deleteAllDatas(){
        pref = getApplicationContext().getSharedPreferences("Profiles", MODE_PRIVATE);

        AlertDialog.Builder builder = new AlertDialog.Builder(AracCekme.this)
                .setTitle("Kaydi Sil")
                .setMessage("Tum kayitlari gercekten silmek istiyor musunuz?")
                .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        pref.edit().clear().commit();
                        Toast.makeText(getApplicationContext(),"Tum kayitlari silindi.",Toast.LENGTH_LONG).show();
                        updateAll();
                        finish();
                    }
                })
                .setNegativeButton("Hayir",null);
        builder.show();

    }

    // Adapterda listener kullanabilmek icin AracCekme aktivitesini instancelayarak erisilebilir hale getiriyoruz.
    public static AracCekme getInstance() { return instance; }

    public static Context getContextOfAracCekme(){ return context;}
}
