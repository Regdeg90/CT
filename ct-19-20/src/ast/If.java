package ast;

public class If extends Stmt{
	
	public final Expr exp;
	public final Stmt stmt;
	public final Stmt stmt2;
	
	public If (Expr exp, Stmt stmt, Stmt stmt2) {
		this.exp = exp;
		this.stmt = stmt;
		this.stmt2 = stmt2;
	}
	public If (Expr exp, Stmt stmt) {
		this.exp = exp;
		this.stmt = stmt;
		this.stmt2 = null;
	}
	
	public <T> T accept(ASTVisitor<T> v) {
		return v.visitIf(this);
	    }
}
