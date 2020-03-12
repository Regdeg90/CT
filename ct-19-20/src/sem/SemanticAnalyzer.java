package sem;

import java.util.ArrayList;

import ast.DotPrinter;

public class SemanticAnalyzer {
	
	public int analyze(ast.Program prog) {
		// List of visitors
		ArrayList<SemanticVisitor> visitors = new ArrayList<SemanticVisitor>() {{
			add(new NameAnalysisVisitor());
			add(new StructCheckVisitor());
			add(new TypeCheckVisitor());
//			add(new DotPrinter());
		}};
		// Error accumulator
		int errors = 0;
		
		// Apply each visitor to the AST
		for (SemanticVisitor v : visitors) {
			prog.accept(v);
			errors += v.getErrorCount();
		}
		
		// Return the number of errors.
		return errors;
	}
}
