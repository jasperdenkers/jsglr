package org.spoofax.interpreter.library.jsglr.treediff;

import java.util.ArrayList;
import java.util.Arrays;

import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.attachments.ParentAttachment;

/**
 * Constructs a (symmetric) matching relation between terms in AST1 and AST2.
 * The matching is stored as attachments to matched terms.
 * The matching is constructed in three phases: 
 * 1. LCS on based on leaf nodes
 * 2. Bottom-up matching of terms to other terms based on the number of matched leaf nodes and structural similarity
 * 3. improvement of the matching relation by matching terms that occur
 * at the same child index of their matched parents (with the same signature)  
 * @author maartje
 *
 */
public class HeuristicTreeMatcher extends AbstractTreeMatcher {
	
	private boolean requireSameSignature;
	private boolean requireSameValue;
	
	/**
	 * Matches terms in AST1 and AST2 by looking at the text fragment that they interpret.
	 * @param requireSameSignature Only allows matching of non-terminals with the same signature
	 * @param requireSameValue Only allows matching of leaf nodes if they have the same value
	 * @param says whether LCS must be repeated to match the leafnodes that are not matched in the first LCS matching round.
	 */
	public HeuristicTreeMatcher(boolean requireSameSignature, boolean requireSameValue, boolean tryMatchingMovedTerms){
		super(new LCSEqualTermsCommand<IStrategoTerm>(), tryMatchingMovedTerms);
		this.requireSameSignature = requireSameSignature;
		this.requireSameValue = requireSameValue;
	}
	
	/**
	 * Collects as candidates for matching terms that are the parent of matched subterms,
	 * plus the common ancestors of the candidates (until the smallest common ancestor is found).
	 */
	ArrayList<IStrategoTerm> getCandidateMatchTerms(IStrategoTerm root1, IStrategoTerm t2) {
		ArrayList<IStrategoTerm> candidateMatches = new ArrayList<IStrategoTerm>();
		for (int i = 0; i < t2.getSubtermCount(); i++) {
			IStrategoTerm subTermMatch1 = TermMatchAttachment.getMatchedTerm(t2.getSubterm(i));
			if(subTermMatch1 != null){
				IStrategoTerm parent1 = ParentAttachment.getParent(subTermMatch1);
				if(parent1 != null)
					addCandidate(candidateMatches, parent1);
			}
		}
		if(candidateMatches.size() > 1){
			IStrategoTerm commonAncestor = commonAncestor(candidateMatches);
			addCandidate(candidateMatches, commonAncestor);
			for (int i = 0; i < candidateMatches.size(); i++) {
				IStrategoTerm ancestorCandidate = candidateMatches.get(i);
				while(ancestorCandidate != commonAncestor){
					assert ancestorCandidate != null;
					addCandidate(candidateMatches, ancestorCandidate);
					ancestorCandidate = ParentAttachment.getParent(ancestorCandidate);
				}
			}
		}
		return candidateMatches;
	}

	private void addCandidate(ArrayList<IStrategoTerm> candidateMatches, IStrategoTerm candidate) {
		if(!contains(candidateMatches, candidate)) 
			candidateMatches.add(candidate);
	}

	private IStrategoTerm commonAncestor(ArrayList<IStrategoTerm> terms) {
		ArrayList<IStrategoTerm> ancestors = new ArrayList<IStrategoTerm>();
		IStrategoTerm anc = terms.get(0);
		while(anc != null){
			ancestors.add(anc);
			anc = ParentAttachment.getParent(anc);
		}
		int index_common_ancestor = -1;
		for (int i = 0; i < terms.size(); i++) {
			IStrategoTerm c = terms.get(i);
			while(c != null && !ancestors.contains(c)){
				c = ParentAttachment.getParent(c);					
			}
			assert c != null: "at least the root node is a common ancestor";
			index_common_ancestor = Math.max(index_common_ancestor, ancestors.indexOf(c));
		}
		IStrategoTerm commonAncestor = ancestors.get(index_common_ancestor);
		return commonAncestor;
	}
	
