package ar.edu.itba.ss.airplane_boarding.utils;

import ar.edu.itba.ss.airplane_boarding.models.*;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Class in charge of initializing the system.
 */
@Component
public class ComponentsProvider {

    // ================================================================================================================
    // Airplane stuff
    // ================================================================================================================

    /**
     * Indicates how many rows of seats the built {@link Airplane} will have.
     */
    private final int amountOfSeatRows;

    /**
     * Indicates how many seats there between the central hall and the airplane side wall for each row.
     */
    private final int amountOfSeatsPerSide;

    /**
     * The width of the central hall.
     */
    private final double centralHallWidth;

    /**
     * The length of the front hall (i.e space between the first seat and the front "wall").
     */
    private final double frontHallLength;

    /**
     * The width each seat has.
     */
    private final double seatWidth;

    /**
     * The separation between each row of seats.
     */
    private final double seatSeparation;

    /**
     * The width of the airplane door.
     */
    private final double doorLength;

    /**
     * The airplane to be boarded.
     */
    private final Airplane airplane;

    // ================================================================================================================
    // Jet bridge stuff
    // ================================================================================================================

    /**
     * The length of thw jet bridge.
     */
    private final double jetBridgeLength;

    /**
     * The width of the jet bridge.
     */
    private final double jetBridgeWidth;

    /**
     * The jet bridge.
     */
    private final JetBridge jetBridge;


    // ================================================================================================================
    // Waiting room stuff
    // ================================================================================================================

    /**
     * Thw waiting room.
     */
    private final WaitingRoom waitingRoom;

    // ================================================================================================================
    // Boarding stuff
    // ================================================================================================================

    /**
     * The {@link BoardingStrategy} to be used (used to initialize {@link Particle}s according to it).
     */
    private final BoardingStrategy boardingStrategy;

    /**
     * The amount of passengers that can enter the airplane at once.
     */
    private final int batch;


    // ================================================================================================================
    // Particles stuff
    // ================================================================================================================

    /**
     * The min. radius of the {@link Particle}s to be created (which will be the starting radius).
     */
    private final double minRadius;

    /**
     * The max. radius of the {@link Particle}s to be created.
     */
    private final double maxRadius;
    /**
     * Mean time a particle needs to get to the minimum radius.
     */
    private final double tao;

    /**
     * Experimental constant that defines the linearity between speed changes and blocks avoidance.
     */
    private final double beta;

    /**
     * The max. speed a particle can reach.
     */
    private final double maxVelocityModule;

    /**
     * The separation between particles when they are initialized;
     */
    private final double particleSeparation;

    /**
     * The starting 'y' component.
     */
    private final double startingY;


    // ================================================================================================================
    // Constructor
    // ================================================================================================================

