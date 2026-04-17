package org.example;

import java.io.File;

public class ParserFactory {
    public Reader getReader(File file) {
        String name = file.getName().toLowerCase();
        if (name.endsWith(".json")) {
            return new JsonParser();
        } else if (name.endsWith(".xml")) {
            return new XmlParser();
        } else if (name.endsWith(".yaml") || name.endsWith(".yml")) {
            return new YamlParser();
        } else if (name.endsWith(".txt")) {
            return new TxtParser();
        } else {
            return new A5Parser();
        }
    }
}
