package rs.ac.bg.etf.pp1;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.symboltable.*;
import rs.etf.pp1.symboltable.concepts.*;
import rs.etf.pp1.symboltable.structure.*;

public class SemanticAnalyzer extends VisitorAdaptor {
	
	private boolean errorDetected = false;
	Logger log = Logger.getLogger(getClass());
	private Obj currentProg;
	private Struct currentType;
	private int constant;
	private Struct constantType;
	private Struct boolType = Tab.find("bool").getType();
	private Obj mainMethod;
	private Obj currentMethod;
	private String methodName;
	private Struct currentEnum;
	private int currentEnumVal = 0;
	private boolean hasReturn = false;
	private List<Struct> actualParams = null;
	private List<Integer> switchCases = null;
	private int forLevel = 0;
	private int switchLevel = 0;
	int nVars; // paketsko pravo pristupa
	
	private boolean isIntOrEnum(Struct struct) {
		return (struct.equals(Tab.intType) || struct.getKind() == Struct.Class); 
	}
	
	
	private String formatStruct(Struct struct) {
	    if (struct == null) {
	        return "null";
	    }
		String type = "";

	    switch (struct.getKind()) {
	        case Struct.Int: type = "int"; break;
	        case Struct.Char: type =  "char"; break;
	        case Struct.Bool: type =  "bool"; break;
	        case Struct.Array: type =  "Arr of " + formatStruct(struct.getElemType()); break;
	        case Struct.Class: type =  "enum"; break;
	        case Struct.None: type =  "noType"; break;
	    }
	    
	    return type;
	}
	
	private String formatObj(Obj obj) {
	    if (obj == null || obj == Tab.noObj) {
	        return "noObj";
	    }

	    String kind = "";

	    switch (obj.getKind()) {
	        case Obj.Var:  kind = "Var"; break;
	        case Obj.Con:  kind = "Con"; break;
	        case Obj.Type: kind = "Type"; break;
	        case Obj.Meth: kind = "Meth"; break;
	        case Obj.Elem: kind = "Elem"; break;
	        case Obj.Prog: kind = "Prog"; break;
	    }

	    return kind + " " + obj.getName() +": " + formatStruct(obj.getType()) + ", " + obj.getAdr() + ", " + obj.getLevel();
	}
	
	/* LOG MESSAGES */


	public void report_error(String message, SyntaxNode info) {
		errorDetected  = true;
		StringBuilder msg = new StringBuilder(message);
		int line = (info == null) ? 0: info.getLine();
		if (line != 0)
			msg.append (" na liniji ").append(line);
		log.error(msg.toString());
	}

	public void report_info(String message, SyntaxNode info) {
		StringBuilder msg = new StringBuilder(message); 
		int line = (info == null) ? 0: info.getLine();
		if (line != 0)
			msg.append (" na liniji ").append(line);
		log.info(msg.toString());
	}
	
	public boolean passed() {
		return !errorDetected;
	}

	
	/* PROGRAM */

	@Override
	public void visit(ProgName progName) {
		currentProg = Tab.insert(Obj.Prog, progName.getI1(), Tab.noType);
		Tab.openScope();
	}
	
	@Override
	public void visit(Program program) {
		nVars = Tab.currentScope().getnVars(); // koliko VARijabli u globalnom scope-u
		Tab.chainLocalSymbols(currentProg);
		Tab.closeScope();
		
		if(mainMethod == null || mainMethod.getLevel() > 0) {
			report_error("Semanticka greska: program nema VOID MAIN metodu", program);
		}
		
		currentProg = null;
	}
	
	/* TYPE */
	
	@Override
	public void visit(Type type) {
		Obj typeObj = Tab.find(type.getI1());
		if(typeObj == Tab.noObj) {
			report_error("Semanticka greska: nepostojeci tip podatka " + type.getI1(), type);
			type.struct = currentType = Tab.noType;
			return;
		}
		if(typeObj.getKind() != Obj.Type) {
			report_error("Semanticka greska: neodgovarajuci tip podatka " + type.getI1(), type);
			type.struct = currentType = Tab.noType;		
			return;
		}
		type.struct = currentType = typeObj.getType();
	}
	
	/* CONST */
	
	@Override 
	public void visit(Constant_num constant_num) {
		constant = constant_num.getN1();
		constantType = Tab.intType;
	}
	
	@Override 
	public void visit(Constant_char constant_char) {
		constant = constant_char.getC1();
		constantType = Tab.charType;
	}
	
	@Override 
	public void visit(Constant_bool constant_bool) {
		constant = constant_bool.getB1();
		constantType = boolType;
	}
	
