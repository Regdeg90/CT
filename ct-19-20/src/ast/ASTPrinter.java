package ast;

import java.io.PrintWriter;

public class ASTPrinter implements ASTVisitor<Void> {

    private PrintWriter writer;

    public ASTPrinter(PrintWriter writer) {
            this.writer = writer;
    }

    @Override
    public Void visitBlock(Block b) {
        writer.print("Block(");
        String delimiter = "";
        for (VarDecl vd : b.vardecls) {
        	writer.print(delimiter);
        	delimiter = ",";
            vd.accept(this);
        }

        for (Stmt s : b.stmts) {
        	writer.print(delimiter);
        	delimiter = ",";
            s.accept(this);
        }
        writer.print(")");
        return null;
    }

    @Override
    public Void visitFunDecl(FunDecl fd) {
        writer.print("FunDecl(");
        fd.type.accept(this);
        writer.print(","+fd.name+",");
        for (VarDecl vd : fd.params) {
            vd.accept(this);
            writer.print("\n,");
        }
        fd.block.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitProgram(Program p) {
        writer.print("Program(");
        String delimiter = "";
        for (StructTypeDecl std : p.structTypeDecls) {
            writer.print(delimiter);
            delimiter = "\n,";
            std.accept(this);
        }
        for (VarDecl vd : p.varDecls) {
            writer.print(delimiter);
            delimiter = "\n,";
            vd.accept(this);
        }
        for (FunDecl fd : p.funDecls) {
            writer.print(delimiter);
            delimiter = "\n,";
            fd.accept(this);
        }
        writer.print(")\n");
	    writer.flush();
        return null;
    }

    @Override
    public Void visitVarDecl(VarDecl vd){
        writer.print("\nVarDecl(");
        vd.type.accept(this);
        writer.print(","+vd.varName);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitVarExpr(VarExpr v) {
        writer.print("\nVarExpr(");
        writer.print(v.name);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitBaseType(BaseType bt) {
        writer.print(bt.name());
        return null;
    }

    @Override
    public Void visitStructTypeDecl(StructTypeDecl st) {
    	
//    	System.out.println(st.varDecls.size());
        writer.print("\nStructTypeDecl(");
        st.structType.accept(this);
        String delimiter = ",";
        for (VarDecl vd : st.varDecls) {
        	writer.print(delimiter);
            vd.accept(this);
            
        }
//        System.out.println("here");
        writer.print(")");
        return null;
    }

	@Override
	public Void visitStructType(StructType st) {
		writer.print("StructType(");
		writer.print(st.stName);
		writer.print(")");
		return null;
	}

	@Override
	public Void visitPointerType(PointerType p) {
		writer.print("PointerType(");
		p.type.accept(this);
//		writer.print(p.type);
		writer.print(")");
		return null;
	}
	

	@Override
	public Void visitArrayType(ArrayType at) {
		writer.print("ArrayType(");
		at.type.accept(this);
		writer.print(",");
		writer.print(at.integer); //maybe intliteral
		writer.print(")");
		return null;
	}
	
//	@Override
//	public Void visitType(Type t) {
//		if (t.equals(BaseType.class)){
//			visitArrayType(t);
//		}
//		
//		return null;
//		
//	}
	@Override
	public Void visitIntLiteral(IntLiteral i) {
		writer.print("IntLiteral(");
		writer.print(i.i);
		writer.print(")");
		return null;
	}

	@Override
	public Void visitStrLiteral(StrLiteral str) {
		writer.print("StrLiteral(");
		writer.print(str.str);
		writer.print(")");
		return null;
	}

	@Override
	public Void visitChrLiteral(ChrLiteral ch) {
		writer.print("ChrLiteral(");
		writer.print(ch.ch);
		writer.print(")");
		return null;
	}

	@Override
	public Void visitFunCallExpr(FunCallExpr fc) {
		writer.print("\nFunCallExpr(");
		writer.print(fc.str);
//		String delimiter = "";
		for (Expr exp : fc.exprs) {
			writer.print(",");
//			delimiter = "\n,";
            exp.accept(this);
        }
		writer.print(")");
		return null;
	}

	@Override
	public Void visitBinOp(BinOp b) {
		writer.print("BinOp(");
		b.exp1.accept(this);
		writer.print(",");
		b.op.accept(this);
		writer.print(",");
		b.exp2.accept(this);
		writer.print(")");
		return null;
	}

	@Override
	public Void visitOp(Op op) {
		writer.print(op.name());
		return null;
	}

	@Override
	public Void visitArrayAccessExpr(ArrayAccessExpr aae) {
		writer.print("ArrayAccessExpr(");
		aae.exp1.accept(this);
		writer.print(",");
		aae.exp2.accept(this);
		writer.print(")");
		return null;
	}

	@Override
	public Void visitFieldAccessExpr(FieldAccessExpr fae) {
		writer.print("FieldAccessExpr(");
		fae.exp.accept(this);
		writer.print(",");
		writer.print(fae.str);
		writer.print(")");
		return null;
	}

	@Override
	public Void visitValueAtExpr(ValueAtExpr vae) {
		writer.print("ValueAtExpr(");
		vae.exp.accept(this);
		writer.print(")");
		return null;
	}

	@Override
	public Void visitSizeOfExpr(SizeOfExpr soe) {
		writer.print("SizeOfExpr(");
		soe.type.accept(this);
		writer.print(")");
		return null;
	}

	@Override
	public Void visitTypecastExpr(TypecastExpr tce) {
		writer.print("TypecastExpr(");
		tce.type.accept(this);
		writer.print(",");
		tce.exp.accept(this);
		writer.print(")");
		return null;
	}

	@Override
	public Void visitExprStmt(ExprStmt es) {
		writer.print("\nExprStmt(");
		es.exp.accept(this);
		writer.print(")");
		return null;
	}

	@Override
	public Void visitWhile(While w) {
		writer.print("\nWhile(");
		w.exp.accept(this);
		writer.print(",");
		w.stmt.accept(this);
		writer.print(")");
		return null;
	}

	@Override
	public Void visitIf(If i) {
		writer.print("\nIf(");
		i.exp.accept(this);
		writer.print(",");
		i.stmt.accept(this);
//		System.out.println("here");
		if(i.stmt2 != null) {
//			System.out.println("e");
			writer.print(",");
			i.stmt2.accept(this);
		}
//		System.out.println("eheh");
		writer.print(")");
		return null;
	}

	@Override
	public Void visitAssign(Assign ass) {
		writer.print("\nAssign(");
		ass.exp.accept(this);
		writer.print(",");
		ass.exp2.accept(this);
		writer.print(")");
		return null;
	}

	@Override
	public Void visitReturn(Return ret) {
		writer.print("\nReturn(");
		if(ret.exp != null) {
			ret.exp.accept(this);
		}
		writer.print(")");
		return null;
	}

    // to complete ...
    
}
