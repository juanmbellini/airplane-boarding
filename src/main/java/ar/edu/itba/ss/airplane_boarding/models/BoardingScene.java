package ar.edu.itba.ss.airplane_boarding.models;

import ar.edu.itba.ss.airplane_boarding.utils.ComponentsProvider;
import ar.edu.itba.ss.g7.engine.models.System;
import ar.edu.itba.ss.g7.engine.simulation.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.DoubleSupplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents the boarding scene (i.e the {@link System} to be simulated).
 */
@Component
public class BoardingScene implements System<BoardingScene.BoardingSceneState> {

    private final double MAX_DURATION = 45 * 60;

    /**
     * A {@link DoubleSupplier} that supplies random double values between 30d and 45d.
     * Is used to get random values of storing luggage time.
     */
    private final static DoubleSupplier RANDOM_STORING_LUGGAGE_TIME_SUPPLIER =
            () -> new Random().nextInt(10) < 5 ? 10d + 10d * new Random().nextDouble() : 0d;

    // ================================================================================================================
    // fixed Obstacles and particles
    // ================================================================================================================

    /**
     * The {@link Wall}s in the system.
     */
    private final List<Wall> obstacles;

    /**
     * The {@link Particle}s in this room.
     */
    private final List<Particle> particles;

    // ================================================================================================================
    // Update stuff
    // ================================================================================================================

    /**
     * The time step (used to update in time the system).
     */
    private final double timeStep;

    /**
     * The actual time.
     */
    private double actualTime;

    /**
     * Calls that have been made to passengers.
     */
    private int calls;

    /**
     * A flag indicating that all passengers are already seated.
     */
    private boolean areAllSeated;

    /**
     * The amount of passengers that can enter the airplane at once.
     */
    private final int batch;

    /**
     * Discount time given to finish the simulation when all passengers are seated.
     */
    private double discountTime;


    // ================================================================================================================
    // Others
    // ================================================================================================================

    /**
     * A {@link Map} holding the waiting time for particles (to simulate the moment in which they store luggage).
     */
    private final Map<Particle, Double> storingLuggageTime;

    /**
     * The components provider.
     */
    private final ComponentsProvider componentsProvider;

    /**
     * Indicates whether this room is clean (i.e can be used to perform the simulation from the beginning).
     */
    private boolean clean;


    // ================================================================================================================
    // Constructor
    // ================================================================================================================

    /**
     * Constructor.
     *
     * @param componentsProvider The {@link ComponentsProvider} that provides all the stuff to perform the simulation.
     * @param timeStep           The time step (used to update in time the system).
     */
    @Autowired
    public BoardingScene(final ComponentsProvider componentsProvider,
                         @Value("${custom.simulation.time-step}") final double timeStep) {
        this.componentsProvider = componentsProvider;

        // obstacles and particles
        final List<Wall> obstacles = new LinkedList<>();
        obstacles.addAll(componentsProvider.getAirplane().getObstacles());
        obstacles.addAll(componentsProvider.getJetBridge().getObstacles());
        obstacles.addAll(componentsProvider.getWaitingRoom().getObstacles());
        this.obstacles = Collections.unmodifiableList(obstacles);

        this.particles = componentsProvider.createParticles();

        // Update stuff
        this.actualTime = 0;
        this.calls = 1;
        this.timeStep = timeStep;
        this.areAllSeated = false;
        this.batch = componentsProvider.getBatch();
        this.discountTime = 0;

        // Others
        this.clean = true;
        this.storingLuggageTime = new HashMap<>();
    }

    // ================================================================================================================
    // Getters
    // ================================================================================================================

    /**
     * @return The {@link Wall}s in the system.
     */
    public List<Wall> getObstacles() {
        return obstacles; // This can be returned as is because it is unmodifiable.
    }

    /**
     * @return The {@link Particle}s in this room.
     */
    /* package */ List<Particle> getParticles() {
        return new LinkedList<>(particles);
    }

    /**
     * @return The time step (used to update in time the system).
     */
    /* package */ double getTimeStep() {
        return timeStep;
    }

    /**
     * @return The actual time.
     */
    public double getActualTime() {
        return actualTime;
    }

    /**
     * Indicates whether the simulation should stop.
     *
     * @return {@code true} if the simulation should stop, or {@code false} otherwise.
     */
    public boolean shouldStop() {
        return actualTime > MAX_DURATION || discountTime > 60;
    }


    // ================================================================================================================
    // Interface stuff
    // ================================================================================================================

