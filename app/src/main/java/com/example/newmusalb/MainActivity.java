package com.example.newmusalb;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.example.newmusalb.model.Album;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements AlbumAdapter.OnItemClickListener {

    private final List<Album> albums = new ArrayList<>();
    private  RecyclerView recyclerView;
    private final AlbumAdapter albumAdapter = new AlbumAdapter(albums, this);

    private static final String URL = "https://newalbumreleases.net/category/cat/";

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

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Document doc = Jsoup.parse(response.body().string());
                    List<String> imgs = doc.select("p").select("img").eachAttr("src");
                    List<String> title = doc.select("h2").select("a").eachText();
                    List<String> title1 = title.stream().map(t -> t.substring(0, t.length() - 7)).collect(Collectors.toList());
                    albums.clear();
                    for (int i = 0; i < imgs.size(); i++) {
                        albums.add(new Album(title1.get(i), imgs.get(i)));
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