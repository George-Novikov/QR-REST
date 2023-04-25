package tools;

import java.util.List;

public class JSONBean {
    private int errorCode;
    private List<String> data;
    private String message;

    public JSONBean(){}
    public JSONBean(int errorCode, List<String> data, String message){
        this.errorCode = errorCode;
        this.data = data;
        this.message = message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}