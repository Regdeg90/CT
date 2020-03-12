package lexer;

import lexer.Token.TokenClass;

import java.io.EOFException;
import java.io.IOException;

/**
 * @author cdubach
 */
public class Tokeniser {

    private Scanner scanner;

    private int error = 0;
    public int getErrorCount() {
	return this.error;
    }

    public Tokeniser(Scanner scanner) {
        this.scanner = scanner;
    }

    private void error(char c, int line, int col) {
        System.out.println("Lexing error: unrecognised character ("+c+") at "+line+":"+col);
	error++;
    }


    public Token nextToken() {
        Token result;
        try {
             result = next();
        } catch (EOFException eof) {
            // end of file, nothing to worry about, just return EOF token
            return new Token(TokenClass.EOF, scanner.getLine(), scanner.getColumn());
        } catch (IOException ioe) {
            ioe.printStackTrace();
            // something went horribly wrong, abort
            System.exit(-1);
            return null;
        }
        return result;
    }

    /*
     * To be completed
     */
    private Token next() throws IOException {

        int line = scanner.getLine();
        int column = scanner.getColumn();

        // get the next character
        char c = scanner.next();
 

        // skip white spaces
        if (Character.isWhitespace(c))
            return next();
            
        if (c == '/') {
        	char c2 = scanner.peek();
        	if (c2 == '/') {
        		scanner.next();
        		c = scanner.peek();

        		while (c != '\n') {
        			scanner.next();
        			c = scanner.peek();
        		}
        		scanner.next();

        		return next();

        	}
        }
        
        if (c == '/') {
        	char c2 = scanner.peek();
        	if (c2 == '*') {
        		scanner.next();
        		try {
        			c = scanner.peek();
        		} catch (EOFException eof) {
        			error(c, line, column);
        	        return new Token(TokenClass.INVALID, line, column);
        		}

        		while (true) {
        			if (c == '*') {
        				scanner.next();
                		try {
                			c = scanner.peek();
                		} catch (EOFException eof) {
                			error(c, line, column);
                	        return new Token(TokenClass.INVALID, line, column);
                		}
        				if (c == '/') {
        					scanner.next();
        					return next();
        				}
        			} else {
        				scanner.next();
                		try {
                			c = scanner.peek();
                		} catch (EOFException eof) {
                			error(c, line, column);
                	        return new Token(TokenClass.INVALID, line, column);
                		}
        			}

        		}
        	}
        }
        
        // recognises the plus operator
        if (c == '=') {
        	try {
    			c = scanner.peek();
    		} catch (EOFException eof) {
    			return new Token(TokenClass.ASSIGN, line, column);
    		} 
        	if (c == '=') {
        		scanner.next();
        		return new Token(TokenClass.EQ, line, column);
        	} else {
        		return new Token(TokenClass.ASSIGN, line, column);
        	}
        }
            
        // recognises the plus operator
        if (c == '{')
            return new Token(TokenClass.LBRA, line, column);

        // recognises the plus operator
        if (c == '}')
            return new Token(TokenClass.RBRA, line, column);
        
        // recognises the plus operator
        if (c == '(')
            return new Token(TokenClass.LPAR, line, column);
        
        // recognises the plus operator
        if (c == ')')
            return new Token(TokenClass.RPAR, line, column);

        // recognises the plus operator
        if (c == '[')
            return new Token(TokenClass.LSBR, line, column);
        
        // recognises the plus operator
        if (c == ']')
            return new Token(TokenClass.RSBR, line, column);
        
        // recognises the plus operator
        if (c == ';')
            return new Token(TokenClass.SC, line, column);
        
        // recognises the plus operator
        if (c == ',') 
        	return new Token(TokenClass.COMMA, line, column);
        
        // Identifier with keywords and types
        if (Character.isLetter(c) || c == '_') {
        	StringBuilder sb = new StringBuilder();
        	sb.append(c);
        	
        	try {
    			c = scanner.peek();
    		} catch (EOFException eof) {
    			return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);
    		} 
        	
        	while (Character.isLetterOrDigit(c) || c =='_') {
        		sb.append(c);
        		scanner.next();
        		
        		try {
        			c = scanner.peek();
        		} catch (EOFException eof) {
                	if (sb.toString().equals("int")) {return new Token(TokenClass.INT, line, column);}
            		else if (sb.toString().equals("void")) {return new Token(TokenClass.VOID, line, column);}
            		else if (sb.toString().equals("char")) {return new Token(TokenClass.CHAR, line, column);}
            		else if (sb.toString().equals("if")) {return new Token(TokenClass.IF, line, column);}
            		
            		else if (sb.toString().equals("else")) {return new Token(TokenClass.ELSE, line, column);}
            		else if (sb.toString().equals("while")) {return new Token(TokenClass.WHILE, line, column);}
            		else if (sb.toString().equals("return")) {return new Token(TokenClass.RETURN, line, column);}
            
            		else if (sb.toString().equals("struct")) {return new Token(TokenClass.STRUCT, line, column);}
            		else if (sb.toString().equals("sizeof")) {return new Token(TokenClass.SIZEOF, line, column);}
            		else {
            			return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);
            		}
        		} 

        	}
        	if (sb.toString().equals("int")) {return new Token(TokenClass.INT, line, column);}
    		else if (sb.toString().equals("void")) {return new Token(TokenClass.VOID, line, column);}
    		else if (sb.toString().equals("char")) {return new Token(TokenClass.CHAR, line, column);}
    		else if (sb.toString().equals("if")) {return new Token(TokenClass.IF, line, column);}
    		
