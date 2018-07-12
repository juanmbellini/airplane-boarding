package ar.edu.itba.ss.airplane_boarding.models;

/**
 * An enum with several boarding strategies.
 */
public enum BoardingStrategy {
    /**
     * Strategy in which particles enter the airplane by row, from the back to the front.
     */
    BACK_TO_FRONT_BY_ROW,
    /**
     * Strategy in which particles enter the airplane by row, from the front to the back.
     */
    FRONT_TO_BACK_BY_ROW,
    /**
     * Strategy in which particles enter the airplane by column, from the outside to the inside.
     */
    OUTSIDE_IN_BY_COLUMN,
    /**
     * Strategy in which particles enter the airplane by column, from the inside to the outside.
     */
    INSIDE_OUT_BY_COLUMN,
    /**
     * Strategy in which particles enter the airplane in a random way.
     */
    RANDOM
}
