package tools;

import sun.nio.cs.ext.IBM1025;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class StringEncoder {
    public static String encode(String source){
        source = source.replace("\u0418", "\u0438");
        byte[] buffer = source.getBytes(StandardCharsets.UTF_8);
        return new String(buffer);
    }
}
