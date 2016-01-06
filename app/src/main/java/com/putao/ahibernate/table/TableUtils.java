package com.putao.ahibernate.table;

/**
 * Created by jidongdong on 15/3/25.
 */


import android.database.sqlite.SQLiteDatabase;

import com.putao.ahibernate.annotation.Column;
import com.putao.ahibernate.annotation.Id;
import com.putao.ahibernate.annotation.Table;
import com.putao.camera.util.Loger;
import com.putao.camera.util.StringHelper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TableUtils {
    public static boolean DEBUG = false;

    private static String DEFAULT_FOREIGN_KEY_SUFFIX = "_id";

    public static String buildDropTableStatement(TableInfo tableInfo) {
        // DROP TABLE IF EXISTS avpig_tingshu_book
        StringBuilder sb = new StringBuilder(256);
        sb.append("DROP TABLE ");
        sb.append("IF EXISTS ");
        sb.append(tableInfo.getTableName());
        return sb.toString();
    }

    public static Object getFieldValue(String filedName, Object obj)
            throws IllegalArgumentException, SecurityException, IllegalAccessException,
            NoSuchFieldException {
        Field field = obj.getClass().getDeclaredField(filedName);
        field.setAccessible(true);
        return field.get(obj);
    }

    // Strubg query = "Select *  from tableName where x = xx";
    public static String buildQueryStatements(String tableName, String fieldName, String fieldValue) {
        StringBuilder sb = new StringBuilder(256);
        sb.append("SELECT * FROM ");
        sb.append(tableName);
        sb.append(" WHERE ");
        sb.append(fieldName);
        sb.append("=");
        sb.append(fieldValue);
        return sb.toString();
    }

    public static String buildCreateTableStatement(TableInfo tableInfo, boolean ifNotExists) {
        // CREATE TABLE IF NOT EXISTS hrw_playlist (id INTEGER PRIMARY KEY,name
        // TEXT CHECK( name != '' ),add_date INTEGER,modified_date INTEGER);
        StringBuilder sb = new StringBuilder(256);
        sb.append("CREATE TABLE ");

        if (ifNotExists) {
            sb.append("IF NOT EXISTS ");
        }

        sb.append(tableInfo.getTableName());
        sb.append(" (");

        Iterator iter = null;
        iter = tableInfo.getColumns().entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry e = (Map.Entry) iter.next();
            if (tableInfo.getColumnsType().get(e.getKey()).equals("Long")) {
                sb.append(e.getValue() + " INTEGER");
            }
            // if (f.getType().getSimpleName().equals("List")) {
            // sb.append(entry.getKey()+DEFAULT_FOREIGN_KEY_SUFFIX +
            // " INTEGER");
            // }
            if (tableInfo.getColumnsType().get(e.getKey()).equals("String")) {
                sb.append(e.getValue() + " TEXT");
            }
            if (tableInfo.getColumnsType().get(e.getKey()).equals("Integer")) {
                sb.append(e.getValue() + " INTENER");
            }
            // and primary key here
            if (tableInfo.getPrimaryColoum().equals(e.getKey())) {
                sb.append(" PRIMARY KEY");
            }

            if (iter.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(")");
        Loger.d("create table sql:" + sb.toString());
        return sb.toString();
    }


    public static Field extractIdField(Class clazz) {
        Field idField = null;
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getAnnotations().length != 0) {
                field.setAccessible(true);
                for (Annotation annotation : field.getAnnotations()) {
                    Class<?> annotationClass = annotation.annotationType();
                    if (annotationClass.getName().equals(
                            "com.hrw.framework.ahibernate.annotation.Id")) {
                        idField = field;
                    }
                }
            }
        }
        return idField;
    }

    public static int createTable(SQLiteDatabase db, boolean ifNotExists, Class... entityClasses) {
        int i = -1;
        for (Class clazz : entityClasses) {
            TableInfo tableInfo = new TableInfo(clazz);
            String sql = buildCreateTableStatement(tableInfo, ifNotExists);
            if (!DEBUG) {
                db.execSQL(sql);
            }
            i = 1;
        }

        return i;
    }

    public static int dropTable(SQLiteDatabase db, Class... entityClasses) {
        int i = -1;
        for (Class clazz : entityClasses) {
            TableInfo tableInfo = new TableInfo(clazz);
            String sql = buildDropTableStatement(tableInfo);
            if (!DEBUG) {
                db.execSQL(sql);
            }
            i = 1;
        }
        return i;
    }

    @SuppressWarnings({
            "rawtypes", "unchecked"
    })
    public static String getTableName(Class clazz) {
        Table table = (Table) clazz.getAnnotation(Table.class);
        String name = null;
        if (isTableNameEmpty(table)) {
            name = table.name();
        } else {
            // if the name isn't specified, it is the class name lowercased
            name = clazz.getSimpleName().toLowerCase();
        }
        return name;
    }

    private static boolean isTableNameEmpty(Table table) {
        return table != null && !StringHelper.isEmpty(table.name());
    }

    public static Map<String, String> getTableColumns(Class clazz) {
        Map<String, String> columns = new HashMap<String, String>();
        Annotation[] fieldAnnotations = null;
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            fieldAnnotations = field.getAnnotations();
            if (fieldAnnotations.length != 0) {
                for (Annotation annotation : fieldAnnotations) {
                    String columnName = null;
                    if (annotation instanceof Id) {
                        columnName = ((Id) annotation).name();
                        columns.put(field.getName(), !StringHelper.isEmpty(columnName) ? columnName
                                : field.getName());
                    } else if (annotation instanceof Column) {
                        columnName = ((Column) annotation).name();
                        columns.put(field.getName(), !StringHelper.isEmpty(columnName) ? columnName
                                : field.getName());
                    }

                }
            }
        }
        return columns;
    }

    public static List<Object> extratToTableInfo(Class clazz) {
        List<Object> tableInfo = new ArrayList<Object>();
        String tableName = getTableName(clazz);
        String primaryKey = null;
        Map<String, String> columns = new HashMap<String, String>();
        Map<String, String> columnsType = new HashMap<String, String>();

        Annotation[] fieldAnnotations = null;
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            fieldAnnotations = field.getAnnotations();
            if (fieldAnnotations.length != 0) {
                for (Annotation annotation : fieldAnnotations) {
                    String columnName = null;
                    if (annotation instanceof Id) {

                        primaryKey = field.getName();

                        columnName = ((Id) annotation).name();
                        columns.put(field.getName(), !StringHelper.isEmpty(columnName) ? columnName
                                : field.getName());
                        columnsType.put(field.getName(), field.getType().getSimpleName());
                    } else if (annotation instanceof Column) {
                        columnName = ((Column) annotation).name();
                        columns.put(field.getName(), !StringHelper.isEmpty(columnName) ? columnName
                                : field.getName());
                        columnsType.put(field.getName(), field.getType().getSimpleName());
                    }

                }
            }
        }
        tableInfo.add(tableName);
        tableInfo.add(primaryKey);
        tableInfo.add(columns);
        tableInfo.add(columnsType);
        return tableInfo;
    }

    public static Map<String, String> getTableColumnsType(Class clazz) {
        Map<String, String> columnsType = new HashMap<String, String>();
        Annotation[] fieldAnnotations = null;
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            fieldAnnotations = field.getAnnotations();
            if (fieldAnnotations.length != 0) {
                for (Annotation annotation : fieldAnnotations) {
                    if (annotation instanceof Id || annotation instanceof Column) {
                        columnsType.put(field.getName(), field.getType().getSimpleName());
                    }
                }
            }
        }
        return columnsType;
    }

    public static String getPrimaryKey(Class clazz) {
        Annotation[] fieldAnnotations = null;
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            fieldAnnotations = field.getAnnotations();
            if (fieldAnnotations.length != 0) {
                for (Annotation annotation : fieldAnnotations) {
                    if (annotation instanceof Id) {
                        return field.getName();
                    }
                }
            }
        }
        return null;
    }

}
