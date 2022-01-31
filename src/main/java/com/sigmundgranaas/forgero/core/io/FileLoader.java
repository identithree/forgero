package com.sigmundgranaas.forgero.core.io;

import java.io.*;

public class FileLoader {
    public InputStream loadStreamFromFile(File file) throws FileNotFoundException {
        return new BufferedInputStream(new FileInputStream(file));
    }
}
