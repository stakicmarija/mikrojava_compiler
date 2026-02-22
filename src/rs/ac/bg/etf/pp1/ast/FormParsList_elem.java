// generated with ast extension for cup
// version 0.8
// 16/1/2026 12:21:7


package rs.ac.bg.etf.pp1.ast;

public class FormParsList_elem extends FormParsList {

    private FormParsElem FormParsElem;
    private FormParsList FormParsList;

    public FormParsList_elem (FormParsElem FormParsElem, FormParsList FormParsList) {
        this.FormParsElem=FormParsElem;
        if(FormParsElem!=null) FormParsElem.setParent(this);
        this.FormParsList=FormParsList;
        if(FormParsList!=null) FormParsList.setParent(this);
    }

    public FormParsElem getFormParsElem() {
        return FormParsElem;
    }

    public void setFormParsElem(FormParsElem FormParsElem) {
        this.FormParsElem=FormParsElem;
    }

    public FormParsList getFormParsList() {
        return FormParsList;
    }

    public void setFormParsList(FormParsList FormParsList) {
        this.FormParsList=FormParsList;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(FormParsElem!=null) FormParsElem.accept(visitor);
        if(FormParsList!=null) FormParsList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(FormParsElem!=null) FormParsElem.traverseTopDown(visitor);
        if(FormParsList!=null) FormParsList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(FormParsElem!=null) FormParsElem.traverseBottomUp(visitor);
        if(FormParsList!=null) FormParsList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("FormParsList_elem(\n");

        if(FormParsElem!=null)
            buffer.append(FormParsElem.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(FormParsList!=null)
            buffer.append(FormParsList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [FormParsList_elem]");
        return buffer.toString();
    }
}
