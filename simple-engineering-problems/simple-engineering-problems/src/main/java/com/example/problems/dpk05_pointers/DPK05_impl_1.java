package com.example.problems.dpk05_pointers;

import java.util.HashMap;
import java.util.Map;

public class DPK05_impl_1 {

    private Map<String, Integer> power = Map.of(
            "John",100,
            "Paul", 90,
            "George", 80,
            "Ringo", 70
    );


    private Map<String, Integer> leaderboard = Map.of(
            "John",0,
            "Paul", 0,
            "George", 0,
            "Ringo", 0
    );


    public Integer getPower(String name) {
        return power.getOrDefault(name, -1);
    }

    public String getPowerFullOne(String personName, String personName2) {
        if (getPower(personName) > getPower(personName2) ){
            return personName;
        }
        return personName2;


    }

    public String play(String player1, String player2) {
        boolean playerOneWin = false;
        String winner = getPowerFullOne(player1, player2);
        if (player1.equals(winner)) {
            leaderboard.put(player1, leaderboard.get(player1)+10);
            leaderboard.put(player2, leaderboard.get(player2)-5);
        }else{
            leaderboard.put(player2, leaderboard.get(player2)+10);
            leaderboard.put(player2, leaderboard.get(player1)-5);
        }


        return winner;
    }

    public Map<String, Integer> getLeaderboard() {
        return leaderboard;
    }
}
