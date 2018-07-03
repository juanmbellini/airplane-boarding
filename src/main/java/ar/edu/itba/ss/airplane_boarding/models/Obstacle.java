package ar.edu.itba.ss.airplane_boarding.models;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 * Represents an obstacle for the pedestrians.
 */
public interface Obstacle {

    /**
     * Indicates whether the given {@code particle} overlaps with this obstacle.
     *
     * @param particle The {@link Particle} with which the overlap check will be done.
     * @return {@code true} if the {@code particle} overlaps, or {@code false} otherwise.
     */
    boolean doOverlap(final Particle particle);

    /**
     * Calculates the escape direction for the given {@code particle}, returning a unit {@link Vector2D}
     * (i.e with norm = 1).
     *
     * @param particle The {@link Particle} whose escape direction
     *                 when overlapping with this obstacle will be calculated.
     * @return The escape direction for the given {@code particle}.
     * @apiNote The returned {@link Vector2D} is a unit vector.
     */
    Vector2D getEscapeDirection(final Particle particle);
}
