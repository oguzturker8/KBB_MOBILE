package com.example.staj;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONException;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public abstract class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    public List<ProfileData> list_profil;
    private SharedPreferences pref;
    Context context = AracCekme.getContextOfAracCekme();

    public abstract void onItemClick(View v, int position);

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView adsoyad,telefon,plaka,eposta;
        public ImageView delete;
        public CardView card_view;

        public ViewHolder(View view  ) {
            super(view);
            card_view = view.findViewById(R.id.profilCard);
            adsoyad = view.findViewById(R.id.profilAdSoyad);
            telefon = view.findViewById(R.id.profilTelefon);
            plaka =  view.findViewById(R.id.profilPlaka);
            eposta = view.findViewById(R.id.pofilEposta);
            delete = view.findViewById(R.id.profilDelete);

        }
        // Kullanabilmek icin methodlari override etmek zorundayiz ihttiyacimizdan degil.
        @Override
        public void onClick(View view) { }
    }

    // Veriyi arayuz elemanina baglayan fonksiyon.
    public RecyclerViewAdapter(List<ProfileData> list_profil) { this.list_profil = list_profil; }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.profil, parent, false);
        final ViewHolder view_holder = new ViewHolder(v);
        return view_holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        pref = context.getSharedPreferences("Profiles", MODE_PRIVATE);

        holder.adsoyad.setText(list_profil.get(position).getAdSoyad());
        holder.telefon.setText(list_profil.get(position).getTelefon());
        holder.plaka.setText(list_profil.get(position).getPlaka());
        holder.eposta.setText(list_profil.get(position).getEposta());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                   AlertDialog.Builder builder = new AlertDialog.Builder(view.getRootView().getContext())
                           .setTitle("Kaydi Sil")
                           .setMessage("Bu kaydi " + holder.adsoyad.getText().toString() + " gercekten silmek istediginizden emin misiniz?")
                           .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialogInterface, int i) {
                                deleteSelectedProfile(holder.plaka.getText().toString(), view);
                                list_profil.remove(position);
                                notifyItemRemoved(position);
                                notifyItemChanged(position,list_profil.size());

                               }
                           })
                           .setNegativeButton("Hayir",null);
                   builder.show();

            }
        });

        holder.adsoyad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    AracCekme.getInstance().Request(holder.plaka.getText().toString());
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                }
        });


    }

    @Override
    public int getItemCount() {
        return list_profil.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void deleteSelectedProfile(String plaka, View view){
        pref =  context.getSharedPreferences("Profiles", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(plaka+"___1");
        editor.remove(plaka+"___2");
        editor.remove(plaka+"___3");
        editor.remove(plaka+"___4");
        editor.apply();
        Toast.makeText(view.getRootView().getContext(), plaka+" Silindi", Toast.LENGTH_LONG).show();
    }


}