package ar.edu.itba.ss.exit_room.models;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 * Class representing a goal.
 */
public class Goal {

    /**
     * The center of this goal.
     */
    private final Vector2D center;

    /**
     * A margin of error for the 'x' axis.
     */
    private final double xMargin;

    /**
     * A margin of error for the 'y' axis.
     */
    private final double yMargin;


    /**
     * Constructor.
     *
     * @param center  The center of this goal.
     * @param xMargin A margin of error for the 'x' axis.
     * @param yMargin A margin of error for the 'y' axis.
     */
    public Goal(Vector2D center, double xMargin, double yMargin) {
        this.center = center;
        this.xMargin = xMargin;
        this.yMargin = yMargin;
    }


    /**
     * Indicates whether the given {@code particle} is near (with a given margin) this goal.
     *
     * @param particle The {@link Particle} to be checked.
     * @return {@code true} if the given {@code particle} is near this goal
     * (i.e in the center of this goal, with a given margin of error), or {@code false} otherwise.
     */
    public boolean isNear(final Particle particle) {
        final Vector2D difference = particle.getPosition().subtract(center);
        return Math.abs(difference.getX()) < xMargin && Math.abs(difference.getY()) < yMargin;
    }

    /**
     * @return The center of this goal.
     */
    public Vector2D getCenter() {
        return center;
    }
}
