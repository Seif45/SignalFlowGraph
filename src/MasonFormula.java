import java.util.ArrayList;

public class MasonFormula {

    private SignalFlowGraph sfg;
    private ArrayList<ArrayList<Node>> totalForwardPathNode = new ArrayList<>();
    private ArrayList<DataType> totalForwardPathGain = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<Node>>> totalLoopNode = new ArrayList<>();
    private ArrayList<ArrayList<DataType>> totalLoopGain = new ArrayList<>();
    private ArrayList<DataType> delta = new ArrayList<>();
    private ArrayList<DataType> finalTerms = new ArrayList<>();

    public MasonFormula(SignalFlowGraph sfg){
        this.sfg = sfg;
        calculateForwardPaths(sfg.getInputNode(), sfg.getInputNode(), new ArrayList<>(), new ArrayList<>());

        for (Node node : sfg.getNodes()){
            calculateLoops(node, node, new ArrayList<>(), new ArrayList<>());
        }

        for (int i = 0; i < totalLoopNode.size(); i++){
            checkUntouched(totalLoopNode.get(i));
        }

        calculateDelta(totalLoopGain);
        checkUntouchedLoopPath();

        for (int i = 0 ; i < totalForwardPathGain.size(); i++){
            ArrayList<DataType> terms = new ArrayList<>();
            terms.add(delta.get(i+1));
            terms.add(totalForwardPathGain.get(i));
            calculateUntouchedGain(terms, finalTerms);
        }
    }

    private void calculateForwardPaths(Node node, Node parent, ArrayList<Node> forwardPathNode, ArrayList<Object> forwardPathGain){
        ArrayList<Node> children = node.getChildren();
        if (children != null && !forwardPathNode.contains(node)){
            forwardPathNode.add(node);
            if(node != sfg.getInputNode()){
                forwardPathGain.add(node.getParents().get(parent));
            }
            for(Node child : children){
                calculateForwardPaths(child, node, forwardPathNode, forwardPathGain);
            }
            forwardPathNode.remove(node);
            if(node != sfg.getInputNode()){
                forwardPathGain.remove(node.getParents().get(parent));
            }
        }
        if(node == sfg.getOutputNode()){
            forwardPathGain.add(node.getParents().get(parent));
            calculateGain(forwardPathGain, totalForwardPathGain);
            forwardPathGain.remove(node.getParents().get(parent));
            totalForwardPathNode.add(new ArrayList<>(forwardPathNode));
        }
    }

    private void calculateLoops(Node node, Node parent, ArrayList<Node> loopNode, ArrayList<Object> loopGain){
        ArrayList<Node> children = node.getChildren();
        if (children != null && !loopNode.contains(node)){
            loopNode.add(node);
            if (!loopNode.isEmpty() && node != loopNode.get(0)){
                loopGain.add(node.getParents().get(parent));
            }
            for(Node child : children){
                calculateLoops(child, node, loopNode, loopGain);
            }
            loopNode.remove(node);
            if (!loopNode.isEmpty() && node != loopNode.get(0)){
                loopGain.remove(node.getParents().get(parent));
            }
        }
        if (!loopNode.isEmpty() && node == loopNode.get(0)){
            if(totalLoopNode.size() == 0 || checkRepeated(totalLoopNode.get(0), loopNode)){
                if (totalLoopNode.isEmpty()){
                    totalLoopNode.add(new ArrayList<>());
                }
                loopGain.add(node.getParents().get(parent));
                if(totalLoopGain.isEmpty()){
                    totalLoopGain.add(new ArrayList<>());
                }
                calculateGain(loopGain, totalLoopGain.get(0));
                totalLoopNode.get(0).add(new ArrayList<>(loopNode));
                loopGain.remove(node.getParents().get(parent));
            }
        }

    }

    private boolean checkRepeated(ArrayList<ArrayList<Node>> lists, ArrayList<Node> nodes){
        boolean repeated = false;
        for(ArrayList<Node> list : lists){
            repeated = true;
            for(Node n : nodes){
                if(list.size() != nodes.size() || !list.contains(n)){
                    repeated = false;
                    break;
                }
            }
            if (repeated){
                break;
            }
        }
        return !repeated;
    }

