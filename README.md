# RxRetroJsoup

**RxJava2 ready !**

<a href="https://github.com/florent37/RxRetroJsoup/raw/master/sample-debug.apk">Sample apk</a>

Create an interface with `@Select` annotated method
```
public interface TutosAndroidFrance {

    @Select("article")
    Observable<Article> articles();

}
```

Annotate your model with Jsoup queries ( https://jsoup.org/cookbook/extracting-data/selector-syntax )

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

                //optionally
                .client(your_okhttp_client)

                .build()
                .create(TutosAndroidFrance.class);
```

Fetch your objects !
```java
tutosAndroidFrance.articles()
                .toList()

                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(items -> );
```

#RxJsoup

```java
RxJsoup.with(url)
    .select("article")
    .flatMap(element -> rxJsoup.text(element, ".entry-title a"))
    .subscribe(text -> {});


    .flatMap(element -> rxJsoup.src(element, ".entry img"))
    .flatMap(element -> rxJsoup.href(element, ".entry a"))
    .flatMap(element -> rxJsoup.attr(element, ".entry h1", "id"))


```

```java
RxJsoup.connect(

           Jsoup.connect("www.thewebsite.com")
               .userAgent(MY_USER_AGENT)
               .data("credential", email)
               .data("pwd", password)
               .cookies(loginForm.cookies())
               .method(Connection.Method.POST)

           )
           .subscibe(response -> {})
```

#Download

<a href='https://ko-fi.com/A160LCC' target='_blank'><img height='36' style='border:0px;height:36px;' src='https://az743702.vo.msecnd.net/cdn/kofi1.png?v=0' border='0' alt='Buy Me a Coffee at ko-fi.com' /></a>

In your module [![Download](https://api.bintray.com/packages/florent37/maven/retrojsoup-compiler/images/download.svg)](https://bintray.com/florent37/maven/retrojsoup-compiler/_latestVersion)
```groovy
compile 'com.github.florent37:retrojsoup:1.0.2'
compile 'com.github.florent37:rxjsoup:1.0.2'
annotationProcessor 'com.github.florent37:retrojsoup-compiler:1.0.2'

//don't forget to include jsoup & rxjava
compile 'org.jsoup:jsoup:1.10.2'
compile 'io.reactivex:rxjava:2.0.7'

//optionaly
compile 'com.squareup.okhttp3:okhttp:3.6.0'
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
