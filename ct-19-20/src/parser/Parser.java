package parser;

import ast.*;


import lexer.Token;
import lexer.Tokeniser;
import lexer.Token.TokenClass;

import java.sql.Struct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;



/**
 * @author cdubach
 */
public class Parser {

    private Token token;

    // use for backtracking (useful for distinguishing decls from procs when parsing a program for instance)
    private Queue<Token> buffer = new LinkedList<>();

    private final Tokeniser tokeniser;



    public Parser(Tokeniser tokeniser) {
        this.tokeniser = tokeniser;
    }

    public Program parse() {
        // get the first token
        nextToken();

        return parseProgram();
    }

    public int getErrorCount() {
        return error;
    }

    private int error = 0;
    private Token lastErrorToken;

    private void error(TokenClass... expected) {

        if (lastErrorToken == token) {
            // skip this error, same token causing trouble
            return;
        }

        StringBuilder sb = new StringBuilder();
        String sep = "";
        for (TokenClass e : expected) {
            sb.append(sep);
            sb.append(e);
            sep = "|";
        }
        System.out.println("Parsing error: expected ("+sb+") found ("+token+") at "+token.position);

        error++;
        lastErrorToken = token;
    }

    /*
     * Look ahead the i^th element from the stream of token.
     * i should be >= 1
     */
    private Token lookAhead(int i) {
        // ensures the buffer has the element we want to look ahead
        while (buffer.size() < i)
            buffer.add(tokeniser.nextToken());
        assert buffer.size() >= i;

        int cnt=1;
        for (Token t : buffer) {
            if (cnt == i)
                return t;
            cnt++;
        }

        assert false; // should never reach this
        return null;
    }


    /*
     * Consumes the next token from the tokeniser or the buffer if not empty.
     */
    private void nextToken() {
        if (!buffer.isEmpty())
            token = buffer.remove();
        else
            token = tokeniser.nextToken();
    }

    /*
     * If the current token is equals to the expected one, then skip it, otherwise report an error.
     * Returns the expected token or null if an error occurred.
     */
    private Token expect(TokenClass... expected) {
        for (TokenClass e : expected) {
            if (e == token.tokenClass) {
                Token cur = token;
                nextToken();
                return cur;
            }
        }

        error(expected);
        return null;
    }

    /*
    * Returns true if the current token is equals to any of the expected ones.
    */
    private boolean accept(TokenClass... expected) {
        boolean result = false;
        for (TokenClass e : expected)
            result |= (e == token.tokenClass);
        return result;
    }


    private Program parseProgram() {
        parseIncludes();

        List<StructTypeDecl> stds = parseStructDecls(new ArrayList<StructTypeDecl>());
        List<VarDecl> vds = parseVarDecls(new ArrayList<VarDecl>());
        List<FunDecl> fds = parseFunDecls(new ArrayList<FunDecl>());

        expect(TokenClass.EOF);
        return new Program(stds, vds, fds);
    }

    // includes are ignored, so does not need to return an AST node
    private void parseIncludes() {
        if (accept(TokenClass.INCLUDE)) {
            nextToken();
            expect(TokenClass.STRING_LITERAL);
            parseIncludes();
        }
    }
//    private void parseVarFunDecls() {
//    	if (accept(TokenClass.INT, TokenClass.VOID, TokenClass.CHAR, TokenClass.STRUCT)) {
//    		parseType();
//    		expect(TokenClass.IDENTIFIER);
//    		//System.out.println("HER");
//    		if (accept(TokenClass.LPAR)) {
//    			nextToken();
//    			parseParams();
//    			expect(TokenClass.RPAR);
//    			parseBlock();
//    			parseFunDecls();
//    			
//    		} else {
//    			parseVarDeclsArray();
//    			expect(TokenClass.SC);
//    			//System.out.println("HER");
//    			parseVarDecls();
//    			parseFunDecls();
//    		}
//    	}
//    }


