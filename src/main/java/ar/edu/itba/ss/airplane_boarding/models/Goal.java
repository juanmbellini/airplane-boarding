package ar.edu.itba.ss.airplane_boarding.models;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.springframework.util.Assert;

import java.util.Optional;
import java.util.Random;

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
     * @param frontHallLength  The airplane's front hall length.
     * @param centralHallWidth The airplane's central hall width.
     * @param doorLength       The door's length.
     * @param seatWidth        The seats width.
     * @param seatSeparation   The sets separation.
     * @param targetRow        The row in which the final goal is located (0-indexed).
     * @param targetColumn     The column in which the final goal is located (0-indexed).
     *                         Note that column numbers are counted from the center to the outside.
     * @param targetSide       The side of the airplane in which the final goal is located (i.e LEFT or RIGHT).
     * @param margin           A margin to be used when calculating a random 'x' component.
     */
    public Goal(final double frontHallLength, final double centralHallWidth, final double doorLength,
                final double seatWidth, final double seatSeparation,
                final int targetRow, final int targetColumn, final AirplaneSide targetSide,
                final double margin) {
        this.stateMachine = new GoalStateMachine(frontHallLength, centralHallWidth, doorLength,
                seatWidth, seatSeparation, targetRow, targetColumn, targetSide, margin);
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
     * Notifies this goal that the target was reached.
     */
    public void notifyArrival() {
        this.stateMachine.nextState();
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
            if (sign != 1 && sign != -1) {
                throw new IllegalArgumentException("The sign should be 1 or -1.");
            }
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
         * A margin to be used when calculating a random 'x' component.
         */
        private final double margin;

        /**
         * The actual state of this state machine.
         */
        private GoalState actualState;


        /**
         * @param frontHallLength  The airplane's front hall length.
         * @param centralHallWidth The airplane's central hall width.
         * @param doorLength       The door's length.
         * @param seatWidth        The seats width.
         * @param seatSeparation   The sets separation.
         * @param targetRow        The row in which the final goal is located.
         * @param targetColumn     The column in which the final goal is located.
         *                         Note that column numbers are counted from the center to the outside.
         * @param targetSide       The side of the airplane in which the final goal is located.
         * @param margin           A margin to be used when calculating a random 'x' component.
         */
        private GoalStateMachine(final double frontHallLength, final double centralHallWidth, final double doorLength,
                                 final double seatWidth, final double seatSeparation,
                                 final int targetRow, final int targetColumn, final AirplaneSide targetSide,
                                 final double margin) {
            this.frontHallLength = frontHallLength;
            this.centralHallWidth = centralHallWidth;
            this.doorLength = doorLength;
            this.seatWidth = seatWidth;
            this.seatSeparation = seatSeparation;
            this.targetRow = targetRow;
            this.targetColumn = targetColumn;
            this.targetSide = targetSide;
            this.margin = margin;

            this.actualState = new InitialGoalState(this);
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
        /* package */  AirplaneSide getTargetSide() {
            return targetSide;
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
            return actualState.getNextPosition();
        }

        /**
         * Makes this state machine go to the next state.
         */
        /* package */ void nextState() {
            this.actualState = actualState.nextState();
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

        private final Vector2D nextPosition;

        /**
         * Constructor.
         *
         * @param goalStateMachine The {@link GoalStateMachine} that owns this state.
         */
        private GoalState(GoalStateMachine goalStateMachine) {
            this.goalStateMachine = goalStateMachine;
            this.nextPosition = provideTarget();
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
         * Calculates (using some randomness) the next position where the owner {@link Particle}
         * of the goal that owns the state machine must go.
         *
         * @return The next position for the owner of the goal that owns the state machine.
         */
        /* package */ Vector2D getNextPosition() {
            return nextPosition;
        }

        /* package */
        abstract Vector2D provideTarget();

    }

    /**
     * The initial state (i.e in the middle of the front hall).
     */
    private final static class InitialGoalState extends GoalState {

        /**
         * Constructor.
         *
         * @param goalStateMachine The {@link GoalStateMachine} that owns this state.
         */
        private InitialGoalState(final GoalStateMachine goalStateMachine) {
            super(goalStateMachine);
        }

        @Override
        /* package */ GoalState nextState() {
            final GoalStateMachine goalStateMachine = getGoalStateMachine();
            return goalStateMachine.getTargetRow() == 0 ?
                    new LastMiddleState(getGoalStateMachine()) :
                    new MiddleGoalState(goalStateMachine, 0);
        }

        @Override
        /* package */ Vector2D provideTarget() {
            final double x = getGoalStateMachine().getTargetSide().getSign()
                    * new Random().nextDouble()
                    * (getGoalStateMachine().getCentralHallWidth() / 2 - getGoalStateMachine().getMargin());
            final double y = getGoalStateMachine().getDoorLength();
            return new Vector2D(x, y);
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
        }

        @Override
        /* package */ GoalState nextState() {
            final GoalStateMachine stateMachine = this.getGoalStateMachine();
            return row + 1 == stateMachine.getTargetRow() ?
                    new LastMiddleState(stateMachine) :
                    new MiddleGoalState(stateMachine, row + 1);
        }

        @Override
        /* package */ Vector2D provideTarget() {
            final double x = getGoalStateMachine().getTargetSide().getSign()
                    * new Random().nextDouble()
                    * (getGoalStateMachine().getCentralHallWidth() / 2 - getGoalStateMachine().getMargin());
            final double y = getGoalStateMachine().getFrontHallLength()
                    + (row + 1) * getGoalStateMachine().getSeatSeparation();
            return new Vector2D(x, y);
        }
    }

    private final static class LastMiddleState extends GoalState {

        /**
         * Constructor.
         *
         * @param goalStateMachine The {@link GoalStateMachine} that owns this state.
         */
        private LastMiddleState(GoalStateMachine goalStateMachine) {
            super(goalStateMachine);
        }

        @Override
        /* package */ GoalState nextState() {
            return new FinalGoalState(getGoalStateMachine());
        }

        @Override
        /* package */ Vector2D provideTarget() {
            final double x = getGoalStateMachine().getTargetSide().getSign()
                    * getGoalStateMachine().getCentralHallWidth() / 2;
            final double y = getGoalStateMachine().getFrontHallLength()
                    + (getGoalStateMachine().getTargetRow() + 0.5) * getGoalStateMachine().getSeatSeparation();
            ;
            return new Vector2D(x, y);
        }
    }

    /**
     * The final state (i.e in the seat the owner must go).
     */
    private final static class FinalGoalState extends GoalState {

        /**
         * Constructor.
         *
         * @param goalStateMachine The {@link GoalStateMachine} that owns this state.
         */
        private FinalGoalState(final GoalStateMachine goalStateMachine) {
            super(goalStateMachine);
        }

        @Override
        /* package */ GoalState nextState() {
            return new ReachedGoalState(this.getGoalStateMachine(), this.getNextPosition());
        }

        @Override
        /* package */ Vector2D provideTarget() {
            final double seatWidth = getGoalStateMachine().getSeatWidth();
            final double halfCentralHallWidth = getGoalStateMachine().getCentralHallWidth() / 2;
            final double x = getGoalStateMachine().getTargetSide().getSign()
                    * (halfCentralHallWidth + (getGoalStateMachine().getTargetColumn() + 0.5) * seatWidth);
            final double y = getGoalStateMachine().getFrontHallLength()
                    + (getGoalStateMachine().getTargetRow() + 0.5) * getGoalStateMachine().getSeatSeparation();
            return new Vector2D(x, y);
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
        /* package */ Vector2D provideTarget() {
            return theLastTarget;
        }
    }
}
