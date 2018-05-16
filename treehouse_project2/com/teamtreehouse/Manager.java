package com.teamtreehouse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import com.teamtreehouse.model.Team;
import com.teamtreehouse.model.Player;
import com.teamtreehouse.model.Players;

public class Manager {

  private BufferedReader mReader;
  private Map<String, String> mMenu;
  private List<Team> teams;
  private List<String> playersInTeam;
  private Players playerSet = new Players();
  

  public Manager() {
    mReader = new BufferedReader(new InputStreamReader(System.in));
    mMenu = new TreeMap<String, String>();
    teams = new ArrayList<>();
    
    mMenu.put("Create", "Create a new team");
    mMenu.put("Add", "Add a player to a team");
    mMenu.put("Balance", "View the League Balance Report");
    mMenu.put("Remove", "Remove a player from a team");
    mMenu.put("Report", "View a report a team grouped by height");
    mMenu.put("Roster", "View roster");
    mMenu.put("Quit", "Exit the manager");
  }
  
  private boolean isThereATeam() {
    if (teams.isEmpty()) {
      System.out.println("Sorry, but you need to create a team first!");
      return false;
    } else {
      return true;
    }
  }
  
  private boolean isMaximumNumberOfTeams() {
    if (teams.size() >= playerSet.getAvailablePlayers().size()) {
      System.out.println("Sorry but you have reached the maximum amount of teams!");
      return true;
    } else {
      return false;
    }
  }
  
  public void run() {
    String choice = "";
    do {
      try {
        printMenu();
        choice = promptForAction();
      
        switch(choice) {
          case "create":
            if (!isMaximumNumberOfTeams()) {
              String teamName = promptForTeamName();
              String coach = promptForCoach();
              Team team = new Team(teamName, coach);
              teams.add(team);
              System.out.printf("The team \"%s\" coached by %s was successfully created!%n%n", team.getTeamName(), team.getCoach());
            }
            break;
          case "add":
            if (isThereATeam()) {
              Player player = promptForPlayer(playerSet.getAvailablePlayers());
              Team selectedTeam = promptForTeam();
              selectedTeam.addPlayerToList(player);
              playerSet.removePlayerFromList(player);
              System.out.printf("%s %s was added to \"%s\"!%n",
                                player.getFirstName(),
                                player.getLastName(),
                                selectedTeam.getTeamName());
            }
            break;
          case "remove":
            if (isThereATeam()) {
              Team selectedTeam = promptForTeam();
              List<Player> players = selectedTeam.getTeamPlayers();
              Player player = promptForPlayer(players);
              selectedTeam.removePlayerFromList(player);
              playerSet.addPlayerToList(player);
              System.out.printf("%s %s was removed from \"%s\"%n",
                                player.getFirstName(),
                                player.getLastName(),
                                selectedTeam.getTeamName());
            }
            break;
          case "report":
            if (isThereATeam()) {
              Team selectedTeam = promptForTeam();
              List<Player> players = selectedTeam.getTeamPlayers();
              showReport(players);
            }
            break;
          case "balance":
            if (isThereATeam()) {
              showBalanceReport();
            }
            break;
          case "roster":
            if (isThereATeam()) {
              showRoster();
            }
            break;
          case "quit":
            System.out.println("Thank you for using our service!");
            break;
          case "exit":
            System.out.println("Thank you for using our service!");
            break;
          default:
            System.out.println("Sorry but that option is not available.");
        }
      } catch(IOException ioe) {
          System.out.println("Sorry there is a problem with your input.");
          ioe.printStackTrace();
      } catch(ArrayIndexOutOfBoundsException aioobe) {
          System.out.println("Sorry there is a problem with your input.");
          aioobe.printStackTrace();
      } catch(NumberFormatException nfe) {
          System.out.println("Sorry there is a problem with your input.");
          nfe.printStackTrace();
      }
    } while(!choice.equals("quit") && !choice.equals("exit"));
  }
  
  private void showRoster() {
    System.out.printf("%nRoster:%n");
    for (Team team : teams) {
      List<Player> players = team.getTeamPlayers();
      String listOfPlayers = "";
      for (Player player : players) {
        String experience;
        if (player.isPreviousExperience()) {
          experience = "experienced";
        } else {
          experience = "inexperienced";
        }
        listOfPlayers = listOfPlayers + player.getFirstName() + " " +
                                        player.getLastName() + " (" + 
                                        player.getHeightInInches() + ", " + 
                                        experience + "), ";
      }
      System.out.printf("\"%s\" coached by %s: [" + listOfPlayers + "]%n",
                       team.getTeamName(),
                       team.getCoach());
    }
  }
  
  private void showReport(List<Player> players) {
    players.sort(new Comparator<Player>() {
      @Override
      public int compare(Player player1, Player player2) {
        if (player1.equals(player2)) {
          return 0;
        }
        return player1.compareTo(player2);
      }
    });
    int index = 1;
    
    System.out.printf("%nList of Players by height:%n");
    for (Player player : players) {
      String experience;
      if (player.isPreviousExperience()) {
        experience = "experienced";
      } else {
        experience = "inexperienced";
      }
      System.out.printf("%d.) %s %s (%d inches - %s)%n", 
                        index,
                        player.getFirstName(),
                        player.getLastName(),
                        player.getHeightInInches(),
                        experience);
      index++;
    }
    double percentage = getAmountOfExperiencedPlayers(players) / (double)players.size() * 100;
    System.out.printf("The average experience level for this team is: %.2f%%.%n",
                      percentage);
  }
  
