package team.bham.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class UserProfileTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static UserProfile getUserProfileSample1() {
        return new UserProfile().id(1L).name("name1").location("location1");
    }

    public static UserProfile getUserProfileSample2() {
        return new UserProfile().id(2L).name("name2").location("location2");
    }

    public static UserProfile getUserProfileRandomSampleGenerator() {
        return new UserProfile().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).location(UUID.randomUUID().toString());
    }
}
