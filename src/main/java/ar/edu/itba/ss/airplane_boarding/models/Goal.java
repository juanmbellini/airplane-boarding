package ar.edu.itba.ss.airplane_boarding.models;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.springframework.util.Assert;

import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Class representing a goal.
 * A goal is an abstraction of a list of positions to which a particle must go.
 *
 * @implNote This class assumes that the origin is set with its 'x' component in the airplane's central hall axis,
 * and its 'y' component in the airplane's bottom wall.
 */
public class Goal {

    /**
     * The {@link GoalStateMachine} that is in charge of managing the logic of this goal.
     */
    private final GoalStateMachine stateMachine;

    /**
     * Constructor.
     *
     * @param frontHallLength     The airplane's front hall length.
     * @param centralHallWidth    The airplane's central hall width.
     * @param doorLength          The door's length.
     * @param seatWidth           The seats width.
     * @param seatSeparation      The sets separation.
     * @param targetRow           The row in which the final goal is located (0-indexed).
     * @param targetColumn        The column in which the final goal is located (0-indexed).
     *                            Note that column numbers are counted from the center to the outside.
     * @param targetSide          The side of the airplane in which the final goal is located (i.e LEFT or RIGHT).
     * @param jetBridgeWidth      The width of the jet bridge (seen in an x-y plane it would be the length).
     * @param particleXSeparation The separation between particles when they are initialized
     *                            (used to generate the region where they must go
     *                            when approaching to the jet bridge).
     * @param margin              A margin to be used when calculating differences.
     */
    public Goal(final double frontHallLength, final double centralHallWidth, final double doorLength,
                final double seatWidth, final double seatSeparation,
                final int targetRow, final int targetColumn, final AirplaneSide targetSide,
                final double jetBridgeWidth, final double particleXSeparation,
                final double margin) {
        this.stateMachine = new GoalStateMachine(frontHallLength, centralHallWidth, doorLength,
                seatWidth, seatSeparation, targetRow, targetColumn, targetSide,
                jetBridgeWidth, particleXSeparation, margin);
    }

    /**
     * Initializes this goal.
     *
     * @param particle The {@link Particle} that will use this goal instance.
     */
    public void initialize(final Particle particle) {
        this.stateMachine.initialize(particle);
    }

    /**
     * Calculates (using some randomness) the position to which the owner {@link Particle} of this goal must go.
     *
     * @return The position to which the owner {@link Particle} of this goal must go.
     */
    public Optional<Vector2D> getTarget() {
        return Optional.ofNullable(stateMachine.getNextPosition());
    }


    /**
     * Notify to this goal that the given {@code particle} hsa moved towards it.
     *
     * @apiNote This method should only be called by the {@link Particle} that owns this goal.
     */
    public void notifyMove() {
        this.stateMachine.notifyMove();
    }


    /**
     * Indicates that the target being returned is the last one.
     *
     * @return {@code true} if the goal is returning the last target, or {@code false} otherwise.
     */
    public boolean isTheLastTarget() {
        return this.stateMachine.isLastMovingTarget();
    }

    public boolean insideAirplane() {
        return this.stateMachine.isInsideAirplane();
    }

    /**
     * Indicates that there are no more targets.
     *
     * @return {@code true} if there are no more targets, or {@code false} otherwise.
     */
    public boolean noMoreTargets() {
        return this.stateMachine.noMoreTargets();
    }


    /**
     * An enum to indicate whether the final goal is in the left or right side of the airplane.
     */
    public enum AirplaneSide {
        /**
         * Indicates that the goal is in the left side of the airplane.
         */
        LEFT(-1),
        /**
         * Indicates that the goal is in the right side of the airplane.
         */
        RIGHT(1);

        /**
         * Indicates the sign of the side (i.e the direction).
         */
        private final int sign;

        /**
         * @param sign Indicates the sign of the side (i.e the direction).
         */
        AirplaneSide(int sign) {
            Assert.isTrue(sign == 1 || sign == -1, "The sign should be 1 or -1.");
            this.sign = sign;
        }

        /**
         * @return The sign of the side (i.e the direction).
         */
        private int getSign() {
            return sign;
        }
    }

    /**
     * Represents the state machine a goal has in order to set the next position.
     */
    private final static class GoalStateMachine {

        /**
         * The particle that will use this state machine.
         */
        private Particle particle;