    private List<StructTypeDecl> parseStructDecls(List<StructTypeDecl> std) {
    	if (accept(TokenClass.STRUCT) && lookAhead(2).tokenClass == TokenClass.LBRA) {
        	nextToken();
        	StructType st = new StructType(token.data);
//        	System.out.println(token.data);
        	expect(TokenClass.IDENTIFIER);
        	expect(TokenClass.LBRA);
        	List<VarDecl> vd = new ArrayList<VarDecl>();
        	if (parseVarCheck()) {
        		parseVarDeclsPlus(vd);    		
        	} 
        	else {
        		expect(TokenClass.INT, TokenClass.VOID, TokenClass.CHAR, TokenClass.STRUCT);
        	}
        	expect(TokenClass.RBRA);
        	expect(TokenClass.SC);
        	std.add(new StructTypeDecl(st, vd));
        	parseStructDecls(std);
        	
        }
        return std;
    }

    private List<VarDecl> parseVarDecls(List<VarDecl> vd) {
      if (parseVarCheck()) {
//    	System.out.println("here");
    	Type t = parseType();
    	String str = token.data;
    	expect(TokenClass.IDENTIFIER);
    	if (accept(TokenClass.LSBR)) {
    		t = parseVarDeclsArray(t);
    	}
    	
    	expect(TokenClass.SC);
    	vd.add(new VarDecl(t, str));
    	parseVarDecls(vd);
    }
        return vd;
    }
    
    private ArrayType parseVarDeclsArray(Type t) {
//    	System.out.println(t);

    	nextToken();
//    	System.out.println(t.getClass());

    	ArrayType at = new ArrayType(t, Integer.parseInt(token.data));

    	expect(TokenClass.INT_LITERAL);
    	expect(TokenClass.RSBR);

    	return at;
    }
    
    private Type parseType() {
    	//System.out.println("parseType()");
    //	System.out.println("Token 1 ahead = " +lookAhead(1).tokenClass + " " + lookAhead(1).data);
    	Type t = null;
    	if (accept(TokenClass.INT,TokenClass.VOID,TokenClass.CHAR, TokenClass.STRUCT)) {
    		if (accept(TokenClass.INT)) {
    			t = BaseType.INT;
    			nextToken();
    		} else if (accept(TokenClass.CHAR)) {
    			t = BaseType.CHAR;
    			nextToken();
    		} else if (accept(TokenClass.VOID)) {
    			t = BaseType.VOID;
    			nextToken();
    		} else if (accept(TokenClass.STRUCT)) {
        		expect(TokenClass.STRUCT);
        		t = new StructType(token.data);
        		expect(TokenClass.IDENTIFIER);
        	}
    		
    	} else {
    		expect(TokenClass.INT,TokenClass.VOID,TokenClass.CHAR, TokenClass.STRUCT);
    	}
    	
    	if (accept(TokenClass.ASTERIX)) {
    		PointerType pt = new PointerType(t);
    		expect(TokenClass.ASTERIX);
    		return pt;
    	}
    	return t;
    }
    
    private List<VarDecl> parseParams(List<VarDecl> vds) {
        //	System.out.println("parseParams()");
        //	System.out.println("Token 1 ahead = " +lookAhead(1).tokenClass + " " + lookAhead(1).data);
        	if (accept(TokenClass.INT, TokenClass.CHAR, TokenClass.VOID, TokenClass.STRUCT)) {
            	Type t = parseType();
            	String str = token.data;
            	expect(TokenClass.IDENTIFIER);
            	vds.add(new VarDecl(t, str));
            	vds = parseMultiparams(vds);   
         	   }
        	//System.out.println("Token 1 ahead = " +lookAhead(1).tokenClass + " " + lookAhead(1).data);
    		return vds;
        }
        
