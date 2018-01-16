package org.spoofax.jsglr2.characterclasses;

import org.spoofax.jsglr2.util.Cache;

public class CharacterClassFactory implements ICharacterClassFactory {

    final private boolean optimize;
    final private boolean cache;

    private Cache<ICharacterClass> characterClassCache;

    public CharacterClassFactory(boolean optimize, boolean cache) {
        this.optimize = optimize;
        this.cache = cache;

        if(cache)
            this.characterClassCache = new Cache<>();
    }

    @Override
    public CharacterClassRangeSet fromEmpty() {
        return CharacterClassRangeSet.EMPTY_CONSTANT;
    }

    @Override
    public final ICharacterClass fromSingle(int character) {
        return new CharacterClassSingle(character);
    }

    @Override
    public final ICharacterClass fromRange(int from, int to) {
        return fromEmpty().addRange(from, to);
    }

    @Override
    public final ICharacterClass union(ICharacterClass a, ICharacterClass b) {
        boolean aIsRangeSet = a instanceof CharacterClassRangeSet;
        boolean bIsRangeSet = b instanceof CharacterClassRangeSet;

        if(aIsRangeSet || bIsRangeSet) {
            CharacterClassRangeSet rangeSet;
            ICharacterClass other;

            if(aIsRangeSet) {
                rangeSet = (CharacterClassRangeSet) a;
                other = b;
            } else {
                rangeSet = (CharacterClassRangeSet) b;
                other = a;
            }

            return rangeSetUnion(rangeSet, other);
        } else {
            CharacterClassRangeSet result = fromEmpty();

            result = rangeSetUnion(result, a);
            result = rangeSetUnion(result, b);

            return result;
        }
    }

    @Override
    public final ICharacterClass intersection(ICharacterClass a, ICharacterClass b) {
        boolean aIsRangeSet = a instanceof CharacterClassRangeSet;
        boolean bIsRangeSet = b instanceof CharacterClassRangeSet;

        if(aIsRangeSet || bIsRangeSet) {
            CharacterClassRangeSet rangeSet;
            ICharacterClass other;

            if(aIsRangeSet) {
                rangeSet = (CharacterClassRangeSet) a;
                other = b;
            } else {
                rangeSet = (CharacterClassRangeSet) b;
                other = a;
            }

            return rangeSetIntersection(rangeSet, other);
        } else {
            CharacterClassRangeSet result = fromEmpty();

            result = rangeSetUnion(result, a);
            result = rangeSetIntersection(result, b);

            return result;
        }
    }

    @Override
    public final ICharacterClass difference(ICharacterClass a, ICharacterClass b) {
        CharacterClassRangeSet aRangeSet;

        if(a instanceof CharacterClassRangeSet)
            aRangeSet = (CharacterClassRangeSet) a;
        else {
            aRangeSet = fromEmpty();

            aRangeSet = rangeSetUnion(aRangeSet, a);
        }

        return rangeSetDifference(aRangeSet, b);
    }

    private final CharacterClassRangeSet rangeSetUnion(CharacterClassRangeSet rangeSet, ICharacterClass other) {
        if(other instanceof CharacterClassRangeSet)
            return ((CharacterClassRangeSet) other).rangeSetUnion(rangeSet);
        else if(other instanceof CharacterClassSingle)
            return ((CharacterClassSingle) other).rangeSetUnion(rangeSet);
        else
            throw new IllegalStateException();
    }

    private final CharacterClassRangeSet rangeSetIntersection(CharacterClassRangeSet rangeSet, ICharacterClass other) {
        if(other instanceof CharacterClassRangeSet)
            return ((CharacterClassRangeSet) other).rangeSetIntersection(rangeSet);
        else if(other instanceof CharacterClassSingle)
            return ((CharacterClassSingle) other).rangeSetIntersection(rangeSet);
        else
            throw new IllegalStateException();
    }

    private final CharacterClassRangeSet rangeSetDifference(CharacterClassRangeSet rangeSet, ICharacterClass other) {
        if(other instanceof CharacterClassRangeSet)
            return ((CharacterClassRangeSet) other).rangeSetDifference(rangeSet);
        else if(other instanceof CharacterClassSingle)
            return ((CharacterClassSingle) other).rangeSetDifference(rangeSet);
        else
            throw new IllegalStateException();
    }

    @Override
    public ICharacterClass finalize(ICharacterClass characterClass) {
        ICharacterClass optimized;

        if(characterClass instanceof CharacterClassRangeSet && optimize)
            optimized = ((CharacterClassRangeSet) characterClass).optimized();
        else
            optimized = characterClass;

        if(cache)
            return characterClassCache.cached(optimized);
        else
            return optimized;
    }


}
