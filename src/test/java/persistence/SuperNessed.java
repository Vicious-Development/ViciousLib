package persistence;

import com.vicious.viciouslib.persistence.storage.aunotamations.Save;

public class SuperNessed {
    public static class a{
        @Save
        public int one = 1;
    }
    public static class b{
        @Save
        public a b = new a();
    }
    public static class c{
        @Save
        public b C = new b();
    }
    public static class END{
        @Save
        public c D = new c();
    }
}
