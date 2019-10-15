package com.example.staj;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Profil extends AppCompatActivity{

    // Profil Aktivitesi

    // ========================== GLOBAL DEGISKENLER ===============================
    private EditText plaka,adSoyad,telefon,eposta;
    private String sAdSoyad,sPlaka,sTelefon,sEposta,Url = "API_URL",token = "API_TOKEN";
    private Button kayitOl;
    private SharedPreferences.Editor editor;
    private boolean validation = false;
    // ========================== GLOBAL DEGISKENLER ===============================

    // Aktivite basladiginda calisan ilk dongu.f
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        // Classta kullanilacak arayuz elemanlarini belirtiyoruz.
        kayitOl = findViewById(R.id.kayitOl);
        plaka = findViewById(R.id.registerPlaka);
        adSoyad = findViewById(R.id.registerAdSoyad);
        telefon = findViewById(R.id.registerTelefon);
        eposta = findViewById(R.id.registerMail);
        //updateAllProfiles();

        // Butonlarin tiklanma fonksiyonlarini aktivite yaratildigi anda aktif ediyoruz.
        kayitOl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidation(adSoyad.getText().toString(), plaka.getText().toString(), telefon.getText().toString(), eposta.getText().toString());
                if (validation) saveLocalData(adSoyad.getText().toString(), plaka.getText().toString(), telefon.getText().toString(), eposta.getText().toString());
            }
        });


    }

        // Girinlen bilgiler dogru ise bu fonksyion bilgileri cihaza kaydeder.
        public void saveLocalData(String adSoyad, String plaka, String telefon, String eposta){
        // SharedPreferences editor sinifi
        editor = getSharedPreferences("Profiles",MODE_PRIVATE).edit();;
        // Kullanicinin girdigi bilgiler yazilmak uzere bos belirlenen global verilere atanir.
        sAdSoyad = adSoyad;
        sPlaka = plaka;
        sTelefon = telefon;
        sEposta = eposta;
        // Burada hangi kullanicinin girdigi bilgiler basit seviyede mapping ile hafizaya yazilmaya hazir hale getirilir.
        String[] convertedKeys = convertKeys(plaka);
        // Haritalanan veriler cihaza kaydedilir.
        editor.putString(convertedKeys[0], adSoyad);
        editor.putString(convertedKeys[1], plaka);
        editor.putString(convertedKeys[2], telefon);
        editor.putString(convertedKeys[3], eposta);
        // Kayit islemi onaylanir.
        editor.commit();
        Toast.makeText(getApplicationContext(),"Kayit basarili",Toast.LENGTH_LONG).show();

            Intent myIntent = new Intent(Profil.this, AracCekme.class);
            Profil.this.startActivity(myIntent);
            finish();
    }

    // Kullanicinin girdigi bilgilerin Key degerleri kullanici adi + ___index_numarasi olarak saklanir ki hem dinamik hem kolay sekilde bulunabilsin.
    public String[] convertKeys(String plaka){
        String[] converted = new String[4];
        converted[0] = plaka+"___1";
        converted[1] = plaka+"___2";
        converted[2] = plaka+"___3";
        converted[3] = plaka+"___4";
        return converted;
    }

    public void checkValidation(String adSoyad, String plaka, String telefon, String eposta){
        if ( adSoyad != null && !adSoyad.isEmpty() && plaka != null && !plaka.isEmpty() && telefon != null && !telefon.isEmpty() && eposta != null && !eposta.isEmpty()  ) validation = true;
        else {
            validation = false;
            Toast.makeText(getApplicationContext(),"Tum bilgileri doldurdugunuzdan emin olun",Toast.LENGTH_LONG).show();
        }
    }




}
