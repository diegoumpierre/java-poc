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
        this.listOfColors.put("whitee",9);
    }

    String label(String[] colors) {
        init();
        String result = "";
        for (int i = 0; i < colors.length; i++) {
            if (i<=1){
                int colorValue = (int) listOfColors.get(colors[i]);
                if (colorValue > 0 && colors.length >1 )
                    result = result + colorValue;
            }else if (i==2){
                int zeros = (int) listOfColors.get(colors[i]);

                if (zeros < 3){
                    for (int j = 0; j < zeros; j++) {
                        result = result+0;
                    }
                    result = result.replace("00","0");
                    result = result + " ohms";
                }else if (zeros >= 3 && zeros <6 ){
                    for (int j = 0; j < zeros -3 ; j++) {
                        result = result+0;
                    }
                    result = result + " kiloohms";
                }else if (zeros >= 6 && zeros <9 ){
                    for (int j = 0; j < zeros -6 ; j++) {
                        result = result+0;
                    }
                    result = result + " megaohms";
                }else{
                    result = result + " gigaohms";
                }

            }
        }
        return result;
    }

}