	@Override
	public void visit(ConstDeclElem constDeclElem) {
		Obj constObj = Tab.find(constDeclElem.getI1());
		if(constObj != Tab.noObj) {
			report_error("Semanticka greska: dvostruka definicija konstante " + constDeclElem.getI1(), constDeclElem);
		}
		else {
			if(constantType.assignableTo(currentType)) {
				constObj = Tab.insert(Obj.Con, constDeclElem.getI1(), currentType);
				constObj.setAdr(constant);
			}
			else {
				report_error("Semanticka greska: neodgovarajuca dodela konstanti "+ constDeclElem.getI1(), constDeclElem);
			}
		}
	}

	/* VAR */
	
	@Override
	public void visit(VarDeclElem_var varDeclElem_v) {
		Obj varObj = null;
		if(currentMethod == null) {
			varObj = Tab.find(varDeclElem_v.getI1());
			if (varObj != Tab.noObj) {
				report_error("Semanticka greska: dvostruka definicija globalne promenljive " + varDeclElem_v.getI1(), varDeclElem_v);
			}
			else {
				varObj = Tab.insert(Obj.Var, varDeclElem_v.getI1(), currentType);
			}
		}
		else {
			varObj = Tab.currentScope().findSymbol(varDeclElem_v.getI1());
			if(varObj != null ) {
				report_error("Semanticka greska:  dvostruka definicija lokalne promenljive " + varDeclElem_v.getI1(), varDeclElem_v);
			}
			else {
				varObj = Tab.insert(Obj.Var, varDeclElem_v.getI1(), currentType);
			}
		}
	}
	
	@Override
	public void visit(VarDeclElem_arr varDeclElem_arr) {
		Obj varObj = null;
		Struct arrayType = new Struct(Struct.Array, currentType);
		
		if(currentMethod == null) {
			varObj = Tab.find(varDeclElem_arr.getI1());
			if (varObj != Tab.noObj) {
				report_error("Semanticka greska: dvostruka definicija globalne promenljive " + varDeclElem_arr.getI1(), varDeclElem_arr);
			}
			else {
				varObj = Tab.insert(Obj.Var, varDeclElem_arr.getI1(), arrayType);
			}
		}
		else {
			varObj = Tab.currentScope().findSymbol(varDeclElem_arr.getI1());
			if(varObj != null ) {
				report_error("Semanticka greska: dvostruka definicija lokalne promenljive " + varDeclElem_arr.getI1(), varDeclElem_arr);
			}
			else {
				varObj = Tab.insert(Obj.Var, varDeclElem_arr.getI1(), arrayType);
			}
		}
	}
	
	/* METHOD */
	
	@Override
	public void visit(MethodName methodName_n) {
		methodName = methodName_n.getI1();
	}
	
	@Override
	public void visit(MethodRet_void methodRet_void) {
		methodRet_void.obj = currentMethod = Tab.insert(Obj.Meth, methodName, Tab.noType);
		Tab.openScope();
		if( methodName.equalsIgnoreCase("main")) {
			mainMethod = currentMethod; 
		}
	}
	
	@Override
	public void visit(MethodRet_type methodRet_type) {
		methodRet_type.obj = currentMethod = Tab.insert(Obj.Meth, methodName, currentType);
		Tab.openScope();
	}
	
	@Override
	public void visit(MethodDecl methodDecl) {
		Tab.chainLocalSymbols(currentMethod);
		Tab.closeScope();
		if(!hasReturn && currentMethod.getType() != Tab.noType) {
			report_error("Semanticka greska: funkcija nema return, " + currentMethod.getName(), methodDecl);
		}
		currentMethod = null;
		methodName = "";
		hasReturn = false;
	}
	
	/* STATEMENT */
	
	@Override
	public void visit(Statement_ret statement_ret) {
		if (currentMethod == null) {
	        report_error("Semanticka greska: return mora biti u okviru funkcije", statement_ret);
	        return;
	    }
		if (!currentMethod.getType().equals(Tab.noType)) {
	        report_error("Semanticka greska: return bez expr u ne void funkciji: " + currentMethod.getName(), statement_ret);
	    }
		hasReturn = true;
	}
	
	@Override
	public void visit(Statement_ret_expr statement_ret_expr) {
		if (currentMethod == null) {
	        report_error("Semanticka greska: return mora biti u okviru funkcije", statement_ret_expr);
	        return;
	    }
		if (!currentMethod.getType().equals(statement_ret_expr.getExpr().struct)) {
	        report_error("Semanticka greska: nevalidan return tip: " + currentMethod.getName(), statement_ret_expr);    
	    }
		hasReturn = true;
	}
    
    /* (Statement_print_num) PRINT LPAREN Expr COMMA NUMBER RPAREN SEMI */
    @Override 
    public void visit(Statement_print_num statement_print_num) {
    	Struct printType = statement_print_num.getExpr().struct;

    	if(!isIntOrEnum(printType) && !printType.equals(Tab.charType) && !printType.equals(boolType)) {
            report_error("Semanticka greska: nevalidni tip za print, num", statement_print_num);
    	}
    }
    
