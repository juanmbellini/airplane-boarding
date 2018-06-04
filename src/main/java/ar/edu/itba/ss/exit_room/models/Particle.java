package ar.edu.itba.ss.exit_room.models;

import ar.edu.itba.ss.g7.engine.simulation.State;
import ar.edu.itba.ss.g7.engine.simulation.StateHolder;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.springframework.util.Assert;


/**
 * Represents a particle in the system.
 */
public class Particle implements StateHolder<Particle.ParticleState> {

    //TODO: move to system

    /**
     * The minimum radius a {@link Particle} can have
     */
    public static final double MIN_RADIUS = 0.1;

    /**
     * The maximum radius a {@link Particle} can have
     */
    public static final double MAX_RADIUS = 0.32;

    /**
     * Mean time a {@link Particle} needs to get to the minimum radius
     */
    public static final double TAO = 0.5;

    /**
     * Experimental constant that defines the linearity between velocity changes and blocks avoidance
     */
    public static final double BETA = 0.9;

    /**
     * The maximum velocity a {@link Particle} can have
     */
    public static final double MAX_VELOCITY = 1.55;

    // Until here.

    /**
     * The particle's radius.
     */
    private double radius;

    /**
     * The particle's position (represented as a 2D vector).
     */
    private Vector2D position;

    /**
     * The particle's velocity (represented as a 2D vector).
     */
    private Vector2D velocity;

    /**
     * Tells wherever or not the particle is overlapping another particle in this moment.
     */
    private Boolean isOverlapping;

    // ================================================================================================================
    // Constructor
    // ================================================================================================================

    /**
     * Constructor.
     *
     * @param radius   The particle's radius.
     * @param position The particle's position (represented as a 2D vector).
     * @param velocity The particle's velocity (represented as a 2D vector).
     */
    public Particle(final double radius, final Vector2D position, final Vector2D velocity) {
        validateRadius(radius);
        validateVector(position);
        validateVector(velocity);

        this.radius = radius;
        this.position = position;
        this.velocity = velocity;
    }

    // ================================================================================================================
    // Getters
    // ================================================================================================================

    /**
     * @return The particle's radius.
     */
    public double getRadius() {
        return radius;
    }

    /**
     * @return The particle's position (represented as a 2D vector).
     */
    public Vector2D getPosition() {
        return position;
    }

    /**
     * @return The particle's velocity (represented as a 2D vector).
     */
    public Vector2D getVelocity() {
        return velocity;
    }

    // ================================================================================================================
    // Update
    // ================================================================================================================

    /**
     * Updates the particle's position
     *
     * @param deltaT the elapsed time
     */
    public void updatePosition(final double deltaT) {
        position.add(getVelocity().scalarMultiply(deltaT));
    }

    /**
     * Updates the particle's radius
     *
     * @param deltaT the elapsed time
     */
    public void updateRadius(final double deltaT) {
        radius = isOverlapping ? MIN_RADIUS : (radius + MAX_RADIUS / (TAO / deltaT));

        if (radius > MAX_RADIUS) {
            radius = MAX_RADIUS;
        }
    }

    /**
     * Updates the particle's velocity
     *
     * @param deltaT the elapsed time
     * @param direction the direction to the goal
     */
    //TODO: el vector direction se calcula usando las direcciones de escape de las particulas (EQ 6 y 7)
    public void updateVelocity(final double deltaT, final Vector2D direction) {
        final double velocityModule = isOverlapping ? MAX_VELOCITY :
            (MAX_VELOCITY * Math.pow((radius - MIN_RADIUS)/(MAX_RADIUS - MIN_RADIUS) , BETA));

        velocity = direction.scalarMultiply(velocityModule);
    }

// ================================================================================================================
    // Others
    // ================================================================================================================

    /**
     * Updates particle overlapping state
     *
     * @param particle Other.
     * @return {@code true} if the new particle would overlap {@code this} particle, or {@code false} otherwise.
     */
    public boolean doOverlap(final Particle particle) {
        if (Boolean.TRUE.equals(isOverlapping)) return true;

        isOverlapping = doOverlap(particle.getPosition(), particle.getRadius());
        return isOverlapping;
    }

    /**
     * Checks if another particle can be created with the given {@code position} and {@code radius} arguments.
     *
     * @param position The position where the new particle will be created.
     * @param radius   The radius of the new particle.
     * @return {@code true} if the new particle would overlap {@code this} particle, or {@code false} otherwise.
     */
    public boolean doOverlap(final Vector2D position, final double radius) {
        return this.radius + radius - this.position.distance(position) > 0;
    }

    /**
     * Validates the given {@code radius} value.
     *
     * @param radius The radius value to be validated.
     * @throws IllegalArgumentException In case the given {@code radius} value is not value (i.e is not positive).
     */
    private static void validateRadius(final double radius) throws IllegalArgumentException {
        Assert.isTrue(radius > 0, "The radius must be positive");
    }

    /**
     * Validates the given {@code vector}.
     *
     * @param vector The {@link Vector2D} to be validated.
     * @throws IllegalArgumentException In case the given {@code vector} is not valid (i.e is {@code null}).
     */
    private static void validateVector(final Vector2D vector) throws IllegalArgumentException {
        Assert.notNull(vector, "The given vector is null");
    }


    @Override
    public ParticleState outputState() {
        return new ParticleState(this);
    }

    /**
     * Represents the state of a given particle.o
     */
    public static final class ParticleState implements State {

        /**
         * The {@link Particle}'s radius.
         */
        private final double radius;

        /**
         * The {@link Particle}'s position (represented as a 2D vector).
         */
        private final Vector2D position;

        /**
         * The {@link Particle}'s velocity (represented as a 2D vector).
         */
        private final Vector2D velocity;

        /**
         * Constructor.
         *
         * @param particle The {@link Particle}'s whose state will be represented.
         */
        /* package */ ParticleState(final Particle particle) {
            radius = particle.getRadius();
            position = particle.getPosition(); // The Vector2D class is unmodifiable.
            velocity = particle.getVelocity(); // The Vector2D class is unmodifiable.
        }

        /**
         * The {@link Particle}'s radius.
         */
        public double getRadius() {
            return radius;
        }

        /**
         * The {@link Particle}'s position (represented as a 2D vector).
         */
        public Vector2D getPosition() {
            return position;
        }

        /**
         * The {@link Particle}'s velocity (represented as a 2D vector).
         */
        public Vector2D getVelocity() {
            return velocity;
        }
    }
}