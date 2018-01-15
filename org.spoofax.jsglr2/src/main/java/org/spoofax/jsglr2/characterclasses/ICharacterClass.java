package org.spoofax.jsglr2.characterclasses;

/**
 * ASCII characters: integer representation [0, 255]
 *
 * End-of-file marker (EOF): integer representation 256
 */
public interface ICharacterClass {

    boolean contains(int character);

    int min();

    int max();

}
