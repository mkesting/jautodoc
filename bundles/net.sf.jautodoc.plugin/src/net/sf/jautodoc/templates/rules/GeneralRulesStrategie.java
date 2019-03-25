/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates.rules;

import java.util.ArrayList;
import java.util.List;

import net.sf.jautodoc.ResourceManager;
import net.sf.jautodoc.utils.ResettableScanner;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;


/**
 * Provides general template scanning rules.
 */
public class GeneralRulesStrategie implements IRulesStrategy {

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.rules.IRulesStrategy#createRules()
	 */
	public List<IRule> createRules() {
		List<IRule> rules = new ArrayList<IRule>();
		
		// Add rule for parsed strings
		rules.add(new StringRule("\"", "\"", ResourceManager.getToken(ResourceManager.STRING)));
		
		// Add rule for unparsed strings
		rules.add(new StringRule("\'", "\'", ResourceManager.getToken(ResourceManager.STRING)));
		
		// Add rule for escaped references 
		rules.add(new StringRule("\\", "$", ResourceManager.getToken(ResourceManager.ESCAPES)));
		
		// Add rule for escaped directives 
        rules.add(new StringRule("\\", "#", ResourceManager.getToken(ResourceManager.ESCAPES)));
		
		// Add rule for hmtl marups
		rules.add(new MarkupRule(ResourceManager.getToken(ResourceManager.MARKUP)));
		
		// Add rules for operators
		rules.add(new OperatorRule(ResourceManager.getToken(ResourceManager.OPERATOR)));
		
		// Add rules for numbers
		rules.add(new NumberRule(ResourceManager.getToken(ResourceManager.NUMBER)));
		
		// Add rules for javadoc tags
		rules.add(new WordRule(new TagDetector(), ResourceManager.getToken(ResourceManager.TAG)));
		
		return rules;
	}

	// ----------------------------------------------------
	// inner classes
	// ----------------------------------------------------
	
	/**
	 * Rule for string detection.
	 */
	private static class StringRule extends SingleLineRule {
		private String endSequence;
		private ResettableScanner rScanner = new ResettableScanner();

		/**
		 * Instantiates a new string rule.
		 * 
		 * @param startSequence the start sequence
		 * @param endSequence the end sequence
		 * @param token the success token
		 */
		public StringRule(String startSequence, String endSequence, IToken token) {
			super(startSequence, endSequence, token);
			this.endSequence = endSequence;
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.text.rules.PatternRule#evaluate(org.eclipse.jface.text.rules.ICharacterScanner)
		 */
		public IToken evaluate(ICharacterScanner scanner) {
			rScanner.setScanner(scanner);
			IToken result = super.evaluate(rScanner);
			if (result != getSuccessToken() || !validate((TemplateCodeScanner)scanner)) {
				result = Token.UNDEFINED;
				rScanner.reset();
			}
			return result;
		}
		
		/**
		 * Validate existing end sequence.
		 * 
		 * @return true, if validate
		 */
		public boolean validate(TemplateCodeScanner scanner) {
			try {
				String token = scanner.getDocument().get(
						scanner.getTokenOffset(), scanner.getTokenLength());
				return token.endsWith(endSequence);
			} catch (BadLocationException e) {/* ignore */}
			
			return false;
		}
	}
	
	/**
	 * Rule for html markup detection.
	 */
	private static class MarkupRule extends SingleLineRule {
		private ResettableScanner rScanner = new ResettableScanner();

		public MarkupRule(IToken token) {
			super("<", ">", token);
		}

		/*
		 * @see PatternRule#evaluate(ICharacterScanner)
		 */
		public IToken evaluate(ICharacterScanner scanner) {
			rScanner.setScanner(scanner);
			IToken result= super.evaluate(rScanner);
			if (result != getSuccessToken() || !validate((TemplateCodeScanner)scanner)) {
				result = Token.UNDEFINED;
				rScanner.reset();
			}
			return result;
		}
		
		private boolean validate(TemplateCodeScanner scanner) {
			try {
				final String token = scanner.getDocument().get(
						scanner.getTokenOffset(), scanner.getTokenLength())	+ ".";

				int offset= 0;
				char character= token.charAt(++offset);

				if (character == '/')
					character= token.charAt(++offset);

				while (Character.isWhitespace(character))
					character= token.charAt(++offset);

				while (Character.isLetter(character))
					character= token.charAt(++offset);

				while (Character.isWhitespace(character))
					character= token.charAt(++offset);

				if (offset >= 2 && token.charAt(offset) == fEndSequence[0])
					return true;

			} catch (BadLocationException exception) {/* ignore */}
			
			return false;
		}
	}
	
	/**
	 * Rule for operator detection.
	 */
	private static class OperatorRule implements IRule {
		// omit comment chars '/' and '*' 
		private static final char[] OPERATORS = {'(', ')', '[', ']', '=', '>', '<',
												 '+','-', '!', ',', '|', '&', '%'};
		
		private final IToken token;

		/**
		 * Creates a new operator rule.
		 *
		 * @param token Token to use for this rule
		 */
		public OperatorRule(IToken token) {
			this.token = token;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.text.rules.IRule#evaluate(org.eclipse.jface.text.rules.ICharacterScanner)
		 */
		public IToken evaluate(ICharacterScanner scanner) {
			int character= scanner.read();
			if (isOperator((char)character)) {
				do {
					character= scanner.read();
				} while (isOperator((char) character));
				scanner.unread();
				return token;
			} else {
				scanner.unread();
				return Token.UNDEFINED;
			}
		}
		
		/**
		 * Checks, if this character is an operator character.
		 *
		 * @param character Character to determine whether it is an operator character
		 * @return <code>true</code>, if the character is an operator
		 */
		private boolean isOperator(char character) {
			for (int index= 0; index < OPERATORS.length; index++) {
				if (OPERATORS[index] == character)
					return true;
			}
			return false;
		}
	}
	
	/**
	 * Rule for number detection.
	 */
	private static class NumberRule implements IRule {
		private final IToken token;

		/**
		 * Creates a new number rule.
		 *
		 * @param token Token to use for this rule
		 */
		public NumberRule(IToken token) {
			this.token = token;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.text.rules.IRule#evaluate(org.eclipse.jface.text.rules.ICharacterScanner)
		 */
		public IToken evaluate(ICharacterScanner scanner) {
			int character= scanner.read();
			if (Character.isDigit((char)character)) {
				do {
					character= scanner.read();
				} while (Character.isDigit((char) character));
				scanner.unread();
				return token;
			} else {
				scanner.unread();
				return Token.UNDEFINED;
			}
		}
	}
}
