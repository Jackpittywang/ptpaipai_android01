package com.putao.ahibernate.sql;

/**
 * Created by jidongdong on 15/3/25.
 */

import com.putao.ahibernate.annotation.Column;
import com.putao.ahibernate.annotation.Id;
import com.putao.ahibernate.annotation.OneToMany;
import com.putao.camera.util.Loger;
import com.putao.camera.util.StringHelper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class Insert extends Operate {

    private Object entity;

    public Insert(Object entity) {
        super(entity.getClass());
        this.entity = entity;
    }

    public Map<String, String> getInsertColumns() {
        Map<String, String> insertColumns = new HashMap<String, String>();
        Class clazz = entity.getClass();
        Field[] fields = clazz.getDeclaredFields();
        Annotation[] fieldAnnotations = null;
        for (Field field : fields) {
            fieldAnnotations = field.getAnnotations();
            if (fieldAnnotations.length != 0) {
                for (Annotation annotation : fieldAnnotations) {
                    String columnName = null;
                    if (annotation instanceof Id && !((Id) annotation).autoGenerate()) {
                        columnName = ((Id) annotation).name();
//                        continue;
                        // TODO
                    } else if (annotation instanceof Column) {
                        columnName = ((Column) annotation).name();
                    } else if (annotation instanceof OneToMany) {
                        continue;
                        // Ignore
                    }
                    field.setAccessible(true);
                    try {
                        insertColumns.put(
                                (columnName != null && !columnName.equals("")) ? columnName : field
                                        .getName(),
                                field.get(entity) == null || StringHelper.isEmpty(field.get(entity).toString()) ? null : field.get(entity).toString());
                    } catch (IllegalArgumentException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
        return insertColumns;
    }

    public String toStatementString() {
        String sql = buildInsertSql(getTableName(), getInsertColumns());
        Loger.d("insert sql:" + sql);
        return sql;
    }

}