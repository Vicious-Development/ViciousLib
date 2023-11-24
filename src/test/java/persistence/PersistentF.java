package persistence;

import com.vicious.viciouslib.persistence.storage.aunotamations.Save;

public enum PersistentF {
    Wack,
    SMACK,
    CrAcK;

    @Save
    public int venus = 0;
}