        /**
         * The airplane's front hall length.
         */
        private final double frontHallLength;
        /**
         * The airplane's central hall width.
         */
        private final double centralHallWidth;
        /**
         * The door's length.
         */
        private final double doorLength;
        /**
         * The seats width.
         */
        private final double seatWidth;
        /**
         * The sets separation.
         */
        private final double seatSeparation;
        /**
         * The row in which the final goal is located.
         */
        private final int targetRow;
        /**
         * The column in which the final goal is located.
         * Note that column numbers are counted from the center to the outside.
         */
        private final int targetColumn;
        /**
         * The side of the airplane in which the final goal is located.
         */
        private final AirplaneSide targetSide;
        /**
         * The width of the jet bridge (seen in an x-y plane it would be the length).
         */
        private final double jetBridgeWidth;
        /**
         * The separation between particles when they are initialized
         * (used to generate the region where they must go when approaching to the jet bridge).
         */
        private final double particleXSeparation;
        /**
         * A margin to be used when calculating differences.
         */
        private final double margin;
        /**
         * The actual state of this state machine.
         */
        private GoalState actualState;


        /**
         * @param frontHallLength     The airplane's front hall length.
         * @param centralHallWidth    The airplane's central hall width.
         * @param doorLength          The door's length.
         * @param seatWidth           The seats width.
         * @param seatSeparation      The sets separation.
         * @param targetRow           The row in which the final goal is located.
         * @param targetColumn        The column in which the final goal is located.
         *                            Note that column numbers are counted from the center to the outside.
         * @param targetSide          The side of the airplane in which the final goal is located.
         * @param jetBridgeWidth      The width of the jet bridge (seen in an x-y plane it would be the length).
         * @param particleXSeparation The separation between particles when they are initialized
         *                            (used to generate the region where they must go
         *                            when approaching to the jet bridge).
         * @param margin              A margin to be used when calculating differences.
         */
        private GoalStateMachine(final double frontHallLength, final double centralHallWidth, final double doorLength,
                                 final double seatWidth, final double seatSeparation,
                                 final int targetRow, final int targetColumn, final AirplaneSide targetSide,
                                 final double jetBridgeWidth, final double particleXSeparation,
                                 final double margin) {
            this.frontHallLength = frontHallLength;
            this.centralHallWidth = centralHallWidth;
            this.doorLength = doorLength;
            this.seatWidth = seatWidth;
            this.seatSeparation = seatSeparation;
            this.targetRow = targetRow;
            this.targetColumn = targetColumn;
            this.targetSide = targetSide;
            this.jetBridgeWidth = jetBridgeWidth;
            this.particleXSeparation = particleXSeparation;
            this.margin = margin;
        }

        /**
         * Initializes the state machine.
         *
         * @param particle The {@link Particle} that will use the state machine.
         */
        /* package */ void initialize(final Particle particle) {
            Assert.state(this.particle == null && actualState == null,
                    "The state machine is already initialized ");
            Assert.notNull(particle, "The particle must not be null");
            this.particle = particle;
            this.actualState = new ApproachJetBridgeState(this, particle.getPosition().getX());
        }

        /**
         * @return The particle that will use this state machine.
         */
        /* package */ Particle getParticle() {
            return particle;
        }

        /**
         * @return The airplane's front hall length.
         */
        /* package */ double getFrontHallLength() {
            return frontHallLength;
        }

        /**
         * @return The airplane's central hall width.
         */
        /* package */ double getCentralHallWidth() {
            return centralHallWidth;
        }

        /**
         * @return The door's length.
         */
        /* package */ double getDoorLength() {
            return doorLength;
        }

        /**
         * @return The seats width.
         */
        /* package */ double getSeatWidth() {
            return seatWidth;
        }

        /**
         * @return The sets separation.
         */
        /* package */ double getSeatSeparation() {
            return seatSeparation;
        }

        /**
         * @return The row in which the final goal is located.
         */
        /* package */ int getTargetRow() {
            return targetRow;
        }

        /**
         * @return The column in which the final goal is located.
         * Note that column numbers are counted from the center to the outside.
         */
        /* package */ int getTargetColumn() {
            return targetColumn;
        }

        /**
         * @return The side of the airplane in which the final goal is located.
         */
        /* package */ AirplaneSide getTargetSide() {
            return targetSide;
        }

