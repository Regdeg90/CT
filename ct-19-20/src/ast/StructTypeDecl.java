package ast;

import java.util.HashMap;
import java.util.List;

public class StructTypeDecl implements ASTNode {
	
	public final StructType structType;
	public final List<VarDecl> varDecls;
	public final HashMap<String, Integer> fexoff = new HashMap<>();
	public int size;
    // to be completed
	
	public StructTypeDecl(StructType structType, List<VarDecl> varDecls ) {
		this.structType = structType;
		this.varDecls = varDecls;
	}
	
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitStructTypeDecl(this);
    }

}
