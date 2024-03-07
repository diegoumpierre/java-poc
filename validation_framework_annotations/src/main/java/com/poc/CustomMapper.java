package com.poc;

import br.com.poc.domain.annotation.Alias;
import br.com.poc.model.CustomMapperFieldDetailModel;
import br.com.poc.service.mapper.converter.template.ClassStructure;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomMapper {
    protected Map<String, List<CustomMapperFieldDetailModel>> classFieldMap = new HashMap<>();

    protected static Map<String, Object> converterClassCreatedMap = new HashMap<>();


    public void readClass(Object object) {
        String className = object.getClass().getName();

        if (classFieldMap.containsKey(className)) return;

        Field[] fields = object.getClass().getDeclaredFields();
        List<CustomMapperFieldDetailModel> attributes = new ArrayList<>();
        try {
            for (Field field : fields) {
                attributes.add(
                        new CustomMapperFieldDetailModel(
                                field.getName(),
                                field.getType().equals(boolean.class)
                        )
                );

                //check if the field have the alias
                List<String> fieldAlias = splitAliasNames(field.getAnnotation(Alias.class));
                if (fieldAlias != null && !fieldAlias.isEmpty()) {
                    fieldAlias.stream().forEach((alias) -> {
                                attributes.add(
                                        new CustomMapperFieldDetailModel(
                                                alias,
                                                field.getType().equals(boolean.class),
                                                field.getName()
                                        )
                                );
                            }
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        classFieldMap.put(className, attributes);
    }

    List<String> splitAliasNames(Alias alias) {
        if (alias == null || alias.otherFieldNames() == null) return Collections.emptyList();
        return Arrays.asList(alias.otherFieldNames().split(","));
    }

    boolean generateTheClassAndGetInstance(ClassStructure classStructure,
                                        String classNameConverter,
                                        String originClassName,
                                        String targetClassName) {

        try {
            //create the converterClass
            String source = classStructure.createClassConverter(
                    classNameConverter,
                    originClassName,
                    classFieldMap.get(originClassName),
                    targetClassName,
                    classFieldMap.get(targetClassName));

            //--------- compile and add to the class loader
            // Save source in .java file.
            File root = Files.createTempDirectory("java").toFile();
            File sourceFile = new File(root, "test/" + classNameConverter + ".java");
            sourceFile.getParentFile().mkdirs();
            Files.write(sourceFile.toPath(), source.getBytes(StandardCharsets.UTF_8));

            // Compile source file.
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            compiler.run(null, null, null, sourceFile.getPath());

            // Load and instantiate compiled class.
            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{root.toURI().toURL()});
            Class<?> cls = Class.forName("test." + classNameConverter, true, classLoader);
            Object instance = cls.getDeclaredConstructor().newInstance();

            converterClassCreatedMap.put(classNameConverter, instance);
            return true;
        }catch (Exception error){
            System.out.println("Error on generate/compile the class. error: "+error.getMessage());
        }
        return false;
    }
}