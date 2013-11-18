package org.javers.model.mapping;

import org.javers.model.mapping.type.JaversType;
import org.javers.model.mapping.type.TypeMapper;

import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author pawel szymczyk
 */
public class FieldBasedPropertyScanner extends PropertyScanner {

    public FieldBasedPropertyScanner(TypeMapper typeMapper) {
        super(typeMapper);
    }

    @Override
    public  List<Property> scan(Class<?> managedClass) {
        List<Field> declaredFields = new LinkedList<Field>();
        declaredFields.addAll(getFields(managedClass));
        List<Property> propertyList = new ArrayList<Property>(declaredFields.size());

        for (Field field : declaredFields) {

            if(fieldIsPersistance(field)) {

                JaversType javersType = typeMapper.getJavesrType(field.getType());
                Property fieldProperty = new FieldProperty(field, javersType);
                propertyList.add(fieldProperty);
            }
        }
        return propertyList;
    }

    private List<Field> getFields(Class<?> clazz) {
        List<Field> superFields;
        if (clazz.getSuperclass() == Object.class) { //recursion stop condition
            superFields = new ArrayList<>();
        }
        else {
            superFields = getFields(clazz.getSuperclass());
        }

        superFields.addAll( Arrays.asList(clazz.getDeclaredFields()) );
        return superFields;
    }

    private boolean fieldIsPersistance(Field field) {
        return Modifier.isTransient(field.getModifiers()) == false
                && field.getAnnotation(Transient.class) == null;
    }
}