package ar.edu.itba.ss.airplane_boarding.models;

/**
 * An enum with several boarding strategies.
 */
public enum BoardingStrategy {
    /**
     * Strategy in which particles enter the airplane by row, from the back to the front.
     */
    BACK_TO_FRONT,
    /**
     * Strategy in which particles enter the airplane by column, from the outside to the inside.
     */
    OUTSIDE_IN
}
