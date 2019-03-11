package net.runelite.client.plugins.templetrekking;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import javax.inject.Inject;
import java.awt.*;

public class TempleTrekkingOverlay extends Overlay
{
    private static final Logger logger = LoggerFactory.getLogger(TempleTrekkingPlugin.class);
    private final Client client;
    private final TempleTrekkingPlugin plugin;
    private final TempleTrekkingConfig config;

    private static final Font FONT = FontManager.getRunescapeFont().deriveFont(Font.BOLD, 16);
    private static final Color TILE_COLOR = new Color(13, 179, 227);
    private static final Color GRAY = new Color(104, 109, 104);
    private static final int MAX_DISTANCE = 2400;
    private static final int UP = 0;
    private static final int DOWN = 1;
    private static final int RIGHT = 3;

    private static final int xOffset = 47;
    private static final int yOffset = 53;
    ArrayList<int[]> path = new ArrayList<>();

    @Inject
    TempleTrekkingOverlay(Client client, TempleTrekkingPlugin plugin, TempleTrekkingConfig config) {
        super(plugin);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        graphics.setFont(FONT);
        renderBogTiles(graphics);
        return null;
    }

    private boolean findAndSetSuccessfulPath(int[][] grid) {
        boolean foundPath = false;
        for (int i = 0; i < grid.length; ++i) {
            foundPath = canTileGetToTheEnd(i, 0, -1, grid) || foundPath;
        }

        return foundPath;
    }

    private boolean canTileGetToTheEnd(int x, int y, int direction, int[][] grid) {
        // Validate bounds.
        if (x < 0 || y < 0 || x >= grid.length || y >= grid[0].length) {
            return false;
        }

        int tile = grid[x][y];
        int endTileColomn = grid[x].length - 1;

        // Hard bog tile.
        if (tile == 1) {
            if (y == endTileColomn) {
                int[] tileCoords = {x, y};
                path.add(tileCoords);
                return true;
            }

            // We can't come from the left, so no need to check that here.
            boolean right = canTileGetToTheEnd(x, y + 1, RIGHT, grid);
            if (right) {
                int[] tileCoords = {x, y};
                path.add(tileCoords);
                return true;
            }

            if (direction != DOWN) {
                boolean up = canTileGetToTheEnd(x - 1, y, UP, grid);
                if (up) {
                    int[] tileCoords = {x, y};
                    path.add(tileCoords);
                    return true;
                }
            }

            if (direction != UP) {
                boolean down = canTileGetToTheEnd(x + 1, y, DOWN, grid);
                if (down) {
                    int[] tileCoords = {x, y};
                    path.add(tileCoords);
                    return true;
                }
            }

            // No valid paths / dead-end.
            return false;
        }

        // Soft tile.
        return false;
    }

    public void renderBogTiles(Graphics2D graphics) {
        Scene scene = client.getScene();
        Tile[][][] tiles = scene.getTiles();

        int[][] bogTiles2D = generateBogTiles2D();

        // Search the bog tiles for a path.
        boolean foundAndSetPath = findAndSetSuccessfulPath(bogTiles2D);

        Player player = client.getLocalPlayer();
        int z = client.getPlane();

        // Mark the bog tiles with the valid path.
        if (foundAndSetPath) {
            for (int[] tuple : path) {
                bogTiles2D[tuple[0]][tuple[1]] = 2;
            }
        }

        for (int x = 0; x < bogTiles2D.length; ++x) {
            for (int y = 0; y < bogTiles2D[0].length; ++y) {
                Tile tile = tiles[z][x + xOffset][y + yOffset];
                if (tile == null || player == null) {
                    continue;
                }
                if (bogTiles2D[x][y] == 2) {
                    renderGroundObject(graphics, tile, player, TILE_COLOR);
                } else {
                    // If for some reason we weren't able to find a path, just draw all hard bog tiles.
                    renderGroundObject(graphics, tile, player, GRAY);
                }
            }
        }

        // Reset the path.
        path.clear();
    }

    public int[][] generateBogTiles2D() {
        Scene scene = client.getScene();
        Tile[][][] tiles = scene.getTiles();

        // 2D bog tiles collection reflects the bog set up, where 0 represents
        // a soft tile and a 1 represents a hard tile.
        //
        // For example:
        //
        // [ [ 1, 1, 1, 0, 0 ],
        //   [ 0, 1, 0, 1, 1 ]
        //   [ 1, 0, 0, 0, 0 ],
        //   [ 1, 0, 0, 1, 1 ],
        //   [ 0, 1, 1, 1, 0 ],,
        //   [ 0, 1, 1, 1, 0 ],
        //   [ 1, 1, 0, 1, 0 ]
        //   [ 0, 1, 1, 1, 1 ],
        //   [ 0, 0, 0, 0, 0 ],
        //   [ 0, 0, 0, 0, 0 ] ]

        int[][] bogTiles2D = new int[10][5];
        Player player = client.getLocalPlayer();
        int z = client.getPlane();

        for (int x = 0; x < Constants.SCENE_SIZE; ++x) {
            for (int y = 0; y < Constants.SCENE_SIZE; ++y) {
                Tile tile = tiles[z][x][y];
                if (tile == null || player == null) {
                    continue;
                }
                if (isHardBogTile(tile, player)) {
                    bogTiles2D[x - xOffset][y - yOffset] = 1;
                } else if (isSoftBogTile(tile, player)) {
                    bogTiles2D[x - xOffset][y - yOffset] = 0;
                }
            }
        }

        return bogTiles2D;
    }

    public boolean isHardBogTile(Tile tile, Player player) {
        GroundObject groundObject = tile.getGroundObject();
        if (groundObject != null) {
            if (player.getLocalLocation().distanceTo(groundObject.getLocalLocation()) <= MAX_DISTANCE) {
                if (groundObject.getId() == 13838) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isSoftBogTile(Tile tile, Player player) {
        GroundObject groundObject = tile.getGroundObject();
        if (groundObject != null) {
            if (player.getLocalLocation().distanceTo(groundObject.getLocalLocation()) <= MAX_DISTANCE) {
                if (groundObject.getId() == 13839) {
                    return true;
                }
            }
        }
        return false;
    }

    public void renderGroundObject(Graphics2D graphics, Tile tile, Player player, Color color) {
        GroundObject groundObject = tile.getGroundObject();
        if (groundObject != null) {
            Polygon polygon = groundObject.getCanvasTilePoly();
            if (player.getLocalLocation().distanceTo(groundObject.getLocalLocation()) <= MAX_DISTANCE) {
                if (groundObject.getId() == 13838 && polygon != null) {
                    OverlayUtil.renderPolygon(graphics, polygon, color);
                }
            }
        }
    }
}