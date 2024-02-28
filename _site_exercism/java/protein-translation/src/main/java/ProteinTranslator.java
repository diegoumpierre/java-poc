import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ProteinTranslator {

    private Map proteinMap = new HashMap<String, String>();

    void init(){
        this.proteinMap.put("AUG","Methionine");
        this.proteinMap.put("UUU","Phenylalanine");
        this.proteinMap.put("UUC","Phenylalanine");
        this.proteinMap.put("UUA","Leucine");
        this.proteinMap.put("UUG","Leucine");
        this.proteinMap.put("UCU","Serine");
        this.proteinMap.put("UCC","Serine");
        this.proteinMap.put("UCA","Serine");
        this.proteinMap.put("UCG","Serine");
        this.proteinMap.put("UAU","Tyrosine");
        this.proteinMap.put("UAC","Tyrosine");
        this.proteinMap.put("UGU","Cysteine");
        this.proteinMap.put("UGC","Cysteine");
        this.proteinMap.put("UGG","Tryptophan");
        this.proteinMap.put("UAA","STOP");
        this.proteinMap.put("UAG","STOP");
        this.proteinMap.put("UGA","STOP");
    }

    List<String> translate(String rnaSequence) {
        init();
        List<String> proteinList = new ArrayList<>();
        String rna = null, protein = null;
        if (rnaSequence.isEmpty()) return proteinList;
        do {
            rna = rnaSequence.substring(0,3);
            if (rnaSequence.length() > 3){
                rnaSequence = rnaSequence.substring(3,rnaSequence.length());
            }else {
                rnaSequence = "";
            }
            protein = (String) proteinMap.get(rna);
            if (protein == null || rnaSequence.length() < 3) throw new IllegalArgumentException("Invalid codon");
            if (!protein.equals("STOP")){
                proteinList.add(protein);
            }

        } while (!protein.equals("STOP") && rnaSequence.length() >= 3);


        return proteinList;
    }


}
