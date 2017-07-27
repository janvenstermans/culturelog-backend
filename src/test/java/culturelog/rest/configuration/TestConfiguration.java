package culturelog.rest.configuration;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *  @author Jan Venstermans
 */
@Configuration
public class TestConfiguration {

    /**
     *
     * @see https://github.com/spring-projects/spring-boot/issues/4295
     */
    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        // avoid flyway scripts run (not H2 safe)
        return new FlywayMigrationStrategy() {
            @Override
            public void migrate(Flyway flyway) {
                // don't do anything
            }
        };
    }

}