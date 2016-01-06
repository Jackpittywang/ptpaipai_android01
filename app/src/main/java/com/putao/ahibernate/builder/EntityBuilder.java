package com.putao.ahibernate.builder;

/**
 * Created by jidongdong on 15/3/25.
 */


import android.database.Cursor;

import com.putao.ahibernate.annotation.Column;
import com.putao.ahibernate.annotation.Id;
import com.putao.ahibernate.annotation.OneToMany;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class EntityBuilder<T> {
    private Class clazz;

    private Cursor cursor;

    public EntityBuilder(Class clazz, Cursor cursor) {
        this.clazz = clazz;
        this.cursor = cursor;
    }

    public List<T> buildQueryList() {
        List<T> queryList = new ArrayList<T>();
        Field[] fields = clazz.getDeclaredFields();
        if (cursor.moveToFirst()) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                try {
                    T t = (T) clazz.newInstance();
                    Annotation[] fieldAnnotations = null;
                    for (Field field : fields) {
                        field.setAccessible(true);
                        fieldAnnotations = field.getAnnotations();
                        if (fieldAnnotations.length != 0) {
                            for (Annotation annotation : fieldAnnotations) {
                                String columnName = null;
                                if (annotation instanceof Id) {
                                    columnName = ((Id) annotation).name();
                                } else if (annotation instanceof Column) {
                                    columnName = ((Column) annotation).name();
                                } else if (annotation instanceof OneToMany) {
                                    continue;
                                    // Ignore
                                }
                                if (field.getType().getSimpleName().equals("Long")) {
                                    field.set(
                                            t,
                                            cursor.getLong(cursor
                                                    .getColumnIndexOrThrow((columnName != null && !columnName
                                                            .equals("")) ? columnName : field
                                                            .getName())));
                                } else if (field.getType().getSimpleName().equals("String")) {
                                    field.set(
                                            t,
                                            cursor.getString(cursor
                                                    .getColumnIndexOrThrow((columnName != null && !columnName
                                                            .equals("")) ? columnName : field
                                                            .getName())));
                                } else if (field.getType().getSimpleName().equals("Integer")) {
                                    field.set(
                                            t,
                                            cursor.getInt(cursor
                                                    .getColumnIndexOrThrow((columnName != null && !columnName
                                                            .equals("")) ? columnName : field
                                                            .getName())));

                                }

                            }
                        }
                    }
                    queryList.add(t);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }
        }
        return queryList;
    }

}