package ar.edu.itba.ss.airplane_boarding.models;

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
     * Goal low 'x' component limit
     * (i.e used to validate that a particle has reached the goal if it already passed a limit).
     */
    private final Double xLowLimit;

    /**
     * Goal high 'x' component limit
     * (i.e used to validate that a particle has reached the goal if it already passed a limit).
     */
    private final Double xHighLimit;

    /**
     * Goal low 'y' component limit
     * (i.e used to validate that a particle has reached the goal if it already passed a limit).
     */
    private final Double yLowLimit;

    /**
     * Goal high 'y' component limit
     * (i.e used to validate that a particle has reached the goal if it already passed a limit).
     */
    private final Double yHighLimit;


    /**
     * Constructor.
     *
     * @param center     The center of this goal.
     * @param xMargin    A margin of error for the 'x' axis.
     * @param yMargin    A margin of error for the 'y' axis.
     * @param xLowLimit  Goal low 'x' component limit
     *                   (i.e used to validate that a particle has reached the goal if it already passed a limit).
     * @param xHighLimit Goal high 'x' component limit
     *                   (i.e used to validate that a particle has reached the goal if it already passed a limit).
     * @param yLowLimit  Goal low 'y' component limit
     *                   (i.e used to validate that a particle has reached the goal if it already passed a limit).
     * @param yHighLimit Goal high 'y' component limit
     *                   (i.e used to validate that a particle has reached the goal if it already passed a limit).
     */
    public Goal(final Vector2D center, final double xMargin, final double yMargin,
                final Double xLowLimit, final Double xHighLimit, final Double yLowLimit, final Double yHighLimit) {
        this.center = center;
        this.xMargin = xMargin;
        this.yMargin = yMargin;
        this.xLowLimit = xLowLimit;
        this.xHighLimit = xHighLimit;
        this.yLowLimit = yLowLimit;
        this.yHighLimit = yHighLimit;
    }


    /**
     * Indicates whether the given {@code particle} is near (with a given margin) this goal.
     *
     * @param particle The {@link Particle} to be checked.
     * @return {@code true} if the given {@code particle} is near this goal
     * (i.e in the center of this goal, with a given margin of error), or {@code false} otherwise.
     */
    public boolean reachedBy(final Particle particle) {
        final Vector2D position = particle.getPosition();
        final Vector2D difference = position.subtract(center);
        return (xLowLimit != null && position.getX() < xLowLimit)
                || (xHighLimit != null && position.getX() > xHighLimit)
                || (yLowLimit != null && position.getY() < yLowLimit)
                || (yHighLimit != null && position.getY() > yHighLimit)
                || Math.abs(difference.getX()) < xMargin && Math.abs(difference.getY()) < yMargin;

    }

    /**
     * @return The center of this goal.
     */
    public Vector2D getCenter() {
        return center;
    }
}
