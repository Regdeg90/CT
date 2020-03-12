package ast;

import java.util.List;

public class Block extends Stmt {

    public final List<VarDecl> vardecls;
    public final List<Stmt> stmts;
    
    public Block(List<VarDecl> vardecls, List<Stmt> stmts) {
    	this.vardecls = vardecls;
    	this.stmts = stmts;
    }

    public <T> T accept(ASTVisitor<T> v) {
	    return v.visitBlock(this);
    }
}
