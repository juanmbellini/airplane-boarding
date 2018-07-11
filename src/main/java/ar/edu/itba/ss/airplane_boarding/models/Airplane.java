package ar.edu.itba.ss.airplane_boarding.models;

import ar.edu.itba.ss.g7.engine.simulation.State;
import ar.edu.itba.ss.g7.engine.simulation.StateHolder;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * A class that abstracts the geometry of an airplane.
 */
public class Airplane implements StateHolder<Airplane.AirplaneState> {

    /**
     * The obstacles (i.e {@link Wall}s) that make up this airplane (external walls and seats).
     */
    private final List<Wall> obstacles;

    /**
     * An {@link AirplaneState} store because it never changes, so it can be reused.
     */
    private final AirplaneState onlyState;

    /**
     * Constructor.
     *
     * @param externalWalls The airplane's external {@link Wall}s.
     * @param seats         The airplane's seats (represented as {@link Wall}s, being each of them a semi-row seat).
     */
    private Airplane(List<Wall> externalWalls, List<Wall> seats) {
        final List<Wall> obstacles = new LinkedList<>();
        obstacles.addAll(externalWalls);
        obstacles.addAll(seats);
        this.obstacles = Collections.unmodifiableList(obstacles);
        this.onlyState = new AirplaneState(this);
    }

    public List<Wall> getObstacles() {
        return obstacles; // This can be returned as is because it is unmodifiable.
    }

    /**
     * Creates an {@link Airplane} instance according to the given specifications.
     *
     * @param amountOfSeatRows     Indicates how many rows of seats the built {@link Airplane} will have.
     * @param amountOfSeatsPerSide Indicates how many seats there
     *                             between the central hall and the airplane side wall for each row.
     * @param centralHallWidth     The width of the central hall.
     * @param frontHallLength      The length of the front hall (i.e space between the first seat and the front "wall").
     * @param seatWidth            The width each seat has.
     * @param seatSeparation       The separation between each row of seats.
     * @param doorLength           The length of the airplane door.
     * @return The built airplane.
     * @implNote This method assumes that the origin is set with its 'x' component in the airplane's central hall axis,
     * and its 'y' component in the airplane's bottom wall.
     */
    public static Airplane buildFromSpecifications(final int amountOfSeatRows, final int amountOfSeatsPerSide,
                                                   final double centralHallWidth, final double frontHallLength,
                                                   final double seatWidth, final double seatSeparation,
                                                   final double doorLength) {

        // Reused calculations
        final double seatsWidth = seatWidth * amountOfSeatsPerSide;
        final double seatsLength = seatSeparation * amountOfSeatRows;
        final double airplaneWidth = 2 * seatsWidth + centralHallWidth;
        final double airplaneLength = seatsLength + frontHallLength;
        final double leftAxis = -airplaneWidth / 2;
        final double rightAxis = airplaneWidth / 2;
        final double bottomAxis = 0d;

        // External walls
        final Wall bottomWall = new Wall(new Vector2D(leftAxis, bottomAxis), new Vector2D(rightAxis, bottomAxis));
        final Wall upperWall = new Wall(new Vector2D(leftAxis, airplaneLength), new Vector2D(rightAxis, airplaneLength));
        final Wall leftWall = new Wall(new Vector2D(leftAxis, bottomAxis), new Vector2D(leftAxis, airplaneLength));
        final Wall rightWall = new Wall(new Vector2D(rightAxis, doorLength), new Vector2D(rightAxis, airplaneLength));
        final Wall leftBottomWall = new Wall(bottomWall.getInitialPoint(), new Vector2D(leftAxis, -5));
        final Wall rightBottomWall = new Wall(bottomWall.getFinalPoint(), new Vector2D(rightAxis, -5));
        final Wall leftNose = new Wall(leftBottomWall.getFinalPoint(), new Vector2D(0, -7));
        final Wall rightNose = new Wall(rightBottomWall.getFinalPoint(), new Vector2D(0, -7));

        final List<Wall> externalWalls = Arrays.asList(bottomWall, upperWall, leftWall, rightWall,
                leftBottomWall, rightBottomWall, leftNose, rightNose);

        // Seats
        final double leftSeatsFinalX = leftAxis + seatsWidth;
        final double rightSeatsInitialX = rightAxis - seatsWidth;
        final Stream<Wall> leftSeatsStream = IntStream.range(0, amountOfSeatRows)
                .mapToDouble(i -> frontHallLength + seatSeparation * (i + 1))
                .mapToObj(l -> new Wall(new Vector2D(leftAxis, l), new Vector2D(leftSeatsFinalX, l)));
        final Stream<Wall> rightSeatsStream = IntStream.range(0, amountOfSeatRows)
                .mapToDouble(i -> frontHallLength + seatSeparation * (i + 1))
                .mapToObj(l -> new Wall(new Vector2D(rightSeatsInitialX, l), new Vector2D(rightAxis, l)));
        final List<Wall> seats = Stream.concat(leftSeatsStream, rightSeatsStream)
                .collect(Collectors.toList());

        // Airplane construction
        return new Airplane(externalWalls, seats);
    }

    @Override
    public AirplaneState outputState() {
        return onlyState;
    }

    /**
     * Represents the state of an {@link Airplane}.
     */
    public final static class AirplaneState implements State {

        /**
         * The states of the {@link Wall}s that make up this obstacles of the airplane.
         */
        private final List<Wall.WallState> airplaneObstacles;

        /**
         * Constructor.
         *
         * @param airplane The {@link Airplane} owning the state.
         */
        /* package */ AirplaneState(Airplane airplane) {
            final List<Wall.WallState> wallStates = airplane.getObstacles().stream()
                    .map(Wall::outputState)
                    .collect(Collectors.toList());
            this.airplaneObstacles = Collections.unmodifiableList(wallStates); // Make it unmodifiable.
        }

        /**
         * @return The states of the {@link Wall}s that make up this obstacles of the airplane.
         */
        public List<Wall.WallState> getAirplaneObstacles() {
            return airplaneObstacles;
        }
    }
}
