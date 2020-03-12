package ast;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import sem.BaseSemanticVisitor;

public class DotPrinter extends BaseSemanticVisitor<String>  {
	
	
	FileWriter write;
    PrintWriter print_line;
    int nodecount;
    
	@Override
	public String visitBaseType(BaseType bt) {
		nodecount++;
		print_line.println(bt.toString()+ nodecount + "[ label = \" "+ bt.toString() +"\"];");
		return bt.toString()+ nodecount;
	}

	@Override
	public String visitStructTypeDecl(StructTypeDecl st) {
		nodecount++;
		
		String stname = st.structType.accept(this);
		
		for (VarDecl vd : st.varDecls) {
			String vardec = vd.accept(this);
			print_line.println(stname + "->" + vardec);
			
		}
		
		return stname;
	}

	@Override
	public String visitBlock(Block b) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitFunDecl(FunDecl p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitProgram(Program p){
    	try {
			this.write = new FileWriter("Dot_Printer.dot");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	this.print_line = new PrintWriter(write);
    	this.nodecount = 0;
		print_line.println("digraph ast{");
		
		print_line.println("Program [ label = \"Program \"];");
		print_line.println("StructDecls [ label = \"StructDecls \"];");
		print_line.println("Program -> StructDecls;");
		for (StructTypeDecl std : p.structTypeDecls) {
			String struct = std.accept(this);
			print_line.println("StructDecls -> " + struct + ";");
		}
//		
//		print_line.println("Program -> VarDecls;");
		
		
		
		
		
		print_line.println("}");
		print_line.close();
		return null;
	}

	@Override
	public String visitVarDecl(VarDecl vd) {
		nodecount++;
		
		print_line.println(vd.varName + nodecount + "[ label = \"Variable("+ vd.varName + ")\"];");
		String type = vd.type.accept(this);
		print_line.println(vd.varName + nodecount + "->" + type);
		
		
		return vd.varName + nodecount;
	}

	@Override
	public String visitVarExpr(VarExpr v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitStructType(StructType st) {
		nodecount++;
		print_line.println(st.stName + nodecount + "[ label = \" Struct("+ st.stName +")\"];");
		return st.stName + nodecount;
	}

	@Override
	public String visitPointerType(PointerType pt) {
		nodecount++;
		print_line.println("PointerType" + nodecount + "[ label = \" PointerType\"];");
		String type = pt.type.accept(this);
		print_line.println("PointerType" + nodecount + "->" + type);
		return "PointerType" + nodecount;
	}

	@Override
	public String visitArrayType(ArrayType at) {
		nodecount++;
		print_line.println("ArrayType" + nodecount + "[ label = \" ArrayType\"];");
		String type = at.type.accept(this);
		print_line.println("ArrayType" + nodecount + "->" + type);
		return "ArrayType" + nodecount;
	}

	@Override
	public String visitIntLiteral(IntLiteral i) {
		nodecount++;
		print_line.println("IntLiteral" + nodecount + "[ label = \" IntLiteral( " + i.i + ")\"];");
		return "IntLiteral" + nodecount;
	}

	@Override
	public String visitStrLiteral(StrLiteral str) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitChrLiteral(ChrLiteral ch) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitFunCallExpr(FunCallExpr fc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitBinOp(BinOp b) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitOp(Op op) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitArrayAccessExpr(ArrayAccessExpr aae) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitFieldAccessExpr(FieldAccessExpr fae) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitValueAtExpr(ValueAtExpr vae) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitSizeOfExpr(SizeOfExpr soe) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitTypecastExpr(TypecastExpr tce) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitExprStmt(ExprStmt es) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitWhile(While w) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitIf(If i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitAssign(Assign ass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String visitReturn(Return ret) {
		// TODO Auto-generated method stub
		return null;
	}

}
