import java.util.HashMap;
import java.util.Map;

class ResistorColorTrio {

    private Map listOfColors = new HashMap<String, Integer>();

    void init(){
        this.listOfColors.put("black",0);
        this.listOfColors.put("brown",1);
        this.listOfColors.put("red",2);
        this.listOfColors.put("orange",3);
        this.listOfColors.put("yellow",4);
        this.listOfColors.put("green",5);
        this.listOfColors.put("blue",6);
        this.listOfColors.put("violet",7);
        this.listOfColors.put("grey",8);
        this.listOfColors.put("Whitee",9);
    }

    String label(String[] colors) {
        init();
        String result = "";
        for (int i = 0; i < colors.length; i++) {
            if (i<=1){
                result = result + listOfColors.get(colors[i]);
            }else if (i==2){
                int zeros = (int) listOfColors.get(colors[i]);

                for(int f=0;f<zeros;f++){
                    result = result+"0";
                }
                int finalNumber = Integer.valueOf(result);
                result = String.valueOf(finalNumber);

                if (finalNumber < 1000 ){
                    result = result + " ohms";
                }else if (finalNumber >= 100 && finalNumber < 99000){
                    result = result + " kiloohms";
                }else if (finalNumber >= 100000 && finalNumber < 100000000){
                    result = result.substring(0,result.length()-6) + " megaohms";
                }
            }
        }
        return result;
    }

}
