// generated with ast extension for cup
// version 0.8
// 16/1/2026 12:21:7


package rs.ac.bg.etf.pp1.ast;

public class Case implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    private CaseSym CaseSym;
    private CaseNumber CaseNumber;
    private ColonCase ColonCase;
    private StatementList StatementList;

    public Case (CaseSym CaseSym, CaseNumber CaseNumber, ColonCase ColonCase, StatementList StatementList) {
        this.CaseSym=CaseSym;
        if(CaseSym!=null) CaseSym.setParent(this);
        this.CaseNumber=CaseNumber;
        if(CaseNumber!=null) CaseNumber.setParent(this);
        this.ColonCase=ColonCase;
        if(ColonCase!=null) ColonCase.setParent(this);
        this.StatementList=StatementList;
        if(StatementList!=null) StatementList.setParent(this);
    }

    public CaseSym getCaseSym() {
        return CaseSym;
    }

    public void setCaseSym(CaseSym CaseSym) {
        this.CaseSym=CaseSym;
    }

    public CaseNumber getCaseNumber() {
        return CaseNumber;
    }

    public void setCaseNumber(CaseNumber CaseNumber) {
        this.CaseNumber=CaseNumber;
    }

    public ColonCase getColonCase() {
        return ColonCase;
    }

    public void setColonCase(ColonCase ColonCase) {
        this.ColonCase=ColonCase;
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
        if(CaseSym!=null) CaseSym.accept(visitor);
        if(CaseNumber!=null) CaseNumber.accept(visitor);
        if(ColonCase!=null) ColonCase.accept(visitor);
        if(StatementList!=null) StatementList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(CaseSym!=null) CaseSym.traverseTopDown(visitor);
        if(CaseNumber!=null) CaseNumber.traverseTopDown(visitor);
        if(ColonCase!=null) ColonCase.traverseTopDown(visitor);
        if(StatementList!=null) StatementList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(CaseSym!=null) CaseSym.traverseBottomUp(visitor);
        if(CaseNumber!=null) CaseNumber.traverseBottomUp(visitor);
        if(ColonCase!=null) ColonCase.traverseBottomUp(visitor);
        if(StatementList!=null) StatementList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Case(\n");

        if(CaseSym!=null)
            buffer.append(CaseSym.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(CaseNumber!=null)
            buffer.append(CaseNumber.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ColonCase!=null)
            buffer.append(ColonCase.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(StatementList!=null)
            buffer.append(StatementList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [Case]");
        return buffer.toString();
    }
}