        /**
         * @return The width of the jet bridge (seen in an x-y plane it would be the length).
         */
        /* package */ double getJetBridgeWidth() {
            return jetBridgeWidth;
        }

        /**
         * @return The separation between particles when they are initialized
         * (used to generate the region where they must go when approaching to the jet bridge).
         */
        /* package */ double getParticleXSeparation() {
            return particleXSeparation;
        }

        /**
         * @return A margin to be used when calculating a random 'x' component.
         */
        /* package */ double getMargin() {
            return margin;
        }

        /**
         * Calculates (using some randomness) the next position where the owner {@link Particle}
         * of the goal that owns the state machine must go.
         *
         * @return The next position for the owner of the goal that owns the state machine.
         */
        /* package */ Vector2D getNextPosition() {
            Assert.state(actualState != null && particle != null, "The state machine must be initialized first");
            return actualState.getNextPosition();
        }

        /**
         * Makes this state machine go to the next state.
         */
        /* package */ void nextState() {
            Assert.state(actualState != null && particle != null, "The state machine must be initialized first");
            this.actualState = actualState.nextState();
        }

        /**
         * Notifies this state machine that the given {@link Particle} has moved towards the {@link Goal} that owns it.
         *
         * @apiNote This method should only be called by the {@link Particle}
         * that owns the {@link Goal} that owns this state machine.
         */
        /* package */ void notifyMove() {
            Assert.state(actualState != null && particle != null, "The state machine must be initialized first");
            this.actualState.notifyMove();
        }

        /**
         * Indicates that the state machine is in the last moving state (i.e has the {@link FinalGoalState} state).
         *
         * @return {@code true} if the state machine's state is the {@link FinalGoalState}, or {@code false} otherwise.
         */
        /* package */ boolean isLastMovingTarget() {
            Assert.state(actualState != null && particle != null, "The state machine must be initialized first");
            return this.actualState.getClass() == FinalGoalState.class;
        }

        /**
         * Indicates that the state machine is in the last moving state (i.e has the {@link FinalGoalState} state).
         *
         * @return {@code true} if the state machine's state is the {@link FinalGoalState}, or {@code false} otherwise.
         */
        /* package */ boolean isInsideAirplane() {
            Assert.state(actualState != null && particle != null, "The state machine must be initialized first");
            return this.actualState.isInside();
        }

        /**
         * Indicates that the state machine is in the last state (i.e has the {@link ReachedGoalState} state).
         *
         * @return {@code true} if the state machine's state is the {@link ReachedGoalState}, or {@code false} otherwise.
         */
        /* package */ boolean noMoreTargets() {
            Assert.state(actualState != null && particle != null, "The state machine must be initialized first");
            return this.actualState.getClass() == ReachedGoalState.class;
        }
    }

    /**
     * An abstract state for the {@link GoalStateMachine}.
     */
    private static abstract class GoalState {

        /**
         * The {@link GoalStateMachine} that owns this state.
         */
        private final GoalStateMachine goalStateMachine;

        /**
         * Constructor.
         *
         * @param goalStateMachine The {@link GoalStateMachine} that owns this state.
         */
        private GoalState(GoalStateMachine goalStateMachine) {
            this.goalStateMachine = goalStateMachine;
        }

        /**
         * @return The {@link GoalStateMachine} that owns this state.
         */
        /* package */  GoalStateMachine getGoalStateMachine() {
            return goalStateMachine;
        }

        /**
         * Calculates the next state of the state machine.
         *
         * @return The next state.
         */
        /* package */
        abstract GoalState nextState();

        /**
         * @return The next position for the owner of the goal that owns the state machine.
         * @apiNote Some states might recalculate this each time this method is called,
         * and others might store the initial one (i.e the first one that was returned).
         */
        abstract Vector2D getNextPosition();

        /**
         * Notifies this state that the given {@link Particle} has moved towards the {@link Goal} that owns it.
         *
         * @apiNote This method should only be called by the {@link Particle}
         * that owns the {@link Goal} that owns the state machine that owns this state.
         */
        /* package */
        abstract void notifyMove();

        /**
         * Indicates that this state returns a goal inside the airplane.
         *
         * @return {@code true} if the target is inside the airplane, or {@code false} otherwise.
         */
        abstract boolean isInside();
    }

    /**
     * An abstract state in which the goal is in the center of a rectangular region,
     * and the next state is given when reached this region.
     */
    private abstract static class ReachARectangularRegionState extends GoalState {

