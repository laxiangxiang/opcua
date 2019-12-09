package com.opc.uaclient.opcua.util;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * @author fujun
 */
@Slf4j
public class YamlConverter {

    private YamlConverter() {
    }

    private static class SingleHolder{
        private static YamlConverter instance = new YamlConverter();
    }

    public static YamlConverter getInstance(){
        return SingleHolder.instance;
    }

    public <T> T readAndConvert(String filePath,Class<T> clazz){
        T t = null;
        ClassLoader classLoader = getClass().getClassLoader();
        try {
            String path = classLoader.getResource(filePath).getPath();
            YamlReader reader = new YamlReader(new FileReader(path));
            t = reader.read(clazz);
        }catch (FileNotFoundException | YamlException e){
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return t;
    }
}
