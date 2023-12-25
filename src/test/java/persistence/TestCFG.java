package persistence;

import com.vicious.viciouslib.aunotamation.Aunotamation;
import com.vicious.viciouslib.persistence.storage.aunotamations.PersistentPath;
import com.vicious.viciouslib.persistence.storage.aunotamations.Save;
import com.vicious.viciouslib.persistence.storage.aunotamations.Typing;
import com.vicious.viciouslib.persistence.storage.aunotamations.Unmapped;
import com.vicious.viciouslib.persistence.vson.VSONMap;

import java.util.*;

public class TestCFG {
    public static void init(){
        fs.addAll(Arrays.asList(PersistentF.values()));
        Aunotamation.processObject(TestCFG.class);
    }

    @PersistentPath
    public static String path = "tests/tests.txt";

    @Save(description = "What's up")
    public static byte bytePos = 127;
    @Save
    public static byte byteNeg = -128;

    @Save
    public static short shortPos = 23178;
    @Save
    public static short shortNeg = -3782;

    @Save
    public static int intPos = 1;
    @Save
    public static int intNeg = -1;

    @Save
    public static long longPos = Integer.MAX_VALUE+112312L;
    @Save
    public static long longNeg = Integer.MIN_VALUE-1221L;

    @Save
    public static float floatPos = 34.656F;
    @Save
    public static float floatNeg = -2467.0F;

    @Save
    public static double doublePos = Float.MAX_VALUE+11231.2D;
    @Save
    public static double doubleNeg = Float.MIN_VALUE-12.21D;

    @Save
    public static boolean boolTrue = true;
    @Save
    public static boolean boolFalse = false;

    @Save
    public static char someChar = 'k';

    @Save
    public static String someString = "Hi I am cool.";

    @Save
    public static Byte bytePosBoxed = 127;
    @Save
    public static Byte byteNegBoxed = -128;

    @Save
    public static Short shortPosBoxed = 23178;
    @Save
    public static Short shortNegBoxed = -3782;

    @Save
    public static Integer intPosBoxed = 1;
    @Save
    public static Integer intNegBoxed = -1;

    @Save
    public static Long longPosBoxed = Integer.MAX_VALUE+112312L;
    @Save
    public static Long longNegBoxed = Integer.MIN_VALUE-1221L;

    @Save
    public static Float floatPosBoxed = 34.656F;
    @Save
    public static Float floatNegBoxed = -2467.0F;

    @Save
    public static Double doublePosBoxed = Float.MAX_VALUE+11231.2D;
    @Save
    public static Double doubleNegBoxed = Float.MIN_VALUE-12.21D;

    @Save
    public static Boolean boolTrueBoxed = true;
    @Save
    public static Boolean boolFalseBoxed = false;

    @Save
    public static Character someCharBoxed = 'k';

    @Save
    public static PersistentA a = new PersistentA();
    @Save
    public static PersistentB b = new PersistentB();

    @Save
    public static SuperNessed.END end = new SuperNessed.END();

    @Save
    public static PersistentE e = PersistentE.ONE;

    @Save
    @Typing(PersistentF.class)
    public static Set<PersistentF> fs = new HashSet<>();

    @Save
    @Unmapped
    public static PersistentF unmappedF = PersistentF.CrAcK;

    @Save
    public static Class<OtherTestCFG> otherTestCFGClass = OtherTestCFG.class;

    @Save
    @Typing(int.class)
    public static List<Integer> ints = new ArrayList<>();

    @Save
    @Typing(PersistentA.class)
    public static List<PersistentA> pas = new ArrayList<>();

    @Save
    @Typing({ArrayList.class,int.class})
    public static List<ArrayList<Integer>> nestedList = new ArrayList<>();

    @Save
    @Typing({Integer.class,Double.class})
    public static Map<Integer,Double> cursed = new HashMap<>();

    @Save
    @Typing({String.class,HashMap.class,Double.class,ArrayList.class,Float.class})
    public static Map<String,HashMap<Double,ArrayList<Float>>> superCursed = new HashMap<>();

    @Save
    public static PersistentZ z = new PersistentZ();

    @Save
    public static PersistentX z2 = new PersistentZ();

    @Save
    @Typing(PersistentX.class)
    public static List<PersistentX> extendeds = new ArrayList<>();

    @Save
    public static String stringWithBracket = "hi";

    @Save
    public static VSONMap mapwithbadStrs = new VSONMap();
}
