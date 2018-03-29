package com.github.florent37.rxretrojsoup.sample.androidarsenal;

import com.github.florent37.retrojsoup.annotations.Select;

import io.reactivex.Observable;

/**
 * Created by florentchampigny on 01/03/2017.
 */

public interface AndroidArsenal {

    @Select("#projects .pi")
    Observable<Project> projects();

}