        /**
         * The next position for the owner of the goal that owns the state machine (stored to avoid recalculating a it)
         */
        private final Vector2D nextPosition;

        /**
         * The starting value for the 'x' component of the goal.
         */
        private final double startingX;
        /**
         * The finishing value for the 'x' component of the goal.
         */
        private final double finishingX;
        /**
         * The starting value for the 'y' component of the goal.
         */
        private final double startingY;
        /**
         * The finishing value for the 'y' component of the goal.
         */
        private final double finishingY;

        /**
         * A {@link Supplier} that returns the next state.
         */
        private final Supplier<GoalState> nextStateSupplier;

        /**
         * Indicates whether the goal is inside the airplane.
         */
        private final boolean isInside;


        /**
         * Constructor.
         *
         * @param goalStateMachine  The {@link GoalStateMachine} that owns this state.
         * @param startingX         The starting value for the 'x' component of the goal.
         * @param finishingX        The finishing value for the 'x' component of the goal.
         * @param startingY         The starting value for the 'y' component of the goal.
         * @param finishingY        The finishing value for the 'y' component of the goal.
         * @param nextStateSupplier A {@link Supplier} that returns the next state.
         */
        private ReachARectangularRegionState(final GoalStateMachine goalStateMachine,
                                             final double startingX, final double finishingX,
                                             final double startingY, final double finishingY,
                                             final Supplier<GoalState> nextStateSupplier,
                                             final boolean isInside) {
            super(goalStateMachine);
            this.startingX = startingX;
            this.finishingX = finishingX;
            this.startingY = startingY;
            this.finishingY = finishingY;
            this.nextStateSupplier = nextStateSupplier;
            final double x = this.startingX + (this.finishingX - this.startingX) / 2;
            final double y = this.startingY + (this.finishingY - this.startingY) / 2;
            this.nextPosition = new Vector2D(x, y);
            this.isInside = isInside;
        }

        @Override
        /* package */ GoalState nextState() {
            return nextStateSupplier.get();
        }

        @Override
        /* package */ Vector2D getNextPosition() {
            return nextPosition;
        }

        @Override
        /* package */ void notifyMove() {
            final Vector2D position = getGoalStateMachine().getParticle().getPosition();
            final double margin = getGoalStateMachine().getMargin();
            // Check whether the particle has enter the center of the target row
            if ((this.startingX + margin <= position.getX()
                    && position.getX() <= this.finishingX - margin)
                    && (this.startingY + margin <= position.getY()
                    && position.getY() <= this.finishingY - margin)) {
                // In this case we assume that the goal was reached, so we move forward the state machine
                getGoalStateMachine().nextState();
            }
        }

        @Override
        /* package */ boolean isInside() {
            return isInside;
        }
    }

    /**
     * State in which the jet bridge must be approached.
     */
    private final static class ApproachJetBridgeState extends ReachARectangularRegionState {

        /**
         * Constructor.
         *
         * @param goalStateMachine The {@link GoalStateMachine} that owns this state.
         * @param startingX        The 'x' component of the starting point of the particle owning the state machine.
         */
        private ApproachJetBridgeState(final GoalStateMachine goalStateMachine, final double startingX) {
            super(goalStateMachine,
                    startingX - goalStateMachine.getParticleXSeparation(),
                    startingX + goalStateMachine.getParticleXSeparation(),
                    0,
                    goalStateMachine.getJetBridgeWidth() - goalStateMachine.getMargin() / 2,
                    () -> new ReachDoorState(goalStateMachine),
                    false);
        }
    }


    /**
     * State in which the particle must reach the airplane door (the "reach check" is done on a region basis).
     */
    private final static class ReachDoorState extends ReachARectangularRegionState {

        /**
         * Constructor.
         *
         * @param goalStateMachine The {@link GoalStateMachine} that owns this state.
         */
        private ReachDoorState(GoalStateMachine goalStateMachine) {
            super(goalStateMachine,
                    goalStateMachine.getCentralHallWidth() / 2,
                    goalStateMachine.getCentralHallWidth() / 2 + 4 * goalStateMachine.getMargin(),
                    0,
                    goalStateMachine.getDoorLength(),
                    () -> new FrontHallState(goalStateMachine),
                    false);
        }
    }