    /**
     * Constructor.
     *
     * @param amountOfSeatRows     Indicates how many rows of seats the built {@link Airplane} will have.
     * @param amountOfSeatsPerSide Indicates how many seats there
     *                             between the central hall and the airplane side wall for each row.
     * @param centralHallWidth     The width of the central hall.
     * @param frontHallLength      The length of the front hall (i.e space between the first seat and the front "wall").
     * @param seatWidth            The width each seat has.
     * @param seatSeparation       The separation between each row of seats.
     * @param doorLength           The length of the airplane door.
     * @param jetBridgeLength      The length of the jet bridge.
     * @param jetBridgeWidth       The width of the jet bridge.
     * @param boardingStrategy     The {@link BoardingStrategy} to be used
     *                             (used to initialize {@link Particle}s according to it).
     * @param batch                The amount of passengers that can enter the airplane at once.
     * @param minRadius            The min. radius of the {@link Particle}s to be created
     *                             (which will be the starting radius).
     * @param maxRadius            The max. amount of {@link Particle}s to be created.
     * @param tao                  Mean time a {@link Particle} needs to get to the minimum radius.
     * @param beta                 Experimental constant that defines the linearity
     *                             between speed changes and blocks avoidance for a {@link Particle}.
     * @param maxVelocityModule    The max. speed a {@link Particle} can reach.
     */
    @Autowired
    public ComponentsProvider(@Value("${custom.system.airplane.rows}") final int amountOfSeatRows,
                              @Value("${custom.system.airplane.columns}") final int amountOfSeatsPerSide,
                              @Value("${custom.system.airplane.central-hall-width}") final double centralHallWidth,
                              @Value("${custom.system.airplane.front-hall-length}") final double frontHallLength,
                              @Value("${custom.system.airplane.seat-width}") final double seatWidth,
                              @Value("${custom.system.airplane.seat-separation}") final double seatSeparation,
                              @Value("${custom.system.airplane.door-length}") final double doorLength,
                              @Value("${custom.system.jet-bridge.length}") final double jetBridgeLength,
                              @Value("${custom.system.jet-bridge.width}") final double jetBridgeWidth,
                              @Value("${custom.simulation.boarding-strategy}") final BoardingStrategy boardingStrategy,
                              @Value("${custom.simulation.entry-batch}") final int batch,
                              @Value("${custom.system.particle.min-radius}") final double minRadius,
                              @Value("${custom.system.particle.max-radius}") final double maxRadius,
                              @Value("${custom.system.particle.tao}") final double tao,
                              @Value("${custom.system.particle.beta}") final double beta,
                              @Value("${custom.system.particle.max-speed}") final double maxVelocityModule) {
        Assert.notNull(boardingStrategy, "The boarding strategy must not be null");
        // Airplane and Particle stuff are validated in their respective creator methods.

        // Boarding stuff
        this.boardingStrategy = boardingStrategy;
        this.batch = batch;

        // Particle stuff
        this.minRadius = minRadius;
        this.maxRadius = maxRadius;
        this.tao = tao;
        this.beta = beta;
        this.maxVelocityModule = maxVelocityModule;
        this.particleSeparation = 5 * maxRadius;
        this.startingY = jetBridgeWidth + 3 * particleSeparation;

        // Airplane stuff
        this.airplane = Airplane.buildFromSpecifications(amountOfSeatRows, amountOfSeatsPerSide,
                centralHallWidth, frontHallLength, seatWidth, seatSeparation, doorLength);
        this.amountOfSeatRows = amountOfSeatRows;
        this.amountOfSeatsPerSide = amountOfSeatsPerSide;
        this.centralHallWidth = centralHallWidth;
        this.frontHallLength = frontHallLength;
        this.seatWidth = seatWidth;
        this.seatSeparation = seatSeparation;
        this.doorLength = doorLength;
        final double airplaneWidth = centralHallWidth + 2 * amountOfSeatsPerSide * seatWidth;

        // Jet bridge stuff
        this.jetBridge = JetBridge.buildFromSpecifications(jetBridgeWidth, jetBridgeLength, airplaneWidth);
        this.jetBridgeLength = jetBridgeLength;
        this.jetBridgeWidth = jetBridgeWidth;

        // Waiting room stuff
        final double waitingRoomWidth = (batch + 1) * 2 * particleSeparation;
        final double waitingRoomLength = startingY
                + (2 * amountOfSeatRows * amountOfSeatsPerSide * 2 * particleSeparation) / batch;
        this.waitingRoom = WaitingRoom.buildFromSpecifications(waitingRoomWidth, waitingRoomLength,
                airplaneWidth, jetBridgeWidth, jetBridgeLength);

    }

    /**
     * Provides the {@link Airplane} to be boarded.
     *
     * @return The {@link Airplane} to be boarded.
     */
    public Airplane getAirplane() {
        return airplane;
    }

    /**
     * Provides the jet bridge.
     *
     * @return The jet bridge.
     */
    public JetBridge getJetBridge() {
        return jetBridge;
    }

    /**
     * Provides the waiting room.
     *
     * @return The waiting room.
     */
    public WaitingRoom getWaitingRoom() {
        return waitingRoom;
    }

    /**
     * Provides the batch value.
     *
     * @return The amount of passengers that can enter the airplane at once.
     */
    public int getBatch() {
        return batch;
    }

