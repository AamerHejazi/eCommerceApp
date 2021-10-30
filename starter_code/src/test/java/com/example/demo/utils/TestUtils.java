package com.example.demo.utils;

import java.lang.reflect.Field;

public class TestUtils {
    public static void injectObject(Object target, Object toInject,String fieldName) {

        boolean wasPrivate = false;

        try {

            Field checkedField = target.getClass().getDeclaredField(fieldName);
            if (!checkedField.isAccessible()) {
                checkedField.setAccessible(true);
                wasPrivate = true;
            }

            checkedField.set(target, toInject);
            if (wasPrivate) {
                checkedField.setAccessible(false);
            }

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
