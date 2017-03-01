package com.github.florent37.rxjsoup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

public class RxJsoup {

    private static final String TAG = "RXJSOUP";
    private final String url;
    private Document document;

    private boolean exceptionIfNotFound = false;

    public RxJsoup(String url, boolean exceptionIfNotFound) {
        this.url = url;
        this.exceptionIfNotFound = exceptionIfNotFound;
    }

    public RxJsoup setExceptionIfNotFound(boolean exceptionIfNotFound) {
        this.exceptionIfNotFound = exceptionIfNotFound;
        return this;
    }


    public RxJsoup with(String url) {
        return new RxJsoup(url, false);
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
                public void call(Subscriber<? super Document> subscriber) {
                    try {
                        document = Jsoup.connect(url).get();
                        subscriber.onNext(document);
                        subscriber.onCompleted();
                    } catch (Exception e) {
                        subscriber.onError(e);
                    }
                }
            });
        }
    }

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

    private class NotFoundException extends Exception {

        public NotFoundException(String expression, String document) {
            super("`" + expression + "` not found in `" + document + "`");
        }
    }
}