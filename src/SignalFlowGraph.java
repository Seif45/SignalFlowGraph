import java.util.ArrayList;

public class SignalFlowGraph {

    private Node inputNode;
    private Node outputNode;

    public SignalFlowGraph(Node inputNode, Node outputNode){
        this.inputNode = inputNode;
        this.outputNode = outputNode;
    }

    public Node getInputNode() {
        return inputNode;
    }

    public Node getOutputNode() {
        return outputNode;
    }

    public ArrayList<Node> getNodes(){
        ArrayList<Node> nodes = new ArrayList<>();
        traverseNodes(inputNode, nodes);
        return nodes;
    }

    private void traverseNodes(Node node, ArrayList<Node> nodes){
        ArrayList<Node> children = node.getChildren();
        if(children != null && !nodes.contains(node)){
            for (Node child : children){
                nodes.add(node);
                traverseNodes(child, nodes);
            }
        }
    }
}