    /* (Statement_print) PRINT LPAREN Expr RPAREN SEMI */
    @Override 
    public void visit(Statement_print statement_print) {
    	Struct printType = statement_print.getExpr().struct;

    	if(!isIntOrEnum(printType) && !printType.equals(Tab.charType) && !printType.equals(boolType) ) {
            report_error("Semanticka greska: nevalidni tip za print", statement_print);
    	}
    	
    }
    
    /* (Statement_read) READ LPAREN Designator RPAREN SEMI  */
    public void visit(Statement_read statement_read) {
    	Obj desObj = statement_read.getDesignator().obj;
    	Struct desType = desObj.getType();
    	
    	if (desObj.getName().endsWith(".length")) {
    	    report_error("Semanticka greska: ne moze se citati niz.length", statement_read);
    	    return;
    	}

    	if(desObj.getKind() != Obj.Var && desObj.getKind() != Obj.Elem) {
    		report_error("Semanticka greska: designator u read mora oznacavati promenljivu ili element niza", statement_read);
    		return;
    	}
    	
    	if(!desType.equals(Tab.intType) && !desType.equals(Tab.charType) && !desType.equals(boolType)) {
    		report_error("Semanticka greska: designator u read mora biti tipa int, char ili bool", statement_read);
    		return;
    	}
    }
    
    /* (Statement_if_else) IF LPAREN Condition RPAREN Statement ElseStatement */
    @Override 
    public void visit(Statement_if_else statement_if_else) {
    	Struct condType = statement_if_else.getCondition().struct;
    	
    	if(!condType.equals(boolType)) {
    		report_error("Semanticka greska: uslov u if nije bool", statement_if_else);
    	}
    	
    }
    
    /* (Statement_for) FOR LPAREN OptDesStatement SEMI OptCondition SEMI OptDesStatement RPAREN Statement */
    @Override 
    public void visit(Statement_for statement_for) {
    	forLevel--;
    }
    
    /* (OptCondition_condition) Condition */
    @Override
    public void visit(OptCondition_condition optCondition_condition) {
    	forLevel++;
    }
    
    /* (OptCondition_e) //epsilon */
    @Override
    public void visit(OptCondition_e optCondition_e) {
    	forLevel++;
    }
    
    /* (Statement_break) BREAK SEMI */
    @Override
    public void visit(Statement_break statement_break) {
    	if(forLevel == 0 && switchLevel == 0) {
    		report_error("Semanticka greska: break izvan for/switch ", statement_break);
    		return;
    	}  	
    }    
    
    /* (Statement_cont) CONTINUE SEMI */
    @Override
    public void visit(Statement_cont statement_cont) {
    	if(forLevel == 0) {
    		report_error("Semanticka greska: continue izvad for petlje ", statement_cont);
    	}
    }   
    
    /* (Statement_switch) Switch LPAREN Expr RPAREN LBRACE CaseList RBRACE */
    @Override
    public void visit(Statement_switch statement_switch) {
    	Struct exprType = statement_switch.getExpr().struct;
    	if(!isIntOrEnum(exprType)) {
    		report_error("Semanticka greska: expr u switch mora da bude int ", statement_switch);
    	}
    	
    	switchLevel--;
    	switchCases = null;
    } 
    
    /* (Switch) SWITCH */
    @Override
    public void visit(Switch sw) {
    	switchLevel++;
    } 
    
    /* (Case) CASE NUMBER COLON StatementList; */
    @Override
    public void visit(Case cs) {
    	if (switchLevel == 0) {
    	    report_error("Semanticka greska: case van switch", cs);
    	}
    	if(switchCases == null) {
    		switchCases = new ArrayList<>();
    	}
    	if(switchCases.contains(cs.getCaseNumber().getN1())) {
    		report_error("Semanticka greska: case " + cs.getCaseNumber().getN1()  + " vec postoji ", cs);
    		return;
    	}
    	
    	switchCases.add(cs.getCaseNumber().getN1());
    } 
	
	/* FORM PARS */
	
    /* (FormParsElem_elem) Type IDENT */
	@Override
	public void visit(FormParsElem_elem formParsElem_elem) {
		Obj formObj = null;
		if(currentMethod == null) {
			report_error("Semanticka greska: [FormParsElem_elem] ", formParsElem_elem);
		}
		else {
			formObj = Tab.currentScope().findSymbol(formParsElem_elem.getI2());
			
			if(formObj != null ) {
				report_error("Semanticka greska: dvostruka definicija parametra " + formParsElem_elem.getI2(), formParsElem_elem);
			}
			else {
				formObj = Tab.insert(Obj.Var, formParsElem_elem.getI2(), currentType);
				int fpPos = currentMethod.getLevel() + 1;
				formObj.setFpPos(fpPos);
				currentMethod.setLevel(fpPos);
			}
		}
	}
	
