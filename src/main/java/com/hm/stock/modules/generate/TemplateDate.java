package com.hm.stock.modules.generate;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class TemplateDate {

    private String baseJavaPath;
    private String baseXmlPath;
    private String basePackage;
    private String domain;

    private JdbcUtils.TableInfo tableInfo;

    private List<String> excludeFields = new ArrayList<>();

    public String getName(){
        return tableInfo.getName();
    }

    public String getUrl(){
        return tableInfo.getName().replace("_", "/");
    }

    public String getRemark(){
        return tableInfo.getRemark();
    }

    public Entity getEntity(){
        return new Entity(this);
    }
    public Mapper getMapper(){
        return new Mapper(this);
    }
    public Service getService(){
        return new Service(this);
    }

    public ServiceImpl getServiceImpl(){
        return new ServiceImpl(this);
    }

    public WebController getWebController(){
        return new WebController(this);
    }

    public AppController getAppController(){
        return new AppController(this);
    }

    @AllArgsConstructor
    public static class AppController {
        private TemplateDate date;
        private final String suffix = "AppController";
        public String getPackagePath(){
            return date.getBasePackage() + "." + date.getDomain() + ".controller.app";
        }
        public String getClassPath(){
            return getPackagePath() +"."+ getClassName();
        }

        public String getClassName(){
            return date.toBigHump(date.getTableInfo().getName(),true)+suffix;
        }
        public File getFilePath(){
            String dirPath = date.getBaseJavaPath() + File.separator + getPackagePath().replace(".", File.separator);
            File fileDir = new File(dirPath);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            return new File(dirPath + File.separator + getClassName() + ".java");
        }
    }

    @AllArgsConstructor
    public static class WebController {
        private TemplateDate date;
        private final String suffix = "WebController";
        public String getPackagePath(){
            return date.getBasePackage() + "." + date.getDomain() + ".controller.web";
        }
        public String getClassPath(){
            return getPackagePath() +"."+ getClassName();
        }

        public String getClassName(){
            return date.toBigHump(date.getTableInfo().getName(),true)+suffix;
        }
        public File getFilePath(){
            String dirPath = date.getBaseJavaPath() + File.separator + getPackagePath().replace(".", File.separator);
            File fileDir = new File(dirPath);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            return new File(dirPath + File.separator + getClassName() + ".java");
        }

    }

    @AllArgsConstructor
    public static class ServiceImpl {
        private TemplateDate date;
        private final String suffix = "ServiceImpl";
        public String getPackagePath(){
            return date.getBasePackage() + "." + date.getDomain() + ".service.impl";
        }
        public String getClassPath(){
            return getPackagePath() +"."+ getClassName();
        }

        public String getClassName(){
            return date.toBigHump(date.getTableInfo().getName(),true)+suffix;
        }
        public File getFilePath(){
            String dirPath = date.getBaseJavaPath() + File.separator + getPackagePath().replace(".", File.separator);
            File fileDir = new File(dirPath);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            return new File(dirPath + File.separator + getClassName() + ".java");
        }

    }

    @AllArgsConstructor
    public static class Service {
        private TemplateDate date;
        private final String suffix = "Service";
        public String getPackagePath(){
            return date.getBasePackage() + "." + date.getDomain() + ".service";
        }
        public String getClassPath(){
            return getPackagePath() +"."+ getClassName();
        }

        public String getClassName(){
            return date.toBigHump(date.getTableInfo().getName(),true)+suffix;
        }
        public File getFilePath(){
            String dirPath = date.getBaseJavaPath() + File.separator + getPackagePath().replace(".", File.separator);
            File fileDir = new File(dirPath);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            return new File(dirPath + File.separator + getClassName() + ".java");
        }
    }

    @AllArgsConstructor
    public static class Mapper {
        private TemplateDate date;
        private final String suffix = "Mapper";
        public String getPackagePath(){
            return date.getBasePackage() + "." + date.getDomain() + ".mapper";
        }
        public String getClassPath(){
            return getPackagePath() +"."+ getClassName();
        }

        public String getClassName(){
            return date.toBigHump(date.getTableInfo().getName(),true)+suffix;
        }
        public File getFilePathByJava(){
            String dirPath = date.getBaseJavaPath() + File.separator + getPackagePath().replace(".", File.separator);
            File fileDir = new File(dirPath);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            return new File(dirPath + File.separator + getClassName() + ".java");
        }
        public File getFilePathByXml(){
            String dirPath = date.getBaseXmlPath()+ File.separator +date.getDomain();
            File fileDir = new File(dirPath);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            return new File(dirPath + File.separator + getClassName() + ".xml");
        }
    }


    @AllArgsConstructor
    public static class Entity {
        private TemplateDate date;
        private final String suffix = "";
        public String getPackagePath(){
            return date.getBasePackage() + "." + date.getDomain() + ".entity";
        }
        public String getClassPath(){
            return getPackagePath() +"."+ getClassName();
        }

        public String getClassName(){
            return date.toBigHump(date.getTableInfo().getName(),true)+suffix;
        }

        public File getFilePath(){
            String dirPath = date.getBaseJavaPath() + File.separator + getPackagePath().replace(".", File.separator);
            File fileDir = new File(dirPath);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            return new File(dirPath + File.separator + getClassName() + ".java");
        }

        public Set<String> getImportPaths(){
            Set<String> strings = new HashSet<>();
            for (JdbcUtils.FieldInfo field : date.getTableInfo().getFields()) {
                if (date.getExcludeFields().contains(field.getName())) {
                    continue;
                }
                strings.add(field.getJavaType().getClassPath());
            }
            return strings;
        }

        public List<Field> getFields(){
            List<Field> fields = new ArrayList<>();
            for (JdbcUtils.FieldInfo fieldByDb : date.getTableInfo().getFields()) {
                if (date.getExcludeFields().contains(fieldByDb.getName())) {
                    continue;
                }
                Field field = new Field();
                field.setName(date.toBigHump(fieldByDb.getName(), false));
                field.setType(fieldByDb.getJavaType().getName());
                field.setRemark(fieldByDb.getRemark());
                fields.add(field);
            }
            return fields;
        }
    }

    @Data
    public static class Field {
        private String name;

        private String type;

        private String remark;
    }

    private String toBigHump(String str,boolean head){
        StringBuilder sb=new StringBuilder();
        char[] charArray = str.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            if (head && i == 0 && (int) c >= 97) {
                sb.append(((char) ((int) c - 32)));
                continue;
            }
            if (c == '_'&& (int) (c = charArray[++i]) >= 97) {
                sb.append(((char) ((int) c - 32)));
                continue;
            }
            sb.append(c);
        }
        return sb.toString();
    }
}
