package ar.edu.itba.ss.exit_room.models;

import ar.edu.itba.ss.exit_room.utils.ComponentsProvider;
import ar.edu.itba.ss.g7.engine.models.System;
import ar.edu.itba.ss.g7.engine.simulation.State;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents the room to be simulated (i.e the {@link System} to be simulated).
 */
public class Room implements System<Room.RoomState> {

    // ================================================================================================================
    // Walls and particles
    // ================================================================================================================

    /**
     * The {@link Wall}s in this room.
     */
    private final List<Wall> walls;

    /**
     * The {@link Particle}s in this room.
     */
    private final List<Particle> particles; //TODO: make final

    // ================================================================================================================
    // Update stuff
    // ================================================================================================================

    /**
     * The time step.
     */
    private final double timeStep;

    /**
     * The simulation duration.
     */
    private final double duration;

    /**
     * The actual time.
     */
    private double actualTime;

    // ================================================================================================================
    // Physics
    // ================================================================================================================

    /**
     * The total amount of {@link Particle}s that have left the room.
     */
    private long outsideParticles;

    /**
     * The actual amount of new {@link Particle}s outside the room.
     */
    private long newOutside;

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
     * @param length    The room's width.
     * @param width     The room's length.
     * @param door      The room's door size.
     * @param minRadius The min. radius of a {@link Particle}.
     * @param duration  The amount of time the simulation will last.
     */
    public Room(final double length, final double width, final double door,
                final int maxAmountOfParticles,
                final double timeStep,
                final double minRadius, final double duration) {
        this.componentsProvider = new ComponentsProvider(length, width, door, minRadius, maxAmountOfParticles);
        this.walls = componentsProvider.buildWalls();
        this.particles = componentsProvider.createParticles();

        // Update stuff
        this.duration = duration;
        this.actualTime = 0;
        this.timeStep = timeStep;

        this.outsideParticles = 0;
        this.newOutside = 0;

        this.clean = true;
    }

    // ================================================================================================================
    // Getters
    // ================================================================================================================

    /**
     * @return The {@link Wall}s in this room.
     */
    /* package */ List<Wall> getWalls() {
        return new LinkedList<>(walls);
    }

    /**
     * @return The {@link Particle}s in this room.
     */
    /* package */ List<Particle> getParticles() {
        return new LinkedList<>(particles);
    }

    /**
     * @return The actual amount of new {@link Particle}s outside the room.
     */
    /* package */ long getNewOutside() {
        return newOutside;
    }

    /**
     * Indicates whether the simulation should stop.
     *
     * @return {@code true} if the simulation should stop, or {@code false} otherwise.
     */
    public boolean shouldStop() {
        return actualTime > duration;
    }


    // ================================================================================================================
    // Interface stuff
    // ================================================================================================================

    @Override
    public void update() {
        //TODO: implement

        this.actualTime += timeStep;
    }

    @Override
    public void restart() {
        if (clean) {
            return;
        }
        this.walls.clear();
        this.particles.clear();
        this.walls.addAll(componentsProvider.buildWalls());
        this.particles.addAll(componentsProvider.createParticles());
    }

    @Override
    public RoomState outputState() {
        return new RoomState(this);
    }


    // ================================================================================================================
    // Helpers
    // ================================================================================================================


    /**
     * Represents the state of a {@link Room}.
     */
    public static final class RoomState implements State {

        /**
         * The {@link Wall}s in this room.
         */
        private final List<Wall.WallState> wallStates;

        /**
         * The {@link Particle}s in this room.
         */
        private final List<Particle.ParticleState> particleStates;

        /**
         * The new amount of particle's that have left the room in this state (i.e the flow).
         */
        private final long newOutside;


        /**
         * Constructor.
         *
         * @param room The {@link Room} owning this state.
         */
        /* package */ RoomState(final Room room) {
            this.wallStates = room.getWalls().stream()
                    .map(Wall.WallState::new)
                    .collect(Collectors.toList());
            this.particleStates = room.getParticles().stream()
                    .map(Particle.ParticleState::new)
                    .collect(Collectors.toList());
            this.newOutside = room.getNewOutside();
        }

        /**
         * @return The {@link Wall}s in this room.
         */
        public List<Wall.WallState> getWallStates() {
            return wallStates;
        }

        /**
         * @return The {@link Particle}s in this room.
         */
        public List<Particle.ParticleState> getParticleStates() {
            return particleStates;
        }

        /**
         * @return The new amount of particle's that have left the room in this state (i.e the flow).
         */
        public long getNewOutside() {
            return newOutside;
        }
    }
}