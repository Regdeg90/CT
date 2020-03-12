package sem;

import java.util.HashMap;
import java.util.Map;

public class Scope {
	private Scope outer;
	private Map<String, Symbol> symbolTable;
	
	public Scope(Scope outer) { 
		this.outer = outer; 
		this.symbolTable = new HashMap<String, Symbol>();
	}
	
	public Scope() { 
		this.outer = null;
		this.symbolTable = new HashMap<String, Symbol>();
		}
	
	public Symbol lookup(String name) {
		Symbol s = symbolTable.get(name);
		
		if (s != null) {
			return s;
		}
		
		if (outer != null) {
			return outer.lookup(name);
		}
		
		return null;
	}
	
	public Symbol lookupCurrent(String name) {
		Symbol s = symbolTable.get(name);
		
		if (s != null) {
			return s;
		}
		
		return null;
	}
	
	public void put(Symbol sym) {
		symbolTable.put(sym.name, sym);
	}
}
