package naga.framework.expression.builder.terms;

import naga.framework.expression.Expression;
import naga.framework.expression.terms.function.Call;

/**
 * @author Bruno Salmon
 */
public class CallBuilder extends UnaryExpressionBuilder {

    public String functionName;
    public ExpressionArrayBuilder orderBy;

    public CallBuilder(String functionName) {
        this(functionName, null);
    }

    public CallBuilder(String functionName, ExpressionBuilder operand) {
        this(functionName, operand, null);
    }

    public CallBuilder(String functionName, ExpressionBuilder operand, ExpressionArrayBuilder orderBy) {
        super(operand);
        this.functionName = functionName;
        this.orderBy = orderBy;
    }

    @Override
    protected void propagateDomainClasses() {
        super.propagateDomainClasses();
        if (orderBy != null)
            orderBy.buildingClass = buildingClass;
    }

    @Override
    protected Call newUnaryOperation(Expression operand) {
        return new Call(functionName, operand, orderBy == null ? null : orderBy.build());
    }
}