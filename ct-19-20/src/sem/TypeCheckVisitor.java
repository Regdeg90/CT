package sem;

import java.sql.Struct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ast.*;

public class TypeCheckVisitor extends BaseSemanticVisitor<Type> {
	
	
	Map<String, StructTypeDecl> structs;
//	Map<String, List<VarDecl>> params;
	public TypeCheckVisitor() {
		this.structs = new HashMap<String, StructTypeDecl>();
//		this.params = new HashMap<String, List<VarDecl>>();
	}
	
	
	@Override
	public Type visitBaseType(BaseType bt) {
		return bt;
	}

	@Override
	public Type visitStructTypeDecl(StructTypeDecl st) {
		StructTypeDecl std = structs.get(st.structType.stName);
		if (std != null) {
			error("Struct of the same name already declared");
			return null;
		} else {
			
			structs.put(st.structType.stName, st);
		}
		for (VarDecl vd : st.varDecls) {
			if (vd.type.getClass() == StructType.class) {
				StructType s = (StructType) vd.type;
				if (st.structType.stName.equals(s.stName)) {
					error("Struct has an incomplete type");
				}
			}
			vd.accept(this);
		}
		

		return null;
	}

	@Override
	public Type visitBlock(Block b) {
		for (VarDecl vd : b.vardecls) {
			vd.accept(this);
		}
		for (Stmt stmt :b.stmts) {
			stmt.accept(this);
		}
		return null;
	}

	@Override
	public Type visitFunDecl(FunDecl p) {
		
		for (VarDecl vd : p.params) {
			vd.accept(this);
		}
		
		for (Stmt stmt : p.block.stmts) {
			if (stmt.getClass() == Return.class) {
				Return ret = (Return) stmt;
				ret.type = p.type;
			}
		}
		
		p.block.accept(this);
		
		return null;
	}


	@Override
	public Type visitProgram(Program p) {
		
		for (StructTypeDecl std : p.structTypeDecls) {
			this.visitStructTypeDecl(std);
		}	

		for (VarDecl vd : p.varDecls) {
			this.visitVarDecl(vd);
		}
		

		for (FunDecl fd : p.funDecls) {
			
			this.visitFunDecl(fd);
		}
		
		return null;
	}

	@Override
	public Type visitVarDecl(VarDecl vd) {
		if (vd.type != null) {
			if (vd.type == BaseType.VOID) {
				error("Varible cannot be null type");
			} else if (vd.type.getClass() == ArrayType.class) {
				ArrayType at = (ArrayType) vd.type;
				if (at.type.accept(this) == BaseType.VOID) {
					error("Varible cannot be null type");
				}
			}
			
//			vd.type.accept(this);
			return vd.type.accept(this);
		}
		return null;
		
	}

	@Override
	public Type visitVarExpr(VarExpr v) {
		try {
			v.type = v.vd.type;
			return v.vd.type;
		} catch (NullPointerException n) {
			error("Error in VarExpr");
			return null;
		}
		
	}

	@Override
	public Type visitStructType(StructType st) {
		StructTypeDecl std = structs.get(st.stName);
//		System.out.println(st.stName);
		if (std == null) {
			error("Struct not declared");
		}
		
		st.std = std;
//		System.out.println("This is struct " + st.stName + " with std "  +st.std.size);
		return st;
	}

	@Override
	public Type visitPointerType(PointerType pt) {
//		System.out.println("visiting pointer type of " + pt.type);
		if (pt.type.getClass() == StructType.class) {
			pt.type.accept(this);
		}
		return pt;
	}

	@Override
	public Type visitArrayType(ArrayType at) {
//		Type t = at.type.accept(this);
		return at;
	}

	@Override
	public Type visitIntLiteral(IntLiteral i) {
		// TODO Auto-generated method stub
		return BaseType.INT;
	}

	@Override
	public Type visitStrLiteral(StrLiteral str) {
		// TODO Auto-generated method stub
		return new ArrayType(BaseType.CHAR, str.str.length()+1);
	}

	@Override
	public Type visitChrLiteral(ChrLiteral ch) {
		// TODO Auto-generated method stub
		return BaseType.CHAR;
	}

