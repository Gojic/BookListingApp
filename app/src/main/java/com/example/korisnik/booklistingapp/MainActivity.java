package com.example.korisnik.booklistingapp;

import android.app.LoaderManager;
import android.app.job.JobScheduler;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.opengl.Visibility;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Knjiga>> {
    private KnjigaAdapter knjigaAdapter;
    ListView listView;
    private TextView prazanView;
    private SearchView pretragaView;
    private Button dugmeZaPretragu;
    private View progressBar;

    /**
     * url za knjigu iz google books api
     */
    private String urlRequestGoogleBooks = "";

    boolean konekcijaUspostavljena;

    /*
    Ovo je stalna vrednost za loader knjige.Ovde je data vrednost br 1 zato sto iamo samo jedan loader
     */
    private static final int KNJIGA_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //deklarisanje i inicijalizacija connectivity manager-a za proveru internet konekcije
        final ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            proveraKonekcije(connectivityManager);
        }
        //kreiram novi adapter i uzimam praznu listu kao argument
        knjigaAdapter = new KnjigaAdapter(this, new ArrayList<Knjiga>());
        listView = findViewById(R.id.lista_svih_knjiga);

        listView.setEmptyView(prazanView);
        //setuj adapter na listu i za argument uzeti  moj adapter
        listView.setAdapter(knjigaAdapter);
        prazanView = findViewById(R.id.empty_title_text);
        progressBar = findViewById(R.id.loading_spinner);


        pretragaView = findViewById(R.id.pretraga);
        pretragaView.onActionViewExpanded();
        pretragaView.setIconified(true);
        pretragaView.setQueryHint("Uneti naziv knjige");

        dugmeZaPretragu = findViewById(R.id.dugmeZaPretragu);
        dugmeZaPretragu.setText("Pretraga");

        if (konekcijaUspostavljena) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            //vrednost iz getLoaderManager() uzimamo i skladistimo pod loaderMandager da bismo mogli da komuniciramo
            //sa samim loaderom
            LoaderManager loaderManager = getLoaderManager();


            //inicijalizuj loader
            loaderManager.initLoader(KNJIGA_LOADER_ID, null, this);
        } else {


            progressBar.setVisibility(View.GONE);
            prazanView.setText(R.string.no_internet);
        }
        dugmeZaPretragu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //opret proveri internet konekciju
                    proveraKonekcije(connectivityManager);
                if (konekcijaUspostavljena) {
                    //apdejtuj URl i restartuj loader da prikaze rezultate nove pretrage
                    updateQuery(pretragaView.getQuery().toString());
                    restartLoader();

                } else {
                    //ocisti adapter od rethodne pretrage
                    knjigaAdapter.clear();
                    progressBar.setVisibility(View.GONE);
                    prazanView.setText(R.string.no_internet);
                }
            }
        });


        //postavi clickListener na listu,koji kada se klikne na odredjenu knjigu salje intent web broweru da se otvori sajt sa vise informacija otoj knjizi
       listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               //pronaci poziciju knnjige koja je kliknuta
               Knjiga currentBook = knjigaAdapter.getItem(i);
               //konvertuje string url u uri objekat da bih mogao da ga ubacim u intent
               assert currentBook != null;
               Uri buyBookUri = Uri.parse(currentBook.getUrlBook());

               //kreiram novi uri sa uri knjige
               Intent websiteIntent = new Intent(Intent.ACTION_VIEW, buyBookUri);

               //poslati intent da se pokrene nova aktivnost
               startActivity(websiteIntent);
           }
       });

    }
   /*
   * Proveriti da li zahtev sadrzi prazan prostor(space) ako sadrzi onda ga zameniti sa plusom
     */

    private String updateQuery(String vrednstPretrage) {
        if (vrednstPretrage.contains(" ")) {
            vrednstPretrage = vrednstPretrage.replace(" ", "+");
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("https://www.googleapis.com/books/v1/volumes?q=").append(vrednstPretrage).append("&filter=paid-ebooks&maxResults=40");
        urlRequestGoogleBooks = stringBuilder.toString();
        return urlRequestGoogleBooks;
    }
    //kreiram loader sa datim URL-om
    @Override
    public Loader<List<Knjiga>> onCreateLoader(int i, Bundle bundle) {
        updateQuery(pretragaView.getQuery().toString());
        return new KnjigaLoader(this, urlRequestGoogleBooks);
    }


    @Override
    public void onLoadFinished(Loader<List<Knjiga>> loader, List<Knjiga> knjigas) {

        View loadingIndicator = findViewById(R.id.loading_spinner);
        loadingIndicator.setVisibility(GONE);


        prazanView.setText(R.string.no_books);

        knjigaAdapter.clear();

        //ako postoji lista knjiga ond je dodati adapteru.Ovo ce pokrenuti listu da se apdejtuje
        if (knjigas != null && !knjigas.isEmpty()) {
            knjigaAdapter.addAll(knjigas);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Knjiga>> loader) {
//loader se reseuje da bi smo mogli da  da  bismo octisli pretjpdnu prettragu
        knjigaAdapter.clear();
    }

    public void restartLoader() {
        prazanView.setVisibility(GONE);
        progressBar.setVisibility(View.VISIBLE);
        getLoaderManager().restartLoader(KNJIGA_LOADER_ID, null, MainActivity.this);
    }

    private void proveraKonekcije(ConnectivityManager connectivityManager) {

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        konekcijaUspostavljena = activeNetwork != null && activeNetwork.isConnected();
        if (activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting()) {
            konekcijaUspostavljena = true;

        } else {
            konekcijaUspostavljena = false;
        }
    }
}
