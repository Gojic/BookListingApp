package com.example.korisnik.booklistingapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Korisnik on 15-Jul-19.
 */

public class QueryUtilis {

    private static final String LOG_TAG = QueryUtilis.class.getSimpleName();

    //kreiramo prazan konstruktor zato sto niko ne bi trebao da pravi objekat od ove klase
    private QueryUtilis() {
    }

    //metod sa kojim saljemo upit od goole APi i vratcam kao listu
    public static ArrayList<Knjiga> fetchKnjige(String traziURL) {
        //kreiram URL objekat
        URL url = createURL(traziURL);
        // izvrsava HTTP zahtev i dobija odgovor u JSON
        String jsonOdgovor = "";
        try {
            jsonOdgovor = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "problem sa kreiranjem HTTP zahteva" + e);
        }

        ArrayList<Knjiga> knjige = extractFeatureFromJson(jsonOdgovor);
        return knjige;
    }

    //vraca novi URL objekat od datog String URL-a
    private static URL createURL(String stringUrl) {
        //mora da se inicijalizuje i zato mu je data pocetna vrednost null
        URL url = null;
        try {
            //pravi se objekat i uzima stringUrl kao argument
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem sa stvaranjem URL-a ", e);
        }
        //vraca napravljeni url
        return url;

    }

    //kreiram HTTP zahtev za datu URL adresu i vratiti odgovor u String formatu
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonOdgovor = "";
        //ako je url null odnosno ne postoji ran ije izaci iz ove metode
        if (url == null) {
            return jsonOdgovor;
        }
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;

        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();// zapocinje konekciju sa intenetom
            httpURLConnection.setReadTimeout(10000);//vreme u miliskeundama za vreme koje treba de se ucita url
            httpURLConnection.setConnectTimeout(15000);//vreme u milisekundama za vreme koje treba da se uspostavi konekcija
            httpURLConnection.setRequestMethod("GET");//koristi GET metod odnosno metod za citanje iformacija sa API
            httpURLConnection.connect();//konektuje aplikaciju sa intetnetom

            //ako je konekcija uspesna odnosno ako je kod odgovora 200
            //onda iscitati input stream i frazirati odgovor
            if (httpURLConnection.getResponseCode() == 200) {
                inputStream = httpURLConnection.getInputStream();
                jsonOdgovor = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "POgresan response kod: " + httpURLConnection.getResponseCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //ova linija koda se uvek izvrsava
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            //ukoliko input stream nije nula odnosno postoji ovde ga zatvaramo
            if (inputStream != null) {
                //zatvaranje input stream moze da baci IOException,zato u potpisu metode stoji da ce ova metoda mozda bacati gresku
                inputStream.close();
            }
        }
        return jsonOdgovor;
    }

    //konvertuje InputStream u string koji sadrzi ceo JSON odgovor sa servera
    private static String readFromStream(InputStream inputStream) throws IOException {
        //kreiram StringBuilde objekat
        StringBuilder output = new StringBuilder();
        //ukolliko inputStream nije prazan
        if (inputStream != null) {
            //kreiram InputStreamReader objekat kao argument uzimam inputStrem
            //i Charset,koji specifira kako da prevede inputStream podatke u citljive karaktere bajt po bajt
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            //BufferedReader uzima inputSTreamRedaer za argument zato sto uzima podatke uz inputStreamReadera
            //i omogocuje da citamo liniku teksta op liniju
            BufferedReader reader = new BufferedReader(inputStreamReader);
            //cita liniju teksta
            String line = reader.readLine();
            //cita sve dok ima sta da se cita odnosmo dok line ,nije prazan
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        //vraca rezultat StringBuildera u string formatu
        return output.toString();
    }

    //ovaj metod vraca listu Knjiga objekata koja je kreirana fraziranjem JSON odgovora
    public static ArrayList<Knjiga> extractFeatureFromJson(String jsonString) {

        //prazna lista u koju cemo da dodajemo knjige
        ArrayList<Knjiga> knjigeLista = new ArrayList<>();

        //pokusavam da fraziram SAMPLE_JSON_RESPONSE.AKo postoji problem sa formatiranjem JSON-a,greska ce biti bacena


        try {
            //kreiram JSON objekat koji ce uzimati za argument jsonString odnosno SAMPLE_JSON_RESPONSE
            JSONObject rootObject = new JSONObject(jsonString);
            //dolazim do jsonArray pomocu getJSONArray()
            JSONArray jsonArray = rootObject.getJSONArray("items");
            //sve dok ima podataka u JSON nizu izvlacimo ih
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONObject properties = jsonObject.getJSONObject("volumeInfo");

                String nazivKnjige = properties.getString("title");
                String autorKnjige = properties.getString("authors");
                String coverImagePath = null;
                JSONObject slikaKnjige = properties.optJSONObject("imageLinks");
                if (null != slikaKnjige) {
                    coverImagePath = slikaKnjige.optString("thumbnail");
                }
                //za datu knjigu ekstratovati JSONObject koji je povezan sa kljucem "saleInfo
                JSONObject saleInfo = jsonObject.getJSONObject("saleInfo");
                //Ekstraktujem vrednost za kljuc "buyLink"
                String urlKnjige = (String) saleInfo.get("buyLink");
                //kreiramo novi objekat Knjiga i dajemo nove vrednosti koje smo izvukli iz JSON-a
                Knjiga knjiga = new Knjiga(nazivKnjige, autorKnjige, coverImagePath, urlKnjige);
                knjigeLista.add(knjiga);


            }
        } catch (JSONException e) {

            Log.e("QueryUtilis", "Problem sa fraziranjem JSON odgovora", e);
        }
        //vracamo listu
        return knjigeLista;
    }

}
