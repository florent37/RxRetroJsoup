package com.github.florent37.autoasync.processor.holders;

import com.squareup.javapoet.ClassName;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;

public class JsoupModelHolder {
    public Element element;
    public ClassName classNameComplete;
    public String className;
    public List<JsoupModelFieldHolder> fields;

    public JsoupModelHolder(Element element, ClassName classNameComplete, String className) {
        this.element = element;
        this.classNameComplete = classNameComplete;
        this.className = className;
        this.fields = new ArrayList<>();
    }

    public void addField(JsoupModelFieldHolder fieldHolder){
        fields.add(fieldHolder);
    }

}
