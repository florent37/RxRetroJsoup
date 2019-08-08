package com.github.florent37.rxjsoup;

import org.jetbrains.annotations.Nullable;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RxJsoup {

    private static final String TAG = "RXJSOUP";
    private final String url;
    private Document document;

    @Nullable
    private OkHttpClient okHttpClient = null;

    private boolean exceptionIfNotFound = false;

    public RxJsoup(String url, boolean exceptionIfNotFound, OkHttpClient okHttpClient) {
        this.url = url;
        this.exceptionIfNotFound = exceptionIfNotFound;
        this.okHttpClient = okHttpClient;
    }

    //RxJsoup.connect(
    //                  Jsoup.connect("www.gggggggg.fr")
    //                      .userAgent(MY_USER_AGENT)
    //                      .data("credential", email)
    //                      .data("pwd", password)
    //                      .cookies(loginForm.cookies())
    //                      .method(Connection.Method.POST)
    //
    //                  ).subscibe( response -> {})
    public static Observable<Connection.Response> connect(final Connection jsoupConnection) {
        return Observable.create(new ObservableOnSubscribe<Connection.Response>() {
            @Override
            public void subscribe(ObservableEmitter<Connection.Response> observableEmitter) throws Exception {
                try {
                    final Connection.Response response = jsoupConnection.execute();
                    observableEmitter.onNext(response);
                    observableEmitter.onComplete();
                } catch (Exception e) {
                    observableEmitter.onError(e);
                }
            }

        });
    }

    public RxJsoup setExceptionIfNotFound(boolean exceptionIfNotFound) {
        this.exceptionIfNotFound = exceptionIfNotFound;
        return this;
    }

    public static RxJsoup with(String url) {
        return new RxJsoup(url, false, null);
    }

    public Observable<String> attr(final Element element, final String expression, final String attr) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> observableEmitter) throws Exception {
                final Elements elements = element.select(expression);
                if (elements.isEmpty() && exceptionIfNotFound) {
                    observableEmitter.onError(new NotFoundException(expression, element.toString()));
                } else {
                    if (elements.isEmpty()) {
                        observableEmitter.onNext("");
                    } else {
                        for (Element e : elements) {
                            observableEmitter.onNext(e.attr(attr));
                        }
                    }
                    observableEmitter.onComplete();
                }
            }
        });
    }

    public Observable<String> href(Element element, String expression) {
        return attr(element, expression, "href");
    }

    public Observable<String> src(Element element, String expression) {
        return attr(element, expression, "src");
    }

    public Observable<String> text(final Element element, final String expression) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> observableEmitter) throws Exception {
                final Elements elements = element.select(expression);
                if (elements.isEmpty() && exceptionIfNotFound) {
                    observableEmitter.onError(new NotFoundException(expression, element.toString()));
                } else {
                    if (elements.isEmpty()) {
                        observableEmitter.onNext("");
                    } else {
                        for (Element e : elements) {
                            observableEmitter.onNext(e.text());
                        }
                    }
                    observableEmitter.onComplete();
                }
            }


        });
    }

    private Observable<Document> document() {
        if (document != null) {
            return Observable.just(document);
        } else {
            return Observable.create(new ObservableOnSubscribe<Document>() {
                @Override
                public void subscribe(final ObservableEmitter<Document> observableEmitter) throws Exception {
                    if (okHttpClient != null) {
                        final Request request = new Request.Builder()
                                .url(url)
                                .get()
                                .build();
                        okHttpClient.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                observableEmitter.onError(e);
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                document = Jsoup.parse(response.body().string(), url);
                                observableEmitter.onNext(document);
                                observableEmitter.onComplete();
                            }
                        });
                    } else {
                        //use default jsoup http client
                        try {
                            document = Jsoup.connect(url).get();
                            observableEmitter.onNext(document);
                            observableEmitter.onComplete();
                        } catch (Exception e) {
                            observableEmitter.onError(e);
                        }
                    }
                }
            });
        }
    }

    //example

    public Observable<Element> select(final String expression) {
        return document().flatMap(
                new Function<Document, ObservableSource<Element>>() {
                    @Override
                    public ObservableSource<Element> apply(@NonNull final Document document) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<Element>() {
                            @Override
                            public void subscribe(ObservableEmitter<Element> observableEmitter) {
                                final Elements elements = document.select(expression);
                                if (elements.isEmpty() && exceptionIfNotFound) {
                                    observableEmitter.onError(new NotFoundException(expression, "document"));
                                } else {
                                    for (Element element : elements) {
                                        observableEmitter.onNext(element);
                                    }
                                    observableEmitter.onComplete();
                                }
                            }
                        });
                    }
                });
    }

    public Observable<Element> getElementsByAttributeValue(final Element element, final String key, final String value) {
        return Observable.create(new ObservableOnSubscribe<Element>() {
            @Override
            public void subscribe(ObservableEmitter<Element> observableEmitter) throws Exception {
                final Elements elements = element.getElementsByAttributeValue(key, value);
                if (elements.isEmpty() && exceptionIfNotFound) {
                    observableEmitter.onError(new NotFoundException(key + " " + value, element.toString()));
                } else {
                    for (Element e : elements) {
                        observableEmitter.onNext(e);
                    }
                    observableEmitter.onComplete();
                }
            }
        });
    }

    private class NotFoundException extends Exception {

        public NotFoundException(String expression, String document) {
            super("`" + expression + "` not found in `" + document + "`");
        }
    }
}