        private List<VarDecl> parseMultiparams(List<VarDecl> vds) {
        	if (accept(TokenClass.COMMA)) {
    	    	expect(TokenClass.COMMA);
    	    	Type t = parseType();
    	    	String str = token.data;
    	    	expect(TokenClass.IDENTIFIER);
    	    	vds.add(new VarDecl(t, str));
    	    	parseMultiparams(vds);
        	} 
        	return vds;
        	
        }

    private List<FunDecl> parseFunDecls(ArrayList<FunDecl> fds) {
    	if (accept(TokenClass.INT, TokenClass.VOID, TokenClass.CHAR, TokenClass.STRUCT) && 
          		((lookAhead(1).tokenClass == TokenClass.IDENTIFIER && lookAhead(2).tokenClass == TokenClass.LPAR)
          	       		||(lookAhead(1).tokenClass == TokenClass.ASTERIX && lookAhead(2).tokenClass == TokenClass.IDENTIFIER && lookAhead(3).tokenClass == TokenClass.LPAR)
          	       		 || (lookAhead(1).tokenClass == TokenClass.IDENTIFIER && lookAhead(2).tokenClass == TokenClass.ASTERIX && 
          	       		 lookAhead(3).tokenClass == TokenClass.IDENTIFIER && lookAhead(4).tokenClass == TokenClass.LPAR)
          	       		 || (lookAhead(1).tokenClass == TokenClass.IDENTIFIER  && lookAhead(2).tokenClass == TokenClass.IDENTIFIER && lookAhead(3).tokenClass == TokenClass.LPAR ) )) {
    		   // && (lookAhead(2).tokenClass == TokenClass.LPAR || lookAhead(3).tokenClass == TokenClass.LPAR ||lookAhead(4).tokenClass == TokenClass.LPAR || lookAhead(5).tokenClass == TokenClass.LPAR)) {
//    	   System.out.println("here");
    	   Type t = parseType();
    	   String str = token.data;
    	   expect(TokenClass.IDENTIFIER);
    	   expect(TokenClass.LPAR);
    	   
    	   List<VarDecl> vds = parseParams(new ArrayList<VarDecl>());
    	   
    	   expect(TokenClass.RPAR);
    	   
    	   Block b = parseBlock();
    	   fds.add(new FunDecl(t, str, vds, b));
	       parseFunDecls(fds); 
       }
        return fds;

    }
    private Block parseBlock() {
    	//System.out.println("parseBlock()");
    	//System.out.println("Token 1 ahead = " +lookAhead(1).tokenClass + " " + lookAhead(1).data);
    	expect(TokenClass.LBRA);
    	
    	List<VarDecl> vds = parseVarDecls(new ArrayList<VarDecl>());
    	
    	List<Stmt> stmts = parseStmtStar(new ArrayList<Stmt>());

    	
    	expect(TokenClass.RBRA);
		return new Block(vds, stmts);
    }
    
    private List<Stmt> parseStmtStar (ArrayList<Stmt> stmts) {
    	//System.out.println("parseStmtStar ()");
    	//System.out.println("Token 1 ahead = " +lookAhead(1).tokenClass + " " + lookAhead(1).data);
    	if (accept(TokenClass.WHILE, TokenClass.IF, TokenClass.RETURN,TokenClass.LBRA) || firstExp()) {
    		Stmt stmt = parseStmt();
    		stmts.add(stmt);
    		parseStmtStar(stmts);
    	}
    	return stmts;
    }

