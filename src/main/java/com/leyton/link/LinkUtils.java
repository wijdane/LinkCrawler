package com.leyton.link;

import java.util.Random;

public class LinkUtils {
    final static String[] names = new String[]{"Korbyn",
            "Markus", "Ammar", "Karim", "Faycel", "Malek", "Youssef",
            "Chance",
            "Anderson", "Bowie",
            "Deangelo",
            "Harlem", "Forest" +
            "Benedict", "Boden",
            "Camdyn", "Liam",
            "Noah",
            "William",
            "James", "Luna", "Langona", "Syspa", "Rosalina", "Hafida", "Amila", "Samida", "Abdellah", "Mohammed", "Ahmed", "Abdeali", "Sosan", "Samira", "Abdelhak", "Kabira", "Romana", "Jomana"};
    final static String[] lastNames = new String[]{"PECK", "SANFORD", "LEWIS", "WALLER", "EDWARDS", "EVANS", "PHILLIPS", "TURNER", "NELSON", "GONZALEZ", "ADAMS", "YOUNG", "ALLEN", "HALL", "CLARK", "RODRIGUEZ", "Gafner", "Gagan", "Gagas", "Gage", "Gagel", "Gagen", "Gager", "Gagliano", "Gagliardi", "Gagliardo", "Gaglio", "Gaglione", "Gagnard", "Gagne", "Kabira", "Romana", "Jomana"};

    final static String[] titles = new String[]{"Marketing", "Coordinator",
            "Medical Assistant",
            "Web Designer",
            "Dog Trainer",
            "President of Sales",
            "Nursing Assistant",
            "Project Manager",
            "Librarian"};


    final static String[] companyTrans = new String[]{"Company","Entreprises"};

    public static String findLastName () {
        return lastNames[(int) (Math.random() * 17)];
    }

    public static String findName () {
        return names[(int) (Math.random() * 17)];
    }

    public static char[] generatePswd ( int len){
        String charsCaps = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String chars = "abcdefghijklmnopqrstuvwxyz";
        String nums = "0123456789";
        String symbols = "!@#$%^&*_=+-/€.?<>)";

        String passSymbols = charsCaps + chars + nums + symbols;
        Random rnd = new Random();

        char[] password = new char[len];
        for (int i = 0; i < len; i++) {
            password[i] = passSymbols.charAt(rnd.nextInt(passSymbols.length()));

        }
        System.out.println(" password : " + password);
        return password;
    }

    public static boolean isCompany(String sousText){
        for (int i = 0; i < companyTrans.length; i++) {
            if(sousText.toLowerCase().contains(companyTrans[i].toLowerCase()))
                return true;
        }
        return false;
    }
}
