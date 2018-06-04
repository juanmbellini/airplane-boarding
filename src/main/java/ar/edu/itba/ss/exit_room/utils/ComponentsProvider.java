package ar.edu.itba.ss.exit_room.utils;

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
     * The starting radius of the {@link Particle}s to be created.
     */
    private final double startingRadius;
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
     * @param startingRadius       The starting radius of the particles.
     * @param maxAmountOfParticles The max. amount of particles.
     */
    public ComponentsProvider(final double roomLength, final double roomWidth, final double roomDoorLength,
                              final double startingRadius, final int maxAmountOfParticles) {
        Assert.isTrue(roomLength > 0 && roomWidth > 0 && roomDoorLength > 0,
                "The dimensions of the room must be positive");
        Assert.isTrue(roomWidth > roomDoorLength,
                "The door length must be greater than the width of the room");
        Assert.isTrue(startingRadius > 0, "The starting radius must be positive");
        Assert.isTrue(maxAmountOfParticles > 0, "The max. amount of particles must be positive");

        // Set the origin in the middle of the door
        this.xMin = -roomWidth / 2;
        this.xMax = roomWidth / 2;
        this.yMin = 0;
        this.yMax = roomLength;
        this.halfRoomDoorLength = roomDoorLength / 2;

        this.startingRadius = startingRadius;
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
        final List<Particle> particles = new LinkedList<>();
        int tries = 0; // Will count the amount of consecutive failed tries of adding randomly a particle into the list.
        while (tries < MAX_AMOUNT_OF_TRIES && particles.size() < maxAmountOfParticles) {
            final double xPosition = (xMin + startingRadius)
                    + new Random().nextDouble() * ((xMax - startingRadius) - (xMin + startingRadius));
            final double yPosition = (yMin + startingRadius)
                    + new Random().nextDouble() * ((yMax - startingRadius) - (yMin + startingRadius));
            final Vector2D position = new Vector2D(xPosition, yPosition);
            if (particles.stream().noneMatch(p -> p.doOverlap(position, startingRadius))) {
                particles.add(new Particle(startingRadius, position, Vector2D.ZERO));
                tries = 0; // When a particle is added, the counter of consecutive failed tries must be set to zero.
            } else {
                tries++;
            }
        }
        return particles;
    }
}
