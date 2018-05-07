package gna;

import libpract.Position;

import java.util.ArrayList;
import java.util.List;

public class Vertex {
    private Position loc;
    private int cost;
    private int minDist;
    private Vertex prev;
    private List<Position> neighborPositions = new ArrayList<>();


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

    public List<Position> getNeighborPositions() {
        return neighborPositions;
    }

    public void addNeighbor(Position neighbor){
        neighborPositions.add(neighbor);
    }

    public void setNeighborPositions(List<Position> neighborPositions) {
        this.neighborPositions = neighborPositions;
    }

    @Override
    public String toString() {
        return "Vertex{" +
                "loc=" + loc +
                ", cost=" + cost +
                ", minDist=" + minDist +
                ", prev=" + prev +
                ", neighborPositions=" + neighborPositions +
                '}';
    }
}
