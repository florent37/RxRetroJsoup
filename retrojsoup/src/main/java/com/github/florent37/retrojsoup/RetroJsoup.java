package com.github.florent37.retrojsoup;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by florentchampigny on 01/03/2017.
 */
public class RetroJsoup {

    private static final String RETRO_JSOUP = "RetroJsoup";

    private String url;
    private Boolean exceptionIfNotFound;

    private RetroJsoup() {
    }

    public <T> T create(Class<T> theClass) {
        try {
            final Class<?> aClass = Class.forName(theClass.getCanonicalName() + RETRO_JSOUP);
            final Constructor<?> constructor = aClass.getDeclaredConstructor(String.class, Boolean.class);
            final T instance = (T) constructor.newInstance(url, exceptionIfNotFound);
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

        public Builder() {
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
            retroJsoup.exceptionIfNotFound = this.exceptionIfNotFound;
            return retroJsoup;
        }
    }
}
