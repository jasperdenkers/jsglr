/*
 * Created on 17.apr.2006
 *
 * Copyright (c) 2005, Karl Trygve Kalleberg <karltk@ii.uib.no>
 * 
 * Licensed under the GNU General Public License, v2
 */
package org.spoofax.jsglr;

import java.util.List;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import aterm.ATerm;

public class ParseReject extends ParseNode {

    public ParseReject(int label, List<IParseNode> kids) {
        super(label, kids);
    }

    @Override
    public ATerm toParseTree(ParseTable pt) {
        throw new NotImplementedException();
    }

}