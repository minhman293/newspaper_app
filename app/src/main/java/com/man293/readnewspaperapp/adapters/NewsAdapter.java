package com.man293.readnewspaperapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.man293.readnewspaperapp.activities.NewsDetail;
import com.man293.readnewspaperapp.models.News;
import com.man293.readnewspaperapp.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;

public class NewsAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private List<News> newsList;
    public ViewHolder holder;

    public NewsAdapter(Context context, int layout, List<News> newsList) {
        this.context = context;
        this.layout = layout;
        this.newsList = newsList;
    }

    @Override
    public int getCount() {
        return newsList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder {
        ImageView img;
        TextView tle, des, date;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, null);
            holder = new ViewHolder();

            holder.tle = (TextView) convertView.findViewById(R.id.txtTitle);
            holder.des = (TextView) convertView.findViewById(R.id.txtNewsDesc);
            holder.date = (TextView) convertView.findViewById(R.id.txtPubDate);
            holder.img = (ImageView) convertView.findViewById(R.id.imgNews);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        News news = newsList.get(position);
        holder.tle.setText(news.getTitle());
        holder.des.setText(news.getDesc());
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        holder.date.setText(formatter.format(news.getPubDate()));
        new AsyncTaskImage().execute(news.getImg());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NewsDetail.class);
                String l = news.getLink();
                intent.putExtra("linkDetail", l);
                intent.putExtra("news", (Serializable) news);
                context.startActivity(intent);
            }
        });

        return convertView;
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
            holder.img.setImageBitmap(bitmap);
        }
    }
}
