import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class PasswordCrack {
    public static void main(String args[]) throws IOException {
        final long start = System.currentTimeMillis(); //they're timing program out at 300000 ms!
        String fileName = "input";
        // String fileName = args[0] + ".txt"; //Doing string concat 1 time is fine
        File file = new File(fileName);
        fileName = file.getAbsolutePath();
        BufferedReader br;
        br = new BufferedReader(new FileReader(file));
        String firstName = "";
        String salt = "";
        String encPwd = "";
        String line = "";
        String fullName = "";
        String lastName = "";
        ArrayList<String> firstNames = new ArrayList<String>();
        ArrayList<String> salts = new ArrayList<String>();
        ArrayList<String> encPwds = new ArrayList<String>();
        ArrayList<String> lastNames = new ArrayList<String>();
        while ((line = br.readLine()) != null) {
            firstName = "";
            salt = "";
            encPwd = "";
            fullName = "";
            lastName = "";
            int strIndex = 0;
            while (line.charAt(strIndex) != ':') {
                strIndex++;
            }
            strIndex++;
            for (int i = 0; i < 2; i++) {
                salt += line.charAt(strIndex);
                strIndex++;
            }
            salts.add(salt);
            while (line.charAt(strIndex) != ':') {
                encPwd += line.charAt(strIndex);
                strIndex++;
            }
            encPwds.add(encPwd);
            strIndex += 9;
            while (line.charAt(strIndex) != ':') {
                fullName += line.charAt(strIndex);
                strIndex++;
            }
            int index = 0;
            while (fullName.charAt(index) != ' ') {
                firstName += fullName.charAt(index);
                index++;
            }
            firstNames.add(firstName.toLowerCase());
            index++;
            while (index != fullName.length()) {
                lastName += fullName.charAt(index);
                index++;

            }
            lastNames.add(lastName.toLowerCase());
        }
        ArrayList<String> dictWords = dictToArray();
        wordAsIs(firstNames, salts, encPwds, start);
        wordAsIs(lastNames, salts, encPwds, start);
        wordAsIs(dictWords, salts, encPwds, start);
        backWords(firstNames, salts, encPwds, start);
        backWords(lastNames, salts, encPwds, start);
        backWords(dictWords, salts, encPwds, start);
        //firstNameMangle(firstNames, salts, encPwds, lastNames, start);
        br.close();
    }

    public static ArrayList<String> dictToArray() throws IOException {
        String fileName = "dictionary";
        File file = new File(fileName);
        fileName = file.getAbsolutePath();
        BufferedReader br;
        br = new BufferedReader(new FileReader(file));
        ArrayList<String> ret = new ArrayList<String>();
        String line = "";
        while ((line = br.readLine()) != null) {
            ret.add(line);
        }
        br.close();
        return ret;
    }

    //checking the words as they are and enrypting with salt
    public static void wordAsIs (ArrayList<String> w, ArrayList<String> s, ArrayList<String> e, long t) {
        boolean names = (w.size() == s.size());
        for(int i = 0; i < s.size(); i++) {
            if(s.get(i) != null && names) {
                String cur = jcrypt.crypt(s.get(i), w.get(i));
                String enc = s.get(i) + e.get(i);
                if(cur.equals(enc)) {
                    long curTime = System.currentTimeMillis();
                    long crackTime = curTime - t;
                    System.out.println(i + ": " + cur + " " + crackTime);
                    w.set(i, null);
                    s.set(i, null);
                    e.set(i, null);
                }
            } else if(s.get(i) != null && !names) {
                //this is for the case where the password hasn't been cracked already
                //and the w arraylist isnt the first or last names
                //because we only wanna compare the salt to every string in the arraylist
                //if that arraylist is of the dictionary words not the first and last names
                for(int j = 0; j < w.size(); j++) {
                    if(s.get(i) != null) {
                        String cur = jcrypt.crypt(s.get(i), w.get(j));
                        String enc = s.get(i) + e.get(i);
                        if(cur.equals(enc)) {
                            long curTime = System.currentTimeMillis();
                            long crackTime = curTime - t;
                            System.out.println(i + ": " + cur + " " + crackTime);
                            s.set(i, null);
                            e.set(i, null);
                        }
                    }
                }
            }
        }
    }

    //just reverses words and encrypts them with the salt that way
    public static void backWords (ArrayList<String> w, ArrayList<String> s, ArrayList<String> e, long t) {
        boolean names = (w.size() == s.size());
        for(int i = 0; i < s.size(); i++) {
            if(s.get(i) != null && names) {
                String forward = w.get(i);
                int length = forward.length();
                String back = "";
                for(int j = length - 1; j >= 0; j--) {
                    back += forward.charAt(j);
                }
                String cur = jcrypt.crypt(s.get(i), back);
                String enc = s.get(i) + e.get(i);
                if(cur.equals(enc)) {
                    long curTime = System.currentTimeMillis();
                    long crackTime = curTime - t;
                    System.out.println(i + ": " + cur + " " + crackTime);
                    w.set(i, null);
                    s.set(i, null);
                    e.set(i, null);
                }
            } else if(s.get(i) != null && !names) { 
                //this is for the case where the password hasn't been cracked already
                //and the w arraylist isnt the first or last names
                //because we only wanna compare the salt to every string in the arraylist
                //if that arraylist is of the dictionary words not the first and last names
                for(int j = 0; j < w.size(); j++) {
                    if(s.get(i) != null) {
                        String forward = w.get(i);
                        int length = forward.length();
                        String back = "";
                        for(int k = length - 1; k >= 0; k--) {
                            back += forward.charAt(k);
                        }
                        String cur = jcrypt.crypt(s.get(i), back);
                        String enc = s.get(i) + e.get(i);
                        if(cur.equals(enc)) {
                            long curTime = System.currentTimeMillis();
                            long crackTime = curTime - t;
                            System.out.println(i + ": " + cur + " " + crackTime);
                            s.set(i, null);
                            e.set(i, null);
                        }
                    }
                }
            }
        }
    }

/* the following stuff was me only handling mangles with first name but i think its more efficient
    to do it the way above; im just leaving this here in case it doesn't make sense
*/
/*
    public static void firstNameMangle (ArrayList<String> f, ArrayList<String> s, 
                                        ArrayList<String> e, ArrayList<String> l, long t) {
        //checking regular first name with salt
        int index = 0;
        for(int i = 0; i < f.size(); i++) {
            String cur = jcrypt.crypt(s.get(index), f.get(index));
            String enc = s.get(index) + e.get(index);
            if(cur.equals(enc)) {
                long curTime = System.currentTimeMillis();
                long crackTime = curTime - t;
                System.out.println(index + ": " + cur + " " + crackTime);
                f.set(index, null);
                s.set(index, null);
                e.set(index, null);
                l.set(index, null);
            }
            index++;
        }

        //checking first name backwards with salt
        index = 0;
        for(int i = 0; i < f.size(); i++) {
            if(f.get(index) != null) {
                String forward = f.get(index);
                int length = forward.length();
                String back = "";
                for(int j = length - 1; j >= 0; j--) {
                    back += forward.charAt(j);
                }
                String cur = jcrypt.crypt(s.get(index), back);
                String enc = s.get(index) + e.get(index);
                if(cur.equals(enc)) {
                    long curTime = System.currentTimeMillis();
                    long crackTime = curTime - t;
                    System.out.println(index + ": " + cur + " " + crackTime);
                    f.set(index, null);
                    s.set(index, null);
                    e.set(index, null);
                    l.set(index, null);
                }
            }
            index++;
        }
    }
*/
}