package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileNotFoundException;

public class JsonParser implements Reader {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Mission read(File file) throws Exception {
        if (file == null || !file.exists()) {
            throw new FileNotFoundException("Файл не найден: " + file);
        }
        return mapper.readValue(file, Mission.class);
    }
}