	/* (FormParsElem_arr) Type IDENT LBRACKET RBRACKET; */
	@Override
	public void visit(FormParsElem_arr formParsElem_arr) {
		Obj formObj = null;
		if(currentMethod == null) {
			report_error("Semanticka greska: [FormParsElem_arr] ", formParsElem_arr);
		}
		else {
			formObj = Tab.currentScope().findSymbol(formParsElem_arr.getI2());
			
			if(formObj != null ) {
				report_error("Semanticka greska: dvostruka definicija parametra " + formParsElem_arr.getI2(), formParsElem_arr);
			}
			else {
				report_info("Pretraga na (" + formParsElem_arr.getI2() + "), nadjeno " + formatObj(formObj), formParsElem_arr);
				formObj = Tab.insert(Obj.Var, formParsElem_arr.getI2(), new Struct(Struct.Array, currentType));
				int fpPos = currentMethod.getLevel() + 1;
				formObj.setFpPos(fpPos);
				currentMethod.setLevel(fpPos);
			}
		}
	}
	
	/* ENUM */
	
	@Override
	public void visit(EnumName enumName) {
		Obj enumObj = null;
		if(currentMethod != null) {
			report_error("Semanticka greska: definisanje enuma lokalno" + enumName.getI1(), enumName);
		}
		else {
			enumObj = Tab.find(enumName.getI1());
			if(enumObj != Tab.noObj) {
				report_error("Semanticka greska: dvostruka definicija enuma " + enumName.getI1(), enumName);
			}
			else {
				currentEnum = new Struct(Struct.Class);
				enumObj = Tab.insert(Obj.Type, enumName.getI1(), currentEnum);
				Tab.openScope();
			}
		}
	} 
	
	@Override
	public void visit(EnumDecl enumDecl) {
		Tab.chainLocalSymbols(currentEnum); 
		Tab.closeScope();
		currentEnum = null;
		currentEnumVal = 0;
	}
	
	@Override
	public void visit(EnumElem_id enumElem_id) {
		Obj enumConstObj = Tab.currentScope().findSymbol(enumElem_id.getI1());
		if(enumConstObj != null ) {
			report_error("Semanticka greska: dvostruka definicija elementa enuma " + enumElem_id.getI1(), enumElem_id);
		}
		else {
			enumConstObj = Tab.insert(Obj.Con, enumElem_id.getI1(), Tab.intType);
			enumConstObj.setAdr(currentEnumVal);
			currentEnumVal++;
		}
	}
	
	@Override
	public void visit(EnumElem_id_num enumElem_id_num) {
		Obj enumConstObj = Tab.currentScope().findSymbol(enumElem_id_num.getI1());
		if(enumConstObj != null ) {
			report_error("Semanticka greska: dvostruka definicija elementa enuma " + enumElem_id_num.getI1(), enumElem_id_num);
		}
		else {
			enumConstObj = Tab.insert(Obj.Con, enumElem_id_num.getI1(), Tab.intType);
			currentEnumVal = enumElem_id_num.getN2();
			enumConstObj.setAdr(currentEnumVal);
			currentEnumVal++;
		}
	}
	
	/* DESIGNATOR */
	
	@Override
	public void visit(Designator_id designator_id) {
	    Obj obj = Tab.find(designator_id.getI1());
	    designator_id.obj = obj;
	    
	    if (obj == Tab.noObj) {
	        report_error("Semanticka greska: nepoznata promenljiva " + designator_id.getI1(), designator_id);
	        return;
	    } 
	    report_info("Pretraga na (" + designator_id.getI1() + "), nadjeno " + formatObj(obj), designator_id);
	    if (obj.getKind() == Obj.Type) {
	        report_error("Semanticka greska: type se ne moze koristiti kao vrednost " + designator_id.getI1(), designator_id);
	        designator_id.obj = Tab.noObj;
	    } 
	}
	
	@Override
	public void visit(Designator_list designator_list) {
	    Obj obj = designator_list.getDesignatorList().obj;
	    if(obj.getType().getKind() == Struct.Array) {
	    	 Obj lengthObj = new Obj(Obj.Var, obj.getName() + ".length", Tab.intType);
	    	 designator_list.obj = lengthObj;
	    	 return;
	    }
	    designator_list.obj = obj;
	}
	
