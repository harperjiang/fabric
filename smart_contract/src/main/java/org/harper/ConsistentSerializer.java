package org.harper;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.apache.commons.beanutils.BeanIntrospector;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.FluentPropertyBeanIntrospector;
import org.apache.commons.beanutils.PropertyUtils;
import org.hyperledger.fabric.contract.annotation.Property;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConsistentSerializer<T> implements JsonSerializer<T> {

    Class<?> type;

    List<PropertyDescriptor> descriptors;

    public ConsistentSerializer(Class<? extends T> type) {
        this.type = type;
        descriptors = Arrays.stream(PropertyUtils.getPropertyDescriptors(type))
                .filter(desc -> {
                    try {
                        Field field = type.getDeclaredField(desc.getName());
                        field.setAccessible(true);
                        return field.getAnnotation(Property.class) != null;
                    } catch (NoSuchFieldException e) {
                        return false;
                    }
                }).sorted(Comparator.comparing(desc -> desc.getName())).collect(Collectors.toList());
    }

    @Override
    public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        for (PropertyDescriptor desc : descriptors) {
            try {
                result.add(desc.getName(), context.serialize(desc.getReadMethod().invoke(src)));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }
}
