/*
 * Created on 05.des.2005
 *
 * Copyright (c) 2005, Karl Trygve Kalleberg <karltk near strategoxt.org>
 *
 * Licensed under the GNU Lesser General Public License, v2.1
 */
package org.spoofax.jsglr.client;




public class Reduce extends ActionItem {

    private static final long serialVersionUID = 8620275049778432244L;

    public final int arity;

    public final int label;

    public final int status;

    public final Production production;

    public Reduce(int arity, int label, int status, boolean isRecoverAction, boolean isBracketAction, boolean isCompletionAction, boolean isPlaceholderInsertionAction, boolean isLiteralCompletionAction) {

        super(REDUCE);

        this.arity = arity;
        this.label = label;
        this.status = status;

        production = new Production(arity, label, status, isRecoverAction, isBracketAction, isCompletionAction, isPlaceholderInsertionAction, isLiteralCompletionAction);
    }


    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Reduce))
            return false;
        Reduce o = (Reduce)obj;
        return arity == o.arity && label == o.label && status == o.status;
    }

    @Override
    public int hashCode() {
        return arity + status * 10 + label * 100;
    }


    @Override
	public String toString() {
        return "reduce(" + arity + ", " + label + ", " + status + ")";
    }
}
