package org.spoofax.jsglr.client.incremental;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.spoofax.jsglr.client.incremental.IncrementalSGLR.DEBUG;
import static org.spoofax.jsglr.client.incremental.IncrementalSGLR.isRangeOverlap;
import static org.spoofax.jsglr.client.incremental.IncrementalSGLR.tryGetListIterator;

import java.util.Iterator;
import java.util.Set;

import org.spoofax.jsglr.client.imploder.IAstNode;
import org.spoofax.jsglr.client.imploder.IToken;

/**
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class IncrementalInputBuilder {
	
	private static final boolean INSERT_WHITESPACE = false;

	private final StringBuilder result = new StringBuilder();
	
	private final Set<String> incrementalSorts;
	
	private final String input;

	@SuppressWarnings("unused") // for debugging
	private final String oldInput;
	
	private final int damageStart;
	
	/**
	 * The (inclusive) end offset of the damage region.
	 */
	private final int damageEnd;

	private final int damageSizeChange;
	
	private boolean isSkipping;
	
	private boolean isDamagePrinted;
	
	private int skippedChars;

	/**
	 * @param incrementalSorts
	 *            Sorts that can be incrementally parsed (e.g., MethodDec, ImportDec).
	 *            *Must* be sorts that only occur in lists (such as MethodDec*).
	 */
	public IncrementalInputBuilder(Set<String> incrementalSorts, String input, String oldInput,
			int damageStart, int damageEnd, int damageSizeChange) {
		this.incrementalSorts = incrementalSorts;
		this.input = input;
		this.oldInput = oldInput;
		this.damageEnd = damageEnd;
		this.damageStart = damageStart;
		this.damageSizeChange = damageSizeChange;
	}

	public String buildPartialInput(IAstNode oldTree) throws IncrementalSGLRException {
		isSkipping = isDamagePrinted = false;
		skippedChars = 0;
		appendTree(oldTree);
		try {
			return result.toString();
		} finally {
			if (DEBUG) System.out.println();
		}
	}
	
	/**
	 * Gets the number of characters *before* the damaged region
	 * that were in the original input string but not in 
	 * the last incremental input string built by this instance.
	 */
	public int getLastSkippedChars() {
		return skippedChars;
	}

	private void appendTree(IAstNode oldTree) throws IncrementalSGLRException {
		IToken left = oldTree.getLeftToken();
		IToken right = oldTree.getRightToken();
		int startOffset = 0;
		int endOffset = 0;
		boolean isSkippingStart = false;
		
		if (left != null && right != null) {
			startOffset = left.getStartOffset();
			endOffset = right.getEndOffset();
			
			if (!isSkipping && !oldTree.isList() && incrementalSorts.contains(oldTree.getSort())
					&& !isRangeOverlap(damageStart, damageEnd /*- damageSizeChange*/, startOffset, endOffset)) {
				isSkipping = isSkippingStart = true;
			}

			Iterator<IAstNode> iterator = tryGetListIterator(oldTree); 
			for (int i = 0, max = oldTree.getChildCount(); i < max; i++) {
				IAstNode child = iterator == null ? oldTree.getChildAt(i) : iterator.next();
				IToken childLeft = child.getLeftToken();
				IToken childRight = child.getRightToken();
				if (childLeft != null)
					appendToken(startOffset, childLeft.getStartOffset() - 1);
				appendTree(child);
				if (childRight != null)
					startOffset = childRight.getEndOffset() + 1;
			}
			appendToken(startOffset, endOffset);
		} else {
			assert oldTree.getChildCount() == 0 :
				"No tokens for tree with children??";
		}
		
		if (isSkippingStart) isSkipping = false;
	}
	
	/**
	 * Appends a token with the given startOffset
	 * and endOffset (inclusive). If the token overlaps
	 * with the damaged region, it is discarded
	 * or merged with the damaged region as necessary.
	 */
	private void appendToken(int startOffset, int endOffset) {
		if (isDamagePrinted /* startOffset >= damageStart */) {
			assert startOffset >= damageStart;
			if (endOffset > damageEnd) {
				int newStartOffset = max(damageEnd + damageSizeChange + 1, startOffset + damageSizeChange);
				internalAppendSubstring(newStartOffset, endOffset + damageSizeChange + 1);
			}
			
		} else {
			if (endOffset >= damageStart) {
				int tokenLength = endOffset - startOffset + 1;
				int charsBeforeDamage = damageStart - startOffset;
				int charsAfterDamage = max(0, endOffset - damageEnd);
				assert charsBeforeDamage + charsAfterDamage <= tokenLength
					&& min(charsBeforeDamage, charsAfterDamage) >= 0;
				if (DEBUG) System.out.print('|');
				internalAppendSubstring(startOffset, startOffset + charsBeforeDamage);
				if (DEBUG) System.out.print('$');
				internalAppendSubstring(damageStart, damageEnd + damageSizeChange + 1);
				if (DEBUG) System.out.print('$');
				internalAppendSubstring(damageEnd + damageSizeChange + 1,
							damageEnd + damageSizeChange + 1 + charsAfterDamage);
				if (DEBUG) System.out.print('|');
				isDamagePrinted = true;
			} else {
				internalAppendSubstring(startOffset, endOffset + 1);
			}
		}
 	}
	
	/**
	 * Directly appends a substring with a given 
	 * startIndex and endIndex (exclusive).
	 * 
	 * @param startIndex  The start index or offset of the string.
	 * @param endIndex    The endIndex of the string (exclusive),
	 *                    equal to endOffset + 1.
	 */
	private void internalAppendSubstring(int startIndex, int endIndex) {
		if (isSkipping) {
			for (int i = startIndex; i < endIndex; i++) {
				// if (DEBUG) System.out.print(input.charAt(i) == '\n' ? "\n" : "-" + input.charAt(i));
				if (input.charAt(i) == '\n') {
					result.append('\n');
				} else if (INSERT_WHITESPACE) {
					result.append(' ');
				} else if (i < damageStart) {
					skippedChars++;
				}
			}
		} else {
			if (DEBUG) System.out.print(input.substring(startIndex, endIndex));
			result.append(input, startIndex, endIndex);
		}
	}
}
