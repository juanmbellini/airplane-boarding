package ar.edu.itba.ss.exit_room.models;

import ar.edu.itba.ss.g7.engine.simulation.State;
import ar.edu.itba.ss.g7.engine.simulation.StateHolder;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 * Represents a wall in the system.
 */
public final class Wall implements StateHolder<Wall.WallState> {

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


    /**
     * @return The direction vector of thw wall (goes from the initial point to the final point).
     * @see <a href="https://es.wikipedia.org/wiki/Vector_director">Vector Director</a>
     * @see <a href="https://en.wikipedia.org/wiki/Euclidean_vector">Euclidean Vector</a>
     */
    public Vector2D getDirectionVector() {
        return finalPoint.subtract(initialPoint);
    }


    @Override
    public WallState outputState() {
        return new WallState(this);
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