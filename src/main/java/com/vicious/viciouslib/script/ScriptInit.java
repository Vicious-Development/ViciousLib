package com.vicious.viciouslib.script;

import com.vicious.viciouslib.persistence.KeyToClass;
import com.vicious.viciouslib.script.condition.impl.*;
import com.vicious.viciouslib.script.condition.impl.numerical.GreaterThan;
import com.vicious.viciouslib.script.condition.impl.numerical.GreaterThanOrEqual;
import com.vicious.viciouslib.script.condition.impl.numerical.LessThan;
import com.vicious.viciouslib.script.condition.impl.numerical.LessThanOrEqual;
import com.vicious.viciouslib.script.operations.*;

public class ScriptInit {
    public static void init(){
        KeyToClass.register(And.class,"vscript.condition.and");
        KeyToClass.register(Or.class,"vscript.condition.or");
        KeyToClass.register(XOr.class,"vscript.condition.xor");
        KeyToClass.register(Not.class,"vscript.condition.not");
        KeyToClass.register(IsTrue.class,"vscript.condition.istrue");
        KeyToClass.register(IsFalse.class,"vscript.condition.isfalse");
        KeyToClass.register(GreaterThan.class,"vscript.condition.greater");
        KeyToClass.register(GreaterThanOrEqual.class,"vscript.condition.greaterequals");
        KeyToClass.register(LessThan.class,"vscript.condition.lesser");
        KeyToClass.register(LessThanOrEqual.class,"vscript.condition.lesserequals");
        KeyToClass.register(ObjectEquals.class,"vscript.condition.equals");
        KeyToClass.register(MemoryEquals.class,"vscript.condition.memequals");
        KeyToClass.register(Contains.class,"vscript.condition.contains");

        KeyToClass.register(ExecStore.class,"vscript.operation.execstore");
        KeyToClass.register(Execute.class,"vscript.operation.execute");
        KeyToClass.register(Retrieve.class,"vscript.operation.retrieve");
        KeyToClass.register(Store.class,"vscript.operation.store");
        KeyToClass.register(JumpIf.class,"vscript.operation.jumpif");
        KeyToClass.register(Jump.class,"vscript.operation.jump");
        KeyToClass.register(Compare.class,"vscript.operation.compare");
        KeyToClass.register(Return.class,"vscript.operation.return");
        KeyToClass.register(ExecReject.class,"vscript.operation.execreject");
        KeyToClass.register(NoOp.class,"vscript.operation.noop");
        KeyToClass.register(NoOp.class,"vscript.operation.push");

    }
}
