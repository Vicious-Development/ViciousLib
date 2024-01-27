package vson;

import com.vicious.viciouslib.persistence.serialization.vson.VSONMapParser;
import com.vicious.viciouslib.persistence.serialization.vson.VSONObjectParser;
import com.vicious.viciouslib.util.quick.ObjectList;
import com.vicious.viciouslib.util.quick.ObjectMap;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class VSONParserTest {
    private VSONObjectParser makeVOP(String text){
        char last = text.charAt(0);
        ByteArrayInputStream bais = new ByteArrayInputStream(text.substring(1).getBytes());
        return new VSONObjectParser(bais,last);
    }

    private Object asObj(String text){
        return makeVOP(text).getObject();
    }

    @Test
    public void testPrimitives(){
        assertEquals("chair",asObj("chair"));
        assertEquals("chair",asObj("\"chair\""));
        assertEquals("[]",asObj("\"[]\""));
        assertNotEquals("[]",asObj("[]"));
        assertEquals("{}",asObj("\"{}\""));
        assertNotEquals("{}",asObj("{}"));
        assertEquals(1,(int)(long)asObj("1"));
        assertEquals(-1,(int)(long)asObj("-1"));
        assertEquals(1.0,asObj("1.0"));
        assertEquals(-1.0,asObj("-1.0"));
        assertEquals(true,asObj("true"));
        assertEquals(false,asObj("false"));
        assertEquals(false,asObj("f"));
        assertEquals(true,asObj("t"));
        assertEquals('c',asObj("'c'"));
        assertEquals(1L,asObj("1L"));
        assertEquals(1D,asObj("1D"));
        assertEquals(1F,asObj("1F"));
        assertEquals(1,asObj("1I"));
        assertEquals((byte)1,asObj("1B"));
        assertEquals((short) 1,asObj("1S"));
        assertEquals("ve\"n'|\"\"us",asObj("ve\\\"n'|\\\"\\\"us"));
    }

    @Test
    public void testMaps(){
        assertEquals(new ObjectMap(),asObj("{}"));
        assertEquals(new ObjectMap().modify(map->{
            map.put("key","value");
        }),asObj("{key=\"value\"}"));
        assertEquals(new ObjectMap().modify(map->{
            map.put("char",'c');
            map.put("int",1);
            map.put("byte",(byte)2);
            map.put("short",(short)3);
            map.put("long",5L);
            map.put("double",3.57D);
            map.put("float",56.1F);
            map.put("bool",true);
            map.put("string","yo");
        }),asObj("{char='c',int=1i,byte=2b,short=3s\nlong=5L,double=3.57D,float=56.1F,bool=true\nstring=\"yo\"}"));
        assertEquals(new ObjectMap().modify(map->{
            map.put("map2",new ObjectMap().modify(m2->{
                m2.put("map3", new ObjectMap());
                m2.put("map4", new ObjectMap().modify(m4->{
                    m4.put("key",132213L);
                }));
            }));
        }),asObj("{map2=    {map3=               {}\nmap4  =  {key  =   132213L}}}"));
    }

    @Test
    public void testCollections(){
        assertEquals(new ObjectList(),asObj("[]"));
        assertEquals(new ObjectList().modify(list->{
            list.add(new ObjectList());
        }),asObj("[[]]"));
        assertEquals(new ObjectList().modify(list->{
            list.add(1,(byte)2,(short)3,4L,true,5.321F,32.2D,"Yo",'l',ObjectList.empty(),ObjectList.of(ObjectList.of(5,6,7)));
        }),asObj("[1I,        2B      ,3S       ,4L,true, 5.321F,32.2D,\"Yo\"\n'l'\n[],[[5I,6I,7I]]]"));
    }

    /**
     * I tried to make the absolute messiest combination of characters to parser. Hopefully this is chaotic enough.
     */
    @Test
    public void testEverything(){
        ObjectMap expected = ObjectMap.empty().modify(map->{
            map.put("arr1",ObjectList.empty());
            map.put("char",'c');
            map.put("big",324879234231L);
            map.put("map1",ObjectMap.empty().modify(m1->{
                m1.put("arr1",ObjectList.of(true,false,true));
                m1.put("map2",ObjectMap.empty().modify(m2->{
                    m2.put("p",'p');
                    m2.put("q","querty");
                    m2.put("g",9.8D);
                    m2.put("map33",ObjectList.of(ObjectMap.empty().modify(m33->{
                        m33.put("THE END","The Quick brown fox pukes all over the program, causing severe \"errors\" and breaking \" ' \" Literally everything");
                    }),ObjectList.empty(),ObjectList.empty(),ObjectList.of(5)));
                }));
            }));
        });
        Object actual = asObj("{arr1=[]\n,char      =       'c',\"big\"=          324879234231L\n        map1={arr1=[true\nfalse,true],map2={p='p',q=\"querty\",g=       9.8D\n map33=[{THE END=\"The Quick brown fox pukes all over the program, causing severe \\\"errors\\\" and breaking \\\" ' \\\" Literally everything\"},[\n],[,,,,],[,\n5I,,]]}}}");
        System.out.println(expected);
        System.out.println(actual);
    }
}