    private Stmt parseStmt() {
    	//System.out.println("parseStmt()");
    	//System.out.println("Token 1 ahead = " +lookAhead(1).tokenClass + " " + lookAhead(1).data);
    	//System.out.println(accept(TokenClass.WHILE));
    	if (accept(TokenClass.WHILE)) {
    		nextToken();
    		expect(TokenClass.LPAR);
    		Expr exp = parseExp();
    		expect(TokenClass.RPAR);
    		Stmt stmt = parseStmt();
    		return new While(exp, stmt);
    		
    	} else if (accept(TokenClass.IF)) {
    		nextToken();
    		expect(TokenClass.LPAR);
    		Expr exp = parseExp();
    		expect(TokenClass.RPAR);
    		Stmt stmt = parseStmt();
    		Stmt stmt2 = parseElseStmt();
    		if (stmt2 != null) {
    			return new If(exp, stmt, stmt2);
    		} else {
    			return new If(exp, stmt);
    		}
    		
    		
    	} else if (accept(TokenClass.RETURN)) {
    		nextToken();
    		Expr exp = null;
    		if (firstExp()) {
    			exp = parseExp();
    		}
    		expect(TokenClass.SC);
    		
    		if (exp!=null) {
    			return new Return(exp);
    		} else {
    			return new Return();
    		}
    	}
    	
    	else if (firstExp()) {
    		 Expr exp = parseExp();
    		 if (accept(TokenClass.ASSIGN)) {
    	    		nextToken();
    	    		Expr exp2 = parseExp();
    	    		expect(TokenClass.SC);
    	    		return new Assign(exp, exp2);
    	    } else {
    	    	expect(TokenClass.SC);
    	    	return new ExprStmt(exp);
    	    }
//    		parseExpStmt();
//    		expect(TokenClass.SC);
    	} else if (accept(TokenClass.LBRA)) {
    		Block b = parseBlock();
    		return b;
    	} else {
    		//add expected tokens here?
    		expect(TokenClass.WHILE,TokenClass.IF,TokenClass.RETURN,TokenClass.LBRA);
    		return null;
    	}
    }
    

    
    private Stmt parseElseStmt() {
    	if (accept(TokenClass.ELSE)) {
    		nextToken();
    		return parseStmt();
    	} else {
    		return null;
    	}
    }
    
    private Expr parseExp() {
    //	System.out.println("parseExp()");
    //	System.out.println("Token 1 ahead = " +lookAhead(1).tokenClass + " " + lookAhead(1).data);
    	Expr exp2 = parseExp2();
    	BinOp binop = parseStarExp(exp2);
    	
    	if (binop!=null) {
    		return binop;
    	} else {
    		return exp2;
    	}
    	
    }
    private BinOp parseStarExp(Expr exp) {
    	if (accept(TokenClass.OR)) {
    		nextToken();
    		Expr exp2 = parseExp2();
    		BinOp binop = parseStarExp(new BinOp(exp, Op.OR, exp2));
    		
    		if (binop==null) {
    			return new BinOp(exp, Op.OR, exp2);
    		} else {
    			return binop;
    		}
    	} else {
    		return null;
    	}
    }
    
    private Expr parseExp2() {
    	Expr exp3 = parseExp3();
    	BinOp binop2 = parseStarExp2(exp3);
    	
    	if (binop2!=null) {
    		return binop2;
    	} else {
    		return exp3;
    	}
    }
    
    private BinOp parseStarExp2(Expr exp2) {
    	if (accept(TokenClass.AND)) {
    		nextToken();
    		Expr exp3 = parseExp3();
    		BinOp binop2 = parseStarExp2(new BinOp(exp2, Op.AND, exp3));
    		
    		if (binop2==null) {
    			return new BinOp(exp2, Op.AND, exp3);
    		} else {
    			return binop2;
    		}
    	} else {
    		return null;
    	}
    }
    
    
    
    private Expr parseExp3() {
    	Expr exp4 = parseExp4();
    	BinOp binop3 = parseStarExp3(exp4);
    	if (binop3!=null) {
    		return binop3;
    	} else {
    		return exp4;
    	}
    }
    
