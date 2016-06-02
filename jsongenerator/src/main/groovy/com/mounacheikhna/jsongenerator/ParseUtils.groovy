package com.mounacheikhna.jsongenerator

import groovy.json.JsonSlurper

final class ParseUtils {

    public static Map<String, String> valuesFromFile(File file) {
        if (! file.exists()) {
            throw new IllegalArgumentException(" File ${file.name} doesn't exist.")
        }
        Map<String, String> res = new HashMap<>()
        switch (file.name) {
            case ~/.*.json$/:
                res = fromJson(file)
                break
            case ~/.*.properties$/:
                res = fromProperties(file)
                break
        }
        return res
    }

    public static Map<String, String> fromProperties(File file) {
        def values = new HashMap<String, String>()
        Properties properties = parseProperties(file.path)
        properties.each {
            values.put(it.key.toString(), it.value.toString())
        }
        return values
    }

    public static Map<String, String> fromJson(File file) {
        JsonSlurper jsonSlurper = new JsonSlurper()
        def values = new HashMap<String, String>()
        def parsed = jsonSlurper.parse(file)
        parsed.each {
            key, value ->
                values.put(key, value)
        }
        return values
    }

    public static Properties parseProperties(String path) {
        Properties properties = new Properties()
        File propertiesFile = new File(path)
        properties.load(propertiesFile.newDataInputStream())
        return  properties
    }

}