package ast;

public class StructType implements Type {

	public final String stName;
	public StructTypeDecl std;
	
	public StructType(String stName) {
	    this.stName = stName;
    }
	
	public <T> T accept(ASTVisitor<T> v) {
        return v.visitStructType(this);
    }
	
}
