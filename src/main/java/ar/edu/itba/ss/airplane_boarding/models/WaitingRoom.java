package ar.edu.itba.ss.airplane_boarding.models;

import ar.edu.itba.ss.g7.engine.simulation.State;
import ar.edu.itba.ss.g7.engine.simulation.StateHolder;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class that abstracts the geometry of the component through which passengers wait to be called..
 */
public class WaitingRoom implements StateHolder<WaitingRoom.WaitingRoomState> {

    /**
     * The obstacles (i.e {@link Wall}s) that make up this waiting room.
     */
    private final List<Wall> obstacles;

    /**
     * A {@link WaitingRoomState} store because it never changes, so it can be reused.
     */
    private final WaitingRoomState onlyState;

    /**
     * Constructor.
     *
     * @param obstacles The obstacles (i.e {@link Wall}s) that make up this waiting room.
     */
    private WaitingRoom(final List<Wall> obstacles) {
        this.obstacles = Collections.unmodifiableList(obstacles);
        this.onlyState = new WaitingRoomState(this);
    }

    /**
     * @return The obstacles (i.e {@link Wall}s) that make up this waiting room.
     */
    public List<Wall> getObstacles() {
        return obstacles; // This can be returned as is because it is unmodifiable.
    }

    @Override
    public WaitingRoomState outputState() {
        return onlyState;
    }

    /**
     * Builds a waiting room according to the given specifications.
     *
     * @param width         The width of the waiting room.
     * @param length        The length of the waiting room.
     * @param airplaneWidth The width of the airplane (used to know where the waiting room should be placed).
     * @return The built waiting room.
     * @implNote This method assumes that the origin is set with its 'x' component in the airplane's central hall axis,
     * and its 'y' component in the airplane's bottom wall.
     */
    public static WaitingRoom buildFromSpecifications(final double width, final double length,
                                                      final double airplaneWidth,
                                                      final double jetBridgeWidth, final double jetBridgeLength) {
        final double startingX = airplaneWidth / 2 + jetBridgeLength;
        final Wall bottomWall = new Wall(new Vector2D(startingX, 0), new Vector2D(startingX + width, 0));
        final Wall upperWall = new Wall(new Vector2D(startingX, length), new Vector2D(startingX + width, length));
        // The SuspiciousNameCombination is suppressed because the jetBridgeWidth is the "door" of the waiting room
        // which is in the 'y' axis.
        @SuppressWarnings("SuspiciousNameCombination") final Wall leftWall =
                new Wall(new Vector2D(startingX, jetBridgeWidth), upperWall.getInitialPoint());
        final Wall rightWall = new Wall(bottomWall.getFinalPoint(), upperWall.getFinalPoint());
        return new WaitingRoom(Arrays.asList(bottomWall, upperWall, leftWall, rightWall));
    }

    /**
     * THe state of a waiting room.
     */
    public final static class WaitingRoomState implements State {

        /**
         * The states of the {@link Wall}s that make up the obstacles of the waiting room.
         */
        private final List<Wall.WallState> waitingRoomObstacles;

        /**
         * Constructor.
         *
         * @param jetBridge The {@link WaitingRoom} owning this state.
         */
        /* package */ WaitingRoomState(WaitingRoom jetBridge) {
            final List<Wall.WallState> wallStates = jetBridge.getObstacles().stream()
                    .map(Wall::outputState)
                    .collect(Collectors.toList());
            this.waitingRoomObstacles = Collections.unmodifiableList(wallStates);  // Make it unmodifiable.
        }

        /**
         * @return The states of the {@link Wall}s that make up the obstacles of the waiting room.
         */
        public List<Wall.WallState> getWaitingRoomObstacles() {
            return waitingRoomObstacles;
        }
    }

}
