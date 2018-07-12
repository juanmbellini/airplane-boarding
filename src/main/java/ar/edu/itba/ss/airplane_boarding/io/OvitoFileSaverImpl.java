package ar.edu.itba.ss.airplane_boarding.io;

import ar.edu.itba.ss.airplane_boarding.models.BoardingScene;
import ar.edu.itba.ss.airplane_boarding.models.Particle;
import ar.edu.itba.ss.airplane_boarding.models.Wall;
import ar.edu.itba.ss.g7.engine.io.OvitoFileSaver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Writer;

/**
 * {@link OvitoFileSaver} for the {@link BoardingScene}.
 */
@Component("ovitoFileSaver")
public class OvitoFileSaverImpl extends OvitoFileSaver<BoardingScene.BoardingSceneState> {

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
    @Autowired
    public OvitoFileSaverImpl(@Value("${custom.output.ovito}") final String filePath,
                              @Value("${custom.system.particle.min-radius}") final double particleMinRadius,
                              @Value("${custom.system.particle.max-radius}") final double particleMaxRadius) {
        super(filePath);
        this.particleMinRadius = particleMinRadius;
        this.particleMaxRadius = particleMaxRadius;
        this.radiusDifference = particleMaxRadius - particleMinRadius;
    }

    @Override
    public void saveState(final Writer writer,
                          final BoardingScene.BoardingSceneState boardingSceneState,
                          final int frame)
            throws IOException {

        final StringBuilder data = new StringBuilder();
        // First, headers
        final int amount = boardingSceneState.getParticleStates().size()
                + 2 * boardingSceneState.getObstaclesStates().size();
        data.append(amount)
                .append("\n")
                .append(frame)
                .append("\n");
        // Save particles
        int identifier = 0;
        for (Particle.ParticleState particle : boardingSceneState.getParticleStates()) {
            saveParticle(data, particle, identifier++);
        }
        // Save airplane obstacles
        for (Wall.WallState wall : boardingSceneState.getObstaclesStates()) {
            saveWall(data, wall, identifier++);
        }

        // Append data into the Writer
        writer.append(data);

    }

    /**
     * Saves a {@link ar.edu.itba.ss.airplane_boarding.models.Particle.ParticleState}
     * into the {@code data} {@link StringBuilder}.
     *
     * @param data       The {@link StringBuilder} that is collecting data.
     * @param particle   The {@link ar.edu.itba.ss.airplane_boarding.models.Particle.ParticleState} with the data.
     * @param identifier An integer id used to identify each particle
     */
    private void saveParticle(final StringBuilder data, final Particle.ParticleState particle, final int identifier) {
        data.append("")
                .append(particle.getPosition().getX())
                .append(" ")
                .append(particle.getPosition().getY())
                .append(" ")
                .append(particle.getVelocity().getX())
                .append(" ")
                .append(particle.getVelocity().getY())
                .append(" ")
                .append(particle.getRadius())
                .append(" ")
                .append(calculateRedForParticle(particle)) // Red
                .append(" ")
                .append(calculateGreenForParticle(particle)) // Green
                .append(" ")
                .append(calculateBlueForParticle(particle)) // Blue
                .append(" ")
                .append(identifier)
                .append("\n");
    }

    /**
     * Saves a {@link ar.edu.itba.ss.airplane_boarding.models.Wall.WallState}
     * into the {@code data} {@link StringBuilder}.
     *
     * @param data       The {@link StringBuilder} that is collecting data.
     * @param wall       The {@link ar.edu.itba.ss.airplane_boarding.models.Wall.WallState} with the data.
     * @param identifier An integer id used to identify each particle
     *                   (this can be used to make sure that two outputted particles belong to the same wall)
     */
    private void saveWall(final StringBuilder data, final Wall.WallState wall, final int identifier) {
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
                .append(" ")
                .append(identifier)
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
                .append(" ")
                .append(identifier)
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
