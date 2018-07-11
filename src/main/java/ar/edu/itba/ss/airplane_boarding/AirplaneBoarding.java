package ar.edu.itba.ss.airplane_boarding;

import ar.edu.itba.ss.airplane_boarding.models.BoardingScene;
import ar.edu.itba.ss.g7.engine.io.DataSaver;
import ar.edu.itba.ss.g7.engine.simulation.SimulationEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.LinkedList;

/**
 * Main class.
 */
@SpringBootApplication
public class AirplaneBoarding implements CommandLineRunner, InitializingBean {

    /**
     * The {@link Logger} object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AirplaneBoarding.class);

    /**
     * The {@link SimulationEngine}.
     */
    private final SimulationEngine<BoardingScene.BoardingSceneState, BoardingScene> engine;

    /**
     * A {@link DataSaver} to output the Ovito file.
     */
    private final DataSaver<BoardingScene.BoardingSceneState> ovitoFileSaver;

    /**
     * A {@link DataSaver} to output the Octave file.
     */
    private final DataSaver<BoardingScene.BoardingSceneState> octaveFileSaver;


    @Autowired
    public AirplaneBoarding(final BoardingScene system,
                            final DataSaver<BoardingScene.BoardingSceneState> ovitoFileSaver,
                            final DataSaver<BoardingScene.BoardingSceneState> octaveFileSaver) {

        this.engine = new SimulationEngine<>(system);
        this.ovitoFileSaver = ovitoFileSaver;
        this.octaveFileSaver = octaveFileSaver;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        this.engine.initialize();
    }

    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("Hello, AirplaneBoarding!");
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
        this.engine.simulate(BoardingScene::shouldStop);
        LOGGER.info("Finished simulation");
    }

    /**
     * Performs the save phase of the program.
     */
    private void save() {
        LOGGER.info("Saving outputs...");
        ovitoFileSaver.save(new LinkedList<>(this.engine.getResults()));
        octaveFileSaver.save(new LinkedList<>(this.engine.getResults()));
        LOGGER.info("Finished saving output in all formats.");
    }

    /**
     * Entry point.
     *
     * @param args The program arguments.
     */
    public static void main(String[] args) {
        SpringApplication.run(AirplaneBoarding.class, args);
    }
}
