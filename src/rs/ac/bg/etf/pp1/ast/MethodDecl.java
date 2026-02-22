// generated with ast extension for cup
// version 0.8
// 16/1/2026 12:21:7


package rs.ac.bg.etf.pp1.ast;

public class MethodDecl implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    private MethodRet MethodRet;
    private OptFormPars OptFormPars;
    private VarDeclListMeth VarDeclListMeth;
    private StatementList StatementList;

    public MethodDecl (MethodRet MethodRet, OptFormPars OptFormPars, VarDeclListMeth VarDeclListMeth, StatementList StatementList) {
        this.MethodRet=MethodRet;
        if(MethodRet!=null) MethodRet.setParent(this);
        this.OptFormPars=OptFormPars;
        if(OptFormPars!=null) OptFormPars.setParent(this);
        this.VarDeclListMeth=VarDeclListMeth;
        if(VarDeclListMeth!=null) VarDeclListMeth.setParent(this);
        this.StatementList=StatementList;
        if(StatementList!=null) StatementList.setParent(this);
    }

    public MethodRet getMethodRet() {
        return MethodRet;
    }

    public void setMethodRet(MethodRet MethodRet) {
        this.MethodRet=MethodRet;
    }

    public OptFormPars getOptFormPars() {
        return OptFormPars;
    }

    public void setOptFormPars(OptFormPars OptFormPars) {
        this.OptFormPars=OptFormPars;
    }

    public VarDeclListMeth getVarDeclListMeth() {
        return VarDeclListMeth;
    }

    public void setVarDeclListMeth(VarDeclListMeth VarDeclListMeth) {
        this.VarDeclListMeth=VarDeclListMeth;
    }

    public StatementList getStatementList() {
        return StatementList;
    }

    public void setStatementList(StatementList StatementList) {
        this.StatementList=StatementList;
    }

    public SyntaxNode getParent() {
        return parent;
    }

    public void setParent(SyntaxNode parent) {
        this.parent=parent;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line=line;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(MethodRet!=null) MethodRet.accept(visitor);
        if(OptFormPars!=null) OptFormPars.accept(visitor);
        if(VarDeclListMeth!=null) VarDeclListMeth.accept(visitor);
        if(StatementList!=null) StatementList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(MethodRet!=null) MethodRet.traverseTopDown(visitor);
        if(OptFormPars!=null) OptFormPars.traverseTopDown(visitor);
        if(VarDeclListMeth!=null) VarDeclListMeth.traverseTopDown(visitor);
        if(StatementList!=null) StatementList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(MethodRet!=null) MethodRet.traverseBottomUp(visitor);
        if(OptFormPars!=null) OptFormPars.traverseBottomUp(visitor);
        if(VarDeclListMeth!=null) VarDeclListMeth.traverseBottomUp(visitor);
        if(StatementList!=null) StatementList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("MethodDecl(\n");

        if(MethodRet!=null)
            buffer.append(MethodRet.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(OptFormPars!=null)
            buffer.append(OptFormPars.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(VarDeclListMeth!=null)
            buffer.append(VarDeclListMeth.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(StatementList!=null)
            buffer.append(StatementList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [MethodDecl]");
        return buffer.toString();
    }
}
