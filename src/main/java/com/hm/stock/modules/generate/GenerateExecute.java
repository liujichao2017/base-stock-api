package com.hm.stock.modules.generate;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class GenerateExecute {
    // 存放Java文件的基础路径
    public static final String BASE_JAVA_PATH = new File("").getAbsoluteFile() + "\\base-stock-api\\src\\main\\java";
    // 存放MapperXml文件的基础路径
    public static final String BASE_XML_PATH = new File(
            "").getAbsoluteFile() + "\\base-stock-api\\src\\main\\resources\\mapper";
    // 渲染模拟存放位置
    public static final String TEMPLATE_PATH = new File(
            "").getAbsoluteFile() + "\\base-stock-api\\src\\main\\resources\\template";
    // 领域名
    public static final String DOMAIN = "fund";
    // 生成那张表
    public static final String TABLE = "fund_interest_rate";

    public static void main(String[] args) throws Exception {
        generate();
    }

    /**
     * 代码生成
     *
     * @throws IOException
     * @throws TemplateException
     */
    private static void generate() throws IOException, TemplateException {
        File file = new File(TEMPLATE_PATH);
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
        cfg.setDirectoryForTemplateLoading(file);
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        JdbcUtils instance = JdbcUtils.getInstance();
        // 生成那张表
        List<JdbcUtils.TableInfo> metaData = instance.getMetaData(TABLE);
        for (JdbcUtils.TableInfo tableInfo : metaData) {
            TemplateDate dataModel = new TemplateDate();
            // 存放Java文件的基础路径
            dataModel.setBaseJavaPath(BASE_JAVA_PATH);
            // 存放MapperXml文件的基础路径
            dataModel.setBaseXmlPath(BASE_XML_PATH);
            // 基础包路径
            dataModel.setBasePackage("com.hm.stock.domain");
            // 领域名
            dataModel.setDomain(DOMAIN);
            dataModel.setTableInfo(tableInfo);
            // 忽略字段
            dataModel.setExcludeFields(Arrays.asList("id", "update_time", "create_time"));

            Template template = cfg.getTemplate("entity.ftl");
            Writer writer = new OutputStreamWriter(new FileOutputStream(dataModel.getEntity().getFilePath()));
            template.process(dataModel, writer);

            template = cfg.getTemplate("mapper.ftl");
            writer = new OutputStreamWriter(new FileOutputStream(dataModel.getMapper().getFilePathByJava()));
            template.process(dataModel, writer);

            template = cfg.getTemplate("xml.ftl");
            writer = new OutputStreamWriter(new FileOutputStream(dataModel.getMapper().getFilePathByXml()));
            template.process(dataModel, writer);

            template = cfg.getTemplate("service.ftl");
            writer = new OutputStreamWriter(new FileOutputStream(dataModel.getService().getFilePath()));
            template.process(dataModel, writer);

            template = cfg.getTemplate("serviceimpl.ftl");
            writer = new OutputStreamWriter(new FileOutputStream(dataModel.getServiceImpl().getFilePath()));
            template.process(dataModel, writer);

            template = cfg.getTemplate("appcontroller.ftl");
            writer = new OutputStreamWriter(new FileOutputStream(dataModel.getAppController().getFilePath()));
            template.process(dataModel, writer);

//            template = cfg.getTemplate("webcontroller.ftl");
//            writer = new OutputStreamWriter(new FileOutputStream(dataModel.getWebController().getFilePath()));
//            template.process(dataModel, writer);

            template = cfg.getTemplate("appcontroller.ftl");
            writer = new OutputStreamWriter(new FileOutputStream(dataModel.getAppController().getFilePath()));
            template.process(dataModel, writer);
        }
    }

}
