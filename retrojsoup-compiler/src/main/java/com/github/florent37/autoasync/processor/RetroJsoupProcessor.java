package com.github.florent37.autoasync.processor;

import com.github.florent37.autoasync.processor.holders.JsoupModelFieldHolder;
import com.github.florent37.autoasync.processor.holders.JsoupModelHolder;
import com.github.florent37.autoasync.processor.holders.SelectHolder;
import com.github.florent37.retrojsoup.annotations.JsoupAttr;
import com.github.florent37.retrojsoup.annotations.JsoupHref;
import com.github.florent37.retrojsoup.annotations.JsoupSrc;
import com.github.florent37.retrojsoup.annotations.JsoupText;
import com.github.florent37.retrojsoup.annotations.Select;
import com.github.florent37.rxjsoup.RxJsoup;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.FuncN;

import static javax.lang.model.element.ElementKind.FIELD;
import static javax.lang.model.element.ElementKind.INTERFACE;
import static javax.lang.model.element.ElementKind.METHOD;

@SupportedAnnotationTypes({
        "com.github.florent37.retrojsoup.annotations.Select",
        "com.github.florent37.retrojsoup.annotations.JsoupHref",
        "com.github.florent37.retrojsoup.annotations.JsoupSrc",
        "com.github.florent37.retrojsoup.annotations.JsoupText",
        "com.github.florent37.retrojsoup.annotations.JsoupAttr"
})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@AutoService(javax.annotation.processing.Processor.class)
public class RetroJsoupProcessor extends AbstractProcessor {

    private Map<ClassName, SelectHolder> selectHolders = new HashMap<>();
    private Map<String, JsoupModelHolder> modelHolders = new HashMap<>();
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        filer = env.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        processAnnotations(env);

        writeHoldersOnJavaFile();

