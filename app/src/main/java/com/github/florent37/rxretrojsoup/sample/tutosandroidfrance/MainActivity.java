package com.github.florent37.rxretrojsoup.sample.tutosandroidfrance;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.github.florent37.retrojsoup.RetroJsoup;
import com.github.florent37.rxretrojsoup.R;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        adapter = new Adapter();
        recyclerView.setAdapter(adapter);

        loadWithRetroJsoup();
    }

    public void loadWithRetroJsoup() {
        final OkHttpClient okHttpClient = new OkHttpClient();

        final TutosAndroidFrance tutosAndroidFrance = new RetroJsoup.Builder()
                .url("http://tutos-android-france.com/")
                .client(okHttpClient)
                .build()
                .create(TutosAndroidFrance.class);

        tutosAndroidFrance.articles()
                .toList()

                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(
                        adapter::addItems,
                        Throwable::printStackTrace
                );
    }
}
