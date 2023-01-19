package com.vicious.viciouslib.persistence.json;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class JSONMapParser extends JSONParser{
    private final JSONMap map = new JSONMap();
    private final Scanner scan;

    public JSONMapParser(String file) throws FileNotFoundException {
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
        return line.charAt(i) != '#' && line.charAt(i) != '}' && line.charAt(i) != ']';
    }

    //TODO read entire lines.
    protected JSONMapParser(Scanner scan){
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
            if(parsed.length() == 2){
                parsed = "";
            }
            else {
                parsed = parsed.substring(1, parsed.length() - 1);
            }
        }
        if(value instanceof AssumedType.Map){
            JSONMapParser inner = new JSONMapParser(scan);
            map.put(name, new JSONMapping(inner.getMap(),"{...}"));
        }
        else if(value instanceof AssumedType.Array){
            JSONArrayParser inner = new JSONArrayParser(scan);
            map.put(name, new JSONMapping(inner.getArray(),"[...]"));
        }
        else {
            map.put(name, new JSONMapping(Deserializer.fix(parsed, value.type), parsed));
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
}
