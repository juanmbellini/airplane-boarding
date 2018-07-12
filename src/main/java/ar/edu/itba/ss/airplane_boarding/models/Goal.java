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
     * @param margin           A margin to be used when calculating differences.
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
     * Notify to this goal that the given {@code particle} hsa moved towards it.
     *
     * @param particle The {@link Particle} that moved.
     * @apiNote This method should only be called by the {@link Particle} that owns this goal.
     */
    public void notifyMove(final Particle particle) {
        this.stateMachine.notifyMove(particle);
    }


    /**
     * Indicates that the target being returned is the last one.
     *
     * @return {@code true} if the goal is returning the last target, or {@code false} otherwise.
     */
    public boolean isTheLastTarget() {
        return this.stateMachine.isLastMovingTarget();
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
         * A margin to be used when calculating differences.
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
         * @param margin           A margin to be used when calculating differences.
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

            this.actualState = new ReachDoorState(this);
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

        /**
         * Notifies this state machine that the given {@link Particle} has moved towards the {@link Goal} that owns it.
         *
         * @param particle The {@link Particle} that moved.
         * @apiNote This method should only be called by the {@link Particle}
         * that owns the {@link Goal} that owns this state machine.
         */
        /* package */ void notifyMove(final Particle particle) {
            this.actualState.notifyMove(particle);
        }

        /**
         * Indicates that the state machine is in the last moving state (i.e has the {@link FinalGoalState} state).
         *
         * @return {@code true} if the state machine's state is the {@link FinalGoalState}, or {@code false} otherwise.
         */
        /* package */ boolean isLastMovingTarget() {
            return this.actualState.getClass() == FinalGoalState.class;
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
         * @param particle The {@link Particle} that moved.
         * @apiNote This method should only be called by the {@link Particle}
         * that owns the {@link Goal} that owns the state machine that owns this state.
         */
        /* package */
        abstract void notifyMove(final Particle particle);
    }


    /**
     * State in which the particle must reach the airplane door (the "reach check" is done on a region basis).
     */
    private final static class ReachDoorState extends GoalState {

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
         * Constructor.
         *
         * @param goalStateMachine The {@link GoalStateMachine} that owns this state.
         */
        private ReachDoorState(GoalStateMachine goalStateMachine) {
            super(goalStateMachine);
            this.startingX = goalStateMachine.getCentralHallWidth() / 2;
            this.finishingX = startingX + 4 * goalStateMachine.getMargin();
            this.startingY = 0;
            this.finishingY = goalStateMachine.getDoorLength();
            final double x = this.startingX + (this.finishingX - this.startingX) / 2;
            final double y = this.startingY + (this.finishingY - this.startingY) / 2;
            this.nextPosition = new Vector2D(x, y);
        }

        @Override
        /* package */ GoalState nextState() {
            return new FrontHallState(this.getGoalStateMachine());
        }

        @Override
        /* package */ Vector2D getNextPosition() {
            return nextPosition;
        }

        @Override
        /* package */ void notifyMove(Particle particle) {
            final Vector2D position = particle.getPosition();
            final double margin = getGoalStateMachine().getMargin();
            if ((this.startingX + margin <= position.getX()
                    && position.getX() <= this.finishingX - margin)
                    && (this.startingY + margin <= position.getY()
                    && position.getY() <= this.finishingY - margin)) {
                // In this case we assume that the goal was reached, so we move forward the state machine
                getGoalStateMachine().nextState();
            }
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
        /* package */ void notifyMove(final Particle particle) {
            // Check whether the particle has overstepped the row's final line.
            // TODO: being inside the airplane
            final Vector2D position = particle.getPosition();
            if (position.getY() + getGoalStateMachine().getMargin() > getNextPosition().getY()) {
                // In this case we assume that the goal was reached, so we move forward the state machine
                getGoalStateMachine().nextState();
            }
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
        /* package */ void notifyMove(Particle particle) {
            // Check whether the particle has overstepped the row's final line.
            final Vector2D position = particle.getPosition();
            if (position.getY() + getGoalStateMachine().getMargin() > getNextPosition().getY()) {
                // In this case we assume that the goal was reached, so we move forward the state machine
                getGoalStateMachine().nextState();
            }
        }
    }

    /**
     * The last middle state (is different because the "reach check" is done on a region basis).
     */
    private final static class LastMiddleState extends GoalState {

        /**
         * The next position for the owner of the goal that owns the state machine (stored to avoid recalculating it).
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
         * Constructor.
         *
         * @param goalStateMachine The {@link GoalStateMachine} that owns this state.
         */
        private LastMiddleState(GoalStateMachine goalStateMachine) {
            super(goalStateMachine);
            final double xMax = goalStateMachine.centralHallWidth / 2;
            switch (getGoalStateMachine().getTargetSide()) {
                case LEFT: {
                    this.startingX = -xMax;
                    this.finishingX = 0;
                    break;
                }
                case RIGHT: {
                    this.startingX = 0;
                    this.finishingX = xMax;
                    break;
                }
                default:
                    throw new RuntimeException("This should not happen");
            }
            final double seatSeparation = goalStateMachine.getSeatSeparation();
            this.startingY = goalStateMachine.getFrontHallLength() + goalStateMachine.getTargetRow() * seatSeparation;
            this.finishingY = this.startingY + seatSeparation;

            final double x = this.startingX + (this.finishingX - this.startingX) / 2;
            final double y = this.startingY + (this.finishingY - this.startingY) / 2;
            this.nextPosition = new Vector2D(x, y);
        }

        @Override
        /* package */ GoalState nextState() {
            return new FinalGoalState(getGoalStateMachine());
        }

        @Override
        /* package */ Vector2D getNextPosition() {
            return nextPosition;
        }

        @Override
        /* package */ void notifyMove(Particle particle) {
            final Vector2D position = particle.getPosition();
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
        /* package */ void notifyMove(Particle particle) {
            final Vector2D position = particle.getPosition();
            final double margin = getGoalStateMachine().getMargin();
            // Check whether the particle has reached the seat (with a margin of error)
            if (position.distance(getNextPosition()) < margin) {
                // In this case we assume that the goal was reached, so we move forward the state machine
                getGoalStateMachine().nextState();
            }
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
        /* package */ void notifyMove(Particle particle) {
            // In this case we don't do anything
        }
    }
}
