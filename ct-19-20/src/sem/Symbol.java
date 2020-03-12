package sem;

public abstract class Symbol {
	public String name;
	
	
//	public Symbol(String name) {
//		this.name = name;
//	}
	boolean isVar() {
		return this.getClass().equals(VarSymbol.class);
	}
	boolean isFun() {
		return this.getClass().equals(FunSymbol.class);
	}
	
}
