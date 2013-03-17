package ru.zenmoney.library;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import android.util.Log;

public class LogTool extends OutputStream {
    private ByteArrayOutputStream bos = new ByteArrayOutputStream();
    private String name;
    private boolean enabled;


    public LogTool(String name, boolean enabled) {
        this.name = name;
        this.enabled = enabled;
    }

    @Override
    public void write(int b) throws IOException {
        if (!enabled) return;
        if (b == (int) '\n') {
            String s = new String(this.bos.toByteArray());
            this.bos = new ByteArrayOutputStream();
        } else {
            this.bos.write(b);
        }
    }
}