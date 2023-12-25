package persistence;

import com.vicious.viciouslib.persistence.storage.aunotamations.PersistentPath;
import com.vicious.viciouslib.persistence.storage.aunotamations.Save;
import com.vicious.viciouslib.persistence.vson.VSONMap;

public class StringMessiness {
    @PersistentPath
    public static String dest = "tests/stringmessiness.txt";

    @Save
    public static VSONMap map = new VSONMap();

    static {
        map.put("one","kek{j}");
        map.put("two","{k}jkj");
        map.put("three","keg[l]");
        map.put("four","[l]smeck");
    }
}
