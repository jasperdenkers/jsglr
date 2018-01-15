package org.spoofax.jsglr2.characterclasses;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CharacterClass {

    public static int EOF_INT = 256;

    public static final ICharacterClass EOF_SINGLETON = new CharacterClassSingle(EOF_INT);

    public static ICharacterClassFactory factory() {
        return new CharacterClassFactory(true, true);
    }

    public static String intToString(int character) {
        if(character == EOF_INT)
            return "EOF";
        else
            return "" + (char) character;
    }

    public static boolean isNewLine(int character) {
        return character != EOF_INT && ((char) character) == '\n';
    }

    public static Comparator<ICharacterClass> comparator() {
        return (one, two) -> {
            return Integer.compare(one.min(), two.min());
        };
    }

    public static boolean disjointSortable(List<ICharacterClass> original) {
        List<ICharacterClass> sorted = new ArrayList<>(original);

        Collections.sort(sorted, comparator());

        return disjointSorted(sorted);
    }

    /*
     * Returns true if each character class only contains characters bigger than the characters in the previous
     * character class.
     */
    public static boolean disjointSorted(List<ICharacterClass> characterClasses) {
        for(int i = 0; i < characterClasses.size() - 1; i++) {
            if(characterClasses.get(i).max() >= characterClasses.get(i + 1).min())
                return false;
        }

        return true;
    }

}
