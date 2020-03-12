package gen;

import ast.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class CodeGenerator implements ASTVisitor<Register> {

    /*
     * Simple register allocator.
     */

    // contains all the free temporary registers
    private Stack<Register> freeRegs = new Stack<Register>();
    public List<String> labels = new ArrayList<String>();
    public List<String> strings = new ArrayList<String>();
    public List<Register> usedRegs = new ArrayList<Register>();
    public HashMap<String, Integer> stdsize = new HashMap<>();
    public String functionname;
    public int offset;


    public CodeGenerator() {
        freeRegs.addAll(Register.tmpRegs);
    }

    private class RegisterAllocationError extends Error {}

    private Register getRegister() {
        try {
            return freeRegs.pop();
        } catch (EmptyStackException ese) {
            throw new RegisterAllocationError(); // no more free registers, bad luck!
        }
    }

    private void freeRegister(Register reg) {
        freeRegs.push(reg);
    }





    private PrintWriter writer; // use this writer to output the assembly instructions


    public void emitProgram(Program program, File outputFile) throws FileNotFoundException {
        writer = new PrintWriter(outputFile);

        visitProgram(program);
        writer.close();
    }

    @Override
    public Register visitBaseType(BaseType bt) {
        return null;
    }

    @Override
    public Register visitStructTypeDecl(StructTypeDecl st) {
        return null;
    }

    @Override
    public Register visitBlock(Block b) {
    	int oldoffset = offset;

    	
        for(VarDecl vd : b.vardecls) {
        	vd.accept(this);
        }
        
        for (Stmt stmt : b.stmts) {
        	stmt.accept(this);
        }
//        writer.println("move $sp, $fp"); comment(" Reseting block sp");
//        writer.println("addi, $sp, $sp, " + (offset-oldoffset));
        offset = oldoffset;
        return null;
    }

    @Override
    public Register visitFunDecl(FunDecl p) {
    	writer.println(p.name + ":");
    	writer.println("move $fp $sp");
    	functionname = p.name;

    	
		
//		writer.println("addi $sp, $sp, " +(offset-p.startoffset-8));
		offset=0;

    	
    	for (VarDecl vd : p.params) {
    		//System.out.println("params");
    		vd.accept(this);
    	}
    	p.block.accept(this);

    	writer.println(p.name + "end:");

    	p.endoffset = offset;
//    	writer.println("addi $sp, $sp, 4");
		
    	writer.println("move $sp, $fp"); comment(" Moving sp back to fp " + p.name);
    	if (!p.name.equals("main")) {
    		writer.println("jr $ra");
    	}

    	
//    	writer.println("move $fp $sp");
        return null;
    }

    @Override
    public Register visitProgram(Program p) {
    	writer.println( " 		.data");
    	new StringStructGenerator(writer, p, stdsize);
    	
        for (StructTypeDecl std : p.structTypeDecls) {
        	std.accept(this);
        }
        
        for (VarDecl vd : p.varDecls) {
        	globalVarDecl(vd);
        }
        writer.println( " 		.text");
        offset = 0;
    	writer.println("j main");
//    	writer.println("move $fp $sp");
        for (FunDecl fd : p.funDecls) {
        	
        	fd.accept(this);
        }
        
		Register v = Register.v0;
		
		writer.println("li " + v.toString() + ", " + 10);
		writer.println("add $a0, " + getRegister().toString() + ", $zero");
		writer.println("syscall");
        
        return null;
    }

    private void globalVarDecl(VarDecl vd) {
    	vd.global = true;
		if (vd.type == BaseType.INT || vd.type == BaseType.CHAR  || vd.type.getClass() == PointerType.class ) {
			writer.println(vd.varName + ": 		.word 0");
		} else if (vd.type.getClass() == ArrayType.class) {
			ArrayType at = (ArrayType) vd.type;
			writer.println(vd.varName + ": 		.space " + at.integer*4);
			
		} else if (vd.type.getClass() == StructType.class) {
			StructType st = (StructType) vd.type;
			writer.println(vd.varName + ": 		.space " + st.std.size);
		}
		
	}

	@Override
    public Register visitVarDecl(VarDecl vd) {
		vd.offset = offset;
		//System.out.println(offset);
		//System.out.println(vd.type);
		if (vd.type == BaseType.INT || vd.type.getClass() == PointerType.class || vd.type == BaseType.CHAR) {
			offset += 4;
			writer.println("addi $sp $sp -4"); comment(" Visit VarDecl " + vd.varName);
		} else if (vd.type.getClass() == ArrayType.class) {
			ArrayType at = (ArrayType) vd.type;
			if (at.type.getClass() == StructType.class) {
				StructType st2 = (StructType) at.type;
				offset += at.integer*st2.std.size;
				writer.println("addi $sp $sp -" + (at.integer)*st2.std.size); comment(" Visit VarDecl " + vd.varName);
			} else {
				offset += at.integer*4;
				writer.println("addi $sp $sp -" + (at.integer)*4); comment(" Visit VarDecl " + vd.varName);
			}
			
		} else if (vd.type.getClass() == StructType.class) {
			StructType st = (StructType) vd.type;
			//System.out.println(st.stName);
			//System.out.println(st.std.size);
			offset += st.std.size;
			writer.println("addi $sp $sp -" + st.std.size); comment(" Visit VarDecl " + vd.varName);
		}
		
        return null;
    }

    @Override
    public Register visitVarExpr(VarExpr v) {
        Register reg = getRegister();
        comment(v.name + " " + ((offset - v.vd.offset) - offset));
        if(!v.vd.global) {
//        	if (v.type == BaseType.INT || v.type.getClass() == PointerType.class || v.type == BaseType.CHAR) {

    		writer.println("la " + reg + ", -" + v.vd.offset + " ($fp)"); comment(" Visit VarExpr " + v.name + " Global: " + v.vd.global);

    		//    		} 
//        	else if (v.type.getClass() == ArrayType.class) {
//    			ArrayType at = (ArrayType) v.type;
//    			if (at.type.getClass() == StructType.class) {
//    				StructType st2 = (StructType) at.type;
//    				
//    				writer.println("la " + reg + ", -" +  v.vd.offset  + " ($fp)");
//    			} else {
//    				writer.println("la " + reg.toString() + ", " + (offset - v.vd.offset + 4) + " ($fp)");
//    			}
//    			
//    		} else if (v.type.getClass() == StructType.class) {
//    			StructType st = (StructType) v.type;
//    			writer.println("la " + reg.toString() + ", " + (offset - v.vd.offset + 4) + " ($sp)");
//    		}
        } else {
        	if (v.vd.type.getClass() == StructType.class) {
        		StructType st = (StructType) v.vd.type;
        		writer.println("la " + reg.toString() + ", " + v.vd.varName); comment(" Visit VarExpr " + v.name + " Global: " + v.vd.global);
        		writer.println("addi " + reg + ", " + reg + ", " + (stdsize.get(st.stName)-4));
        	} else if (v.vd.type.getClass() == ArrayType.class){
        		ArrayType at = (ArrayType) v.vd.type;
        		writer.println("la " + reg.toString() + ", " + v.vd.varName); comment(" Visit VarExpr " + v.name + " Global: " + v.vd.global);
        		
    			if (at.type.getClass() == StructType.class) {
    				StructType st2 = (StructType) at.type;
    				
    				writer.println("addi " + reg + ", " + reg + ", " + ((at.integer)*stdsize.get(st2.stName)-4));
    			} else {
    				writer.println("addi " + reg + ", " + reg + ", " + ((at.integer)*4-4));

    			}
        	} else {
        		writer.println("la " + reg.toString() + ", " + v.vd.varName); comment(" Visit VarExpr " + v.name + " Global: " + v.vd.global);
        	}
        	
        }
        
        
        return reg;
    }

	@Override
	public Register visitStructType(StructType st) {
		writer.println("Visiting struct type " + st.stName + " with structdecl " + st.std);
		return null;
	}

	@Override
	public Register visitPointerType(PointerType pt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Register visitArrayType(ArrayType at) {
		//System.out.println("herererer");
		return null;
	}

	@Override
	public Register visitIntLiteral(IntLiteral i) {
		Register result = getRegister();
		writer.println("li " + result.toString() + "," + i.i);
		return result;
	}

	@Override
	public Register visitStrLiteral(StrLiteral str) {
		//System.out.println("visiting str literal");
		Register result = getRegister();
//		if (str.str.contains("\\n")) {
//			str.str.replaceAll("\n", "\\n");
//		}
		writer.println("la " + result.toString() + ", string" + strings.size());
		strings.add("string"+ strings.size());
		return result;
	}

	@Override
	public Register visitChrLiteral(ChrLiteral ch) {
		Register result = getRegister();
		writer.println("la " + result.toString() + ", '" + ch.ch + "'");
		return result;
	}

	@Override
	public Register visitFunCallExpr(FunCallExpr fc) {
//		if(labels.contains(fc.str)) {
//			writer.println(fc.str + (labels.size()+1) + ":");
//			labels.add(fc.str + (labels.size()+1));
//		} else {
//			writer.println(fc.str + ":");
//			labels.add(fc.str);
//		}
		
		//System.out.println(fc.str);
		if (fc.str.equals("print_i")) {
			Register res = null;
			for(Expr exp : fc.exprs) {
				res = exp.accept(this);
				//System.out.println(exp.getClass());
//				ValueAtExpr vae = (ValueAtExpr) exp;
//				//System.out.println(vae.exp.getClass());
				getdata(res,exp);
//				if (exp.getClass() != BinOp.class && exp.getClass() != IntLiteral.class && exp.getClass() != FunCallExpr.class) {
//					writer.println("lw " + res.toString() + ", (" + res.toString() + ")");
//				}
			}
			
			
			Register v = Register.v0;
			writer.println("li " + v.toString() + ", " + 1);
			writer.println("add $a0, " + res.toString() + ", $zero");
			writer.println("syscall");
			freeRegister(res);
			
		} else if (fc.str.equals("read_i")) {
			Register res = getRegister();
//			for(Expr exp : fc.exprs) {
//				res = exp.accept(this);
//				writer.println("lw " + res.toString() + ", (" + res.toString() + ")");
//				
//			}
			Register v = Register.v0;
			writer.println("li " + v.toString() + ", " + 5);
			writer.println("syscall");
			writer.println("move, " + res.toString() + ", " + v.toString());
			return res;
		} else if (fc.str.equals("print_s")) {
			Register res = null;
			for(Expr exp : fc.exprs) {
				//System.out.println("600");
				//System.out.println(exp.getClass());
				//System.out.println(exp.type.getClass());
				res = exp.accept(this);
				
//				if (exp.type.getClass() == PointerType.class) {
//					writer.println("lw " + res + " (" + res + ")");
//					comment("fucking work");
//				}
				
				if (exp.getClass() == TypecastExpr.class) {
					TypecastExpr tce = (TypecastExpr) exp;
					//System.out.println(tce.exp.type);
					if (tce.exp.getClass() != StrLiteral.class) {
						
//						getdata(res, exp);
						if (tce.exp.type.getClass() == ArrayType.class) {
							ArrayType at = (ArrayType) tce.exp.type;
							//System.out.println(at.type);
							for (int i = 0; i < at.integer; i++) {
								//System.out.println(i);
								
								Register v = Register.v0;
								writer.println("li " + v.toString() + ", " + 4);
								writer.println("move $a0, " + res.toString());
								writer.println("syscall");
								writer.println("subi " + res.toString() + ", " + res.toString() + ", 4");
							}
						} else {
							getdata(res,exp);
						}
					}
					
				} else {
					
//					if (exp.type.getClass() == PointerType.class) {
//						//System.out.println("current here");
//						PointerType pt = (PointerType) exp.type;
//						VarExpr ve = (VarExpr)exp;
//						//System.out.println(ve.type);
//						if (pt.type.getClass() == ArrayType.class) {
//							ArrayType at = (ArrayType) pt.type;
//							//System.out.println(at.type);
//							for (int i = 0; i < at.integer; i++) {
//								//System.out.println(i);
//								
//								Register v = Register.v0;
//								writer.println("li " + v.toString() + ", " + 4);
//								writer.println("move $a0, " + res.toString());
//								writer.println("syscall");
//								writer.println("subi " + res.toString() + ", " + res.toString() + ", 4");
//							}
//						} else {
//							getdata(res,exp);
//						}
						

					getdata(res, exp);
					
					
				}
				
			}
			
			Register v = Register.v0;
			writer.println("li " + v.toString() + ", " + 4);
			writer.println("move $a0, " + res.toString());
			writer.println("syscall");
			freeRegister(res);
			
		}else if ( fc.str.equals("read_c")) {
			Register res = getRegister();
//			for(Expr exp : fc.exprs) {
//				res = exp.accept(this);
//				writer.println("lw " + res.toString() + ", (" + res.toString() + ")");
//				
//			}
			Register v = Register.v0;
			writer.println("li " + v.toString() + ", " + 12);
			writer.println("syscall");
			writer.println("move, " + res.toString() + ", " + v.toString());
			return res;
		} else if (fc.str.equals("print_c")) {
			Register res = null;
			for(Expr exp : fc.exprs) {
				res = exp.accept(this);
				getdata(res, exp);
//				if (exp.getClass() != BinOp.class && exp.getClass() != ChrLiteral.class) {
//					writer.println("lb " + res.toString() + ", (" + res.toString() + ")");
//				}
			}
			
			
			Register v = Register.v0;
			writer.println("li " + v + ", " + 11);
			writer.println("add $a0, " + res + ", $zero");
			writer.println("syscall");
			freeRegister(res);
			
		} else if (fc.str.equals("mcmalloc")) {
			Register res = null;
			for(Expr exp : fc.exprs) {
				res = exp.accept(this);
				getdata(res, exp);
				if(exp.getClass() == SizeOfExpr.class) {
					SizeOfExpr soe = (SizeOfExpr) exp;
					if (soe.type == BaseType.CHAR) {
						writer.println("li " + res + ", 4");
					}
				}
//				if (exp.getClass() != BinOp.class && exp.getClass() != ChrLiteral.class) {
//					writer.println("lb " + res.toString() + ", (" + res.toString() + ")");
//				}
			}
			
			
			Register v = Register.v0;
			writer.println("li " + v.toString() + ", 9");
			writer.println("add $a0, " + res.toString() + ", $zero");
			writer.println("syscall");
			writer.println("add $v0, $v0, " + res);
			writer.println("addi $v0, $v0 4");
			writer.println("move " + res + ", $v0");
			return res;
			
		} 
		
		else {
			
			usedRegs = new ArrayList<Register>();

			for (Register reg : Register.tmpRegs) {
				if (!freeRegs.contains(reg)) {
					usedRegs.add(reg);
					writer.println("sw " + reg + ", ($sp)"); comment("Saving used register");
					writer.println("addi $sp, $sp, -4");
					offset+=4;
					freeRegister(reg);
				}
			}

			int oldoffset = offset;
			Register ret = null;
//			fc.fd.startoffset = offset;
			writer.println("sw $fp, ($sp)");
//			offset = 8;
			writer.println("addi $sp, $sp, -4");
			writer.println("sw $ra ($sp)"); comment(" Saving return addres for " + fc.str);
			writer.println("addi $sp, $sp, -4");
//			offset+=4;
	    	
	    	
			VarDecl[] params = fc.fd.params.toArray(new VarDecl[0]);
			Expr[] args = fc.exprs.toArray(new Expr[0]);
			int count = 0;
			for (int i =0; i < fc.exprs.size(); i++) {
				//System.out.println("declared in a function type is " + args[i].type);
				Register reg = args[i].accept(this);
//				getdatalhs(reg, args[i]);
				
				try {
					if (args[i].getClass() == ValueAtExpr.class) {
						ValueAtExpr vae = (ValueAtExpr) args[i];
						writer.println("lw " + reg + "(" + reg + ")");
						args[i] = vae.exp;
						
					}
				} catch (NullPointerException n2) {
					
				}
				
				
				
				
				try {
					
					
				if (args[i].type.getClass() == StructType.class && args[i].getClass() == VarExpr.class) {
					Register temp = getRegister();
					comment("Saving a struct to stack");
					StructType st = (StructType) args[i].type;
					int j =0;
					while (j < st.std.size) {
						writer.println("lw " + temp + ", -" + j + "("+ reg + ")");
						writer.println("sw " + temp + ", ($sp)");
						writer.println("addi $sp, $sp, -4");
						count+=4;
						j+=4;
					}
					freeRegister(temp);

				} else {

					getdata(reg, args[i]);
					writer.println("sw " + reg + ", ($sp)");
					writer.println("addi $sp, $sp, -4");
					count +=4;
				}
				freeRegister(reg);
				} catch (NullPointerException n) {
					getdata(reg, args[i]);
					writer.println("sw " + reg + ", ($sp)");
					writer.println("addi $sp, $sp, -4");
					count +=4;
				}

				
//				if (args[i].getClass() == VarExpr.class) {
//					writer.println("lw " + reg + ", ("+ reg +")" );
//				}
//				writer.println("sw " + reg + ", -" + params[i].offset + "($sp)" );
				

			}
			writer.println("addi $sp, $sp, " + count);
//			offset = oldoffset+4;
			writer.println("jal " + fc.str);
//			writer.println("addi $sp, $sp, 4");
//			writer.println("addi $sp, $sp " + (offset - fc.fd.startoffset +8));
			writer.println("addi $sp, $sp, 4");
			writer.println("lw $ra " + "($sp)"); comment(" Getting return addres for " + fc.str);
			
			writer.println("addi $sp, $sp, 4");
			writer.println("lw $fp, ($sp)");
			offset = oldoffset;
			Collections.reverse(usedRegs);
			
			for (Register reg : usedRegs) {				
				writer.println("addi $sp, $sp, 4");
				writer.println("lw " + reg + ", ($sp)"); comment("Restoring used register");
				offset -=4;
				freeRegs.remove(reg);
			}
//			writer.println("addi $sp, $sp, -4");
			
			
			if (fc.fd.type != BaseType.VOID) {
				ret = getRegister();
				writer.println("move " + ret + ", $v0");
			}
			return ret;
		}
		return null;
	}

	@Override
	public Register visitBinOp(BinOp b) {
		String label = "binopend" + labels.size(); 
//		writer.println(label + ":");
		labels.add(label);
		Register result = null;
		Register exp1 = b.exp1.accept(this);
		getdata(exp1, b.exp1);
//		if (b.exp1.getClass() != BinOp.class && b.exp1.getClass() != IntLiteral.class && b.exp1.getClass() != FunCallExpr.class) {
//			writer.println("lw " + exp1.toString() + ", (" + exp1.toString() + ")");
//		}
		//Free this one and push to stack then call after exp2
		//System.out.println(b.exp1.getClass());
		Register exp2 = null;
		if (b.op != Op.AND && b.op != Op.OR) {
			
			comment("Saving lhs of binop to stack");
			writer.println("sw " + exp1 + ", ($sp)" );
			writer.println("addi $sp, $sp, -4");
			
			freeRegister(exp1);
			exp2 = b.exp2.accept(this);
			getdata(exp2, b.exp2);
			
			comment("Getting lhs of binop from stack");
			exp1 = getRegister();
			writer.println("addi $sp, $sp, 4");
			writer.println("lw " + exp1 + ", ($sp)" );
			result = getRegister();
//			if ((b.exp2.getClass() != BinOp.class && b.exp2.getClass() != IntLiteral.class && b.exp2.getClass() != ChrLiteral.class 
//					&& b.exp1.getClass() != FunCallExpr.class)) {
//				writer.println("lw " + exp2.toString() + ", (" + exp2.toString() + ")");
//			}
		}
		
		if (b.op == Op.ADD) {	
			writer.println("add " + result.toString() + ", " + exp1.toString() + ", " + exp2.toString());
			freeRegister(exp1);
			freeRegister(exp2);
		} else if (b.op == Op.SUB) {
			writer.println("sub " + result.toString() + ", " + exp1.toString() + ", " + exp2.toString());
			freeRegister(exp1);
			freeRegister(exp2);
		} else if (b.op == Op.DIV) {
			writer.println("div " + result.toString() + ", " + exp1.toString() + ", " + exp2.toString());
			writer.println("mflo " + result.toString());
			freeRegister(exp1);
			freeRegister(exp2);
		} else if (b.op == Op.MOD) {
			writer.println("div " + result.toString() + ", " + exp1.toString() + ", " + exp2.toString());
			writer.println("mfhi " + result.toString());
			freeRegister(exp1);
			freeRegister(exp2);
		} else if (b.op == Op.MUL) {
			writer.println("mul " + result.toString() + ", " + exp1.toString() + ", " + exp2.toString());
			freeRegister(exp1);
			freeRegister(exp2);
		} else if (b.op == Op.LT) {
			writer.println("slt " + result.toString() + ", " + exp1.toString() + ", " + exp2.toString());
			freeRegister(exp1);
			freeRegister(exp2);
		} else if (b.op == Op.GT) {
			writer.println("slt " + result.toString() + ", " + exp2.toString() + ", " + exp1.toString());
			freeRegister(exp1);
			freeRegister(exp2);
		} else if (b.op == Op.LE) {
			writer.println("addi " + exp2.toString() + ", " + exp2.toString() + ", 1");
			writer.println("slt " + result.toString() + ", " + exp1.toString() + ", " + exp2.toString());
			freeRegister(exp1);
			freeRegister(exp2);
		} else if (b.op == Op.GE) {
			writer.println("addi " + exp1.toString() + ", " + exp1.toString() + ", 1");
			writer.println("slt " + result.toString() + ", " + exp2.toString() + ", " + exp1.toString());
			freeRegister(exp1);
			freeRegister(exp2);
		} else if (b.op == Op.EQ) {
			writer.println("seq " + result.toString() + ", " + exp1.toString() + ", " + exp2.toString());
			freeRegister(exp1);
			freeRegister(exp2);
		} else if (b.op == Op.NE) {
			writer.println("sne " + result.toString() + ", " + exp1.toString() + ", " + exp2.toString());
			freeRegister(exp1);
			freeRegister(exp2);
		} else if (b.op == Op.AND) {
			Register temp = getRegister();
			result = getRegister();
			writer.println("addi " + temp.toString() + ", $zero , 1");
			
			writer.println("seq " + result.toString() + ", " + exp1.toString() + ", " + temp.toString());
			freeRegister(temp);
			writer.println("beq " + result + ", $zero, " + label);

			
			comment("Saving lhs of binop to stack");
			writer.println("sw " + exp1 + ", ($sp)" );
			writer.println("addi $sp, $sp, -4");
			freeRegister(exp1);
			
			exp2 = b.exp2.accept(this);
			getdata(exp2, b.exp2);
			
			comment("Getting lhs of binop from stack");
			exp1 = getRegister();
			writer.println("addi $sp, $sp, 4");
			writer.println("lw " + exp1 + ", ($sp)" );
//			if ((b.exp2.getClass() != BinOp.class && b.exp2.getClass() != IntLiteral.class)) {
//				writer.println("lw " + exp2.toString() + ", (" + exp2.toString() + ")");
//			}

			writer.println("seq " + result.toString() + ", " + exp2.toString() + ", " + result.toString());
			
			freeRegister(exp1);
			freeRegister(exp2);
			
		} else if (b.op == Op.OR) {
			Register temp = getRegister();
			result = getRegister();
			writer.println("addi " + temp.toString() + ", $zero , 1");
			writer.println("move " + result + ", "  + exp1);
			writer.println("beq " + exp1 + ", " + temp + ", " + label);
			freeRegister(temp);

			
			comment("Saving lhs of binop to stack");
			writer.println("sw " + exp1 + ", ($sp)" );
			writer.println("addi $sp, $sp, -4");
			freeRegister(exp1);
			
			exp2 = b.exp2.accept(this);
			getdata(exp2, b.exp2);
			
			comment("Getting lhs of binop from stack");
			exp1 = getRegister();
			writer.println("addi $sp, $sp, 4");
			writer.println("lw " + exp1 + ", ($sp)" );
//			if ((b.exp2.getClass() != BinOp.class && b.exp2.getClass() != IntLiteral.class) ) {
//				writer.println("lw " + exp2.toString() + ", (" + exp2.toString() + ")");
//			}


			writer.println("addi " + temp.toString() + ", $zero , 1");
			writer.println("or " + result.toString() + ", " + exp1.toString() + ", " + exp2.toString());
			
			writer.println("seq " + result.toString() + ", " + temp.toString() + ", " + result.toString());
			
			freeRegister(exp1);
			freeRegister(exp2);
			freeRegister(temp);
		}
//		labels.add(label);
		writer.println(label + ":");
		return result;
	}

	@Override
	public Register visitOp(Op op) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Register visitArrayAccessExpr(ArrayAccessExpr aae) {
		Register r = getRegister();
		Register reg = aae.exp1.accept(this);
		Register integer = aae.exp2.accept(this);
//		writer.println();
		////System.out.println("herereeee");
		////System.out.println(aae.exp1.type.getClass());
		if(aae.exp1.type.getClass() == PointerType.class) {
			writer.println("lw " + reg + ", (" + reg + ")");
		}
		getdatalhs(reg, aae.exp1);
		
		getdata(integer, aae.exp2);
//		if (aae.exp2.getClass() == VarExpr.class) {
//			writer.println("lw " + integer.toString() + ", (" + integer.toString() + ")");
//		}
		
		writer.println("addi " + r.toString() + ", $zero, 4");
		writer.println("mul " + integer.toString() + ", " + integer.toString() + ", " + r.toString());
		writer.println("sub " + integer.toString() + ", " + reg.toString() + ", " + integer.toString());
		writer.println("la " + reg.toString() + ", (" + integer.toString() + ")");
		freeRegister(integer);
		freeRegister(r);
		return reg;
	}

	@Override
	public Register visitFieldAccessExpr(FieldAccessExpr fae) {
		comment("Field Access that happening " + fae.str + " " + fae.offset);
		
		//System.out.println("this is fae " + fae.exp.type.getClass());
		
		Register reg = fae.exp.accept(this);
		getdatalhs(reg, fae.exp);
		if (fae.exp.type.getClass() == StructType.class) {
			
			StructType st = (StructType) fae.exp.type;
			//System.out.println("fae.exp.type name " + st.std);
			
			writer.println("addi " + reg + ", " + reg + ", -" + st.std.fexoff.get(fae.str));
		}
		
//		writer.println("addi " + reg + ", " + reg + ", " + -);
//		writer.println("lw " + reg + ", (" + reg + ")"  );
		return reg;
	}

	@Override
	public Register visitValueAtExpr(ValueAtExpr vae) {
		Register reg = vae.exp.accept(this);
//		writer.println("lw " + reg + ", (" + reg + ")");
		return reg;
	}

	@Override
	public Register visitSizeOfExpr(SizeOfExpr soe) {
		Register reg = getRegister();
		//System.out.println(soe.type);
		if (soe.type.getClass() == StructType.class) {
			StructType st = (StructType) soe.type;

			writer.println("li " + reg + ", " + stdsize.get(st.stName));
		} 
		else if (soe.type == BaseType.CHAR){
			writer.println("li " + reg + ", 1");
		} 
		else {
			writer.println("li " + reg + ", 4");
		}
		
		return reg;
	}

	@Override
	public Register visitTypecastExpr(TypecastExpr tce) {
//		//System.out.println(tce.exp.getClass());
		
		Register reg = tce.exp.accept(this);
//		Register temp = getRegister();
//		writer.println("move " + temp + ", " + reg);
		comment("Preparing to typecast");
		if (tce.exp.type == BaseType.CHAR && tce.type == BaseType.INT) {
			writer.println("lw " + reg + ", (" + reg + ")");
		} else if (tce.exp.type.getClass() == ArrayType.class && tce.type.getClass() == PointerType.class) {		
			ArrayType at = (ArrayType) tce.exp.type;
			int j = 0;
			
			 
		} else if (tce.exp.type.getClass() == PointerType.class && tce.type.getClass() == PointerType.class) {
			PointerType pt = (PointerType) tce.type;
			//not sure this works
			 
		}

		return reg;
	}

	@Override
	public Register visitExprStmt(ExprStmt es) {
		
		es.exp.accept(this);
		return null;
	}

	@Override
	public Register visitWhile(While w) {
		
		String start = "while" + labels.size();
		String lab = "whileend" + labels.size();
		labels.add(lab);
		writer.println(start + ":");
		Register exp = w.exp.accept(this);
		getdata(exp, w.exp);
		writer.println("beq " + exp.toString() + ", $zero, " + lab);
		freeRegister(exp);
		w.stmt.accept(this);
		writer.println("j " + start);
		writer.println(lab + ":");
		
		
		return null;
	}

	@Override
	public Register visitIf(If i) {
		Register exp = i.exp.accept(this);
		getdata(exp, i.exp);
		String lab = "ifend" + labels.size();
		if (i.stmt2 == null) {
			labels.add(lab);
			writer.println("beq " + exp.toString() + ", $zero, " + lab);
			freeRegister(exp);
			int oldoffset = offset;
			i.stmt.accept(this);
			offset = oldoffset;
			
		} else {
			labels.add(lab);
			writer.println("beq " + exp.toString() + ", $zero, " + lab + "else"  );
			freeRegister(exp);
			String els = lab + "else";
			int oldoffset = offset;
			i.stmt.accept(this);
			writer.println("j " + lab);
			writer.println(els + ":");
			offset = oldoffset;
			i.stmt2.accept(this);
		}
		writer.println(lab + ":");
		
		return null;
	}

	@Override
	public Register visitAssign(Assign ass) {
		
//		Register value = getRegister(); comment("Register for value of binop");
		comment("Loading address in assign exp 1 ");
		Register exp1 = ass.exp.accept(this);
		comment("Loading value into assign exp 2");
		Register exp2 = ass.exp2.accept(this);
		//System.out.println("assingment of " + ass.exp2.getClass());
//		if (ass.exp2.getClass() == VarExpr.class || ass.exp2.getClass() == ArrayAccessExpr.class) {
//
//			writer.println("lw " + exp2.toString() + ", (" + exp2.toString() + ")");
////			writer.println("move " + value.toString() + ", " + exp2.toString());
////			writer.println("la " + exp1.toString() + ", 0(" + exp1.toString() + ")");
//			writer.println("sw " + exp2.toString() + ", 0(" + exp1.toString() +")");
//		} 
////		else if (ass.exp2.getClass() == TypecastExpr.class) {
////			TypecastExpr tce = (TypecastExpr) ass.exp2;
////			if (tce.exp.getClass() == StrLiteral.class) {
////				writer.println("move " + value.toString() + ", " + exp2.toString());
////				
////			}
////		
//		else {
////			writer.println("move " + value.toString() + ", " + exp2.toString());
////			writer.println("la " + exp1.toString() + ", 0(" + exp1.toString() + ")");
		if(ass.exp.getClass() == ValueAtExpr.class) {
			getdatalhs(exp1, ass.exp);

		}

		if (ass.exp2.type.getClass() == StructType.class) {
			if(ass.exp2.getClass() == ValueAtExpr.class) {
				getdatalhs(exp2, ass.exp2);
			}
			Register temp = getRegister();
			comment("Assigning a struct");
			StructType st = (StructType) ass.exp.type;
			int j =0;
			//System.out.println(st.std);
			while (j < st.std.size) {
				writer.println("lw " + temp + ", -" + j + "("+ exp2 + ")");
				writer.println("sw " + temp + ", -"+ j +"("+  exp1 + ")");
				j+=4;
			}
			freeRegister(temp);
		} else {

			
			//System.out.println("getting data from exp2 " + ass.exp2.getClass());
			getdata(exp2, ass.exp2);
			writer.println("sw " + exp2.toString() + ", 0(" + exp1.toString() +")");
		}

//		}
//		freeRegister(value);
		freeRegister(exp1);
		freeRegister(exp2);
		
		return null;
	}



	@Override
	public Register visitReturn(Return ret) {
		if (ret.exp != null) {
			Register reg = ret.exp.accept(this);
			getdata(reg, ret.exp);
//			if (ret.exp.getClass() == VarExpr.class) {
//				writer.println("lw " + reg + " (" + reg + ")");
//			}
			writer.println("move " + Register.v0 + ", " + reg);

			writer.println("j " + functionname + "end");
			freeRegister(reg);
			return Register.v0;
		}
		writer.println("j " + functionname + "end");
		return null;
	}
	
	public void getdata(Register reg, Expr exp) {
		if (exp.getClass() == ArrayAccessExpr.class) {
			ArrayAccessExpr aae = (ArrayAccessExpr) exp;
			writer.println("lw " + reg + ", (" + reg + ")");
			comment("Loading array access in get data");
//			getdata(reg,aae.exp2);
		} else if (exp.getClass() == VarExpr.class && exp.type.getClass() != StructType.class) {
			writer.println("lw " + reg + " (" + reg + ")");
			comment("Loading var exp in get data");
		} else if (exp.getClass() == IntLiteral.class || exp.getClass() == StrLiteral.class || exp.getClass() == ChrLiteral.class) {
			
		}
		else if (exp.getClass() == FieldAccessExpr.class) {
			FieldAccessExpr fce = (FieldAccessExpr) exp;
			comment("Loading FieldAccessExpr exp");
			writer.println("lw " + reg + ", (" + reg + ")");
		} 
		else if (exp.getClass() == ValueAtExpr.class) {
			ValueAtExpr vae = (ValueAtExpr) exp;
			writer.println("lw " + reg + ", (" + reg + ")");
			comment("Loading Value at in get data, vae.exp.getclass() =  " + vae.exp.getClass());

//			getdata(reg, vae.exp);

			getdata(reg, vae.exp);


//			getdata(reg, vae.exp);
		} 
//		else if(exp.getClass() == ChrLiteral.class) {
//			writer.println("lb " + reg + " (" + reg + ")");
//			comment("Loading char exp ");
//		}
	}
	
	private void getdatalhs(Register reg, Expr exp) {
		if (exp.getClass() == ValueAtExpr.class) {
			ValueAtExpr vae = (ValueAtExpr) exp;
			writer.println("lw " + reg + ", (" + reg + ")");
			comment("Loading Value at in get data lhs, vae.exp.getclass() =  " + vae.exp.getClass());
			if (vae.exp.getClass() == ValueAtExpr.class) {

				getdatalhs(reg, vae.exp);
			}
			
			
		}
		
	}
	
	public void comment(String s) {
		writer.println("  #" + s);
	}
}
