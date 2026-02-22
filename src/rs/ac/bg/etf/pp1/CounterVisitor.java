package rs.ac.bg.etf.pp1;

import rs.ac.bg.etf.pp1.ast.*;

public class CounterVisitor extends VisitorAdaptor {

    public int enumCount = 0;
    public int methodCount = 0;

    public int globalVarCount = 0;
    public int globalConstCount = 0;
    public int globalArrayCount = 0;

    public int localVarMainCount = 0;
    public int statementMainCount = 0;
    public int functionCallMainCount = 0;

    private boolean inMain = false;


    @Override
    public void visit(EnumDecl node) {
        enumCount++;
    }


    @Override
    public void visit(MethodName node) {
        if ("main".equalsIgnoreCase(node.getI1())) {
            inMain = true;
        }
    }

    @Override
    public void visit(MethodDecl node) {
        methodCount++;
        inMain = false;
    }

 
    @Override
    public void visit(VarDeclElem_var node) {

        boolean isGlobal = node.getParent().getParent() instanceof DeclList;

        if (isGlobal) {
            globalVarCount++;
        }

        if (inMain) {
            localVarMainCount++;
        }
    }

    @Override
    public void visit(VarDeclElem_arr node) {

        boolean isGlobal = node.getParent().getParent() instanceof DeclList;

        if (isGlobal) {
            globalArrayCount++;
        }

        if (inMain) {
            localVarMainCount++;
        }
    }


    @Override
    public void visit(ConstDecl node) {
        globalConstCount++;
    }

    private void countStatement() {
        if (inMain) statementMainCount++;
    }

    @Override public void visit(Statement_designator n) { countStatement(); }
    @Override public void visit(Statement_if_else n) { countStatement(); }
    @Override public void visit(Statement_break n) { countStatement(); }
    @Override public void visit(Statement_cont n) { countStatement(); }
    @Override public void visit(Statement_ret_expr n) { countStatement(); }
    @Override public void visit(Statement_ret n) { countStatement(); }
    @Override public void visit(Statement_read n) { countStatement(); }
    @Override public void visit(Statement_print n) { countStatement(); }
    @Override public void visit(Statement_print_num n) { countStatement(); }
    @Override public void visit(Statement_switch n) { countStatement(); }
    @Override public void visit(Statement_for n) { countStatement(); }
    @Override public void visit(Statement_list n) { countStatement(); }

    /* ================= FUNCTION CALLS ================= */

    @Override
    public void visit(FactorMore_meth node) {
        if (inMain) functionCallMainCount++;
    }

    @Override
    public void visit(DesignatorStatement_act node) {
        if (inMain) functionCallMainCount++;
    }
}