    /**
     * State in which the particle must reach the front hall of the airplane.
     */
    private final static class FrontHallState extends GoalState {

        /**
         * The next position for the owner of the goal that owns the state machine
         * (stored to avoid recalculating a random value)
         */
        private final Vector2D nextPosition;

        /**
         * Constructor.
         *
         * @param goalStateMachine The {@link GoalStateMachine} that owns this state.
         */
        private FrontHallState(final GoalStateMachine goalStateMachine) {
            super(goalStateMachine);
            final double x = getGoalStateMachine().getTargetSide().getSign()
                    * new Random().nextDouble()
                    * (getGoalStateMachine().getCentralHallWidth() / 2 - getGoalStateMachine().getMargin());
            final double y = getGoalStateMachine().getDoorLength();
            this.nextPosition = new Vector2D(x, y);
        }

        @Override
        /* package */ GoalState nextState() {
            final GoalStateMachine goalStateMachine = getGoalStateMachine();
            return goalStateMachine.getTargetRow() == 0 ?
                    new LastMiddleState(getGoalStateMachine()) :
                    new MiddleGoalState(goalStateMachine, 0);
        }

        @Override
        /* package */ Vector2D getNextPosition() {
            return nextPosition;
        }

        @Override
        /* package */ void notifyMove() {
            // Check whether the particle has overstepped the row's final line.
            // TODO: being inside the airplane
            final Vector2D position = getGoalStateMachine().getParticle().getPosition();
            if (position.getY() + getGoalStateMachine().getMargin() > getNextPosition().getY()) {
                // In this case we assume that the goal was reached, so we move forward the state machine
                getGoalStateMachine().nextState();
            }
        }

        @Override
        /* package */ boolean isInside() {
            return true;
        }
    }

    /**
     * A middle state (i.e in the middle of each row of the central hall).
     */
    private final static class MiddleGoalState extends GoalState {

        /**
         * The row where the middle goal is (0-indexed)
         */
        private final int row;

        /**
         * The next position for the owner of the goal that owns the state machine
         * (stored to avoid recalculating a random value)
         */
        private final Vector2D nextPosition;

        /**
         * Constructor.
         *
         * @param goalStateMachine The {@link GoalStateMachine} that owns this state.
         * @param row              The row where the middle goal is (0-indexed).
         */
        private MiddleGoalState(final GoalStateMachine goalStateMachine, final int row) {
            super(goalStateMachine);
            Assert.isTrue(row != goalStateMachine.getTargetRow(),
                    "This state should not be used with the target row");
            this.row = row;
            final double x = getGoalStateMachine().getTargetSide().getSign()
                    * new Random().nextDouble()
                    * (getGoalStateMachine().getCentralHallWidth() / 2 - getGoalStateMachine().getMargin());
            final double y = getGoalStateMachine().getFrontHallLength()
                    + (row + 1) * getGoalStateMachine().getSeatSeparation();
            this.nextPosition = new Vector2D(x, y);
        }

        @Override
        /* package */ GoalState nextState() {
            final GoalStateMachine stateMachine = this.getGoalStateMachine();
            return row + 1 == stateMachine.getTargetRow() ?
                    new LastMiddleState(stateMachine) :
                    new MiddleGoalState(stateMachine, row + 1);
        }

        @Override
        /* package */ Vector2D getNextPosition() {
            return nextPosition;
        }

        @Override
        /* package */ void notifyMove() {
            // Check whether the particle has overstepped the row's final line.
            final Vector2D position = getGoalStateMachine().getParticle().getPosition();
            if (position.getY() + getGoalStateMachine().getMargin() > getNextPosition().getY()) {
                // In this case we assume that the goal was reached, so we move forward the state machine
                getGoalStateMachine().nextState();
            }
        }

        @Override
        /* package */ boolean isInside() {
            return true;
        }
    }

    /**
     * The last middle state (is different because the "reach check" is done on a region basis).
     */
    private final static class LastMiddleState extends ReachARectangularRegionState {

        /**
         * Constructor.
         *
         * @param goalStateMachine The {@link GoalStateMachine} that owns this state.
         */
        private LastMiddleState(final GoalStateMachine goalStateMachine) {
            super(goalStateMachine,
                    calculateStartingX(goalStateMachine),
                    calculateFinishingX(goalStateMachine),
                    goalStateMachine.getFrontHallLength()
                            + goalStateMachine.getTargetRow() * goalStateMachine.getSeatSeparation(),
                    goalStateMachine.getFrontHallLength()
                            + (goalStateMachine.getTargetRow() + 1) * goalStateMachine.getSeatSeparation(),
                    () -> new FinalGoalState(goalStateMachine),
                    true);
        }

