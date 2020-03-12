package ast;

public interface ASTVisitor<T> {
    public T visitBaseType(BaseType bt);
    public T visitStructTypeDecl(StructTypeDecl st);
    public T visitBlock(Block b);
    public T visitFunDecl(FunDecl p);
    public T visitProgram(Program p);
    public T visitVarDecl(VarDecl vd);
    public T visitVarExpr(VarExpr v);
    public T visitStructType(StructType st);
    public T visitPointerType(PointerType pt);
    public T visitArrayType(ArrayType at);
    public T visitIntLiteral(IntLiteral i);
    public T visitStrLiteral(StrLiteral str);
    public T visitChrLiteral(ChrLiteral ch);
    public T visitFunCallExpr(FunCallExpr fc);
    public T visitBinOp(BinOp b);
    public T visitOp(Op op);
    public T visitArrayAccessExpr(ArrayAccessExpr aae);
    public T visitFieldAccessExpr(FieldAccessExpr fae);
    public T visitValueAtExpr(ValueAtExpr vae);
    public T visitSizeOfExpr(SizeOfExpr soe);
    public T visitTypecastExpr(TypecastExpr tce);
    public T visitExprStmt(ExprStmt es);
    public T visitWhile(While w);
    public T visitIf(If i);
    public T visitAssign(Assign ass);
    public T visitReturn(Return ret);
    // to complete ... (should have one visit method for each concrete AST node class)
//	Void visitType(Type t);
}