    private BinOp parseStarExp3(Expr exp3) {
    	if (accept(TokenClass.EQ)) {
    		nextToken();
    		Expr exp4 = parseExp4();
    		BinOp binop3 = parseStarExp3(new BinOp(exp3, Op.EQ, exp4));
    		
    		if (binop3==null) {
    			return new BinOp(exp3, Op.EQ, exp4);
    		} else {
    			return binop3;
    		}
    	} else if (accept(TokenClass.NE)) {
    		nextToken();
    		Expr exp4 = parseExp4();
    		BinOp binop3 = parseStarExp3(new BinOp(exp3, Op.NE, exp4));
    		
    		if (binop3==null) {
    			return new BinOp(exp3, Op.NE, exp4);
    		} else {
    			return binop3;
    		}
    	} else {
    		return null;
    	}
		
    }
    
    private Expr parseExp4() {
    	Expr exp5 = parseExp5();
    	BinOp binop4 = parseStarExp4(exp5);
    	if (binop4!=null) {
    		return binop4;
    	} else {
    		return exp5;
    	}
    }
    
    private BinOp parseStarExp4(Expr exp4) {
    	if (accept(TokenClass.GE)) {
    		nextToken();
    		Expr exp5 = parseExp5();
    		BinOp binop4 = parseStarExp4(new BinOp(exp4, Op.GE, exp5));
    		
    		if (binop4==null) {
    			return new BinOp(exp4, Op.GE, exp5);
    		} else {
    			return binop4;
    		}
    		
    	} else if (accept(TokenClass.GT)) {
    		nextToken();
    		Expr exp5 = parseExp5();
    		BinOp binop4 = parseStarExp4(new BinOp(exp4, Op.GT, exp5));
    		
    		if (binop4==null) {
    			return new BinOp(exp4, Op.GT, exp5);
    		} else {
    			return binop4;
    		}
    		
    		
    	} else if (accept(TokenClass.LE)) {
    		nextToken();
    		Expr exp5 = parseExp5();
    		BinOp binop4 = parseStarExp4(new BinOp(exp4, Op.LE, exp5));
    		
    		if (binop4==null) {
    			return new BinOp(exp4, Op.LE, exp5);
    		} else {
    			return binop4;
    		}
    		
    	} else if (accept(TokenClass.LT)) {
    		nextToken();
    		Expr exp5 = parseExp5();
    		BinOp binop4 = parseStarExp4(new BinOp(exp4, Op.LT, exp5));
    		
    		if (binop4==null) {
    			return new BinOp(exp4, Op.LT, exp5);
    		} else {
    			return binop4;
    		}
    		
    	} else {
    		return null;
    	}
    	
    }
    
    private Expr parseExp5() {
    	Expr exp6 = parseExp6();
    	BinOp binop5 = parseStarExp5(exp6);
    	if (binop5!=null) {
    		return binop5;
    	} else {
    		return exp6;
    	}
    }
    
    private BinOp parseStarExp5(Expr exp5) {
    	if (accept(TokenClass.PLUS)) {
    		nextToken();
    		Expr exp6 = parseExp6();
    		BinOp binop5 = parseStarExp5(new BinOp(exp5, Op.ADD, exp6));
    		
    		if (binop5==null) {
    			return new BinOp(exp5, Op.ADD, exp6);
    		} else {
    			return binop5;
    		}
    	} else if (accept(TokenClass.MINUS)) {
    		nextToken();
    		Expr exp6 = parseExp6();
    		BinOp binop5 = parseStarExp5(new BinOp(exp5, Op.SUB, exp6));
    		
    		if (binop5==null) {
    			return new BinOp(exp5, Op.SUB, exp6);
    		} else {
    			return binop5;
    		}
    	} else {
    		return null;
    	}
    	
    }
    
    private Expr parseExp6() {
    	Expr exp7 = parseExp7();
    	BinOp binop6 = parseStarExp6(exp7);
    	if (binop6!=null) {
    		return binop6;
    	} else {
    		return exp7;
    	}
    }
    
