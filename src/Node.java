import java.util.ArrayList;
import java.util.HashMap;

public class Node {

    private ArrayList<Node> children = new ArrayList<>();
    private HashMap<Node, Object> parents = new HashMap<>();

    public void setChildren(ArrayList<Node> children) {
        this.children = children;
    }

    public ArrayList<Node> getChildren() {
        return children;
    }

    public void setParents(HashMap<Node, Object> parents) {
        this.parents = parents;
    }

    public HashMap<Node, Object> getParents() {
        return parents;
    }
}
