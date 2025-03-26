package ch.zhaw.it.pm2.racetrack;

import java.util.Optional;

/**
 * Represents the possible space types of the racetrack grid.
 * The 'spaceChar' property is used to parse the track file and represents the space type
 * in the text representation of the Track. The character mapping is as follows:
 * - WALL: '#' represents a road boundary or off track space.
 * - TRACK: ' ' represents a road or open track space.
 * - FINISH_LEFT: '<' indicates a finish line space to be crossed leftwards.
 * - FINISH_RIGHT: '>' indicates a finish line space to be crossed rightwards.
 * - FINISH_UP: '^' indicates a finish line space to be crossed upwards.
 * - FINISH_DOWN: 'v' indicates a finish line space to be crossed downwards.
 */
public enum SpaceType {
    WALL('#'),
    TRACK(' '),
    FINISH_LEFT('<'),
    FINISH_RIGHT('>'),
    FINISH_UP('^'),
    FINISH_DOWN('v');

    private final char spaceChar;

    SpaceType(final char spaceChar) {
        this.spaceChar = spaceChar;
    }

    /**
     * Returns the character representing this space type.
     *
     * @return the character associated with the space type.
     */
    public char getSpaceChar() {
        return spaceChar;
    }

    /**
     * Returns an Optional containing the matching SpaceType for the provided character.
     *
     * @param spaceChar the character to match against the space types.
     * @return an Optional with the matching SpaceType, or an empty Optional if no match is found.
     */
    public static Optional<SpaceType> ofChar(char spaceChar) {
        for (SpaceType type : SpaceType.values()) {
            if (type.spaceChar == spaceChar) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }
}
