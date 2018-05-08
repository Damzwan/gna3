package gna;

import java.util.*;


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
        int width = image1[0].length;
        int height = image1.length;

        Comparator<Vertex> comparator = Comparator.comparingInt(Vertex::getMinDist);
        PriorityQueue<Vertex> toVisit = new PriorityQueue<>(comparator);
        List<Vertex> visited = new ArrayList<>();

        Vertex top = new Vertex(new Position(0, 0), null, image1[0][0], image2[0][0]);
        toVisit.add(top);
        top.setMinDist(0);

        Position target = new Position(width-1, height-1);

        while (! top.getLoc().equals(target)) {
            top = toVisit.remove();
            addNeighboringPositions(top, width, height);
            visited.add(top);

            for (Position neighbor : top.getNeighborPositions()) {
                //Check whether the given neighbor location already exists in the list of visited items/queue.
                //If there is no vertex with this position we add a new vertex to the queue, otherwise we update the minimum distance of the existing vertex if necessary
                Vertex v = isAlreadyAdded(neighbor, toVisit, visited);
                if (v == null) {
                    v = new Vertex(neighbor, top, image1[neighbor.getY()][neighbor.getX()], image2[neighbor.getY()][neighbor.getX()]);
                    toVisit.add(v);
                }
                if (v.getCost() + top.getMinDist() < v.getMinDist()) {
                    v.setMinDist(v.getCost() + top.getMinDist());
                    if (toVisit.remove(v))
                        toVisit.add(v);
                    v.setPrev(top);
                }
            }
        }

        //return a list of the path from the upper left to the lower right
        List<Position> line = new ArrayList<>();
        while (top != null) {
            line.add(0, top.getLoc());
            top = top.getPrev();
        }
        return line;
    }

    public boolean isNotOutOfBound(Position pos, int width, int height) {
        return pos.getX() < width && pos.getX() >= 0 && pos.getY() < height && pos.getY() >= 0;
    }

    public void addNeighboringPositions(Vertex vertex, int width, int height) {
        int x = vertex.getLoc().getX();
        int y = vertex.getLoc().getY();

        for (int nx = x-1; nx <= x+1; nx++) {
            for (int ny = y-1; ny <= y+1; ny++) {
                if (nx != x || ny != y) {
                    Position pos = new Position(nx, ny);
                    if (isNotOutOfBound(pos, width, height)) {
                        vertex.addNeighbor(pos);
                    }
                }
            }
        }
    }

    public Vertex isAlreadyAdded(Position pos, PriorityQueue<Vertex> queue, List<Vertex> lst) {
        for (Vertex v : queue) {
            if (pos.equals(v.getLoc())) {
                return v;
            }
        }
        for (Vertex v : lst) {
            if (pos.equals(v.getLoc())) {
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

        Stack<Position> stack = new Stack<>();
        Position start = new Position(0, 1);
        stack.add(start);

        while (!stack.isEmpty()){
            Position curr = stack.pop();
            color(mask, curr);

            Position pos = new Position(curr.getX() + 1, curr.getY());
            if (canColor(mask, pos)) stack.add(pos);
            pos = new Position(curr.getX() - 1, curr.getY());
            if (canColor(mask, pos)) stack.add(pos);
            pos = new Position(curr.getX(), curr.getY() + 1);
            if (canColor(mask, pos)) stack.add(pos);
            pos = new Position(curr.getX(), curr.getY() - 1);
            if (canColor(mask, pos)) stack.add(pos);
        }
        for (int i = 0; i < mask.length; i++) {
            for (int j = 0; j < mask.length; j++) {
                if (mask[i][j] == Stitch.EMPTY) mask[i][j] = Stitch.IMAGE2;
            }
        }
    }

    public boolean canColor(Stitch[][] mask, Position pos) {
        return isNotOutOfBound(pos, mask[0].length, mask.length) && mask[pos.getY()][pos.getX()] == Stitch.EMPTY;
    }

    public void color(Stitch[][] mask, Position pos){
        mask[pos.getY()][pos.getX()] = Stitch.IMAGE1;
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


