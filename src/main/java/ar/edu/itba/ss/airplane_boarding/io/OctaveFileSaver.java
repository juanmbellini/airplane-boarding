package ar.edu.itba.ss.airplane_boarding.io;

import ar.edu.itba.ss.airplane_boarding.models.BoardingScene;
import ar.edu.itba.ss.g7.engine.io.TextFileSaver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Writer;
import java.util.Queue;

/**
 * A {@link ar.edu.itba.ss.g7.engine.io.DataSaver} that outputs stuff to be analyzed with Octave.
 */
@Component("octaveFileSaver")
public class OctaveFileSaver extends TextFileSaver<BoardingScene.BoardingSceneState> {

//    /**
//     * The simulation duration.
//     */
//    private final double duration;

    /**
     * The simulation time step.
     */
    private final double timeStep;


    /**
     * Constructor
     *
     * @param filePath Path to the file to be saved.
     *                 //     * @param duration The simulation duration.
     * @param timeStep The simulation time step.
     */
    public OctaveFileSaver(@Value("${custom.output.octave}") final String filePath,
//                           final double duration,
                           @Value("${custom.simulation.time-step}") final double timeStep) {
        super(filePath);
//        this.duration = duration;
        this.timeStep = timeStep;
    }

    @Override
    public void doSave(final Writer writer, final Queue<BoardingScene.BoardingSceneState> queue) throws IOException {
        // Save amount of particles that left the room in each time step.
//        final String left = "left = [" + queue.stream()
//                .map(Room.RoomState::getNewOutside)
//                .map(Object::toString)
//                .collect(Collectors.joining(", ")) + "];";

//        final String duration = "duration = " + this.duration + ";";
        final String timeStep = "dt = " + this.timeStep + ";";
//        final String time = "t = 0:dt:duration;";

        // Append results into the Writer
        writer
//                .append(left)
//                .append("\n")
//                .append(duration)
//                .append("\n")
                .append(timeStep)
                .append("\n")
//                .append(time)
//                .append("\n");
        ;
    }
}
