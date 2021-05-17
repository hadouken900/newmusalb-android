package com.example.newmusalb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import com.example.newmusalb.model.Album;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements AlbumAdapter.OnItemClickListener {

    private List<Album> albums = new ArrayList<>();
    private RecyclerView recyclerView;
    private AlbumAdapter albumAdapter = new AlbumAdapter(albums, this);

    private final String URL = "https://newmusalb.herokuapp.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view_albums);

        /*
        albums <--- data
        adapter <-- albums
         */
        clientConnection();
        albums.add(new Album("jija", "asdfsd"));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(albumAdapter);


    }

    private void clientConnection() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                                .url(URL)
                                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String resp = response.body().string();
                    Gson gson = new Gson();
                    Type collectionType = new TypeToken<List<Album>>(){}.getType();
                    albums = gson.fromJson(resp, collectionType);
                    for (Album a: albums) {
                        System.out.println(a.getTitle());
                    }


                    MainActivity.this.runOnUiThread(() -> {
                        albumAdapter.setList(albums);
                        albumAdapter.notifyDataSetChanged();
                    });


                }
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        String title = albums.get(position).getTitle();
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("spotify:search:"+title));
        startActivity(intent);
    }
}