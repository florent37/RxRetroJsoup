package com.github.florent37.rxretrojsoup.sample.tutosandroidfrance;

import com.github.florent37.retrojsoup.annotations.JsoupHref;
import com.github.florent37.retrojsoup.annotations.JsoupSrc;
import com.github.florent37.retrojsoup.annotations.JsoupText;

/**
 * Created by florentchampigny on 28/02/2017.
 */

public class Article {

    @JsoupText(".entry-title a")
    public String title;

    @JsoupHref(".read-more a")
    public String href;

    @JsoupSrc(".entry-thumb img")
    public String image;

    @JsoupText(".entry-content p")
    public String description;

    public Article() {
    }

    @Override
    public String toString() {
        return title + "\n" +
                href + "\n" +
                image + "\n" +
                description + "\n";
    }
}