	/* (DesignatorList_id_len) IDENT DOT OptIdLen */
	@Override
	public void visit(DesignatorList_id_len designatorList_id_len) {

	    String baseName = designatorList_id_len.getI1();  
	    Obj baseObj = Tab.find(baseName);
	    OptIdLen optIdLen = designatorList_id_len.getOptIdLen();
	    
	    String memberName;
	    
	    if (baseObj == Tab.noObj) {
	        report_error("Semanticka greska: nepoznat identifikator: " + baseName, designatorList_id_len);
	        designatorList_id_len.obj = Tab.noObj;
	        return;
	    }
	    report_info("Pretraga na (" + designatorList_id_len.getI1() + "), nadjeno " + formatObj(baseObj), designatorList_id_len);
	    if (optIdLen instanceof OptIdLen_id) {
	        OptIdLen_id optIdLen_id = (OptIdLen_id) optIdLen;
	        memberName = optIdLen_id.getI1(); 
	    } 
	    else if (optIdLen instanceof OptIdLen_len) {
	        memberName = "length";
	    } 
	    else {
	        report_error("Semanticka greska: nevalidan OptIdLen", designatorList_id_len);
	        designatorList_id_len.obj = Tab.noObj;
	        return;
	    }
	    
	    // length
	    if (memberName.equals("length")) {
	        if (baseObj.getType().getKind() == Struct.Array) {
	            designatorList_id_len.obj = baseObj;
	        } 
	        else {
	            report_error("Semanticka greska: " + baseName + " nije niz, nema .length polje", designatorList_id_len);
	            designatorList_id_len.obj = Tab.noObj;
	        }
	        return;
	    }
	    
	    // enum.ident
	    if (baseObj.getKind() == Obj.Type && baseObj.getType().getKind() == Struct.Class) {
	        SymbolDataStructure membersTable = baseObj.getType().getMembersTable();
	        Obj enumField = membersTable.searchKey(memberName);
	        
	        if (enumField != null) {
	            designatorList_id_len.obj = enumField;
	        } 
	        else {
	            report_error("Semanticka greska: enum " + baseName + " nema polje " + memberName, designatorList_id_len);
	            designatorList_id_len.obj = Tab.noObj;
	        }
	        return;
	    }
	    
	    designatorList_id_len.obj = Tab.noObj;
	}
	
	
	@Override
	public void visit(DesignatorArrName designatorArrName) {
	    String name = designatorArrName.getI1();
	    Obj obj = Tab.find(name);
	    designatorArrName.obj = obj;
	    
	    if(obj == Tab.noObj) {
	        report_error("Semanticka greska: nepoznata promenljiva " + name, designatorArrName);
	        return;
	    }
	    report_info("Pretraga na (" + designatorArrName.getI1() + "), nadjeno " + formatObj(obj), designatorArrName);
	    if(obj.getKind() != Obj.Var  && obj.getType().getKind() != Struct.Array) {
	    	 report_error("Semanticka greska: neodgovarajuca promenljiva " + name, designatorArrName);   	
	    	 designatorArrName.obj = Tab.noObj;
	    }
	}
		

	/* (DesignatorList_expr) DesignatorArrName LBRACKET Expr RBRACKET */
	@Override
	public void visit(DesignatorList_expr designatorList_expr) {
	    
	    Obj arrObj = designatorList_expr.getDesignatorArrName().obj;

	    if (arrObj == Tab.noObj) {
	        designatorList_expr.obj = Tab.noObj;
	        return;
	    }
	    
	    if (arrObj.getType().getKind() != Struct.Array) {
	        designatorList_expr.obj = Tab.noObj;
	        return;
	    }
	    
	    if(!isIntOrEnum(designatorList_expr.getExpr().struct)) {
	    	report_error("Semanticka greska: niz nije indeksiran sa int", designatorList_expr);
	    	designatorList_expr.obj = Tab.noObj;
	    	return;
	    }
	 
	    designatorList_expr.obj = new Obj(Obj.Elem, arrObj.getName() + "[index]", arrObj.getType().getElemType());;
	}

	
	/* (DesignatorStatement_expr) Designator Assignop Expr */
	@Override
	public void visit(DesignatorStatement_expr designatorStatement_expr) {
	    Obj leftObj = designatorStatement_expr.getDesignator().obj;
	    Struct rightType = designatorStatement_expr.getExpr().struct;
	    
	    if (leftObj == Tab.noObj) {
	        report_error("Semanticka greska: nepoznata promenljiva na levoj strani", designatorStatement_expr);
	        return;
	    }
	    
	    if (leftObj.getName().endsWith(".length")) {
	        report_error("Semanticka greska: .length je samo za čitanje", designatorStatement_expr);
	        return;
	    }
	    
	    if (leftObj.getKind() == Obj.Con) {
	        report_error("Semanticka greska: ne moze se dodeliti (enum)konstanti: " + leftObj.getName(), designatorStatement_expr); 
	        return;
	    }

	    
	    if (leftObj.getKind() != Obj.Var && leftObj.getKind() != Obj.Elem) {
	        report_error("Semanticka greska: leva strana mora biti promenljiva ili element niza", designatorStatement_expr);
	        return;
	    }
	    
	    
	    if (!(rightType.assignableTo(leftObj.getType()) || (isIntOrEnum(leftObj.getType()) && isIntOrEnum(rightType)))) {
	    	    report_error("Semanticka greska: tipovi nisu kompatibilni", designatorStatement_expr);
	    	}

	}
	
