package ar.edu.itba.ss.exit_room.models;

import ar.edu.itba.ss.g7.engine.simulation.State;
import ar.edu.itba.ss.g7.engine.simulation.StateHolder;
import com.sun.istack.internal.Nullable;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.springframework.util.Assert;


/**
 * Represents a particle in the system.
 */
public class Particle implements StateHolder<Particle.ParticleState> {

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
    @Nullable
    private Boolean isOverlapping;

    // ================================================================================================================
    // Constructor
    // ================================================================================================================

    /**
     * Constructor.
     *
     * @param radius       The particle's radius.
     * @param position     The particle's position (represented as a 2D vector).
     * @param velocity     The particle's velocity (represented as a 2D vector).
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
    // Setters
    // ================================================================================================================

    public void update(final double dt) {
        Assert.state(isOverlapping != null, "We must check overlapping states before updating");

        if (isOverlapping) {
            //TODO: set radius to min radius
            //TODO: set speed to vd max
        } else {
            //TODO: update speeds module according to betha and the radius
            //TODO update radius
        }

        updatePosition(dt);

        //We clear the overlapping state
        isOverlapping = null;
    }

    private void updatePosition(final double dt) {
        position.add(getVelocity().scalarMultiply(dt));
    }

    // ================================================================================================================
    // Others
    // ================================================================================================================

    /**
     * Checks if another particle can be created with the given {@code position} and {@code radius} arguments.
     *
     * @param position The position where the new particle will be created.
     * @param radius   The radius of the new particle.
     * @return {@code true} if the new particle would overlap {@code this} particle, or {@code false} otherwise.
     */
    public boolean doOverlap(final Vector2D position, final double radius) {
        isOverlapping = this.radius + radius - this.position.distance(position) < 0;
        return isOverlapping;
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