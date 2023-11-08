package com.man293.readnewspaperapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.man293.readnewspaperapp.R;
import com.man293.readnewspaperapp.models.News;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class NewsDetail extends AppCompatActivity {

    TextView tvTitleDetail, tvContent;
    ImageView imvNewsDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        // initialize
        tvTitleDetail = (TextView) findViewById(R.id.tvTitleNewsDetail);
        tvContent = (TextView) findViewById(R.id.tvNewsContent);
        imvNewsDetail = (ImageView) findViewById(R.id.imvNewsDetail);



        // get link from previous intent
        Intent intent = getIntent();
        News news = (News) intent.getSerializableExtra("news");
        //String linkDetail = (String) intent.getStringExtra("linkDetail");
        String linkDetail = news.getLink();

        // get data from internet using asynctask
        new LoadDetailNews().execute(linkDetail);

        // set img detail
        new AsyncTaskImage().execute(news.getImg());
    }

    // show detail news
    private class LoadDetailNews extends AsyncTask<String, Void, String> {

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
            // still ok here
        }

        //        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//
//            try {
//                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//                DocumentBuilder builder = factory.newDocumentBuilder();
//                InputSource is = new InputSource(new StringReader(s));
//                Document document = builder.parse(is);
//
//                // Find the meta tag with the name attribute "description"
//                NodeList metaList = document.getElementsByTagName("meta");
//                String descriptionContent = null;
//
//                for (int i = 0; i < metaList.getLength(); i++) {
//                    Element metaElement = (Element) metaList.item(i);
//                    if (metaElement.hasAttribute("name") && metaElement.getAttribute("name").equals("description")) {
//                        descriptionContent = metaElement.getAttribute("content");
//                        break;
//                    }
//                }
//
//                //tvTitleDetail.setText(descriptionContent); // Set the text in tvTitleDetail
//                Toast.makeText(NewsDetail.this, "number: " + metaList.getLength(), Toast.LENGTH_SHORT).show();
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                return;
//            }
//        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                org.jsoup.nodes.Document doc = Jsoup.parse(s);

                // DESCRIPTION
                // Find the meta tag with the name attribute "description"
                org.jsoup.nodes.Element metaTag = doc.select("meta[property=og:title]").first();
                if (metaTag != null) {
                    String descriptionContent = metaTag.attr("content");
                    tvTitleDetail.setText(descriptionContent);
                } else {
                    // Handle the case where the meta tag is not found
                }

                // CONTENT
                // Get the body element
                org.jsoup.nodes.Element body = doc.body();

                // Find all <p> elements within the body
                Elements paragraphs = body.select("p");

                // Create a StringBuilder to store the content of the <p> elements
                StringBuilder paragraphContent = new StringBuilder();

                // Loop through the <p> elements and append their content to the StringBuilder
                for (org.jsoup.nodes.Element paragraph : paragraphs) {
                    String paragraphText = paragraph.text();
                    paragraphContent.append(paragraphText).append("\n");
                }

                // Set the text in a TextView or handle the content as needed
                String allParagraphsText = paragraphContent.toString();
                tvContent.setText(allParagraphsText);

            } catch (Exception e) {
                e.printStackTrace();
                // Handle the parsing exception
            }

        }


    }
    // asynctask load img from internet
    private class AsyncTaskImage extends AsyncTask<String, Void, Bitmap> {

        Bitmap bitmapImg;

        @Override
        protected Bitmap doInBackground(String... strings) {
            try {

                URL imgUrl = new URL(strings[0]);

                InputStream inputStream = imgUrl.openConnection().getInputStream();

                bitmapImg = BitmapFactory.decodeStream(inputStream);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bitmapImg;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            imvNewsDetail.setImageBitmap(bitmap);
        }
    }

}