    private BinOp parseStarExp6(Expr exp6) {
    	if (accept(TokenClass.ASTERIX)) {
    		nextToken();
    		Expr exp7 = parseExp7();
    		BinOp binop6 = parseStarExp6(new BinOp(exp6, Op.MUL, exp7));
    		
    		if (binop6==null) {
    			return new BinOp(exp6, Op.MUL, exp7);
    		} else {
    			return binop6;
    		}
    		
    		
    	} else if (accept(TokenClass.DIV)) {
    		nextToken();
//    		System.out.println(token);
    		Expr exp7 = parseExp7();
    		BinOp binop6 = parseStarExp6(new BinOp(exp6, Op.DIV, exp7));
    		
    		if (binop6==null) {
    			return new BinOp(exp6, Op.DIV, exp7);
    		} else {
    			return binop6;
    		}
    		
    		
    	} else if (accept(TokenClass.REM)) {
    		nextToken();
    		Expr exp7 = parseExp7();
    		BinOp binop6 = parseStarExp6(new BinOp(exp6, Op.MOD, exp7));
    		
    		if (binop6==null) {
    			return new BinOp(exp6, Op.MOD, exp7);
    		} else {
    			return binop6;
    		}
    		
    		
    	} else {
    		return null;
    	}
    }
    
    
    private Expr parseExp7() {
    	if (accept(TokenClass.SIZEOF)) {
    		SizeOfExpr soe = parseSizeOf();
    		Expr exp9 = parseExp9(soe);//nealy added all exp9 here
    		if (exp9!=null) {
    			return exp9;
    		} else {
    			return soe;
    		}
    	
    	} else if (accept(TokenClass.ASTERIX)) {
//    		System.out.println("valueat");
    		ValueAtExpr vae = parseValueAt();
    		Expr exp9 = parseExp9(vae);
    		
    		if (exp9!=null) {
    			return exp9;
    		} else {
    			return vae;
    		}
    	} else if (accept(TokenClass.LPAR) && (lookAhead(1).tokenClass == TokenClass.INT ||lookAhead(1).tokenClass == TokenClass.VOID ||lookAhead(1).tokenClass == TokenClass.CHAR || lookAhead(1).tokenClass == TokenClass.STRUCT)) {
    		TypecastExpr tce = parseTypeCast();
    		Expr exp9 = parseExp9(tce);
    		if (exp9!=null) {
    			return exp9;
    		} else {
    			return tce;
    		}
    	} else {
    		return parseExp8();
    	}
    }
    
    private Expr parseExp8() {
//    	System.out.println("parse term");
    	Expr term = parseTerm();
//    	System.out.println("exp9");
    	Expr exp9 = parseExp9(term);
    	
    	if (exp9!= null) {
    		return exp9;
    	} else {
    		return term;
    	}
    }
    
    private Expr parseTerm() {
    	if (accept(TokenClass.INT_LITERAL)) {
    		IntLiteral i = new IntLiteral(Integer.parseInt(token.data));
    		nextToken();
    		return i;
    	}else if (accept(TokenClass.CHAR_LITERAL)) {
    		ChrLiteral ch = new ChrLiteral(token.data);
    		nextToken();
    		return ch;
    	}else if (accept(TokenClass.STRING_LITERAL)) {
    		StrLiteral str =  new StrLiteral(token.data);
    		nextToken();
    		return str;
    	} else if(accept(TokenClass.LPAR)) {
    		nextToken();
    		Expr exp = parseExp();
    		expect(TokenClass.RPAR);
    		return exp;
    	} else if (accept(TokenClass.MINUS)) {
    		nextToken();
    		
//    		Expr exp = parseExp6();
    		Expr exp = parseExp7(); //Pases test
    		//turned from parse exp to parse term.
    		return new BinOp(new IntLiteral(0), Op.SUB, exp);
    	} else if (accept(TokenClass.IDENTIFIER)) {
    		
    		if (lookAhead(1).tokenClass == TokenClass.LPAR) {
//    			System.out.println("funcall");
    			return parseFuncall();
    			
    			
    		} else {
    			VarExpr vexp = new VarExpr(token.data);
    			nextToken();
    			return vexp;
    		}
    	}
    	
    	else {
    		expect(TokenClass.IDENTIFIER,TokenClass.INT_LITERAL,TokenClass.CHAR_LITERAL,TokenClass.STRING_LITERAL,TokenClass.MINUS,TokenClass.LPAR);
    		return null;
    	}
    }
    
