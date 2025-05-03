package com.noom.interview.fullstack.sleep;

import com.noom.interview.fullstack.sleep.SleepApplication;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles(SleepApplication.UNIT_TEST_PROFILE)
public class SleepApplicationTests {

    @Test
    public void contextLoads() {
        Assertions.assertThat(true).isTrue();
    }
}