  private void showBalanceReport() {
    System.out.printf("%nBalance Report:%n");
    for (Team team : teams) {
      System.out.printf("\"%s\" coached by %s:  [%d experienced and %d inexperienced players].%n" + 
                        "%nAmount of players for each height:" +
                        getListOfHeights(team) + "%n",
                        team.getTeamName(),
                        team.getCoach(),
                        getAmountOfExperiencedPlayers(team.getTeamPlayers()),
                        getAmountOfInexperiencedPlayers(team.getTeamPlayers()));
    }
  }
  
  private String getListOfHeights(Team team) {
    String stringListOfHeights = "";
    List<Player> players = team.getTeamPlayers();
    Map<String, List<Player>> heights = new TreeMap<>();
    heights.put("35-40", new ArrayList<Player>());
    heights.put("41-46", new ArrayList<Player>());
    heights.put("47-50", new ArrayList<Player>());
    for (Player player : players) {
      if (player.getHeightInInches() >= 35 && player.getHeightInInches() <= 40) {
        heights.get("35-40").add(player);
      } else if (player.getHeightInInches() >= 41 && player.getHeightInInches() <= 46) {
        heights.get("41-46").add(player);
      } else {
        heights.get("47-50").add(player);
      }
    }
    for (String height : heights.keySet()) {
      stringListOfHeights = stringListOfHeights + "%n" + height + " inches [" + heights.get(height).size() + " Player(s)]:%n";
      for (Player player : heights.get(height)) {
        stringListOfHeights = stringListOfHeights + player.getFirstName() + " " + player.getLastName() +
                              " (" + player.getHeightInInches() + " inches)" + "%n";
      }
    }
    return stringListOfHeights;
  }
  
  private int getAmountOfExperiencedPlayers(List<Player> players) {
    int amount = 0;
    for (Player player : players) {
      if (player.isPreviousExperience()) {
        amount++;
      }
    }
    return amount;
  }
  
  private int getAmountOfInexperiencedPlayers(List<Player> players) {
    int amount = 0;
    for (Player player : players) {
      if (!player.isPreviousExperience()) {
        amount++;
      }
    }
    return amount;
  }
  
  private Team promptForTeam() throws IOException {
    teams.sort(new Comparator<Team>() {
      @Override
      public int compare(Team team1, Team team2) {
        if (team1.equals(team2)) {
          return 0;
        }
        return team1.getTeamName().compareTo(team2.getTeamName());
      }
    });
      
    int index = 1;
    System.out.printf("%nAvailable Teams:%n");
    for (Team team : teams) {
      System.out.printf("%d.) \"%s\" coached by %s%n",
                        index,
                        team.getTeamName(),
                        team.getCoach());
      index++;
    }
    System.out.printf("%nPlease enter the number of the team you want to select: ");
    String choice = mReader.readLine();
    index = Integer.parseInt(choice) - 1;
    return teams.get(index);
  }
  
  private Player promptForPlayer(List<Player> listOfAvailablePlayers) throws IOException {
    int index = 1;
    
    listOfAvailablePlayers.sort(new Comparator<Player>() {
      @Override
      public int compare(Player player1, Player player2) {
        if (player1.equals(player2)) {
          return 0;
        }
        return player1.getLastName().compareTo(player2.getLastName());
      }
    });
    
    String experience;
    System.out.printf("%nAvailable Players%n");
    for (Player player : listOfAvailablePlayers) {
      if (player.isPreviousExperience()) {
        experience = "experienced";
      } else {
        experience = "inexperienced";
      }
      System.out.printf("%d.) %s %s (%d inches - %s)%n", 
                        index,
                        player.getFirstName(),
                        player.getLastName(),
                        player.getHeightInInches(),
                        experience);
      index++;
    }
    System.out.printf("%nPlease enter the number of the player you want to select: ");
    String choice = mReader.readLine();
    index = Integer.parseInt(choice) - 1;
    return listOfAvailablePlayers.get(index);
  }
  
  private String promptForCoach() throws IOException {
    System.out.printf("Enter the name of the coach: ");
    String choice = mReader.readLine();
    return choice.trim();
  }
  
  private String promptForTeamName() throws IOException {
    System.out.printf("%nEnter the name of the team: ");
    String choice = mReader.readLine();
    return choice.trim();
  }
  
  private String promptForAction() throws IOException {
    System.out.printf("%nEnter your choice: ");
    String choice = mReader.readLine();
    return choice.trim().toLowerCase();
  }
  
  private void printMenu() {
    System.out.printf("%nMenu%n");
    for (Map.Entry<String, String> option : mMenu.entrySet()) {
      System.out.printf("%s - %s %n",
                        option.getKey(),
                        option.getValue());
    }
  }
}