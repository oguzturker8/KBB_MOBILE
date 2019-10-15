package com.example.staj;

public class ProfileData {

    // Profil listesi icin gerekli data modeli adapter buna gore verileri bagliyor.
    private String adSoyad, eposta, plaka, telefon;

    public String getAdSoyad()
    {
        return this.adSoyad;
    }
    public String getPlaka() { return this.plaka; }
    public String getTelefon() {return this.telefon; }
    public String getEposta() {return this.eposta; }

    public ProfileData(String adSoyad,String telefon,String plaka, String eposta)
    {
        this.adSoyad = adSoyad;
        this.telefon = telefon;
        this.plaka = plaka;
        this.eposta = eposta;
    }
    public ProfileData() { }
}
