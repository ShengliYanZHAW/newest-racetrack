package ch.zhaw.it.pm2.racetrack;

import java.util.Optional;

/**
 * Represents possible space types of the racetrack grid. <b>(This shall not be altered!)</b>
 * <p>The property {@link SpaceType#spaceChar} is used to parse from the track file and represents
 * the {@link SpaceType} in the text representation of the {@link Track}
 * created by {@link Track#toString()}.</p>
 * The mapping of the Characters is as follows:
 *  <ul>
 *    <li>WALL = '#' : road boundary or off track space</li>
 *    <li>TRACK = ' ' : road or open track space</li>
 *    <li>FINISH_LEFT  = '&lt;' : finish line spaces which have to be crossed leftwards</li>
 *    <li>FINISH_RIGHT = '&gt;' : finish line spaces which have to be crossed rightwards</li>
 *    <li>FINISH_UP    = '^'    : finish line spaces which have to be crossed upwards</li>
 *    <li>FINISH_DOWN  = 'v'    : finish line spaces which have to be crossed downwards</li>
 * </ul>
 */
public enum SpaceType {
    /** road boundary or off track space. */
    WALL('#'),
    /** road or open track space. */
    TRACK(' '),
    /** finish line space which have to be crossed leftwards. */
    FINISH_LEFT('<'),
    /** finish line space which have to be crossed rightwards. */
    FINISH_RIGHT('>'),
    /** finish line space which have to be crossed upwards. */
    FINISH_UP('^'),
    /** finish line space which have to be crossed downwards. */
    FINISH_DOWN('v');

    /**
     * Character representation of the {@link SpaceType} in the track file and printout.
     */
    private final char spaceChar;

    SpaceType(final char spaceChar) {
        this.spaceChar = spaceChar;
    }

    /**
     * Get spaceChar representing this {@link SpaceType}.
     * @return spaceChar representing this {@link SpaceType}
     */
    public char getSpaceChar() {
        return spaceChar;
    }

    /**
     * Detects the matching {@link SpaceType} for the provided character.
     * @param spaceChar char value to return the matching {@link SpaceType} for
     * @return Optional&lt;SpaceType&gt; matching the spaceChar, Optional&lt;SpaceType&gt;#empty()
     */
    public static Optional<SpaceType> ofChar(char spaceChar) {
        for (SpaceType type : SpaceType.values()) {
            if (type.spaceChar == spaceChar) return Optional.of(type);
        }
        return Optional.empty();
        // alternative version using functional streams
        // return Arrays.stream(SpaceType.values()).filter(type -> type.spaceChar == spaceChar).findFirst();
    }
}
