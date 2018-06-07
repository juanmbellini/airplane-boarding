package ar.edu.itba.ss.exit_room.models;

import ar.edu.itba.ss.g7.engine.simulation.State;
import ar.edu.itba.ss.g7.engine.simulation.StateHolder;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Random;


/**
 * Represents a particle in the system.
 */
public class Particle implements StateHolder<Particle.ParticleState>, Obstacle {

    private static final Vector2D GOAL = new Vector2D(0, 0); // TODO: Move to Room

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
     * Point to which this particle will try to move.
     */
    private Vector2D goal = GOAL; // TODO: define a class for this? something similar as a particle (position and radius?

    /**
     * Flag indicating whether this particle reached the goal
     * (i.e will be used to calculate a new goal in order to go away from the initial one).
     */
    private boolean reachedGoal;

    /**
     * Auxiliary variable that holds the new radius
     * (i.e is saved here in order to keep the original value not being modified before movement, as is used by others).
     */
    private Double newRadius;

    /**
     * Auxiliary variable that holds the new velocity
     * (i.e is saved here in order to keep the original value not being modified before movement, as is used by others).
     */
    private Vector2D newVelocity;

    /**
     * Flag indicating if this particle can move (i.e was prepared with the {@link #prepareMove(List, double)} method).
     */
    private boolean canMove;

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
        this.reachedGoal = false;
        this.canMove = false;
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
     * Prepares this particle to be moved.
     *
     * @param inContact The {@link Obstacle}s that are in contact with this particle.
     * @param timeStep  The time step.
     */
    public void prepareMove(final List<Obstacle> inContact, final double timeStep) {
        if (inContact.isEmpty()) {
            // Particle is not in contact with any obstacle (i.e does not overlap with anything)
            // Radius
            final double auxRadius = radius + maxRadius / (tao / timeStep);
            this.newRadius = auxRadius > maxRadius ? maxRadius : auxRadius;
            // Velocity
            final double speed = maxVelocityModule * Math.pow((newRadius - minRadius) / (maxRadius - minRadius), beta);
            final Vector2D goalDirection = goal.subtract(this.position).normalize();
            this.newVelocity = goalDirection.scalarMultiply(speed);
        } else {
            // Particle is in contact with those obstacles in the list (i.e it overlaps)
            // Radius
            this.newRadius = minRadius;
            // Velocity
            final Vector2D escapeDirection = inContact.stream()
                    .map(obstacle -> obstacle.getEscapeDirection(this))
                    .reduce(Vector2D.ZERO, Vector2D::add)
                    .normalize();
            this.newVelocity = escapeDirection.scalarMultiply(maxVelocityModule); // Escape velocity is the same as max
        }
        this.canMove = true;
    }

    /**
     * Moves this particles according to the preparation it got.
     *
     * @param timeStep The time step.
     */
    public void move(final double timeStep) {
        Assert.state(canMove, "The particle cannot move because it was not prepared yet. " +
                "Call the prepareMove(List, double) first before each move.");
        this.radius = newRadius;
        this.velocity = newVelocity;
        this.position = position.add(getVelocity().scalarMultiply(timeStep));

        // TODO: improve this
        final Vector2D sub = this.position.subtract(GOAL);
        if (!reachedGoal && (this.position.getY() <= 0 || Math.abs(sub.getX()) < 0.5 && Math.abs(sub.getY()) < 0.5)) {
            reachedGoal = true;
            goal = new Vector2D(-10d + new Random().nextDouble() * 20d, -30 + new Random().nextDouble() * 10d);
        }

        this.canMove = false;
        this.newRadius = null;
        this.newVelocity = null;
    }

    // ================================================================================================================
    // Obstacle
    // ================================================================================================================

    @Override
    public boolean doOverlap(final Particle particle) {
        return doOverlap(particle.getPosition(), particle.getRadius());
    }

    @Override
    public Vector2D getEscapeDirection(final Particle particle) {
        Assert.state(doOverlap(particle),
                "Tried to calculate an escape direction with a particle that is not overlapping");
        return particle.getPosition().subtract(this.position).normalize();
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