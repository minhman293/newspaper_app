package com.man293.readnewspaperapp.models;

import java.io.Serializable;
import java.util.Date;

public class News implements Serializable {
    private int id;
    private String title, desc, img, link;
    private Date pubDate;

    public News(int id, String img, String title, String desc, String link, Date pubDate) {
        this.id = id;
        this.img = img;
        this.title = title;
        this.desc = desc;
        this.link = link;
        this.pubDate = pubDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }
}

