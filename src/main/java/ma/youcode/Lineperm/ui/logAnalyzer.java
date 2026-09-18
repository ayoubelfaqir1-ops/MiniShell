package ma.youcode.Lineperm.ui;

import java.util.Scanner;

import ma.youcode.Lineperm.services.logAnalyzerService;

public class logAnalyzer {
    
    public static void demarrer(Scanner scanner) {
        logAnalyzerService logAnServ = new logAnalyzerService();
        Boolean a = true;
        System.out.println("Bienvenue dans LogAnalyzer. Choisissez une statistique par son numéro.");
        System.out.println("=== LogAnalyzer ===");
        
        System.out.println();
        System.out.println("1) Nombre total d'actions");
        System.out.println("2) Nombre d'accès refuses");
        System.out.println("3) Utilisateurs distincts");
        System.out.println("4) Actions par utilisateur");
        System.out.println("5) Top 3 des fichiers consultés");
        System.out.println("6) Accès refuses d'un utilisateur");
        System.out.println("7) Utilisateur le plus actif");
        System.out.println("8) Repartition des actions par type");
        System.out.println("0) Quitter");
        System.out.println();
        while (a) {
            System.out.print("Choix : ");
            int choix = scanner.nextInt();
            if (choix>8) {
                System.out.println("choix invalide !");
            }
            switch (choix) {
                case 1:
                    logAnServ.NombreActions();
                    break;
                case 2:
                    logAnServ.nombreActionsRefuses();
                    break;
                case 3:
                    logAnServ.distinctsUsers();
                    break;
                case 4:
                    logAnServ.actionsParUser();
                    break;
                case 5:
                    logAnServ.fichierPlusConsultes();
                    break;
                case 6:
                    logAnServ.AccesRefuseesUser();
                    break;
                case 7:
                    logAnServ.userPlusActif();
                    break;
                case 8:
                    logAnServ.SeperationActionByType();;
                    break;
                case 0:
                    a = false;
                    if (scanner.hasNextLine()) {
                        scanner.nextLine();
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
