package persistence;

import com.vicious.viciouslib.persistence.storage.aunotamations.PersistentPath;
import com.vicious.viciouslib.persistence.storage.aunotamations.Save;

public class InstantiatedConfig {
    @Save
    public int one = 0;
    @PersistentPath
    public String path;
    public InstantiatedConfig(String path){
        this.path=path;
    }
}
