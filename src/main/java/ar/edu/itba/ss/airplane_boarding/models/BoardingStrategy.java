package ar.edu.itba.ss.airplane_boarding.models;

/**
 * An enum with several boarding strategies.
 */
public enum BoardingStrategy {

    BACK_TO_FRONT_BY_ROW,
    FRONT_TO_BACK_BY_ROW,
    OUTSIDE_IN_BY_COLUMN,
    INSIDE_OUT_BY_COLUMN,
    RANDOM;
//    // TODO: maybe we should create an interface and subclasses
//    // TODO: because, for example, block boarding might receive any of the others?
}
