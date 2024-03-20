package com.poc.domain.validation;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AnnotationValidationTest {
    @Test
    void ifTheParameterAreBiggerThenTwoShouldReturnTrue() throws Exception{


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


    }
}