package com.iflytek.obu.hunan.vrbt.migu.music.decode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Client {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
//        解析参数
        Config config = parseConfig(args);

//        判断 code、url 优先级 如果是 url 则输入日期  获取解密 key
        String key = getKey(config);
        Decoder decoder = new Decoder();
        while (true) {
            try {
                System.err.println("请输入加密文件的路径(输入exit退出):");
                String source = scanner.nextLine();
                if ("exit".equals(source)) {
                    break;
                }
                String dist = source + ".txt";
                List<String> strings = FileUtils.readLines(new File(source), "UTF8");
                strings = strings.stream().map(content -> {
                    try {
                        byte[] decode = decoder.decode(key, content);
                        return new String(decode, "GBK");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return "";
                }).collect(Collectors.toList());
                FileUtils.writeLines(new File(dist), strings);
                System.err.println("文件解密成功，解密后文件路径：【" + dist + "】");
            } catch (Exception e) {
                System.err.println("解密失败");
                e.printStackTrace();
            }
        }
    }

    private static String getKey(Config config) throws Exception {
        if (config.getCode() != null && !"".equals(config.getCode())) {
            return config.getCode();
        }
        ObjectMapper mapper = new ObjectMapper();
        MapType mapType = mapper.getTypeFactory().constructMapType(Map.class, String.class, String.class);
        Map<String, String> map = mapper.readValue(new URL(config.getUrl()), mapType);
        if (map == null || map.size() == 0) {
            System.err.println("获取解密信息失败，请检查输入的URL");
            System.exit(0);
        }
        System.err.println("请输入文件的日期:");
        String date = scanner.nextLine();
        String code = map.get(date);
        if (code == null || "".equals(code)) {
            System.err.println("查询不到该日期的数据 " + date);
            System.exit(-1);
        }
        return code;
    }

    private static Config parseConfig(String[] args) {
        Config config = new Config();
        if (args == null || args.length == 0) {
            System.err.println("请输入URL:");
            config.setUrl(scanner.nextLine());
        } else {
            for (String arg : args) {
                if (arg.startsWith("--url=")) {
                    config.setUrl(arg.substring(6));
                }
                if (arg.startsWith("--code=")) {
                    config.setCode(arg.substring(7));
                }
            }
        }
        return config;
    }
}
