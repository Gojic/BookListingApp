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

    public KnjigaAdapter(@NonNull Context context, @NonNull ArrayList<Knjiga> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;

        if (view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.prikaz_knjige, parent, false);
            Knjiga knjiga = getItem(position);

            TextView nazivKnjige = view.findViewById(R.id.ime_knjige);
            nazivKnjige.setText(knjiga.getmImeKnjige());

            TextView imeAutora = view.findViewById(R.id.autor_knjige);
            imeAutora.setText(knjiga.getmAutorKnjige());

            ImageView slikaNaslovneStranice = view.findViewById(R.id.sika_korica);
            Picasso.with(getContext()).load(knjiga.getmSlikaId()).into(slikaNaslovneStranice);

        }

        return view;
    }
}
