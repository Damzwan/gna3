package gna;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;


import libpract.*;

/**
 * Implement the methods stitch, seam and floodfill.
 */
public class Stitcher {
    /**
     * Return the sequence of positions on the seam. The first position in the
     * sequence is (0, 0) and the last is (width - 1, height - 1). Each position
     * on the seam must be adjacent to its predecessor and successor (if any).
     * Positions that are diagonally adjacent are considered adjacent.
     * <p>
     * image1 and image2 are both non-null and have equal dimensions.
     * <p>
     * Remark: Here we use the default computer graphics coordinate system,
     * illustrated in the following image:
     * <p>
     * +-------------> X
     * |  +---+---+
     * |  | A | B |
     * |  +---+---+
     * |  | C | D |
     * |  +---+---+
     * Y v
     * <p>
     * The historical reasons behind using this layout is explained on the following
     * website: http://programarcadegames.com/index.php?chapter=introduction_to_graphics
     * <p>
     * Position (y, x) corresponds to the pixels image1[y][x] and image2[y][x]. This
     * convention also means that, when an autoant run -Dimg1=./images/zee1.png -Dimg2=./images/zee2.png -Doffsetx=318 -Doffsety=-7ated test mentioned that it used the array
     * {{A,B},{C,D}} as a test image, this corresponds to the image layout as shown in
     * the illustration above.
     */
    public List<Position> seam(int[][] image1, int[][] image2) {

        Comparator<Vertex> comparator = Comparator.comparingDouble(Vertex::getMinDist);
        PriorityQueue<Vertex> notVisited = new PriorityQueue<>(comparator);
        List<Vertex> visited = new ArrayList<>();

        notVisited.add(new Vertex(new Position(0, 0), null, image1[0][0], image2[0][0]));
        notVisited.peek().setMinDist(0);

        while (!notVisited.isEmpty()) {
            Vertex top = notVisited.remove();
            addNeighbors(top, image1, image2);
            visited.add(top);

            for (Vertex neighbor : top.getNeighbors()) {
                Vertex v = isAlreadyAdded(neighbor, notVisited, visited);
                if (v == null) {
                    notVisited.add(neighbor);
                    v  = neighbor;
                }
                if (v.getCost() + top.getMinDist() <= neighbor.getMinDist()) {
                    v.setMinDist(v.getCost() + top.getMinDist());
                    v.setPrev(top);
                }
            }
        }

        Vertex last = null;
        for (Vertex v : visited) {
            if (v.getLoc().equals(new Position(image1[0].length - 1, image1.length - 1))) {
                last = v;
                break;
            }
        }

        List<Position> line = new ArrayList<>();
        while (last != null) {
            line.add(0, last.getLoc());
            last = last.getPrev();
        }
        return line;
    }

    public boolean isNotOutOfBound(Position pos, int width, int height) {
        return pos.getX() <= width && pos.getY() <= height;
    }

    public void addNeighbors(Vertex vertex, int[][] image1, int[][] image2) {
        int x = vertex.getLoc().getX();
        int y = vertex.getLoc().getY();
        if (isNotOutOfBound(new Position(x + 1, y), image1[0].length - 1, image1.length - 1))
            vertex.addNeighbor(new Vertex(new Position(x + 1, y), vertex, image1[y][x + 1], image2[y][x + 1]));
        if (isNotOutOfBound(new Position(x, y + 1), image1[0].length - 1, image1.length - 1))
            vertex.addNeighbor(new Vertex(new Position(x, y + 1), vertex, image1[y + 1][x], image2[y + 1][x]));
        if (isNotOutOfBound(new Position(x + 1, y + 1), image1[0].length - 1, image1.length - 1))
            vertex.addNeighbor(new Vertex(new Position(x + 1, y + 1), vertex, image1[y + 1][x + 1], image2[y + 1][x + 1]));
    }

    public Vertex isAlreadyAdded(Vertex vertex, PriorityQueue<Vertex> queue, List<Vertex> lst) {
        for (Vertex v : queue) {
            if (vertex.getLoc().equals(v.getLoc())) {
                return v;
            }
        }
        for (Vertex v : lst) {
            if (vertex.getLoc().equals(v.getLoc())) {
                return v;
            }
        }
        return null;
    }

    /**
     * Apply the floodfill algorithm described in the assignment to mask. You can assume the mask
     * contains a seam from the upper left corner to the bottom right corner. The seam is represented
     * using Stitch.SEAM and all other positions contain the default value Stitch.EMPTY. So your
     * algorithm must replace all Stitch.EMPTY values with either Stitch.IMAGE1 or Stitch.IMAGE2.
     * <p>
     * Positions left to the seam should contain Stitch.IMAGE1, and those right to the seam
     * should contain Stitch.IMAGE2. You can run `ant test` for a basic (but not complete) test
     * to check whether your implementation does this properly.
     */
    public void floodfill(Stitch[][] mask) {
        boolean isRight = false;
        for (int i = 0; i < mask.length; i++) {
            for (int j = 0; j < mask[0].length; j++) {
                if (mask[i][j] == Stitch.SEAM) {
                    isRight = true;
                    if (mask[i][j] != mask[mask.length - 1][mask[0].length - 1])
                        j++;
                }
                if (isRight) mask[i][j] = Stitch.IMAGE2;
                else mask[i][j] = Stitch.IMAGE1;
            }
        }
    }

    /**
     * Return the mask to stitch two images together. The seam runs from the upper
     * left to the lower right corner, where in general the rightmost part comes from
     * the second image (but remember that the seam can be complex, see the spiral example
     * in the assignment). A pixel in the mask is Stitch.IMAGE1 on the places where
     * image1 should be used, and Stitch.IMAGE2 where image2 should be used. On the seam
     * record a value of Stitch.SEAM.
     * <p>
     * ImageCompositor will only call this method (not seam and floodfill) to
     * stitch two images.
     * <p>
     * image1 and image2 are both non-null and have equal dimensions.
     */
    public Stitch[][] stitch(int[][] image1, int[][] image2) {
        Stitch[][] mask = new Stitch[image1.length][image1[0].length];
        for (int i = 0; i < image1.length; i++) {
            for (int j = 0; j < image1[0].length; j++) {
                mask[i][j] = Stitch.EMPTY;
            }
        }
        List<Position> seamPositions = seam(image1, image2);
        for (Position position : seamPositions) {
            mask[position.getY()][position.getX()] = Stitch.SEAM;
        }
        floodfill(mask);
        return mask;
    }
}


