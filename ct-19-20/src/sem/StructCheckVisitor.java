package sem;

import java.util.HashMap;
import java.util.Map;

import ast.*;
public class StructCheckVisitor extends BaseSemanticVisitor<Void> {
	
	Map<String, StructTypeDecl> structs;
	public StructCheckVisitor() {
		this.structs = new HashMap<String, StructTypeDecl>();
	}
	@Override
	public Void visitBaseType(BaseType bt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitStructTypeDecl(StructTypeDecl st) {
		
		StructTypeDecl std = structs.get(st.structType.stName);
		if (std != null) {

		} else {
			structs.put(st.structType.stName, st);
		}
		
		return null;
	}

	 
	public Void visitBlock(Block b, Type type) {
		
		for (VarDecl vd : b.vardecls) {
			this.visitVarDecl(vd);
		}
		
		for (Stmt stmt : b.stmts) {
			this.visitStmt(stmt, type);
		}
		
		return null;
	}
	public Void visitStmt(Stmt stmt, Type type) {
	if (stmt.getClass() == Block.class) {
		this.visitBlock((Block) stmt, type);
	} else if (stmt.getClass() == While.class) {
		this.visitWhile((While) stmt, type);
	} else if (stmt.getClass() == If.class) {
		this.visitIf((If) stmt, type);
	} else if (stmt.getClass() == Assign.class) {
		this.visitAssign((Assign) stmt);
	} else if (stmt.getClass() == Return.class) {
		this.visitReturn((Return) stmt, type);
	} else if (stmt.getClass() == ExprStmt.class) {
		this.visitExprStmt((ExprStmt) stmt);
	}
			
	return null;
	}

	@Override
	public Void visitFunDecl(FunDecl p) {
		try {
			for(VarDecl vd : p.params) {
				this.visitVarDecl(vd);
			}
			this.visitBlock(p.block, p.type);
			
		} catch (NullPointerException n) {
			
		}
		return null;
	}

	@Override
	public Void visitProgram(Program p) {
		
		for (StructTypeDecl std : p.structTypeDecls) {
			std.accept(this);
		}
		
		for (VarDecl vd : p.varDecls) {
			vd.accept(this);
		}
		
		for (FunDecl fd : p.funDecls) {
			fd.accept(this);
		}
		return null;
	}

	@Override
	public Void visitVarDecl(VarDecl vd) {
		return null;
	}

	@Override
	public Void visitVarExpr(VarExpr v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitStructType(StructType st) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitPointerType(PointerType pt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitArrayType(ArrayType at) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitIntLiteral(IntLiteral i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitStrLiteral(StrLiteral str) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitChrLiteral(ChrLiteral ch) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitFunCallExpr(FunCallExpr fc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitBinOp(BinOp b) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitOp(Op op) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitArrayAccessExpr(ArrayAccessExpr aae) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitFieldAccessExpr(FieldAccessExpr fae) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitValueAtExpr(ValueAtExpr vae) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitSizeOfExpr(SizeOfExpr soe) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitTypecastExpr(TypecastExpr tce) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitExprStmt(ExprStmt es) {
		// TODO Auto-generated method stub
		return null;
	}


	public Void visitWhile(While w, Type type) {
		this.visitStmt(w.stmt, type);
		return null;
	}


	public Void visitIf(If i, Type type) {
		this.visitStmt(i.stmt, type);
		if (i.stmt2 != null) {
			this.visitStmt(i.stmt2, type);
		}
		return null;
	}

	@Override
	public Void visitAssign(Assign ass) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Void visitReturn(Return ret, Type type) {
		ret.type = type;
		return null;
	}
	@Override
	public Void visitBlock(Block b) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Void visitWhile(While w) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Void visitIf(If i) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Void visitReturn(Return ret) {
		// TODO Auto-generated method stub
		return null;
	}

}
