package com.gameguildstudios.amail;


public class Utils {
    private static String email;
    private static String password;
    private static final String filename = "credentials";

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

    public static String getCredentials() {
        if (password == null || email == null)
            return "";
        return email + "," + password;
    }

    public static String getFilename() {
        return filename;
    }

//    private String EMAIL = "gameguildstudios@gmail.com";
//    private String PASSWORD = "ctjlcfybakjjjskg";

}
