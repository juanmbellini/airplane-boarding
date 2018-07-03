package ar.edu.itba.ss.airplane_boarding.models;

import ar.edu.itba.ss.g7.engine.simulation.State;
import ar.edu.itba.ss.g7.engine.simulation.StateHolder;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.springframework.util.Assert;

/**
 * Represents a wall in the system.
 */
public final class Wall implements StateHolder<Wall.WallState>, Obstacle {

    /**
     * The wall's initial position.
     */
    private final Vector2D initialPoint;

    /**
     * The wall's final position.
     */
    private final Vector2D finalPoint;


    /**
     * Constructor.
     *
     * @param initialPoint The wall's initial position.
     * @param finalPoint   The wall's final position.
     */
    public Wall(final Vector2D initialPoint, final Vector2D finalPoint) {
        this.initialPoint = initialPoint;
        this.finalPoint = finalPoint;
    }

    /**
     * @return The wall's initial position.
     */
    public Vector2D getInitialPoint() {
        return initialPoint;
    }

    /**
     * @return The wall's final position.
     */
    public Vector2D getFinalPoint() {
        return finalPoint;
    }

    @Override
    public WallState outputState() {
        return new WallState(this);
    }

    @Override
    public boolean doOverlap(Particle particle) {
        final Vector2D projection = this.getProjectionInWall(particle);
        final Vector2D directionVector = this.getDirectionVector();
        final double argCosine = projection.dotProduct(directionVector)
                / (projection.getNorm() * directionVector.getNorm());
        return !(projection.getNorm() > directionVector.getNorm()) // Projection is bigger than wall
                && !(argCosine == -1d)  // Projection is before wall (i.e points to the other side)
                && particle.getRadius() - projection.add(initialPoint).distance(particle.getPosition()) > 0;
    }

    @Override
    public Vector2D getEscapeDirection(Particle particle) {
        Assert.state(doOverlap(particle),
                "Tried to calculate an escape direction with a particle that is not overlapping");
        return particle.getPosition().subtract(this.getProjectionInWall(particle).add(initialPoint)).normalize();
    }


    /**
     * @return The direction vector of thw wall (goes from the initial point to the final point).
     * @see <a href="https://es.wikipedia.org/wiki/Vector_director">Vector Director</a>
     * @see <a href="https://en.wikipedia.org/wiki/Euclidean_vector">Euclidean Vector</a>
     */
    private Vector2D getDirectionVector() {
        return finalPoint.subtract(initialPoint);
    }

    /**
     * Returns the projection of the given {@code particle} in this wall.
     *
     * @param particle The {@link Particle} whose projection in this wall will be calculated.
     * @return The projection of the given {@code particle} in this wall.
     * @apiNote Note that if the projection vector (i.e the return value)
     * can have a bigger norm than the direction vector, or point against it.
     */
    private Vector2D getProjectionInWall(final Particle particle) {
        final Vector2D directionVector = this.getDirectionVector();
        final Vector2D fromInitial = particle.getPosition().subtract(this.getInitialPoint());
        return directionVector
                .scalarMultiply(fromInitial.dotProduct(directionVector))
                .scalarMultiply(1 / directionVector.getNormSq());
    }


    /**
     * Represents the state of a {@link Wall}.
     */
    public static final class WallState implements State {
        /**
         * The wall's initial state.
         */
        private final Vector2D initialPoint;
        /**
         * The wall's final state.
         */
        private final Vector2D finalPoint;

        /**
         * Constructor.
         *
         * @param wall The {@link Wall} owning this state.
         */
        /* package */ WallState(final Wall wall) {
            initialPoint = wall.initialPoint;
            finalPoint = wall.finalPoint;
        }

        /**
         * @return The wall's initial state.
         */
        public Vector2D getInitialPoint() {
            return initialPoint;
        }

        /**
         * @return The wall's final state.
         */
        public Vector2D getFinalPoint() {
            return finalPoint;
        }
    }
}