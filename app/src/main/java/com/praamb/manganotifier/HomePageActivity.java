package com.praamb.manganotifier;

import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.praamb.manganotifier.util.NotificationUtil;
import com.praamb.manganotifier.worker.KingdomWorker;

import java.util.concurrent.TimeUnit;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;


public class HomePageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        setupNotificationChannel();

        SharedPreferences sharedPreferences = getSharedPreferences("LatestManga",MODE_PRIVATE);

        enqueueKingdomWorkRequest(sharedPreferences);
        setLatestChapters(sharedPreferences);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("LatestManga",MODE_PRIVATE);
        setLatestChapters(sharedPreferences);
    }

    private void enqueueKingdomWorkRequest(SharedPreferences sharedPreferences) {
        PeriodicWorkRequest.Builder kingdomRequestBuilder =
                    new PeriodicWorkRequest.Builder(KingdomWorker.class, 15,
                            TimeUnit.MINUTES);
        PeriodicWorkRequest kingdomWorkRequest = kingdomRequestBuilder.build();
        WorkManager.getInstance().enqueueUniquePeriodicWork("KingdomNotifier",
                ExistingPeriodicWorkPolicy.REPLACE,kingdomWorkRequest);
        sharedPreferences.edit().putString("refreshTaskId",kingdomWorkRequest.getId().toString()).apply();
    }

    private void setupNotificationChannel() {
        String channelId = getString(R.string.notification_channel);
        String channelName = getString(R.string.channel_name);
        String channelDesc = getString(R.string.channel_description);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(NotificationUtil.createNotificationChannel(channelId,channelName,channelDesc));
    }

    private void setLatestChapters(SharedPreferences sharedPreferences)
    {
        int kingdomChapter = sharedPreferences.getInt("kingdom",-1);
        String kC = ""+kingdomChapter;
        //StringBuilder sb = new StringBuilder()
        EditText et = findViewById(R.id.kingdomChapter);
        et.setText(kC);
    }

    public void onClickBtn(View v)
    {
        int kingdomChapter = Integer.parseInt(((EditText)findViewById(R.id.kingdomChapter)).getText().toString());
        SharedPreferences sharedPreferences = getSharedPreferences("LatestManga",MODE_PRIVATE);
        sharedPreferences.edit().putInt("kingdom",kingdomChapter).apply();
        Toast.makeText(this, "Updated Chapter", Toast.LENGTH_LONG).show();

        /*OneTimeWorkRequest compressionWork =
                new OneTimeWorkRequest.Builder(KingdomWorker.class)
                        .build();
        WorkManager.getInstance().enqueue(compressionWork);*/
    }

    public void openKingdomURL(View view){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://reader.sensescans.com/series/kingdom/"));
        startActivity(browserIntent);
    }


}