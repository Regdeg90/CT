package sem;

import java.util.ArrayList;
import java.util.List;

import ast.*;


public class NameAnalysisVisitor extends BaseSemanticVisitor<Void> {
	
	private Scope scope;
	public NameAnalysisVisitor() {
		this.scope = new Scope();
	}
	
	@Override
	public Void visitBaseType(BaseType bt) {
		// To be completed...
		return null;
	}

	@Override
	public Void visitStructTypeDecl(StructTypeDecl sts) {
		
		this.visitStructType(sts.structType);
		Scope oldScope = scope;
		scope = new Scope(oldScope);
		
		for (VarDecl vd : sts.varDecls) {
			vd.accept(this);
		}
		scope = oldScope;
		return null;
	}

	@Override
	public Void visitBlock(Block b) {
		Scope oldScope = scope;
		
		scope = new Scope(oldScope);
		
		for (VarDecl vd : b.vardecls) {
			this.visitVarDecl(vd);
		}
		
		for (Stmt stmt : b.stmts) {
			stmt.accept(this);
		}
		
		scope = oldScope;
		return null;
	}

//	public boolean lookup(String str) {
//	try {
//		Symbol s = scope.lookup(str);
//		System.out.println(s);
//		return true;
//	} catch (NullPointerException n) {
//		return false;
//	}
//}
//
//public boolean lookupcurrent(String str) {
//	try {
//		Symbol s = scope.lookupCurrent(str);
//		return true;
//	} catch (NullPointerException n) {
//		return false;
//	}
//}
//
	@Override
	public Void visitFunDecl(FunDecl p) {
		Symbol s = scope.lookup(p.name);
//		System.out.println(p.name + p.type + p.params.size());
		
		if (s != null) {
			error("Function name already declared");
		} else {
//			System.out.println("hereeeeee");
			scope.put(new FunSymbol(p));
		}
		
		Scope oldScope = scope;
		scope = new Scope(oldScope);
		
		for (VarDecl vd : p.params) {
			vd.accept(this);

		}
		
		
		p.block.accept(this);
		
		scope = oldScope;
		return null;
	}


	@Override
	public Void visitProgram(Program p) {
//		scope = new Scope();
//		System.out.println(scope);
		Block pb = new Block(new ArrayList<VarDecl>(), new ArrayList<Stmt>());
		Type t = BaseType.VOID;
	
		List<VarDecl> vds = new ArrayList<VarDecl>();
		vds.add(new VarDecl(new PointerType(BaseType.CHAR), "s"));
		
		this.visitFunDecl(new FunDecl(t, "print_s",vds, pb));
//		scope.put(new FunSymbol(new FunDecl(t, "print_s",vds, pb)));
//		System.out.println(vds.size());
//		FunSymbol s = (FunSymbol) scope.lookup("print_s");
//		System.out.println(s.fd.params.size());

		vds = new ArrayList<VarDecl>();
		
		vds.add(new VarDecl(BaseType.INT, "i"));
		scope.put(new FunSymbol(new FunDecl(t, "print_i", vds, pb)));
		
		vds = new ArrayList<VarDecl>();
		vds.add(new VarDecl(BaseType.CHAR, "c"));
		scope.put(new FunSymbol(new FunDecl(t, "print_c", vds, pb)));
		
		vds = new ArrayList<VarDecl>();
		scope.put(new FunSymbol(new FunDecl(BaseType.CHAR, "read_c", vds, pb)));
		scope.put(new FunSymbol(new FunDecl(BaseType.INT, "read_i", vds, pb)));
		
		vds = new ArrayList<VarDecl>();
		vds.add(new VarDecl(BaseType.INT, "size"));
		t = new PointerType(BaseType.VOID);
		scope.put(new FunSymbol(new FunDecl(t, "mcmalloc", vds	, pb)));

		
//		Scope oldScope = scope;
//		scope = new Scope(oldScope);;
		
		for (StructTypeDecl std : p.structTypeDecls) {
			std.accept(this);
		}

//		Scope oldScope = scope;
//		scope = new Scope(oldScope);
		for (VarDecl vd : p.varDecls) {
			vd.accept(this);
			vd.global = true;

		}

//		scope = new Scope(oldScope);
		for (FunDecl fd : p.funDecls) {
			fd.accept(this);
		}

//		scope = oldScope;
		
		return null;
	}

	@Override
	public Void visitVarDecl(VarDecl vd) {
		Symbol s = scope.lookupCurrent(vd.varName);
//		System.out.println(vd.varName);
		
		if (s != null) {
			error("Variable already declared");
		} else {
			scope.put(new VarSymbol(vd));
		}
		return null;
	}

	@Override
	public Void visitVarExpr(VarExpr v) {
		Symbol vs = scope.lookup(v.name);
		
		if (vs == null) {
			error("Variable is not defined");
		} else if (!vs.isVar()) {
			error("Variable is the name of a function");
		} else {
			v.vd = ((VarSymbol) vs).vd;
			v.global = v.vd.global;
		}

		return null;
	}


