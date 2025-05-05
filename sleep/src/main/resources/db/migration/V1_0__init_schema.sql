CREATE TABLE sleep_logs (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    sleep_date DATE NOT NULL,
    time_in_bed_start TIMESTAMP NOT NULL,
    time_in_bed_end TIMESTAMP NOT NULL,
    total_time_in_bed_minutes INTEGER NOT NULL,
    morning_feeling VARCHAR(10) NOT NULL
);