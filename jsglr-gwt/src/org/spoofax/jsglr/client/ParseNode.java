/*
 * Created on 30.mar.2006
 *
 * Copyright (c) 2005, Karl Trygve Kalleberg <karltk near strategoxt.org>
 *
 * Licensed under the GNU General Public License, v2
 */
package org.spoofax.jsglr.client;

import java.util.List;

import org.spoofax.jsglr.shared.terms.ATerm;
import org.spoofax.jsglr.shared.terms.ATermFactory;
import org.spoofax.jsglr.shared.terms.ATermList;

public class ParseNode extends IParseNode {

    public final int label;

    protected final List<IParseNode> kids;

    private int cachedHashCode;

    public ParseNode(int label, List<IParseNode> kids) {
        this.label = label;
        this.kids = kids;
    }

    @Override
	public ATerm toParseTree(ParseTable pt) {
        ATermFactory factory = pt.getFactory();

        ATermList l1 = factory.makeList();
        for (int i = kids.size() - 1; i >= 0; i--) {
            l1 = l1.prepend(kids.get(i).toParseTree(pt));
        }

        return factory.makeAppl(pt.applAFun, pt.getProduction(label), l1);
    }

    /**
     * todo: stolen from TAFReader; move elsewhere
     */
    public static ATermList makeList(ATermFactory factory, List<ATerm> terms) {
        ATermList result = factory.makeList();
        for (int i = terms.size() - 1; i >= 0; i--) {
            result = result.prepend(terms.get(i));
        }
        return result;
    }

    @Override
    public String toString() {
        return "regular(aprod(" + label + ")," + kids + ")";
    }

    public int getLabel() { return label; }

    public List<IParseNode> getKids() { return kids; }

    @Override
	@Deprecated
    void clear() {
        for (int i = 0; i < kids.size(); i++) {
            kids.get(i).clear();
        }
        kids.clear();
        cachedHashCode = NO_HASH_CODE;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof ParseNode))
            return false;
        if (obj == this)
            return true;
        ParseNode o = (ParseNode)obj;
        if(label != o.label || kids.size() != o.kids.size()
                || hashCode() != o.hashCode())
            return false;
        for(int i=0;i<kids.size();i++) {
            if(!kids.get(i).equals(o.kids.get(i)))
                return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        if (cachedHashCode != NO_HASH_CODE)
            return cachedHashCode;
        final int prime = 31;
        int result = prime * label;
        for(IParseNode n : kids)
            result += (prime * n.hashCode());
        cachedHashCode = result;
        return result;
    }

    @Override
    public String toStringShallow() {
        return "regular*(" + label + ", {" +  kids.size() + "})";
    }
}
