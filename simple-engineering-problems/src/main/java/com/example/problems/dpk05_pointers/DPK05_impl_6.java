package com.example.problems.dpk05_pointers;

import java.util.HashMap;
import java.util.Map;

public class DPK05_impl_6 {

    public static final String DRAW = "draw";
    private HashMap<String,Integer> power = new HashMap<>();
    private HashMap<String,Integer> leaderboard = new HashMap<>();

    public DPK05_impl_6(){
        power.put("John", 100);
        power.put("Paul", 90);
        power.put("George", 80);
        power.put("Ringo", 70);

        leaderboard.put("John", 0);
        leaderboard.put("Paul", 0);
        leaderboard.put("George", 0);
        leaderboard.put("Ringo", 0);
    }


    public int getPower(String name) {
        return power.getOrDefault(name,-1);
    }

    public String getPowerful(String name1, String name2) {
        if (getPower(name1) > getPower(name2)) {
            return name1;
        } else if (getPower(name1) < getPower(name2)) {
            return name2;
        }
        return DRAW;
    }

    public String play(String player1, String player2) {
        String winner = getPowerful(player1, player2);
        if (winner.equals(DRAW)) {
            leaderboard.put(player1, leaderboard.get(player1) + 5);
            leaderboard.put(player2, leaderboard.get(player1) + 5);
        } else if (winner.equals(player1)) {
            leaderboard.put(player1, leaderboard.get(player1) + 10);
            leaderboard.put(player2, leaderboard.get(player2) - 5);
        } else if (winner.equals(player2)) {
            leaderboard.put(player2, leaderboard.get(player2) + 10);
            leaderboard.put(player1, leaderboard.get(player1) - 5);
        }
        return winner;
    }



    public Map<String, Integer> getLeaderboard() {
        return this.leaderboard;
    }


}
