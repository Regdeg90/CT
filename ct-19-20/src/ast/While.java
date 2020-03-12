package ast;

public class While extends Stmt{
	
	public final Expr exp;
	public final Stmt stmt;
	
	public While (Expr exp, Stmt stmt) {
		this.exp = exp;
		this.stmt = stmt;
	}
	
	public <T> T accept(ASTVisitor<T> v) {
		return v.visitWhile(this);
	    }
}
