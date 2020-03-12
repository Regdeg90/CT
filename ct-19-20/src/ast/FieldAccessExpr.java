package ast;

public class FieldAccessExpr extends Expr{
	
	public final Expr exp;
	public final String str;
	public int offset;
	
	public FieldAccessExpr(Expr exp, String str) {
		this.exp = exp;
		this.str = str;
	}
	
	public <T> T accept(ASTVisitor<T> v) {
		return v.visitFieldAccessExpr(this);
	    }

}
