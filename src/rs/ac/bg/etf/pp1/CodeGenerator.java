package rs.ac.bg.etf.pp1;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;

public class CodeGenerator extends VisitorAdaptor {
	
	private int mainPc;
	public int getMainPc() { return this.mainPc; }
	
	
	/* METHOD */
	
	@Override
	public void visit(MethodRet_void methodRet_void) {
		// pocetna adresa metode, pre enter
		methodRet_void.obj.setAdr(Code.pc);
		
		if(methodRet_void.getMethodName().getI1().equalsIgnoreCase("main")) {
			this.mainPc = Code.pc;
		}
		
		// enter b1, b2
		Code.put(Code.enter); 
		// b1 form par
		Code.put(methodRet_void.obj.getLevel()); 
		// b2 locals (form pars + lokalne)
		Code.put(methodRet_void.obj.getLocalSymbols().size());
	}
	
	@Override
	public void visit(MethodRet_type methodRet_type) {
		methodRet_type.obj.setAdr(Code.pc);
		
		Code.put(Code.enter); 
		Code.put(methodRet_type.obj.getLevel()); 
		Code.put(methodRet_type.obj.getLocalSymbols().size());
	}
	
	@Override
	public void visit(MethodDecl methodDecl) {
		// brise aktivacioni zapis
		Code.put(Code.exit);
		// vraca povratnu adresu
		Code.put(Code.return_);
	}
	
	/* FACTOR - uvek sa desne strane - mora da se nalazi na expr steku */
	
	@Override
	public void visit(FactorMore_num factorMore_num) {
		Code.loadConst(factorMore_num.getN1());
	}
	
	@Override
	public void visit(FactorMore_char factorMore_char) {
		Code.loadConst(factorMore_char.getC1());
	}
	
	@Override
	public void visit(FactorMore_bool factorMore_bool) {
		Code.loadConst(factorMore_bool.getB1());
	}
	
	@Override
	public void visit(FactorMore_var factorMore_var) {
		Obj obj = factorMore_var.getDesignator().obj;
		String objName = obj.getName();
		if(objName.endsWith(".length")){
			return;
		}
		Code.load(obj);
	}
	
	@Override
	public void visit(FactorMore_new factorMore_new) {
		// newarray b (0-char 1-ostalo)
		// ocekuje na steku duzinu
		// vraca adr sa heap gde je aloc niz
		Code.put(Code.newarray);
		if(factorMore_new.getType().struct.equals(Tab.charType)) {
			Code.put(0);
		}
		else {
			Code.put(1);
		}
	}
	
	@Override
	public void visit(FactorMore_meth factorMore_meth) {
		Obj funcObj = factorMore_meth.getDesignator().obj;
		if (funcObj == Tab.find("ord") || funcObj == Tab.find("chr") || funcObj == Tab.find("len")) {
            return;
        }
        
		int offset = funcObj.getAdr() - Code.pc;
		Code.put(Code.call);
		Code.put2(offset);
	}
	
	@Override
	public void visit(Factor factor) {
		// ako se desi minus negiram ono sto je na vrhu steka
		if(factor.getUnary() instanceof Unary_minus) {
			Code.put(Code.neg); 
		}
	}
	
	/* DESIGNATOR */ 
	
	@Override
	public void visit(DesignatorList_id_len designatorList_id_len) {
		// ne stavljam nista na stek u factor nego ovde
		if(designatorList_id_len.getOptIdLen() instanceof OptIdLen_len) {
			Code.load(designatorList_id_len.obj);
			Code.put(Code.arraylength);
		}
		
	}
	
	@Override
	public void visit(DesignatorArrName designatorArrName) {
		// neophodno i sa leve i sa desne strane, treba nam adr i za load i store
		// za load elem niza adr
		Code.load(designatorArrName.obj);
	}
	
	@Override
	public void visit(DesignatorStatement_expr designatorStatement_expr) {
		// sa steka u designator sa leve strane
		Code.store(designatorStatement_expr.getDesignator().obj);
	}
	
	@Override
	public void visit(DesignatorStatement_inc designatorStatement_inc) {
		Obj desObj = designatorStatement_inc.getDesignator().obj;
		if(desObj.getKind() == Obj.Elem) {
			Code.put(Code.dup2);
		}
		Code.load(desObj);
		Code.loadConst(1);
		Code.put(Code.add);
		Code.store(desObj);
	}
	
	@Override
	public void visit(DesignatorStatement_dec designatorStatement_dec) {
		Obj desObj = designatorStatement_dec.getDesignator().obj;
		// ako je elem niza treba nam adr i indeks
		if(desObj.getKind() == Obj.Elem) {
			Code.put(Code.dup2);
		}
		Code.load(desObj);
		Code.loadConst(1);
		Code.put(Code.sub);
		Code.store(desObj);
	}
	
	@Override
	public void visit(DesignatorStatement_paren designatorStatement_paren) {
		Obj methObj =  designatorStatement_paren.getDesignator().obj;
		int offset = methObj.getAdr() - Code.pc;
		// call s
		Code.put(Code.call);
		// s offset
		Code.put2(offset);
		
		if(!methObj.getType().equals(Tab.noType)) {
			// ako nije void metoda da ne ostane return na steku
			Code.put(Code.pop);
		}
		
	}
	
