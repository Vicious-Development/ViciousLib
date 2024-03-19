package persistence;

import com.vicious.viciouslib.persistence.IPersistent;
import com.vicious.viciouslib.persistence.storage.aunotamations.PersistentPath;
import com.vicious.viciouslib.persistence.storage.aunotamations.Save;

public class Obj1 implements IPersistent {
    @PersistentPath
    public String path = "tests/obj.txt";

    @Save
    public boolean toggled = false;
}
