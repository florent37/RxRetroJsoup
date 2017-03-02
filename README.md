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

#RxJsoup

```java
RxJsoup.with(url)
    .select("article")
    .flatMap(element -> rxJsoup.text(element, ".entry-title a"))
    .subscribe(text -> {});

```


#Download

<a href='https://ko-fi.com/A160LCC' target='_blank'><img height='36' style='border:0px;height:36px;' src='https://az743702.vo.msecnd.net/cdn/kofi1.png?v=0' border='0' alt='Buy Me a Coffee at ko-fi.com' /></a>

In your module [![Download](https://api.bintray.com/packages/florent37/maven/RetroJsoup/images/download.svg)](https://bintray.com/florent37/maven/RetroJsoup/_latestVersion)
```groovy
compile 'com.github.florent37:retrojsoup:1.0.0'
compile 'com.github.florent37:rxjsoup:1.0.0'

annotationProcessor 'com.github.florent37:retrojsoup-compiler:1.0.0'

//don't forget to include jsoup & rxjava
compile 'org.jsoup:jsoup:1.10.2'
compile 'io.reactivex:rxjava:1.2.7'
```

# Credits

Author: Florent Champigny

<a href="https://plus.google.com/+florentchampigny">
  <img alt="Follow me on Google+"
       src="https://raw.githubusercontent.com/florent37/DaVinci/master/mobile/src/main/res/drawable-hdpi/gplus.png" />
</a>
<a href="https://twitter.com/florent_champ">
  <img alt="Follow me on Twitter"
       src="https://raw.githubusercontent.com/florent37/DaVinci/master/mobile/src/main/res/drawable-hdpi/twitter.png" />
</a>
<a href="https://fr.linkedin.com/in/florentchampigny">
  <img alt="Follow me on LinkedIn"
       src="https://raw.githubusercontent.com/florent37/DaVinci/master/mobile/src/main/res/drawable-hdpi/linkedin.png" />
</a>

#License

    Copyright 2017 florent37, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
