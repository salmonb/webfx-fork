package mongooses.core.domainmodel.functions;

import webfx.fxkits.extra.type.PrimType;
import webfx.fxkits.extra.type.Type;
import webfx.platforms.core.util.Dates;
import webfx.framework.expression.lci.DataReader;
import webfx.framework.expression.terms.function.Function;

import java.time.LocalDate;
import java.time.Month;

/**
 * @author Bruno Salmon
 */
public class DateIntervalFormat extends Function {

    public DateIntervalFormat() {
        super("dateIntervalFormat", new String[] {"date1", "date2"}, new Type[] {null, null}, PrimType.STRING, true);
    }

    @Override
    public Object evaluate(Object argument, DataReader dataReader) {
        try {
            Object[] arguments = (Object[]) argument;

            LocalDate date1 = Dates.parseIsoLocalDate((String) arguments[0]);
            LocalDate date2 = Dates.parseIsoLocalDate((String) arguments[1]);

            int day1 = date1.getDayOfMonth();
            Month month1 = date1.getMonth();
            String month1Name = month1.name().toLowerCase();
            int day2 = date2.getDayOfMonth();
            Month month2 = date2.getMonth();
            int year2 = date2.getYear();
            StringBuffer sb = new StringBuffer();
            if (month1 == month2) {
                sb.append(day1);
                if (day2 != day1)
                    sb.append('-').append(day2);
                sb.append(' ').append(month1Name);
            } else
                sb.append(day1).append(' ').append(month1Name).append(" - ").append(day2).append(' ').append(month2.name().toLowerCase());
            if (year2 != LocalDate.now().getYear())
                sb.append(' ').append(year2);
            return sb.toString();
        } catch (Exception e) {
            return argument;
        }
    }
}