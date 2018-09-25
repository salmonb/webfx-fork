package webfx.framework.expression.terms.function.java;

import webfx.framework.expression.lci.DataReader;
import webfx.framework.expression.terms.function.Function;

/**
 * @author Bruno Salmon
 */
public final class Coalesce extends Function {

    public Coalesce() {
        super("coalesce", null, null, null, true);
    }

    @Override
    public Object evaluate(Object argument, DataReader dataReader) {
        if (!(argument instanceof Object[]))
            return argument;
        for (Object arg : ((Object[]) argument)) {
            if (arg != null /* && !(arg instanceof UnknownValue) */)
                return arg;
        }
        return null;
    }
}
