package com.example.korisnik.booklistingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class KnjigaAdapter extends ArrayAdapter<Knjiga>{
    /**
     *   Ovde inicijalizujem ArrayAdapter
     * @param context ovo je kontekst
     * @param objects ovaj argument koristimo kada nas ArrayAdapter popui TextView
     * Posto je ovo nas adapter sa dva TextView i jednog ImageView ne treba nam taj argument pa je zato
     * njegovs vrednost nula
     */
    public KnjigaAdapter(@NonNull Context context, @NonNull ArrayList<Knjiga> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //proveravamo da li je vec postoji view ako ne onda ga napraviti
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.prikaz_knjige, parent, false);
        }
        //dobiti poziciju u adapteru Knjige
            Knjiga knjiga = getItem(position);

        //naci TexView u prikaz_knjige.xml
            TextView nazivKnjige = view.findViewById(R.id.ime_knjige);
            //setovati odgovarajucu vrednost u tom view-u
            nazivKnjige.setText(knjiga.getmImeKnjige());

            TextView imeAutora = view.findViewById(R.id.autor_knjige);
            imeAutora.setText(knjiga.getmAutorKnjige());

            ImageView slikaNaslovneStranice = view.findViewById(R.id.sika_korica);
            //{Pomocu picassa sam nasao i pikazao koricu knjige
            Picasso.with(getContext()).load(knjiga.getmSlikaId()).into(slikaNaslovneStranice);



        return view;
    }
}
