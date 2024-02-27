import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The proposal for the class it is show the basic structure.
 *
 * @author diegoUmpierre
 * @since Jan 04 2024
 */
public class WordCount {

    private Map<String, Integer> wordCount;

    public Map<String, Integer> phrase(String input) {
        wordCount = new HashMap<>();

        String[] wordsSpace = cleanWordIgnoreCaracters(input.toLowerCase()).split(" ");
        for (String wSpace : wordsSpace) {
            String[] wordsComma = wSpace.split(",");
            doCount(wordsComma);
        }
        return wordCount;
    }

    private String cleanWordIgnoreCaracters(String input){

        String[] arrayOfLine = {"I.2 Other Interpretive Provisions" , "I.3 Accounting Terms","Including all","II.1 The Loans","II.3 Prepayments.","III.2 Illegality","IV.2 Conditions","V.2 Authorization","expected to have"};
        Pattern pat = Pattern.compile("^[A-Z]+\\.[0-9]+\\b");
        List<String> listOfHeadings = new ArrayList<>();
        for (String s : arrayOfLine) {
            Matcher m = pat.matcher(s);
            if (m.find()) {
                listOfHeadings.add(s);
            }
        }
        System.out.println(listOfHeadings);




//        input = input.replaceAll("\n","");
//        input = input.replaceAll("!","");
//        input = input.replaceAll("&","");
//        input = input.replaceAll("@","");
//        input = input.replaceAll("\\$","");
//        input = input.replaceAll("%","");
//        input = input.replaceAll("\\^","");
//        input = input.replaceAll(":","");
//        input = input.replaceAll("\\.","");
//        input = input.replaceAll("\\.","");
       // input = input.replaceAll("'","");
        return input;
    }
    private void doCount(String[] words){
        for(String word :words){

            if (word.startsWith("'") && word.endsWith("'")){
                word = word.replaceFirst("'","");
                word = word.substring(0,word.length()-1);
            }
            if (word.trim().equals("")) continue;

            if (wordCount.containsKey(word)){
                wordCount.put(word, wordCount.get(word)+1);
            }else{
                wordCount.put(word, 1);
            }
        }
    }
}
