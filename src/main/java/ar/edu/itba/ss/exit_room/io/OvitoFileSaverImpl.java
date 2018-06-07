package ar.edu.itba.ss.exit_room.io;

import ar.edu.itba.ss.exit_room.models.Particle;
import ar.edu.itba.ss.exit_room.models.Room;
import ar.edu.itba.ss.exit_room.models.Wall;
import ar.edu.itba.ss.g7.engine.io.OvitoFileSaver;

import java.io.IOException;
import java.io.Writer;

/**
 * {@link OvitoFileSaver} for the {@link Room}.
 */
public class OvitoFileSaverImpl extends OvitoFileSaver<Room.RoomState> {

    /**
     * Min. particle radius (used to set colors).
     */
    private final double particleMinRadius;
    /**
     * Max. particle radius (used to set colors).
     */
    private final double particleMaxRadius;

    /**
     * Difference between min. and max. radius (saved to avoid recalculating this each time).
     */
    private final double radiusDifference;


    /**
     * Constructor.
     *
     * @param filePath          Path to the file to be saved.
     * @param particleMinRadius Min. particle radius (used to set colors).
     * @param particleMaxRadius Max. particle radius (used to set colors).
     */
    public OvitoFileSaverImpl(String filePath, double particleMinRadius, double particleMaxRadius) {
        super(filePath);
        this.particleMinRadius = particleMinRadius;
        this.particleMaxRadius = particleMaxRadius;
        this.radiusDifference = particleMaxRadius - particleMinRadius;
    }

    @Override
    public void saveState(Writer writer, Room.RoomState roomState, int frame) throws IOException {

        final StringBuilder data = new StringBuilder();
        // First, headers
        data.append(roomState.getParticleStates().size() + 2 * roomState.getWallStates().size())
                .append("\n")
                .append(frame)
                .append("\n");
        // Save particles
        for (Particle.ParticleState particle : roomState.getParticleStates()) {
            saveParticle(data, particle);
        }
        // Save walls
        for (Wall.WallState wall : roomState.getWallStates()) {
            saveWall(data, wall);
        }

        // Append data into the Writer
        writer.append(data);

    }

    /**
     * Saves a {@link ar.edu.itba.ss.exit_room.models.Particle.ParticleState}
     * into the {@code data} {@link StringBuilder}.
     *
     * @param data     The {@link StringBuilder} that is collecting data.
     * @param particle The {@link ar.edu.itba.ss.exit_room.models.Particle.ParticleState} with the data.
     */
    private void saveParticle(final StringBuilder data, Particle.ParticleState particle) {
        data.append("")
                .append(particle.getPosition().getX())
                .append(" ")
                .append(particle.getPosition().getY())
                .append(" ")
                .append(particle.getVelocity().getX())
                .append(" ")
                .append(particle.getVelocity().getY())
                .append(" ")
                .append(particleMinRadius)
                .append(" ")
                .append(calculateRedForParticle(particle)) // Red
                .append(" ")
                .append(calculateGreenForParticle(particle)) // Green
                .append(" ")
                .append(calculateBlueForParticle(particle)) // Blue
                .append("\n");
    }

    /**
     * Saves a {@link ar.edu.itba.ss.exit_room.models.Wall.WallState}
     * into the {@code data} {@link StringBuilder}.
     *
     * @param data The {@link StringBuilder} that is collecting data.
     * @param wall The {@link ar.edu.itba.ss.exit_room.models.Wall.WallState} with the data.
     */
    private void saveWall(final StringBuilder data, final Wall.WallState wall) {
        data.append("")
                .append(wall.getInitialPoint().getX())
                .append(" ")
                .append(wall.getInitialPoint().getY())
                .append(" ")
                .append(0)
                .append(" ")
                .append(0)
                .append(" ")
                .append(0.05)  // Radius
                .append(" ")
                .append(1) // Red
                .append(" ")
                .append(1) // Green
                .append(" ")
                .append(1) // Blue
                .append("\n")
                .append(wall.getFinalPoint().getX())
                .append(" ")
                .append(wall.getFinalPoint().getY())
                .append(" ")
                .append(0)
                .append(" ")
                .append(0)
                .append(" ")
                .append(0.05) // Radius
                .append(" ")
                .append(1) // Red
                .append(" ")
                .append(1) // Green
                .append(" ")
                .append(1) // Blue
                .append("\n");
    }

    /**
     * Provides the red component for the given particle.
     *
     * @param particleState The particle whose red color component is going to be calculated.
     * @return The red component for the particle.
     */
    private double calculateRedForParticle(final Particle.ParticleState particleState) {
        return particleState.hasReachedTheGoal() ?
                0d : (particleMaxRadius - particleState.getRadius()) / radiusDifference;
    }

    /**
     * Provides the green component for the given particle.
     *
     * @param particleState The particle whose green color component is going to be calculated.
     * @return The green component for the particle.
     */
    private double calculateGreenForParticle(final Particle.ParticleState particleState) {
        return particleState.hasReachedTheGoal() ?
                0d : (particleState.getRadius() - particleMinRadius) / radiusDifference;
    }

    /**
     * Provides the blue component for the given particle.
     *
     * @param particleState The particle whose blue color component is going to be calculated.
     * @return The blue component for the particle.
     */
    private double calculateBlueForParticle(final Particle.ParticleState particleState) {
        return particleState.hasReachedTheGoal() ? 1d : 0d;
    }
}
