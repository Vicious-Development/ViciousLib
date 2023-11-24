package persistence;

import com.vicious.viciouslib.persistence.storage.aunotamations.Save;

public class PersistentY extends PersistentX{
    @Save
    public boolean c = true;
}
