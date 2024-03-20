package com.poc.domain.validation;

import com.poc.domain.Person;
import org.junit.jupiter.api.Test;

class AnnotationValidationTest {


    AnnotationValidation annotationValidation = new AnnotationValidation();

    @Test
    void validatePessoaNotNullAgeShouldFail() throws Exception {

        Person person = new Person();
        person.setName("JOA");

        annotationValidation.validate(person);

        //expects a exception


/*
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

 */

    }
}