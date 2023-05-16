package tools;

public class SubdocumentEntry {
    private String label;
    private int index;

    public SubdocumentEntry(){}
    public SubdocumentEntry(String label, int index){
        this.label = label;
        this.index = index;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
