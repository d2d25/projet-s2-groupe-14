package com.qrcodeemargement.app.utils;

import org.springframework.stereotype.Component;

@Component
public class UserData {

    public static String MAIL = "denez.fauchon@etu.univ-orleans.fr";
    public static String PASSWORD = "12345687";
    public static String FIRSTNAME = "FirstName";
    public static String LASTNAME = "LastName";
    public static String NUMETU = "2197521";

    public static String mailTeacher1(){
        return "yohan.boichut@univ-orleans.fr";
    }

    public static String mailTeacher2(){
        return "frederic.moal@univ-orleans.fr";
    }

    public  static String mailStudent1(){
        return "leo.alvarez@etu.univ-orleans.fr";
    }

    public  static String mailStudent2(){
        return "denez.fauchon@etu.univ-orleans.fr";
    }

    public  static String mailAdmin1(){
        return "admin@univ-orleans.fr";
    }

    public  static String mailAdmin2(){
        return "admin2@univ-orleans.fr";
    }


    public static String firstNameTeacher() { return "yohan"; }

    public static String firstNameStudent() { return "leo"; }

    public static String firstNameAdmin() { return "admin"; }

    public static String lastNameTeacher() { return "boichut"; }

    public static String lastNameStudent() { return "alvarez"; }

    public static String lastNameAdmin() { return "admin"; }

    public static String numEtu() { return "2196714"; }

    public static String roleTeacher() { return "ROLE_TEACHER"; }

    public static String roleStudent() { return "ROLE_STUDENT"; }

    public static String roleAdmin() { return "ROLE_ADMIN"; }



    public static String unknownRole() { return "    "; }

    public  static String unknownMail(){return "unknown@etu.univ-orleans.fr"; }

    public  static String unknownPassword(){return "fgyuhjfgttyufr"; }

    public  static String wrongMail(){return "falseMail.univ-orleans.fr"; }

    public  static String wrongPassword(){return "1234567"; }




}
