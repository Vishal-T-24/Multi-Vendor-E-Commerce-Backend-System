package org.example.Util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {



    public  String HashPassword(String password){
        return BCrypt.hashpw(password,BCrypt.gensalt());
    }

    public boolean verifyPassword(String PlainPassword,String HashedPassword){
        return BCrypt.checkpw(PlainPassword,HashedPassword);
    }


}
