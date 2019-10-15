package com.example.staj;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.viewpagerindicator.CirclePageIndicator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends AppCompatActivity {

    // Main sinifimiz uygulama calistirildiginda ekrana gelen baslangic aktivitemizdir.

    // ========================== GLOBAL DEGISKENLER ===============================
    public String Url = "API_URL",token = "API_TOKEN";
    public  ArrayList<ArrayList<String>> requestResult = new ArrayList<>();
    //public String[] separated;
    public String[] urls, titles, links;
    private static ViewPager mPager;
    private static int currentPage = 0, NUM_PAGES = 0;
    private Button otopark,profil,arac;
    // ========================== GLOBAL DEGISKENLER ===============================

    // OnCreate Main sinifi cagirildiginda ilk calisan hayat fonksiyonudur.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Request();

        otopark = findViewById(R.id.otopark);
        arac = findViewById(R.id.arac);
        profil = findViewById(R.id.profil);

        otopark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openOtopark(view);
            }
        });
        arac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openArac(view);
            }
        });
        profil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openProfil(view);
            }
        });


    }

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
            public void onErrorResponse(VolleyError error){ }
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
    public static ArrayList<ArrayList<String>> parseRequest(String response) throws JSONException {
            // Response string fakat string bir json formatinda yazilmis oldugu icin json a cevirip ayristiriyoruz.
            JSONObject jsonObject = new JSONObject(response);
            JSONArray tempArray = (JSONArray) jsonObject.get("Result"); // Istedigimiz bilgiler sadece result basligi altinda oldugundan sadece result i aliyoruz.
            ArrayList<ArrayList<String>> result = new ArrayList<>(); // Sonuclari tek bir listede tutmak icin 2d arraylist olusturuyoruz.
            ArrayList<String> ID = new ArrayList<>();
            ArrayList<String> Header = new ArrayList<>();
            ArrayList<String> Description = new ArrayList<>();
            ArrayList<String> Image = new ArrayList<>();
            ArrayList<String> Url = new ArrayList<>();
            // json a donusen response icinde istedigimiz key lerden valuelari dizilere ekliyoruz.
            // String array yerine arraylist kullanma nedenimiz cevabin boyutunun degisebilmesidir.
            for(int i=0; i<tempArray.length(); i++){
                ID.add(tempArray.getJSONObject(i).getString("ID"));
                Header.add(tempArray.getJSONObject(i).getString("Header"));
                Description.add(tempArray.getJSONObject(i).getString("Description"));
                Image.add(tempArray.getJSONObject(i).getString("Image"));
                Url.add(tempArray.getJSONObject(i).getString("Url"));
            }
            result.add(ID);
            result.add(Header);
            result.add(Description);
            result.add(Image);
            result.add(Url);
            return result;
            // Daha sonra sirayla tum tekli arraylistleri 2d bir sonuc araylistine atip donduruyoruz
        }

        // Bu fonksyionda ayrilan degerleri ihtiyacimiza gore kullaniyoruz.
    public void seperateResult() {
        // Artik arraylist sayesinde sonucun boyutunu bildigimizden string array kullanabiliriz.
        int size = requestResult.get(0).size();
        //separated = new String[size*5]; Tum bilgileri tek dizide sirali tutmak icin 5katiyla genisletiyoruz.
        urls= new String[size];
        titles = new String[size];
        links = new String[size];

        for (int i = 0; i < size; i++) {
            // Su an sadece bu uc diziye ihtiyacimiz var.
            urls[i] = requestResult.get(3).get(i);
            titles[i] = requestResult.get(1).get(i);
            links[i] = requestResult.get(4).get(i);
        }
        // Arayuzdeki viewpager i dinamik olarak sisiriyoruz.
        mPager = findViewById(R.id.pagerMain);
        mPager.setAdapter(new ViewPagerAdapter(Main.this,urls,titles,links)); // Diziler adapter a yollaniyor burada gorsel element data birlesimi yapiliyor.
        CirclePageIndicator indicator = findViewById(R.id.indicatorMain);
        indicator.setViewPager(mPager); // Slider gostergesi
        final float denstiy = getResources().getDisplayMetrics().density;
        indicator.setRadius(5 * denstiy);
        NUM_PAGES = urls.length;
        // Otomatik ve uygulamayi dondurmadan gecis yapmasi icin thread kullaniyoruz.
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            @Override
            public void run() {
                if ( currentPage == NUM_PAGES) currentPage = 0;
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        },3000, 3000); // Slider gecis suresini buradan ayarlayabilirsiniz.

        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
            @Override
            public void onPageSelected(int position) { }
            @Override
            public void onPageScrollStateChanged(int state) { }
        });

    }
    // Tiklanildiginda ilgili aktiviteye yonleendiren open fonksiyonlari
    public void openArac(View view) {
        Intent myIntent = new Intent(Main.this, AracCekme.class);
        Main.this.startActivity(myIntent);
    }
    public void openOtopark(View view) {
        Intent myIntent = new Intent(Main.this, Otopark.class);
        Main.this.startActivity(myIntent);
    }
    public void openProfil(View view){
        Intent myIntent = new Intent(Main.this, Profil.class);
        Main.this.startActivity(myIntent);
    }

}
