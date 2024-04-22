package team.bham.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TeamTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Team getTeamSample1() {
        return new Team().id(1L).name("name1").description("description1").colour("colour1").schedule("schedule1");
    }

    public static Team getTeamSample2() {
        return new Team().id(2L).name("name2").description("description2").colour("colour2").schedule("schedule2");
    }

    public static Team getTeamRandomSampleGenerator() {
        return new Team()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .colour(UUID.randomUUID().toString())
            .schedule(UUID.randomUUID().toString());
    }
}
