package ar.edu.itba.ss.exit_room.utils;

import ar.edu.itba.ss.exit_room.models.Goal;
import ar.edu.itba.ss.exit_room.models.Particle;
import ar.edu.itba.ss.exit_room.models.Wall;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Class in charge of initializing the system.
 */
public class ComponentsProvider {

    /**
     * The max. amount of consecutive failed tries of adding a {@link Particle}
     * into the returned {@link List} of {@link Particle} by {@link #createParticles()} method.
     */
    private static final int MAX_AMOUNT_OF_TRIES = 3000;


    // ================================================================================================================
    // Room stuff
    // ================================================================================================================

    /**
     * The 'x' component that is minimum.
     */
    private final double xMin;

    /**
     * The 'x' component that is maximum.
     */
    private final double xMax;

    /**
     * The 'y' component that is minimum.
     */
    private final double yMin;

    /**
     * The 'y' component that is maximum.
     */
    private final double yMax;

    /**
     * Half of the length of a door.
     */
    private final double halfRoomDoorLength;

    // ================================================================================================================
    // Particles stuff
    // ================================================================================================================

    /**
     * The min. radius of the {@link Particle}s to be created (which will be the starting radius).
     */
    private final double minRadius;

    /**
     * The max. radius of the {@link Particle}s to be created.
     */
    private final double maxRadius;
    /**
     * Mean time a particle needs to get to the minimum radius.
     */
    private final double tao;

    /**
     * Experimental constant that defines the linearity between speed changes and blocks avoidance.
     */
    private final double beta;

    /**
     * The max. speed a particle can reach.
     */
    private final double maxVelocityModule;

    /**
     * The max. amount of {@link Particle}s to be created.
     */
    private final int maxAmountOfParticles;

    /**
     * Constructor.
     *
     * @param roomLength           The room's length.
     * @param roomWidth            The room's width.
     * @param roomDoorLength       The door's length.
     * @param minRadius            The min. radius of the {@link Particle}s to be created
     *                             (which will be the starting radius).
     * @param maxRadius            The max. amount of {@link Particle}s to be created.
     * @param tao                  Mean time a {@link Particle} needs to get to the minimum radius.
     * @param beta                 Experimental constant that defines the linearity
     *                             between speed changes and blocks avoidance for a {@link Particle}.
     * @param maxVelocityModule    The max. speed a {@link Particle} can reach.
     * @param maxAmountOfParticles The max. amount of particles.
     */
    public ComponentsProvider(final double roomLength, final double roomWidth, final double roomDoorLength,
                              final double minRadius, double maxRadius,
                              double tao, double beta, double maxVelocityModule,
                              final int maxAmountOfParticles) {
        Assert.isTrue(roomLength > 0 && roomWidth > 0 && roomDoorLength > 0,
                "The dimensions of the room must be positive");
        Assert.isTrue(roomWidth > roomDoorLength,
                "The door length must be greater than the width of the room");

        // For particles only validate amount of them,
        // as the rest of the arguments are being validated already by the Particle class
        Assert.isTrue(maxAmountOfParticles > 0, "The max. amount of particles must be positive");

        // Set the origin in the middle of the door
        this.xMin = -roomWidth / 2;
        this.xMax = roomWidth / 2;
        this.yMin = 0;
        this.yMax = roomLength;
        this.halfRoomDoorLength = roomDoorLength / 2;
        this.minRadius = minRadius;
        this.maxRadius = maxRadius;
        this.tao = tao;
        this.beta = beta;
        this.maxVelocityModule = maxVelocityModule;
        this.maxAmountOfParticles = maxAmountOfParticles;
    }


    /**
     * Creates the {@link List} of {@link Wall}s that defines a {@link ar.edu.itba.ss.exit_room.models.Room}.
     *
     * @return The {@link List} of {@link Wall}s that define the room.
     * @apiNote The origin is in the middle of the door.
     */
    public List<Wall> buildWalls() {
        final Wall leftWall = new Wall(new Vector2D(xMin, yMin), new Vector2D(xMin, yMax));
        final Wall rightWall = new Wall(new Vector2D(xMax, yMin), new Vector2D(xMax, yMax));
        final Wall backWall = new Wall(new Vector2D(xMin, yMax), new Vector2D(xMax, yMax));
        final Wall frontLeftWall = new Wall(new Vector2D(xMin, yMin), new Vector2D(-halfRoomDoorLength, yMin));
        final Wall frontRightWall = new Wall(new Vector2D(halfRoomDoorLength, yMin), new Vector2D(xMax, yMin));
        return Arrays.asList(leftWall, rightWall, backWall, frontLeftWall, frontRightWall);
    }


    /**
     * Creates the {@link List} of {@link Particle}.
     *
     * @return The {@link List} of {@link Particle}s.
     */
    public List<Particle> createParticles() {
        final Goal goal = buildGoalForParticles();
        final List<Particle> particles = new LinkedList<>();
        int tries = 0; // Will count the amount of consecutive failed tries of adding randomly a particle into the list.
        while (tries < MAX_AMOUNT_OF_TRIES && particles.size() < maxAmountOfParticles) {
            final double xPosition = (xMin + minRadius)
                    + new Random().nextDouble() * ((xMax - minRadius) - (xMin + minRadius));
            final double yPosition = (yMin + minRadius)
                    + new Random().nextDouble() * ((yMax - minRadius) - (yMin + minRadius));
            final Vector2D position = new Vector2D(xPosition, yPosition);
            if (particles.stream().noneMatch(p -> p.doOverlap(position, minRadius))) {
                particles.add(new Particle(minRadius, position, Vector2D.ZERO,
                        goal, this::buildNewRandomOutsideGoal, minRadius, maxRadius, tao, beta, maxVelocityModule));
                tries = 0; // When a particle is added, the counter of consecutive failed tries must be set to zero.
            } else {
                tries++;
            }
        }
        return particles;
    }

    /**
     * Builds a {@link Goal} for the {@link Particle}s that are going to be created by this {@link ComponentsProvider}
     * (i.e having into account the room - or walls - that are built).
     *
     * @return The built {@link Goal}.
     */
    private Goal buildGoalForParticles() {
        final Vector2D center = new Vector2D(0, 0);
        final double margin = halfRoomDoorLength - minRadius;
        return new Goal(center, margin, 0, null, null, 0d, null);
    }

    /**
     * Builds a new random {@link Goal} that is outside the room.
     *
     * @return a new random {@link Goal}.
     */
    private Goal buildNewRandomOutsideGoal() {
        final double xCenter = xMin + new Random().nextDouble() * (xMax - xMin);
        final double yCenter = -yMax / 2 + new Random().nextDouble() * (-yMax / 4 - (-yMax / 2));
        return new Goal(new Vector2D(xCenter, yCenter), 0d, 0d, null, null, null, null);
    }
}
