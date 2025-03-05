package com.hm.stock.modules.i18n;

import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.execptions.CommonResultCode;
import com.hm.stock.modules.execptions.ErrorResultCode;

import java.io.File;
import java.io.FileReader;
import java.util.*;

/**
 * 国家地区对照码
 * https://uutool.cn/info-lang/
 */
public class GenerateCode {

    private static final File I18N_DIR_FILE = new File(new File("").getAbsolutePath()+"\\base-stock-api\\src\\main\\resources\\i18n");

    public static void main(String[] args) throws Exception {
        File[] files = I18N_DIR_FILE.listFiles();
        Map<String, Properties> map = new HashMap<>();
        for (File file : files) {
            Properties properties = new Properties();
            properties.load(new FileReader(file));
            String name = file.getName();
            map.put(name, properties);
        }
        Map<String, String> msgCode = getMsgCode();
        Map<String, List<String>> addMsgMap = new LinkedHashMap<>();

        for (Map.Entry<String, String> entry : msgCode.entrySet()) {
            for (Map.Entry<String, Properties> propertiesEntry : map.entrySet()) {
                Properties properties = propertiesEntry.getValue();
                if (LogicUtils.isNull(properties.get(entry.getKey()))) {
                    addMsgMap.computeIfAbsent(propertiesEntry.getKey(),key-> new ArrayList<>()).add(entry.getKey()+"="+entry.getValue());
                }
            }
        }
        for (Map.Entry<String, List<String>> entry : addMsgMap.entrySet()) {
            System.out.println("\n\n");
            System.out.println(entry.getKey());
            entry.getValue().forEach(System.out::println);
        }
    }

    public static Map<String,String> getMsgCode(){
        Map<String,String> map = new LinkedHashMap<>();
        for (CommonResultCode resultCode : CommonResultCode.values()) {
            map.put(resultCode.name(), resultCode.getMessage());
        }
        for (ErrorResultCode resultCode : ErrorResultCode.values()) {
            map.put(resultCode.name(), resultCode.getMessage());
        }
        return map;
    }

}