	@Override
	public void visit(DesignatorStatement_act designatorStatement_act) {
		Obj methObj =  designatorStatement_act.getDesignator().obj;
		int offset = methObj.getAdr() - Code.pc;
		Code.put(Code.call);
		Code.put2(offset);
		
		if(!methObj.getType().equals(Tab.noType)) {
			Code.put(Code.pop);
		}
		// actpars su lista expr, automatski ce se staviiti na stek
	}
	
	/* EXPR */
	
	@Override
	public void visit(Expr_ternary expr_ternary) {
		
	}
	
	@Override
	public void visit(AddopTermList_addop addopTermList_addop) {
		if(addopTermList_addop.getAddop() instanceof Addop_plus) {
			Code.put(Code.add);
			return;
		}
		if(addopTermList_addop.getAddop() instanceof Addop_minus) {
			Code.put(Code.sub);
			return;
		}
	}
	
	@Override
	public  void visit(MulopFactorList_mul mulopFactorList_mul) {
		if(mulopFactorList_mul.getMulop() instanceof Mulop_mul) {
			Code.put(Code.mul);
			return;
		}
		if(mulopFactorList_mul.getMulop() instanceof Mulop_div) {
			Code.put(Code.div);
			return;
		}
		if(mulopFactorList_mul.getMulop() instanceof Mulop_rem) {
			Code.put(Code.rem);
			return;
		}
	}
	
	/* CONDITION */
	
	private List<Integer> skipCondFact = new ArrayList<>(); // sledeci or da trazi tacan ili else
	private List<Integer> skipCondition = new ArrayList<>(); // idi u then
	private Stack<List<Integer>> toElseStack = new Stack<>();
	private List<Integer> afterElse = new ArrayList<>();
	
	private int getRelop(Relop relop) {
		if(relop instanceof Relop_eq) {
			return Code.eq;
		} 
		else if(relop instanceof Relop_neq) {
			return Code.ne;
		} 
		else if(relop instanceof Relop_gt) {
			return Code.gt;
		} 
		else if(relop instanceof Relop_gte) {
			return Code.ge;
		} 
		else if(relop instanceof Relop_lt) {
			return Code.lt;
		} 
		else{
			return Code.le;
		}
	}
	
	@Override
	public void visit(CondFact_relop condFact_relop) {
		// skacem ako nije zadovoljen relop
		Code.putFalseJump(getRelop(condFact_relop.getRelop()), 0);
		skipCondFact.add(Code.pc - 2);
	}

	
	@Override
	public void visit(CondFact_expr condFact_expr) {
		// jedna stvar na steku, za uslov potrebne dve
		// jos jedna promenljiva sa kojom cu da poredim
		Code.loadConst(1);
		// nq 1 znaci skoci negde do or, ceo and netacan
		// fixup cu adresu sledeceg or
		Code.putFalseJump(Code.eq, 0);
		// cuvam adr u kojoj se desio netacan uslov
		skipCondFact.add(Code.pc - 2);
		
		// ako nismo skocili, tacna
	}
	
	@Override
	public void visit(CondTerm condTerm) {
		// prosli svi add, izmedju dva or
		// ako nismo uradili skok jer je neka false stigli smo do ovde
		// tacan je or i ide na then
		Code.putJump(0);
		skipCondition.add(Code.pc - 2);
		// netacne skacu ovde 
		while(!skipCondFact.isEmpty()) {
			Code.fixup(skipCondFact.removeLast());
		}
	}
	
	@Override
	public void visit(Condition condition) {
		// netacne bezuslovno na else
		// desice se jednom postoji samo jedan condition 
		Code.putJump(0); 
		// treba da fixup na else
		toElseStack.push(new ArrayList<>());
		toElseStack.peek().add(Code.pc - 2);
		// sve tacne fixup na then
		while(!skipCondition.isEmpty()){
			Code.fixup(skipCondition.removeLast());
		}
	}
	
	@Override 
	public void visit(ElseStatement_e elseStatement_e) {
		// neispunjen uslov ovde skaci
		List<Integer> toElse = toElseStack.pop();
		Code.fixup(toElse.removeLast());
	}
	
	@Override 
	public void visit(Else else_) {
		// tacne izbacimo treba da oreskoce statement, netacne vratimo
		Code.putJump(0);
		afterElse.add(Code.pc - 2);
		// netacne fixup da skoce ovde
		List<Integer> toElse = toElseStack.pop();
		Code.fixup(toElse.removeLast());
	}
	
	@Override 
	public void visit(ElseStatement_else elseStatement_else) {
		// else se zavrsi
		Code.fixup(afterElse.removeLast());
	}

	@Override
	public void visit(Colon colon) {
		// tacne preskoce 
		Code.putJump(0);
		afterElse.add(Code.pc - 2);
		
		List<Integer> toElse = toElseStack.pop();
		Code.fixup(toElse.removeLast());
	}
	
	@Override
	public void visit(ExprTernary exprTernary) {
		Code.fixup(afterElse.removeLast());
	}
	