	@Override
	public Type visitFunCallExpr(FunCallExpr fc) {
		
		try {
			
			VarDecl[] params = fc.fd.params.toArray(new VarDecl[0]);
			Expr[] args = fc.exprs.toArray(new Expr[0]);
//			System.out.println(fc.str);
			if (params.length != args.length) {
//				System.out.println(params[0].varName);
//				System.out.println(fc.fd.name);
//				System.out.println(params.length);
//				System.out.println(args.length);
				error("Params and args length didn't match");
				
			} else {
				int err = 0;
//				System.out.println(params.length);
				for (int i =0; i <params.length; i++) {
					args[i].type = params[i].type;
//					System.out.println(params[i].type);
//					System.out.println(args[i].accept(this));
					if (!truetype(params[i].type,args[i].accept(this))) {

//						System.out.println(params[i].type);
//						System.out.println(args[i].accept(this));
						err++;
						
					}
				}
				if (err>0) {
//					System.out.println(err);
					error("Wrong parameter type");
				}
			}
			
//			fc.type = fc.fd.type;
			return fc.fd.type;
		} catch (NullPointerException n) {
//			System.out.println("her");
			error("Error in FuncallExpr");
			return null;
		}
		
	}

	@Override
	public Type visitBinOp(BinOp b) {
		Type lhs = b.exp1.accept(this);
		Type rhs = b.exp2.accept(this);
		
		b.exp1.type = lhs;
		b.exp2.type = rhs;
		
		if (lhs != null && rhs != null) {
			if (b.op == Op.ADD || b.op == Op.MOD || b.op == Op.SUB 
					|| b.op == Op.MUL || b.op == Op.GT || b.op == Op.LT
					|| b.op == Op.GE || b.op == Op.LE || b.op == Op.AND
					|| b.op == Op.OR || b.op == Op.DIV) {
				if (truetype(lhs,BaseType.INT) && truetype(rhs,BaseType.INT)) {
					b.type = BaseType.INT;
					return b.type;
				} else {
					error("Expected INT type");
				}
				
			} else  if (b.op == Op.EQ || b.op == Op.NE){
				if (lhs.getClass() == StructType.class || lhs.getClass() == ArrayType.class 
						|| lhs == BaseType.VOID) {
					error("Left hand side unexpected Type");
				} else if (!truetype(lhs, rhs)) {
					error("Type didn't match");
				}
					
					else {
					b.type = BaseType.INT;
					return b.type;
				}
			}
		}
		
		return null;
	}

	@Override
	public Type visitOp(Op op) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type visitArrayAccessExpr(ArrayAccessExpr aae) {
		Type e = aae.exp1.accept(this);
		Type e2 = aae.exp2.accept(this);
		
		aae.exp1.type = e;
		aae.exp2.type = e2;
		
		if (e != null && e2 != null) {
			if (e.getClass() != ArrayType.class && e.getClass() != PointerType.class) {
//				ArrayType at = (ArrayType) e;
//				e = at.type.accept(this);
//				if (e != ElemType.class) {
//					
//				}
				
				error("Array Access not of ArrayType or PointerType");
				return null;

			} else if (!truetype(e2, BaseType.INT)) {
				error("Accessing with type that is not int");
				return null;

			}
			//Check if we can access f[7] from declared f[6]
			if (e.getClass() == ArrayType.class) {
				ArrayType at = (ArrayType) e;

				e = at.type.accept(this);
			} else {
				PointerType pt = (PointerType) e;
				e = pt.type.accept(this);
			}
			return e;
		}
		return null;
		
	}

	@Override
	public Type visitFieldAccessExpr(FieldAccessExpr fae) {
		
		Type e = fae.exp.accept(this);
		fae.exp.type = e;
//		System.out.println(e);
		if (e != null) {
			if (e.getClass() != StructType.class) {
				error("Field Access is not of struct type");
				
			} else {
				StructType st = (StructType) e;
				
				StructTypeDecl std = structs.get(st.stName);
				
				if (std == null) {
					error("Accessing a struct that doesn't exist");
				} else {
					
					for(VarDecl vd : std.varDecls) {
						int offset = 0;
						if (vd.type == BaseType.INT || vd.type.getClass() == PointerType.class || vd.type == BaseType.CHAR) {
							offset += 4;

						} else if (vd.type.getClass() == ArrayType.class) {
							ArrayType at = (ArrayType) vd.type;
							offset += at.integer*4;
						}
						if (vd.varName.equals(fae.str)) {
//							System.out.println("here");
							fae.offset = offset;
							return vd.type;
						}
					}
					error("There was no string in the struct matching");
				}
				
			}
		}
		
//		System.out.println(e);
		
		return null;
	}

	@Override
	public Type visitValueAtExpr(ValueAtExpr vae) {
		Type e = vae.exp.accept(this);
		
		
		if (e!= null) {
			if (e.getClass() != PointerType.class) {
				error("Value at Expr not a PointerType");

			} else {
				PointerType p = (PointerType) e;
				vae.exp.type = p.type;
//				System.out.println("this is p.type " + p.type);
				return p.type;
			}
		}
		return null;
	}

