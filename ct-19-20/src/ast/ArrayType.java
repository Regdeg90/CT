package ast;

public class ArrayType implements Type {
	
	public final Type type;
	public final int integer;
	
	public ArrayType(Type type, int integer) {
	    this.type = type;
	    this.integer = integer;
    }
	
	public <T> T accept(ASTVisitor<T> v) {
        return v.visitArrayType(this);
    }
}