	private Stack<Integer> condStart = new Stack<>();
	private Stack<Integer> updateStart = new Stack<>();
	private Stack<Integer> bodyStart = new Stack<>();
	private Stack<List<Integer>> breakStack = new Stack<>();

	private int semiCnt = 0;

	@Override
	public void visit(Semi semi) {
		semiCnt++;
		if(semiCnt % 2 == 1) {
			condStart.push(Code.pc);
			breakStack.push(new ArrayList<>());
		}
		if(semiCnt % 2 == 0) {
			Code.putJump(0);
			bodyStart.push(Code.pc - 2);
			updateStart.push(Code.pc);
		}
	}
	
	@Override
	public void visit(OptDesStatement_des optDesStatement_des) {
		if(semiCnt % 2 == 0 && semiCnt > 0 && !condStart.isEmpty()) {
			Code.putJump(condStart.pop());
			}
		if(!bodyStart.isEmpty()) {
			Code.fixup(bodyStart.pop());
		}
		
	}
	
	@Override
	public void visit(OptDesStatement_e optDesStatement_e) {
		if(semiCnt % 2 == 0 && semiCnt > 0 && !condStart.isEmpty()) {
			Code.putJump(condStart.pop());
			}
		if(!bodyStart.isEmpty()) {
			Code.fixup(bodyStart.pop());
		}
		
	}

	/* STATEMENT */
	
	
	@Override
	public void visit(Statement_for statement_for) {
	    // posle body na update
	    Code.putJump(updateStart.pop());

	    semiCnt = 0;

	    // kraj 
	    if(!toElseStack.isEmpty()) {
		    List<Integer> toElse = toElseStack.pop();
		    while(!toElse.isEmpty()) {
		    	Code.fixup(toElse.removeLast());
		    }
	    }
	    
	    if(!breakStack.isEmpty()) {
	    	List<Integer> break_ = breakStack.pop();
		    while(!break_.isEmpty()) {
		    	Code.fixup(break_.removeLast());
		    }
	    }

	}

	@Override
	public void visit(Statement_break statement_break) {
		Code.putJump(0);
		breakStack.peek().add(Code.pc - 2);
	}
	

	@Override
	public void visit(Statement_cont statement_cont) {
		if(!nextCase.isEmpty()) {
			Code.put(Code.pop);
		}
		Code.putJump(updateStart.peek());

	}
	
	@Override
	public void visit(Statement_print statement_print) {
		// width za print 
		Code.loadConst(0);
		if(statement_print.getExpr().struct.equals(Tab.charType)) {
			Code.put(Code.bprint);
		}
		else {
			Code.put(Code.print);
		}	
	}
	
	@Override
	public void visit(Statement_print_num statement_print_num) {
		int num = statement_print_num.getN2();
		Code.loadConst(num);
		if(statement_print_num.getExpr().struct.equals(Tab.charType)) {
			Code.put(Code.bprint);
		}
		else {
			Code.put(Code.print);
		}	
	}
	
	@Override
	public void visit(Statement_ret statement_ret) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}
	
	@Override
	public void visit(Statement_ret_expr statement_ret_expr) {
		// ostavi expr na steku
		
		Code.put(Code.exit);
		Code.put(Code.return_);
	}
	
	@Override
	public void visit(Statement_read statement_read) {
		//ostavi na steku procitanu val
		Obj desObj = statement_read.getDesignator().obj;
		if(desObj.getType().equals(Tab.charType)) {
			Code.put(Code.bread);
		}
		else {
			Code.put(Code.read);
		}
		Code.store(desObj);
	}
	
	private Stack<Integer> nextCase = new Stack<>();
	private Stack<Integer> skipCaseCond = new Stack<>();

	@Override
	public void visit(Switch switch_) {
		breakStack.push(new ArrayList<>());
	}

	@Override
	public void visit(CaseNumber caseNumber) {
		Code.put(Code.dup);
		
		Code.loadConst(caseNumber.getN1());
		Code.putFalseJump(Code.eq, 0);

		nextCase.push(Code.pc-2);
	}
	
	@Override
	public void visit(ColonCase colonCase) {
		if(!skipCaseCond.isEmpty()) {
			Code.fixup(skipCaseCond.pop());
		}
	
	}
	
	@Override
	public void visit(Case case_) {
		Code.putJump(0);
		skipCaseCond.add(Code.pc-2);
	}
	
	@Override
	public void visit(CaseSym caseSym) {
		
		if(!nextCase.isEmpty()) {
			Code.fixup(nextCase.pop());
		}
	
	}
	
	@Override
	public void visit(Statement_switch statement_switch) {
		if(!nextCase.isEmpty()) {
			Code.fixup(nextCase.pop());
		}
		if(!skipCaseCond.isEmpty()) {
			Code.fixup(skipCaseCond.pop());
		}
		if(!breakStack.isEmpty()) {
	    	List<Integer> break_ = breakStack.pop();
		    while(!break_.isEmpty()) {
		    	Code.fixup(break_.removeLast());
		    }
	    }
		Code.put(Code.pop);
	}

}
