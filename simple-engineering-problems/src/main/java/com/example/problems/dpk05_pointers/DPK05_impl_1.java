package com.example.problems.dpk05_pointers;

import java.util.HashMap;
import java.util.Map;

public class DPK05_impl_1 {

    private HashMap<String, Integer> power = new HashMap<>();
    private HashMap<String, Integer> leaderboard = new HashMap<>();

    public DPK05_impl_1(){
        //initializing power
        power.put("John",100);
        power.put("Paul", 90);
        power.put("George", 80);
        power.put("Ringo", 70);
        power.put("Ringo3", 70);

        //initializing leaderboard
        leaderboard.put("John",0);
        leaderboard.put("Paul",0);
        leaderboard.put("George",0);
        leaderboard.put("Ringo",0);
        leaderboard.put("Ringo3", 0);
    }


    public Integer getPower(String name) {
        return power.getOrDefault(name, -1);
    }

    public String getPowerFullOne(String personName, String personName2) {
        if (getPower(personName) > getPower(personName2) ){
            return personName;
        }else if (getPower(personName) < getPower(personName2)){
                return personName2;
            }
        return "draw";
    }

    public String play(String player1, String player2) {
        String winner = getPowerFullOne(player1, player2);
        if (player1.equals(winner)) {
            leaderboard.put(player1, leaderboard.get(player1)+10);
            leaderboard.put(player2,leaderboard.get(player2)-5);
        }else if (player2.equals(winner)) {
            leaderboard.put(player2, leaderboard.get(player2)+10);
            leaderboard.put(player2, leaderboard.get(player2)-5);
        }else {
            leaderboard.put(player1, leaderboard.get(player1)+5);
            leaderboard.put(player2, leaderboard.get(player2)+5);
        }
        return winner;
    }

    public Map<String, Integer> getLeaderboard() {
        return leaderboard;
    }
}
