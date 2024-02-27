import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ProteinTranslator {

    private Map proteinList = new HashMap<String, String>();

    void init(){
        this.proteinList.put("AUG","Methionine");
        this.proteinList.put("UUU","Phenylalanine");
        this.proteinList.put("UUC","Phenylalanine");
        this.proteinList.put("UUA","Leucine");
        this.proteinList.put("UUG","Leucine");
        this.proteinList.put("UCU","Serine");
        this.proteinList.put("UCC","Serine");
        this.proteinList.put("UCA","Serine");
        this.proteinList.put("UCG","Serine");
        this.proteinList.put("UAU","Tyrosine");
        this.proteinList.put("UAC","Tyrosine");
        this.proteinList.put("UGU","Cysteine");
        this.proteinList.put("UGC","Cysteine");
        this.proteinList.put("UGG","Tryptophan");
        this.proteinList.put("UAA","STOP");
        this.proteinList.put("UAG","STOP");
        this.proteinList.put("UGA","STOP");
    }

    List<String> translate(String rnaSequence) {
        init();



        "STOP"

    }
}
