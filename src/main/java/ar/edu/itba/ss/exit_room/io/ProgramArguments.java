package ar.edu.itba.ss.exit_room.io;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Class containing the program arguments.
 */
@Component
public class ProgramArguments {

    /**
     * The simulation's duration.
     */
    private final double duration;

    /**
     * The particles' stuff.
     */
    private final ParticleProperties particleProperties;

    /**
     * The room's stuff.
     */
    private final RoomProperties roomProperties;

    /**
     * The output stuff.
     */
    private final OutputStuff outputStuff;

    /**
     * Constructor.
     *
     * @param duration           The simulation's duration.
     * @param particleProperties The particles' stuff.
     * @param roomProperties     The room's stuff.
     * @param outputStuff        The output stuff.
     */
    @Autowired
    public ProgramArguments(@Value("${custom.simulation.duration}") double duration,
                            final ParticleProperties particleProperties,
                            final RoomProperties roomProperties,
                            final OutputStuff outputStuff) {
        this.duration = duration;
        this.particleProperties = particleProperties;
        this.roomProperties = roomProperties;
        this.outputStuff = outputStuff;
    }

    /**
     * @return The simulation's duration.
     */
    public double getDuration() {
        return duration;
    }

    /**
     * @return The particles' stuff.
     */
    public ParticleProperties getParticleProperties() {
        return particleProperties;
    }

    /**
     * @return The room's stuff.
     */
    public RoomProperties getRoomProperties() {
        return roomProperties;
    }

    /**
     * @return The output stuff.
     */
    public OutputStuff getOutputStuff() {
        return outputStuff;
    }

    /**
     * Particles' stuff.
     */
    @Component
    public static final class ParticleProperties {

        /**
         * The max. amount of particles.
         */
        private final int maxAmount;

        /**
         * The min. diameter for a particle.
         */
        private final double minRadius;

        /**
         * The max. diameter for a particle.
         */
        private final double maxRadius;

        /**
         * Constructor.
         *
         * @param maxAmount The max. amount of particles.
         * @param minRadius The min. diameter for a particle.
         * @param maxRadius The max. diameter for a particle.
         */
        @Autowired
        public ParticleProperties(@Value("${custom.system.particle.max-amount}") final int maxAmount,
                                  @Value("${custom.system.particle.min-radius}") final double minRadius,
                                  @Value("${custom.system.particle.max-radius}") final double maxRadius) {
            this.maxAmount = maxAmount;
            this.minRadius = minRadius;
            this.maxRadius = maxRadius;
        }

        /**
         * @return The max. amount of particles.
         */
        public int getMaxAmount() {
            return maxAmount;
        }

        /**
         * @return The min. diameter for a particle.
         */
        public double getMinRadius() {
            return minRadius;
        }

        /**
         * @return The max. diameter for a particle.
         */
        public double getMaxRadius() {
            return maxRadius;
        }
    }

    /**
     * Room shape's stuff.
     */
    @Component
    public static final class RoomProperties {

        /**
         * The room's length.
         */
        private final double roomLength;

        /**
         * The room's width.
         */
        private final double roomWidth;

        /**
         * The room's hole size.
         */
        private final double roomHoleSize;

        /**
         * Constructor.
         *
         * @param roomLength   The room's length.
         * @param roomWidth    The room's width.
         * @param roomHoleSize The room's hole size.
         */
        @Autowired
        public RoomProperties(@Value("${custom.system.room.L}") final double roomLength,
                              @Value("${custom.system.room.W}") final double roomWidth,
                              @Value("${custom.system.room.D}") final double roomHoleSize) {
            this.roomLength = roomLength;
            this.roomWidth = roomWidth;
            this.roomHoleSize = roomHoleSize;
        }

        /**
         * @return The room's length.
         */
        public double getRoomLength() {
            return roomLength;
        }

        /**
         * @return The room's width.
         */
        public double getRoomWidth() {
            return roomWidth;
        }

        /**
         * @return The room's hole size.
         */
        public double getRoomHoleSize() {
            return roomHoleSize;
        }
    }

    /**
     * Output stuff.
     */
    @Component
    public static final class OutputStuff {

        /**
         * Path for the Ovito file.
         */
        private final String ovitoFilePath;

        /**
         * @param ovitoFilePath Path for the Ovito file.
         */
        @Autowired
        public OutputStuff(@Value("${custom.output.ovito}") final String ovitoFilePath) {
            this.ovitoFilePath = ovitoFilePath;
        }

        /**
         * @return Path for the Ovito file.
         */
        public String getOvitoFilePath() {
            return ovitoFilePath;
        }
    }
}
