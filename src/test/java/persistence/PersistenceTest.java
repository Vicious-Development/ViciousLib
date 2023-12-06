package persistence;

import com.vicious.viciouslib.persistence.KeyToClass;
import com.vicious.viciouslib.persistence.PersistenceHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class PersistenceTest {

    @BeforeEach
    void setUp() {
        KeyToClass.register(PersistentZ.class, "vicious.test.pz");
        KeyToClass.register(PersistentY.class, "vicious.test.py");
        KeyToClass.register(PersistentX.class, "vicious.test.px");
        TestCFG.init();
    }

    @Test
    public void testPrimitives() {
        assertTrue(TestCFG.boolFalse);
        assertFalse(TestCFG.boolTrue);
        assertNotEquals(TestCFG.bytePos, 127);
        assertNotEquals(TestCFG.byteNeg, -128);
        assertNotEquals(TestCFG.intPos, 1);
        assertNotEquals(TestCFG.intNeg, -1);
        assertNotEquals(TestCFG.shortPos, 23178);
        assertNotEquals(TestCFG.shortNeg, -3782);
        assertNotEquals(TestCFG.longPos, Integer.MAX_VALUE + 112312L);
        assertNotEquals(TestCFG.longNeg, Integer.MIN_VALUE - 1221L);
        assertNotEquals(TestCFG.floatPos, 34.656F);
        assertNotEquals(TestCFG.floatNeg, -2467.0F);
        assertNotEquals(TestCFG.doublePos, Float.MAX_VALUE + 11231.2D);
        assertNotEquals(TestCFG.doubleNeg, Float.MIN_VALUE - 12.21D);
        assertNotEquals(TestCFG.someChar, 'k');
    }

    @Test
    public void testStrings() {
        assertNotEquals(TestCFG.someString, "Hi I am cool.");
    }

    @Test
    public void testBoxedPrimitives() {
        assertTrue(TestCFG.boolFalseBoxed);
        assertFalse(TestCFG.boolTrueBoxed);
        assertNotEquals((byte) TestCFG.bytePosBoxed, 127);
        assertNotEquals((byte) TestCFG.byteNegBoxed, -128);
        assertNotEquals((int) TestCFG.intPosBoxed, 1);
        assertNotEquals((int) TestCFG.intNegBoxed, -1);
        assertNotEquals((short) TestCFG.shortPosBoxed, 23178);
        assertNotEquals((short) TestCFG.shortNegBoxed, -3782);
        assertNotEquals(TestCFG.longPosBoxed, Integer.MAX_VALUE + 112312L);
        assertNotEquals(TestCFG.longNegBoxed, Integer.MIN_VALUE - 1221L);
        assertNotEquals(TestCFG.floatPosBoxed, 34.656F);
        assertNotEquals(TestCFG.floatNegBoxed, -2467.0F);
        assertNotEquals(TestCFG.doublePosBoxed, Float.MAX_VALUE + 11231.2D);
        assertNotEquals(TestCFG.doubleNegBoxed, Float.MIN_VALUE - 12.21D);
        assertNotEquals(TestCFG.someCharBoxed, 'k');
    }

    @RepeatedTest(2)
    public void testCodewiseModification() {
        TestCFG.floatPos = -2138F;
        PersistenceHandler.save(TestCFG.class);
        PersistenceHandler.load(TestCFG.class);
        assertEquals(TestCFG.floatPos, -2138F);
        TestCFG.floatPos = -21388F;
        PersistenceHandler.save(TestCFG.class);
        PersistenceHandler.load(TestCFG.class);
        assertEquals(TestCFG.floatPos, -21388F);
    }

    @RepeatedTest(2)
    public void testNestedSavables() {
        TestCFG.a.a = 2;
        PersistenceHandler.save(TestCFG.class);
        PersistenceHandler.load(TestCFG.class);
        assertEquals(TestCFG.a.a,2);
        TestCFG.a.a = 3;
        PersistenceHandler.save(TestCFG.class);
        PersistenceHandler.load(TestCFG.class);
        assertEquals(TestCFG.a.a,3);

        TestCFG.end.D.C.b.one = 5;
        PersistenceHandler.save(TestCFG.class);
        PersistenceHandler.load(TestCFG.class);
        assertEquals(TestCFG.end.D.C.b.one,5);
        TestCFG.end.D.C.b.one = 99;
        PersistenceHandler.save(TestCFG.class);
        PersistenceHandler.load(TestCFG.class);
        assertEquals(TestCFG.end.D.C.b.one,99);
    }

    @RepeatedTest(2)
    public void testEnums(){
        TestCFG.e=PersistentE.FOUR;
        PersistenceHandler.save(TestCFG.class);
        PersistenceHandler.load(TestCFG.class);
        assertEquals(TestCFG.e,PersistentE.FOUR);
        TestCFG.e=PersistentE.THREE;
        PersistenceHandler.save(TestCFG.class);
        PersistenceHandler.load(TestCFG.class);
        assertEquals(TestCFG.e,PersistentE.THREE);

        for (PersistentF f : TestCFG.fs) {
            f.venus=2;
        }
        PersistenceHandler.save(TestCFG.class);
        PersistenceHandler.load(TestCFG.class);
        assertEquals(PersistentF.Wack.venus,2);
        assertEquals(PersistentF.CrAcK.venus,2);
        assertEquals(PersistentF.SMACK.venus,2);

        for (PersistentF f : TestCFG.fs) {
            f.venus=3;
        }
        PersistentF.SMACK.venus=-27;
        PersistenceHandler.save(TestCFG.class);
        PersistenceHandler.load(TestCFG.class);
        assertEquals(PersistentF.Wack.venus,3);
        assertEquals(PersistentF.CrAcK.venus,3);
        assertEquals(PersistentF.SMACK.venus,-27);

        TestCFG.unmappedF=PersistentF.SMACK;
        PersistenceHandler.save(TestCFG.class);
        PersistenceHandler.load(TestCFG.class);
        assertEquals(TestCFG.unmappedF,PersistentF.SMACK);

        TestCFG.unmappedF=PersistentF.CrAcK;
        PersistenceHandler.save(TestCFG.class);
        PersistenceHandler.load(TestCFG.class);
        assertEquals(TestCFG.unmappedF,PersistentF.CrAcK);
    }

    @RepeatedTest(2)
    public void testInternalStatic(){
        OtherTestCFG.isPool=false;
        PersistenceHandler.save(TestCFG.class);
        PersistenceHandler.load(TestCFG.class);
        assertFalse(OtherTestCFG.isPool);

        OtherTestCFG.isPool=true;
        PersistenceHandler.save(TestCFG.class);
        PersistenceHandler.load(TestCFG.class);
        assertTrue(OtherTestCFG.isPool);
    }

    @RepeatedTest(2)
    public void testCollections(){
        TestCFG.ints.clear();
        TestCFG.ints.add(1);
        TestCFG.ints.add(2);
        TestCFG.ints.add(3);
        TestCFG.ints.add(4);
        PersistenceHandler.save(TestCFG.class);
        PersistenceHandler.load(TestCFG.class);
        assertEquals(TestCFG.ints.get(0),1);
        assertEquals(TestCFG.ints.get(1),2);
        assertEquals(TestCFG.ints.get(2),3);
        assertEquals(TestCFG.ints.get(3),4);
        TestCFG.ints.clear();
        TestCFG.ints.add(2);
        TestCFG.ints.add(3);
        TestCFG.ints.add(4);
        TestCFG.ints.add(5);
        PersistenceHandler.save(TestCFG.class);
        PersistenceHandler.load(TestCFG.class);
        assertEquals(TestCFG.ints.get(0),2);
        assertEquals(TestCFG.ints.get(1),3);
        assertEquals(TestCFG.ints.get(2),4);
        assertEquals(TestCFG.ints.get(3),5);

        TestCFG.pas.clear();
        TestCFG.pas.add(new PersistentA());
        TestCFG.pas.add(new PersistentA());
        TestCFG.pas.add(new PersistentA());
        PersistenceHandler.save(TestCFG.class);
        PersistenceHandler.load(TestCFG.class);
        assertEquals(TestCFG.pas.get(0).a,1);
        assertEquals(TestCFG.pas.get(1).a,1);
        assertEquals(TestCFG.pas.get(2).a,1);

        TestCFG.pas.get(0).a=28;
        TestCFG.pas.get(1).a=298;
        TestCFG.pas.get(2).a=278;

        PersistenceHandler.save(TestCFG.class);
        PersistenceHandler.load(TestCFG.class);
        assertEquals(TestCFG.pas.get(0).a,28);
        assertEquals(TestCFG.pas.get(1).a,298);
        assertEquals(TestCFG.pas.get(2).a,278);
    }

    @RepeatedTest(2)
    public void testNestedLists(){
        TestCFG.nestedList.clear();
        ArrayList<Integer> ints = new ArrayList<>();
        ints.add(1);
        ints.add(2);
        ints.add(3);
        TestCFG.nestedList.add(ints);
        ints = new ArrayList<>();
        ints.add(4);
        ints.add(5);
        ints.add(6);
        TestCFG.nestedList.add(ints);
        PersistenceHandler.save(TestCFG.class);
        PersistenceHandler.load(TestCFG.class);
        assertEquals(TestCFG.nestedList.get(0).get(0),1);
        assertEquals(TestCFG.nestedList.get(0).get(1),2);
        assertEquals(TestCFG.nestedList.get(0).get(2),3);
        assertEquals(TestCFG.nestedList.get(1).get(0),4);
        assertEquals(TestCFG.nestedList.get(1).get(1),5);
        assertEquals(TestCFG.nestedList.get(1).get(2),6);
    }

    @RepeatedTest(2)
    public void testMaps(){
        TestCFG.cursed.clear();
        TestCFG.cursed.put(1,2.34);
        TestCFG.cursed.put(2,2.384);
        TestCFG.cursed.put(3,2.3476);
        PersistenceHandler.save(TestCFG.class);
        PersistenceHandler.load(TestCFG.class);
        assertEquals(TestCFG.cursed.get(1),2.34);
        assertEquals(TestCFG.cursed.get(2),2.384);
        assertEquals(TestCFG.cursed.get(3),2.3476);
    }

    @RepeatedTest(2)
    public void testExtremelyNestedMapsAndCollections(){
        TestCFG.superCursed.clear();
        HashMap<Double,ArrayList<Float>> m1 = new HashMap<>();
        ArrayList<Float> floats = new ArrayList<>();
        floats.add(3.5F);
        floats.add(3.9F);
        floats.add(3.1F);
        m1.put(1D,floats);
        TestCFG.superCursed.put("hi",m1);
        PersistenceHandler.save(TestCFG.class);
        PersistenceHandler.load(TestCFG.class);
        assertEquals(TestCFG.superCursed.get("hi").get(1D).get(0),3.5F);
        assertEquals(TestCFG.superCursed.get("hi").get(1D).get(1),3.9F);
        assertEquals(TestCFG.superCursed.get("hi").get(1D).get(2),3.1F);
    }

   // @RepeatedTest(2)
    public void testExtendingSavables(){
        TestCFG.z.v='6';
        TestCFG.z.b=978;
        TestCFG.z.c=false;
        TestCFG.z2.b=78;
        ((PersistentZ)TestCFG.z2).c=false;
        ((PersistentZ)TestCFG.z2).v='f';
        PersistenceHandler.save(TestCFG.class);
        PersistenceHandler.load(TestCFG.class);
        assertEquals(((PersistentZ) TestCFG.z2).v,'f');
        assertEquals(TestCFG.z2.b,78);
        assertFalse(((PersistentZ) TestCFG.z2).c);
        assertEquals(TestCFG.z.v,'6');
        assertEquals(TestCFG.z.b,978);
        assertFalse(TestCFG.z.c);
    }

    @RepeatedTest(2)
    public void testCollectingExtendedSavables(){
        TestCFG.extendeds.clear();
        TestCFG.extendeds.add(new PersistentX());
        TestCFG.extendeds.add(new PersistentY());
        TestCFG.extendeds.add(new PersistentZ());
        PersistenceHandler.save(TestCFG.class);
        PersistenceHandler.load(TestCFG.class);
        TestCFG.extendeds.get(0).b=7;
        ((PersistentY)TestCFG.extendeds.get(1)).c=false;
        ((PersistentZ)TestCFG.extendeds.get(2)).v='t';
        PersistenceHandler.save(TestCFG.class);
        PersistenceHandler.load(TestCFG.class);
        assertEquals(TestCFG.extendeds.get(0).b,7);
        assertFalse(((PersistentY) TestCFG.extendeds.get(1)).c);
        assertEquals(((PersistentZ)TestCFG.extendeds.get(2)).v,'t');
    }
}