package org.example;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class ParserFactory {

    private static final List<String> SUPPORTED_FORMATS = Arrays.asList(".json", ".xml", ".yaml", ".yml", ".txt", ".a5");

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
            throw new IllegalArgumentException(
                    "Неподдерживаемый формат файла: " + name +
                            ". Поддерживаемые форматы: " + SUPPORTED_FORMATS);
        }
    }
}
