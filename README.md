# RxRetroJsoup

Create an interface with `@Select` annotated method
```
public interface TutosAndroidFrance {

    @Select("article")
    Observable<Article> articles();

}
```

Annotate your model

```java

public class Article {

    @JsoupText(".entry-title a")
    String title;

    @JsoupHref(".read-more a")
    String href;

    @JsoupSrc(".entry-thumb img")
    String image;

    @JsoupText(".entry-content p")
    String description;

    public Article() {
    }
    ...

}
```

Build a RetroJsoup

```java
final TutosAndroidFrance tutosAndroidFrance = new RetroJsoup.Builder()
                .url("http://tutos-android-france.com/")
                .build()
                .create(TutosAndroidFrance.class);
```

Fetch your objects !
```java
tutosAndroidFrance.articles()
                .toList()

                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(
                    new Action1<List<Article>>() {
                            @Override
                            public void call(List<Article> items) {

                            }
                });
```