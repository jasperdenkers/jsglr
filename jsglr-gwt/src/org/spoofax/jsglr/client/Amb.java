/*
 * Created on 30.mar.2006
 *
 * Copyright (c) 2005, Karl Trygve Kalleberg <karltk near strategoxt.org>
 *
 * Licensed under the GNU General Public License, v2
 */
package org.spoofax.jsglr.client;

import java.util.ArrayList;
import java.util.List;

import org.spoofax.jsglr.shared.terms.ATerm;
import org.spoofax.jsglr.shared.terms.ATermFactory;
import org.spoofax.jsglr.shared.terms.ATermList;


public class Amb extends IParseNode {

    private final List<IParseNode> alternatives;

    private int cachedHashCode = NO_HASH_CODE;

    Amb(IParseNode left, IParseNode right) {
        alternatives = new ArrayList<IParseNode>(2);
        alternatives.add(left);
        alternatives.add(right);
    }

    public Amb(List<IParseNode> alternatives) {
        this.alternatives = alternatives;
    }

    @Override
	public ATerm toParseTree(ParseTable pt) {

        ATermFactory factory = pt.getFactory();
        ATermList list = factory.makeList();
        list = addToParseTree(pt, factory, list);
        return pt.getFactory().makeAppl(pt.ambAFun, list);
    }

    private ATermList addToParseTree(ParseTable pt, ATermFactory factory,
            ATermList list) {

        for (int i = alternatives.size() - 1; i >= 0; i--) {
            IParseNode alt = alternatives.get(i);
            if (alt instanceof Amb) {
                list = ((Amb) alt).addToParseTree(pt, factory, list);
            } else {
                list = list.prepend(alt.toParseTree(pt));
            }
        }
        return list;
    }

    @Override
	@Deprecated
    void clear() {
        for (int i = 0; i < alternatives.size(); i++) {
            alternatives.get(i).clear();
        }
        alternatives.clear();
        cachedHashCode = NO_HASH_CODE;
    }

    @Override
    public String toString() {
        return "amb(" + alternatives + ")";
    }

    public boolean hasAmbiguity(IParseNode newNode) {
        throw new NotImplementedException();
    }

    public List<IParseNode> getAlternatives() {
        return alternatives;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Amb))
            return false;
        if (obj == this)
            return true;
        Amb o = (Amb)obj;
        if(o.alternatives.size() != alternatives.size()
                || o.hashCode() != hashCode())
            return false;
        for(int i=0;i<alternatives.size();i++)
            if(!alternatives.get(i).equals(o.alternatives.get(i)))
                return false;
        return true;
    }

    @Override
    public int hashCode() {
        if (cachedHashCode != NO_HASH_CODE) {
            assert cachedHashCode == alternatives.hashCode();
            return cachedHashCode;
        }
        int result = cachedHashCode = alternatives.hashCode();
        return result;
    }

    @Override
    public String toStringShallow() {
        return "Amb";
    }
}
