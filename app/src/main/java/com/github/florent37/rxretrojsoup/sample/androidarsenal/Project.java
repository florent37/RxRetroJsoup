package com.github.florent37.rxretrojsoup.sample.androidarsenal;

import com.github.florent37.retrojsoup.annotations.JsoupHref;
import com.github.florent37.retrojsoup.annotations.JsoupText;

/**
 * Created by florentchampigny on 28/02/2017.
 */

public class Project {

    @JsoupText(".title a:eq(0)")
    public String title;

    @JsoupHref(".title a:eq(1)")
    public String href;

    @JsoupText(".desc p")
    public String description;

    @JsoupText(".title :eq(1)")
    public String category;

    public Project() {
    }

    @Override
    public String toString() {
        return title + "\n" +
                href + "\n" +
                category + "\n" +
                description + "\n";
    }
}
