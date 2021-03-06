/*
 * Created on 04.des.2005
 *
 * Copyright (c) 2005, Karl Trygve Kalleberg <karltk near strategoxt.org>
 * 
 * Licensed under the GNU Lesser General Public License, v2.1
 */
package org.spoofax.jsglr.client;

import org.spoofax.jsglr.shared.Tools;
import org.spoofax.terms.util.NotImplementedException;


public class Link {

    public static int linksCreated =0; //mj Testing
    
    protected Frame parent;

    protected AbstractParseNode label;

    private boolean rejected;
    
    public int length; //mj: private final (see sglr.reducer: replace link by link with less avoids)
    private int line;
    private int column;

    public int recoverWeight; //Weighted count used for disambiguation: Deletions are more expensive then Insertions
    public int recoverCount; //number of recover actions (used for cutting branches and error analysis)

    public boolean hasCompletedLabel; 
    
    public int placeholderCount;
    
    

    public Link(Frame destination, AbstractParseNode t, int length, int line, int column) {
        this.parent = destination;
        label = t;
        rejected = false;

        this.length = length;
        this.line = line;
        this.column = column;

        recoverWeight = 0;
        recoverCount = 0;
        
        hasCompletedLabel = false;
        placeholderCount = 0;

        linksCreated +=1;
    }

    public void reject() {
        if(Tools.tracing) {
            SGLR.TRACE("SG_MarkLinkRejected() - " + parent.state.stateNumber + ", " + length);
        }
        rejected = true;
        label.reject();
    }

    public boolean isRejected() {
        return rejected;
    }

    @Override
	public String toString() {
        return "" + parent.state.stateNumber;
    }

    public int getLength() {
        return length;
    }
    
    public int getLine() {
      return line;
    }

    public int getColumn() {
      return column;
    }

    public void clear() {
        if(parent != null) {
            parent = null;//todo.clear();
        }
        this.label = null;
    }

    public void addAmbiguity(AbstractParseNode t, int tokensSeen) {
    	assert(label instanceof ParseNode);
        if(Tools.tracing) {
            SGLR.TRACE("SG_CreateAmbCluster() - " + tokensSeen);
        }
        ((ParseNode)label).makeAmbiguity(t);
        //label = ParseNode.createAmbNode(label, t);
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Link))
            return false;
        Link o = (Link)obj;
        
        return o.parent == parent &&
        o.label == label &&
        o.rejected == rejected &&
        o.length == length;
    }
    
    @Override
    public int hashCode() {
    	throw new NotImplementedException();
    }
}