    @Override
    public void update() {
        this.clean = false;

        // First, get those that are storing yet luggage
        final List<Particle> areStoringLuggage = storingLuggageTime.entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        // Also get those that are noy yet called
        final List<Particle> notYetCalled = particles.stream()
                .filter(p -> !p.itItsTurn())
                .collect(Collectors.toList());

        // Then filter the particle list to get just the ones that are not storing luggage and have been called.
        final List<Particle> particlesThatCanMove = particles.stream()
                .filter(p -> !notYetCalled.contains(p))
                .filter(p -> !areStoringLuggage.contains(p))
                .collect(Collectors.toList());

        // Then, prepare the particles in order to be moved
        for (Particle particle : particlesThatCanMove) {
            final List<Obstacle> inContact = Stream
                    .concat(
                            obstacles.stream(),
                            particles.stream().filter(otherParticle -> otherParticle != particle)
                    )
                    .filter(obstacle -> obstacle.doOverlap(particle))
                    .collect(Collectors.toList());
            particle.prepareMove(inContact, timeStep);
        }

        // Then, move
        particlesThatCanMove.forEach(particle -> particle.move(timeStep));

        // Then update the storing luggage times
        final Map<Particle, Double> newStoringLuggageTimes = storingLuggageTime.entrySet().stream()
                .filter(e -> e.getValue() > 0) // Only update those that are still storing luggage
                .map(e -> new AbstractMap.SimpleEntry<>(e.getKey(), e.getValue() - timeStep))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        newStoringLuggageTimes.forEach(storingLuggageTime::put);

        // Then, check if there are new particles that have arrived to their seats
        final List<Particle> newStoringLuggage = particles.stream()
                .filter(p -> !storingLuggageTime.containsKey(p))
                .filter(Particle::isStoringLuggage)
                .collect(Collectors.toList());
        newStoringLuggage.forEach(p -> storingLuggageTime.put(p, RANDOM_STORING_LUGGAGE_TIME_SUPPLIER.getAsDouble()));

        // Then, check if another call must be done
        final long seatedPassengers = particles.stream()
                .filter(Particle::alreadySeated)
                .count();
        final int calledPassengers = calls * batch;
        if (seatedPassengers > calledPassengers - batch / 2) {
            particles.forEach(Particle::updateCall);
            calls++;
        }

        // Check if are all seated
        if (!areAllSeated) {
            // Only do this if not modified yet. Once modified, avoid it.
            this.areAllSeated = particles.stream().allMatch(Particle::alreadySeated);
        }

        // Finally, update the time
        this.actualTime += timeStep;
        if (areAllSeated) {
            discountTime += timeStep;
        }
    }

    @Override
    public void restart() {
        if (clean) {
            return;
        }
        this.particles.clear();
        this.particles.addAll(componentsProvider.createParticles());
        this.actualTime = 0;
        this.storingLuggageTime.clear();
        this.clean = true;
    }

    @Override
    public BoardingSceneState outputState() {
        return new BoardingSceneState(this);
    }

    // ================================================================================================================
    // Helpers
    // ================================================================================================================


    /**
     * The state of a {@link BoardingScene}.
     */
    public static final class BoardingSceneState implements State {

        /**
         * The states of the {@link Wall}s that make up the obstacles in the scene.
         */
        private final List<Wall.WallState> obstaclesStates;

        /**
         * The {@link Particle}s in this room.
         */
        private final List<Particle.ParticleState> particleStates;

        /**
         * The moment to which this state belongs to.
         */
        private final double actualTime;

        /**
         * The time step used in the simulation.
         */
        private final double timeStep;

        /**
         * Constructor.
         *
         * @param boardingScene The {@link BoardingScene} owning this state.
         */
        /* package */ BoardingSceneState(BoardingScene boardingScene) {
            final List<Wall.WallState> obstacleStatesAux = boardingScene.getObstacles().stream()
                    .map(Wall::outputState)
                    .collect(Collectors.toList());
            this.obstaclesStates = Collections.unmodifiableList(obstacleStatesAux);
            final List<Particle.ParticleState> particleStatesAux = boardingScene.getParticles().stream()
                    .map(Particle.ParticleState::new)
                    .collect(Collectors.toList());
            this.particleStates = Collections.unmodifiableList(particleStatesAux); // Make it unmodifiable.
            this.actualTime = boardingScene.getActualTime();
            this.timeStep = boardingScene.getTimeStep();
        }

        /**
         * @return
         */
        public List<Wall.WallState> getObstaclesStates() {
            return obstaclesStates;
        }

        /**
         * @return The {@link Particle}s in this room.
         */
        public List<Particle.ParticleState> getParticleStates() {
            return particleStates;
        }

        /**
         * @return The moment to which this state belongs to.
         */
        public double getActualTime() {
            return actualTime;
        }

        /**
         * @return The time step used in the simulation.
         */
        public double getTimeStep() {
            return timeStep;
        }
    }
}