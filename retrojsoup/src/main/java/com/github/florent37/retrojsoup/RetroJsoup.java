package com.github.florent37.retrojsoup;

import com.github.florent37.rxjsoup.RxJsoup;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import okhttp3.OkHttpClient;

/**
 * Created by florentchampigny on 01/03/2017.
 */
public class RetroJsoup {

    private static final String RETRO_JSOUP = "RetroJsoup";

    private String url;
    private Boolean exceptionIfNotFound;
    private OkHttpClient okHttpClient;

    private RetroJsoup() {
    }

    public <T> T create(Class<T> theClass) {
        try {
            final Class<?> aClass = Class.forName(theClass.getCanonicalName() + RETRO_JSOUP);
            final Constructor<?> constructor = aClass.getDeclaredConstructor(RxJsoup.class);

            final RxJsoup rxJsoup = new RxJsoup(url, exceptionIfNotFound, okHttpClient);

            final T instance = (T) constructor.newInstance(rxJsoup);
            return instance;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class Builder {
        private String url;
        private boolean exceptionIfNotFound = false;

        private OkHttpClient okHttpClient;

        public Builder() {
        }

        public Builder client(OkHttpClient okHttpClient){
            this.okHttpClient = okHttpClient;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder exceptionIfNotFound(){
            this.exceptionIfNotFound = true;
            return this;
        }

        public RetroJsoup build() {
            final RetroJsoup retroJsoup = new RetroJsoup();
            retroJsoup.url = this.url;
            retroJsoup.okHttpClient = this.okHttpClient;
            retroJsoup.exceptionIfNotFound = this.exceptionIfNotFound;
            return retroJsoup;
        }
    }
}
