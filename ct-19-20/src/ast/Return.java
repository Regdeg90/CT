package ast;

public class Return extends Stmt{
	
	public final Expr exp;
	public Type type;

	
	public Return (Expr exp) {
		this.exp = exp;
	}
	public Return () {
		this.exp = null;
	}
	
	public <T> T accept(ASTVisitor<T> v) {
		return v.visitReturn(this);
	    }
}
