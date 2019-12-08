package com.example.demo.opcua.util;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;

public class YamlReader {

    private YamlReader() {
    }

    private static class SingleHolder{
        private static YamlReader instance = new YamlReader();
    }

    public static YamlReader getInstance(){
        return SingleHolder.instance;
    }

    public Object read(String filePath){
        InputStream in = null;
        try {
            Yaml yaml = new Yaml();
            ClassLoader classLoader =  YamlReader.class.getClassLoader();
            in = classLoader.getResourceAsStream(filePath);
                Object o = yaml.loadAs(in,Object.class);
                System.out.println(o);
                return o;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                in.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }
}
