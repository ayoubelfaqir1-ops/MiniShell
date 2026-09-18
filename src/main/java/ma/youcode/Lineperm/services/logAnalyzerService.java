package ma.youcode.Lineperm.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Map;
import ma.youcode.Lineperm.enums.ActionStatus;
import ma.youcode.Lineperm.enums.ActionType;
import ma.youcode.Lineperm.models.Action;

public class logAnalyzerService {
    private final Path actionsFile = Paths.get("src", "main", "Resources", "Actions.txt");
    public void NombreActions() {
        List<String> lines = readFile();
        System.out.println("le nombre total d'actions est " + lines.stream().count());
    }
    public void nombreActionsRefuses() {
        List<String> lines = readFile();
        List<Action> actions = extractFromLines(lines);
        long n = actions.stream()
                    .filter(action -> action.getStatus() == ActionStatus.REFUSE)
                    .count();
        System.out.println(
            "le nombre d'actions refuses c'est " + n
        );;
    }
    public void distinctsUsers() {
        List<String> lines = readFile();
        List<Action> actions = extractFromLines(lines);
        Stream<String> n = actions.stream()
                    .map(act -> act.getUsername())
                    .distinct();
        System.out.println(
            "le nombre d'utulisateurs distincts est  " + n.count()
        );;
    }
    public void actionsParUser() {
        List<String> lines = readFile();
        List<Action> actions = extractFromLines(lines);
        Map<String,Long> n = actions.stream()
                    .collect(Collectors.groupingBy(Action::getUsername, Collectors.counting()));
        for(Map.Entry<String, Long> entry : n.entrySet()){
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
    }
    
    public List<String> readFile() {
        try {
            List<String> lines = Files.readAllLines(actionsFile);
            return lines;
        }catch (IOException e) {
            e.printStackTrace();
            return List.of();
        }
    }
    public List<Action> extractFromLines(List<String> lines) {
        List<Action> actions = lines.stream()
        .map(line -> {
            String[] actionInfo = line.split(";");
            ActionType actionType = ActionType.valueOf(actionInfo[3]);
            ActionStatus actionStatus = ActionStatus.valueOf(actionInfo[5]);
            
            Action action = new Action(
                actionInfo[0],
                actionInfo[1],
                actionInfo[2],
                actionType,
                actionInfo[4],
                actionStatus
            );
            return action;
        })
        .collect(Collectors.toList());
        return actions;
    }
    public void fichierPlusConsultes() {
        List<String> lines = readFile();
        List<Action> actions = extractFromLines(lines);
        Map<String,Long> n = actions.stream()
                    .collect(Collectors.groupingBy(Action::getTarget, Collectors.counting()))
                    .entrySet()
                    .stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .limit(3)
                    .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                        ));
        for(Map.Entry<String, Long> entry : n.entrySet()){
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
    }
    public void AccesRefuseesUser() {
        List<String> lines = readFile();
        List<Action> actions = extractFromLines(lines);
        Map<String,Long> n = actions.stream()
                    .collect(Collectors.groupingBy(Action::getUsername, Collectors.filtering(
                        act -> act.getStatus().equals(ActionStatus.REFUSE),
                        Collectors.counting()
        )));
        for(Map.Entry<String, Long> entry : n.entrySet()){
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
    }
    public void userPlusActif() {
        List<String> lines = readFile();
        List<Action> actions = extractFromLines(lines);
        Map<String,Long> n = actions.stream()
                    .collect(Collectors.groupingBy(
                        Action::getUsername,
                        Collectors.counting()
                    ))
                    .entrySet()
                    .stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .limit(1)
                    .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                    ));
        for(Map.Entry<String, Long> entry : n.entrySet()){
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
    }
    public void SeperationActionByType() {
        List<String> lines = readFile();
        List<Action> actions = extractFromLines(lines);
        Map<ActionType,List<Action>> n = actions.stream()
                    .collect(Collectors.groupingBy(
                        Action::getActionType
                    ));
        for(Map.Entry<ActionType, List<Action>> entry : n.entrySet()){
            System.out.println();
            System.out.println(entry.getKey() + " :");
            
            for(Action act : entry.getValue()) {
                System.out.println(act.getDate() + ";" + act.getTime() +";"+ act.getUsername() +";"+ act.getActionType() +";"+ act.getTarget() +";"+ act.getStatus());
            }
        }
    }
}