    private void calculateGain(ArrayList<Object> gains, ArrayList<DataType> calculatedGains){
        StringBuilder stringGain = new StringBuilder();
        Double numericGain = null;
        for (Object gain : gains){
            if(gain.getClass().equals(String.class)){
                stringGain.append((String) gain);
            }
            else if(gain.getClass().equals(Integer.class) || gain.getClass().equals(Double.class) || gain.getClass().equals(Float.class)){
                if (numericGain == null){
                    numericGain = 1.0;
                }
                numericGain *= (double) gain;
            }
        }
        int count = 0;
        for (int i = 0 ; i < stringGain.length(); i++){
            if (stringGain.toString().charAt(i) == '-'){
                stringGain.deleteCharAt(i);
                count++;
            }
        }
        if (count % 2 == 1){
            if (numericGain != null){
                numericGain *= -1.0;
            }
            else {
                numericGain = -1.0;
            }
        }
        DataType totalGain = new DataType();
        totalGain.setNumbers(numericGain);
        totalGain.setLetters(stringGain.toString());
        calculatedGains.add(totalGain);
    }

    private void checkUntouched(ArrayList<ArrayList<Node>> lists){
        if (!lists.isEmpty()){
            boolean untouched;
            for (int i = 0 ; i < totalLoopNode.get(0).size(); i++){
                for (int j = i + 1; j < lists.size(); j++){
                    untouched = true;
                    if (lists.get(j).size() <= totalLoopNode.get(0).get(i).size()){
                        for (int k = 0; k < lists.get(j).size(); k++){
                            if (totalLoopNode.get(0).get(i).contains(lists.get(j).get(k))){
                                untouched = false;
                                break;
                            }
                        }
                    }
                    else{
                        for (int k = 0; k < totalLoopNode.get(0).get(i).size(); k++){
                            if (lists.get(j).contains(totalLoopNode.get(0).get(i).get(k))){
                                untouched = false;
                                break;
                            }
                        }
                    }
                    if(untouched){
                        ArrayList<Node> toAdd = new ArrayList<>(lists.get(j));
                        toAdd.addAll(totalLoopNode.get(0).get(i));
                        if(totalLoopNode.indexOf(lists) + 1 == totalLoopNode.size() || ( i > 0 || checkRepeated(totalLoopNode.get(totalLoopNode.indexOf(lists) + 1), toAdd))){
                            ArrayList<DataType> toCalculate = new ArrayList<>();
                            toCalculate.add(totalLoopGain.get(0).get(i));
                            toCalculate.add(totalLoopGain.get(totalLoopNode.indexOf(lists)).get(j));
                            if (totalLoopNode.indexOf(lists) + 1 == totalLoopGain.size()){
                                totalLoopGain.add(new ArrayList<>());
                            }
                            calculateUntouchedGain(toCalculate, totalLoopGain.get(totalLoopNode.indexOf(lists)+1));
                            if (totalLoopNode.indexOf(lists) + 1 == totalLoopNode.size()){
                                totalLoopNode.add(new ArrayList<>());
                            }
                            totalLoopNode.get(totalLoopNode.indexOf(lists) + 1).add(toAdd);
                        }
                    }
                }
            }
        }
    }

    private void calculateUntouchedGain(ArrayList<DataType> toCalculate, ArrayList<DataType> toStore){
        Double numericGain = null;
        StringBuilder stringGain = new StringBuilder();
        for (DataType list : toCalculate){
            if (list.getNumbers() != null){
                if (numericGain == null){
                    numericGain = 1.0;
                }
                numericGain *= list.getNumbers();
            }
            else {
                if (numericGain != null && (stringGain.toString().startsWith("+") || stringGain.toString().startsWith("-"))){
                    stringGain.insert(0, numericGain.toString() + " ");
                }
                numericGain = null;
            }
            if (list.getLetters() != null){
                if (stringGain.toString().contains("+") || stringGain.toString().contains("-")){
                    stringGain.insert(0, list.getLetters() + "(");
                    stringGain.insert(stringGain.length() ,")");
                }
                else{
                    stringGain.append(list.getLetters());
                }
            }
        }
        DataType toAdd = new DataType();
        toAdd.setNumbers(numericGain);
        toAdd.setLetters(stringGain.toString());
        toStore.add(toAdd);
    }
    
