package ar.edu.itba.ss.exit_room;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hello world!
 */
@SpringBootApplication
public class ExitRoom implements CommandLineRunner, InitializingBean {

    /**
     * The {@link Logger} object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ExitRoom.class);


    @Override
    public void afterPropertiesSet() throws Exception {
        // TODO: initialize me here
    }

    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("Hello, ExitRoom!");
        // First, simulate
        simulate();
        // Then, save
        save();
        LOGGER.info("Bye-bye!");
        System.exit(0);
    }


    /**
     * Performs the simulation phase of the program.
     */
    private void simulate() {
        LOGGER.info("Starting simulation...");
        // TODO: simulate here
        LOGGER.info("Finished simulation");
    }

    /**
     * Performs the save phase of the program.
     */
    private void save() {
        LOGGER.info("Saving outputs...");
        // TODO: save here
        LOGGER.info("Finished saving output in all formats.");
    }

    public static void main(String[] args) {
        SpringApplication.run(ExitRoom.class, args);
    }
}
