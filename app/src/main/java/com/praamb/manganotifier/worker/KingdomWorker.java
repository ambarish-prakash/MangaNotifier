package com.praamb.manganotifier.worker;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.praamb.manganotifier.HomePageActivity;
import com.praamb.manganotifier.util.NotificationUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.IOException;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static android.content.Context.MODE_PRIVATE;

public class KingdomWorker extends Worker {

    private Context context;

    public KingdomWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
    }

    @Override
    public Result doWork() {

        StringBuilder sb = new StringBuilder();
        sb.append("Latest chapter is Chapter ");
        int latestChapter = 1;
        boolean foundKingdom = false;
        try {
            Document doc = Jsoup.connect("http://reader.sensescans.com/feeds/rss").get();
            Elements elements = doc.select("item");
            for(Element element: elements)
            {
                String title = ((TextNode)element.childNode(1).childNode(0)).getWholeText();
                String mangaName = title.split(" ")[0];
                if(!mangaName.equals("Kingdom"))
                    continue;
                else
                {
                    latestChapter = Integer.parseInt(title.split(" ")[3].split(":")[0]);
                    sb.append(latestChapter);
                    foundKingdom = true;
                    break;
                }
            }
        } catch (IOException e) {
            return Result.failure();
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences("LatestManga",MODE_PRIVATE);
        int k = sharedPreferences.getInt("kingdom",100);

        if(foundKingdom && latestChapter>k) {
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, HomePageActivity.class), 0);
            NotificationUtil.notify(context, "Kingdom", sb.toString(), pendingIntent);
            sharedPreferences.edit().putInt("kingdom",latestChapter).apply();
        }


        return Result.success();
    }
}
