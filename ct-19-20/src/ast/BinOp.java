package ast;

public class BinOp extends Expr{
	
	public final Expr exp1;
	public final Op op;
	public final Expr exp2;
	
	public BinOp(Expr exp1, Op op, Expr exp2) {
		this.exp1 = exp1;
		this.op = op;
		this.exp2 = exp2;
	}
	
	public <T> T accept(ASTVisitor<T> v) {
		return v.visitBinOp(this);
	    }

}