	/* (DesignatorStatement_inc) Designator INC */
	@Override
	public void visit(DesignatorStatement_inc designatorStatement_inc) {
	    Obj leftObj = designatorStatement_inc.getDesignator().obj;
	    
	    if (leftObj == Tab.noObj) {
	    	 report_error("Semanticka greska: nepoznat designator u inc  ", designatorStatement_inc);
	        return;
	    }
	    if (leftObj.getKind() != Obj.Var && leftObj.getKind() != Obj.Elem) {
	        report_error("Semanticka greska: inkrement neadekvatne promenljive ", designatorStatement_inc);
	        return;
	    }
	    
	    if (leftObj.getName().endsWith(".length")) {
	        report_error("Semanticka greska: ne moze se inkrementirati .length", designatorStatement_inc);
	        return;
	    }
	    
	    if (!leftObj.getType().equals(Tab.intType)) {
	        report_error("Semanticka greska: inkrement podrzan samo za int", designatorStatement_inc);
	        return;
	    }

	}
	
	/* (DesignatorStatement_dec) Designator DEC */
	@Override
	public void visit(DesignatorStatement_dec designatorStatement_dec) {
	    Obj leftObj = designatorStatement_dec.getDesignator().obj;
	    
	    if (leftObj == Tab.noObj) {
	    	 report_error("Semanticka greska: nepoznat designator u dec  ", designatorStatement_dec);
	        return;
	    }
	    if (leftObj.getKind() != Obj.Var && leftObj.getKind() != Obj.Elem) {
	        report_error("Semanticka greska: dekrement neadekvatne promenljive ", designatorStatement_dec);
	        return;
	    }
	    
	    if (leftObj.getName().endsWith(".length")) {
	        report_error("Semanticka greska: ne moze se dekrementirati .length", designatorStatement_dec);
	        return;
	    }
	    
	    if (!leftObj.getType().equals(Tab.intType)) {
	        report_error("Semanticka greska: dekrement podrzan samo za int", designatorStatement_dec);
	        return;
	    }
	}
	
	/* (DesignatorStatement_act) Designator LPAREN ActPars RPAREN */
	@Override
	public void visit(DesignatorStatement_act designatorStatement_act) {
		Obj methObj = designatorStatement_act.getDesignator().obj;
		
		if(methObj == Tab.noObj || methObj.getKind() != Obj.Meth) {
			report_error("Semanticka greska: poziv nije metoda", designatorStatement_act);
			actualParams = null;
			return;
		}
		if (actualParams == null) {
		     actualParams = new ArrayList<>();
		}
	    if (methObj.getLevel() != actualParams.size()) {
	        report_error("Semanticka greska: pogresan broj argumenata", designatorStatement_act);
	        actualParams = null;
	        return;
	    }
	    int i = 0;
	    for (Obj fp : methObj.getLocalSymbols()) {
	        if (fp.getFpPos() > 0) {
	            Struct actType = actualParams.get(i);

	            if (!actType.assignableTo(fp.getType())) {
	                report_error("Semanticka greska: neodgovarajuci tip argumenta",
	                             designatorStatement_act);
	            }
	            i++;
	        }
	    }
	    
	    actualParams = null;
		
	}
	
	/* ACT PARS */
	
	/*  (OptActPars_act) ActPars; */
	@Override public void visit(OptActPars_act optActPars_act) {
		
	}
	
	/* (ActPars) ExprList; */
	@Override public void visit(ExprList exprList) {}
	
	/* (ExprList_list) ExprList COMMA Expr */
	@Override public void visit(ExprList_list exprList_list) {
	    if (actualParams == null) {
	        actualParams = new ArrayList<>();
	    }
		actualParams.add(exprList_list.getExpr().struct);
	}
	
	/* (ExprList_elem) Expr */
	@Override public void visit(ExprList_elem exprList_elem) {
		
	    if (actualParams == null) {
	        actualParams = new ArrayList<>();
	    }
		actualParams.add(exprList_elem.getExpr().struct);
	}

	/* FACTOR */
	
