package org.example;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.File;
import java.io.FileNotFoundException;

public class XmlParser implements Reader {
    private final XmlMapper mapper = new XmlMapper();

    @Override
    public Mission read(File file) throws Exception {
        if (file == null || !file.exists()) {
            throw new FileNotFoundException("Файл не найден: " + file);
        }
        return mapper.readValue(file, Mission.class);
    }
}