package script;

import com.vicious.viciouslib.script.Constants;
import com.vicious.viciouslib.script.condition.impl.IsFalse;
import com.vicious.viciouslib.script.condition.impl.IsTrue;
import com.vicious.viciouslib.script.function.ScriptFunction;
import com.vicious.viciouslib.script.impl.Script;
import com.vicious.viciouslib.script.impl.ScriptContext;
import com.vicious.viciouslib.script.operations.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ScriptTest {
    @Test
    public void testMonoArgVoid(){
        Script script = new Script();
        ScriptContext context = new ScriptContext();
        //Init
        context.getVariable("string").setValue("What's Up Doc?");
        script.addFunction("println", ScriptFunction.ofVoid((s)->System.out.println(s[0]),1));
        //Script Logic
        script.append(new Retrieve("string"));
        script.append(new Execute("println"));
        context.setScript(script);
        context.execute();
    }
    @Test
    public void testMultiArgVoid(){
        Script script = new Script();
        ScriptContext context = new ScriptContext();
        //Init
        context.getVariable("string").setValue("What's Up Sock?");
        context.getVariable("string1").setValue("I'm not a sock?");
        script.addFunction("println2", ScriptFunction.ofVoid((s)->{
            assertEquals(context.getVariable("string").getValue(),s[0]);
            assertEquals(context.getVariable("string1").getValue(),s[1]);
            for (Object o : s) {
                System.out.println(o);
            }
        },2));
        //Script Logic
        script.append(new Retrieve("string"));
        script.append(new Retrieve("string1"));
        script.append(new Execute("println2"));
        context.setScript(script);
        context.execute();
    }
    @Test
    public void testMonoArgReturn(){
        Script script = new Script();
        ScriptContext context = new ScriptContext();
        //Init
        context.getVariable("var1").setValue(10);
        script.addFunction("toDouble", ScriptFunction.of(args->(double)(int)args[0],1));
        //Logic
        script.append(new Retrieve("var1"));
        script.append(new ExecStore("var1","toDouble"));
        context.setScript(script);
        context.execute();
        assertTrue(context.getVariable("var1").getValue() instanceof Double);
        assertEquals(10.0,context.getVariable("var1").getValue());
    }
    @Test
    public void testMultiArgReturn(){
        Script script = new Script();
        ScriptContext context = new ScriptContext();
        //Init
        script.addFunction("times", ScriptFunction.of(args->{
            double result = 1;
            for (Object arg : args) {
                result *= ((Number)arg).doubleValue();
            }
            return result;
        },5));
        //Logic
        context.push(1);
        context.push(2);
        context.push(3);
        context.push(4);
        context.push(5);
        script.append(new Execute("times"));
        context.setScript(script);
        context.execute();
        assertEquals(2D * 3D * 4D * 5D,context.pop());
    }

    /**
     * Tests a branching script. The branch will be jumped if the condition is false. If the condition is true the branch will not be jumped.
     */
    @Test
    public void branch(){
        ScriptContext context = new ScriptContext();
        Script script = new Script();
        AtomicInteger idx = new AtomicInteger(5);
        AtomicInteger delta = new AtomicInteger(5);
        //Setup
        script.addFunction("verify", new ScriptFunction() {
            @Override
            public Object apply(Object[] arguments) {
                assertEquals(idx.get(),context.getIndex());
                idx.addAndGet(delta.get());
                return true;
            }
            @Override
            public int length() {
                return 0;
            }
        });
        script.append(new NoOp());
        script.append(new NoOp());
        script.append(new NoOp());
        script.append(new NoOp());
        script.append(new NoOp());
        script.append(new Execute("verify"));
        script.branch(new IsTrue(),asList(new NoOp(),new NoOp(), new Execute("verify")));
        script.append(new NoOp());
        script.append(new NoOp());
        script.append(new NoOp());
        script.append(new NoOp());
        script.append(new Execute("verify"));
        context.setScript(script);
        //Execute
        context.execute();
        //Setup false script
        delta.set(10);
        idx.set(5);
        script.set(6, new Compare(new IsFalse()));
        //There is no function crash so if this is not jumped then there will be a crash.
        script.set(10,new Execute("crash"));
        context.compile();
        context.execute();
    }

    @Test
    public void whileLoop(){
        AtomicInteger loopCounter = new AtomicInteger();
        AtomicBoolean isTrue = new AtomicBoolean(true);
        ScriptContext context = new ScriptContext();
        Script script = new Script();
        script.addFunction("incre",new ScriptFunction() {
            @Override
            public Object apply(Object[] arguments) {
                loopCounter.incrementAndGet();
                if(loopCounter.get() == 5){
                    isTrue.set(false);
                }
                return Constants.VOID;
            }

            @Override
            public int length() {
                return 0;
            }
        });
        context.push(true);
        script.whileLoop(new IsTrue(),asList(new ExecReject("incre"),new Push(isTrue::get)));
        context.setScript(script);
        context.execute();
        assertEquals(5,loopCounter.get());
    }

    private static List<Operation> asList(Operation... ops){
        return new ArrayList<>(Arrays.asList(ops));
    }
}