	@Override
	public Void visitStructType(StructType st) {

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
		Symbol s = scope.lookup(fc.str);
//		FunSymbol t = (FunSymbol) scope.lookup(fc.str);
//		System.out.println(t.fd.params.size());
		if (s == null) {
			error("Function does not exist");
		} else if (!s.isFun()) {
			error("This is not a function");
		} else {
			fc.fd = ((FunSymbol) s).fd;
//			FunSymbol t = (FunSymbol) scope.lookup(fc.str);
//			System.out.println("hehehe");
//			System.out.println(t.fd.params.size());
			
		}
		
		for (Expr exp : fc.exprs) {
			exp.accept(this);
		}
		
		
		return null;
	}
//	public Void visitExp(Expr exp) {
//		if (exp.getClass() == VarExpr.class) {
//			this.visitVarExpr((VarExpr) exp);
//		} else if (exp.getClass() == FunCallExpr.class) {
//			this.visitFunCallExpr((FunCallExpr) exp);
//		} else if (exp.getClass() == BinOp.class) {
//			this.visitArrayAccessExpr((ArrayAccessExpr) exp);
//		} else if (exp.getClass() == FieldAccessExpr.class) {
//			this.visitFieldAccessExpr((FieldAccessExpr) exp);
//		} else if (exp.getClass() == ValueAtExpr.class) {
//			this.visitValueAtExpr((ValueAtExpr) exp);
//		} else if (exp.getClass() == SizeOfExpr.class) {
//			this.visitSizeOfExpr((SizeOfExpr) exp);
//		} else if (exp.getClass() == TypecastExpr.class) {
//			this.visitTypecastExpr((TypecastExpr) exp);
//		}		
//		return null;
//	}

	@Override
	public Void visitBinOp(BinOp b) {
		b.exp1.accept(this);
		b.exp2.accept(this);
//		this.visitExp(b.exp1);
//		this.visitExp(b.exp2);
		return null;
	}

	@Override
	public Void visitOp(Op op) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visitArrayAccessExpr(ArrayAccessExpr aae) {
		aae.exp1.accept(this);
		aae.exp2.accept(this);

//		this.visitExp(aae.exp1);
//		this.visitExp(aae.exp2);
		return null;
	}

	@Override
	public Void visitFieldAccessExpr(FieldAccessExpr fae) {
		fae.exp.accept(this);

		return null;
	}

	@Override
	public Void visitValueAtExpr(ValueAtExpr vae) {
		vae.exp.accept(this);

		return null;
	}

	@Override
	public Void visitSizeOfExpr(SizeOfExpr soe) {
		soe.type.accept(this);
		return null;
	}

	@Override
	public Void visitTypecastExpr(TypecastExpr tce) {
		tce.exp.accept(this);
		
		
		return null;
	}

	@Override
	public Void visitExprStmt(ExprStmt es) {
		es.exp.accept(this);
		return null;
	}
	
	
//	public Void visitStmt(Stmt stmt) {
//		if (stmt.getClass() == Block.class) {
//			System.out.println("block");
//			Scope oldScope = scope;
//			scope = new Scope(oldScope);
//			this.visitBlock((Block) stmt);
//			scope = oldScope;
//		} else if (stmt.getClass() == While.class) {
//			this.visitWhile((While) stmt);
//		} else if (stmt.getClass() == If.class) {
//			this.visitIf((If) stmt);
//		} else if (stmt.getClass() == Assign.class) {
//			this.visitAssign((Assign) stmt);
//		} else if (stmt.getClass() == Return.class) {
//			this.visitReturn((Return) stmt);
//		} else if (stmt.getClass() == ExprStmt.class) {
//			this.visitExprStmt((ExprStmt) stmt);
//		}
//				
//		return null;
//		
//	}
	@Override
	public Void visitWhile(While w) {
		
		w.exp.accept(this);
		
		w.stmt.accept(this);
		
		return null;
		
	}

	@Override
	public Void visitIf(If i) {
		
		i.exp.accept(this);

		i.stmt.accept(this);
		if (i.stmt2!=null) {
			i.stmt2.accept(this);
		}

		
		return null;
	}

	@Override
	public Void visitAssign(Assign ass) {
		ass.exp.accept(this);
		ass.exp2.accept(this);
		Class e = ass.exp.getClass();
//		System.out.println(e);
		if (e == VarExpr.class || e == FieldAccessExpr.class
				|| e == ArrayAccessExpr.class || e == ValueAtExpr.class) {
			
		} else {
			error("LHS of assign is wrong Expr"); 
		}
		return null;
	}

	@Override
	public Void visitReturn(Return ret) {
		if (ret.exp != null) {
			ret.exp.accept(this);
		}
		return null;
	}

	// To be completed...


}
