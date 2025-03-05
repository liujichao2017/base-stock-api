package com.hm.stock.modules.generate;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Date;
import java.util.*;

@Slf4j
public class JdbcUtils {

    private Connection conn;

    private JdbcUtils() {
    }

    public static JdbcUtils getInstance() {
        return getInstance("18.143.194.172", "3306", "xingu", "xingu", "kjKdXc67eM6JtL3n");
    }

    public static void main(String[] args) throws SQLException {
        try {
            JdbcUtils instance = getInstance();
            Connection conn = instance.conn;
            ResultSet rs = conn.getMetaData().getColumns(conn.getCatalog(), "%", "%", "%");
//            System.out.println(buildRows(rs).toJSONString(JSONWriter.Feature.PrettyFormat));
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            int[] arr = {3,4,6,7,9};
            for (int i : arr) {
                System.out.print(metaData.getColumnName(i) + "\t");
            }
            String sql = "alter table `%s` modify `%s`  decimal(%d,6) DEFAULT '0' COMMENT '%s';%n ";

            while (rs.next()) {
                if (!"DECIMAL".equals(rs.getString("TYPE_NAME"))) {
                    continue;
                }
//                for (int i : arr) {
//                    System.out.print(rs.getString(i) + "\t");
//                }
                String tableName = rs.getString("TABLE_NAME");
                String columnName = rs.getString("COLUMN_NAME");
                String typeName = rs.getString("TYPE_NAME");
                long columnSize = rs.getLong("COLUMN_SIZE");
                long decimalDigits = rs.getLong("DECIMAL_DIGITS");
                String remarks = rs.getString("REMARKS");
                if (decimalDigits<6L) {
                    System.out.printf(sql,tableName,columnName,columnSize,remarks);
                }

                //                System.out.println(typeName);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static JdbcUtils getInstance(String ip, String port, String dbName, String user, String pwd) {
        String className = "com.mysql.cj.jdbc.Driver";
        try {
            JdbcUtils jdbcUtils = new JdbcUtils();
            Class.forName(className);
            String url = String.format("jdbc:mysql://%s:%s/%s?serverTimezone=UTC", ip, port, dbName);
            jdbcUtils.conn = DriverManager.getConnection(url, user, pwd);
            PreparedStatement ps = jdbcUtils.conn.prepareStatement("select 1");
            ResultSet rs = ps.executeQuery();
            rs.next();
            if (rs.getLong(1) != 1L) {
                throw new RuntimeException("验证数据库通宵失败");
            }
            return jdbcUtils;
        } catch (ClassNotFoundException e) {
            log.error("找不到数据库驱动包: " + className);
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public JSONArray queryList(String sql) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            return buildRows(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static JSONArray buildRows(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        JSONArray list = new JSONArray();
        while (resultSet.next()) {
            JSONObject obj = new JSONObject();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String columnName = metaData.getColumnName(i);
                obj.put(columnName, resultSet.getObject(columnName));
            }
            list.add(obj);
        }
        return list;
    }

    public JSONObject queryOne(String sql) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            JSONObject obj = new JSONObject();
            if (resultSet.next()) {
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    String columnName = metaData.getColumnName(i);
                    obj.put(columnName, resultSet.getObject(columnName));
                }
            }
            return obj;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int executeChange(String sql) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public List<TableInfo> getMetaData() {
        return getMetaData("%");
    }

    private ResultSet getMetaDataTest() {
        try {
            return conn.getMetaData().getColumns(conn.getCatalog(), "%", "%", "%");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public List<TableInfo> getMetaData(String tableNamePattern) {
        try {
            ResultSet rs = conn.getMetaData().getColumns(conn.getCatalog(), "%", tableNamePattern, "%");
            Map<String, TableInfo> map = new HashMap<>();
//            System.out.println(buildRows(rs).toJSONString(JSONWriter.Feature.PrettyFormat));
            while (rs.next()) {
                TableInfo tableInfo = map.computeIfAbsent(rs.getString("TABLE_NAME"), this::createTable);
                List<FieldInfo> fields = tableInfo.getFields();
                FieldInfo fieldInfo = new FieldInfo();
                fieldInfo.setName(rs.getString("COLUMN_NAME"));
                fieldInfo.setType(rs.getString("TYPE_NAME"));
                JavaTypeEnum typeEnum = JavaTypeEnum.getEnum(fieldInfo.getType());
                if (typeEnum == null) {
                    System.out.println(tableInfo.getName() + "   " + fieldInfo.getName() + "   " + fieldInfo.getType());
                }
                fieldInfo.setJavaType(typeEnum);
                fieldInfo.setRemark(rs.getString("REMARKS"));
                fieldInfo.setNull("YES".equals(rs.getString("IS_NULLABLE")));
                fieldInfo.setDefaultVal(rs.getString("COLUMN_DEF"));
                fields.add(fieldInfo);
            }
            return new ArrayList<>(map.values());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private TableInfo createTable(String tableName) {
        TableInfo tableInfo = new TableInfo(tableName);
        tableInfo.setRemark(getTableComment(tableName));
        return tableInfo;
    }

    private String getTableComment(String tableName) {
        String comment = null;
        try {
            PreparedStatement ps = conn.prepareStatement("show table status");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String name = rs.getString("Name");
                if (name.equals(tableName)) {
                    comment = rs.getString("Comment");
                    break;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return comment;
    }

    @Data
    public static class TableInfo {
        private String name;
        private List<FieldInfo> fields;
        private String remark;

        public TableInfo(String name) {
            this.name = name;
            this.fields = new ArrayList<>();
        }
    }

    @Data
    public static class FieldInfo {
        private String name;
        private String type;
        private JavaTypeEnum javaType;
        private boolean isNull;
        private String defaultVal;
        private String remark;
    }

    @AllArgsConstructor
    @Getter
    public enum JavaTypeEnum {
        DATE("Date", "java.util.Date", Date.class),
        DECIMAL("BigDecimal", "java.math.BigDecimal", BigDecimal.class),
        STRING("String", "java.lang.String", String.class),
        INT("Long", "java.lang.Long", Long.class),

        ;
        private String name;
        private String classPath;
        private Class<?> classObject;

        public static JavaTypeEnum getEnum(String columnName) {
            switch (columnName) {
                case "DECIMAL":
                case "DOUBLE":
                    return JavaTypeEnum.DECIMAL;
                case "INT":
                case "BIT":
                case "TINYINT":
                case "BIGINT":
                case "MEDIUMINT":
                case "SMALLINT":
                    return JavaTypeEnum.INT;
                case "DATETIME":
                    return JavaTypeEnum.DATE;
                case "VARCHAR":
                case "LONGTEXT":
                case "MEDIUMTEXT":
                case "CHAR":
                case "TEXT":
                    return JavaTypeEnum.STRING;
            }
            return null;
        }
    }
}
