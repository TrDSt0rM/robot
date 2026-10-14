public class Terrain implements BoardElement {

    private final String type;

    public Terrain(String type) {
        this.type = type;
    }

    public Terrain() {
        this.type = "Plaines";
    }

    public String getType() {
        return type;
    }
}