	/* (Factor) Unary FactorMore; */
	@Override
	public void visit(Factor factor) {
		if((factor.getUnary() instanceof Unary_minus) && (!isIntOrEnum(factor.getFactorMore().struct))) {
			report_error("Semanticka greska: minus mora da bude ispred tipa int", factor);
			factor.struct = Tab.noType;
			return;
		}
	    factor.struct = factor.getFactorMore().struct;
	}
	
	/* (FactorMore_num) NUMBER */
	@Override
	public void visit(FactorMore_num factor_num) {
	    factor_num.struct = Tab.intType;
	}

	/* (FactorMore_char) CHARACTER */
	@Override
	public void visit(FactorMore_char factor_char) {
	    factor_char.struct = Tab.charType;
	}

	/* (FactorMore_bool) BOOL */
	@Override
	public void visit(FactorMore_bool factor_bool) {
	    factor_bool.struct = boolType;
	}
	
	/* (FactorMore_new) NEW Type LBRACKET Expr RBRACKET */
	@Override
	public void visit(FactorMore_new factorMore_new) {
		if(!factorMore_new.getExpr().struct.equals(Tab.intType)) {
			report_error("Semanticka greska: kreiranje niza sa ne int vrednosti", factorMore_new);
			factorMore_new.struct = Tab.noType;
			return;
		}
		Struct arrayType = new Struct(Struct.Array, currentType);
	    factorMore_new.struct = arrayType;
	}
	
	/* (FactorMore_meth) Designator LPAREN OptActPars RPAREN */
	@Override
	public void visit(FactorMore_meth factorMore_meth) {
	    Obj methodObj = factorMore_meth.getDesignator().obj;
	
	    if (methodObj == Tab.noObj || methodObj.getKind() != Obj.Meth) {
	        report_error("Semanticka greska: poziv nije metoda ", factorMore_meth);
	        factorMore_meth.struct = Tab.noType;
	        return;
	    }
	
	    if (actualParams == null) {
	        actualParams = new ArrayList<>();
	    }
	
	    if (methodObj.getLevel() != actualParams.size()) {
	        report_error("Semanticka greska: pogresan broj argumenata", factorMore_meth);
	        actualParams = null;
	        return;
	    }
	
	    int i = 0;
	    for (Obj fp : methodObj.getLocalSymbols()) {
	        if (fp.getFpPos() > 0) {
	            Struct actType = actualParams.get(i);
	            if (!actType.assignableTo(fp.getType())) {
	                report_error("Neodgovarajuci tip argumenta", factorMore_meth);
	            }
	            i++;
	        }
	    }
	
	    if (methodObj.getType() == Tab.noType) {
	        report_error("Semanticka greska: void metoda u izrazu", factorMore_meth);
	    }
	
	    factorMore_meth.struct = methodObj.getType();
	    actualParams = null;
	}

	
	/* 	(FactorMore_expr) LPAREN Expr RPAREN */
	@Override
	public void visit(FactorMore_expr factorMore_expr) {
	    factorMore_expr.struct = factorMore_expr.getExpr().struct;
	}
	
	/* (FactorMore_var) Designator */
    @Override
    public void visit(FactorMore_var factorMore_var) {
        Obj obj = factorMore_var.getDesignator().obj;
        if(obj != Tab.noObj) {
            factorMore_var.struct = obj.getType();
        } 
        else {
            factorMore_var.struct = Tab.noType;
        }
    }
    
    /* CONDITION */
    
    /* (CondFact_expr) ExprNonTernary */
    @Override
    public void visit(CondFact_expr condFact_expr) {
    	 if (!condFact_expr.getExprNonTernary().struct.equals(boolType)) {
    	        report_error("Semanticka greska: uslov mora biti bool", condFact_expr);
    	        condFact_expr.struct = Tab.noType;
    	    } 
    	 else {
    	        condFact_expr.struct = boolType;
    	    }
    }
    
    /* (CondFact_relop) ExprNonTernary Relop ExprNonTernary; */
    @Override
    public void visit(CondFact_relop condFact_relop) {
        Struct left = condFact_relop.getExprNonTernary().struct;
        Struct right = condFact_relop.getExprNonTernary1().struct;

        if (!left.equals(right)) {
            report_error("Semanticka greska: nekompatibilni tipovi u relaciji", condFact_relop);
            condFact_relop.struct = Tab.noType;
        } else {
            condFact_relop.struct = boolType;
        }
    }
    
    /* (CondTerm) CondFactList */
    @Override
    public void visit(CondTerm condTerm) {
    	condTerm.struct = condTerm.getCondFactList().struct;
    }
    
    /* (CondFactList_fact) CondFact */
    @Override
    public void visit(CondFactList_fact condFactList_fact) {
    	condFactList_fact.struct = condFactList_fact.getCondFact().struct;
    }
    
