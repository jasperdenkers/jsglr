package org.spoofax.jsglr2.parsetable;

import org.spoofax.jsglr2.actions.IAction;
import org.spoofax.jsglr2.actions.IReduce;
import org.spoofax.jsglr2.parser.Parse;

public interface ICharacterToActions {

    /*
     * Returns all actions.
     */
    IAction[] getActions();

    /*
     * Returns actions applicable to the given character.
     */
    Iterable<IAction> getActions(int character);

    /*
     * Returns reduce actions applicable to the given character.
     */
    Iterable<IReduce> getReduceActions(Parse parse);

}