	/**
	 * Calculates a weight matching score based on:
	 * 1. number of matched leafnodes
	 * 2. matching of the parent terms
	 * 3. same signature and/or equal
	 */
	double matchingScore(IStrategoTerm t1, IStrategoTerm t2){
		if(!meetsMatchingCriteria(t1, t2))
			return -1.0;
		if(HelperFunctions.haveTupleOrListType(t1, t2) && hasMatchedParents(t1, t2) && hasMatchingChildIndex(t1, t2)){
			return 1.0;
		}
		ArrayList<IStrategoTerm> leafnodes1 = HelperFunctions.collectLeafnodes(t1);
		ArrayList<IStrategoTerm> leafnodes2 = HelperFunctions.collectLeafnodes(t2);
		double maxValue = 
			2.0 /*equal signatures*/ 
		  + 2.0 /*equal terms*/ 
		  + 2.0 /*matched parents*/ 
		  + 2.0 /*matched child index*/ 
		  + leafnodes1.size() + leafnodes2.size();		
		double value = 0.0;
		
		//structure similarity
		if(HelperFunctions.haveSameSignature(t1, t2) || HelperFunctions.isPrimitiveWithSameValue(t1, t2)){
			value += 2.0; //+2 for equal signatures or equal values
			if(t1.equals(t2)){
				value +=2.0; //+2 for equal terms
			}
		}
		
		//number of matched leafnodes, e.g., do both terms interpret the same text fragment
		for (IStrategoTerm ln2 : leafnodes2) {
			IStrategoTerm ln2Partner = TermMatchAttachment.getMatchedTerm(ln2);
			if(leafnodes1.contains(ln2Partner)){
				value += 2.0; //+2 for matched leafnodes
			}
		}
		
		//whether or not the parent nodes are matched
		if (hasMatchedParents(t1, t2)){
			value += 2.0; //+2 for matched parents
			if(hasMatchingChildIndex(t1, t2)){
				value +=2.0; //+2 for matched child index
			}
		}
		
		return 1.0 * value/maxValue;
	}

	private boolean hasMatchingChildIndex(IStrategoTerm t1, IStrategoTerm t2) {
		if(!hasMatchedParents(t1, t2))
			return false;
		IStrategoTerm parent1 = ParentAttachment.getParent(t1);
		IStrategoTerm parent2 = ParentAttachment.getParent(t2);
		int childIndex1 = getChildIndex(parent1, t1);
		int childIndex2 = getChildIndex(parent2, t2);
		assert childIndex1 != -1 && childIndex2 != -1;
		return 
			HelperFunctions.haveSameSignature(parent1, parent2) &&
			Tools.isTermAppl(parent1) &&
			childIndex1 == childIndex2;
	}

	private int getChildIndex(IStrategoTerm parent, IStrategoTerm trm) {
		return Arrays.asList(parent.getAllSubterms()).indexOf(trm);
	}

	private boolean meetsMatchingCriteria(IStrategoTerm t1, IStrategoTerm t2) {
		if(t1 == null || t2 == null)
			return false;
		if(!HelperFunctions.isSameTermType(t1, t2)){
			return false;
		}
		if(!HelperFunctions.haveSameSignature(t1, t2) && Tools.isTermAppl(t1)){
			assert Tools.isTermAppl(t2): "Same term type required";
			return 
				!requireSameSignature && 
				hasMatchedParents(t1, t2) && 
				hasMatchingChildIndex(t1, t2); 
			//only match appl terms with different signatures in case they are not moved 
			//(but they can be mis-aligned list or tuple elements) 
		}
		if(HelperFunctions.isPrimitiveWithDifferentValues(t1, t2)){
			return !requireSameValue;
		}
		return true;
	}

	private boolean hasMatchedParents(IStrategoTerm t1, IStrategoTerm t2) {
		IStrategoTerm parent1 = ParentAttachment.getParent(t1);
		IStrategoTerm parent2 = ParentAttachment.getParent(t2);
		IStrategoTerm partnerParent1 = TermMatchAttachment.getMatchedTerm(parent1);
		boolean hasMatchingParents = (partnerParent1 == parent2 && partnerParent1 != null);
		return hasMatchingParents;
	}
}
