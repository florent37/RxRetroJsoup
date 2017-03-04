package com.github.florent37.rxjsoup;

import org.jetbrains.annotations.Nullable;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

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
        return Observable.create(new Observable.OnSubscribe<Connection.Response>() {
            @Override
            public void call(Subscriber<? super Connection.Response> subscriber) {
                try {
                    final Connection.Response response = jsoupConnection.execute();
                    subscriber.onNext(response);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public RxJsoup setExceptionIfNotFound(boolean exceptionIfNotFound) {
        this.exceptionIfNotFound = exceptionIfNotFound;
        return this;
    }

    public RxJsoup with(String url) {
        return new RxJsoup(url, false, null);
    }

    public Observable<String> attr(final Element element, final String expression, final String attr) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                final Elements elements = element.select(expression);
                if (elements.isEmpty() && exceptionIfNotFound) {
                    subscriber.onError(new NotFoundException(expression, element.toString()));
                } else {
                    for (Element e : elements) {
                        subscriber.onNext(e.attr(attr));
                    }
                    subscriber.onCompleted();
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
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                final Elements elements = element.select(expression);
                if (elements.isEmpty() && exceptionIfNotFound) {
                    subscriber.onError(new NotFoundException(expression, element.toString()));
                } else {
                    for (Element e : elements) {
                        subscriber.onNext(e.text());
                    }
                    subscriber.onCompleted();
                }
            }
        });
    }

    private Observable<Document> document() {
        if (document != null) {
            return Observable.just(document);
        } else {
            return Observable.create(new Observable.OnSubscribe<Document>() {
                @Override
                public void call(final Subscriber<? super Document> subscriber) {
                    if (okHttpClient != null) {
                        final Request request = new Request.Builder()
                                .url(url)
                                .get()
                                .build();
                        okHttpClient.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                subscriber.onError(e);
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                document = Jsoup.parse(response.body().string(), url);
                                subscriber.onNext(document);
                                subscriber.onCompleted();
                            }
                        });
                    } else {
                        //use default jsoup http client
                        try {
                            document = Jsoup.connect(url).get();
                            subscriber.onNext(document);
                            subscriber.onCompleted();
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                }
            });
        }
    }

    //example

    public Observable<Element> select(final String expression) {
        return document().flatMap(
                new Func1<Document, Observable<? extends Element>>() {
                    @Override
                    public Observable<? extends Element> call(final Document document) {
                        return Observable.create(new Observable.OnSubscribe<Element>() {
                            @Override
                            public void call(Subscriber<? super Element> subscriber) {
                                final Elements elements = document.select(expression);
                                if (elements.isEmpty() && exceptionIfNotFound) {
                                    subscriber.onError(new NotFoundException(expression, "document"));
                                } else {
                                    for (Element element : elements) {
                                        subscriber.onNext(element);
                                    }
                                    subscriber.onCompleted();
                                }
                            }
                        });
                    }
                }

        );
    }

    public Observable<Element> getElementsByAttributeValue(final Element element, final String key, final String value) {
        return Observable.create(new Observable.OnSubscribe<Element>() {
            @Override
            public void call(Subscriber<? super Element> subscriber) {
                final Elements elements = element.getElementsByAttributeValue(key, value);
                if (elements.isEmpty() && exceptionIfNotFound) {
                    subscriber.onError(new NotFoundException(key + " " + value, element.toString()));
                } else {
                    for (Element e : elements) {
                        subscriber.onNext(e);
                    }
                    subscriber.onCompleted();
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