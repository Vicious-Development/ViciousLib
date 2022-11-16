package com.vicious.viciouslib.persistence.json;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class JSONParser {
    private JSONMap map = new JSONMap();
    int i = 0;
    Scanner scan;

    public JSONParser(String file) throws FileNotFoundException {
        scan = new Scanner(new File(file));
        while (scan.hasNextLine()){
            i=0;
            String line = scan.nextLine();
            if(shouldParse(line)) {
                parseLine(line);
            }
        }
        scan.close();
    }

    private boolean shouldParse(String line) {
        if(line.isEmpty()) return false;
        skipSyntax(line);
        return line.charAt(i) != '#' && line.charAt(i) != '}';
    }

    protected JSONParser(Scanner scan){
        this.scan=scan;
        while (scan.hasNextLine()){
            i=0;
            String line = scan.nextLine();
            skipSyntax(line);
            if(line.isEmpty()){
                continue;
            }
            if(line.charAt(i) == '}'){
                break;
            }
            else{
                parseLine(line);
            }
        }
    }

    public JSONMap getMap(){
        return map;
    }

    private void parseLine(String line){
        String name = parseName(line);
        skipSyntax(line);
        AssumedType value = parseValue(line);
        String parsed = value.string;
        if(parsed.startsWith("\"") && parsed.endsWith("\"")){
            parsed = parsed.substring(1,parsed.length()-1);
        }
        if(value instanceof AssumedType.Map){
            JSONParser inner = new JSONParser(scan);
            map.put(name, new JSONMapping(inner.getMap(),"{...}"));
        }
        else {
            map.put(name, new JSONMapping(Deserializer.fix(parsed, value.type), parsed));
        }
    }

    private void skipSyntax(String line) {
        while (i < line.length() && (Character.isWhitespace(line.charAt(i)) || line.charAt(i) == '=')){
            i++;
        }
    }

    private String parseName(String line){
        StringBuilder name = new StringBuilder();
        for (;i < line.length(); i++) {
            char c = line.charAt(i);
            if(c != ' ' && c != '='){
                name.append(c);
            }
            else{
                return name.toString();
            }
        }
        return name.toString();
    }
    private AssumedType parseValue(String line){
        AssumedType type = new AssumedType();
        if(i >= line.length()) return type;
        if(line.charAt(i) == '{'){
            return new AssumedType.Map();
        }
        for (;i < line.length(); i++) {
            char c = line.charAt(i);
            if(c != '\n'){
                type.append(c);
            }
        }
        return type;
    }
}
