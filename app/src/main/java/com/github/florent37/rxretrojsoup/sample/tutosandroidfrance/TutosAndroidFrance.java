package com.github.florent37.rxretrojsoup.sample.tutosandroidfrance;

import com.github.florent37.retrojsoup.annotations.Select;

import io.reactivex.Observable;

/**
 * Created by florentchampigny on 28/02/2017.
 */
public interface TutosAndroidFrance {

    @Select("article")
    Observable<Article> articles();

}
