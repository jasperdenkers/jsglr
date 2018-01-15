package org.spoofax.jsglr2.measure;

import java.util.Queue;

import org.spoofax.jsglr2.actions.IAction;
import org.spoofax.jsglr2.actions.IReduce;
import org.spoofax.jsglr2.elkhound.AbstractElkhoundStackNode;
import org.spoofax.jsglr2.parseforest.AbstractParseForest;
import org.spoofax.jsglr2.parseforest.hybrid.ParseNode;
import org.spoofax.jsglr2.parser.ForShifterElement;
import org.spoofax.jsglr2.parser.Parse;
import org.spoofax.jsglr2.parser.ParseFailure;
import org.spoofax.jsglr2.parser.ParseSuccess;
import org.spoofax.jsglr2.parser.observing.IParserObserver;
import org.spoofax.jsglr2.parsetable.IProduction;
import org.spoofax.jsglr2.stack.StackLink;
import org.spoofax.jsglr2.stack.collections.IForActorStacks;
import org.spoofax.jsglr2.states.IState;

public class ParserMeasureObserver<ParseForest extends AbstractParseForest>
    implements IParserObserver<ParseForest, AbstractElkhoundStackNode<ParseForest>> {

    public long length = 0;
    Parse<ParseForest, AbstractElkhoundStackNode<ParseForest>> parse;

    public long stackNodes = 0;
    public long stackLinks = 0;
    public long stackLinksRejected = 0;

    public long actors;

    public long doReductions = 0;
    public long doLimitedReductions = 0;

    public long doReductionsLR = 0;
    public long doReductionsDeterministicGLR = 0;
    public long doReductionsNonDeterministicGLR = 0;

    public long reducers = 0;
    public long reducersElkhound = 0;

    public long deterministicDepthResets = 0;

    public int parseNodes = 0, parseNodesAmbiguous = 0, parseNodesContextFree = 0, parseNodesLexical = 0,
        parseNodesLayout = 0, parseNodesContextFreeAmbiguous = 0, parseNodesLexicalAmbiguous = 0,
        parseNodesLayoutAmbiguous = 0;
    public int derivations = 0;
    public int characterNodes = 0;

    class Actor {
        public AbstractElkhoundStackNode<ParseForest> stack;
        public Iterable<IAction> applicableActions;

        public Actor(AbstractElkhoundStackNode<ParseForest> stack, Iterable<IAction> applicableActions) {
            this.stack = stack;
            this.applicableActions = applicableActions;
        }
    }

    class Reducer {
        public IReduce reduce;
        public ParseForest[] parseNodes;

        public Reducer(IReduce reduce, ParseForest[] parseNodes) {
            this.reduce = reduce;
            this.parseNodes = parseNodes;
        }
    }

    @Override
    public void parseStart(Parse<ParseForest, AbstractElkhoundStackNode<ParseForest>> parse) {
        this.parse = parse;

        length += parse.inputLength;
    }

    @Override
    public void parseCharacter(Parse<ParseForest, AbstractElkhoundStackNode<ParseForest>> parse,
        Iterable<AbstractElkhoundStackNode<ParseForest>> activeStacks) {
    }

    @Override
    public void addActiveStack(AbstractElkhoundStackNode<ParseForest> stack) {
    }

    @Override
    public void addForActorStack(AbstractElkhoundStackNode<ParseForest> stack) {
    }

    @Override
    public void findActiveStackWithState(IState state) {
    }

    @Override
    public void createStackNode(AbstractElkhoundStackNode<ParseForest> stack) {
        stackNodes++;
    }

    @Override
    public void createStackLink(StackLink<ParseForest, AbstractElkhoundStackNode<ParseForest>> link) {
        stackLinks++;
    }

    @Override
    public void resetDeterministicDepth(AbstractElkhoundStackNode<ParseForest> stack) {
        deterministicDepthResets++;
    }

    @Override
    public void rejectStackLink(StackLink<ParseForest, AbstractElkhoundStackNode<ParseForest>> link) {
        stackLinksRejected++;
    }

    @Override
    public void forActorStacks(IForActorStacks<AbstractElkhoundStackNode<ParseForest>> forActorStacks) {
    }

    @Override
    public void handleForActorStack(AbstractElkhoundStackNode<ParseForest> stack,
        IForActorStacks<AbstractElkhoundStackNode<ParseForest>> forActorStacks) {
    }

    @Override
    public void actor(AbstractElkhoundStackNode<ParseForest> stack,
        Parse<ParseForest, AbstractElkhoundStackNode<ParseForest>> parse, Iterable<IAction> applicableActions) {
        actors++;
    }

    @Override
    public void skipRejectedStack(AbstractElkhoundStackNode<ParseForest> stack) {
    }

    @Override
    public void
        addForShifter(ForShifterElement<ParseForest, AbstractElkhoundStackNode<ParseForest>> forShifterElement) {
    }

    @Override
    public void doReductions(Parse<ParseForest, AbstractElkhoundStackNode<ParseForest>> parse,
        AbstractElkhoundStackNode<ParseForest> stack, IReduce reduce) {
        doReductions++;

        if(stack.deterministicDepth >= reduce.arity()) {
            if(parse.activeStacks.isSingle())
                doReductionsLR++;
            else
                doReductionsDeterministicGLR++;
        } else {
            doReductionsNonDeterministicGLR++;
        }
    }

    @Override
    public void doLimitedReductions(Parse<ParseForest, AbstractElkhoundStackNode<ParseForest>> parse,
        AbstractElkhoundStackNode<ParseForest> stack, IReduce reduce,
        StackLink<ParseForest, AbstractElkhoundStackNode<ParseForest>> throughLink) {
        doLimitedReductions++;
    }

    @Override
    public void reducer(AbstractElkhoundStackNode<ParseForest> stack, IReduce reduce, ParseForest[] parseNodes,
        AbstractElkhoundStackNode<ParseForest> activeStackWithGotoState) {
        reducers++;
    }

    @Override
    public void reducerElkhound(AbstractElkhoundStackNode<ParseForest> stack, IReduce reduce,
        ParseForest[] parseNodes) {
        reducersElkhound++;
    }

    @Override
    public void directLinkFound(Parse<ParseForest, AbstractElkhoundStackNode<ParseForest>> parse,
        StackLink<ParseForest, AbstractElkhoundStackNode<ParseForest>> directLink) {
    }

    @Override
    public void accept(AbstractElkhoundStackNode<ParseForest> acceptingStack) {
    }

    @Override
    public void createParseNode(ParseForest parseForest, IProduction production) {
        parseNodes++;

        ParseNode parseNode = (ParseNode) parseForest;

        if(parseNode.production.isContextFree())
            parseNodesContextFree++;

        if(!parseNode.production.isLayout()
            && (parseNode.production.isLexical() || parseNode.production.isLexicalRhs()))
            parseNodesLexical++;

        if(parseNode.production.isLayout())
            parseNodesLayout++;
    }

    @Override
    public void createDerivation(int nodeNumber, IProduction production, ParseForest[] parseNodes) {
        derivations++;
    }

    @Override
    public void createCharacterNode(ParseForest characterNode, int character) {
        characterNodes++;
    }

    @Override
    public void addDerivation(ParseForest parseNode) {
    }

    @Override
    public void shifter(ParseForest termNode,
        Queue<ForShifterElement<ParseForest, AbstractElkhoundStackNode<ParseForest>>> forShifter) {
    }

    @Override
    public void remark(String remark) {
    }

    @Override
    public void success(ParseSuccess<ParseForest, ?> success) {
    }

    @Override
    public void failure(ParseFailure<ParseForest, ?> failure) {
        throw new IllegalStateException("Failing parses not allowed during measurements");
    }

}
