package com.example.musicmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        updateSeekBar.interrupt();
    }

    Button btnPlay, btnNext, btnPrev, btnff, btnfr;
    TextView txtsongname, txtsongstart, txtsongstop;
    SeekBar seekBarmusic, seekBarvolume;

    String strSongName;

    public static final String EXTRA_NAME = "song_name";
    MediaPlayer mediaPlayer;
    AudioManager audioManager;
    int position;
    ArrayList<File> mySongs;

    Thread updateSeekBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        btnPlay = findViewById(R.id.playpause);
        btnPrev = findViewById(R.id.previous);
        btnNext = findViewById(R.id.next);
        btnff = findViewById(R.id.fastforward);
        btnfr = findViewById(R.id.fastreverse);
        txtsongname = findViewById(R.id.textSong);
        txtsongstart = findViewById(R.id.textStart);
        txtsongstop = findViewById(R.id.textEnd);
        seekBarmusic = findViewById(R.id.seekbarMusic);

        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        mySongs = (ArrayList) bundle.getParcelableArrayList("songs");
        String SongName = intent.getStringExtra("songname");
        position = bundle.getInt("pos",0);
        txtsongname.setSelected(true);
        Uri uri = Uri.parse(mySongs.get(position).toString());
        strSongName = mySongs.get(position).getName();
        txtsongname.setText(strSongName);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();


        //seekbar(song) total duration
        seekBarmusic.setMax(mediaPlayer.getDuration());


        seekBarmusic.getProgressDrawable().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.MULTIPLY);
        seekBarmusic.getThumb().setColorFilter(getResources().getColor(R.color.blue), PorterDuff.Mode.SRC_IN);

        seekBarmusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        //song end time
        String endTime = createTime(mediaPlayer.getDuration());
        txtsongstop.setText(endTime);

        final Handler handler = new Handler();
        final int delay = 1000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentTime = createTime(mediaPlayer.getCurrentPosition());
                txtsongstart.setText(currentTime);
                handler.postDelayed(this, delay);

            }
        },delay);


        updateSeekBar = new Thread(){
            @Override
            public void run() {
                int currentposition = 0;

                try{
                    while(currentposition<mediaPlayer.getDuration()){

                        sleep(500);
                        currentposition = mediaPlayer.getCurrentPosition();
                        seekBarmusic.setProgress(currentposition);
                    }
                }
                catch (InterruptedException | IllegalStateException e){
                    e.printStackTrace();
                }
            }
        };

        updateSeekBar.start();


        //volume seekBar
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        seekBarvolume = findViewById(R.id.seekbarVolume);

        seekBarvolume.setMax(maxVolume);
        seekBarvolume.setProgress(currentVolume);

        seekBarvolume.getProgressDrawable().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.MULTIPLY);
        seekBarvolume.getThumb().setColorFilter(getResources().getColor(R.color.blue), PorterDuff.Mode.SRC_IN);

        seekBarvolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                float volume = progress / 100f;
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()){
                    btnPlay.setBackgroundResource(R.drawable.play);
                    mediaPlayer.pause();
                }
                else {
                    btnPlay.setBackgroundResource(R.drawable.pause);
                    mediaPlayer.start();
                }
            }
        });


        //Button for next song
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position+1)%mySongs.size());
                Uri uri = Uri.parse(mySongs.get(position).toString());
                strSongName = mySongs.get(position).getName();
                txtsongname.setText(strSongName);

                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();

                //seekBar maximum limit
                seekBarmusic.setMax(mediaPlayer.getDuration());

                //song end time
                String endTime = createTime(mediaPlayer.getDuration());
                txtsongstop.setText(endTime);
                btnPlay.setBackgroundResource(R.drawable.pause);
            }
        });


        //Button for previous song
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position-1)<0)?(mySongs.size()-1):(position-1);
                Uri uri = Uri.parse(mySongs.get(position).toString());
                strSongName = mySongs.get(position).getName();
                txtsongname.setText(strSongName);

                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();

                //seekBar maximum limit
                seekBarmusic.setMax(mediaPlayer.getDuration());

                //song end time
                String endTime = createTime(mediaPlayer.getDuration());
                txtsongstop.setText(endTime);
                btnPlay.setBackgroundResource(R.drawable.pause);
            }
        });


        //Button for fastforward
        btnff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+10000);
                }
            }
        });

        //Button for fastrewind
        btnfr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-10000);
                }
            }
        });



    }

    public String createTime(int duration){
        String time = "";
        int min = duration/1000/60;
        int sec = duration/1000%60;

        time+=min+":";
        if(sec<10){
            time+="0";
        }
        time+=sec;

        return time;
    }
}