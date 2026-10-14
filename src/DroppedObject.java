public class DroppedObject implements BoardElement{

    private final String name;

    public DroppedObject(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