	@Override
	public Type visitSizeOfExpr(SizeOfExpr soe) {
		// TODO Auto-generated method stub
		return BaseType.INT;
	}

	@Override
	public Type visitTypecastExpr(TypecastExpr tce) {
		Type t = tce.exp.accept(this);
		tce.exp.type = t;
//		System.out.println(t);
		if (t != null) {
			if (t == BaseType.CHAR || t.getClass() == ArrayType.class || t.getClass() == PointerType.class) {
				if (t == BaseType.CHAR && tce.type == BaseType.INT) {
					return BaseType.INT;
				} else if (t.getClass() == ArrayType.class && tce.type.getClass() == PointerType.class) {		
					ArrayType at = (ArrayType) t;
					return new PointerType(at.type.accept(this));
				} else if (t.getClass() == PointerType.class && tce.type.getClass() == PointerType.class) {
					PointerType pt = (PointerType) tce.type;
					//not sure this works
					return new PointerType(pt.type);
				} else {
					error("Unexpected TypeCast");
				}
			}
			error("Cannot Typecast this exp");
			return null;
		}
		System.out.println("hehehehehehhhehehehhe");
		return null;

	}

	@Override
	public Type visitExprStmt(ExprStmt es) {
		Type t = es.exp.accept(this);
		es.exp.type = t;
		return t;
	}

	@Override
	public Type visitWhile(While w) {
		Type t = w.exp.accept(this);
		w.exp.type = t;
		
		if (!truetype(t, BaseType.INT)) {
			error("While statement condition not int type");
		}
		
		w.stmt.accept(this);
		return t;
	}

	@Override
	public Type visitIf(If i) {
		Type t = i.exp.accept(this);
		i.exp.type = t;
		if (truetype(t, BaseType.INT)) {
			i.stmt.accept(this);
			if (i.stmt2 != null) {
				i.stmt2.accept(this);
			}
			
		} else {
			error("If statement condition not int type");
		}
		return null;
	}

	@Override
	public Type visitAssign(Assign ass) {
		Type lhs = ass.exp.accept(this);
		Type rhs = ass.exp2.accept(this);
		ass.exp.type = lhs;
		ass.exp2.type = rhs;
		if (lhs != null && rhs != null) {
			if (truetype(lhs, BaseType.VOID) || lhs.getClass() == ArrayType.class) {
				error("Assigment of left side not possible");
			}
			
			if (!truetype(lhs, rhs)) {
//				System.out.println(lhs);
//				System.out.println(rhs);
				error("Mismatched assignment types ");
			}
		}

		return null;
	}
	
	public boolean truetype(Type t, Type t2) {
		List<BaseType> b = Arrays.asList(BaseType.values());
		if (t == null || t2 == null) {
			error("Type error");
			return false;
		}
		if(b.contains(t) && b.contains(t2)) {
			return t.equals(t2);
		}else if (t.getClass() == StructType.class && t2.getClass() == StructType.class){
//			System.out.println("here");
			StructType st1 = (StructType) t;
			StructType st2 = (StructType) t2;
			return st1.stName.equals(st2.stName);
		}
		else if (t.getClass() == ArrayType.class && t2.getClass() == ArrayType.class) {
			ArrayType at = (ArrayType) t;
			ArrayType at2 = (ArrayType) t2;
			
			if (at.integer != at2.integer) { //Don't know if these would ever be needed				
				error("Array lengths don't match");
			}
			return truetype(at.type, at2.type);
		}else if (t.getClass() == PointerType.class && t2.getClass() == PointerType.class) {
			PointerType pt = (PointerType) t;
			PointerType pt2 = (PointerType) t2;
			return truetype(pt.type, pt2.type);
		}else {
			return false;
		}
	}

	@Override
	public Type visitReturn(Return ret) {
		
		if (ret.exp == null && truetype(ret.type, BaseType.VOID)) {
			
			return BaseType.VOID;
		} else if (ret.exp != null){
			ret.exp.type = ret.type;
			Type t = ret.exp.accept(this);
//			System.out.println(t);
			if (truetype(t, ret.type)) {
				return ret.type;
			}

		else {
				error("Mismatched return type " + ret.type + " with " + t);
			}
		} else if (ret.exp == null && !truetype(ret.type, BaseType.VOID)) {
			error("Return type null when expected type");
		}
//		if (ret.type == null) {
//			return null;
//		}
		
		return null;
	}

	// To be completed...


}
