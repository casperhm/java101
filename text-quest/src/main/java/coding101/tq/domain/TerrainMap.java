package coding101.tq.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A map of terrain, as a 2D array of rows.
 */
public class TerrainMap {

    /** A coordinate pattern like {@code 1,2}. */
    public static final Pattern COORDINATE_REGEX = Pattern.compile("(\\d+)\\s*,\\s*(\\d+)");

    /** Metadata key for a starting coordinate in the form X,Y. */
    public static final String START_META = "start";

    private final String name;
    private final Map<String, String> metadata;

    // following NOT final to support changes via modifyAt()... can happen after map
    // has expanded but save file was in old (smaller) map
    private int width;
    private int height;
    private TerrainType[][] terrain;

    /**
     * Constructor.
     *
     * @param name     the map name
     * @param terrain  the terrain
     * @param metadata the metadata
     * @throws IllegalArgumentException if any argument is {@litearl null}
     */
    @JsonCreator
    public TerrainMap(
            @JsonProperty("name") String name,
            @JsonProperty("terrain") TerrainType[][] terrain,
            @JsonProperty("metadata") Map<String, String> metadata) {
        super();
        this.name = Objects.requireNonNull(name);
        this.terrain = Objects.requireNonNull(terrain);
        this.metadata = Collections.unmodifiableMap(Objects.requireNonNull(metadata));
        if (terrain.length < 1 || terrain[0] == null || terrain[0].length < 1) {
            throw new IllegalArgumentException("Invalid terrain array: must have at least 1 non-empty element.");
        }
        this.width = terrain[0].length;
        this.height = terrain.length;
    }

    /**
     * Test if a player can move to a specific coordinate on this map.
     *
     * @param x the X coordinate to test
     * @param y the Y coordinate to test
     * @return {@code true} if the player is allowed to move to the given coordinate
     */
    public boolean canPlayerMoveTo(int x, int y) {
        // TODO: validate player can move to specified coordinate
        return true;
    }

    /**
     * Get the map name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the terrain width.
     *
     * @return the width
     */
    public final int width() {
        return width;
    }

    /**
     * Get the terrain height.
     *
     * @return the height.
     */
    public final int height() {
        return height;
    }

    /**
     * Get the metadata.
     *
     * @return the metadata
     */
    @JsonGetter(value = "metadata")
    public Map<String, String> metadata() {
        return metadata;
    }

    /**
     * Get the terrain.
     *
     * @return the terrain
     */
    @JsonGetter(value = "terrain")
    public TerrainType[][] terrain() {
        return terrain;
    }