        /**
         * Calculates which is the starting 'x' according to the given {@code goalStateMachine}.
         *
         * @param goalStateMachine The {@link GoalStateMachine} from where data to perform the calculation is taken.
         * @return The starting 'x'.
         */
        private static double calculateStartingX(final GoalStateMachine goalStateMachine) {
            final double xMax = goalStateMachine.centralHallWidth / 2;
            switch (goalStateMachine.getTargetSide()) {
                case LEFT:
                    return -xMax;
                case RIGHT:
                    return 0;
                default:
                    throw new RuntimeException("This should not happen");
            }
        }

        /**
         * Calculates which is the finishing 'x' according to the given {@code goalStateMachine}.
         *
         * @param goalStateMachine The {@link GoalStateMachine} from where data to perform the calculation is taken.
         * @return The starting 'x'.
         */
        private static double calculateFinishingX(final GoalStateMachine goalStateMachine) {
            final double xMax = goalStateMachine.centralHallWidth / 2;
            switch (goalStateMachine.getTargetSide()) {
                case LEFT:
                    return 0;
                case RIGHT:
                    return xMax;
                default:
                    throw new RuntimeException("This should not happen");
            }
        }
    }

    /**
     * The final state (i.e in the seat the owner must go).
     */
    private final static class FinalGoalState extends GoalState {

        /**
         * The next position for the owner of the goal that owns the state machine (stored to avoid recalculating it)
         */
        private final Vector2D nextPosition;

        /**
         * Constructor.
         *
         * @param goalStateMachine The {@link GoalStateMachine} that owns this state.
         */
        private FinalGoalState(final GoalStateMachine goalStateMachine) {
            super(goalStateMachine);
            final double seatWidth = getGoalStateMachine().getSeatWidth();
            final double halfCentralHallWidth = getGoalStateMachine().getCentralHallWidth() / 2;
            final double x = getGoalStateMachine().getTargetSide().getSign()
                    * (halfCentralHallWidth + (getGoalStateMachine().getTargetColumn() + 0.5) * seatWidth);
            final double y = getGoalStateMachine().getFrontHallLength()
                    + (getGoalStateMachine().getTargetRow() + 0.5) * getGoalStateMachine().getSeatSeparation();
            this.nextPosition = new Vector2D(x, y);
        }

        @Override
        /* package */ GoalState nextState() {
            return new ReachedGoalState(this.getGoalStateMachine(), this.nextPosition);
        }

        @Override
        /* package */ Vector2D getNextPosition() {
            return nextPosition;
        }

        @Override
        /* package */ void notifyMove() {
            final Vector2D position = getGoalStateMachine().getParticle().getPosition();
            final double margin = getGoalStateMachine().getMargin();
            // Check whether the particle has reached the seat (with a margin of error)
            if (position.distance(getNextPosition()) < margin) {
                // In this case we assume that the goal was reached, so we move forward the state machine
                getGoalStateMachine().nextState();
            }
        }

        @Override
        /* package */ boolean isInside() {
            return true;
        }
    }

    /**
     * A state that represents the moment in which the final target is reached (i.e this is the real final state).
     */
    private final static class ReachedGoalState extends GoalState {

        /**
         * The target that was used to reach this state.
         */
        private final Vector2D theLastTarget;

        /**
         * Constructor.
         *
         * @param goalStateMachine The {@link GoalStateMachine} that owns this state.
         * @param theLastTarget    The target that was used to reach this state.
         */
        private ReachedGoalState(GoalStateMachine goalStateMachine, Vector2D theLastTarget) {
            super(goalStateMachine);
            this.theLastTarget = theLastTarget;
        }

        @Override
        /* package */ GoalState nextState() {
            // There are no new states after the final goal, so if asked for a next state, the same is returned.
            return this;
        }

        @Override
        /* package */ Vector2D getNextPosition() {
            return theLastTarget;
        }

        @Override
        /* package */ void notifyMove() {
            // In this case we don't do anything
        }

        @Override
        /* package */ boolean isInside() {
            return false;
        }
    }
}