    		else if (sb.toString().equals("else")) {return new Token(TokenClass.ELSE, line, column);}
    		else if (sb.toString().equals("while")) {return new Token(TokenClass.WHILE, line, column);}
    		else if (sb.toString().equals("return")) {return new Token(TokenClass.RETURN, line, column);}
    
    		else if (sb.toString().equals("struct")) {return new Token(TokenClass.STRUCT, line, column);}
    		else if (sb.toString().equals("sizeof")) {return new Token(TokenClass.SIZEOF, line, column);}
    		else {
    			return new Token(TokenClass.IDENTIFIER, sb.toString(), line, column);
    		}
        }
            // String literal
        if (c == '\"') {
        	
        	StringBuilder sb = new StringBuilder();
        	String escapechars = "tbnrf\'\\\"0";
        	
    		try {
    			c = scanner.peek();
    		} catch (EOFException eof) {
    			error(c, line, column);
    	        return new Token(TokenClass.INVALID, line, column);
    		}

        	
        	while (Character.isDefined(c) && c != '\"') {
        		
        		if (c == '\\') {
        			sb.append(c);
            		scanner.next();
             		
            		try {
            			c = scanner.peek();
            		} catch (EOFException eof) {
            			error(c, line, column);
            	        return new Token(TokenClass.INVALID, line, column);
            		}
            		
                	String cstring = String.valueOf(c);
                	if (escapechars.contains(cstring)) {
                		sb.append(c);
//                		if (c == 'n') {sb.append('\n');}
//                		else if (c =='b') {sb.append('\b');}
//                		else if (c =='t') {sb.append('\t');}
//                		else if (c =='r') {sb.append('\r');}
//                		else if (c =='f') {sb.append('\f');}
//                		else if (c =='\'') {sb.append('\'');}
//                		else if (c =='\\') {sb.append('\\');}
//                		else if (c =='\"') {sb.append('\"');}
//                		else if (c =='\0') {sb.append('\0');}
            			scanner.next();
                		try {
                			c = scanner.peek();
                		} catch (EOFException eof) {
                			error(c, line, column);
                	        return new Token(TokenClass.INVALID, line, column);
                		}
            		} else {
            			error(c, line, column);
            	        return new Token(TokenClass.INVALID, line, column);
            		}
        		} 
        		else {
        			
        			sb.append(c);
        			scanner.next();
            		try {
            			c = scanner.peek();
            		} catch (EOFException eof) {
            			error(c, line, column);
            	        return new Token(TokenClass.INVALID, line, column);
            		}
        		}

        	}
//        	System.out.println("I got here");
        	if (c == '\"') {
        		scanner.next();
            	return new Token(TokenClass.STRING_LITERAL, sb.toString(), line, column);
        	} else {
        		error(c, line, column);
    	        return new Token(TokenClass.INVALID, line, column);
        	}
    		
        }
            
        // Int_literal
        if (Character.isDigit(c)) {
        	StringBuilder sb = new StringBuilder();
        	sb.append(c);
//        	System.out.println(c);
        	try {
    			c = scanner.peek();
    		} catch (EOFException eof) {
    			return new Token(TokenClass.INT_LITERAL, sb.toString(), line, column);
    		} 
        	
        	while (Character.isDigit(c)) {
        		sb.append(c);
        		scanner.next();
        		try {
        			c = scanner.peek();
        		} catch (EOFException eof) {
        			return new Token(TokenClass.INT_LITERAL, sb.toString(), line, column);
        		}      		
        	}
        	
        	
        	return new Token(TokenClass.INT_LITERAL, sb.toString(), line, column);
        	
        }
            
        // Char_literal
        if (c == '\'') {   	
        	StringBuilder sb = new StringBuilder();
        	String escapechars = "tbnrf\'\\\"0";
    		try {
    			c = scanner.peek();
    		} catch (EOFException eof) {
    			error(c, line, column);
    	        return new Token(TokenClass.INVALID, line, column);
    		}
        	if (Character.isDefined(c) && c != '\'') {
            	if (c == '\\') {
            		sb.append(c);
            		scanner.next();
            		
            		try {
            			c = scanner.peek();
            		} catch (EOFException eof) {
            			error(c, line, column);
            	        return new Token(TokenClass.INVALID, line, column);
            		}
            		
                	String cstring = String.valueOf(c);
            		if (escapechars.contains(cstring)) {
            			
            			sb.append(c);

//                		if (c == 'n') {sb.append('\n');}
//                		else if (c =='b') {sb.append('\b');}
//                		else if (c =='t') {sb.append('\t');}
//                		else if (c =='r') {sb.append('\r');}
//                		else if (c =='f') {sb.append('\f');}
//                		else if (c =='\'') {sb.append('\'');}
//                		else if (c =='\\') {sb.append('\\');}
//                		else if (c =='\"') {sb.append('\"');}
//                		else if (c =='\0') {sb.append('\0');}
                		scanner.next();
                		
                		try {
                			c = scanner.peek();
                		} catch (EOFException eof) {
                			error(c, line, column);
                	        return new Token(TokenClass.INVALID, line, column);
                		}
                		
                		if (c == '\'') {
                    		scanner.next();
                			return new Token(TokenClass.CHAR_LITERAL, sb.toString(), line, column);
                		} else {
                			error(c, line, column);
                	        return new Token(TokenClass.INVALID, line, column);
                		}
                	} else {
//                		scanner.next();
            			error(c, line, column);
            	        return new Token(TokenClass.INVALID, line, column);
                	}
            	} else {
            		sb.append(c);
            		scanner.next();
            		try {
            			c = scanner.peek();
            		} catch (EOFException eof) {
            			error(c, line, column);
            	        return new Token(TokenClass.INVALID, line, column);
            		}
            		if(c == '\'') {
                		scanner.next();
                		return new Token(TokenClass.CHAR_LITERAL, sb.toString(), line, column);
            		} 
            		else {
            			error(c, line, column);
            	        return new Token(TokenClass.INVALID, line, column);
            		}

            		
            	}
        	}
            
        }
    
        // recognises the plus operator
        if (c == '&') {
        	try {
    			c = scanner.peek();
    		} catch (EOFException eof) {
    			error(c, line, column);
    	        return new Token(TokenClass.INVALID, line, column);
    		}
        	
        	if (c == '&') {
        		scanner.next();
        		return new Token(TokenClass.AND, line, column);
        	} 
        }
        
        if (c == '|') {
        	try {
    			c = scanner.peek();
    		} catch (EOFException eof) {
    			error(c, line, column);
    	        return new Token(TokenClass.INVALID, line, column);
    		}
        	
        	if (c == '|') {
        		scanner.next();
        		return new Token(TokenClass.OR, line, column);
        	} 
        }
        
        if (c == '!') {
        	try {
    			c = scanner.peek();
    		} catch (EOFException eof) {
    			error(c, line, column);
    	        return new Token(TokenClass.INVALID, line, column);
    		}
        	
        	if (c == '=') {
        		scanner.next();
        		return new Token(TokenClass.NE, line, column);
        	} 
        }
        
        if (c == '<') {
        	try {
    			c = scanner.peek();
    		} catch (EOFException eof) {
    			return new Token(TokenClass.LT, line, column);
    		} 
        	
        	if (c == '=') {
        		scanner.next();
        		return new Token(TokenClass.LE, line, column);
        	} else {
        		return new Token(TokenClass.LT, line, column);
        	}
        }
        
        if (c == '>') {
        	try {
    			c = scanner.peek();
    		} catch (EOFException eof) {
    			return new Token(TokenClass.GT, line, column);
    		} 
        	
        	if (c == '=') {
        		scanner.next();
        		return new Token(TokenClass.GE, line, column);
        	} else {
        		return new Token(TokenClass.GT, line, column);
        	}
        }
            
        // recognises the plus operator
        if (c == '+')
            return new Token(TokenClass.PLUS, line, column);
        // recognises the plus operator
        if (c == '-')
            return new Token(TokenClass.MINUS, line, column);
        // recognises the plus operator
        if (c == '*')
            return new Token(TokenClass.ASTERIX, line, column);
        // recognises the plus operator
        if (c == '/')
            return new Token(TokenClass.DIV, line, column);
        // recognises the plus operator
        if (c == '%')
            return new Token(TokenClass.REM, line, column);
        // recognises the plus operator
        if (c == '.')
            return new Token(TokenClass.DOT, line, column);
        
        if (c == '#') {
//        	System.out.println(c);
        	StringBuilder sb = new StringBuilder();
        	sb.append(c);
    		try {
    			c = scanner.peek();
    		} catch (EOFException eof) {
//    			System.out.println("her");
    			error(c, line, column);
    	        return new Token(TokenClass.INVALID, line, column);
    		}

        	while (!(sb.length() > 9)) {
        		sb.append(c);
//        		System.out.println(sb);
        		scanner.next();
        		if (sb.toString().equals("#include")) {
//        			System.out.println("supposed out");
//        			scanner.next();
        			return new Token(TokenClass.INCLUDE, line, column);
        		}
        		
            	
        		try {
        			c = scanner.peek();
        		} catch (EOFException eof) {
        			error(c, line, column);
        	        return new Token(TokenClass.INVALID, line, column);
        		}
        	}
//        	System.out.println("gone");
        	
        }

        
        

        


        // if we reach this point, it means we did not recognise a valid token
        error(c, line, column);
        return new Token(TokenClass.INVALID, line, column);
    }


}
