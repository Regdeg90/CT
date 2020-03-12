package ast;

import java.util.List;

public class FunCallExpr extends Expr{
	
	public final String str;
	public final List<Expr> exprs;
	public FunDecl fd;
	
	public FunCallExpr(String str, List<Expr> exprs) {
		this.str = str;
		this.exprs = exprs;
	}
	
	public <T> T accept(ASTVisitor<T> v) {
		return v.visitFunCallExpr(this);
	    }

}
