package tools;

public class SubdocumentEntry {
    private String label;
    private int start;
    private int end;

    public SubdocumentEntry(){}
    public SubdocumentEntry(String label, int start, int end){
        this.label = label;
        this.start = start;
        this.end = end;
    }


    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

}
