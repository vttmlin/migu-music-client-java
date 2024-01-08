package com.iflytek.obu.hunan.vrbt.migu.music.decode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Client {
    public static void main(String[] args) throws IOException {
        String url = "";
        String key = "";
        Scanner scanner = new Scanner(System.in);
        if (args == null || args.length == 0) {
            System.err.println("请输入URL:");
            url = scanner.nextLine();
        } else {
            for (String arg : args) {
                if (arg.startsWith("--url=")) {
                    url = arg.substring(6);
                }
                if (arg.startsWith("--key=")) {
                    key = arg.substring(6);
                }
            }
        }

        ObjectMapper mapper = new ObjectMapper();
        MapType mapType = mapper.getTypeFactory().constructMapType(Map.class, String.class, String.class);
        Map<String, String> map = mapper.readValue(new URL(url), mapType);
        if (map == null || map.size() == 0) {
            System.err.println("获取解密信息失败，请检查输入的URL");
            System.exit(0);
        }
        System.err.println("请输入文件的日期:");
        String date = scanner.nextLine();
        if ("".equals(key)) {
            key = map.get(date);
        }
        if (key == null || "".equals(key)) {
            System.err.println("日期【" + date + "】获取解密key失败");
            System.exit(0);
        } else {
            System.err.println("日期【" + date + "】key 获取成功==>> " + key);
        }
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
                final String key1 = key;
                strings = strings.stream().map(content -> {
                    try {
                        byte[] decode = decoder.decode(key1, content);
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
}
