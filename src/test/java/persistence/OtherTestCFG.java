package persistence;

import com.vicious.viciouslib.persistence.storage.aunotamations.Save;
import com.vicious.viciouslib.persistence.storage.aunotamations.Unmapped;

public class OtherTestCFG {
    @Save
    public static int pool = 37;

    @Save
    public static boolean isPool = true;

    @Save
    @Unmapped
    public static PersistentF f = PersistentF.CrAcK;
}