    /* (CondFactList_and_fact) CondFactList AND CondFact */
    @Override
    public void visit(CondFactList_and_fact condFactList_and_fact) {
        if (!condFactList_and_fact.getCondFactList().struct.equals(boolType) || !condFactList_and_fact.getCondFact().struct.equals(boolType)) {
            report_error("Semanticka greska: AND zahteva bool operande", condFactList_and_fact);
            condFactList_and_fact.struct = Tab.noType;
        } 
        else {
        	condFactList_and_fact.struct = boolType;
        }
    }
    
    /* (Condition) CondTermList */
    @Override
    public void visit(Condition condition) {
    	condition.struct = condition.getCondTermList().struct;
    }
    
    /* (CondTermList_term) CondTerm */
    @Override
    public void visit(CondTermList_term condTermList_term) {
    	condTermList_term.struct = condTermList_term.getCondTerm().struct;
    }

    /* (CondTermList_or_term) CondTermList OR CondTerm */
    @Override
    public void visit(CondTermList_or_term condTermList_or_term) {
    	if (!condTermList_or_term.getCondTermList().struct.equals(boolType) || !condTermList_or_term.getCondTerm().struct.equals(boolType)) {
            report_error("Semanticka greska: OR zahteva bool operande", condTermList_or_term);
            condTermList_or_term.struct = Tab.noType;
        } 
        else {
        	condTermList_or_term.struct = boolType;
        }
    }

    
    /* EXPR */
    
    /* (Expr_ternary) ExprTernary */ 
    @Override
    public void visit(Expr_ternary expr_ternary) {
    	expr_ternary.struct = expr_ternary.getExprTernary().struct;
    } 
    
    /* (Expr_regular) ExprNonTernary */ 
    @Override
    public void visit(Expr_regular expr_regular) {
        expr_regular.struct = expr_regular.getExprNonTernary().struct;
    }
  
    /* (ExprTernary) Condition QUEST Expr COLON Expr;	 */ 
    @Override
    public void visit(ExprTernary exprTernary) {
    	Struct thenType = exprTernary.getExpr().struct;
    	Struct elseType = exprTernary.getExpr1().struct;
    	
    	if(exprTernary.getCondition().struct != boolType) {
    		 report_error("Semanticka greska: uslov u ternarnom operatoru mora biti bool", exprTernary);
    		 exprTernary.struct = Tab.noType;
    		 return;
    	}
    	
    	if(!(thenType.equals(elseType) || (isIntOrEnum(thenType) && isIntOrEnum(elseType)))) {
    		report_error("Semanticka greska: tipovi u ternarnom operatoru nisu kompatibilni", exprTernary);
    		exprTernary.struct = Tab.noType;
    		return;
    	}
    	
    	exprTernary.struct = thenType;
    }
    
    /* (ExprNonTernary) AddopTermList; */ 
    @Override
    public void visit(ExprNonTernary exprNonTernary) {
        exprNonTernary.struct = exprNonTernary.getAddopTermList().struct;
    }
    
    /* (AddopTermList_term) Term */
    @Override
    public void visit(AddopTermList_term addopTermList_term) {
        addopTermList_term.struct = addopTermList_term.getTerm().struct;
    }
    
    /* (AddopTermList_addop) AddopTermList Addop Term */
    @Override
    public void visit(AddopTermList_addop addopTermList_addop) {
        Struct leftType = addopTermList_addop.getAddopTermList().struct;
        Struct rightType = addopTermList_addop.getTerm().struct;
        
        if (isIntOrEnum(leftType) && isIntOrEnum(rightType)) {
            addopTermList_addop.struct = Tab.intType; 
        } 
        else {
            report_error("Semanticka greska: nevalidni tipovi za addop ", addopTermList_addop);
            addopTermList_addop.struct = Tab.noType;
        }
    }
    
    /* (Term) MulopFactorList; */
    @Override
    public void visit(Term term) {
        term.struct = term.getMulopFactorList().struct;
    }
    
    /* (MulopFactorList_factor) Factor */
    @Override
    public void visit(MulopFactorList_factor mulopFactorList_factor) {
        mulopFactorList_factor.struct = mulopFactorList_factor.getFactor().struct;
    }
    
    /* (MulopFactorList_mul) MulopFactorList Mulop Factor; */
    @Override
    public void visit(MulopFactorList_mul mulopFactorList_mul) {
        Struct leftType = mulopFactorList_mul.getMulopFactorList().struct;
        Struct rightType = mulopFactorList_mul.getFactor().struct;
        
        if (isIntOrEnum(leftType) && isIntOrEnum(rightType)) {
            mulopFactorList_mul.struct = Tab.intType;
        } 
        else {
            report_error("Semanticka greska: nevalidni tipovi za mulop operaciju ", mulopFactorList_mul);
            mulopFactorList_mul.struct = Tab.noType;
        }
    }
    

}