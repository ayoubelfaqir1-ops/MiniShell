package ma.youcode.Lineperm.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Map;

import ma.youcode.Lineperm.Daos.ActionsDao;
import ma.youcode.Lineperm.Daos.FileDao;
import ma.youcode.Lineperm.enums.ActionStatus;
import ma.youcode.Lineperm.enums.ActionType;
import ma.youcode.Lineperm.models.Action;

public class logAnalyzerService {
    
    private final FileDao fileDao;
    private final ActionsDao actionsDao;
    private List<Action> actions ;
    private final DateTimeFormatter formatter24 = DateTimeFormatter.ofPattern("HH:mm");


    public logAnalyzerService() {
        this.fileDao = new FileDao();
        this.actionsDao = new ActionsDao();
        this.actions = this.actionsDao.getAll();
    }
    public void NombreActions() {
        System.out.println("le nombre total d'actions est " + actions.size());
    }
    public void nombreActionsRefuses() {
        Long n = actions.stream().filter(act -> act.getStatus().equals(ActionStatus.REFUSE)).count();
        System.out.println(
            "le nombre d'actions refuses c'est " + n
        );
    }
    public void distinctsUsers() {
        Long n = actions.stream().map(act -> act.getUsername()).distinct().count();
        System.out.println(
            "le nombre d'utulisateurs distincts est  " + n
        );;
    }
    public void actionsParUser() {
        Map<String,Long> n = actions.stream()
                    .collect(Collectors.groupingBy(Action::getUsername, Collectors.counting()));
        for(Map.Entry<String, Long> entry : n.entrySet()){
            System.out.println(" Utilisateur : " + entry.getKey() + " |  Total des actions : " + entry.getValue());
        }
    }
    
    public void fichierPlusConsultes() {
        Map<String,Integer> n = actionsDao.getTop3ActiveFiles();
        for(Map.Entry<String, Integer> entry : n.entrySet()){
            System.out.println(" Fichier : " + entry.getKey() + " |  Consultations : " + entry.getValue());
        }
    }
    public void AccesRefuseesUser() {
        Map<String,Integer> n = actionsDao.AccesRefusedByUser();
        ;
        for(Map.Entry<String, Integer> entry : n.entrySet()){
            System.out.println(" Utilisateur : " + entry.getKey() + " |  Tentatives refusees : " + entry.getValue());
        }
    }

    public void userPlusActif() {
        Map<String,Integer> n = actionsDao.UserPlusActif();
        for(Map.Entry<String, Integer> entry : n.entrySet()){
            System.out.println(" Utilisateur le plus actif : " + entry.getKey() + " (Total : " + entry.getValue() + " actions)");
        }
    }

    public void SeperationActionByType() {
        Map<ActionType,List<Action>> n = actionsDao.ActionsByType();
        for(Map.Entry<ActionType, List<Action>> entry : n.entrySet()){
            System.out.println();
            System.out.println(entry.getKey() + " :");
            
            for(Action act : entry.getValue()) {
                System.out.println(" [" + act.getDate() + " " + act.getTime() + "] " + act.getUsername() + " a cible '" + act.getTarget() + "' (Statut: " + act.getStatus() + ")");
            }
        }
    }

    public void addAction(String target, String userName, ActionType type, ActionStatus status) {
        Action action = new Action(
                LocalDate.now().toString(),
                LocalTime.now().format(formatter24),
                userName,
                type,
                target,
                status);
        actionsDao.save(action);
        actions.add(action);
    }
}
