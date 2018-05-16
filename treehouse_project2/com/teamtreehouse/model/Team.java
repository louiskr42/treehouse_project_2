package com.teamtreehouse.model;

import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Arrays;

public class Team {
  private String mTeamName;
  private String mCoach;
  private List<Player> players;
  
  public Team(String teamName, String coach) {
    mTeamName = teamName;
    mCoach = coach;
    players = new LinkedList<Player>();
  }
  
  public String getTeamName() {
    return mTeamName;
  }
  
  public String getCoach() {
    return mCoach;
  }
  
  
  public int compareTo(Team other) {
    if (mTeamName.charAt(0) > other.getTeamName().charAt(0)) {
      return 1;
    } else if (mTeamName.charAt(0) < other.getTeamName().charAt(0)) {
      return -1;
    } else {
      return 0;
    }
  }
  
  public void removePlayerFromList(Player player) {
    players.remove(player);
  }
  
  public void addPlayerToList(Player player) {
    players.add(player);
  }
  
  public List<Player> getTeamPlayers() {
    return players;
  }
}