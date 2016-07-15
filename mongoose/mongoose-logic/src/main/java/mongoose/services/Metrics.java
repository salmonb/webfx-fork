package mongoose.services;

import java.time.Instant;

/**
 * @author Bruno Salmon
 */
public interface Metrics {

    void setDate(Instant date);

    Instant getDate();

    void setMemoryTotal(Long memoryTotal);

    Long getMemoryTotal();

    void setMemoryFree(Long memoryFree);

    Long getMemoryFree();

    void setMemoryMax(Long memoryMax);

    Long getMemoryMax();

    void setSystemLoadAverage(Double systemLoadAverage);

    Double getSystemLoadAverage();

    void setProcessCpuLoad(Double processCpuLoad);

    Double getProcessCpuLoad();

    Long getMemoryUsed();
}
