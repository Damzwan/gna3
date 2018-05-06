package gna;

import libpract.Position;

import java.util.ArrayList;
import java.util.List;

public class Vertex {
    private Position loc;
    private int cost;
    private int minDist;
    private Vertex prev;
    private List<Vertex> neighbors = new ArrayList<>();


    public Vertex(Position loc, Vertex prev, int pixel1, int pixel2) {
        this.loc = loc;
        this.cost = ImageCompositor.pixelSqDistance(pixel1, pixel2);
        this.minDist = Integer.MAX_VALUE;
        this.prev = prev;
    }

    public Position getLoc() {
        return loc;
    }

    public void setLoc(Position loc) {
        this.loc = loc;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getMinDist() {
        return minDist;
    }

    public void setMinDist(int minDist) {
        this.minDist = minDist;
    }

    public Vertex getPrev() {
        return prev;
    }

    public void setPrev(Vertex prev) {
        this.prev = prev;
    }

    public List<Vertex> getNeighbors() {
        return neighbors;
    }

    public void addNeighbor(Vertex neighor){
        neighbors.add(neighor);
    }
}
