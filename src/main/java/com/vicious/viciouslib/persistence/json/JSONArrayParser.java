package com.vicious.viciouslib.persistence.json;

import java.util.Scanner;

public class JSONArrayParser extends JSONParser{
    private final JSONArray obs = new JSONArray();
    private final Scanner scan;

    private boolean shouldParse(String line) {
        if(line.isEmpty()) return false;
        skipSyntax(line);
        return line.charAt(i) != '#' && line.charAt(i) != '}';
    }

    public JSONArrayParser(Scanner scan){
        this.scan=scan;
        while (scan.hasNextLine()){
            i=0;
            String line = scan.nextLine();
            skipSyntax(line);
            if(line.isEmpty()){
                continue;
            }
            if(line.charAt(i) == ']'){
                break;
            }
            else{
                parseLine(line);
            }
        }
    }

    public JSONArray getArray(){
        return obs;
    }

    private void parseLine(String line){
        skipSyntax(line);
        AssumedType value = parseValue(line);
        String parsed = value.string;
        if(parsed.startsWith("\"") && parsed.endsWith("\"")){
            parsed = parsed.substring(1,parsed.length()-1);
        }
        if(value instanceof AssumedType.Map){
            JSONMapParser inner = new JSONMapParser(scan);
            obs.add(new JSONValue(inner.getMap(),"[...]"));
        }
        else if(value instanceof AssumedType.Array){
            JSONArrayParser inner = new JSONArrayParser(scan);
            obs.add(new JSONValue(inner.getArray(),"[...]"));
        }
        else {
            obs.add(new JSONValue(Deserializer.fix(parsed, value.type), parsed));
        }
    }
}
