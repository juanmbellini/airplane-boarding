package ar.edu.itba.ss.airplane_boarding.models;

import ar.edu.itba.ss.airplane_boarding.utils.ComponentsProvider;
import ar.edu.itba.ss.g7.engine.models.System;
import ar.edu.itba.ss.g7.engine.simulation.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents the boarding scene (i.e the {@link System} to be simulated).
 */
@Component
public class BoardingScene implements System<BoardingScene.BoardingSceneState> {

    // ================================================================================================================
    // Airplane and particles
    // ================================================================================================================

    /**
     * The {@link Airplane} to be boarded.
     */
    private final Airplane airplane;

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


    // ================================================================================================================
    // Others
    // ================================================================================================================

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

        // Airplane and particles
        this.airplane = componentsProvider.getAirplane();
        this.particles = componentsProvider.createParticles();

        // Update stuff
        this.actualTime = 0;
        this.timeStep = timeStep;

        // Others
        this.clean = true;
    }

    // ================================================================================================================
    // Getters
    // ================================================================================================================

    /**
     * @return The {@link Airplane} to be boarded.
     */
    /* package */ Airplane getAirplane() {
        return airplane;
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
        return actualTime > 120;
//        return true; // TODO: set a stop condition
    }


    // ================================================================================================================
    // Interface stuff
    // ================================================================================================================

    @Override
    public void update() {
        this.clean = false;
        // First, prepare the particles in order to be moved
        for (Particle particle : particles) {
            final List<Obstacle> inContact = Stream
                    .concat(
                            airplane.getObstacles().stream(),
                            particles.stream().filter(otherParticle -> otherParticle != particle)
                    )
                    .filter(obstacle -> obstacle.doOverlap(particle))
                    .collect(Collectors.toList());
            particle.prepareMove(inContact, timeStep);
        }
        // Then, move
        particles.forEach(particle -> particle.move(timeStep));
        this.actualTime += timeStep;
    }

    @Override
    public void restart() {
        if (clean) {
            return;
        }
        this.particles.clear();
        this.particles.addAll(componentsProvider.createParticles());
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
         * The state of the {@link Airplane} to be boarded.
         */
        private final Airplane.AirplaneState airplaneState;

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
            this.airplaneState = boardingScene.getAirplane().outputState();
            final List<Particle.ParticleState> particleStatesAux = boardingScene.getParticles().stream()
                    .map(Particle.ParticleState::new)
                    .collect(Collectors.toList());
            this.particleStates = Collections.unmodifiableList(particleStatesAux); // Make it unmodifiable.
            this.actualTime = boardingScene.getActualTime();
            this.timeStep = boardingScene.getTimeStep();
        }

        /**
         * @return The state of the {@link Airplane} to be boarded.
         */
        public Airplane.AirplaneState getAirplaneState() {
            return airplaneState;
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