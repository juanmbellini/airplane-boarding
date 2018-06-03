package ar.edu.itba.ss.exit_room.models;

import ar.edu.itba.ss.g7.engine.models.System;
import ar.edu.itba.ss.g7.engine.simulation.State;
import org.springframework.util.Assert;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents the silo to be simulated (i.e the {@link System} to be simulated).
 */
public class Silo implements System<Silo.SiloState> {

    // ================================================================================================================
    // Shape stuff
    // ================================================================================================================

    /**
     * The silo's top wall.
     */
    private final Wall topWall;

    /**
     * The silo's left wall.
     */
    private final Wall leftWall;

    /**
     * The silo's right wall.
     */
    private final Wall rightWall;

    /**
     * The silo's bottom wall that is left to the hole.
     */
    private final Wall leftBottomWall;

    /**
     * The silo's bottom wall that is right to the hole.
     */
    private final Wall rightBottomWall;

    /**
     * The silo's length (i.e used for re-spawning {@link Particle}s).
     */
    private final double siloLength;

    /**
     * The silo's width (i.e used for re-spawning {@link Particle}s).
     */
    private final double siloWidth;

    // ================================================================================================================
    // Particles
    // ================================================================================================================

    public final double minRadius;

    public final double maxRadius;

    /**
     * The {@link Particle}s in this silo.
     */
    private List<Particle> particles; //TODO: make final

    // ================================================================================================================
    // Integration
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
     * The total amount of {@link Particle}s that have left the silo.
     */
    private long outsideParticles;

    /**
     * The actual amount of new {@link Particle}s outside the silo.
     */
    private long newOutside;

    // ================================================================================================================
    // Others
    // ================================================================================================================

    /**
     * Indicates whether this silo is clean (i.e can be used to perform the simulation from the beginning).
     */
    private boolean clean;

    // ================================================================================================================
    // Constructor
    // ================================================================================================================

    /**
     * Constructor.
     *
     * @param length             The silo's width.
     * @param width              The silo's length.
     * @param hole               The silo's hole size.
     * @param minRadius          The min. radius of a {@link Particle}.
     * @param maxRadius          The max. radius of a {@link Particle}.
     * @param duration           The amount of time the simulation will last.
     */
    public Silo(final double length, final double width, final double hole, final double dt,
        final double minRadius, final double maxRadius, final double duration) {

        this.minRadius = minRadius;
        this.maxRadius = maxRadius;
        this.timeStep = dt;

        // Shape stuff
        this.leftBottomWall = Wall.getLeftBottomWall(width, hole);
        this.rightBottomWall = Wall.getRightBottomWall(width, hole);
        this.leftWall = Wall.getLeftWall(length);
        this.rightWall = Wall.getRightWall(length, width);
        this.topWall = Wall.getTopWall(length, width);
        this.siloLength = length;
        this.siloWidth = width;

        // TODO: load Particles

        // Integration
        this.duration = duration;
        this.actualTime = 0;

        final List<Wall> walls = Stream.of(leftBottomWall, rightBottomWall, leftWall, rightWall, topWall)
            .collect(Collectors.toList());

        this.outsideParticles = 0;
        this.newOutside = 0;

        this.clean = true;
    }

    // ================================================================================================================
    // Getters
    // ================================================================================================================

    /**
     * @return The silo's top wall.
     */
    public Wall getTopWall() {
        return topWall;
    }

    /**
     * @return The silo's left wall.
     */
    public Wall getLeftWall() {
        return leftWall;
    }

    /**
     * @return The silo's right wall.
     */
    public Wall getRightWall() {
        return rightWall;
    }

    /**
     * @return The silo's bottom wall that is left to the hole.
     */
    public Wall getLeftBottomWall() {
        return leftBottomWall;
    }

    /**
     * @return The silo's bottom wall that is right to the hole.
     */
    public Wall getRightBottomWall() {
        return rightBottomWall;
    }

    /**
     * @return The {@link Particle}s in this silo.
     */
    public List<Particle> getParticles() {
        return new LinkedList<>(particles);
    }

    /**
     * @return The actual amount of new {@link Particle}s outside the silo.
     */
    public long getNewOutside() {
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
    }

    @Override
    public void restart() {
        if (clean) {
            return;
        }

        //TODO: implement
    }

    @Override
    public SiloState outputState() {
        return new SiloState(this);
    }


    // ================================================================================================================
    // Helpers
    // ================================================================================================================

    /**
     * Validates the given shape values.
     *
     * @param length The silo's length to be validated.
     * @param width  The silo's width to be validated.
     * @param hole   The silo's hole size to be validated.
     * @throws IllegalArgumentException If any value is not valid.
     */
    private static void validateShape(final double length, final double width, final double hole)
        throws IllegalArgumentException {
        Assert.isTrue(length > width && width > hole, "The given values of shape are not valid");
    }

    /**
     * Represents the state of a {@link Silo}.
     */
    public static final class SiloState implements State {

        /**
         * The silo's top wall.
         */
        private final Wall.WallState topWall;

        /**
         * The silo's left wall.
         */
        private final Wall.WallState leftWall;

        /**
         * The silo's right wall.
         */
        private final Wall.WallState rightWall;

        /**
         * The silo's bottom wall that is left to the hole.
         */
        private final Wall.WallState leftBottomWall;

        /**
         * The silo's bottom wall that is right to the hole.
         */
        private final Wall.WallState rightBottomWall;

        /**
         * The {@link Particle}s in this silo.
         */
        private final List<Particle.ParticleState> particleStates;

        /**
         * The new amount of particle's that have left the silo in this state (i.e the flow).
         */
        private final long newOutside;


        /**
         * Constructor.
         *
         * @param silo The {@link Silo} owning this state.
         */
        public SiloState(final Silo silo) {
            this.topWall = silo.getTopWall().outputState();
            this.leftWall = silo.getLeftWall().outputState();
            this.rightWall = silo.getRightWall().outputState();
            this.leftBottomWall = silo.getLeftBottomWall().outputState();
            this.rightBottomWall = silo.getRightBottomWall().outputState();
            this.particleStates = silo.getParticles().stream()
                .map(Particle.ParticleState::new)
                .collect(Collectors.toList());
            this.newOutside = silo.getNewOutside();
        }

        /**
         * @return The silo's top wall.
         */
        public Wall.WallState getTopWall() {
            return topWall;
        }

        /**
         * @return The silo's left wall.
         */
        public Wall.WallState getLeftWall() {
            return leftWall;
        }

        /**
         * @return The silo's right wall.
         */
        public Wall.WallState getRightWall() {
            return rightWall;
        }

        /**
         * @return The silo's bottom wall that is left to the hole.
         */
        public Wall.WallState getLeftBottomWall() {
            return leftBottomWall;
        }

        /**
         * @return The silo's bottom wall that is right to the hole.
         */
        public Wall.WallState getRightBottomWall() {
            return rightBottomWall;
        }

        /**
         * @return The {@link Particle}s in this silo.
         */
        public List<Particle.ParticleState> getParticleStates() {
            return particleStates;
        }

        /**
         * @return The new amount of particle's that have left the silo in this state (i.e the flow).
         */
        public long getNewOutside() {
            return newOutside;
        }

    }
}