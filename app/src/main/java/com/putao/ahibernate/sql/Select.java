package com.putao.ahibernate.sql;

/**
 * Created by jidongdong on 15/3/25.
 */

import java.util.Map;

public class Select extends Operate {
    private Object entity;

    private Map<String, String> where;

    public Select(Object entity) {
        super(entity.getClass());
        this.entity = entity;
        try {
            this.where = buildWhere(entity);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public Select(Class clazz, Map<String, String> where) {
        super(clazz);
        this.where = where;
    }

    public String toStatementString() {
        return buildSelectSql(getTableName(), where);
    }

}