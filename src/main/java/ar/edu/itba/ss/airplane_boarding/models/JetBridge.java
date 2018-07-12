package ar.edu.itba.ss.airplane_boarding.models;

import ar.edu.itba.ss.g7.engine.simulation.State;
import ar.edu.itba.ss.g7.engine.simulation.StateHolder;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class that abstracts the geometry of the component through which passengers enter an {@link Airplane}.
 */
public class JetBridge implements StateHolder<JetBridge.JetBridgeState> {

    /**
     * The obstacles (i.e {@link Wall}s) that make up this jet bridge.
     */
    private final List<Wall> obstacles;

    /**
     * A {@link JetBridgeState} store because it never changes, so it can be reused.
     */
    private final JetBridgeState onlyState;

    /**
     * Constructor.
     *
     * @param obstacles The obstacles (i.e {@link Wall}s) that make up this jet bridge.
     */
    private JetBridge(final List<Wall> obstacles) {
        this.obstacles = Collections.unmodifiableList(obstacles);
        this.onlyState = new JetBridgeState(this);
    }

    /**
     * @return The obstacles (i.e {@link Wall}s) that make up this jet bridge.
     */
    public List<Wall> getObstacles() {
        return obstacles; // This can be returned as is because it is unmodifiable.
    }

    @Override
    public JetBridgeState outputState() {
        return onlyState;
    }

    /**
     * Builds a jet bridge according to the given specifications.
     *
     * @param width         The width of the jet bridge.
     * @param length        The length of the jet bridge.
     * @param airplaneWidth The width of the airplane (used to know where the jet bridge should be placed).
     * @return The built jet bridge.
     * @implNote This method assumes that the origin is set with its 'x' component in the airplane's central hall axis,
     * and its 'y' component in the airplane's bottom wall.
     */
    public static JetBridge buildFromSpecifications(final double width, final double length,
                                                    final double airplaneWidth) {
        final double startingX = airplaneWidth / 2;
        // SuspiciousNameCombination are suppressed because the jet bridge is disposed along the 'x' axis
        @SuppressWarnings("SuspiciousNameCombination") final Wall bottomWall =
                new Wall(new Vector2D(startingX, 0), new Vector2D(startingX + length, 0));
        @SuppressWarnings("SuspiciousNameCombination") final Wall upperWall =
                new Wall(new Vector2D(startingX, width), new Vector2D(startingX + length, width));
        return new JetBridge(Arrays.asList(bottomWall, upperWall));
    }

    /**
     * THe state of a jet bridge.
     */
    public final static class JetBridgeState implements State {

        /**
         * The states of the {@link Wall}s that make up the obstacles of the jet bridge.
         */
        private final List<Wall.WallState> jetBridgeObstacles;

        /**
         * Constructor.
         *
         * @param jetBridge The {@link JetBridge} owning this state.
         */
        /* package */ JetBridgeState(JetBridge jetBridge) {
            final List<Wall.WallState> wallStates = jetBridge.getObstacles().stream()
                    .map(Wall::outputState)
                    .collect(Collectors.toList());
            this.jetBridgeObstacles = Collections.unmodifiableList(wallStates);  // Make it unmodifiable.
        }

        /**
         * @return The states of the {@link Wall}s that make up the obstacles of the jet bridge.
         */
        public List<Wall.WallState> getJetBridgeObstacles() {
            return jetBridgeObstacles;
        }
    }

}
