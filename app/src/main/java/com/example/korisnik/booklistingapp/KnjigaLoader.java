package com.example.korisnik.booklistingapp;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

/**
 * Created by Korisnik on 19-Jul-19.
 */

public class KnjigaLoader extends android.content.AsyncTaskLoader<List<Knjiga>>{
    /** zahtev za URl-om */
    private String mUrl;
    public KnjigaLoader(Context context,String url) {
        super(context);
        mUrl = url;

    }
    //ova klasa je automatski pozvana iz initLoader
    @Override
    protected void onStartLoading() {
        //potrebni je da bi pokrenuo loader da onavlja posao u pozadini
        //kada pozovemo forceLoad(); loader je onda pokrenut korak 1
        forceLoad();
    }
    /**
     * posle  ovaj metod pocinje da izvrsava zadatak u pozadini i vraca resultat
     * kada je loader zavrsio sa ubacivanjem podataka obaveestice loadermanager
     * koji ce zatim da prosledi odatke  onLoad finished metodi
     * korak 2
     *
     */
    @Override
    public List<Knjiga> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        //Fraziram odgovor i ekstraktujem listu knjiga
        List<Knjiga> knjige = QueryUtilis.fetchKnjige(mUrl);
        return knjige;
    }

}
