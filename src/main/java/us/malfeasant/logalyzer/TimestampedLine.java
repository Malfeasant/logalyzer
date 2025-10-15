package us.malfeasant.logalyzer;

import java.time.LocalDateTime;
import java.util.Optional;

class TimestampedLine {
    private final Optional<LocalDateTime> lineTime;
    private final Optional<LocalDateTime> normTime;
    private final int lineNumber;

    private final int offset;
    private final int length;

    TimestampedLine() {
        
    }
}
