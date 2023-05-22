package com.example.musicmate;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    String[] items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        listView = findViewById(R.id.listViewApp);


        permissions();
    }

    // for asking permission from user
    public void permissions(){
        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {


                        ArrayList<File> mySongs = findSong(Environment.getExternalStorageDirectory());
                        items = new String[mySongs.size()];
                        for(int i=0; i<mySongs.size(); i++){
                            items[i] = mySongs.get(i).getName().toString().replace(".mp3","")
                                    .replace(".wav","");
                        }

                        customAdapter customAdapter = new customAdapter();
                        listView.setAdapter(customAdapter);

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                String songName = (String) listView.getItemAtPosition(i);
                                startActivity(new Intent(getApplicationContext(), PlayerActivity.class)
                                        .putExtra("songs", mySongs)
                                        .putExtra("songname", songName)
                                        .putExtra("pos", i));
                            }
                        });

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    public ArrayList<File> findSong(File file){
        ArrayList arrayList = new ArrayList();
        File[] songs = file.listFiles();
        if (songs!=null) {
            for (File myFile : songs){
                if (myFile.isDirectory() && !myFile.isHidden()) {
                    arrayList.addAll(findSong(myFile));
                }
                else {
                    if (myFile.getName().endsWith(".mp3") || myFile.getName().endsWith(".wav")) {
                        arrayList.add(myFile);
                    }
                }
            }
        }
        return arrayList;
    }

    class customAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View songView = getLayoutInflater().inflate(R.layout.list_songs,null);
            TextView textSong = songView.findViewById(R.id.songname);
            textSong.setSelected(true);
            textSong.setText(items[i]);

            return songView;
        }
    }

}