package com.github.florent37.autoasync.processor.holders;

import javax.lang.model.element.Element;

/**
 * Created by florentchampigny on 01/03/2017.
 */

public class JsoupModelFieldHolder {
    public Element element;
    public String name;
    public String jsoupQuery;

    public boolean forText = false;
    public boolean customAttr = false;
    public String attr;

    public JsoupModelFieldHolder(Element element, String name, String jsoupQuery, String attr, boolean customAttr) {
        this.element = element;
        this.jsoupQuery = jsoupQuery;
        this.attr = attr;
        this.name = name;
        this.customAttr = customAttr;
    }

    public JsoupModelFieldHolder(Element element, String name, String jsoupQuery, boolean forText) {
        this.element = element;
        this.jsoupQuery = jsoupQuery;
        this.forText = forText;
        this.name = name;
    }


}
