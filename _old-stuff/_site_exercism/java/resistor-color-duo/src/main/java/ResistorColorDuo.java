import java.util.HashMap;
import java.util.Map;


class ResistorColorDuo {

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
        this.listOfColors.put("white",9);
    }

    int value(String[] colors) {
        init();
        String result = "";
        for (int i = 0; i < colors.length; i++) {
            if (i<=1){
                result = result + listOfColors.get(colors[i]);
            }
        }
        return Integer.valueOf(result).intValue();
    }
}
