public class DataType {

    private Double numbers;
    private String letters;

    public Double getNumbers() {
        return numbers;
    }

    public String getLetters() {
        return letters;
    }

    public void setLetters(String letters) {
        this.letters = letters;
    }

    public void setNumbers(Double numbers) {
        this.numbers = numbers;
    }

    public String printExpression(){
        if (letters.equals("()") || letters.equals(" ()") || letters.equals("() ") || letters.equals(" () ")){
            letters = "";
        }
        if (numbers == null){
            return letters;
        }
        else if (numbers == 0.0){
            if (letters.startsWith("+") || letters.startsWith("-")){
                return "1 " + letters;
            }
            else if (letters.startsWith(" +") || letters.startsWith(" -")){
                return "1" + letters;
            }
            else {
                return "";
            }
        }
        else if (numbers == 1.0){
            if (letters == null || letters.equals("")){
                return "1";
            }
            else {
                if (letters.startsWith("+") || letters.startsWith("-")){
                    return "1 " + letters;
                }
                else if (letters.startsWith(" +") || letters.startsWith(" -")){
                    return "1" + letters;
                }
                else {
                    return letters;
                }
            }
        }
        else if (numbers == -1.0){
            if (letters.equals("") || letters.equals(null)){
                return numbers.toString();
            }
            else {
                return "- (" + letters + ")";
            }
        }
        else{
            if (letters.equals("") || letters.equals(null)){
                return numbers.toString();
            }
            else {
                return numbers.toString() + " (" + letters + ")";
            }
        }
    }
}
