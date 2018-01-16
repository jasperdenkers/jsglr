package org.spoofax.jsglr2.characterclasses;

public final class CharacterClassSingle implements ICharacterClass {

    private final int character;

    public CharacterClassSingle(int character) {
        this.character = character;
    }

    @Override
    public final boolean contains(int character) {
        return this.character == character;
    }

    @Override
    public int min() {
        return character;
    }

    @Override
    public int max() {
        return character;
    }

    public final CharacterClassRangeSet rangeSetUnion(CharacterClassRangeSet rangeSet) {
        return rangeSet.addSingle(character);
    }

    public final CharacterClassRangeSet rangeSetIntersection(CharacterClassRangeSet rangeSet) {
        if(rangeSet.contains(character))
            return CharacterClassRangeSet.EMPTY_CONSTANT.addSingle(character);
        else
            return CharacterClassRangeSet.EMPTY_CONSTANT;
    }

    public final CharacterClassRangeSet rangeSetDifference(CharacterClassRangeSet rangeSet) {
        return rangeSet.removeSingle(character);
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(character);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        CharacterClassSingle that = (CharacterClassSingle) o;

        return this.character == that.character;
    }

    @Override
    public final String toString() {
        return "{" + CharacterClass.intToString(character) + "}";
    }

}