    /**
     * Creates the {@link List} of {@link Particle}.
     *
     * @return The {@link List} of {@link Particle}s.
     */
    public List<Particle> createParticles() {
        // TODO: particles positions should be initialized according to a boarding strategy? or random? on a queue?
        final List<Goal> goals = new LinkedList<>();
        switch (boardingStrategy) {
            case BACK_TO_FRONT: {
                for (int row = amountOfSeatRows - 1; row >= 0; row--) {
                    for (int column = amountOfSeatsPerSide - 1; column >= 0; column--) {
                        goals.add(buildGoal(row, column, Goal.AirplaneSide.LEFT));
                        goals.add(buildGoal(row, column, Goal.AirplaneSide.RIGHT));
                    }
                }
                break;
            }
            case OUTSIDE_IN: {
                for (int column = amountOfSeatsPerSide - 1; column >= 0; column--) {
                    for (int row = amountOfSeatRows - 1; row >= 0; row--) {
                        goals.add(buildGoal(row, column, Goal.AirplaneSide.LEFT));
                        goals.add(buildGoal(row, column, Goal.AirplaneSide.RIGHT));
                    }
                }
                break;
            }
            default:
                throw new RuntimeException("This should not happen");
        }

        final double startingX = 2 * particleSeparation
                + centralHallWidth / 2 + amountOfSeatsPerSide * seatWidth + maxRadius + jetBridgeLength;


        final Shuffler shuffler = new Shuffler(goals.size(), batch);
        final List<Particle> particles = new LinkedList<>();
        for (int idx = 0; idx < goals.size(); idx++) {
            final Goal goal = goals.get(idx);
            final int initialCallingNumber = idx / batch;
            // Must give a lot of separation in order to avoid congestion
            final Vector2D initialPosition = new Vector2D(
                    startingX + 2 * particleSeparation * shuffler.getNext(),
                    startingY + initialCallingNumber * 2 * particleSeparation
            );
            final Particle particle = new Particle(maxRadius, initialPosition, Vector2D.ZERO, goal,
                    minRadius, maxRadius, tao, beta, maxVelocityModule, initialCallingNumber);
            particles.add(particle);
        }
        return particles;
    }

    /**
     * Builds the {@link Goal} according to the given specifications..
     *
     * @param targetRow    The row where the final target is.
     * @param targetColumn The column where the final target is.
     * @param targetSide   The side of the airplane where the target is.
     * @return The built {@link Goal}.
     */
    private Goal buildGoal(final int targetRow, final int targetColumn, final Goal.AirplaneSide targetSide) {
        return new Goal(frontHallLength, centralHallWidth, doorLength, seatWidth, seatSeparation,
                targetRow, targetColumn, targetSide, jetBridgeWidth, particleSeparation, minRadius);
    }

    /**
     * A class that wraps the logic to shuffle values repeated in a batch.
     */
    private static final class Shuffler {
        /**
         * A {@link Queue} holding the elements in the shuffler.
         */
        private final Queue<Integer> nextValues;

        /**
         * Constructor.
         *
         * @param amountOfElements The overall amount of elements that the shuffler must contain.
         * @param batch            The batch value.
         */
        /* package */ Shuffler(final int amountOfElements, final int batch) {
            final int parts = amountOfElements / batch;
            final int reminder = amountOfElements % batch;

            this.nextValues = new LinkedList<>();
            for (int i = 0; i < parts; i++) {
                addToQueue(IntStream.range(0, batch).boxed().collect(Collectors.toList()));
            }
            if (reminder != 0) {
                addToQueue(IntStream.range(0, reminder).boxed().collect(Collectors.toList()));
            }

        }

        /**
         * Adds elements to the queue, performing a shuffling with a given randomness.
         *
         * @param rawSourceOfValues The raw source of values to be added to the queue.
         */
        private void addToQueue(final List<Integer> rawSourceOfValues) {
//            shuffle(rawSourceOfValues);
            nextValues.addAll(rawSourceOfValues);
        }

        /**
         * Returns the next element in the shuffler.
         *
         * @return The next element in the shuffler.
         * @throws NoSuchElementException if there are no more elements in the shuffler.
         */
        /* package */ int getNext() {
            return nextValues.remove();
        }

        /**
         * Shuffles the list with 35% of swapping probability.
         *
         * @param list The list to be shuffled.
         */
        static void shuffle(final List<Integer> list) {
            for (int i = 0; i < list.size(); i++) {
                // Toss a coin to check if the element must be swapped.
                if (new Random().nextInt(100) < 35) {
                    Collections.swap(list, i, new Random().nextInt(list.size()));
                }
            }
        }
    }
}
