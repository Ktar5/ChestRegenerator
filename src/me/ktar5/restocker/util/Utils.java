package me.ktar5.restocker.util;

import org.bukkit.ChatColor;

import java.util.*;

/**
 * Created by Carter on 4/7/2015.
 */
public class Utils {

    public static <A> A[] trim(A[] array){
        ArrayList<A> newarray = new ArrayList<>();
        for (A obj : array)
            if (obj != null)
                newarray.add(obj);
        return newarray.toArray(array);
    }

    public static <A> A[] removeDuplicates(A[] array) {
        Set<A> set = new HashSet<>();
        Collections.addAll(set, array);
        return set.toArray(array);
    }

    public static String colorString(String input){
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public static String removeColor(String input){
        return ChatColor.stripColor(input);
    }

    public static List<String> colorList(List<String> inputs){
        inputs.stream().forEachOrdered(Utils::colorString);
        return inputs;
    }

    public static String[] colorArray(String[] inputs){
        Arrays.stream(inputs).forEachOrdered(Utils::colorString);
        return inputs;
    }

    public static int limitNumber(int number, int limit) {
        return number > limit ? limit : number;
    }

    public static String limitString(String string, int limit) {
        return string.substring(0, Math.min(string.length(), limit));
    }

}
