package ast;

public class Assign extends Stmt{
	
	public final Expr exp;
	public final Expr exp2;
	
	public Assign (Expr exp, Expr exp2) {
		this.exp = exp;
		this.exp2 = exp2;
	}
	
	public <T> T accept(ASTVisitor<T> v) {
		return v.visitAssign(this);
	    }
}