    private void calculateDelta(ArrayList<ArrayList<DataType>> lists){
        double numericPart = 1.0;
        StringBuilder stringPart = new StringBuilder();
        for (int i = 1 ; i <= lists.size(); i++){
            for (DataType gain : lists.get(i-1)){
                if (Math.pow(-1, i) == -1.0){
                    if (gain.getLetters() != null && !gain.getLetters().equals("")){
                        if (gain.getNumbers() != null){
                            if (gain.getNumbers() < 0){
                                if (stringPart.length() == 0){
                                    stringPart.append("+ ");
                                }
                                else {
                                    stringPart.append(" + ");
                                }
                            }
                            else {
                                if (stringPart.length() == 0){
                                    stringPart.append("- ");
                                }
                                else {
                                    stringPart.append(" - ");
                                }
                            }
                        }
                        else {
                            if (stringPart.length() == 0){
                                stringPart.append("- ");
                            }
                            else {
                                stringPart.append(" - ");
                            }
                        }
                        stringPart.append(gain.getLetters());
                    }
                    else {
                        if (gain.getNumbers() != null){
                            numericPart = numericPart + (-1.0 * gain.getNumbers());
                        }
                    }
                }
                else {
                    if (gain.getLetters() != null && !gain.getLetters().equals("")){
                        if (gain.getNumbers() != null){
                            if (gain.getNumbers() < 0){
                                if (stringPart.length() == 0){
                                    stringPart.append("- ");
                                }
                                else {
                                    stringPart.append(" - ");
                                }
                            }
                            else {
                                if (stringPart.length() == 0){
                                    stringPart.append("+ ");
                                }
                                else {
                                    stringPart.append(" + ");
                                }
                            }
                        }
                        else {
                            if (stringPart.length() == 0){
                                stringPart.append("+ ");
                            }
                            else {
                                stringPart.append(" + ");
                            }
                        }
                        stringPart.append(gain.getLetters());
                    }
                    else {
                        if (gain.getNumbers() != null){
                            numericPart = numericPart + (1.0 * gain.getNumbers());
                        }
                    }
                }
            }
        }
        DataType add = new DataType();
        add.setNumbers(numericPart);
        add.setLetters(stringPart.toString());
        delta.add(add);
    }

    private void checkUntouchedLoopPath(){
        boolean untouched;
        ArrayList<ArrayList<DataType>> untouchedLoopPathGain = new ArrayList<>();
        for (ArrayList<Node> nodes : totalForwardPathNode) {
            for (int j = 0; j < totalLoopNode.size(); j++) {
                for (int k = 0; k < totalLoopNode.get(j).size(); k++) {
                    untouched = true;
                    if (totalLoopNode.get(j).get(k).size() <= nodes.size()) {
                        for (Node node : totalLoopNode.get(j).get(k)) {
                            if (nodes.contains(node)) {
                                untouched = false;
                                break;
                            }
                        }
                    } else {
                        for (Node node : nodes) {
                            if (totalLoopNode.get(j).get(k).contains(node)) {
                                untouched = false;
                                break;
                            }
                        }
                    }
                    if (j == untouchedLoopPathGain.size()) {
                        untouchedLoopPathGain.add(new ArrayList<>());
                    }
                    if (untouched) {
                        untouchedLoopPathGain.get(j).add(totalLoopGain.get(j).get(k));
                    } else {
                        DataType add = new DataType();
                        add.setNumbers(0.0);
                        add.setLetters(null);
                        untouchedLoopPathGain.get(j).add(add);
                    }
                }
            }
            calculateDelta(untouchedLoopPathGain);
            untouchedLoopPathGain = new ArrayList<>();
        }
    }

    public String  printAll (){
        String toReturn = "";
        for (int i = 1; i <= totalForwardPathGain.size(); i++){
            toReturn += "M" + i + " = " + totalForwardPathGain.get(i-1).printExpression() + "\r\n";
        }
        toReturn += "\r\n";
        for (int i = 1; i <= totalLoopGain.size(); i++){
            for (int j = 1; j <= totalLoopGain.get(i-1).size(); j++){
                toReturn += "L" + i + j + " = " + totalLoopGain.get(i-1).get(j-1).printExpression() + "\r\n";
            }
        }
        toReturn += "\r\n";
        for (int i = 0; i < delta.size(); i++){
            if (i == 0){
                toReturn += "Δ" + " = " + delta.get(i).printExpression() + "\r\n";
            }
            else {
                toReturn += "Δ" + i + " = " + delta.get(i).printExpression() + "\r\n";
            }
        }
        toReturn += "\r\n";
        toReturn += "Transfer Function = (";
        double numericPart = 0.0;
        StringBuilder stringPart = new StringBuilder();
        for (DataType finalTerm : finalTerms) {
            if (finalTerm.getNumbers() != null) {
                if (finalTerm.getLetters() == null || finalTerm.getLetters().equals("")){
                    numericPart += finalTerm.getNumbers();
                }
            }
            else {
                numericPart = 1.0;
            }
            if (finalTerm.getLetters() != null) {
                if (stringPart.length() > 0){
                    stringPart.append(" + ").append(finalTerm.getLetters());
                }
                else {
                    stringPart.append(finalTerm.getLetters());
                }
            }
        }

        DataType last =  new DataType();
        last.setNumbers(numericPart);
        last.setLetters(stringPart.toString());
        toReturn += last.printExpression() + ") / (";
        toReturn += delta.get(0).printExpression();
        toReturn += ")";
        return toReturn;
    }
}