    /**
     * Get the starting coordinate for the map.
     *
     * This will look for the {@code start} metadata value, which is expected to be
     * a 0-based coordinate in the form {@code X,Y}. If not available, then a
     * default coordinate of {@code 9,9} will be returned.
     *
     * @return the starting coordinate
     */
    public Coordinate startingCoordinate() {
        String start = metadata.get(START_META);
        if (start != null) {
            Matcher m = COORDINATE_REGEX.matcher(start);
            if (m.find()) {
                return new Coordinate(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
            }
        }
        return new Coordinate(9, 9);
    }

    /**
     * Get the terrain type at a specific coordinate.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the terrain type, or {@link TerrainType#Empty} if {@code x} or
     *         {@code y} are out of bounds
     */
    public final TerrainType terrainAt(int x, int y) {
        if (x >= width || y >= height || x < 0 || y < 0) {
            return TerrainType.Empty;
        }
        return terrain[y][x];
    }

    /**
     * Modify the terrain type at a specific coordinate.
     *
     * @param x    the x coordinate
     * @param y    the y coordinate
     * @param type the type to set
     * @returns {@code true} if the terrain was changed
     */
    public final boolean modifyAt(int x, int y, TerrainType type) {
        if (y >= height || x >= width) {
            // expand rows or width with copy to new 2D array
            final int newWidth = Math.max(x + 1, width);
            TerrainType[][] newTerrain = new TerrainType[y + 1][];
            for (int row = 0, maxRow = terrain.length; row < maxRow; row++) {
                TerrainType[] newRow = new TerrainType[newWidth];
                System.arraycopy(terrain[row], 0, newRow, 0, terrain[row].length);
                newTerrain[row] = newRow;
            }
            for (int row = terrain.length, maxRow = y + 1; row < maxRow; row++) {
                newTerrain[row] = new TerrainType[newWidth];
            }
            terrain = newTerrain;
            width = newWidth;
            height = y + 1;
        }
        if (terrain[y][x] != type) {
            terrain[y][x] = type;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "TerrainMap{width=" + width + ", height=" + height + "}";
    }

    /**
     * Consume a terrain coordinate.
     */
    @FunctionalInterface
    public static interface TerrainConsumer {

        /**
         * Process a single terrain coordinate.
         *
         * @param x    the x coordinate
         * @param y    the y coordinate
         * @param type the terrain type
         */
        void accept(int x, int y, TerrainType type);
    }

    /**
     * Walk a quadrant of the terrain.
     *
     * This method will walk all coordinates in a given quadrant, that is defined by
     * the top-left (x,y) origin and a width and height. Coordinates that are out of
     * bounds of the map data will still be passed to {@code out}, with a
     * {@code null} value.
     *
     * @param x      the x origin
     * @param y      the y origin
     * @param width  the quadrant width
     * @param height the quadrant height
     * @param out    the destination
     */
    public void walk(int x, int y, int width, int height, TerrainConsumer out) {
        int maxCol = x + width;
        int maxRow = y + height;
        for (int row = y; row < maxRow; row++) {
            for (int col = x; col < maxCol; col++) {
                TerrainType[] r = row < terrain.length ? terrain[row] : null;
                TerrainType t = r != null && col < r.length ? r[col] : null;
                out.accept(col, row, t);
            }
        }
    }

    /**
     * Walk the area immediately surrounding a point, skipping the point itself.
     *
     * @param x   the x origin
     * @param y   the y origin
     * @param out the destination
     */
    public void walkSurrounding(int x, int y, TerrainConsumer out) {
        for (int row = Math.max(0, y - 1), maxRow = Math.min(height - 1, y + 1); row <= maxRow; row++) {
            for (int col = Math.max(0, x - 1), maxCol = Math.min(width - 1, x + 1); col <= maxCol; col++) {
                if (col == x && row == y) {
                    continue;
                }
                TerrainType[] r = row < terrain.length ? terrain[row] : null;
                TerrainType t = r != null && col < r.length ? r[col] : null;
                out.accept(col, row, t);
            }
        }
    }

    /**
     * Render the complete map to a string.
     *
     * @return the map rendered as as string
     * @throws RuntimeException if there is any problem rendering
     */
    public String render() {
        return render(0, 0, width, height);
    }

    /**
     * Render a quadrant as a string.
     *
     * @param x      the x origin
     * @param y      the y origin
     * @param width  the quadrant width
     * @param height the quadrant height
     * @return the quadrant rendered as a string
     * @throws RuntimeException if there is any problem rendering
     */
    public String render(int x, int y, int width, int height) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(width * height)) {
            walk(x, y, width, height, new TerrainPrinter(out));
            return out.toString(StandardCharsets.US_ASCII);
        } catch (IOException e) {
            throw new RuntimeException("Error rendering quadrant: %s".formatted(e.getMessage()), e);
        }
    }

    /**
     * A {@link TerrainConsumer} that prints the terrain to an output stream.
     *
     * Rows are delimited with a newline character.
     */
    public static final class TerrainPrinter implements TerrainConsumer {

        private final OutputStream out;
        int lastRow = -1;

        /**
         * Constructor.
         *
         * @param out the output stream
         * @throws IllegalArgumentException if any argument is {@literal null}
         */
        public TerrainPrinter(OutputStream out) {
            super();
            this.out = Objects.requireNonNull(out);
        }

        @Override
        public void accept(int x, int y, TerrainType type) {
            try {
                if (lastRow < 0) {
                    lastRow = y;
                } else if (y > lastRow) {
                    out.write('\n');
                    lastRow = y;
                }
                out.write(type != null ? type.getKey() : TerrainType.EMPTY);
            } catch (IOException e) {
                throw new RuntimeException("Error writing to output stream: %s".formatted(e.getMessage()), e);
            }
        }
    }
}
