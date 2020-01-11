package com.example.korisnik.booklistingapp;

import android.app.LoaderManager;
import android.app.job.JobScheduler;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.opengl.Visibility;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
    private ListView listView;
    private TextView prazanView;
    private SearchView pretragaView;
    private Button dugmeZaPretragu;
    private View progressBar;


    private String urlRequestGoogleBooks = "";
    boolean konekcijaUspostavljena;

    private static final int KNJIGA_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        proveraKonekcije(connectivityManager);



        knjigaAdapter = new KnjigaAdapter(this, new ArrayList<Knjiga>());
        listView = findViewById(R.id.lista_svih_knjiga);


        listView.setEmptyView(prazanView);
        listView.setAdapter(knjigaAdapter);
        prazanView = findViewById(R.id.empty_title_text);
        progressBar = findViewById(R.id.loading_spinner);

        if (konekcijaUspostavljena) {
            prazanView.setText(R.string.no_internet);
            progressBar.setVisibility(View.GONE);

        } else {

            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(KNJIGA_LOADER_ID, null, this).forceLoad();

        }
        pretragaView = findViewById(R.id.pretraga);
        pretragaView.onActionViewExpanded();
        pretragaView.setIconified(true);
        pretragaView.setQueryHint("Uneti naziv knjige");

        dugmeZaPretragu = findViewById(R.id.dugmeZaPretragu);
        dugmeZaPretragu.setText("Pretraga");

        dugmeZaPretragu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                updateQuery(pretragaView.getQuery().toString());
                restartLoader();
            }
        });

    }

    private String updateQuery(String vrednstPretrage) {
        if (vrednstPretrage.contains(" ")) {
            vrednstPretrage = vrednstPretrage.replace(" ", "+");
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("https://www.googleapis.com/books/v1/volumes?q=").append(vrednstPretrage).append("&filter=paid-ebooks&maxResults=5");
        urlRequestGoogleBooks = stringBuilder.toString();
        return urlRequestGoogleBooks;
    }

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


        if (knjigas != null && !knjigas.isEmpty()) {
            knjigaAdapter.addAll(knjigas);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Knjiga>> loader) {

        knjigaAdapter.clear();
    }

    private void proveraKonekcije(ConnectivityManager connectivityManager) {

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        konekcijaUspostavljena = activeNetwork != null && activeNetwork.isConnected();
        if (konekcijaUspostavljena != true) {

            konekcijaUspostavljena = false;
        } else {

            konekcijaUspostavljena = true;
        }
    }


    public void restartLoader() {
        prazanView.setVisibility(GONE);
        progressBar.setVisibility(View.VISIBLE);
        getLoaderManager().restartLoader(KNJIGA_LOADER_ID, null, MainActivity.this);
    }
}
