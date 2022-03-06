package space.gavinklfong.demo;

public class Solution {

    public static int getLengthOfLastWord(String s) {
        if (s == null || s.length() == 0) return 0;

        int length = 0;

        for (int i = s.length() - 1; i >= 0; i--) {
            char c = s.charAt(i);
            if (c == ' ') {
                if (length == 0) {
                    continue;
                } else {
                    break;
                }
            } else {
                length++;
            }
        }

        return length;
    }

}
