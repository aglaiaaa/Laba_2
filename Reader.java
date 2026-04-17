package org.example;

import java.io.File;

public interface Reader {
    Mission read(File file) throws Exception;
}
