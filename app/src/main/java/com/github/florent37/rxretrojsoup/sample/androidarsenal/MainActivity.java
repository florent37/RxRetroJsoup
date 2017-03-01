package com.github.florent37.rxretrojsoup.sample.androidarsenal;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.github.florent37.retrojsoup.RetroJsoup;
import com.github.florent37.rxjsoup.RxJsoup;
import com.github.florent37.rxretrojsoup.R;
import com.github.florent37.rxretrojsoup.sample.tutosandroidfrance.TutosAndroidFrance;

import rx.android.schedulers.AndroidSchedulers;
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
        final AndroidArsenal tutosAndroidFrance = new RetroJsoup.Builder()
                .url("https://android-arsenal.com/")
                .build()
                .create(AndroidArsenal.class);

        tutosAndroidFrance.projects()
                .toList()

                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(
                        adapter::addItems,
                        t -> Toast.makeText(getBaseContext(), t.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