    private Expr parseExp9(Expr exp) {
    	//System.out.println("hi");
    	if (accept(TokenClass.DOT)) {
//    		System.out.println(".field");
    		nextToken();
    		String str = token.data;
    		expect(TokenClass.IDENTIFIER);
//    		System.out.println(token);
    		Expr fae = new FieldAccessExpr(exp, str);
    		Expr exp2 = parseExp9(fae);
    		if (exp2!=null) {
    			return exp2;
    		} else {
    			return fae;
    		}
    		
    	} else if (accept(TokenClass.LSBR)) {
    		nextToken();
    		Expr exp2 = parseExp();
    	//	System.out.print(token);
    		expect(TokenClass.RSBR);
//    		parseExp9(exp);
    		Expr aae = new ArrayAccessExpr(exp, exp2);
    		Expr exp3 = parseExp9(aae);
    		if (exp3!=null) {
    			return exp3;
    		} else {
    			return aae;
    		}
    		
    	}
    	return null;
    }

    private FunCallExpr parseFuncall() {
    //	System.out.println("parseFuncall()");
    //	System.out.println("Token 1 ahead = " +lookAhead(1).tokenClass + " " + lookAhead(1).data);
    	String str = token.data;
    	expect(TokenClass.IDENTIFIER);
    	expect(TokenClass.LPAR);
    	List<Expr> exps = new ArrayList<Expr>();
    	if (firstExp()) {
//    		System.out.println("parseExp");
    		Expr exp = parseExp();
//    		System.out.println("outa here");
    		exps.add(exp);
    		exps = parsemultiFuncall(exps);
//    		System.out.println(exps.size());
    	}
//    	System.out.println("here");
//    	System.out.println(token);
    	expect(TokenClass.RPAR);  
    //	System.out.println("Im outa here");
    //	System.out.println(token);
		return new FunCallExpr(str, exps);
    }
    
    private List<Expr> parsemultiFuncall(List<Expr> exps) {
    	if (accept(TokenClass.COMMA)) {
    		nextToken();
    		Expr exp = parseExp();
    		exps.add(exp);
    		parsemultiFuncall(exps);
    	} 
    	return exps;
    }
    
    private ValueAtExpr parseValueAt() {
    	expect(TokenClass.ASTERIX);
//    	System.out.println(token);
    	Expr exp = parseExp7();
		return new ValueAtExpr(exp);
    }
    
    private SizeOfExpr parseSizeOf() {
//    	System.out.println("sizeof");
    	expect(TokenClass.SIZEOF);
//    	System.out.println(token);
    	expect(TokenClass.LPAR);
//    	System.out.println(token);
    	Type t = parseType();
//    	System.out.println(token);
    	expect(TokenClass.RPAR);
//    	System.out.println("token here is");
//    	System.out.println(token);
		return new SizeOfExpr(t);
    }
    
    private TypecastExpr parseTypeCast() {
//    	System.out.println("typecast");
    	expect(TokenClass.LPAR);
    	Type t = parseType();
    	expect(TokenClass.RPAR);
    	Expr exp = parseExp7();
    	return new TypecastExpr(t, exp);
    }
	


//    private void parseStructDeclsa() {
////    	System.out.println(" parseStructDecls()");
// //   	System.out.println("Token 1 ahead = " +lookAhead(1).tokenClass + " " + lookAhead(1).data);
//        if (accept(TokenClass.STRUCT) && lookAhead(2).tokenClass == TokenClass.LBRA) {
//        	nextToken();
//        	expect(TokenClass.IDENTIFIER);
//        	expect(TokenClass.LBRA);
//        	if (parseVarCheck()) {
//        		parseVarDeclsPlus();    		
//        	} else {
//        		expect(TokenClass.INT, TokenClass.VOID, TokenClass.CHAR, TokenClass.STRUCT);
//        	}
//        	expect(TokenClass.RBRA);
//        	expect(TokenClass.SC);
//        	
////        	parseStructDecls();
//        	
//        }
//    }
    
