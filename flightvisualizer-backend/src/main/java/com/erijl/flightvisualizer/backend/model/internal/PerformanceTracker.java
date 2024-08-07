package com.erijl.flightvisualizer.backend.model.internal;

import org.springframework.util.StopWatch;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class PerformanceTracker {
    private final StopWatch stopWatch;
    private final Map<String, Long> performanceMap = new HashMap<>();

    private long startTime = 0;

    public PerformanceTracker() {
        this.stopWatch = new StopWatch();
    }

    /**
     * Start the initial tracking
     */
    public void startTracking() {
        if(stopWatch.isRunning()) return;
        stopWatch.start();
        startTime = System.currentTimeMillis();
    }

    /**
     * Add a performance key to the tracker
     *
     * @param key the key to add
     */
    public void addPerformance(String key) {
        long currentTime = System.currentTimeMillis();
        performanceMap.put(key, currentTime - startTime);
    }

    /**
     * Get the complete performance track record as a string
     *
     * @return the performance track record as a string
     */
    public String getPerformanceTrackrecordString() {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("\nPerformanceTracker: \n\n");

            performanceMap.forEach((key, value) -> {
                builder.append(key)
                        .append(": ")
                        .append(formatDuration(value))
                        .append("\n");
            });
            builder.append("\n");
            builder.append("Total time: ").append(formatDuration(stopWatch.getTotalTimeMillis()));

            return builder.toString();
        } catch (Exception e) {
            return "Error generating performance track record: " + e.getMessage();
        }
    }

    /**
     * Stop the tracking
     */
    public void stop() {
        this.addPerformance("Finished");
        this.stopWatch.stop();
    }


    private String formatDuration(long millis) {
        long seconds = Duration.ofMillis(millis).getSeconds();
        long HH = seconds / 3600;
        long MM = (seconds % 3600) / 60;
        long SS = seconds % 60;
        return String.format("%02dh %02dmin %02dsec", HH, MM, SS);
    }
}