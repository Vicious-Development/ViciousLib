package com.vicious.viciouslib.configuration;

import com.vicious.viciouslib.LoggerWrapper;
import com.vicious.viciouslib.serialization.SerializationUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CommentedJSONObject {
    public static class IsCommentException extends Exception{};
    private Map<String,Object> map = new HashMap<>();
    private int lineIndex = 0;
    private String line;
    private Scanner scan;
    public CommentedJSONObject(Scanner scan){
        this.scan=scan;
        while(scan.hasNextLine()){
            line=scan.nextLine();
            lineIndex=0;
            try {
                readLine();
            } catch (IsCommentException ignored) {}
        }
        scan.close();
    }
    public static CommentedJSONObject fromFile(File f) throws FileNotFoundException {
        CommentedJSONObject obj = new CommentedJSONObject(new Scanner(f));
        return new CommentedJSONObject(new Scanner(f));
    }
    public String readName() throws IsCommentException{
        StringBuilder val = new StringBuilder();
        int count = 0;
        for (int i = lineIndex; i < line.length(); i++) {
            char c = line.charAt(i);
            if(count == 0 && c == '#') throw new IsCommentException();
            else if(c == '"'){
                count++;
            }
            else if(count == 1){
                val.append(c);
            }
            else{
                //Skip blank space after ':'
                lineIndex++;
                return val.toString();
            }
        }
        return val.toString();
    }
    public String readValue(){
        StringBuilder val = new StringBuilder();
        for (int i = lineIndex; i < line.length(); i++) {
            char c = line.charAt(i);
            val.append(c);

        }
        return val.toString();
    }
    public void readLine() throws IsCommentException{
        String objectName = readName();
        String objectStringValue = readValue();
        Class<?> type = getExpectedType(objectStringValue);
        try {
            Object value = SerializationUtil.parse(type,objectStringValue);
            map.put(objectName,value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Class<?> getExpectedType(String str){
        char first = str.charAt(0);
        if(first == '"') return String.class;
        else if(first == '\'') return Character.class;
        else if(Character.isDigit(first) || first == '-') {
            BigDecimal bd = new BigDecimal(str);
            if(first == '-' && BigDecimal.ZERO.compareTo(bd)==0) {
                return Double.class;
            }
            return BigDecimal.class;
        }
        return String.class;
    }
    public static Writer comment(Writer writer, String comment) {
        try {
            writer.write("\n");
            writer.write(comment);
        } catch (IOException e) {
            LoggerWrapper.logError("Failed to write a comment: " + comment);
            e.printStackTrace();
        }
        return writer;
    }

    public static Writer writeValue(Writer writer, Object value, int indentFactor, int indent) throws JSONException, IOException {
        if (value == null || value.equals(null)) {
            writer.write("null");
        } else if (value instanceof JSONString) {
            Object o;
            try {
                o = ((JSONString) value).toJSONString();
            } catch (Exception e) {
                throw new JSONException(e);
            }
            writer.write(o != null ? o.toString() : convertToFileForm(value.toString()));
        } else if (value instanceof Number) {
            // not all Numbers may match actual JSON Numbers. i.e. fractions or Imaginary
            final String numberAsString = numberToString((Number) value);
            try {
                // Use the BigDecimal constructor for it's parser to validate the format.
                @SuppressWarnings("unused")
                BigDecimal testNum = new BigDecimal(numberAsString);
                // Close enough to a JSON number that we will use it unquoted
                writer.write(numberAsString);
            } catch (NumberFormatException ex){
                // The Number value is not a valid JSON number.
                // Instead we will quote it as a string
                convertToFileForm(numberAsString, writer);
            }
        } else if (value instanceof Boolean) {
            writer.write(value.toString());
        }
        else if (value instanceof Enum<?>) {
            writer.write(convertToFileForm(((Enum<?>)value).name()));
        } else if (value instanceof JSONObject) {
            ((JSONObject) value).write(writer, indentFactor, indent);
        } else if (value instanceof JSONArray) {
            ((JSONArray) value).write(writer, indentFactor, indent);
        } else if (value instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) value;
            new JSONObject(map).write(writer, indentFactor, indent);
        } else if (value instanceof Collection) {
            Collection<?> coll = (Collection<?>) value;
            new JSONArray(coll).write(writer, indentFactor, indent);
        } else if (value.getClass().isArray()) {
            new JSONArray(value).write(writer, indentFactor, indent);
        }
        else {
            convertToFileForm(SerializationUtil.serialize(value).toString(), writer);
        }
        return writer;
    }
    public static String convertToFileForm(String str){
        return '"' + str + '"';
    }
    public static String numberToString(Number number) throws JSONException {
        if (number == null) {
            throw new JSONException("Null pointer");
        }
        testValidity(number);

        // Shave off trailing zeros and decimal point, if possible.

        String string = number.toString();
        if (string.indexOf('.') > 0 && string.indexOf('e') < 0
                && string.indexOf('E') < 0) {
            while (string.endsWith("0")) {
                string = string.substring(0, string.length() - 1);
            }
            if (string.endsWith(".")) {
                string = string.substring(0, string.length() - 1);
            }
        }
        return string;
    }
    public static void testValidity(Object o) throws JSONException {
        if (o != null) {
            if (o instanceof Double) {
                if (((Double) o).isInfinite() || ((Double) o).isNaN()) {
                    throw new JSONException(
                            "JSON does not allow non-finite numbers.");
                }
            } else if (o instanceof Float) {
                if (((Float) o).isInfinite() || ((Float) o).isNaN()) {
                    throw new JSONException(
                            "JSON does not allow non-finite numbers.");
                }
            }
        }
    }
    //JSON Code, not mine.
    public static Writer convertToFileForm(String string, Writer w) throws IOException {
        if (string == null || string.length() == 0) {
            w.write("\"\"");
            return w;
        }
        char b;
        char c = 0;
        String hhhh;
        int i;
        int len = string.length();

        w.write('"');
        for (i = 0; i < len; i += 1) {
            b = c;
            c = string.charAt(i);
            switch (c) {
                case '\\':
                case '"':
                    w.write('\\');
                    w.write(c);
                    break;
                case '/':
                    if (b == '<') {
                        w.write('\\');
                    }
                    w.write(c);
                    break;
                case '\b':
                    w.write("\\b");
                    break;
                case '\t':
                    w.write("\\t");
                    break;
                case '\n':
                    w.write("\\n");
                    break;
                case '\f':
                    w.write("\\f");
                    break;
                case '\r':
                    w.write("\\r");
                    break;
                default:
                    if (c < ' ' || (c >= '\u0080' && c < '\u00a0')
                            || (c >= '\u2000' && c < '\u2100')) {
                        w.write("\\u");
                        hhhh = Integer.toHexString(c);
                        w.write("0000", 0, 4 - hhhh.length());
                        w.write(hhhh);
                    } else {
                        w.write(c);
                    }
            }
        }
        w.write('"');
        return w;
    }
}
