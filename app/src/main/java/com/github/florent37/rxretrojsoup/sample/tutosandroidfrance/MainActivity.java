package com.github.florent37.rxretrojsoup.sample.tutosandroidfrance;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.github.florent37.retrojsoup.RetroJsoup;
import com.github.florent37.rxretrojsoup.R;

import java.util.List;

import okhttp3.OkHttpClient;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

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
                .okHttpClient(okHttpClient)
                .build()
                .create(TutosAndroidFrance.class);

        tutosAndroidFrance.articles()
                .toList()

                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(
                        new Action1<List<Article>>() {
                            @Override
                            public void call(List<Article> items) {
                                adapter.addItems(items);
                            }
                        },
                        Throwable::printStackTrace
                );
    }
}
