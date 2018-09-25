package webfx.framework.expression.terms;

import webfx.framework.expression.Expression;
import webfx.framework.expression.lci.DataReader;
import webfx.fxkits.extra.type.PrimType;
import webfx.fxkits.extra.type.Type;
import webfx.platforms.core.util.Booleans;

/**
 * @author Bruno Salmon
 */
public final class Not<T> extends UnaryExpression<T> {

    public Not(Expression<T> operand) {
        super(operand);
    }

    @Override
    public Type getType() {
        return PrimType.BOOLEAN;
    }

    @Override
    public Object evaluate(T domainObject, DataReader<T> dataReader) {
        return Booleans.isFalse(super.evaluate(domainObject, dataReader));
    }

    @Override
    public StringBuilder toString(StringBuilder sb) {
        sb.append("not(");
        operand.toString(sb);
        return sb.append(')');
    }
}
