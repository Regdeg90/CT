package ast;

public class ChrLiteral extends Expr{
	
	public final String ch;
	
	public ChrLiteral(String data) {
		this.ch = data;
	}
	
	public <T> T accept(ASTVisitor<T> v) {
		return v.visitChrLiteral(this);
	    }

}
