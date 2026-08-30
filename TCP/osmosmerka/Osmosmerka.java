package TCP.osmosmerka;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Osmosmerka {

    private final char[][] letters;
    private final List<String> words;


    public Osmosmerka(String path) throws FileNotFoundException {
        this.letters = new char[8][];
        this.words = new ArrayList<>();

        try(
                Scanner in = new Scanner(new BufferedInputStream(new FileInputStream(path)))
        ){
            for(int i = 0; i < 8; i++){
                letters[i] = in.nextLine().toCharArray();
            }
            this.words.addAll(List.of(in.nextLine().split("\\|")));
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        Arrays.stream(this.letters).forEach(builder::append);
        this.words.forEach(w -> builder.append(w).append("|"));
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    public List<String> getWords(){
        return words;
    }

    public boolean N(int i, int j, String word){
        if(i < word.length() - 1) return false;

        for (int k = 0; k < word.length(); k++)
            if(this.letters[i-k][j] != word.charAt(k))
                return false;

        return true;
    }

    public boolean S(int i, int j, String word){
        if(i + word.length() > 8) return false;

        for (int k = 0; k < word.length(); k++)
            if(this.letters[i+k][j] != word.charAt(k))
                return false;

        return true;
    }

    public boolean E(int i, int j, String word){
        if(j + word.length() > 8 ) return false;

        for (int k = 0; k < word.length(); k++)
            if(this.letters[i][j+k] != word.charAt(k))
                return false;

        return true;
    }

    public boolean W(int i, int j, String word){
        if(j < word.length() - 1) return false;

        for (int k = 0; k < word.length(); k++)
            if(this.letters[i][j-k] != word.charAt(k))
                return false;

        return true;
    }

    public boolean NE(int i, int j, String word){
        if(i < word.length() - 1 || j + word.length() > 8) return false;

        for (int k = 0; k < word.length(); k++)
            if(this.letters[i-k][j+k] != word.charAt(k))
                return false;

        return true;
    }

    public boolean NW(int i, int j, String word){
        if(i < word.length() - 1 || j < word.length() - 1) return false;

        for (int k = 0; k < word.length(); k++)
            if(this.letters[i-k][j-k] != word.charAt(k))
                return false;

        return true;
    }

    public boolean SE(int i, int j, String word){
        if(i + word.length() > 8 || j + word.length() > 8) return false;

        for (int k = 0; k < word.length(); k++)
            if(this.letters[i+k][j+k] != word.charAt(k))
                return false;

        return true;
    }

    public boolean SW(int i, int j, String word){
        if(i + word.length() > 8 || j < word.length() - 1) return false;

        for (int k = 0; k < word.length(); k++)
            if(this.letters[i+k][j-k] != word.charAt(k))
                return false;

        return true;
    }
}