    private boolean parseVarCheck() {
    	if (accept(TokenClass.INT, TokenClass.VOID, TokenClass.CHAR, TokenClass.STRUCT) && 
        		((lookAhead(1).tokenClass == TokenClass.IDENTIFIER && (lookAhead(2).tokenClass == TokenClass.SC || lookAhead(2).tokenClass == TokenClass.LSBR))
        		||(lookAhead(1).tokenClass == TokenClass.ASTERIX && lookAhead(2).tokenClass == TokenClass.IDENTIFIER && (lookAhead(3).tokenClass == TokenClass.SC || lookAhead(3).tokenClass == TokenClass.LSBR))
        		 || (lookAhead(1).tokenClass == TokenClass.IDENTIFIER && lookAhead(2).tokenClass == TokenClass.ASTERIX && 
        		 lookAhead(3).tokenClass == TokenClass.IDENTIFIER && (lookAhead(4).tokenClass == TokenClass.SC || lookAhead(4).tokenClass == TokenClass.LSBR))
        		 || (lookAhead(1).tokenClass == TokenClass.IDENTIFIER  && lookAhead(2).tokenClass == TokenClass.IDENTIFIER && 
        		 (lookAhead(3).tokenClass == TokenClass.SC || lookAhead(3).tokenClass == TokenClass.LSBR)) )) {
    		return true;
    	} else {
    		return false;
    	}
    }
    
    private void parseVarDeclsPlus(List<VarDecl> vd) {
    	parseVarDecls(vd);
    	if (parseVarCheck()) {
    		parseVarDeclsPlus(vd);
    	}
    }
    
//    private void parseVarDeclsa() {
//    	//System.out.println("parseVarDecls()");
////    	System.out.println("HEREVAR");
//    	//System.out.println("Token 1 ahead = " +lookAhead(1).tokenClass + " " + lookAhead(1).data);
////    	System.out.println(accept(TokenClass.INT, TokenClass.VOID, TokenClass.CHAR, TokenClass.STRUCT) && lookAhead(2).tokenClass != TokenClass.LPAR);
//    	//Sort this out into different cats
//        if (parseVarCheck()) {
//        	//System.out.println("here");
//        	parseType();
//        	expect(TokenClass.IDENTIFIER);
//        	parseVarDeclsArray();
//        	expect(TokenClass.SC);
//        	parseVarDecls();
//        }
//        //System.out.println("Token 1 ahead = " +lookAhead(1).tokenClass + " " + lookAhead(1).data);
//        
//    }
    

    


    

    	//System.out.println("parseFunDecls()");
    //	System.out.println("Token 1 ahead = " +lookAhead(1).tokenClass + " " + lookAhead(1).data);
//    	System.out.println("HERE");
//    	System.out.print(lookAhead(1).tokenClass);
       
    	
   
    

    
    
    private boolean firstExp() {
    	//System.out.println("firstExp()");
    	//System.out.println("Token 1 ahead = " +lookAhead(1).tokenClass + " " + lookAhead(1).data);
    	if (accept(TokenClass.LPAR, TokenClass.IDENTIFIER, TokenClass.INT_LITERAL, TokenClass.MINUS, TokenClass.CHAR_LITERAL, TokenClass.STRING_LITERAL, TokenClass.ASTERIX,TokenClass.SIZEOF)) {
    		return true;
    	}
    	return false;
    }


    

    

    

    

    

    // to be completed ...

}
