package org.spoofax.jsglr2.tests.characterclasses;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.function.Predicate;

import org.junit.Test;
import org.spoofax.jsglr2.characterclasses.CharacterClass;
import org.spoofax.jsglr2.characterclasses.ICharacterClass;
import org.spoofax.jsglr2.characterclasses.ICharacterClassFactory;

public class CharacterClassTest {

    ICharacterClassFactory factory = CharacterClass.factory();

    ICharacterClass AZ = factory.fromRange(65, 90);
    ICharacterClass az = factory.fromRange(97, 122);

    ICharacterClass x = factory.fromSingle(120);
    ICharacterClass eof = factory.fromSingle(CharacterClass.EOF_INT);

    private void testCharacterClass(ICharacterClass characters, Predicate<Integer> contains) {
        for(int i = 0; i <= CharacterClass.EOF_INT; i++) {
            boolean expected = characters.contains(i);

            assertEquals("Character " + i + " ('" + CharacterClass.intToString(i) + "') for characters "
                + characters.toString() + ":", contains.test(i), expected);
        }
    }

    @Test
    public void testLowerCaseLettersRange() {
        testCharacterClass(az, character -> {
            return 97 <= character && character <= 122;
        });

        assertEquals(az.contains('a'), true);
        assertEquals(az.contains('A'), false);
    }

    @Test
    public void testUppercaseCaseLettersRange() {
        testCharacterClass(AZ, character -> {
            return 65 <= character && character <= 90;
        });
    }

    @Test
    public void testLettersUnionRange() {
        ICharacterClass letters = factory.union(az, AZ);

        testCharacterClass(letters, character -> {
            return 65 <= character && character <= 90 || 97 <= character && character <= 122;
        });
    }

    @Test
    public void testSingletonRange() {
        testCharacterClass(x, character -> {
            return character == 120;
        });
    }

    @Test
    public void testSingletonRangeUnion() {
        ICharacterClass characters = factory.union(x, AZ);

        testCharacterClass(characters, character -> {
            return 65 <= character && character <= 90 || character == 120;
        });

        assertEquals(characters.contains('a'), false);
        assertEquals(characters.contains('B'), true);
        assertEquals(characters.contains('x'), true);
    }

    @Test
    public void testEOF() {
        ICharacterClass characters = factory.fromSingle(CharacterClass.EOF_INT);

        testCharacterClass(characters, character -> {
            return character == CharacterClass.EOF_INT;
        });
    }

    @Test
    public void testRangeEOFunion() {
        ICharacterClass characters = factory.union(az, factory.fromSingle(CharacterClass.EOF_INT));

        testCharacterClass(characters, character -> {
            return 97 <= character && character <= 122 || character == CharacterClass.EOF_INT;
        });
    }

    @Test
    public void testNewLineDetection() {
        char newLineChar = '\n';
        int newLineInt = newLineChar;

        assertEquals(CharacterClass.isNewLine(newLineChar), true);
        assertEquals(CharacterClass.isNewLine(newLineInt), true);
    }

    @Test
    public void testComparisons() {
        assertEquals(CharacterClass.comparator().compare(AZ, az), -1);
        assertEquals(CharacterClass.comparator().compare(az, AZ), 1);
        assertEquals(CharacterClass.comparator().compare(az, az), 0);
        assertEquals(CharacterClass.comparator().compare(AZ, AZ), 0);
        assertEquals(CharacterClass.comparator().compare(AZ, x), -1);
        assertEquals(CharacterClass.comparator().compare(x, AZ), 1);
        assertEquals(CharacterClass.comparator().compare(x, az), 1);
        assertEquals(CharacterClass.comparator().compare(az, x), -1);
        assertEquals(CharacterClass.comparator().compare(az, eof), -1);
        assertEquals(CharacterClass.comparator().compare(eof, az), 1);
    }

    @Test
    public void testDisjointSortanble() {
        assertEquals(CharacterClass.disjointSorted(Arrays.asList(AZ, az)), true);
        assertEquals(CharacterClass.disjointSorted(Arrays.asList(az, AZ)), false);
        assertEquals(CharacterClass.disjointSorted(Arrays.asList(AZ, AZ)), false);

        assertEquals(CharacterClass.disjointSortable(Arrays.asList(AZ, az)), true);
        assertEquals(CharacterClass.disjointSortable(Arrays.asList(az, AZ)), true);
        assertEquals(CharacterClass.disjointSortable(Arrays.asList(AZ, AZ)), false);

        assertEquals(CharacterClass.disjointSortable(Arrays.asList(az, x)), false);
        assertEquals(CharacterClass.disjointSortable(Arrays.asList(az, AZ, eof)), true);
    }

}