        return true;
    }

    private boolean isInterface(Element element) {
        return element.getKind() == INTERFACE;
    }

    private boolean isChildOfInterface(Element element) {
        final Element enclosingElement = element.getEnclosingElement();
        return enclosingElement != null && enclosingElement.getKind() == INTERFACE;
    }

    protected void processAnnotations(RoundEnvironment env) {
        for (Element element : env.getElementsAnnotatedWith(Select.class)) {
            if (isChildOfInterface(element)) {
                processSelect(element.getEnclosingElement());
            }
        }

        final Set<Element> jsoupAnnotatedField = new HashSet<>();
        jsoupAnnotatedField.addAll(env.getElementsAnnotatedWith(JsoupHref.class));
        jsoupAnnotatedField.addAll(env.getElementsAnnotatedWith(JsoupText.class));
        jsoupAnnotatedField.addAll(env.getElementsAnnotatedWith(JsoupSrc.class));
        jsoupAnnotatedField.addAll(env.getElementsAnnotatedWith(JsoupAttr.class));

        final Set<Element> jsoupModels = new HashSet<>();
        for (Element field : jsoupAnnotatedField) {
            jsoupModels.add(field.getEnclosingElement());
        }

        processJsoupModels(jsoupModels);
    }

    private void processJsoupModels(Set<Element> jsoupModels) {
        for (Element jsoupModel : jsoupModels) {

            final ClassName classFullName = ClassName.get((TypeElement) jsoupModel); //com.github.florent37.sample.TutoAndroidFrance
            final String className = jsoupModel.getSimpleName().toString(); //TutoAndroidFrance

            final JsoupModelHolder jsoupModelHolder = new JsoupModelHolder(jsoupModel, classFullName, className);

            for (Element field : jsoupModel.getEnclosedElements()) {
                if (field.getKind() == FIELD) {

                    final String fieldName = field.getSimpleName().toString();

                    {
                        final JsoupText annotationText = field.getAnnotation(JsoupText.class);
                        if (annotationText != null) {
                            jsoupModelHolder.addField(new JsoupModelFieldHolder(field, fieldName, annotationText.value(), true));
                        }
                    }

                    {
                        final JsoupHref annotationHref = field.getAnnotation(JsoupHref.class);
                        if (annotationHref != null) {
                            jsoupModelHolder.addField(new JsoupModelFieldHolder(field, fieldName, annotationHref.value(), "href", false));
                        }
                    }

                    {
                        final JsoupSrc annotationSrc = field.getAnnotation(JsoupSrc.class);
                        if (annotationSrc != null) {
                            jsoupModelHolder.addField(new JsoupModelFieldHolder(field, fieldName, annotationSrc.value(), "src", false));
                        }
                    }

                    {
                        final JsoupAttr annotationAttr = field.getAnnotation(JsoupAttr.class);
                        if (annotationAttr != null) {
                            jsoupModelHolder.addField(new JsoupModelFieldHolder(field, fieldName, annotationAttr.value(), annotationAttr.attr(), true));
                        }
                    }

                }
            }

            modelHolders.put(classFullName.toString(), jsoupModelHolder);
        }
    }

    private void processSelect(Element element) {
        final ClassName classFullName = ClassName.get((TypeElement) element); //com.github.florent37.sample.TutoAndroidFrance
        final String className = element.getSimpleName().toString(); //TutoAndroidFrance

        final SelectHolder selectHolder = new SelectHolder(element, classFullName, className);

        final List<Element> methods = getMethods(element);
        for (Element method : methods) {
            selectHolder.addMethod(method);
        }

        selectHolders.put(classFullName, selectHolder);
    }

    private List<Element> getMethods(Element element) {
        final List<? extends Element> enclosedElements = element.getEnclosedElements();
        final List<Element> mehods = new ArrayList<>();
        for (Element e : enclosedElements) {
            if (e.getKind() == METHOD)
                mehods.add(e);
        }
        return mehods;
    }

    protected void writeHoldersOnJavaFile() {
        for (JsoupModelHolder holder : modelHolders.values()) {
            construct(holder);
        }

        for (SelectHolder holder : selectHolders.values()) {
            construct(holder);
        }

        modelHolders.clear();
        selectHolders.clear();
    }

    public void construct(JsoupModelHolder modelHolder) {

        final TypeSpec.Builder builder = TypeSpec.classBuilder(modelHolder.className + Constants.PARSER)
                .addModifiers(Modifier.PUBLIC);

        for (JsoupModelFieldHolder field : modelHolder.fields) {
            builder.addMethod(MethodSpec.methodBuilder(field.name.replace("-","_").replace(" ","_"))
                    .addModifiers(Modifier.PUBLIC)
                    .returns(TypeName.VOID)
                    .addParameter(modelHolder.classNameComplete, "item")
                    .addParameter(TypeName.get(String.class), "value")
                    .addStatement("item.$L = value", field.name)
                    .build());
        }

        final TypeSpec newClass = builder.build();

        final JavaFile javaFile = JavaFile.builder(modelHolder.classNameComplete.packageName(), newClass).build();

        try {
            javaFile.writeTo(System.out);
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void construct(SelectHolder selectHolder) {

        final TypeSpec.Builder builder = TypeSpec.classBuilder(selectHolder.className + Constants.MAIN_CLASS_NAME)
                .addModifiers(Modifier.PUBLIC)
                .addField(FieldSpec.builder(ClassName.get(RxJsoup.class), "rxJsoup", Modifier.PRIVATE, Modifier.FINAL).build());

        builder.addMethod(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get(String.class), "url")
                .addParameter(ClassName.get(Boolean.class), "exceptionIfNotFound")
                .addStatement("this.rxJsoup = new RxJsoup(url, exceptionIfNotFound)")
                .build());


        for (Element method : selectHolder.methods) {
            final Select annotation = method.getAnnotation(Select.class);
            if (annotation != null) {
                final String annotationValue = annotation.value();

                ExecutableElement executableElement = (ExecutableElement) method;
                final TypeName returnType = ((ParameterizedTypeName) ParameterizedTypeName.get(executableElement.getReturnType())).typeArguments.get(0);

                final String returnTypeString = returnType.toString();
                final JsoupModelHolder jsoupModelHolder = this.modelHolders.get(returnTypeString);

                final MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(method.getSimpleName().toString())
                        .addModifiers(Modifier.PUBLIC)
                        .returns(ParameterizedTypeName.get(ClassName.get(Observable.class), returnType))
                        .addCode("return rxJsoup.select($S)\n", annotationValue)
                        .addCode("                .flatMap(new $T<Element, Observable<? extends $T>>() {\n", ClassName.get(Func1.class), returnType)
                        .addCode("                      @Override\n")
                        .addCode("                      public Observable<? extends $T> call($T element) {\n", returnType, ClassName.get(org.jsoup.nodes.Element.class))
                        .addCode("                      return Observable.zip(\n")
                        .addCode("                                  new Observable[]{\n");

                if (jsoupModelHolder != null) {
                    for (JsoupModelFieldHolder field : jsoupModelHolder.fields) {
                        final String rxJsoupMethod = field.forText ? "text" : field.attr;
                        if (field.customAttr) {
                            methodBuilder.addCode("                                          rxJsoup.attr(element, $S, $S),\n", field.jsoupQuery, field.attr);
                        } else {
                            methodBuilder.addCode("                                          rxJsoup.$L(element, $S),\n", rxJsoupMethod.replace("-", "_").replace(" ", "_"), field.jsoupQuery);
                        }
                    }
                }

                methodBuilder.addCode("                                  },\n")
                        .addCode("                      new $T<$T>() {\n", ClassName.get(FuncN.class), returnType)
                        .addCode("                          @Override\n")
                        .addCode("                          public $T call(Object... args) {\n", returnType)
                        .addCode("                                 final $T item = new $T();\n", returnType, returnType)
                        .addCode("                                 final $L$L parser = new $L$L();\n", returnType, Constants.PARSER, returnType, Constants.PARSER);

                if (jsoupModelHolder != null) {
                    int itemPosition = 0;
                    for (JsoupModelFieldHolder field : jsoupModelHolder.fields) {
                        methodBuilder.addCode("                                 parser.$L(item, (String) args[$L]);\n", field.name, itemPosition);
                        itemPosition++;
                    }
                }


                methodBuilder.addCode("                                 return item;\n")
                        .addCode("                          }\n")
                        .addCode("                      });\n")
                        .addCode("             }});");
                builder.addMethod(methodBuilder.build());
            }
        }

        final TypeName superClass = TypeName.get(selectHolder.element.asType());
        if (isInterface(selectHolder.element)) {
            builder.addSuperinterface(superClass);
        } else {
            builder.superclass(superClass);
        }

        final TypeSpec newClass = builder.build();

        final JavaFile javaFile = JavaFile.builder(selectHolder.classNameComplete.packageName(), newClass).build();

        try {
            javaFile.writeTo(System.out);
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
