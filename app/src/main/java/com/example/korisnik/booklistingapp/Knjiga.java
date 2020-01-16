package com.example.korisnik.booklistingapp;

/**
 * Created by Korisnik on 15-Jul-19.
 */

public class Knjiga {
    private String mImeKnjige;
    private String mAutorKnjige;
    private String mSlikaId ;
    private String urlBook;


    public Knjiga(String mImeKnjige, String mAutorKnjige, String mSlikaId,String urlBook) {
        this.mImeKnjige = mImeKnjige;
        this.mAutorKnjige = mAutorKnjige;
        this.mSlikaId = mSlikaId;
        this.urlBook = urlBook;

    }



    public String getmImeKnjige() {
        return mImeKnjige;
    }

    public String getmAutorKnjige() {
        return mAutorKnjige;
    }

    public String getmSlikaId() {
        return mSlikaId;
    }

    public String getUrlBook() {
        return urlBook;
    }
}
