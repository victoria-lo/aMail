package com.gameguildstudios.amail;

public class Utils {
    private static String email;
    private static String password;

    public Utils(String email, String password) {
        Utils.email = email;
        Utils.password = password;
    }

    public static void setEmail(String email) {
        Utils.email = email;
    }

    public static void setPassword(String password) {
        Utils.password = password;
    }

    public static String getEmail() {
        if(email == null){
            return "";
        }
        return email;
    }

    public static String getPassword() {
        if(password == null){
            return "";
        }
        return password;
    }

//    private String EMAIL = "gameguildstudios@gmail.com";
//    private String PASSWORD = "ctjlcfybakjjjskg";

}
