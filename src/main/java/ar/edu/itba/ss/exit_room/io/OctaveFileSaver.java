package ar.edu.itba.ss.exit_room.io;

import ar.edu.itba.ss.exit_room.models.Room;
import ar.edu.itba.ss.g7.engine.io.TextFileSaver;

import java.io.IOException;
import java.io.Writer;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * A {@link ar.edu.itba.ss.g7.engine.io.DataSaver} that outputs stuff to be analyzed with Octave.
 */
public class OctaveFileSaver extends TextFileSaver<Room.RoomState> {

    /**
     * The simulation duration.
     */
    private final double duration;

    /**
     * The simulation time step.
     */
    private final double timeStep;


    /**
     * Constructor
     *
     * @param filePath Path to the file to be saved.
     * @param duration The simulation duration.
     * @param timeStep The simulation time step.
     */
    public OctaveFileSaver(final String filePath, final double duration, final double timeStep) {
        super(filePath);
        this.duration = duration;
        this.timeStep = timeStep;
    }

    @Override
    public void doSave(final Writer writer, final Queue<Room.RoomState> queue) throws IOException {
        // Save amount of particles that left the room in each time step.
        final String left = "left = [" + queue.stream()
                .map(Room.RoomState::getNewOutside)
                .map(Object::toString)
                .collect(Collectors.joining(", ")) + "];";

        final String duration = "duration = " + this.duration + ";";
        final String timeStep = "dt = " + this.timeStep + ";";
        final String time = "t = 0:dt:duration;";

        // Append results into the Writer
        writer.append(left)
                .append("\n")
                .append(duration)
                .append("\n")
                .append(timeStep)
                .append("\n")
                .append(time)
                .append("\n");
    }
}
