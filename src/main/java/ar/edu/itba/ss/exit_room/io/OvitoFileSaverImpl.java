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
     * Constructor.
     *
     * @param filePath Path to the file to be saved.
     */
    public OvitoFileSaverImpl(String filePath) {
        super(filePath);
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
    private static void saveParticle(final StringBuilder data, Particle.ParticleState particle) {
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
                .append(1) // Red
                .append(" ")
                .append(1) // Green
                .append(" ")
                .append(1) // Blue
                .append("\n");
    }

    /**
     * Saves a {@link ar.edu.itba.ss.exit_room.models.Wall.WallState}
     * into the {@code data} {@link StringBuilder}.
     *
     * @param data The {@link StringBuilder} that is collecting data.
     * @param wall The {@link ar.edu.itba.ss.exit_room.models.Wall.WallState} with the data.
     */
    private static void saveWall(final StringBuilder data, final Wall.WallState wall) {
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
                .append(0) // Green
                .append(" ")
                .append(0) // Blue
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
                .append(0) // Green
                .append(" ")
                .append(0) // Blue
                .append("\n");
    }
}
