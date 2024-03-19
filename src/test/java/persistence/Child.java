package persistence;

import com.vicious.viciouslib.persistence.storage.aunotamations.Save;

public class Child extends Obj1{
    @Save
    public int i = 1;
}
