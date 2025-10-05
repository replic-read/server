package com.rere.server;

import com.rere.server.inter.ReplicReadServerApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.mockStatic;

@SpringBootTest(classes = ReplicReadServerApplication.class)
class ReplicReadServerApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void mainCallsSpringApplication() {
        try (var mock = mockStatic(SpringApplication.class)) {
            mock.when(() -> SpringApplication.run(ReplicReadServerApplication.class))
                    .thenReturn(null);

            ReplicReadServerApplication.main(new String[0]);

            mock.verify(() -> SpringApplication.run(ReplicReadServerApplication.class));
        }
    }

}
