package ar.edu.itba.ss.airplane_boarding;

import ar.edu.itba.ss.airplane_boarding.io.OctaveFileSaver;
import ar.edu.itba.ss.airplane_boarding.io.OvitoFileSaverImpl;
import ar.edu.itba.ss.airplane_boarding.io.ProgramArguments;
import ar.edu.itba.ss.airplane_boarding.models.Room;
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
    private final SimulationEngine<Room.RoomState, Room> engine;

    /**
     * A {@link DataSaver} to output the Ovito file.
     */
    private final DataSaver<Room.RoomState> ovitoFileSaver;

    /**
     * A {@link DataSaver} to output the Octave file.
     */
    private final DataSaver<Room.RoomState> octaveFileSaver;

    /**
     * Constructor.
     *
     * @param programArguments The {@link ProgramArguments} for this simulation.
     */
    @Autowired
    public AirplaneBoarding(final ProgramArguments programArguments) {
        final double length = programArguments.getRoomProperties().getRoomLength();
        final double width = programArguments.getRoomProperties().getRoomWidth();
        final double door = programArguments.getRoomProperties().getRoomDoorSize();
        final double minRadius = programArguments.getParticleProperties().getMinRadius();
        final double maxRadius = programArguments.getParticleProperties().getMaxRadius();
        final double tao = programArguments.getParticleProperties().getTao();
        final double beta = programArguments.getParticleProperties().getBeta();
        final double maxVelocityModule = programArguments.getParticleProperties().getMaxVelocityModule();
        final int maxAmountOfParticles = programArguments.getParticleProperties().getMaxAmount();
        final double timeStep = programArguments.getTimeStep();
        final double duration = programArguments.getDuration();
        final String ovitoFilePath = programArguments.getOutputStuff().getOvitoFilePath();
        final String evacuationFilePath = programArguments.getOutputStuff().getOctaveFilePath();

        final Room room = new Room(length, width, door,
                minRadius, maxRadius, tao, beta, maxVelocityModule, maxAmountOfParticles,
                timeStep, duration);

        this.engine = new SimulationEngine<>(room);
        this.ovitoFileSaver = new OvitoFileSaverImpl(ovitoFilePath, minRadius, maxRadius);
        this.octaveFileSaver = new OctaveFileSaver(evacuationFilePath, duration, timeStep);
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
        this.engine.simulate(Room::shouldStop);
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
