package ar.edu.itba.ss.exit_room.models;

import ar.edu.itba.ss.g7.engine.simulation.State;
import ar.edu.itba.ss.g7.engine.simulation.StateHolder;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.springframework.util.Assert;


/**
 * Represents a particle in the system.
 */
public class Particle implements StateHolder<Particle.ParticleState> {

    // ================================================================================================================
    // Model state
    // ================================================================================================================

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


    // ================================================================================================================
    // Update stuff
    // ================================================================================================================

    /**
     * Tells wherever or not the particle is overlapping another particle in this moment.
     */
    private Boolean isOverlapping;

    /**
     * The min. radius this particle can have.
     */
    private final double minRadius;

    /**
     * The max. radius this particle can have.
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


    // ================================================================================================================
    // Constructor
    // ================================================================================================================

    /**
     * Constructor.
     *
     * @param radius            The particle's radius.
     * @param position          The particle's position (represented as a 2D vector).
     * @param velocity          The particle's velocity (represented as a 2D vector).
     * @param minRadius         The min. radius this particle can have.
     * @param maxRadius         The max. radius this particle can have.
     * @param tao               Mean time a particle needs to get to the minimum radius.
     * @param beta              Experimental constant that defines the linearity
     *                          between speed changes and blocks avoidance.
     * @param maxVelocityModule The max. speed a particle can reach.
     */
    public Particle(final double radius, final Vector2D position, final Vector2D velocity,
                    final double minRadius, final double maxRadius,
                    double tao, double beta, double maxVelocityModule) {

        // First, validate
        validateRadius(radius, minRadius, maxRadius);
        validateVector(position);
        validateVector(velocity);
        validateTao(tao);
        validateBeta(beta);
        validateMaxVelocityModule(maxVelocityModule);

        // Then, set
        this.radius = radius;
        this.position = position;
        this.velocity = velocity;
        this.minRadius = minRadius;
        this.maxRadius = maxRadius;
        this.tao = tao;
        this.beta = beta;
        this.maxVelocityModule = maxVelocityModule;
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
        radius = isOverlapping ? minRadius : (radius + maxRadius / (tao / deltaT));
        if (radius > maxRadius) {
            radius = maxRadius;
        }
    }

    /**
     * Updates the particle's velocity
     *
     * @param deltaT    the elapsed time
     * @param direction the direction to the goal
     */
    //TODO: el vector direction se calcula usando las direcciones de escape de las particulas (EQ 6 y 7)
    public void updateVelocity(final double deltaT, final Vector2D direction) {
        final double velocityModule = isOverlapping ? maxVelocityModule :
                (maxVelocityModule * Math.pow((radius - minRadius) / (maxRadius - minRadius), beta));

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
        if (Boolean.TRUE.equals(isOverlapping)) {
            return true;
        }
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
    private static void validateRadius(final double radius, final double minRadius, final double maxRadius)
            throws IllegalArgumentException {
        Assert.isTrue(radius > 0 && minRadius > 0 && maxRadius > 0, "The radius must be positive");
        Assert.isTrue(maxRadius >= radius && radius >= minRadius,
                "The radius must have a value between minRadius and maxRadius");
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

    /**
     * Validates the given {@code tao} value.
     *
     * @param tao The tao value to be validated.
     * @throws IllegalArgumentException In case the given {@code tao} value is not valid.
     */
    private static void validateTao(final double tao) throws IllegalArgumentException {
        // TODO: implement
    }

    /**
     * Validates the given {@code beta} value.
     *
     * @param beta The beta value to be validated.
     * @throws IllegalArgumentException In case the given {@code beta} value is not valid.
     */
    private static void validateBeta(final double beta) throws IllegalArgumentException {
        // TODO: implement
    }

    /**
     * Validates the given {@code maxVelocityModule}.
     *
     * @param maxVelocityModule The max. velocity module value to be validated.
     * @throws IllegalArgumentException In case the given {@code maxVelocityModule} is not valid.
     */
    private static void validateMaxVelocityModule(final double maxVelocityModule) throws IllegalArgumentException {
        Assert.isTrue(maxVelocityModule > 0, "The max. velocity module must be positive.");
        // TODO: more validations?
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