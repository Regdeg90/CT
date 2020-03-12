package gen;

import ast.*;

import java.io.PrintWriter;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StringStructGenerator implements ASTVisitor<Register> {
	
	public PrintWriter writer;
	public List<String> strings = new ArrayList<String>();
	public HashMap<String, Integer> stdsize = new HashMap<>();
	int offset = 0;
	public StringStructGenerator(PrintWriter w, Program p, HashMap<String, Integer> stdsize2) {
		writer = w;
		stdsize = stdsize2;
		p.accept(this);
		
	}
	
	@Override
	public Register visitBaseType(BaseType bt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Register visitStructTypeDecl(StructTypeDecl st) {
		offset = 0;
		for (VarDecl vd : st.varDecls) {
			st.fexoff.put(vd.varName, offset);
			////System.out.println(vd.varName + " offset = " + offset);
			if (vd.type == BaseType.INT || vd.type.getClass() == PointerType.class || vd.type == BaseType.CHAR) {
				offset += 4;

			} else if (vd.type.getClass() == ArrayType.class) {
				ArrayType at = (ArrayType) vd.type;
				if (at.type.getClass() == StructType.class) {
					StructType st2 = (StructType) at.type;
					offset += at.integer*st2.std.size;
				} else {
					offset += at.integer*4;
				}
				
			} else if (vd.type.getClass() == StructType.class) {
				StructType st3 = (StructType) vd.type;
				offset += st3.std.size;
			}
			
		}
//		writer.println(st.structType.stName + ": 		.space  "+ offset + "\n .align 2");
		st.size = offset;
		stdsize.put(st.structType.stName, st.size);
		////System.out.println(st.structType.stName + " has size " + st.size);

		return null;
	}

	@Override
	public Register visitBlock(Block b) {
		for (VarDecl vd : b.vardecls) {
			vd.accept(this);
		}
		
		for (Stmt stmt : b.stmts) {
			////System.out.println("yo");
			stmt.accept(this);
		}
		return null;
	}

	@Override
	public Register visitFunDecl(FunDecl p) {
		////System.out.println("yo");
		p.block.accept(this);
		return null;
	}

	@Override
	public Register visitProgram(Program p) {
		for (StructTypeDecl std : p.structTypeDecls) {
			std.accept(this);
		}

		for (FunDecl fd : p.funDecls) {
			////System.out.println("yo");
			fd.accept(this);
		}
		
		return null;
	}

	@Override
	public Register visitVarDecl(VarDecl vd) {
		////System.out.println(offset);
//		if (vd.type.getClass() == StructType.class) {
//			StructType st = (StructType) vd.type;
//			writer.println(vd.varName + ": 		.space  "+ st.std.size + "\n");
//
//		} else if (vd.type.getClass() == ArrayType.class) {
//			ArrayType at = (ArrayType) vd.type;
//			if (at.type.getClass() == StructType.class) {
//				StructType st2 = (StructType) at.type;
//				writer.println(vd.varName + ": 		.space  "+ at.integer*st2.std.size + "\n");
//			}
//			
//			
//		}
		return null;
	}

	@Override
	public Register visitVarExpr(VarExpr v) {
		
		return null;
	}

	@Override
	public Register visitStructType(StructType st) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Register visitPointerType(PointerType pt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Register visitArrayType(ArrayType at) {
		
		return null;
	}

	@Override
	public Register visitIntLiteral(IntLiteral i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Register visitStrLiteral(StrLiteral str) {
		//////System.out.println("her");
		writer.println("string"+ strings.size()+ ": 		.asciiz  \"" + str.str + "\" \n .align 2");
		strings.add("string" + strings.size());
		return null;
	}

	@Override
	public Register visitChrLiteral(ChrLiteral ch) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Register visitFunCallExpr(FunCallExpr fc) {
		for (Expr exp : fc.exprs) {
			////System.out.println("heere");
			exp.accept(this);
		}
		
		return null;
	}

	@Override
	public Register visitBinOp(BinOp b) {
		b.exp1.accept(this);
		b.exp2.accept(this);
		return null;
	}

	@Override
	public Register visitOp(Op op) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Register visitArrayAccessExpr(ArrayAccessExpr aae) {
		aae.exp1.accept(this);
		aae.exp2.accept(this);

		return null;
	}

	@Override
	public Register visitFieldAccessExpr(FieldAccessExpr fae) {
		fae.exp.accept(this);

		return null;
	}

	@Override
	public Register visitValueAtExpr(ValueAtExpr vae) {
		vae.exp.accept(this);

		return null;
	}

	@Override
	public Register visitSizeOfExpr(SizeOfExpr soe) {
		soe.type.accept(this);
		return null;
	}

	@Override
	public Register visitTypecastExpr(TypecastExpr tce) {
		tce.exp.accept(this);

		return null;
	}

	@Override
	public Register visitExprStmt(ExprStmt es) {
		es.exp.accept(this);
		return null;
	}

	@Override
	public Register visitWhile(While w) {
		w.exp.accept(this);
		w.stmt.accept(this);
		return null;
	}

	@Override
	public Register visitIf(If i) {
		i.exp.accept(this);
		i.stmt.accept(this);
		if(i.stmt2 != null) {
			i.stmt2.accept(this);
		}
		return null;
	}

	@Override
	public Register visitAssign(Assign ass) {
		ass.exp.accept(this);
		ass.exp2.accept(this);
		return null;
	}

	@Override
	public Register visitReturn(Return ret) {
		if (ret.exp != null) {
			ret.exp.accept(this);
		}
		return null;
	}
	
}
