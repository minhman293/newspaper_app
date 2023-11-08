package com.man293.readnewspaperapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

import com.man293.readnewspaperapp.R;
import com.man293.readnewspaperapp.adapters.NewsAdapter;
import com.man293.readnewspaperapp.models.News;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

public class NewsListView extends AppCompatActivity {

    ListView lvNews;
    ArrayList<News> arrNews;
    NewsAdapter newsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list_view);

        lvNews = (ListView) findViewById(R.id.lvNewspaper);
        arrNews = new ArrayList<>();

        new NewspaperInternet().execute("https://thanhnien.vn/rss/giao-duc.rss");

        newsAdapter = new NewsAdapter(this, R.layout.single_news_layout, arrNews);

        lvNews.setAdapter(newsAdapter);
    }


    private class NewspaperInternet extends AsyncTask<String, Void, String> {

        // this method to read data from url and return a string of data
        @Override
        protected String doInBackground(String... strings) {
            StringBuilder builder = new StringBuilder();

            try {
                URL url = new URL(strings[0]);
                InputStreamReader inputStreamReader = new InputStreamReader(url.openConnection().getInputStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    builder.append(line);
                }
                bufferedReader.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return builder.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            XMLDOMParser xmldomParser = new XMLDOMParser();

            Document document = xmldomParser.getDocument(s);

            // Clear the existing data
            arrNews.clear();

            // get all item
            NodeList nodeList = document.getElementsByTagName("item");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element e = (Element) nodeList.item(i);
                String tlee = xmldomParser.getValue(e, "title");
                String imgg = xmldomParser.getImageSrcFromDescription(e);
                String desc = xmldomParser.getDescriptionContent(e);
                Date datee = xmldomParser.getDateFromPubDate(e);
                String link = xmldomParser.getValue(e, "link");
                arrNews.add(new News(i, imgg, tlee, desc, link, datee));
            }

            // Notify the adapter that the data has changed
            newsAdapter.notifyDataSetChanged();
        }
    }
}